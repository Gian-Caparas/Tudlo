import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { Badge } from '@/components/ui/Badge.jsx'
import { LineChart } from '@/components/ui/LineChart.jsx'
import { TopicBars } from '@/components/ui/TopicBars.jsx'
import { studentProgress } from '@/mockdata/mockData.js'
import { Icon } from '@/components/ui/Icon.jsx'

function MyProgressPage({ onNavigate }) {
  const data = studentProgress

  // Find the highest and lowest topic scores
  const strongest = data.topicScores.reduce((a, b) => (b.percent > a.percent ? b : a))
  const weakest = data.topicScores.reduce((a, b) => (b.percent < a.percent ? b : a))

  return (
    <div>
      <PageHeader
        eyebrow="MY LEARNING"
        title={`Your progress, ${data.firstName}`}
        description="See what's going well and where a little more practice can help."
      />

      <section className="student-hero">
        <div>
          <span>OVERALL AVERAGE</span>
          <strong>{data.overallAverage}%</strong>
          <p>Across {data.assessmentCount} assessments</p>
          <Badge tone="success">↑ {data.pointsSinceLastQuiz} points since Quiz 2</Badge>
        </div>
        <div className="hero-message">
          <span>YOUR LATEST WIN</span>
          <h2>{data.latestWin.title}</h2>
          <p>{data.latestWin.detail}</p>
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
          <LineChart
            values={data.scoresOverTime.map((s) => s.percent)}
            labels={data.scoresOverTime.map((s) => s.name)}
          />
        </section>

        <section className="card chart-card">
          <div className="section-head">
            <div>
              <h2>Performance by topic</h2>
              <p>Your current topic mastery</p>
            </div>
          </div>
          <TopicBars
            values={data.topicScores.map((t) => t.percent)}
            labels={data.topicScores.map((t) => t.name)}
          />
          <div className="strength-labels">
            <span><i className="strong-dot" />Strongest: <b>{strongest.name}</b></span>
            <span><i className="weak-dot" />Focus area: <b>{weakest.name}</b></span>
          </div>
        </section>
      </div>

            <section className="student-tip">
        <Icon name="book" />
        <div>
          <strong>{data.nextStep.title}</strong>
          <p>{data.nextStep.detail}</p>
        </div>
        <button onClick={() => onNavigate('Topics to Review')}>Review topics →</button>
      </section>
      
    </div>
  )
}

export default MyProgressPage