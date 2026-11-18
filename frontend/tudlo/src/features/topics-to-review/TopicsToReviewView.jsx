import { useEffect, useState } from 'react'
import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { Badge } from '@/components/ui/Badge.jsx'
import { academicService } from '@/services/academicService.js'

export function TopicsToReviewView() {
  const [progress, setProgress] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true
    academicService.getOwnProgress()
      .then((data) => active && setProgress(data))
      .catch((requestError) => active && setError(requestError.message))
    return () => { active = false }
  }, [])

  if (error) return <div className="card"><p>{error}</p></div>
  if (!progress) return <div className="card" role="status"><p>Loading recommended subjects…</p></div>

  const topics = progress.subjectsToReview

  return (
    <div>
      <PageHeader
        eyebrow="PERSONALIZED REVIEW"
        title="Topics to Review"
        description="Prioritize subjects using actual recorded academic performance."
      />

      {topics.length === 0 ? (
        <section className="card">
          <p>No recorded grades are available yet. Missing and incomplete assessments are not treated as zero.</p>
        </section>
      ) : (
        <section>
          <h2 className="subheading">Recommended focus areas</h2>
          {topics.map((topic, index) => (
            <article className="card review-card" key={topic.subjectId}>
              <div className={`review-rank ${index === 0 ? 'rank-0' : ''}`}>
                {String(index + 1).padStart(2, '0')}
              </div>
              <div>
                <div className="review-title">
                  <h3>{topic.subjectName}</h3>
                  <strong className={index === 0 ? 'danger-text' : ''}>
                    {topic.observedPercentage}%
                  </strong>
                </div>
                <span className="outcome">LOWEST OBSERVED SUBJECT PERFORMANCE</span>
                <p>{topic.gradedAssessmentCount} graded assessment(s); {topic.missingAssessmentCount} missing or incomplete item(s).</p>
                <Badge tone={index === 0 ? 'danger' : 'warning'}>
                  {index === 0 ? 'High Priority' : 'Review Next'}
                </Badge>
              </div>
            </article>
          ))}
        </section>
      )}
    </div>
  )
}
