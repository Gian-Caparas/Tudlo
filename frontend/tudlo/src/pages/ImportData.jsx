import { useState } from 'react'
import { PageHeader } from '../components/shared/PageHeader.jsx'
import { Badge } from '../components/ui/Badge.jsx'
import { Icon } from '../components/ui/Icon.jsx'
import { importSummary, validationRows } from '../mockdata/mockData.js'

const stepNames = ['Upload CSV', 'Validate', 'Done']

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

function ValidationRow({ row }) {
  return (
    <tr className={row.error ? 'invalid-row' : 'valid-row'}>
      <td>{row.student}</td>
      <td>{row.assessment}</td>
      <td><strong>{row.score === '-' ? '—' : row.score}</strong></td>
      <td>{row.maxScore}</td>
      <td><Badge tone={row.error ? 'danger' : 'success'}>{row.error || 'Valid'}</Badge></td>
    </tr>
  )
}

function ImportData({ onNavigate }) {
  const [step, setStep] = useState(1) // 1 = upload, 2 = validate, 3 = done
  const validCount = importSummary.total - importSummary.invalid

  return (
    <div>
      <PageHeader
        eyebrow="DATA WORKFLOW"
        title="Import assessment data"
        description="Bring scores into Tudlo, validate them, and correct issues before analysis."
      />

      <Stepper step={step} />

      {step === 1 && (
        <section className="card upload-card">
          <div className="upload-zone">
            <div><Icon name="upload" size={22} /></div>
            <h2>Upload your score sheet</h2>
            <p>Drag and drop a CSV file here, or choose one from your computer.</p>
            {/* Later: a real file picker goes here. For now the button just moves on. */}
            <button className="btn secondary" onClick={() => setStep(2)}>Choose CSV file</button>
            <small>CSV only · Maximum 10 MB</small>
          </div>
          <div className="file-hint">
            <strong>Expected columns</strong>
            <span>Student name</span>
            <span>Assessment</span>
            <span>Score</span>
            <span>Max score</span>
          </div>
        </section>
      )}

      {step === 2 && (
        <>
          <div className="summary-banner">
            <Icon name="alert" size={16} />
            <div>
              <strong>
                {validCount} of {importSummary.total} rows imported (96%). {importSummary.invalid} need correction.
              </strong>
              <span>Fix the highlighted rows in your CSV, then upload again—or continue with valid rows only.</span>
            </div>
            <button className="btn primary" onClick={() => setStep(3)}>Import valid rows</button>
          </div>

          <section className="card">
            <div className="section-head">
              <div>
                <h2>Validation results</h2>
                <p>{importSummary.fileName} · {importSummary.total} rows</p>
              </div>
              <div className="legend">
                <span><i className="green" />Valid</span>
                <span><i className="red" />Needs correction</span>
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
                    <th>Validation</th>
                  </tr>
                </thead>
                <tbody>
                  {validationRows.map((row) => (
                    <ValidationRow key={row.student} row={row} />
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
          <h2>{validCount} score rows are ready</h2>
          <p>
            Your class analytics and learning gaps have been updated. {importSummary.invalid} invalid rows were
            skipped and can be imported later.
          </p>
          <div>
            <button className="btn ghost" onClick={() => setStep(1)}>Import another file</button>
            <button className="btn primary" onClick={() => onNavigate('Learning Gaps')}>View learning gaps</button>
          </div>
        </section>
      )}
    </div>
  )
}

export default ImportData