import { useRef, useState } from 'react'
import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { Icon } from '@/components/ui/Icon.jsx'
import { academicService } from '@/services/academicService.js'

export function ImportDataView() {
  const [step, setStep] = useState(1)
  const [term, setTerm] = useState('Current')
  const [file, setFile] = useState(null)
  const [summary, setSummary] = useState(null)
  const [error, setError] = useState('')
  const fileInput = useRef(null)

  function selectFile(selected) {
    if (!selected) return
    if (!selected.name.toLowerCase().endsWith('.xlsx')) {
      setError('Please choose an Excel (.xlsx) classlist.')
      return
    }
    setError('')
    setFile(selected)
  }

  async function handleImport() {
    if (!file) return
    setError('')
    try {
      const result = await academicService.importClasslist(file, term)
      setSummary(result)
      setStep(3)
    } catch (requestError) {
      setError(requestError.message)
    }
  }

  function reset() {
    setFile(null)
    setSummary(null)
    setError('')
    setStep(1)
  }

  return (
    <div>
      <PageHeader
        eyebrow="DATA WORKFLOW"
        title="Import classlist"
        description="Upload the subject workbook before managing assessments and students."
      />

      {step === 1 && (
        <section className="card upload-card">
          <div
            className="upload-zone"
            onDragOver={(event) => event.preventDefault()}
            onDrop={(event) => {
              event.preventDefault()
              selectFile(event.dataTransfer.files[0])
            }}
          >
            <div><Icon name="upload" size={22} /></div>
            <h2>Upload your .xlsx classlist</h2>
            <p>Use the subject name as the filename and section names as worksheet names.</p>
            <input
              ref={fileInput}
              type="file"
              accept=".xlsx"
              hidden
              onChange={(event) => selectFile(event.target.files[0])}
            />
            <button className="btn secondary" type="button" onClick={() => fileInput.current.click()}>
              Choose Excel file
            </button>
            <label>
              Term
              <input value={term} onChange={(event) => setTerm(event.target.value)} />
            </label>
            {file && <strong>{file.name}</strong>}
            {error && <small style={{ color: '#b42318' }}>{error}</small>}
            {file && (
              <button className="btn primary" type="button" onClick={handleImport}>
                Import classlist
              </button>
            )}
          </div>
        </section>
      )}

      {step === 3 && summary && (
        <section className="card upload-card">
          <h2>Import complete</h2>
          <p>{summary.subject} · {summary.term}</p>
          <p>
            {summary.studentsCreated} students created · {summary.studentsReused} reused ·
            {' '}{summary.enrollmentsCreated} enrollments created · {summary.enrollmentsReused} reused
          </p>
          {summary.rejectedRows?.length > 0 && (
            <ul>
              {summary.rejectedRows.map((row, index) => (
                <li key={`${row.section}-${row.rowNumber}-${index}`}>
                  {row.section}, row {row.rowNumber}: {row.reason}
                </li>
              ))}
            </ul>
          )}
          {error && <small style={{ color: '#b42318' }}>{error}</small>}
          <button className="btn secondary" type="button" onClick={reset}>Import another file</button>
        </section>
      )}
    </div>
  )
}
