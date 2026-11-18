import { useEffect, useState } from 'react'
import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { Badge } from '@/components/ui/Badge.jsx'
import { academicService } from '@/services/academicService.js'

export function MySubmissionsView() {
  const [rows, setRows] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true
    academicService.getOwnSubmissions()
      .then((submissions) => active && setRows(submissions))
      .catch((requestError) => active && setError(requestError.message))
    return () => { active = false }
  }, [])

  if (error) return <div className="card"><p>{error}</p></div>

  const submitted = rows.filter((row) => row.observedPercentage !== null).length
  const onTime = rows.filter((row) => row.status === 'SUBMITTED').length
  const needsAttention = rows.filter((row) =>
    row.status === 'MISSING' || row.status === 'NOT_REQUIRED').length

  return (
    <div>
      <PageHeader
        eyebrow="MY LEARNING"
        title="My Submissions"
        description="Track your assessment scores and submission status."
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
              {rows.map((row) => {
                const tone = row.status === 'MISSING'
                  ? 'danger'
                  : row.status === 'LATE'
                    ? 'warning'
                    : 'success'
                return (
                  <tr key={row.submissionId}>
                    <td><strong>{row.assessmentName}</strong></td>
                    <td className="muted">{row.dueDate ?? '—'}</td>
                    <td><strong>{row.observedPercentage !== null ? `${row.observedPercentage}%` : '—'}</strong></td>
                    <td><Badge tone={tone}>{row.status}</Badge></td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  )
}
