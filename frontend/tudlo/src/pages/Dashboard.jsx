import { stats, performance, needsSupport } from '../mockdata/mockData.js'

function Dashboard({ setPage }) {
  return (
    <div>
      <p className="text-sm">CSIT321 - G6</p>
      <h2 className="text-2xl font-bold">Good morning, Prof. Ramon</h2>
      <p className="mb-4">Here's where your class stands today, and who may need your attention.</p>

      {/* Stat cards */}
      <div className="mb-6 grid max-w-3xl grid-cols-2 gap-3 sm:grid-cols-4">
        <div className="border p-3">
          <p className="text-sm">Class average</p>
          <p className="text-2xl font-bold">{stats.classAverage}</p>
        </div>
        <div className="border p-3">
          <p className="text-sm">Pass rate</p>
          <p className="text-2xl font-bold">{stats.passRate}</p>
          <p className="text-xs">{stats.passDetail}</p>
        </div>
        <div className="border p-3">
          <p className="text-sm">Submission completion</p>
          <p className="text-2xl font-bold">{stats.completion}</p>
          <p className="text-xs">{stats.completionDetail}</p>
        </div>
        <div className="border p-3">
          <p className="text-sm">Students needing support</p>
          <p className="text-2xl font-bold">{stats.needSupport}</p>
        </div>
      </div>

      {/* Class performance */}
      <h3 className="font-bold">Class performance</h3>
      <p className="mb-2 text-sm">Average score by assessment</p>
      <div className="mb-6 max-w-xl">
        {performance.map((p) => (
          <div key={p.name} className="mb-2">
            <p className="text-sm">{p.name}: {p.percent}%</p>
            <progress value={p.percent} max="100" className="w-full" />
          </div>
        ))}
      </div>

      {/* Needs support (click a row to go to Students) */}
      <h3 className="font-bold">Needs support ({needsSupport.length})</h3>
      <ul className="mb-6 max-w-xl">
        {needsSupport.map((s) => (
          <li key={s.name} className="border-b py-2">
            <button onClick={() => setPage('students')} className="block w-full text-left">
              <span className="font-bold">{s.name}</span> - {s.detail} <span className="text-sm">({s.tag})</span>
            </button>
          </li>
        ))}
      </ul>

      {/* Insight */}
      <div className="max-w-3xl border p-3">
        <p className="font-bold">Insight worth noticing</p>
        <p>
          Programming Basics is the lowest-performing topic across the class at 58%. Consider revisiting variables
          and control flow before the next assessment.{' '}
          <button onClick={() => setPage('gaps')} className="underline">View learning gaps</button>
        </p>
      </div>
    </div>
  )
}

export default Dashboard