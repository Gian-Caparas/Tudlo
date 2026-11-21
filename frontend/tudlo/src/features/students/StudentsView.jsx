import { useEffect, useState } from 'react'
import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { SubjectSelect } from '@/components/shared/SubjectSelect.jsx'
import { DataGate } from '@/components/shared/DataGate.jsx'
import { Icon } from '@/components/ui/Icon.jsx'
import { Modal } from '@/components/ui/Modal.jsx'
import { rules } from '@/utils/scoring.js'

// One score in the grid. Type in it to change the score. Red is below the support threshold, green is a high score.
function ScoreCell({ record, maxScore, onSave, onFail }) {
  const saved = record ? String(record.score) : ''
  const [value, setValue] = useState(saved)

  const percent = record && maxScore ? (record.score / maxScore) * 100 : null
  let tone = ''
  if (percent !== null && percent < rules.supportThreshold) tone = 'low'
  else if (percent !== null && percent >= rules.goodThreshold) tone = 'high'

  // Runs when you leave the cell or press Enter
  async function commit() {
    const text = value.trim()
    if (text === saved) return

    const score = text === '' ? null : Number(text)
    if (score !== null && (Number.isNaN(score) || score < 0)) {
      onFail('A score must be a number of 0 or more.')
      setValue(saved)
      return
    }
    if (score !== null && maxScore && score > maxScore) {
      onFail(`A score cannot be more than ${maxScore} for this activity.`)
      setValue(saved)
      return
    }

    try {
      await onSave(score)
    } catch (err) {
      onFail(err.message)
      setValue(saved)
    }
  }

  function handleKeyDown(e) {
    if (e.key === 'Enter') e.currentTarget.blur() // blur runs commit
    if (e.key === 'Escape') {
      setValue(saved)
      e.currentTarget.blur()
    }
  }

  return (
    <div className={`col-cell score ${tone}`}>
      <input
        className="score-edit"
        type="number"
        step="any"
        min="0"
        value={value}
        placeholder="—"
        aria-label="Score"
        onChange={(e) => setValue(e.target.value)}
        onBlur={commit}
        onKeyDown={handleKeyDown}
      />
      {maxScore ? <span className="score-max">/ {maxScore}</span> : null}
    </div>
  )
}

// The dialog to add an activity (a new column in the grid)
function ActivityModal({ items, onAdd, onClose }) {
  const [name, setName] = useState('')
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  // Pressing Escape closes the dialog
  useEffect(() => {
    const close = (e) => e.key === 'Escape' && !saving && onClose()
    window.addEventListener('keydown', close)
    return () => window.removeEventListener('keydown', close)
  }, [onClose, saving])

  async function handleSubmit(e) {
    e.preventDefault() // stop the page from reloading
    const trimmed = name.trim()

    if (trimmed === '') {
      setError('Enter an activity name.')
      return
    }
    if (items.some((i) => i.name.toLowerCase() === trimmed.toLowerCase())) {
      setError('This activity already exists in this class.')
      return
    }

    setSaving(true)
    setError('')
    try {
      await onAdd(trimmed)
      onClose()
    } catch (err) {
      setError(err.message)
      setSaving(false)
    }
  }

  return (
    <Modal
      title="Activity"
      onClose={saving ? () => {} : onClose}
      footer={
        <>
          <button className="btn ghost" type="button" onClick={onClose} disabled={saving}>
            Cancel
          </button>
          {/* Submits the form below via its id */}
          <button className="btn primary" type="submit" form="activity-form" disabled={saving}>
            {saving ? 'Saving…' : 'Save activity'}
          </button>
        </>
      }
    >
      <form id="activity-form" className="form-grid" onSubmit={handleSubmit}>
        <label>
          Activity name
          <input
            type="text"
            value={name}
            maxLength="150"
            autoFocus
            onChange={(e) => setName(e.target.value)}
          />
        </label>
        {error && <p className="form-error" role="alert">{error}</p>}
      </form>
    </Modal>
  )
}

export function StudentsView({ data }) {
  const [search, setSearch] = useState('')
  const [adding, setAdding] = useState(false)
  const [message, setMessage] = useState('')
  const { students, items, scores } = data

  const query = search.trim().toLowerCase()
  const shown = students.filter((s) =>
    `${s.firstName} ${s.lastName} ${s.id}`.toLowerCase().includes(query)
  )

  function findScore(studentId, assessment) {
    return scores.find((r) => r.studentId === studentId && r.assessment === assessment)
  }

  // Saves one typed score, and clears the last error when it works
  async function saveScore(studentId, assessment, score) {
    await data.saveScore({ studentId, assessment, score })
    setMessage('')
  }

  return (
    <div>
      <PageHeader
        eyebrow={data.classLabel}
        title="Students"
        description="Explore individual progress, topic strengths, and support history."
      />

      <div className="students-toolbar">
        <SubjectSelect data={data} />
        <span className="example-tag">{shown.length} students shown</span>

        <label className="search">
          <Icon name="search" size={14} />
          <input
            type="text"
            value={search}
            placeholder={`Search ${students.length} students...`}
            onChange={(e) => setSearch(e.target.value)}
          />
        </label>

        <button
          type="button"
          className="btn primary"
          disabled={data.status !== 'ready' || !data.selected}
          onClick={() => setAdding(true)}
        >
          Add activity
        </button>
      </div>

      {adding && <ActivityModal items={items} onAdd={data.addActivity} onClose={() => setAdding(false)} />}

      <DataGate data={data}>
        {message && <p className="form-error" role="alert">{message}</p>}

        {students.length === 0 ? (
          <p className="mapping-note">No students in this class yet. Import a class list file to add them.</p>
        ) : (
          <div className="students-scroll">
            <div
              className="students-grid"
              style={{ gridTemplateColumns: `minmax(190px, 1.7fr) repeat(${items.length}, minmax(110px, 1fr))` }}
            >
              <div className="col-card">
                <div className="col-head">Student Name</div>
                {shown.map((s) => (
                  <div className="col-cell name" key={s.id}>{s.firstName} {s.lastName}</div>
                ))}
              </div>

              {items.map((item) => (
                <div className="col-card" key={item.id}>
                  <div className="col-head" title={item.name}>
                    {item.name}
                    <small>out of {item.maxScore}</small>
                  </div>
                  {shown.map((s) => {
                    const record = findScore(s.id, item.name)
                    return (
                      <ScoreCell
                        key={`${s.id}-${item.id}-${record?.score ?? 'none'}`}
                        record={record}
                        maxScore={Number(item.maxScore)}
                        onSave={(score) => saveScore(s.id, item.name, score)}
                        onFail={setMessage}
                      />
                    )
                  })}
                </div>
              ))}
            </div>
          </div>
        )}

        {students.length > 0 && shown.length === 0 && (
          <p className="mapping-note">No students match your search.</p>
        )}

        <p className="mapping-note">
          Click a score to change it. Max scores come from Subject Mapping. Red scores are below {rules.supportThreshold}%.
        </p>
      </DataGate>
    </div>
  )
}