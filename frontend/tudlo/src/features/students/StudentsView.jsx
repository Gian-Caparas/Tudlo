import { useState } from 'react'
import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { DataGate } from '@/components/shared/DataGate.jsx'
import { Icon } from '@/components/ui/Icon.jsx'
import { rules } from '@/utils/scoring.js'

// One score in the grid. Red is below the support threshold, green is a high score.
function ScoreCell({ record, maxScore }) {
  if (!record) return <div className="col-cell">—</div>

  const percent = maxScore ? (record.score / maxScore) * 100 : null
  let tone = ''
  if (percent !== null && percent < rules.supportThreshold) tone = 'low'
  else if (percent !== null && percent >= rules.goodThreshold) tone = 'high'

  return (
    <div className={`col-cell score ${tone}`}>
      {maxScore ? `${record.score} / ${maxScore}` : record.score}
    </div>
  )
}

// The form to give a student a score for an assessment item
function ActivityForm({ data, onClose }) {
  const { students, items, scores, addActivity } = data
  const [studentId, setStudentId] = useState(students[0]?.id ?? '')
  const [assessment, setAssessment] = useState(items[0]?.name ?? '')
  const [score, setScore] = useState('')
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  const item = items.find((i) => i.name === assessment)
  const current = scores.find((r) => r.studentId === studentId && r.assessment === assessment)

  async function handleSubmit(e) {
    e.preventDefault() // stop the page from reloading
    const value = Number(score)

    if (score === '' || Number.isNaN(value) || value < 0) {
      setError('Enter a score of 0 or more.')
      return
    }
    if (item && value > Number(item.maxScore)) {
      setError(`The score cannot be more than ${item.maxScore}.`)
      return
    }

    setSaving(true)
    setError('')
    try {
      await addActivity({ studentId, assessment, score: value })
      onClose()
    } catch (err) {
      setError(err.message)
      setSaving(false)
    }
  }

  return (
    <form className="activity-form card" onSubmit={handleSubmit}>
      <label>
        Student
        <select value={studentId} onChange={(e) => setStudentId(e.target.value)}>
          {students.map((s) => (
            <option key={s.id} value={s.id}>{s.firstName} {s.lastName}</option>
          ))}
        </select>
      </label>

      <label>
        Assessment item
        <select value={assessment} onChange={(e) => setAssessment(e.target.value)}>
          {items.map((i) => (
            <option key={i.id} value={i.name}>{i.name} (out of {i.maxScore})</option>
          ))}
        </select>
      </label>

      <label>
        Score
        <input
          type="number"
          step="any"
          min="0"
          value={score}
          placeholder={current ? `Now ${current.score}` : 'Score'}
          onChange={(e) => setScore(e.target.value)}
        />
      </label>

      <div className="activity-actions">
        <button type="submit" className="btn primary" disabled={saving}>
          {saving ? 'Saving…' : current ? 'Replace score' : 'Save score'}
        </button>
        <button type="button" className="btn ghost" onClick={onClose}>Cancel</button>
      </div>

      {error && <p className="form-error" role="alert">{error}</p>}
    </form>
  )
}

export function StudentsView({ data }) {
  const [search, setSearch] = useState('')
  const [adding, setAdding] = useState(false)
  const { students, items, scores } = data

  const query = search.trim().toLowerCase()
  const shown = students.filter((s) =>
    `${s.firstName} ${s.lastName} ${s.id}`.toLowerCase().includes(query)
  )
  const ready = data.status === 'ready' && students.length > 0 && items.length > 0

  function findScore(studentId, assessment) {
    return scores.find((r) => r.studentId === studentId && r.assessment === assessment)
  }

  return (
    <div>
      <PageHeader
        eyebrow={data.classLabel}
        title="Students"
        description="Explore individual progress, topic strengths, and support history."
      />

      <div className="students-toolbar">
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

        <button type="button" className="btn primary" disabled={!ready} onClick={() => setAdding(true)}>
          Add activity
        </button>
      </div>

      <DataGate data={data}>
        {adding && <ActivityForm data={data} onClose={() => setAdding(false)} />}

        {students.length === 0 ? (
          <p className="mapping-note">No students in this class list yet. Add them in Import Class List.</p>
        ) : (
          <div className="students-scroll">
            <div
              className="students-grid"
              style={{ gridTemplateColumns: `minmax(190px, 1.7fr) repeat(${items.length}, minmax(96px, 1fr))` }}
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
                  {shown.map((s) => (
                    <ScoreCell key={s.id} record={findScore(s.id, item.name)} maxScore={Number(item.maxScore)} />
                  ))}
                </div>
              ))}
            </div>
          </div>
        )}

        {students.length > 0 && shown.length === 0 && (
          <p className="mapping-note">No students match your search.</p>
        )}

        <p className="mapping-note">
          Assessment items and max scores match Subject Mapping. Red scores are below {rules.supportThreshold}%.
        </p>
      </DataGate>
    </div>
  )
}