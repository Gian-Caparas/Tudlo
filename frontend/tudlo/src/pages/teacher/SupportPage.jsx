import { useEffect, useState } from 'react'
import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { Badge } from '@/components/ui/Badge.jsx'
import { academicService } from '@/services/academicService.js'

export default function SupportPage() {
  const [history, setHistory] = useState([])
  const [error, setError] = useState('')
  const [form, setForm] = useState({
    enrollmentId: '',
    type: '',
    notes: '',
    activityDate: new Date().toISOString().slice(0, 10),
  })

  useEffect(() => {
    let active = true
    academicService.getSupportHistory()
      .then((items) => active && setHistory(items))
      .catch((requestError) => active && setError(requestError.message))
    return () => { active = false }
  }, [])

  async function submit(event) {
    event.preventDefault()
    setError('')
    try {
      await academicService.recordSupport({
        teacherId: 0,
        enrollmentId: Number(form.enrollmentId),
        type: form.type,
        notes: form.notes,
        activityDate: form.activityDate,
      })
      const items = await academicService.getSupportHistory()
      setHistory(items)
      setForm((current) => ({ ...current, enrollmentId: '', type: '', notes: '' }))
    } catch (requestError) {
      setError(requestError.message)
    }
  }

  return (
    <div>
      <PageHeader
        eyebrow="STUDENT SUPPORT"
        title="Support activities"
        description="Review interventions explicitly recorded for students."
      />
      {error && <div className="card"><p>{error}</p></div>}
      <form className="card" onSubmit={submit}>
        <h2>Record support activity</h2>
        <label>
          Enrollment ID
          <input
            type="number"
            required
            value={form.enrollmentId}
            onChange={(event) => setForm({ ...form, enrollmentId: event.target.value })}
          />
        </label>
        <label>
          Activity type
          <input
            required
            value={form.type}
            onChange={(event) => setForm({ ...form, type: event.target.value })}
          />
        </label>
        <label>
          Remarks
          <textarea
            value={form.notes}
            onChange={(event) => setForm({ ...form, notes: event.target.value })}
          />
        </label>
        <label>
          Date
          <input
            type="date"
            required
            value={form.activityDate}
            onChange={(event) => setForm({ ...form, activityDate: event.target.value })}
          />
        </label>
        <button className="btn primary" type="submit">Record support</button>
      </form>
      <section className="card">
        {history.length === 0 ? (
          <p>No support activities have been recorded yet.</p>
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr><th>Date</th><th>Type</th><th>Remarks</th></tr>
              </thead>
              <tbody>
                {history.map((item) => (
                  <tr key={item.id}>
                    <td>{item.activityDate}</td>
                    <td><Badge tone="success">{item.type}</Badge></td>
                    <td>{item.notes || '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </div>
  )
}
