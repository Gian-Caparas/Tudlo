import { useState, useRef } from 'react'
import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { SubjectSelect } from '@/components/shared/SubjectSelect.jsx'
import { DataGate } from '@/components/shared/DataGate.jsx'
import { Icon } from '@/components/ui/Icon.jsx'

const stepNames = ['Upload CSV', 'Done']
const expectedColumns = ['Student-id', 'Firstname', 'Lastname']
const expectedNaming = ['File Name: SubjectName', 'Section: Selected in header dropdown']
const maxFileSize = '10 MB'

// ---------- helpers ----------

// Splits one CSV line into cells. Commas inside "quotes" are kept together.
function splitLine(line) {
  const cells = []
  let current = ''
  let inQuotes = false
  for (const char of line) {
    if (char === '"') inQuotes = !inQuotes
    else if (char === ',' && !inQuotes) {
      cells.push(current.trim())
      current = ''
    } else current += char
  }
  cells.push(current.trim())
  return cells
}

function normalizeHeader(h) {
  return h.toLowerCase().replace(/[\s\-_]+/g, '').trim()
}

// Turns the text of a CSV file into students like { id, firstName, lastName }.
// Returns { students } or { error }.
function parseCsv(text) {
  const lines = text.replace(/^\uFEFF/, '').split(/\r?\n/).filter((line) => line.trim() !== '')
  if (lines.length < 2) return { error: 'This file has no rows to import.' }

  const rawHeaders = splitLine(lines[0])
  const headers = rawHeaders.map(normalizeHeader)

  const id = headers.findIndex((h) => ['studentid', 'stundentid', 'id', 'studentno', 'studentnumber'].includes(h))
  const firstName = headers.findIndex((h) => ['firstname', 'studentfirstname', 'givenname', 'fname'].includes(h))
  const lastName = headers.findIndex((h) => ['lastname', 'studentlastname', 'surname', 'familyname', 'lname'].includes(h))

  if ([id, firstName, lastName].includes(-1)) {
    return { error: `Your CSV needs these columns: ${expectedColumns.join(', ')}.` }
  }

  const students = lines
    .slice(1)
    .map((line) => {
      const cells = splitLine(line)
      return {
        id: cells[id] ?? '',
        firstName: cells[firstName] ?? '',
        lastName: cells[lastName] ?? '',
      }
    })
    .filter((student) => student.id !== '') // a row without a Student ID cannot be saved

  if (students.length === 0) return { error: 'This file has no rows to import.' }
  return { students }
}

// ---------- small components ----------

// Shows the steps. Only needs to know which step we are on.
function Stepper({ step }) {
  return (
    <div className="stepper">
      {stepNames.map((name, i) => {
        const number = i + 1
        const state = number < step ? 'done' : number === step ? 'active' : ''
        return (
          <button key={name} className={state} tabIndex={-1}>
            <i>{number < step ? <Icon name="check" size={13} /> : number}</i>
            <span><small>STEP {number}</small>{name}</span>
          </button>
        )
      })}
    </div>
  )
}

// One line under the upload box: a label on the left, chips on the right
function HintRow({ label, items }) {
  return (
    <div className="file-hint">
      <strong>{label}</strong>
      {items.map((item) => (
        <span key={item}>{item}</span>
      ))}
    </div>
  )
}

// ---------- the page ----------

