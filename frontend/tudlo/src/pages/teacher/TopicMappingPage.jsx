import { useState } from 'react'
import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { Icon } from '@/components/ui/Icon.jsx'
import { topics, outcomes, startingItems } from '@/mockdata/mockData.js'

// One table row. It gets the item and a function to call when something changes.
function MappingRow({ item, index, onChange }) {
  return (
    <tr>
      <td>
        <strong>{item.name}</strong>
        <small>{item.hint}</small>
      </td>
      <td>
        {/* controlled: value + onChange, just like the input in the Forms lesson */}
        <select value={item.topic} onChange={(e) => onChange(index, 'topic', e.target.value)}>
          <option value="">Select topic</option>
          {topics.map((t) => (
            <option key={t} value={t}>{t}</option>
          ))}
        </select>
      </td>
      <td>
        <select value={item.outcome} onChange={(e) => onChange(index, 'outcome', e.target.value)}>
          <option value="">Select outcome</option>
          {outcomes.map((o) => (
            <option key={o} value={o}>{o}</option>
          ))}
        </select>
      </td>
      <td>
        <input
          type="number"
          className="score-input"
          value={item.maxScore}
          onChange={(e) => onChange(index, 'maxScore', e.target.value)}
        />
      </td>
    </tr>
  )
}

function TopicMappingPage() {
  const [items, setItems] = useState(startingItems)
  const [saved, setSaved] = useState(false)

  // Change one field of one row: map returns a changed copy for that row
  // and the original object for the rest (never edit state directly).
  function handleChange(index, field, value) {
    setItems(
      items.map((item, i) => (i === index ? { ...item, [field]: value } : item))
    )
    setSaved(false)
  }

  // The Save button submits the form, so stop the page from reloading.
  function handleSubmit(e) {
    e.preventDefault()
    setSaved(true)
  }

  const mappedCount = items.filter((item) => item.topic && item.outcome).length
  const percent = Math.round((mappedCount / items.length) * 100)

  return (
    <form onSubmit={handleSubmit}>
      <PageHeader
        eyebrow="ASSESSMENT SETUP"
        title="Topic Mapping"
        description="Connect assessment items to topics and outcomes for more meaningful analytics."
        action={
          <button type="submit" className="btn primary">
            <Icon name="check" size={15} /> Save mapping
          </button>
        }
      />

      <section className="progress-card">
        <div>
          <span>Mapping progress</span>
          <strong>{percent}% categorized</strong>
        </div>
        <div className="progress"><i style={{ width: `${percent}%` }} /></div>
        <small>{mappedCount} of {items.length} assessment items mapped</small>
      </section>

      <section className="card">
        <div className="table-wrap">
          <table className="mapping-table">
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
                <MappingRow key={item.name} item={item} index={index} onChange={handleChange} />
              ))}
            </tbody>
          </table>
        </div>
      </section>

      {saved && (
        <div className="toast">
          <Icon name="check" size={16} /> Mapping saved
        </div>
      )}
    </form>
  )
}

export default TopicMappingPage