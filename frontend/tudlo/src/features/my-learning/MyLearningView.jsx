import { myLearningMock } from '@/mockdata/myLearningMock.js'

export function MyLearningView() {
  const data = myLearningMock

  return (
    <div className="space-y-6">
      <div>
        <p className="text-xs font-medium uppercase tracking-widest text-teal-700">
          My learning
        </p>
        <h1 className="mt-2 text-3xl font-semibold text-slate-900">
          Your progress, {data.studentName}
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
            {data.overallAverage}%
          </p>
          <p className="mt-3 text-xs text-slate-500">
            Across {data.topicCount} topics
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


    </div>
  )
}