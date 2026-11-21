import { useState } from 'react'
import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { SubjectSelect } from '@/components/shared/SubjectSelect.jsx'
import { DataGate } from '@/components/shared/DataGate.jsx'
import { Icon } from '@/components/ui/Icon.jsx'
import { getPercent } from '@/utils/scoring.js'

// A line that explains how a real score becomes a percent, using the first score of this subject
function getExample(items, scores, students) {
  const item = items[0]
  if (!item) return ''
  const first = `Example: ${item.name} is out of ${item.maxScore}.`

  const record = scores.find((r) => r.assessment === item.name)
  const student = students.find((s) => s.id === record?.studentId)
  const percent = record ? getPercent(record, items) : null
  if (!student || percent === null) return first

  return `${first} ${student.firstName} ${student.lastName}'s score of ${record.score} / ${item.maxScore} in Students equals ${Math.round(percent)}%.`
}

// The table and the Save logic. It is only shown once the data is ready.
function MappingForm({ data }) {
  const [draft, setDraft] = useState(data.items) // a copy you edit. Nothing is saved until you press Save.
  const [message, setMessage] = useState(null) // { type: 'success' | 'error', text }
  const [saving, setSaving] = useState(false)

  // Change one field of one row (never edit state directly)
  function handleChange(id, field, value) {
    setDraft(draft.map((item) => (item.id === id ? { ...item, [field]: value } : item)))
    setMessage(null)
  }

  async function handleSubmit(e) {
    e.preventDefault() // stop the page from reloading
    setSaving(true)
    try {
      const result = await data.saveMapping(draft)
      if (result.failed.length > 0) {
        const list = result.failed.map((f) => `${f.name} (${f.message})`).join(', ')
        setMessage({ type: 'error', text: `Could not save: ${list}` })
      } else if (result.changed === 0) {
        setMessage({ type: 'success', text: 'Nothing to save' })
      } else {
        setMessage({ type: 'success', text: 'Mapping saved' })
      }
    } catch (err) {
      setMessage({ type: 'error', text: err.message })
    }
    setSaving(false)
  }

  if (draft.length === 0) {
    return <p className="mapping-note">This subject has no assessment items yet.</p>
  }

  return (
    <form id="mapping-form" onSubmit={handleSubmit}>
      <div className="mapping-cards">
        <div className="col-card">
          <div className="col-head">Assessment Item</div>
          {draft.map((item) => (
            <div className="col-cell" key={item.id}>{item.name}</div>
          ))}
        </div>

        <div className="col-card">
          <div className="col-head">Learning Outcome</div>
          {draft.map((item) => (
            <div className="col-cell left" key={item.id}>
              <input
                type="text"
                value={item.outcome}
                placeholder="Add a learning outcome"
                aria-label={`Learning outcome for ${item.name}`}
                onChange={(e) => handleChange(item.id, 'outcome', e.target.value)}
              />
            </div>
          ))}
        </div>

        <div className="col-card">
          <div className="col-head">Max Score</div>
          {draft.map((item) => (
            <div className="col-cell" key={item.id}>
              <input
                type="number"
                min="1"
                step="any"
                className="max"
                value={item.maxScore}
                aria-label={`Max score for ${item.name}`}
                onChange={(e) => handleChange(item.id, 'maxScore', e.target.value)}
              />
            </div>
          ))}
        </div>
      </div>

      <p className="mapping-note">{getExample(draft, data.scores, data.students)}</p>

      {message?.type === 'error' && <p className="form-error" role="alert">{message.text}</p>}
      {message?.type === 'success' && (
        <div className="toast">
          <Icon name="check" size={16} /> {message.text}
        </div>
      )}
      {saving && <p className="mapping-note">Saving…</p>}
    </form>
  )
}

export function SubjectMappingView({ data }) {
  return (
    <div>
      <PageHeader
        eyebrow="ASSESSMENT SETUP"
        title="Subject Mapping"
        description="Connect assessment items to topics and outcomes for more meaningful analytics."
        action={
          // This button is outside the form, so form="mapping-form" connects it to the form
          <button
            type="submit"
            form="mapping-form"
            className="btn primary"
            disabled={data.status !== 'ready' || data.items.length === 0}
          >
            <Icon name="check" size={15} /> Save mapping
          </button>
        }
      />

      <div className="subject-toolbar">
        <SubjectSelect data={data} />
        <span className="toolbar-note">These same assessment items and max scores are used in Students.</span>
      </div>

      <DataGate data={data}>
        <MappingForm data={data} />
      </DataGate>
    </div>
  )
}