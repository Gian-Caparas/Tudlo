import { useState, useRef } from 'react'
import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { SubjectSelect } from '@/components/shared/SubjectSelect.jsx'
import { DataGate } from '@/components/shared/DataGate.jsx'
import { Icon } from '@/components/ui/Icon.jsx'

const stepNames = ['Upload CSV', 'Done']
const expectedColumns = ['Student ID', 'Student First Name', 'Student Last Name']
const expectedNaming = ['File Name: Subject_Section.csv', 'Selected class is used when the name does not match']
const maxFileSize = '10 MB'

// ---------- helpers ----------

function splitLine(line, delimiter) {
  const cells = []
  let current = ''
  let inQuotes = false
  for (let i = 0; i < line.length; i += 1) {
    const char = line[i]
    if (char === '"' && inQuotes && line[i + 1] === '"') {
      current += '"'
      i += 1
    } else if (char === '"') {
      inQuotes = !inQuotes
    } else if (char === delimiter && !inQuotes) {
      cells.push(current.trim())
      current = ''
    } else current += char
  }
  cells.push(current.trim())
  return cells
}

function normalizeHeader(header) {
  return header.toLowerCase().replace(/[^a-z0-9]/g, '')
}

function findDelimiter(headerLine) {
  return [',', ';', '\t']
    .map((delimiter) => ({
      delimiter,
      count: splitLine(headerLine, delimiter).length - 1,
    }))
    .sort((a, b) => b.count - a.count)[0].delimiter
}

function getClassFromFileName(fileName, subjects) {
  const base = fileName
    .replace(/\.csv$/i, '')
    .replace(/\s*\(\d+\)$/, '')
    .trim()
  const normalize = (value) => value.toLowerCase().replace(/[\s_-]+/g, '')
  const normalizedBase = normalize(base)

  const exactClass = subjects.find(
    (subject) => normalizedBase === normalize(`${subject.subjectName}${subject.classSection}`)
  )
  if (exactClass) return exactClass

  const knownClassPrefix = subjects
    .flatMap((subject) => [
      { subject, prefix: `${subject.subjectName}_${subject.classSection}` },
      { subject, prefix: `${subject.subjectName}-${subject.classSection}` },
      { subject, prefix: `${subject.subjectName} ${subject.classSection}` },
    ])
    .filter(({ prefix }) => {
      const lowerBase = base.toLowerCase()
      const lowerPrefix = prefix.toLowerCase()
      return lowerBase.startsWith(lowerPrefix)
        && (lowerBase.length === lowerPrefix.length || ['_', '-', ' '].includes(lowerBase[lowerPrefix.length]))
    })
    .sort((a, b) => b.prefix.length - a.prefix.length)[0]
  if (knownClassPrefix) return knownClassPrefix.subject

  const subjectMatches = subjects.filter(
    (subject) => normalizedBase === normalize(subject.subjectName)
  )
  if (subjectMatches.length === 1) return subjectMatches[0]

  const separator = base.includes('_') ? '_' : '-'
  const parts = base.split(separator)
  if (parts.length === 2 && parts.every((part) => part.trim())) {
    return { subjectName: parts[0].trim(), classSection: parts[1].trim() }
  }

  return null
}

// Turns the text of a CSV file into students like { id, firstName, lastName }.
// Returns { students } or { error }.
function parseCsv(text) {
  const lines = text.replace(/^\uFEFF/, '').split(/\r?\n/).filter((line) => line.trim() !== '')
  if (lines.length < 2) return { error: 'This file has no rows to import.' }

  const delimiter = findDelimiter(lines[0])
  const rawHeaders = splitLine(lines[0], delimiter)
  const headers = rawHeaders.map(normalizeHeader)

  const id = headers.findIndex((h) =>
    ['studentid', 'stundentid', 'id', 'studentno', 'studentnumber', 'idnumber'].includes(h)
  )
  const firstName = headers.findIndex((h) =>
    ['firstname', 'studentfirstname', 'givenname', 'fname'].includes(h)
  )
  const lastName = headers.findIndex((h) =>
    ['lastname', 'studentlastname', 'surname', 'familyname', 'lname'].includes(h)
  )

  if ([id, firstName, lastName].includes(-1)) {
    return { error: `Your CSV needs these columns: ${expectedColumns.join(', ')}.` }
  }

  const students = lines
    .slice(1)
    .map((line) => {
      const cells = splitLine(line, delimiter)
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
  const [result, setResult] = useState(null) // { label, created, imported, skipped, failed }
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

    const target = getClassFromFileName(file.name, data.subjects ?? [])
      ?? (data.selected
        ? { subjectName: data.selected.subjectName, classSection: data.selected.classSection }
        : null)
    if (!target?.subjectName || !target?.classSection) {
      setError('Choose a class or name the CSV Subject_Section.csv, for example AppDev_G6.csv.')
      return
    }

    setBusy(true)
    setError('')
    try {
      const parsed = parseCsv(await file.text())
      if (parsed.error) {
        setError(parsed.error)
        return
      }

      const outcome = await data.importClassList({ ...target, students: parsed.students })
      if (outcome.imported === 0 && outcome.failed.length > 0) {
        setError(`No students were imported into ${outcome.label}. Could not import IDs: ${outcome.failed.join(', ')}`)
      } else if (outcome.imported === 0 && !outcome.created) {
        setError(`All of these students are already in ${outcome.label}.`)
      } else {
        setResult(outcome)
        setStep(2)
      }
    } catch (err) {
      setError(err.message || 'The CSV import failed. Please try again.')
    } finally {
      setBusy(false)
    }
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
            <p>
              Drag and drop a CSV file here, or choose one from your computer.
              {' '}The filename can set the class; otherwise the selected class is used:
              {' '}<strong>{data.classLabel}</strong>
            </p>

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
          <h2>{result.imported} Student {result.imported === 1 ? 'row is' : 'rows are'} ready</h2>
          <p>
            {result.created ? `New class ${result.label} was created.` : `Added to ${result.label}.`}
            {' '}Your class analytics and learning gaps have been updated.
            {result.skipped > 0 &&
              ` ${result.skipped} ${result.skipped === 1 ? 'student was' : 'students were'} already in this class list and skipped.`}
            {result.failed.length > 0 &&
              ` ${result.failed.length} could not be imported. IDs: ${result.failed.join(', ')}.`}
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