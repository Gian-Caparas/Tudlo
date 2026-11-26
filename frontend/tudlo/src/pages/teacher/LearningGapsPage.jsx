import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { SubjectSelect } from '@/components/shared/SubjectSelect.jsx'
import { DataGate } from '@/components/shared/DataGate.jsx'
import { Avatar } from '@/components/ui/Avatar.jsx'
import { Badge } from '@/components/ui/Badge.jsx'
import { rules, average, getPercent, getOverallStudentScore } from '@/utils/scoring.js'

// Returns the heatmap cell color based on the score percentage
function getCellColor(percent) {
  if (percent === null) return 'empty'
  if (percent < rules.supportThreshold) return 'red' // Below 60%
  if (percent < 70) return 'orange'                  // 60% – 69%
  if (percent < rules.goodThreshold) return 'yellow' // 70% – 79%
  return 'green'                                     // 80% and above
}

// Calculates and ranks assessments from highest to lowest % missed
function getMostMissed(items, scores) {
  return items
    .map((item) => {
      // Collect valid percentage scores for this assessment
      const percents = scores
        .filter((r) => r.assessment === item.name)
        .map((r) => getPercent(r, items))
        .filter((p) => p !== null)

      if (percents.length === 0) return null

      // Missed % is 100 minus the class average for this item
      const missed = Math.max(0, Math.round(100 - average(percents)))
      return { ...item, missed }
    })
    .filter(Boolean)
    .sort((a, b) => b.missed - a.missed) // Highest missed % first
}

// Single row in the heatmap for one student
function StudentRow({ student, items, scores, gridColumns }) {
  const fullName = `${student.firstName} ${student.lastName}`
  const overall = getOverallStudentScore(student.id, scores, items)
  const isAtRisk = overall && overall.percent < rules.supportThreshold

  return (
    <div className="heat-row" style={{ gridTemplateColumns: gridColumns }}>
      {/* Sticky student name and ID column */}
      <div className="student-name">
        <Avatar name={fullName} size="sm" />
        <div className="student-name-meta">
          <strong>{fullName}</strong>
          <small>{student.id}</small>
        </div>
      </div>

      {/* Student's overall average percentage cell */}
      {overall ? (
        <div className={`heat-cell ${getCellColor(overall.percent)}`}>
          <span>{Math.round(overall.percent)}%</span>
        </div>
      ) : (
        <div className="heat-cell empty">—</div>
      )}

      {/* Individual score cells for each assessment */}
      {items.map((item) => {
        const record = scores.find((r) => r.studentId === student.id && r.assessment === item.name)
        const percent = record ? getPercent(record, items) : null

        if (!record) return <div key={item.id} className="heat-cell empty">—</div>

        return (
          <div key={item.id} className={`heat-cell ${getCellColor(percent)}`}>
            <span>{record.score}</span>
            {percent !== null && <small>{Math.round(percent)}%</small>}
          </div>
        )
      })}

      {/* Support status badge */}
      <div className="heat-status">
        <Badge tone={isAtRisk ? 'danger' : overall ? 'success' : 'neutral'}>
          {isAtRisk ? 'Needs Support' : overall ? 'On Track' : 'No Data'}
        </Badge>
      </div>
    </div>
  )
}

function LearningGapsBody({ data }) {
  const { students, items, scores, classLabel } = data

  // Show an empty message if students or assessments haven't been added yet
  if (students.length === 0 || items.length === 0) {
    return (
      <div className="card" style={{ textAlign: 'center', padding: '32px' }}>
        <p className="mapping-note">
          {students.length === 0
            ? 'No students enrolled in this class yet. Import students in Import Class List.'
            : 'No assessment items found. Add items in Subject Mapping.'}
        </p>
      </div>
    )
  }

  // Dynamic column widths: Student | Overall | [Assessments...] | Status
  const gridColumns = `minmax(180px, 1.35fr) 88px repeat(${items.length}, minmax(105px, 1fr)) 110px`
  const mostMissed = getMostMissed(items, scores)

  return (
    <div className="gap-layout">
      {/* Left panel: Topic & Assessment Performance Heatmap */}
      <section className="card heatmap-card">
        <div className="section-head">
          <div>
            <h2>Performance Heatmap</h2>
            <p>Visual breakdown of student performance across all assessments in {classLabel}.</p>
          </div>
          {/* Color key legend */}
          <div className="legend heat-legend">
            <span><i className="red" /> &lt;60% Gap</span>
            <span><i className="orange" /> 60–69%</span>
            <span><i className="yellow" /> 70–79%</span>
            <span><i className="green" /> ≥80% Strong</span>
          </div>
        </div>

        {/* Horizontal scroll wrapper (Student column stays sticky) */}
        <div className="heatmap-scroll">
          <div className="heatmap">
            {/* Column headers */}
            <div className="heat-head" style={{ gridTemplateColumns: gridColumns }}>
              <span>Student</span>
              <span>Overall</span>
              {items.map((item) => (
                <span key={item.id}>
                  {item.name}
                  <small style={{ display: 'block', fontWeight: 400, opacity: 0.75 }}>
                    /{item.maxScore}
                  </small>
                </span>
              ))}
              <span>Status</span>
            </div>

            {/* Student heatmap rows */}
            {students.map((student) => (
              <StudentRow
                key={student.id}
                student={student}
                items={items}
                scores={scores}
                gridColumns={gridColumns}
              />
            ))}
          </div>
        </div>
      </section>

      {/* Right panel: Ranked list of assessments students struggled with most */}
      <aside className="card missed-list">
        <div className="section-head">
          <div>
            <h2>Most missed assessments</h2>
            <p>Ranked across all submissions</p>
          </div>
        </div>

        {mostMissed.length === 0 ? (
          <p className="empty-state">No assessment submissions recorded yet.</p>
        ) : (
          mostMissed.map((entry, index) => (
            <div className="missed" key={entry.id}>
              <i>{index + 1}</i>
              <div>
                <strong>{entry.name}</strong>
                <span>{entry.outcome || `Out of ${entry.maxScore} points`}</span>
                <Badge tone="neutral">{classLabel}</Badge>
              </div>
              <b>{entry.missed}% missed</b>
            </div>
          ))
        )}
      </aside>
    </div>
  )
}

export default function LearningGapsPage({ data }) {
  if (!data) {
    return <div className="card"><p>Loading learning gaps data…</p></div>
  }

  return (
    <div>
      {/* Top header with class label and subject switcher */}
      <PageHeader
        eyebrow={data.classLabel || 'LEARNING GAPS'}
        title="Student Support Heatmap"
        description="Identify skill gaps and students falling below the 60% support threshold."
        action={<SubjectSelect data={data} />}
      />
      {/* Handles loading/error states before rendering the page body */}
      <DataGate data={data}>
        <LearningGapsBody data={data} />
      </DataGate>
    </div>
  )
}
