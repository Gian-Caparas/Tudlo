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
    </div>
  )
}