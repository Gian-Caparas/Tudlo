export const myLearningMock = {
  studentName: 'Maria',
  classLabel: 'CSIT321 – G6',
  overallAverage: 67,
  topicCount: 5,
  latestWin: {
    assessment: 'Final',
    subject: 'Programming Basics',
    score: 74,
    maxScore: 100,
    note: 'continuing your upward trend after the midterm.',
  },
  topics: [
    { name: 'UI Design', average: 89 },
    { name: 'Programming Basics', average: 46 },
    { name: 'Databases', average: 68 },
    { name: 'Web Development', average: 74 },
    { name: 'Algorithms', average: 57 },
  ],
  topicsToReview: [
    {
      name: 'Programming Basics',
      learningOutcome: 'Use loops to repeat a set of instructions',
      average: 46,
      priority: 'High priority',
    },
    {
      name: 'Algorithms',
      learningOutcome: 'Compare algorithms using time complexity',
      average: 57,
      priority: 'Review next',
    },
  ],
  scoreBreakdown: {
    subject: 'Programming Basics',
    scores: [
      { label: 'Quiz 1', score: 9, maxScore: 20 },
      { label: 'Quiz 2', score: 11, maxScore: 20 },
      { label: 'Prelim', score: 28, maxScore: 50 },
      { label: 'Midterm', score: 31, maxScore: 50 },
      { label: 'Prefinal', score: 35, maxScore: 50 },
      { label: 'Final', score: 74, maxScore: 100 },
    ],
  },
  support: {
    description: 'Peer tutoring in Programming Basics',
    scoreBefore: 46,
    scoreAfter: 68,
  },
}