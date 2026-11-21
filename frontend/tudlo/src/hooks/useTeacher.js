import { useCallback, useEffect, useState } from 'react'
import { useAuth } from '@/hooks/useAuth.js'
import { teacherService } from '@/services/teacherService.js'

// Loads the data of the teacher pages (Dashboard, Import Class List, Subject Mapping, Students, Support)
// from the backend. It is loaded once in App, so every page shows the same subject and the same data.
export function useTeacherData(){
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

  // Excel Import (xlsx/xls) - backend handles everything
  async function importClassListExcel(file) {
    const outcome = await teacherService.importClassListExcel(file)
    // Refresh subjects list and select the newly created one
    const latest = await teacherService.getSubjects(teacherId)
    setSubjects(latest.map((s) => ({ ...s, label: `${s.subjectName} – ${s.classSection}` })))
    if (outcome.offerings?.length > 0) {
      setSelectedId(outcome.offerings[0].offeringId)
    }
    setReloadCount((n) => n + 1)

    return {
      ...outcome,
      label: (outcome.offerings ?? []).map(o => `${o.subjectName} – ${o.classSection}`).join(', '),
      created: (outcome.createdAccountsCount ?? 0) > 0,
      imported: outcome.importedCount ?? 0,
      skipped: outcome.skippedDuplicatesCount ?? 0,
      failed: [],
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

  // Adds a new assessment item for the chosen subject and section
  async function addAssessment({ name, outcome, maxScore }) {
    const subject = { subjectName, classSection }
    await teacherService.createAssessment({ name, outcome, maxScore }, subject)
    refresh()
  }

  // Deletes an assessment item
  async function deleteAssessment(id) {
    await teacherService.deleteAssessment(id)
    refresh()
  }

  // Saves a score. If the student already has a score for that assessment item, it is replaced.
  async function addActivity({ studentId, assessment, score }) {
    const existing = scores.find((r) => r.studentId === studentId && r.assessment === assessment)
    const body = { activityName: assessment, studentId, score: Number(score), subjectName, classSection }
    if (existing) await teacherService.updateActivity(existing.id, body)
    else await teacherService.createActivity(body)
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
    importClassList: importClassListExcel,
    importClassListExcel,
    saveMapping,
    addAssessment,
    deleteAssessment,
    addActivity,
    saveScore,
  }
}