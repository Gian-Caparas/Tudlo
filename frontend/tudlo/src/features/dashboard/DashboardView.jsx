import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { DataGate } from '@/components/shared/DataGate.jsx'
import { Badge } from '@/components/ui/Badge.jsx'
import { Avatar } from '@/components/ui/Avatar.jsx'
import { Icon } from '@/components/ui/Icon.jsx'
import { LineChart } from '@/components/ui/LineChart.jsx'
import { rules, average, getPercent, getNeedsSupport } from '@/utils/scoring.js'

// ---------- helpers: everything on the page is calculated from the backend data ----------

function getGreeting() {
  const hour = new Date().getHours()
  if (hour < 12) return 'Good morning'
  if (hour < 18) return 'Good afternoon'
  return 'Good evening'
}

// Class average % of each assessment item, in the order of Subject Mapping
function getPerformance(items, scores) {
  return items
    .map((item) => {
      const percents = scores
        .filter((r) => r.assessment === item.name)
        .map((r) => getPercent(r, items))
        .filter((p) => p !== null)
      return { name: item.name, outcome: item.outcome, percent: Math.round(average(percents)), count: percents.length }
    })
    .filter((row) => row.count > 0)
}

function getStats(items, scores, students, performance) {
  const percentsOf = (studentId) =>
    scores
      .filter((r) => r.studentId === studentId)
      .map((r) => getPercent(r, items))
      .filter((p) => p !== null)

  const allPercents = scores.map((r) => getPercent(r, items)).filter((p) => p !== null)
  const scoredStudents = students.filter((s) => percentsOf(s.id).length > 0)
  const passed = scoredStudents.filter((s) => average(percentsOf(s.id)) >= rules.passMark).length
  const expected = students.length * items.length // every student should have a score for every item

  const last = performance[performance.length - 1]
  const before = performance[performance.length - 2]

  return {
    classAverage: allPercents.length ? `${average(allPercents).toFixed(1)}%` : '—',
    passRate: scoredStudents.length ? `${Math.round((passed / scoredStudents.length) * 100)}%` : '—',
    passDetail: `${passed} of ${scoredStudents.length}`,
    completion: expected ? `${Math.round((allPercents.length / expected) * 100)}%` : '—',
    completionDetail: `${allPercents.length} of ${expected}`,
    change: last && before ? last.percent - before.percent : 0,
  }
}

// ---------- small components ----------

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

const tagTone = { 'Below 60%': 'danger', 'Below Threshold': 'danger', Overdue: 'warning' }

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

// ---------- the page ----------

function DashboardBody({ data, onNavigate }) {
  const { items, scores, students } = data
  const performance = getPerformance(items, scores)
  const stats = getStats(items, scores, students, performance)
  const needsSupport = getNeedsSupport(scores, students, items)
  const lowest = performance.reduce((low, row) => (low === null || row.percent < low.percent ? row : low), null)
  const trendingUp = stats.change >= 0

  return (
    <>
      <section className="kpi-grid">
        <StatCard
          label="Class average"
          value={stats.classAverage}
          note={`${trendingUp ? '↑ +' : '↓ '}${stats.change}%`}
          icon="chart"
          up={trendingUp}
        />
        <StatCard label="Pass rate" value={stats.passRate} note={stats.passDetail} icon="check" />
        <StatCard label="Submission completion" value={stats.completion} note={stats.completionDetail} icon="file" />
        <StatCard
          label="Students needing support"
          value={needsSupport.length}
          note={`Below ${rules.supportThreshold}%`}
          icon="heart"
          attention
        />
      </section>

      <div className="two-col dashboard-grid">
        <SectionCard
          title="Class performance"
          subtitle="Average score by assessment"
          badge={
            performance.length > 1 && (
              <Badge tone={trendingUp ? 'success' : 'warning'}>
                {trendingUp ? 'Trending Up' : 'Trending Down'}
              </Badge>
            )
          }
          className="chart-card"
        >
          {performance.length > 0 ? (
            <LineChart
              values={performance.map((p) => p.percent)}
              labels={performance.map((p) => p.name)}
            />
          ) : (
            <p>No scores yet for this subject.</p>
          )}
        </SectionCard>

        <SectionCard
          title="Needs support"
          subtitle="Based on your flagging rules"
          badge={<span className="count">{needsSupport.length}</span>}
          className="support-list"
        >
          {needsSupport.map((s) => (
            <SupportRow key={s.id} student={s} onOpen={() => onNavigate('Students')} />
          ))}
        </SectionCard>
      </div>

      {lowest && (
        <section className="insight-banner">
          <span className="insight-mark">i</span>
          <div>
            <strong>Insight worth noticing</strong>
            <p>
              {lowest.name} is the lowest-scoring assessment in {data.classLabel} at {lowest.percent}%.
              {lowest.outcome && ` Consider revisiting: ${lowest.outcome}`}
            </p>
          </div>
          <button onClick={() => onNavigate('Learning Gaps')}>View learning gaps →</button>
        </section>
      )}
    </>
  )
}

export function DashboardView({ data, userName, onNavigate }) {
  return (
    <div>
      <PageHeader
        eyebrow={data.classLabel}
        title={`${getGreeting()}, ${userName}`}
        description="Here's where your class stands today, and who may need your attention."
      />
      <DataGate data={data} onNavigate={onNavigate}>
        <DashboardBody data={data} onNavigate={onNavigate} />
      </DataGate>
    </div>
  )
}