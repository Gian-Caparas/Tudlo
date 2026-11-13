import { PageHeader } from '../components/shared/PageHeader.jsx'
import { Badge } from '../components/ui/Badge.jsx'
import { topicsToReview } from '../mockdata/mockData.js'

function TopicsToReview() {
  const { heading, topics, supportSession: s } = topicsToReview

  return (
    <div>
      <PageHeader
        eyebrow="PERSONALIZED REVIEW"
        title="Topics to Review"
        description="Focus your study time on the skills that can make the biggest difference."
      />

      <div className="review-layout">
        {/* Left: ranked review cards */}
        <section>
          <h2 className="subheading">{heading}</h2>

          {topics.map((t, i) => (
            <article className="card review-card" key={t.name}>
              <div className={`review-rank ${i === 0 ? 'rank-0' : ''}`}>
                {String(i + 1).padStart(2, '0')}
              </div>
              <div>
                <div className="review-title">
                  <h3>{t.name}</h3>
                  <strong className={i === 0 ? 'danger-text' : ''}>{t.percent}%</strong>
                </div>
                <span className="outcome">LEARNING OUTCOME</span>
                <p className="outcome-text">{t.outcome}</p>
                <p>{t.tip}</p>
                <Badge tone={t.tone}>{t.tag}</Badge>
              </div>
            </article>
          ))}
        </section>

        {/* Right: support session card */}
        <aside className="card support-summary">
          <div className="eyebrow">{s.label}</div>
          <h2>{s.title}</h2>
          <div className="support-date">
            <i />
            {s.date}
          </div>
          <Badge tone="success">{s.type}</Badge>
          <h3>{s.topic}</h3>
          <p>{s.summary}</p>

          <div className="big-compare">
            <span><small>Before</small>{s.before}%</span>
            <i>→</i>
            <span><small>After</small>{s.after}%</span>
          </div>

          <Badge tone="success">{s.note}</Badge>
        </aside>
      </div>
    </div>
  )
}

export default TopicsToReview