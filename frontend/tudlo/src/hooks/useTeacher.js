import { useCallback, useEffect, useState } from 'react'
import { useAuth } from '@/hooks/useAuth.js'
import { teacherService } from '@/services/teacherService.js'

// Loads the data of the teacher pages (Dashboard, Import Class List, Subject Mapping, Students)
// from the backend. It is loaded once in App, so every page shows the same subject and the same data.
export function useTeacherData() {
  const { role, user, profileId } = useAuth()
  const isTeacher = role === 'teacher'
  const teacherId = profileId ?? user?.id ?? null

  const [subjects, setSubjects] = useState([])
  const [selectedId, setSelectedId] = useState(null)
  const [students, setStudents] = useState([])
  const [items, setItems] = useState([])
  const [scores, setScores] = useState([])
  const [status, setStatus] = useState('loading') // 'loading' | 'ready' | 'error'
  const [error, setError] = useState('')
  const [reloadCount, setReloadCount] = useState(0)

  const selected = subjects.find((s) => s.id === selectedId) ?? null
  const subjectName = selected?.subjectName
  const classSection = selected?.classSection

  // Step 1: the subjects (and sections) this teacher teaches
  useEffect(() => {
    if (!isTeacher || !teacherId) return undefined
    let active = true

    teacherService
      .getSubjects(teacherId)
      .then((list) => {
        if (!active) return
        setSubjects(
          list.map((s) => ({ ...s, label: `${s.subjectName} – ${s.classSection}` }))
        )
        setSelectedId((current) => current ?? list[0]?.id ?? null)
        if (list.length === 0) setStatus('ready')
      })
      .catch((e) => {
        if (!active) return
        setError(e.message)
        setStatus('error')
      })

    return () => {
      active = false
    }
  }, [isTeacher, teacherId, reloadCount])

  // Step 2: the class list, assessment items and scores of the chosen subject
  useEffect(() => {
    if (!subjectName) return undefined
    let active = true
    const subject = { subjectName, classSection }

    Promise.all([
      teacherService.getClassList(subject),
      teacherService.getAssessments(subject),
      teacherService.getActivities(subject),
    ])
      .then(([classList, assessments, activities]) => {
        if (!active) return
        setStudents(
          classList.map((c) => ({ id: c.studentId, firstName: c.firstName, lastName: c.lastName }))
        )
        setItems(
          assessments.map((a) => ({
            id: a.id,
            name: a.assessmentItem,
            outcome: a.learningOutcome,
            maxScore: a.maxScore,
          }))
        )
        setScores(
          activities.map((a) => ({
            id: a.id,
            studentId: a.studentId,
            assessment: a.activityName,
            score: a.score,
          }))
        )
        setError('')
        setStatus('ready')
      })
      .catch((e) => {
        if (!active) return
        setError(e.message)
        setStatus('error')
      })

    return () => {
      active = false
    }
  }, [subjectName, classSection, reloadCount])

  // Choose another subject. Every page follows it.
  const selectSubject = useCallback((id) => {
    setStatus('loading')
    setSelectedId(id)
  }, [])

  // Try again after an error
  const retry = useCallback(() => {
    setStatus('loading')
    setReloadCount((n) => n + 1)
  }, [])

  // Load the latest data again without showing the loading screen
  const refresh = useCallback(() => setReloadCount((n) => n + 1), [])

  // Imports a class list file. The class (subject + section) comes from the file name.
  // If the class does not exist yet, it is created. Students already in the class are skipped.
  async function importClassList({ subjectName: name, classSection: section, students: rows }) {
    const same = (a, b) => a.trim().toLowerCase() === b.trim().toLowerCase()

    // 1. Find the class, or create it
    const mine = await teacherService.getSubjects(teacherId)
    let subject = mine.find((s) => same(s.subjectName, name) && same(s.classSection, section))
    const created = !subject
    if (created) {
      try {
        subject = await teacherService.createSubject({ subjectName: name, classSection: section, teacherId })
      } catch (e) {
        if (e.status === 0 || e.status === 401 || e.status === 403) throw e
        throw new Error(`Could not create the class ${name} – ${section}.`, { cause: e })
      }
    }

    // 2. Students who are already in this class are skipped
    const known = new Set((await teacherService.getClassList(subject)).map((c) => c.studentId))
    const fresh = []
    rows.forEach((row) => {
      if (known.has(row.id)) return
      known.add(row.id) // also skips repeated rows in the same file
      fresh.push(row)
    })
    const skipped = rows.length - fresh.length

    // 3. Add the new students
    let failed = []
    if (fresh.length > 0) {
      try {
        await teacherService.bulkImportClassList(subject, fresh)
      } catch (e) {
        if (e.status === 0 || e.status === 401) throw e
        // The bulk import is all or nothing, so add the students one by one to find the ones that fail
        const results = await Promise.allSettled(
          fresh.map((student) => teacherService.addClassListStudent(subject, student))
        )
        failed = fresh.filter((_, i) => results[i].status === 'rejected').map((s) => s.id)
      }
    }

    // 4. Show this class everywhere. Its data is only seen when this class is selected.
    const latest = await teacherService.getSubjects(teacherId)
    setSubjects(latest.map((s) => ({ ...s, label: `${s.subjectName} – ${s.classSection}` })))
    setSelectedId(subject.id)
    setReloadCount((n) => n + 1)

    return {
      label: `${subject.subjectName} – ${subject.classSection}`,
      created,
      imported: fresh.length - failed.length,
      skipped,
      failed,
    }
  }

  // Saves the changed learning outcomes and max scores of the chosen subject
  async function saveMapping(draft) {
    const subject = { subjectName, classSection }
    const changed = draft.filter((d) => {
      const old = items.find((i) => i.id === d.id)
      return old && (old.outcome !== d.outcome || Number(old.maxScore) !== Number(d.maxScore))
    })

    const failed = []
    const toSave = []
    changed.forEach((item) => {
      if (item.outcome.trim() === '') failed.push({ name: item.name, message: 'Learning outcome is required' })
      else if (!(Number(item.maxScore) > 0)) failed.push({ name: item.name, message: 'Max score must be more than 0' })
      else toSave.push(item)
    })

    const results = await Promise.allSettled(toSave.map((item) => teacherService.updateAssessment(item, subject)))
    let rejected = 0
    results.forEach((result, i) => {
      if (result.status === 'rejected') {
        rejected += 1
        failed.push({ name: toSave[i].name, message: result.reason.message })
      }
    })

    refresh()
    return { changed: changed.length, saved: toSave.length - rejected, failed }
  }

  // Adds a new activity (an assessment item) to the chosen subject.
  // Its learning outcome and max score can be set in Subject Mapping.
  async function addActivity(name) {
    const subject = { subjectName, classSection }
    await teacherService.createAssessment(
      { name: name.trim(), outcome: 'Not set yet', maxScore: 100 },
      subject
    )
    refresh()
  }

  // Saves one score typed into the Students grid.
  // A score of null removes the score.
  async function saveScore({ studentId, assessment, score }) {
    const existing = scores.find((r) => r.studentId === studentId && r.assessment === assessment)

    if (score === null) {
      if (existing) await teacherService.deleteActivity(existing.id)
    } else {
      const body = { activityName: assessment, studentId, score, subjectName, classSection }
      if (existing) await teacherService.updateActivity(existing.id, body)
      else await teacherService.createActivity(body)
    }
    refresh()
  }

  return {
    status,
    error,
    subjects,
    selectedId,
    selected,
    classLabel: selected?.label ?? '',
    students,
    items,
    scores,
    selectSubject,
    retry,
    importClassList,
    saveMapping,
    addActivity,
    saveScore,
  }
}