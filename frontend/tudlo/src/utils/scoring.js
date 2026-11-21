// The rules the teacher pages use
export const rules = {
  passMark: 75,          // a student passes with an average of 75% or more
  supportThreshold: 60,  // below 60% is red in Students and flags the student on the Dashboard
  goodThreshold: 80,     // 80% and above is green in Students
}

export function average(numbers) {
  return numbers.length ? numbers.reduce((sum, n) => sum + n, 0) / numbers.length : 0
}

// A score as a percent of the max score of its assessment item (from Subject Mapping).
// null when there is no score or no max score.
export function getPercent(record, items) {
  const item = items.find((i) => i.name === record.assessment)
  const max = Number(item?.maxScore) || 0
  if (record.score === null || record.score === undefined || !max) return null
  return (record.score / max) * 100
}

// Students who have a score below the support threshold
export function getNeedsSupport(scores, students, items) {
  const list = []

  students.forEach((student) => {
    const lowest = scores
      .filter((r) => r.studentId === student.id)
      .map((record) => ({ record, percent: getPercent(record, items) }))
      .filter((x) => x.percent !== null && x.percent < rules.supportThreshold)
      .sort((a, b) => a.percent - b.percent)[0]

    if (lowest) {
      list.push({
        id: student.id,
        name: `${student.firstName} ${student.lastName}`,
        detail: `${lowest.record.assessment} · ${Math.round(lowest.percent)}%`,
        tag: 'Below Threshold',
      })
    }
  })

  return list
}