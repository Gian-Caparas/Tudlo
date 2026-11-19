import { useState, useRef } from 'react'
import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { Icon } from '@/components/ui/Icon.jsx'
import { importSettings } from '@/mockdata/mockData.js'

const { stepNames, expectedColumns, expectedNaming, maxFileSize } = importSettings

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

// Turns the text of a CSV file into students like { id, firstName, lastName }.
// Returns { students } or { error }.
function parseCsv(text) {
  const lines = text.replace(/^\uFEFF/, '').split(/\r?\n/).filter((line) => line.trim() !== '')
  if (lines.length < 2) return { error: 'This file has no rows to import.' }

  const headers = splitLine(lines[0]).map((h) => h.toLowerCase())
  const id = headers.findIndex((h) => h === 'student id' || h === 'id')
  const firstName = headers.findIndex((h) => h === 'student first name' || h === 'first name')
  const lastName = headers.findIndex((h) => h === 'student last name' || h === 'last name')

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

export function ImportClassListView({ onImport, onNavigate }) {
  const [step, setStep] = useState(1) // 1 = upload, 2 = done
  const [count, setCount] = useState(0) // how many students were imported
  const [error, setError] = useState('')
  const fileInput = useRef(null) // points to the hidden file picker

  // Reads the chosen file, imports the students, and moves to the last step
  async function readFile(file) {
    if (!file) return
    if (!file.name.toLowerCase().endsWith('.csv')) {
      setError('Please choose a CSV file.')
      return
    }

    const result = parseCsv(await file.text())
    if (result.error) {
      setError(result.error)
      return
    }

    onImport(result.students) // sends the students up to App
    setCount(result.students.length)
    setError('')
    setStep(2)
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
    setCount(0)
    setError('')
    setStep(1)
  }

  return (
    <div>
      <PageHeader
        eyebrow="DATA WORKFLOW"
        title="Import Class List"
        description="Bring scores into Tudlo, validate them, and correct issues before analysis."
      />

      <Stepper step={step} />

      {step === 1 && (
        <section className="card upload-card">
          <div
            className="upload-zone"
            onDragOver={(e) => e.preventDefault()}
            onDrop={handleDrop}
          >
            <div><Icon name="upload" size={22} /></div>
            <h2>Upload your score sheet</h2>
            <p>Drag and drop a CSV file here, or choose one from your computer.</p>

            <input
              ref={fileInput}
              type="file"
              accept=".csv"
              onChange={handleFileChange}
              hidden
            />
            <button className="btn secondary" onClick={() => fileInput.current.click()}>
              Choose CSV file
            </button>

            <small>CSV only · Maximum {maxFileSize}</small>
            {error && <small style={{ color: '#b42318' }}>{error}</small>}
          </div>
          <HintRow label="Expected columns" items={expectedColumns} />
          <HintRow label="Expected Naming" items={expectedNaming} />
        </section>
      )}

      {step === 2 && (
        <section className="card done-card">
          <div className="done-check"><Icon name="check" size={28} /></div>
          <p className="eyebrow">IMPORT COMPLETE</p>
          <h2>{count} Student rows are ready</h2>
          <p>Your class analytics and learning gaps have been updated.</p>
          <div>
            <button className="btn ghost" onClick={handleReset}>Import another file</button>
            <button className="btn primary" onClick={() => onNavigate('Learning Gaps')}>View learning gaps</button>
          </div>
        </section>
      )}
    </div>
  )
}