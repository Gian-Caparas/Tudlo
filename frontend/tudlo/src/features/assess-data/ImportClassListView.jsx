import { useState, useRef } from 'react'
import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { DataGate } from '@/components/shared/DataGate.jsx'
import { Icon } from '@/components/ui/Icon.jsx'

const stepNames = ['Upload Excel', 'Done']
const expectedColumns = ['User ID Number', 'Firstname', 'Lastname']
const expectedNaming = ['File Name: <Subject>.xlsx (e.g., Mathematics.xlsx)', 'Each sheet name = Section (e.g., G6A, G6B)']
const maxFileSize = '10 MB'

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
  const [result, setResult] = useState(null)
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  const fileInput = useRef(null)

  // Reads the chosen file, sends to backend, and moves to the last step
  async function readFile(file) {
    if (!file) return
    const name = file.name.toLowerCase()
    if (!name.endsWith('.xlsx') && !name.endsWith('.xls')) {
      setError('Please choose an Excel file (.xlsx or .xls).')
      return
    }

    setBusy(true)
    setError('')
    try {
      // Backend extracts subject from filename, section from sheet names
      const importFn = data.importClassListExcel ?? data.importClassList
      const outcome = await importFn(file)
      const importedCount = outcome.importedCount ?? outcome.imported ?? 0
      const offerings = outcome.offerings ?? []
      if (importedCount === 0 && offerings.length === 0) {
        setError('No students were imported. Check that your Excel has data rows with User ID Number, Firstname, Lastname columns.')
      } else {
        setResult(outcome)
        setStep(2)
      }
    } catch (err) {
      setError(err.message || 'The Excel import failed. Please try again.')
    } finally {
      setBusy(false)
    }
  }

  function handleFileChange(e) {
    readFile(e.target.files[0])
    e.target.value = ''
  }

  function handleDrop(e) {
    e.preventDefault()
    readFile(e.dataTransfer.files[0])
  }

  function handleReset() {
    setResult(null)
    setError('')
    setStep(1)
  }

  // Format result for display
  const formatResult = (result) => {
    if (!result) return null
    const offerings = result.offerings ?? []
    const labels = offerings.length > 0
      ? offerings.map(o => `${o.subjectName} - ${o.classSection}`).join(', ')
      : (result.label ?? '')
    return {
      imported: result.importedCount ?? result.imported ?? 0,
      created: result.createdAccountsCount ?? (result.created ? result.imported : 0),
      skipped: result.skippedDuplicatesCount ?? result.skipped ?? 0,
      label: labels,
    }
  }

  const formatted = formatResult(result)

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
            <h2>Upload Class List Excel</h2>
            <p>
              Drag and drop an Excel file (.xlsx/.xls) here, or choose one from your computer.
              {' '}The filename sets the Subject; each sheet name becomes a Section.
            </p>

            <input
              ref={fileInput}
              type="file"
              accept=".xlsx,.xls"
              onChange={handleFileChange}
              hidden
            />
            <button
              className="btn secondary"
              type="button"
              disabled={busy}
              onClick={() => fileInput.current.click()}
            >
              {busy ? 'Importing…' : 'Choose Excel file'}
            </button>

            <small>Excel only (.xlsx, .xls) · Maximum {maxFileSize}</small>
            {error && <small role="alert" style={{ color: '#b42318' }}>{error}</small>}
          </div>
          <HintRow label="Expected columns per sheet" items={expectedColumns} />
          <HintRow label="Expected Naming" items={expectedNaming} />
        </section>
      )}

      {step === 2 && formatted && (
        <section className="card done-card">
          <div className="done-check"><Icon name="check" size={28} /></div>
          <p className="eyebrow">IMPORT COMPLETE</p>
          <h2>{formatted.imported} Student {formatted.imported === 1 ? 'row is' : 'rows are'} ready</h2>
          <p>
            {formatted.created > 0 ? `${formatted.created} new student account${formatted.created === 1 ? '' : 's'} created with default password. ` : ''}
            Added to {formatted.label}.
            {' '}Your class analytics and learning gaps have been updated.
            {formatted.skipped > 0 &&
              ` ${formatted.skipped} ${formatted.skipped === 1 ? 'student was' : 'students were'} already enrolled and skipped.`}
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
        description="Upload an Excel workbook: filename = Subject, sheet names = Sections, columns = User ID Number, Firstname, Lastname."
      />
      <DataGate data={data} allowEmpty>
        <ImportBody data={data} onNavigate={onNavigate} />
      </DataGate>
    </div>
  )
}