function ImportBody({ data, onNavigate }) {
  const [step, setStep] = useState(1) // 1 = upload, 2 = done
  const [result, setResult] = useState(null) // { imported, skipped, failed }
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  const fileInput = useRef(null) // points to the hidden file picker

  // Reads the chosen file, sends the students to the backend, and moves to the last step
  async function readFile(file) {
    if (!file) return
    if (!file.name.toLowerCase().endsWith('.csv')) {
      setError('Please choose a CSV file.')
      return
    }

    const parsed = parseCsv(await file.text())
    if (parsed.error) {
      setError(parsed.error)
      return
    }

    // Auto-match subject from filename if matching
    const rawName = file.name.replace(/\.csv$/i, '').trim().toLowerCase()
    if (data.subjects && data.selectSubject) {
      const match = data.subjects.find((s) => {
        const subName = s.subjectName.toLowerCase()
        const full = `${s.subjectName} - ${s.classSection}`.toLowerCase()
        const fullUnderscore = `${s.subjectName}_${s.classSection}`.toLowerCase()
        return rawName === full || rawName === fullUnderscore || rawName === subName
      })
      if (match && match.id !== data.selectedId) {
        data.selectSubject(match.id)
      }
    }

    setBusy(true)
    setError('')
    try {
      const outcome = await data.importStudents(parsed.students)
      if (outcome.imported === 0) {
        setError(
          outcome.failed.length > 0
            ? `No students were imported. Check that these Student IDs have an account: ${outcome.failed.join(', ')}`
            : 'All of these students are already in this class list.'
        )
      } else {
        setResult(outcome)
        setStep(2)
      }
    } catch (err) {
      setError(err.message)
    }
    setBusy(false)
  }

  function handleFileChange(e) {
    readFile(e.target.files[0])
    e.target.value = '' // so choosing the same file again still works
  }

  function handleDrop(e) {
    e.preventDefault() // stop the browser from opening the file
    readFile(e.dataTransfer.files[0])
  }

  function handleReset() {
    setResult(null)
    setError('')
    setStep(1)
  }

  return (
    <>
      <Stepper step={step} />

      {step === 1 && (
        <section className="card upload-card">
          <div
            className="upload-zone"
            onDragOver={(e) => e.preventDefault()}
            onDrop={handleDrop}
          >
            <div><Icon name="upload" size={22} /></div>
            <h2>Upload Class List CSV</h2>
            <p>Drag and drop a CSV file here, or choose one from your computer. Target: <strong>{data.classLabel}</strong></p>

            <input
              ref={fileInput}
              type="file"
              accept=".csv"
              onChange={handleFileChange}
              hidden
            />
            <button
              className="btn secondary"
              type="button"
              disabled={busy}
              onClick={() => fileInput.current.click()}
            >
              {busy ? 'Importing…' : 'Choose CSV file'}
            </button>

            <small>CSV only · Maximum {maxFileSize}</small>
            {error && <small role="alert" style={{ color: '#b42318' }}>{error}</small>}
          </div>
          <HintRow label="Expected columns" items={expectedColumns} />
          <HintRow label="Expected Naming" items={expectedNaming} />
        </section>
      )}

      {step === 2 && result && (
        <section className="card done-card">
          <div className="done-check"><Icon name="check" size={28} /></div>
          <p className="eyebrow">IMPORT COMPLETE</p>
          <h2>{result.imported} Student rows are ready</h2>
          <p>
            Your class analytics and learning gaps have been updated.
            {result.skipped > 0 &&
              ` ${result.skipped} ${result.skipped === 1 ? 'student was' : 'students were'} already in this class list and skipped.`}
            {result.failed.length > 0 &&
              ` ${result.failed.length} could not be imported. Check that these Student IDs have an account: ${result.failed.join(', ')}.`}
          </p>
          <div>
            <button className="btn ghost" onClick={handleReset}>Import another file</button>
            <button className="btn primary" onClick={() => onNavigate('Learning Gaps')}>View learning gaps</button>
          </div>
        </section>
      )}
    </>
  )
}

export function ImportClassListView({ data, onNavigate }) {
  return (
    <div>
      <PageHeader
        eyebrow="DATA WORKFLOW"
        title="Import Class List"
        description="Bring scores into Tudlo, validate them, and correct issues before analysis."
        action={<SubjectSelect data={data} />}
      />
      <DataGate data={data}>
        <ImportBody data={data} onNavigate={onNavigate} />
      </DataGate>
    </div>
  )
}