import { useState } from 'react'
import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { Icon } from '@/components/ui/Icon.jsx'
import { classInfo, rules, subjects } from '@/mockdata/mockData.js'

// One score in the grid. Red is below the support threshold, green is a high score.
function ScoreCell({ record, maxScore }) {
  if (!record || record.score === null) return <div className="col-cell">—</div>

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

export function StudentsView({ students, scores, mapping }) {
  const [subject, setSubject] = useState(subjects[0])
  const [search, setSearch] = useState('')

  const items = mapping[subject] ?? []

  const query = search.trim().toLowerCase()
  const shown = students.filter((s) =>
    `${s.firstName} ${s.lastName} ${s.id}`.toLowerCase().includes(query)
  )

  function findScore(studentId, assessment) {
    return scores.find(
      (r) => r.subject === subject && r.studentId === studentId && r.assessment === assessment
    )
  }

  return (
    <div>
      <PageHeader
        eyebrow={classInfo.code}
        title="Students"
        description="Explore individual progress, topic strengths, and support history."
      />

      <div className="students-toolbar">
        <select value={subject} onChange={(e) => setSubject(e.target.value)} aria-label="Subject">
          {subjects.map((name) => (
            <option key={name} value={name}>{name}</option>
          ))}
        </select>
        <span className="example-tag">Example data · {shown.length} students shown</span>

        <label className="search">
          <Icon name="search" size={14} />
          <input
            type="text"
            value={search}
            placeholder={`Search ${students.length} students...`}
            onChange={(e) => setSearch(e.target.value)}
          />
        </label>

        {/* TODO: waiting for the design of what "Add activity" should do */}
        <button type="button" className="btn primary">Add activity</button>
      </div>

      <div className="students-scroll">
        <div
          className="students-grid"
          style={{ gridTemplateColumns: `minmax(190px, 1.7fr) repeat(${items.length}, minmax(86px, 1fr))` }}
        >
          <div className="col-card">
            <div className="col-head">Student Name</div>
            {shown.map((s) => (
              <div className="col-cell name" key={s.id}>{s.firstName} {s.lastName}</div>
            ))}
          </div>

          {items.map((item) => (
            <div className="col-card" key={item.name}>
              <div className="col-head">
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

      {shown.length === 0 && <p className="mapping-note">No students match your search.</p>}

      <p className="mapping-note">
        Assessment items and max scores match Subject Mapping. Red scores are below {rules.supportThreshold}%.
      </p>
    </div>
  )
}