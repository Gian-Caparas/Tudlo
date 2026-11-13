import { useState } from 'react'
import { Link } from 'react-router-dom'
import { importSummary, validationRows } from '../mockdata/mockData.js'

function ImportData() {
  const [step, setStep] = useState(1) // 1 = upload, 2 = validate, 3 = done
  const validCount = importSummary.total - importSummary.invalid

  return (
    <div>
      <p className="text-sm">DATA WORKFLOW</p>
      <h2 className="text-2xl font-bold">Import assessment data</h2>
      <p className="mb-4">Bring scores into Tudlo, validate them, and correct issues before analysis.</p>

      {/* Step indicator */}
      <p className="mb-4">
        <span className={step === 1 ? 'font-bold' : ''}>1. Upload CSV</span>
        {' > '}
        <span className={step === 2 ? 'font-bold' : ''}>2. Validate</span>
        {' > '}
        <span className={step === 3 ? 'font-bold' : ''}>3. Done</span>
      </p>

      {/* STEP 1 */}
      {step === 1 && (
        <div className="max-w-xl border border-dashed p-6 text-center">
          <p className="font-bold">Upload your score sheet</p>
          <p className="mb-3 text-sm">Drag and drop a CSV file here, or choose one from your computer.</p>
          {/* Later: a real file picker goes here. For now the button just moves on. */}
          <button onClick={() => setStep(2)} className="border px-3 py-1">
            Choose CSV file
          </button>
          <p className="mt-3 text-xs">CSV only · Maximum 10 MB</p>
          <p className="mt-2 text-xs">Expected columns: Student name, Assessment, Score, Max score</p>
        </div>
      )}

      {/* STEP 2 */}
      {step === 2 && (
        <div>
          <div className="mb-4 max-w-3xl border p-3">
            <p className="font-bold">
              {validCount} of {importSummary.total} rows imported. {importSummary.invalid} need correction.
            </p>
            <p className="mb-2 text-sm">
              Fix the highlighted rows in your CSV, then upload again, or continue with valid rows only.
            </p>
            <button onClick={() => setStep(3)} className="mr-3 border px-3 py-1">
              Import valid rows
            </button>
            <button onClick={() => setStep(1)} className="border px-3 py-1">
              Upload again
            </button>
          </div>

          <h3 className="font-bold">Validation results</h3>
          <p className="mb-2 text-sm">{importSummary.fileName} · {importSummary.total} rows</p>
          <table className="w-full max-w-3xl text-left text-sm">
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
                <tr key={row.student} className={row.error ? 'bg-red-100' : ''}>
                  <td>{row.student}</td>
                  <td>{row.assessment}</td>
                  <td>{row.score}</td>
                  <td>{row.maxScore}</td>
                  <td>{row.error || 'Valid'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* STEP 3 */}
      {step === 3 && (
        <div className="max-w-xl border p-6 text-center">
          <p className="text-sm">IMPORT COMPLETE</p>
          <p className="text-xl font-bold">{validCount} score rows are ready</p>
          <p className="mb-3">
            Your class analytics and learning gaps have been updated. {importSummary.invalid} invalid rows were
            skipped and can be imported later.
          </p>
          <button onClick={() => setStep(1)} className="mr-3 border px-3 py-1">
            Import another file
          </button>
          <Link to="/gaps" className="border px-3 py-1">
            View learning gaps
          </Link>
        </div>
      )}
    </div>
  )
}

export default ImportData