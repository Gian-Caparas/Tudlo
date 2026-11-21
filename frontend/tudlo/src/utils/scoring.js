import { rules } from '@/mockdata/mockData.js'

export function average(numbers) {
  return numbers.length ? numbers.reduce((sum, n) => sum + n, 0) / numbers.length : 0
}

// The max score of an assessment in a subject. It comes from Subject Mapping.
export function getMaxScore(mapping, subject, assessment) {
  const item = mapping[subject]?.find((i) => i.name === assessment)
  return Number(item?.maxScore) || 0
}

// A score as a percent. null when the score is missing or the max score is not set.
export function getPercent(record, mapping) {
  const max = getMaxScore(mapping, record.subject, record.assessment)
  if (record.score === null || record.score === undefined || !max) return null
  return (record.score / max) * 100
}

// Students with a score under the support threshold, or with a missing score
export function getNeedsSupport(scores, students, mapping) {
  const list = []

  students.forEach((student) => {
    const mine = scores.filter((r) => r.studentId === student.id)
    const lowest = mine
      .map((record) => ({ record, percent: getPercent(record, mapping) }))
      .filter((x) => x.percent !== null && x.percent < rules.supportThreshold)
      .sort((a, b) => a.percent - b.percent)[0]
    const missing = mine.find((r) => r.score === null)
    const name = `${student.firstName} ${student.lastName}`

    if (lowest) {
      list.push({
        id: student.id,
        name,
        detail: `${lowest.record.subject} · ${Math.round(lowest.percent)}%`,
        tag: 'Below Threshold',
      })
    } else if (missing) {
      list.push({ id: student.id, name, detail: `${missing.subject} · ${missing.assessment}`, tag: 'Overdue' })
    }
  })

  return list
}