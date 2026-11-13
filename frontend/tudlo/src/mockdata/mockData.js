// Fake data copied from the Figma screens.
// Later, this is the part we replace with real data.

export const topics = [
  'UI Design',
  'Programming Basics',
  'Databases',
  'Web Development',
  'Algorithms',
]

export const outcomes = [
  'LO1 · Apply design principles',
  'LO2 · Use control structures',
  'LO3 · Design relational schemas',
  'LO4 · Create responsive interfaces',
  'LO5 · Evaluate algorithm efficiency',
]

// Dashboard
export const stats = {
  classAverage: '76.4%',
  passRate: '83%',
  passDetail: '35 of 42',
  completion: '96%',
  completionDetail: '188 of 195',
  needSupport: 4,
}

export const performance = [
  { name: 'Quiz 1', percent: 68 },
  { name: 'Quiz 2', percent: 74 },
  { name: 'Midterm', percent: 71 },
  { name: 'Milestone 1', percent: 79 },
]

export const needsSupport = [
  { name: 'Maria Santos', detail: 'Programming Basics · 48%', tag: 'Below Threshold' },
  { name: 'Carlo Mendoza', detail: 'Project Milestone 1', tag: 'Overdue' },
  { name: 'Joshua Dela Cruz', detail: 'Algorithms · 54%', tag: 'Below Threshold' },
  { name: 'Andrei Castillo', detail: 'Quiz 2', tag: 'Overdue' },
]

// Import Data (step 2 table)
export const importSummary = { total: 195, invalid: 7, fileName: 'scores_quiz2_csit321.csv' }

export const validationRows = [
  { student: 'Maria Santos', assessment: 'Quiz 2', score: '18', maxScore: '20', error: '' },
  { student: 'Joshua Dela Cruz', assessment: 'Quiz 2', score: '0', maxScore: '20', error: '' },
  { student: 'Carlo Mendoza', assessment: 'Quiz 2', score: '-', maxScore: '20', error: 'Missing Score' },
  { student: 'Angelica Reyes', assessment: 'Quiz 2', score: '22', maxScore: '20', error: 'Score Exceeds Maximum' },
  { student: 'Jasmine Garcia', assessment: 'Quiz 2', score: '17', maxScore: '20', error: '' },
  { student: 'Miguel Bautista', assessment: 'Quiz 2', score: 'sixteen', maxScore: '20', error: 'Score Must Be A Number' },
]

// Topic Mapping (first two start mapped, the rest are empty)
export const startingItems = [
  { name: 'Quiz 1 · Q1', hint: 'Identify visual hierarchy', topic: 'UI Design', outcome: 'LO1 · Apply design principles', maxScore: 5 },
  { name: 'Quiz 1 · Q2', hint: 'Write a conditional statement', topic: 'Programming Basics', outcome: 'LO2 · Use control structures', maxScore: 10 },
  { name: 'Quiz 1 · Q3', hint: 'Normalize a data table', topic: '', outcome: '', maxScore: 10 },
  { name: 'Quiz 2 · Q1', hint: 'Build a responsive layout', topic: '', outcome: '', maxScore: 15 },
  { name: 'Midterm · Q4', hint: 'Analyze time complexity', topic: '', outcome: '', maxScore: 20 },
]

// Student pages: My Progress
export const studentProgress = {
  firstName: 'Maria',
  overallAverage: 67,
  assessmentCount: 4,
  pointsSinceLastQuiz: 9,
  latestWin: {
    title: 'Project Milestone 1 was your best result yet.',
    detail: 'You scored 74%, continuing your upward trend after the midterm.',
  },
  scoresOverTime: [
    { name: 'Quiz 1', percent: 62 },
    { name: 'Quiz 2', percent: 58 },
    { name: 'Midterm', percent: 65 },
    { name: 'Milestone 1', percent: 74 },
  ],
  topicScores: [
    { name: 'UI Design', percent: 92 },
    { name: 'Programming Basics', percent: 48 },
    { name: 'Databases', percent: 69 },
    { name: 'Web Development', percent: 74 },
    { name: 'Algorithms', percent: 56 },
  ],
  nextStep: {
    title: 'A good next step',
    detail: 'Spend 20 minutes reviewing loops and conditional logic. Your recent support session already moved this topic up by 20 points.',
  },
}

// Student pages: Topics to Review
export const topicsToReview = {
  heading: 'Recommended focus areas',
  topics: [
    {
      name: 'Programming Basics',
      percent: 48,
      outcome: 'Use loops to repeat a set of instructions',
      tip: 'Practice tracing nested loops step by step.',
      tag: 'High Priority',
      tone: 'danger',
    },
    {
      name: 'Algorithms',
      percent: 56,
      outcome: 'Compare algorithms using time complexity',
      tip: 'Review Big O examples from the midterm.',
      tag: 'Review Next',
      tone: 'warning',
    },
    {
      name: 'Databases',
      percent: 69,
      outcome: 'Normalize relational data structures',
      tip: 'Try one more third normal form exercise.',
      tag: 'Keep Practicing',
      tone: 'neutral',
    },
  ],
  supportSession: {
    label: 'TEACHER SUPPORT',
    title: 'Your support activity',
    date: 'February 14, 2025',
    type: 'Peer Tutoring',
    topic: 'Programming Basics · Loops practice',
    summary: 'Paired guided exercises focused on nested loops and tracing program flow.',
    before: 48,
    after: 68,
    note: 'Improved By 20 Points',
  },
}