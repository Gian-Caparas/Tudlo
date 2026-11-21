import { useState } from 'react'
import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { Icon } from '@/components/ui/Icon.jsx'
import { subjects } from '@/mockdata/mockData.js'
import { getPercent } from '@/utils/scoring.js'

// A line that explains how a real score becomes a percent, using the first score of this subject
function getExample(subject, items, scores, students) {
  const item = items[0]
  if (!item) return ''
  const first = `Example: ${item.name} is out of ${item.maxScore}.`

  const record = scores.find((r) => r.subject === subject && r.assessment === item.name && r.score !== null)
  const student = students.find((s) => s.id === record?.studentId)
  const percent = record ? getPercent(record, { [subject]: items }) : null
  if (!student || percent === null) return first

  return `${first} ${student.firstName} ${student.lastName}'s score of ${record.score} / ${item.maxScore} in Students equals ${Math.round(percent)}%.`
}

export function SubjectMappingView({ savedMapping, scores, students, onSave }) {
  const [draft, setDraft] = useState(savedMapping) // a copy you edit. Nothing is saved until you press Save.
  const [subject, setSubject] = useState(subjects[0])
  const [saved, setSaved] = useState(false)

  const items = draft[subject] ?? []

  // Change one field of one row in the chosen subject (never edit state directly)
  function handleChange(index, field, value) {
    setDraft({
      ...draft,
      [subject]: items.map((item, i) => (i === index ? { ...item, [field]: value } : item)),
    })
    setSaved(false)
  }

  function handleSubmit(e) {
    e.preventDefault() // stop the page from reloading
    onSave(draft) // sends the draft up to App
    setSaved(true)
  }

  return (
    <form onSubmit={handleSubmit}>
      <PageHeader
        eyebrow="ASSESSMENT SETUP"
        title="Subject Mapping"
        description="Connect assessment items to topics and outcomes for more meaningful analytics."
        action={
          <button type="submit" className="btn primary">
            <Icon name="check" size={15} /> Save mapping
          </button>
        }
      />

      <div className="subject-toolbar">
        <select value={subject} onChange={(e) => setSubject(e.target.value)} aria-label="Subject">
          {subjects.map((name) => (
            <option key={name} value={name}>{name}</option>
          ))}
        </select>
        <span className="example-tag">Example data</span>
        <span className="toolbar-note">These same assessment items and max scores are used in Students.</span>
      </div>

      <div className="mapping-cards">
        <div className="col-card">
          <div className="col-head">Assessment Item</div>
          {items.map((item) => (
            <div className="col-cell" key={item.name}>{item.name}</div>
          ))}
        </div>

        <div className="col-card">
          <div className="col-head">Learning Outcome</div>
          {items.map((item, index) => (
            <div className="col-cell left" key={item.name}>
              <input
                type="text"
                value={item.outcome}
                placeholder="Add a learning outcome"
                aria-label={`Learning outcome for ${item.name}`}
                onChange={(e) => handleChange(index, 'outcome', e.target.value)}
              />
            </div>
          ))}
        </div>

        <div className="col-card">
          <div className="col-head">Max Score</div>
          {items.map((item, index) => (
            <div className="col-cell" key={item.name}>
              <input
                type="number"
                min="1"
                className="max"
                value={item.maxScore}
                aria-label={`Max score for ${item.name}`}
                onChange={(e) => handleChange(index, 'maxScore', e.target.value)}
              />
            </div>
          ))}
        </div>
      </div>

      <p className="mapping-note">{getExample(subject, items, scores, students)}</p>

      {saved && (
        <div className="toast">
          <Icon name="check" size={16} /> Mapping saved
        </div>
      )}
    </form>
  )
}