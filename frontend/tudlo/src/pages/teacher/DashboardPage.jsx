import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { Badge } from '@/components/ui/Badge.jsx'
import { Avatar } from '@/components/ui/Avatar.jsx'
import { Icon } from '@/components/ui/Icon.jsx'
import { LineChart } from '@/components/ui/LineChart.jsx'
import { stats, performance, needsSupport } from '@/mockdata/mockData.js'

// Props: one stat card (destructured props)
function StatCard({ label, value, note, icon, up, attention }) {
  return (
    <div className={`card kpi ${attention ? 'attention' : ''}`}>
      <div className="kpi-top">
        <span>{label}</span>
        <div><Icon name={icon} size={16} /></div>
      </div>
      <strong>{value}</strong>
      <small className={up ? 'up' : ''}>{note}</small>
    </div>
  )
}

// Props + children: a card with a title, and whatever is written inside its tags
function SectionCard({ title, subtitle, badge, className = '', children }) {
  return (
    <section className={`card ${className}`}>
      <div className="section-head">
        <div>
          <h2>{title}</h2>
          <p>{subtitle}</p>
        </div>
        {badge}
      </div>
      {children}
    </section>
  )
}

const tagTone = { 'Below Threshold': 'danger', Overdue: 'warning' }

function SupportRow({ student, onOpen }) {
  return (
    <button onClick={onOpen}>
      <Avatar name={student.name} size="sm" />
      <span>
        <strong>{student.name}</strong>
        <small>{student.detail}</small>
      </span>
      <Badge tone={tagTone[student.tag]}>{student.tag}</Badge>
      <Icon name="chevron" size={14} />
    </button>
  )
}

function DashboardPage({ onNavigate }) {
  return (
    <div>
      <PageHeader
        eyebrow="CSIT321 – G6"
        title="Good morning, Prof. Ramon"
        description="Here's where your class stands today, and who may need your attention."
      />

      <section className="kpi-grid">
        <StatCard label="Class average" value={stats.classAverage} note={`↑ ${stats.trend}`} icon="chart" up />
        <StatCard label="Pass rate" value={stats.passRate} note={stats.passDetail} icon="check" />
        <StatCard label="Submission completion" value={stats.completion} note={stats.completionDetail} icon="file" />
        <StatCard label="Students needing support" value={stats.needSupport} note={stats.needSupportNote} icon="heart" attention />
      </section>

      <div className="two-col dashboard-grid">
        <SectionCard
          title="Class performance"
          subtitle="Average score by assessment"
          badge={<Badge tone="success">Trending Up</Badge>}
          className="chart-card"
        >
          <LineChart
            values={performance.map((p) => p.percent)}
            labels={performance.map((p) => p.name)}
          />
        </SectionCard>

        <SectionCard
          title="Needs support"
          subtitle="Based on your flagging rules"
          badge={<span className="count">{needsSupport.length}</span>}
          className="support-list"
        >
          {needsSupport.map((s) => (
            <SupportRow key={s.name} student={s} onOpen={() => onNavigate('Students')} />
          ))}
        </SectionCard>
      </div>

      <section className="insight-banner">
        <span className="insight-mark">i</span>
        <div>
          <strong>Insight worth noticing</strong>
          <p>
            Programming Basics is the lowest-performing topic across the class at 58%. Consider revisiting
            variables and control flow before the next assessment.
          </p>
        </div>
        <button onClick={() => onNavigate('Learning Gaps')}>View learning gaps →</button>
      </section>
    </div>
  )
}

export default DashboardPage