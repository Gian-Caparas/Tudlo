import { useEffect, useState } from 'react'
import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { Badge } from '@/components/ui/Badge.jsx'
import { academicService } from '@/services/academicService.js'

export default function LearningGapsPage() {
  const [offerings, setOfferings] = useState([])
  const [selected, setSelected] = useState('')
  const [heatmap, setHeatmap] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true
    academicService.getMyOfferings()
      .then((items) => {
        if (!active) return
        setOfferings(items)
        if (items.length) setSelected(String(items[0].id))
      })
      .catch((requestError) => active && setError(requestError.message))
    return () => { active = false }
  }, [])

  useEffect(() => {
    if (!selected) return
    let active = true
    academicService.getHeatmap(selected)
      .then((data) => active && setHeatmap(data))
      .catch((requestError) => active && setError(requestError.message))
    return () => { active = false }
  }, [selected])

  return (
    <div>
      <PageHeader
        eyebrow="LEARNING GAPS"
        title="Student support heatmap"
        description="Identify students below the 60% support threshold."
        action={
          <select value={selected} onChange={(event) => setSelected(event.target.value)}>
            {offerings.map((offering) =>
              <option key={offering.id} value={offering.id}>
                {offering.subjectName} · {offering.sectionName}
              </option>)}
          </select>
        }
      />
      {error && <div className="card"><p>{error}</p></div>}
      {heatmap && (
        <section className="card">
          <h2>{heatmap.subjectName} · {heatmap.sectionName}</h2>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Student</th>
                  <th>Student ID</th>
                  <th>Observed %</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {heatmap.students.map((student) => (
                  <tr key={student.studentId}>
                    <td><strong>{student.fullName || 'Unnamed student'}</strong></td>
                    <td>{student.studentNumber}</td>
                    <td>{student.observedPercentage === null ? '—' : `${student.observedPercentage}%`}</td>
                    <td>
                      <Badge tone={student.needsSupport ? 'danger' : 'success'}>
                        {student.needsSupport ? 'Needs support' : 'On track'}
                      </Badge>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      )}
    </div>
  )
}
