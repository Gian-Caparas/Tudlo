import { useEffect, useState } from 'react'
import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { Badge } from '@/components/ui/Badge.jsx'
import { LineChart } from '@/components/ui/LineChart.jsx'
import { TopicBars } from '@/components/ui/TopicBars.jsx'
import { Icon } from '@/components/ui/Icon.jsx'
import { academicService } from '@/services/academicService.js'

export function MyProgressView({ onNavigate }) {
  const [data, setData] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true
    academicService.getOwnProgress()
      .then((progress) => active && setData(progress))
      .catch((requestError) => active && setError(requestError.message))
    return () => { active = false }
  }, [])

  if (error) return <div className="card"><p>{error}</p></div>
  if (!data) return <div className="card" role="status"><p>Loading your progress…</p></div>

  const subjectScores = data.subjects
    .map((subject) => ({ name: subject.subjectName, percent: subject.observedPercentage }))
    .filter((subject) => subject.percent !== null)
  const strongest = subjectScores.reduce(
    (best, subject) => subject.percent > best.percent ? subject : best,
    subjectScores[0])
  const weakest = subjectScores.reduce(
    (lowest, subject) => subject.percent < lowest.percent ? subject : lowest,
    subjectScores[0])
  const overall = data.overallObservedPercentage

  return (
    <div>
      <PageHeader
        eyebrow="MY LEARNING"
        title="Your progress"
        description="See what's going well and where a little more practice can help."
      />

      <section className="student-hero">
        <div>
          <span>OVERALL AVERAGE</span>
          <strong>{overall === null ? '—' : `${overall}%`}</strong>
          <p>Across {data.assessmentCount} graded assessments</p>
        </div>
        <div className="hero-message">
          <span>YOUR LATEST WIN</span>
          <h2>{strongest?.name ?? 'No recorded result yet'}</h2>
          <p>{strongest ? `${strongest.percent}% observed performance` : 'Complete graded assessments to start tracking progress.'}</p>
        </div>
      </section>

      <div className="two-col dashboard-grid">
        <section className="card chart-card">
          <div className="section-head">
            <div>
              <h2>Scores over time</h2>
              <p>Your performance across assessments</p>
            </div>
          </div>
          {subjectScores.length ? (
            <LineChart
              values={subjectScores.map((subject) => subject.percent)}
              labels={subjectScores.map((subject) => subject.name)}
            />
          ) : <p>No graded assessments yet.</p>}
        </section>

        <section className="card chart-card">
          <div className="section-head">
            <div>
              <h2>Performance by topic</h2>
              <p>Your current topic mastery</p>
            </div>
          </div>
          {subjectScores.length ? (
            <TopicBars
              values={subjectScores.map((subject) => subject.percent)}
              labels={subjectScores.map((subject) => subject.name)}
            />
          ) : <p>No subject performance is available yet.</p>}
          <div className="strength-labels">
            {strongest && <span><i className="strong-dot" />Strongest: <b>{strongest.name}</b></span>}
            {weakest && <span><i className="weak-dot" />Focus area: <b>{weakest.name}</b></span>}
          </div>
        </section>
      </div>

            <section className="student-tip">
        <Icon name="book" />
        <div>
          <strong>{weakest ? 'A good next step' : 'Progress is getting started'}</strong>
          <p>{weakest ? `Review ${weakest.name}; it currently has the lowest observed performance.` : 'There are no graded results to prioritize yet.'}</p>
        </div>
        <button onClick={() => onNavigate('Topics to Review')}>Review topics →</button>
      </section>
      
    </div>
  )
}

