import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { Badge } from '@/components/ui/Badge.jsx'
import { Avatar } from '@/components/ui/Avatar.jsx'
import { Icon } from '@/components/ui/Icon.jsx'
import { LineChart } from '@/components/ui/LineChart.jsx'

// ---------- helpers: everything on the page is calculated from the scores ----------

const percentOf = (record) => (record.score / record.maxScore) * 100
const average = (numbers) =>
  numbers.length ? numbers.reduce((sum, n) => sum + n, 0) / numbers.length : 0

function getGreeting() {
  const hour = new Date().getHours()
  if (hour < 12) return 'Good morning'
  if (hour < 18) return 'Good afternoon'
  return 'Good evening'
}

// Average percent per assessment, in the order they first appear
function getPerformance(scores) {
  const names = [...new Set(scores.map((r) => r.assessment))]
  return names.map((name) => {
    const graded = scores.filter((r) => r.assessment === name && r.score !== null)
    return { name, percent: Math.round(average(graded.map(percentOf))) }
  })
}

function getStats(scores, performance) {
  const graded = scores.filter((r) => r.score !== null)
  const students = [...new Set(scores.map((r) => r.student))]

  const passed = students.filter((name) => {
    const mine = graded.filter((r) => r.student === name).map(percentOf)
    return average(mine) >= rules.passMark
  }).length

  const last = performance[performance.length - 1]
  const before = performance[performance.length - 2]

  return {
    classAverage: `${average(graded.map(percentOf)).toFixed(1)}%`,
    passRate: `${students.length ? Math.round((passed / students.length) * 100) : 0}%`,
    passDetail: `${passed} of ${students.length}`,
    completion: `${scores.length ? Math.round((graded.length / scores.length) * 100) : 0}%`,
    completionDetail: `${graded.length} of ${scores.length}`,
    change: last && before ? last.percent - before.percent : 0,
  }
}

// Students with a score under the threshold, or with a missing score
function getNeedsSupport(scores) {
  const names = [...new Set(scores.map((r) => r.student))]
  const list = []

  names.forEach((name) => {
    const mine = scores.filter((r) => r.student === name)
    const lowest = mine
      .filter((r) => r.score !== null && percentOf(r) < rules.supportThreshold)
      .sort((a, b) => percentOf(a) - percentOf(b))[0]
    const missing = mine.find((r) => r.score === null)

    if (lowest) {
      list.push({ name, detail: `${lowest.assessment} · ${Math.round(percentOf(lowest))}%`, tag: 'Below Threshold' })
    } else if (missing) {
      list.push({ name, detail: missing.assessment, tag: 'Overdue' })
    }
  })

  return list
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

// ---------- the page ----------

export function DashboardView({ scores, userName, onNavigate }) {
  const performance = getPerformance(scores)
  const stats = getStats(scores, performance)
  const needsSupport = getNeedsSupport(scores)
  const trendingUp = stats.change >= 0
  const lowestTopic = topicAverages.reduce((low, t) => (t.percent < low.percent ? t : low))

  return (
    <div>
      <PageHeader
        eyebrow={classInfo.code}
        title={`${getGreeting()}, ${userName}`}
        description="Here's where your class stands today, and who may need your attention."
      />

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
          note={`Below ${rules.supportThreshold}% or overdue`}
          icon="heart"
          attention
        />
      </section>

      <div className="two-col dashboard-grid">
        <SectionCard
          title="Class performance"
          subtitle="Average score by assessment"
          badge={
            <Badge tone={trendingUp ? 'success' : 'warning'}>
              {trendingUp ? 'Trending Up' : 'Trending Down'}
            </Badge>
          }
          className="chart-card"
        >
          {performance.length > 0 ? (
            <LineChart
              values={performance.map((p) => p.percent)}
              labels={performance.map((p) => p.name)}
            />
          ) : (
            <p>No scores yet. Import a CSV to get started.</p>
          )}
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
            {lowestTopic.name} is the lowest-performing topic across the class at {lowestTopic.percent}%.
            Consider revisiting it before the next assessment.
          </p>
        </div>
        <button onClick={() => onNavigate('Learning Gaps')}>View learning gaps →</button>
      </section>
    </div>
  )
}