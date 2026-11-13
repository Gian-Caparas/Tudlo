import { PageHeader } from '../components/shared/PageHeader.jsx'
import { Badge } from '../components/ui/Badge.jsx'
import { mySubmissions } from '../mockdata/mockData.js'

function MySubmissions() {
  const { description, rows } = mySubmissions

  const submitted = rows.filter((r) => r.score !== null).length
  const onTime = rows.filter((r) => r.status === 'On Time').length
  const needsAttention = rows.filter(
    (r) => r.status === 'Overdue' || r.status === 'Missing'
  ).length

  return (
    <div>
      <PageHeader
        eyebrow="MY LEARNING"
        title="My Submissions"
        description={description}
      />

      <section className="submission-summary">
        <div>
          <span>Submitted</span>
          <strong>{submitted} of {rows.length}</strong>
        </div>
        <div>
          <span>On time</span>
          <strong>{onTime}</strong>
        </div>
        <div>
          <span>Needs attention</span>
          <strong className="danger-text">{needsAttention}</strong>
        </div>
      </section>

      <section className="card">
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Assessment</th>
                <th>Due date</th>
                <th>Score</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {rows.map((r) => (
                <tr key={r.name}>
                  <td><strong>{r.name}</strong></td>
                  <td className="muted">{r.due}</td>
                  <td><strong>{r.score !== null ? `${r.score}%` : '—'}</strong></td>
                  <td><Badge tone={r.tone}>{r.status}</Badge></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  )
}

export default MySubmissions