import { myLearningMock } from '@/mockdata/myLearningMock.js'

const barColor = (average) => {
  if (average >= 80) return 'bg-teal-700'
  if (average >= 60) return 'bg-teal-500'
  return 'bg-orange-400'
}


export function MyLearningView() {
  const data = myLearningMock
  const strongest = [...data.topics].sort((a, b) => b.average - a.average)[0]
  const focusTopics = data.topics.filter((t) => t.average < 60)

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

      <section className="rounded-xl border border-slate-200 bg-white">
        <div className="border-b border-slate-200 p-6">
        <h2 className="text-lg font-semibold text-slate-900">Performance by Topic</h2>
        <p className="text-sm text-slate-500">Your current average in each topic</p>
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
          {data.topics.map((topic) => (
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
              {data.topics.map((topic) => (
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
          <p>Strongest: {strongest.name}</p>
          <p>Focus area: {focusTopics.map((t) => t.name).join(' and ')}</p>
        </div>
      </div>
    </section>
     
    </div>
  )
}