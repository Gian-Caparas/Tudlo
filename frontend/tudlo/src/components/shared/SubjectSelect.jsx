// The dropdown to choose a subject and section. Every teacher page uses the same one.
export function SubjectSelect({ data }) {
  if (data.subjects.length === 0) return null

  return (
    <select
      className="subject-select"
      value={data.selectedId ?? ''}
      onChange={(e) => data.selectSubject(Number(e.target.value))}
      aria-label="Subject and section"
    >
      {data.subjects.map((s) => (
        <option key={s.id} value={s.id}>{s.label}</option>
      ))}
    </select>
  )
}