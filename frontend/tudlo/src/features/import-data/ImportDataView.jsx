import { useState, useRef } from 'react'
import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { Icon } from '@/components/ui/Icon.jsx'
import { importSettings } from '@/mockdata/mockData.js'

const { stepNames, expectedColumns, maxFileSize } = importSettings

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

// Turns the text of a CSV file into rows like { student, assessment, score, maxScore }.
// Returns { rows } or { error }.
function parseCsv(text) {
  const lines = text.replace(/^\uFEFF/, '').split(/\r?\n/).filter((line) => line.trim() !== '')
  if (lines.length < 2) return { error: 'This file has no rows to import.' }

  const headers = splitLine(lines[0]).map((h) => h.toLowerCase())
  const student = headers.findIndex((h) => h === 'student name' || h === 'student')
  const assessment = headers.indexOf('assessment')
  const score = headers.indexOf('score')
  const maxScore = headers.findIndex((h) => h === 'max score' || h === 'max_score' || h === 'maxscore')

  if ([student, assessment, score, maxScore].includes(-1)) {
    return { error: `Your CSV needs these columns: ${expectedColumns.join(', ')}.` }
  }

  const rows = lines.slice(1).map((line) => {
    const cells = splitLine(line)
    return {
      student: cells[student] ?? '',
      assessment: cells[assessment] ?? '',
      score: cells[score] ?? '',
      maxScore: cells[maxScore] ?? '',
    }
  })
  return { rows }
}

// Turns a CSV row (text) into a score record (numbers).
// A missing or non-number score is saved as null, meaning "not submitted".
function toRecord(row) {
  const score = Number(row.score)
  const maxScore = Number(row.maxScore)
  const hasScore = row.score !== '' && !Number.isNaN(score) && maxScore > 0
  return {
    student: row.student,
    assessment: row.assessment,
    score: hasScore ? score : null,
    maxScore,
  }
}

// ---------- small components ----------

// Shows the 3 steps. Only needs to know which step we are on.
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

// ---------- the page ----------

export function ImportDataView({ onImport, onNavigate }) {
  const [step, setStep] = useState(1) // 1 = upload, 2 = review, 3 = done
  const [rows, setRows] = useState([]) // rows read from the CSV
  const [fileName, setFileName] = useState('')
  const [error, setError] = useState('')
  const fileInput = useRef(null) // points to the hidden file picker

  // Reads the chosen file and moves on to step 2
  async function readFile(file) {
    if (!file) return
    if (!file.name.toLowerCase().endsWith('.csv')) {
      setError('Please choose a CSV file.')
      return
    }

    const text = await file.text()
    const result = parseCsv(text)

    if (result.error) {
      setError(result.error)
      return
    }

    setError('')
    setRows(result.rows)
    setFileName(file.name)
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

  function handleImport() {
    onImport(rows.map(toRecord)) // sends every row up to App
    setStep(3)
  }

  function handleReset() {
    setRows([])
    setFileName('')
    setError('')
    setStep(1)
  }

  return (
    <div>
      <PageHeader
        eyebrow="DATA WORKFLOW"
        title="Import assessment data"
        description="Bring scores into Tudlo before analysis."
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
          <div className="file-hint">
            <strong>Expected columns</strong>
            {expectedColumns.map((column) => (
              <span key={column}>{column}</span>
            ))}
          </div>
        </section>
      )}

      {step === 2 && (
        <>
          <div className="summary-banner">
            <Icon name="alert" size={16} />
            <div>
              <strong>{rows.length} rows found in {fileName}.</strong>
              <span>Check the rows below, then import them.</span>
            </div>
            <button className="btn primary" onClick={handleImport}>Import rows</button>
          </div>

          <section className="card">
            <div className="section-head">
              <div>
                <h2>Rows to import</h2>
                <p>{fileName} · {rows.length} rows</p>
              </div>
            </div>
            <div className="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>Student</th>
                    <th>Assessment</th>
                    <th>Score</th>
                    <th>Max score</th>
                  </tr>
                </thead>
                <tbody>
                  {rows.map((row, i) => (
                    <tr key={i} className="valid-row">
                      <td>{row.student}</td>
                      <td>{row.assessment}</td>
                      <td><strong>{row.score === '' ? '—' : row.score}</strong></td>
                      <td>{row.maxScore}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        </>
      )}

      {step === 3 && (
        <section className="card done-card">
          <div className="done-check"><Icon name="check" size={28} /></div>
          <p className="eyebrow">IMPORT COMPLETE</p>
          <h2>{rows.length} score rows are ready</h2>
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