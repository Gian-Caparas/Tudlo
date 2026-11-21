// The rules the teacher pages use
export const rules = {
  passMark: 75,          // a student passes with an average of 75% or more
  supportThreshold: 60,  // below 60% overall grade is red in Students and flags the student for support
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

// Computes a student's overall score, total max score, and overall percentage for a subject.
export function getOverallStudentScore(studentId, scores, items) {
  const studentScores = scores.filter((r) => r.studentId === studentId)
  if (!studentScores.length) return null

  let earned = 0
  let max = 0
  let completedCount = 0

  studentScores.forEach((record) => {
    const item = items.find((i) => i.name === record.assessment)
    const itemMax = Number(item?.maxScore) || 0
    if (record.score !== null && record.score !== undefined && itemMax > 0) {
      earned += Number(record.score)
      max += itemMax
      completedCount += 1
    }
  })

  if (!max) return null
  const percent = (earned / max) * 100
  return { earned, max, percent, completedCount }
}

// Students who need support based on their overall grade in the subject falling below 60%
export function getNeedsSupport(scores, students, items) {
  const list = []

  students.forEach((student) => {
    const overall = getOverallStudentScore(student.id, scores, items)
    if (overall && overall.percent < rules.supportThreshold) {
      list.push({
        id: student.id,
        name: `${student.firstName} ${student.lastName}`,
        overallPercent: Math.round(overall.percent * 10) / 10,
        earned: overall.earned,
        max: overall.max,
        completedCount: overall.completedCount,
        detail: `Overall: ${Math.round(overall.percent)}% (${overall.earned}/${overall.max} pts)`,
        tag: 'Below 60%',
      })
    }
  })

  return list
}