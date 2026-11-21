import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { SubjectSelect } from '@/components/shared/SubjectSelect.jsx'
import { DataGate } from '@/components/shared/DataGate.jsx'
import { Badge } from '@/components/ui/Badge.jsx'
import { rules, getPercent, getOverallStudentScore } from '@/utils/scoring.js'

function getTone(percent) {
  if (percent === null) return 'none'
  if (percent < rules.supportThreshold) return 'low'
  if (percent >= rules.goodThreshold) return 'high'
  return 'mid'
}

function LearningGapsBody({ data }) {
  const { students, items, scores } = data

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

  return (
    <section className="card">
      <div className="section-head" style={{ marginBottom: '16px' }}>
        <div>
          <h2>Performance Heatmap</h2>
          <p>Visual breakdown of student performance across all assessments in {data.classLabel}.</p>
        </div>
        <div style={{ display: 'flex', gap: '8px' }}>
          <Badge tone="danger">&lt;60% Gap</Badge>
          <Badge tone="warning">60–74%</Badge>
          <Badge tone="success">≥80% Strong</Badge>
        </div>
      </div>

      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Student</th>
              <th>Overall</th>
              {items.map((item) => (
                <th key={item.id} style={{ textAlign: 'center', minWidth: '90px' }}>
                  <div>{item.name}</div>
                  <small style={{ fontWeight: 'normal', opacity: 0.75 }}>/{item.maxScore}</small>
                </th>
              ))}
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {students.map((student) => {
              const overall = getOverallStudentScore(student.id, scores, items)
              const isAtRisk = overall && overall.percent < rules.supportThreshold

              return (
                <tr key={student.id}>
                  <td>
                    <strong>{student.firstName} {student.lastName}</strong>
                    <br />
                    <small style={{ opacity: 0.7 }}>{student.id}</small>
                  </td>
                  <td>
                    {overall ? (
                      <strong style={{ color: isAtRisk ? '#b42318' : 'inherit' }}>
                        {Math.round(overall.percent)}%
                      </strong>
                    ) : (
                      '—'
                    )}
                  </td>
                  {items.map((item) => {
                    const record = scores.find(
                      (r) => r.studentId === student.id && r.assessment === item.name
                    )
                    const percent = record ? getPercent(record, items) : null
                    const tone = getTone(percent)

                    return (
                      <td key={item.id} style={{ textAlign: 'center' }}>
                        {record ? (
                          <span className={`col-cell score ${tone}`} style={{ display: 'inline-block', padding: '4px 8px', borderRadius: '4px' }}>
                            {record.score}
                            <small style={{ display: 'block', fontSize: '0.7rem' }}>
                              {percent !== null ? `${Math.round(percent)}%` : ''}
                            </small>
                          </span>
                        ) : (
                          <span style={{ opacity: 0.35 }}>—</span>
                        )}
                      </td>
                    )
                  })}
                  <td>
                    <Badge tone={isAtRisk ? 'danger' : overall ? 'success' : 'none'}>
                      {isAtRisk ? 'Needs Support' : overall ? 'On Track' : 'No Data'}
                    </Badge>
                  </td>
                </tr>
              )
            })}
          </tbody>
        </table>
      </div>
    </section>
  )
}

export default function LearningGapsPage({ data }) {
  if (!data) {
    return <div className="card"><p>Loading learning gaps data…</p></div>
  }

  return (
    <div>
      <PageHeader
        eyebrow={data.classLabel || 'LEARNING GAPS'}
        title="Student Support Heatmap"
        description="Identify skill gaps and students falling below the 60% support threshold."
        action={<SubjectSelect data={data} />}
      />
      <DataGate data={data}>
        <LearningGapsBody data={data} />
      </DataGate>
    </div>
  )
}
