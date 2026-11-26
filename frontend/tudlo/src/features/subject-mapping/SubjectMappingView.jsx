import { useEffect, useState } from 'react'
import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { SubjectSelect } from '@/components/shared/SubjectSelect.jsx'
import { DataGate } from '@/components/shared/DataGate.jsx'
import { Icon } from '@/components/ui/Icon.jsx'
import { Modal } from '@/components/ui/Modal.jsx'
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

function AddAssessmentModal({ data, onClose }) {
  const [name, setName] = useState('')
  const [outcome, setOutcome] = useState('')
  const [maxScore, setMaxScore] = useState('')
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  async function handleAdd(e) {
    e.preventDefault()
    const trimmedName = name.trim()
    const trimmedOutcome = outcome.trim()
    const numMax = Number(maxScore)

    if (!trimmedName) {
      setError('Please enter an assessment name (e.g. Quiz 1, Midterm Exam).')
      return
    }
    if (!trimmedOutcome) {
      setError('Please enter a learning outcome.')
      return
    }
    if (!numMax || numMax <= 0) {
      setError('Max score must be greater than 0.')
      return
    }

    if (data.items.some((i) => i.name.toLowerCase() === trimmedName.toLowerCase())) {
      setError(`An assessment named "${trimmedName}" already exists for this subject.`)
      return
    }

    setSaving(true)
    setError('')
    try {
      await data.addAssessment({ name: trimmedName, outcome: trimmedOutcome, maxScore: numMax })
      onClose()
    } catch (err) {
      setError(err.message || 'Failed to add assessment item.')
      setSaving(false)
    }
  }

  return (
    <Modal
      title="Add Assessment Item"
      onClose={onClose}
      footer={
        <div style={{ display: 'flex', gap: '8px', justifyContent: 'flex-end', width: '100%' }}>
          <button type="button" className="btn ghost" onClick={onClose} disabled={saving}>
            Cancel
          </button>
          <button type="submit" form="add-assessment-form" className="btn primary" disabled={saving}>
            {saving ? 'Adding…' : 'Add Item'}
          </button>
        </div>
      }
    >
      <form id="add-assessment-form" className="form-grid" onSubmit={handleAdd}>
        <label>
          Assessment Item Name
          <input
            type="text"
            required
            placeholder="e.g. Quiz 1, Midterm Exam, Final Project"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </label>
        <label>
          Learning Outcome
          <textarea
            required
            rows={3}
            placeholder="e.g. Understand relational schemas and SQL queries"
            value={outcome}
            onChange={(e) => setOutcome(e.target.value)}
          />
        </label>
        <label>
          Max Score
          <input
            type="number"
            min="1"
            step="any"
            required
            placeholder="e.g. 50"
            value={maxScore}
            onChange={(e) => setMaxScore(e.target.value)}
          />
        </label>
        {error && <p className="form-error" role="alert" style={{ color: '#b42318' }}>{error}</p>}
      </form>
    </Modal>
  )
}

// The table and the Save logic. It is only shown once the data is ready.
function MappingForm({ data, onOpenAdd }) {
  const [draft, setDraft] = useState(data.items)
  const [message, setMessage] = useState(null)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    setDraft(data.items)
  }, [data.items])

  function handleChange(id, field, value) {
    setDraft(draft.map((item) => (item.id === id ? { ...item, [field]: value } : item)))
    setMessage(null)
  }

  async function handleSubmit(e) {
    e.preventDefault()
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
    return (
      <div className="card" style={{ textAlign: 'center', padding: '32px 16px' }}>
        <p className="mapping-note" style={{ marginBottom: '16px' }}>This subject has no assessment items yet.</p>
        <button type="button" className="btn primary" onClick={onOpenAdd}>
          <Icon name="check" size={15} /> Add first assessment item
        </button>
      </div>
    )
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
              <textarea
                value={item.outcome}
                placeholder="Add a learning outcome"
                rows={2}
                wrap="soft"
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
  const [showAddModal, setShowAddModal] = useState(false)

  return (
    <div>
      <PageHeader
        eyebrow="ASSESSMENT SETUP"
        title="Subject Mapping"
        description="Connect assessment items to topics and outcomes for more meaningful analytics."
        action={
          <div style={{ display: 'flex', gap: '8px' }}>
            <button
              type="button"
              className="btn secondary"
              disabled={data.status !== 'ready'}
              onClick={() => setShowAddModal(true)}
            >
              + Add Assessment Item
            </button>
            <button
              type="submit"
              form="mapping-form"
              className="btn primary"
              disabled={data.status !== 'ready' || data.items.length === 0}
            >
              <Icon name="check" size={15} /> Save mapping
            </button>
          </div>
        }
      />

      <div className="subject-toolbar">
        <SubjectSelect data={data} />
        <span className="toolbar-note">These same assessment items and max scores are used in Students.</span>
      </div>

      <DataGate data={data}>
        <MappingForm data={data} onOpenAdd={() => setShowAddModal(true)} />
      </DataGate>

      {showAddModal && (
        <AddAssessmentModal data={data} onClose={() => setShowAddModal(false)} />
      )}
    </div>
  )
}
