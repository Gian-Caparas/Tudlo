import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { Badge } from '@/components/ui/Badge.jsx'
import { Avatar } from '@/components/ui/Avatar.jsx'
import { Icon } from '@/components/ui/Icon.jsx'
import { LineChart } from '@/components/ui/LineChart.jsx'
import { classInfo, rules, subjects, assessmentNames } from '@/mockdata/mockData.js'
import { average, getPercent, getNeedsSupport } from '@/utils/scoring.js'

// ---------- helpers: everything on the page is calculated from the data ----------

function getGreeting() {
  const hour = new Date().getHours()
  if (hour < 12) return 'Good morning'
  if (hour < 18) return 'Good afternoon'
  return 'Good evening'
}

// Class average % for each assessment (all subjects together). Only assessments that have scores.
function getPerformance(scores, mapping) {
  return assessmentNames
    .map((name) => {
      const percents = scores
        .filter((r) => r.assessment === name)
        .map((r) => getPercent(r, mapping))
        .filter((p) => p !== null)
      return { name, percent: Math.round(average(percents)), count: percents.length }
    })
    .filter((item) => item.count > 0)
}

function getStats(scores, students, mapping, performance) {
  const percentsOf = (studentId) =>
    scores
      .filter((r) => r.studentId === studentId)
      .map((r) => getPercent(r, mapping))
      .filter((p) => p !== null)

  const allPercents = scores.map((r) => getPercent(r, mapping)).filter((p) => p !== null)
  const scoredStudents = students.filter((s) => percentsOf(s.id).length > 0)
  const passed = scoredStudents.filter((s) => average(percentsOf(s.id)) >= rules.passMark).length

  // every student should have a score for every assessment of every subject that has scores
  const subjectsWithScores = new Set(scores.map((r) => r.subject)).size
  const expected = students.length * subjectsWithScores * assessmentNames.length

  const last = performance[performance.length - 1]
  const before = performance[performance.length - 2]

  return {
    classAverage: `${average(allPercents).toFixed(1)}%`,
    passRate: `${scoredStudents.length ? Math.round((passed / scoredStudents.length) * 100) : 0}%`,
    passDetail: `${passed} of ${scoredStudents.length}`,
    completion: `${expected ? Math.round((allPercents.length / expected) * 100) : 0}%`,
    completionDetail: `${allPercents.length} of ${expected}`,
    change: last && before ? last.percent - before.percent : 0,
  }
}

// The subject with the lowest class average
function getLowestSubject(scores, mapping) {
  let lowest = null
  subjects.forEach((name) => {
    const percents = scores
      .filter((r) => r.subject === name)
      .map((r) => getPercent(r, mapping))
      .filter((p) => p !== null)
    if (percents.length === 0) return
    const percent = Math.round(average(percents))
    if (lowest === null || percent < lowest.percent) lowest = { name, percent }
  })
  return lowest
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

export function DashboardView({ scores, students, mapping, userName, onNavigate }) {
  const performance = getPerformance(scores, mapping)
  const stats = getStats(scores, students, mapping, performance)
  const needsSupport = getNeedsSupport(scores, students, mapping)
  const lowestSubject = getLowestSubject(scores, mapping)
  const trendingUp = stats.change >= 0

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
            <p>No scores yet.</p>
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

      {lowestSubject && (
        <section className="insight-banner">
          <span className="insight-mark">i</span>
          <div>
            <strong>Insight worth noticing</strong>
            <p>
              {lowestSubject.name} is the lowest-performing subject across the class at {lowestSubject.percent}%.
              Consider revisiting it before the next assessment.
            </p>
          </div>
          <button onClick={() => onNavigate('Learning Gaps')}>View learning gaps →</button>
        </section>
      )}
    </div>
  )
}