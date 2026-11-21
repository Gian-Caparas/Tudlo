import { myLearningMock } from '@/mockdata/myLearningMock.js'
import { useEffect, useState } from 'react'
import { useAuth } from '@/hooks/useAuth.js'
import { myProgressService } from '@/services/myProgressService.js'
import { activityService } from '@/services/activityService.js'
import { assessmentService } from '@/services/assessmentService.js'

const barColor = (average) => {
  if (average >= 80) return 'bg-teal-700'
  if (average >= 60) return 'bg-teal-500'
  return 'bg-orange-400'
}

const scoreColor = (percent, best) => {
  if (percent < 60) return 'text-red-600'
  if (percent === best) return 'text-teal-700'
  return 'text-slate-900'
}


export function MyLearningView() {
  const data = myLearningMock
  const { user } = useAuth()
  const [rows, setRows] = useState(null)
  const [error, setError] = useState('')
  const [scoreCard, setScoreCard] = useState(null)


  useEffect(() => {
    if (!user?.userIdNumber) return
  myProgressService.getByStudent(user.userIdNumber)
    .then(setRows)
    .catch((err) => setError(err.message))
  }, [user?.userIdNumber])

  useEffect(() => {
    if (!rows?.length || !user?.userIdNumber) return

    activityService.getByStudent(user.userIdNumber)
      .then(async (activities) => {
        const hasScores = new Set(activities.map((a) => a.subjectName))
        const target = [...rows]
          .sort((a, b) => a.subjectAverage - b.subjectAverage)
          .find((row) => hasScores.has(row.subjectName))

        if (!target) {
          setScoreCard({ subject: '', scores: [] })
          return
        }

        const assessments = await assessmentService.getBySubject(target.subjectName, target.classSection)
        const maxByItem = Object.fromEntries(assessments.map((a) => [a.assessmentItem, a.maxScore]))

        setScoreCard({
          subject: target.subjectName,
          scores: activities
            .filter((a) => a.subjectName === target.subjectName && maxByItem[a.activityName])
            .map((a) => ({ label: a.activityName, score: a.score, maxScore: maxByItem[a.activityName] })),
        })
      })
      .catch(() => setScoreCard({ subject: '', scores: [] }))
  }, [rows, user?.userIdNumber])

  if (error) return <p className="text-sm text-red-600">{error}</p>
  if (!rows) return <p className="text-sm text-slate-500">Loading your progress…</p>

  const topics = rows.map((row) => ({
    name: row.subjectName,
    average: Math.round(row.subjectAverage),
  }))
  const strongest = [...topics].sort((a, b) => b.average - a.average)[0]
  const focusTopics = topics.filter((t) => t.average < 60)
  const scores = scoreCard?.scores ?? []
  const bestPercent = scores.length
  ? Math.max(...scores.map((s) => (s.score / s.maxScore) * 100))
  : 0
  const reviewList = rows
  .filter((row) => row.subjectAverage < 80)
  .sort((a, b) => a.subjectAverage - b.subjectAverage)
  .map((row) => ({
    name: row.subjectName,
    average: Math.round(row.subjectAverage),
    completed: row.completedAssessments,
    total: row.totalAssessments,
    priority: row.subjectAverage < 60 ? 'High priority' : 'Medium priority',
  }))
  const overallAverage = rows.length
  ? Math.round(rows.reduce((sum, row) => sum + row.subjectAverage, 0) / rows.length)
  : 0

  return (
    <div className="space-y-6">
      <div>
        <p className="text-xs font-medium uppercase tracking-widest text-teal-700">
          My learning
        </p>
        <h1 className="mt-2 text-3xl font-semibold text-slate-900">
          Your progress, {user?.firstName ?? user?.fullName?.split(' ')[0] ?? 'there'}
        </h1>
        <p className="mt-2 text-sm text-slate-500">
          See what's going well and where a little more practice can help.
        </p>
      </div>

      <div className="grid overflow-hidden rounded-xl border border-slate-200 bg-white md:grid-cols-[300px_1fr]">
        <div className="border-b border-slate-200 p-8 md:border-b-0 md:border-r">
          <p className="text-[10px] font-medium uppercase tracking-widest text-slate-500">
            Overall average
          </p>
          <p className="mt-6 text-5xl font-semibold text-teal-700">
            {overallAverage}%
          </p>
          <p className="mt-3 text-xs text-slate-500">
            Across {topics.length} subjects
          </p>
        </div>

        <div className="p-8">
          <p className="text-[10px] font-medium uppercase tracking-widest text-slate-500">
            Your latest win
          </p>
          <p className="mt-5 text-xl text-slate-900">
            Your {data.latestWin.assessment} in {data.latestWin.subject} was your best result yet.
          </p>
          <p className="mt-3 text-xs text-slate-500">
            You scored {data.latestWin.score}/{data.latestWin.maxScore}, {data.latestWin.note}
          </p>
        </div>
      </div>

      <section className="rounded-xl border border-slate-200 bg-white">
        <div className="border-b border-slate-200 p-6">
        <h2 className="text-lg font-semibold text-slate-900">Performance by Subject</h2>
        <p className="text-sm text-slate-500">Your current average in each subject</p>
        </div>

        <div className="p-6">
          <div className="flex">
           <div className="flex h-56 flex-col justify-between pr-3 pt-6 text-xs text-slate-500">
            <span>100%</span>
            <span>75%</span>
            <span>50%</span>
            <span>25%</span>
            <span>0%</span>
          </div>

      <div className="flex-1">
        <div className="flex h-56 items-end gap-6 border-b border-slate-200 pt-6">
          {topics.map((topic) => (
            <div
              key={topic.name}
              className="flex h-full flex-1 flex-col items-center justify-end"
            >
              <span className="mb-1 text-sm font-semibold text-slate-900">
                {topic.average}%
              </span>
              <div
                className={`w-12 rounded-t ${barColor(topic.average)}`}
                style={{ height: `${topic.average}%` }}
              />
            </div>
              ))}
            </div>

            <div className="mt-2 flex gap-6">
              {topics.map((topic) => (
                <p key={topic.name} className="flex-1 text-center text-xs text-slate-500">
                  {topic.name}
                </p>
              ))}
            </div>
          </div>
        </div>
      </div>

      <div className="flex flex-wrap items-center gap-6 border-t border-slate-200 px-6 py-4 text-xs text-slate-500">
        <span className="flex items-center gap-2">
          <span className="h-2.5 w-2.5 rounded-sm bg-teal-700" /> Strong 80% and up
        </span>
        <span className="flex items-center gap-2">
          <span className="h-2.5 w-2.5 rounded-sm bg-teal-500" /> On track 60-79%
        </span>
          <span className="flex items-center gap-2">
        <span className="h-2.5 w-2.5 rounded-sm bg-orange-400" /> Needs focus under 60%
        </span>
          <div className="ml-auto">
          <p>Strongest: {strongest?.name ?? 'None'}</p>
          <p>Focus area: {focusTopics.map((t) => t.name).join(' and ') || 'None'}</p>
        </div>
      </div>
    </section>

    <section className="rounded-xl border border-slate-200 bg-white p-6">
  <h2 className="text-lg font-semibold text-slate-900">Subjects to review</h2>

  <div className="mt-4 divide-y divide-slate-200">
    {reviewList.map((topic, index) => {
      const isHigh = topic.priority === 'High priority'

      return (
        <div
          key={topic.name}
          className="flex items-center justify-between gap-4 py-4 first:pt-0 last:pb-0"
        >
          <div>
            <p className="font-medium text-slate-900">
              {String(index + 1).padStart(2, '0')} {topic.name}
            </p>
            <p className="mt-2 text-sm text-slate-500">
            {topic.completed}/{topic.total} assessments completed
            </p>
          </div>

          <div className="flex flex-col items-end gap-2">
            <span
              className={`text-xl font-semibold ${
                isHigh ? 'text-red-600' : 'text-slate-900'
              }`}
            >
              {topic.average}%
            </span>
            <span
              className={`rounded-full px-4 py-1 text-xs ${
                isHigh
                  ? 'bg-red-50 text-red-600'
                  : 'bg-amber-50 text-amber-700'
              }`}
            >
              {topic.priority}
            </span>
          </div>
        </div>
      )
       })}
       </div>

       {reviewList.length === 0 && (
          <p className="mt-4 text-sm text-slate-500">Nothing to review. Great work!</p>
        )}
    </section>

    <section className="rounded-xl border border-slate-200 bg-white p-6">
  <h2 className="text-lg font-semibold text-slate-900">
    {scoreCard?.subject ? `Scores behind ${scoreCard.subject}` : 'Your scores'}
  </h2>

  <div className="mt-4 grid gap-3 sm:grid-cols-3">
    {scores.map((item) => {
      const percent = (item.score / item.maxScore) * 100
      const color = scoreColor(percent, bestPercent)

      return (
        <div key={item.label} className="rounded-lg bg-slate-50 px-4 py-3 text-center">
          <p className={`text-xs ${color}`}>{item.label}</p>
          <p className={`text-sm font-semibold ${color}`}>
            {item.score}/{item.maxScore}
          </p>
        </div>
      )
    })}
  </div>

  {scores.length === 0 && (
    <p className="mt-4 text-sm text-slate-500">No scores recorded yet.</p>
  )}

</section>

  <section className="flex items-center justify-between gap-4 rounded-xl border border-slate-200 bg-white p-6">
  <div className="flex items-center gap-4">
    <svg
      className="h-5 w-5 text-teal-700"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.8"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
    >
      <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
    </svg>

    <div>
      <p className="font-medium text-teal-700">Support from your teacher</p>
      <p className="mt-1 text-sm text-slate-500">{data.support.description}</p>
    </div>
  </div>

  <p className="text-2xl font-semibold text-teal-700">
    {data.support.scoreBefore}% → {data.support.scoreAfter}%
  </p>
</section>
     
    </div>
  )
}