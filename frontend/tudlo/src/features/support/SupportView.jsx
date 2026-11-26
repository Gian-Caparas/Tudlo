import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { SubjectSelect } from '@/components/shared/SubjectSelect.jsx'
import { DataGate } from '@/components/shared/DataGate.jsx'
import { Badge } from '@/components/ui/Badge.jsx'
import { Avatar } from '@/components/ui/Avatar.jsx'
import { Icon } from '@/components/ui/Icon.jsx'
import { rules, getOverallStudentScore } from '@/utils/scoring.js'

function getSeverity(percent) {
  if (percent < 40) return { label: 'Critical', tone: 'danger' }
  if (percent < 50) return { label: 'High Priority', tone: 'danger' }
  if (percent < 60) return { label: 'Moderate', tone: 'warning' }
  return { label: 'On Track', tone: 'success' }
}

function SupportBody({ data, onNavigate }) {
  const { students, items, scores } = data

  // Overall statistics for the selected subject and section
  const scoredStudents = students
    .map((s) => ({
      ...s,
      overall: getOverallStudentScore(s.id, scores, items),
    }))
    .filter((s) => s.overall !== null)

  const passingStudents = scoredStudents.filter((s) => s.overall.percent >= rules.passMark)
  const atRiskStudents = scoredStudents
    .filter((s) => s.overall.percent < rules.supportThreshold)
    .sort((a, b) => a.overall.percent - b.overall.percent)

  return (
    <>
      <section className="kpi-grid kpi-grid-3">
        <div className="card kpi attention">
          <div className="kpi-top">
            <span>Students Needing Support</span>
            <div><Icon name="heart" size={16} /></div>
          </div>
          <strong>{atRiskStudents.length}</strong>
          <small className="down">Overall grade &lt; 60%</small>
        </div>

        <div className="card kpi">
          <div className="kpi-top">
            <span>Total Enrolled</span>
            <div><Icon name="users" size={16} /></div>
          </div>
          <strong>{students.length}</strong>
          <small>{scoredStudents.length} with recorded scores</small>
        </div>

        <div className="card kpi">
          <div className="kpi-top">
            <span>Passing Rate</span>
            <div><Icon name="check" size={16} /></div>
          </div>
          <strong>
            {scoredStudents.length ? `${Math.round((passingStudents.length / scoredStudents.length) * 100)}%` : '—'}
          </strong>
          <small>{passingStudents.length} of {scoredStudents.length} passing (≥75%)</small>
        </div>
      </section>

      <section className="card" style={{ marginTop: '20px' }}>
        <div className="section-head" style={{ marginBottom: '16px' }}>
          <div>
            <h2>At-Risk Learners ({atRiskStudents.length})</h2>
            <p>Students whose cumulative grade is below 60% in {data.classLabel}.</p>
          </div>
          {atRiskStudents.length > 0 && (
            <Badge tone="danger">Requires Support Intervention</Badge>
          )}
        </div>

        {atRiskStudents.length === 0 ? (
          <div style={{ padding: '24px 0', textAlign: 'center' }}>
            <p className="mapping-note" style={{ fontSize: '1rem', color: '#027a48' }}>
              ✓ All scored students currently maintain an overall grade of 60% or higher.
            </p>
          </div>
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Student</th>
                  <th>Student ID</th>
                  <th>Cumulative Score</th>
                  <th>Overall Grade</th>
                  <th>Completed Assessments</th>
                  <th>Support Priority</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {atRiskStudents.map((s) => {
                  const severity = getSeverity(s.overall.percent)
                  return (
                    <tr key={s.id}>
                      <td>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                          <Avatar name={`${s.firstName} ${s.lastName}`} size="sm" />
                          <strong>{s.firstName} {s.lastName}</strong>
                        </div>
                      </td>
                      <td><code>{s.id}</code></td>
                      <td>{s.overall.earned} / {s.overall.max} pts</td>
                      <td>
                        <strong style={{ color: '#b42318' }}>
                          {Math.round(s.overall.percent * 10) / 10}%
                        </strong>
                      </td>
                      <td>{s.overall.completedCount} of {items.length}</td>
                      <td>
                        <Badge tone={severity.tone}>{severity.label}</Badge>
                      </td>
                      <td>
                        <button
                          type="button"
                          className="btn ghost btn-sm"
                          onClick={() => onNavigate && onNavigate('Students')}
                        >
                           View student
                        </button>
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        )}
      </section>

      {scoredStudents.length > 0 && (
        <section className="card" style={{ marginTop: '20px' }}>
          <div className="section-head" style={{ marginBottom: '16px' }}>
            <div>
              <h2>All Scored Students ({scoredStudents.length})</h2>
              <p>Overview of all students with assessment records in {data.classLabel}.</p>
            </div>
          </div>

          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Student</th>
                  <th>Student ID</th>
                  <th>Cumulative Score</th>
                  <th>Overall Grade</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {scoredStudents.map((s) => {
                  const isAtRisk = s.overall.percent < rules.supportThreshold
                  const isPassing = s.overall.percent >= rules.passMark
                  return (
                    <tr key={s.id}>
                      <td>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                          <Avatar name={`${s.firstName} ${s.lastName}`} size="sm" />
                          <span>{s.firstName} {s.lastName}</span>
                        </div>
                      </td>
                      <td><code>{s.id}</code></td>
                      <td>{s.overall.earned} / {s.overall.max} pts</td>
                      <td>
                        <strong>{Math.round(s.overall.percent * 10) / 10}%</strong>
                      </td>
                      <td>
                        <Badge tone={isAtRisk ? 'danger' : isPassing ? 'success' : 'warning'}>
                          {isAtRisk ? 'Needs Support' : isPassing ? 'Passing' : 'Approaching Pass'}
                        </Badge>
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        </section>
      )}
    </>
  )
}

export function SupportView({ data, onNavigate }) {
  if (!data) {
    return <div className="card"><p>Loading support data…</p></div>
  }

  return (
    <div>
      <PageHeader
        eyebrow={data.classLabel || 'STUDENT SUPPORT'}
        title="Student Support"
        description="Monitor students requiring academic support based on an overall grade below 60%."
        action={<SubjectSelect data={data} />}
      />
      <DataGate data={data} onNavigate={onNavigate}>
        <SupportBody data={data} onNavigate={onNavigate} />
      </DataGate>
    </div>
  )
}
