import { useState } from 'react'
import { topics, outcomes, startingItems } from '../mockdata/mockData.js'

function TopicMapping() {
  const [items, setItems] = useState(startingItems)
  const [saved, setSaved] = useState(false)

  // change one field (topic, outcome or maxScore) of one row
  function change(index, field, value) {
    const newItems = items.map((item, i) => {
      if (i === index) return { ...item, [field]: value }
      return item
    })
    setItems(newItems)
    setSaved(false)
  }

  const mappedCount = items.filter((item) => item.topic && item.outcome).length
  const percent = Math.round((mappedCount / items.length) * 100)

  return (
    <div>
      <div className="mb-4 flex items-start justify-between">
        <div>
          <p className="text-sm">ASSESSMENT SETUP</p>
          <h2 className="text-2xl font-bold">Topic Mapping</h2>
          <p>Connect assessment items to topics and outcomes for more meaningful analytics.</p>
        </div>
        <button onClick={() => setSaved(true)} className="border px-3 py-1">
          Save mapping
        </button>
      </div>

      {saved && <p className="mb-2 font-bold">Mapping saved!</p>}

      <div className="mb-4 max-w-4xl border p-3">
        <p className="text-sm">Mapping progress</p>
        <p className="font-bold">{percent}% categorized</p>
        <progress value={mappedCount} max={items.length} className="w-full" />
        <p className="text-xs">{mappedCount} of {items.length} assessment items mapped</p>
      </div>

      <table className="w-full max-w-4xl text-left">
        <thead>
          <tr>
            <th>Assessment item</th>
            <th>Topic</th>
            <th>Learning outcome</th>
            <th>Max score</th>
          </tr>
        </thead>
        <tbody>
          {items.map((item, index) => (
            <tr key={item.name} className="border-t">
              <td className="py-2">
                <p className="font-bold">{item.name}</p>
                <p className="text-xs">{item.hint}</p>
              </td>
              <td>
                <select value={item.topic} onChange={(e) => change(index, 'topic', e.target.value)}>
                  <option value="">Select topic</option>
                  {topics.map((t) => (
                    <option key={t} value={t}>{t}</option>
                  ))}
                </select>
              </td>
              <td>
                <select value={item.outcome} onChange={(e) => change(index, 'outcome', e.target.value)}>
                  <option value="">Select outcome</option>
                  {outcomes.map((o) => (
                    <option key={o} value={o}>{o}</option>
                  ))}
                </select>
              </td>
              <td>
                <input
                  type="number"
                  value={item.maxScore}
                  onChange={(e) => change(index, 'maxScore', e.target.value)}
                  className="w-20 border"
                />
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default TopicMapping