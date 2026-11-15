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

// ---------- Teacher pages ----------

export const classInfo = { code: 'CSIT321 – G6' }

// Rules used by the Dashboard
export const rules = {
  passMark: 70,          // a student passes with an average of 70% or more
  supportThreshold: 60,  // any score under 60% flags the student
}

// Settings used by Import Data
export const importSettings = {
  stepNames: ['Upload CSV', 'Validate', 'Done'],
  expectedColumns: ['Student name', 'Assessment', 'Score', 'Max score'],
  maxFileSize: '10 MB',
}

// Score records. score: null means the student has not submitted yet.
function makeScores(student, [q1, q2, mid, m1]) {
  return [
    { student, assessment: 'Quiz 1', score: q1, maxScore: 20 },
    { student, assessment: 'Quiz 2', score: q2, maxScore: 20 },
    { student, assessment: 'Midterm', score: mid, maxScore: 50 },
    { student, assessment: 'Milestone 1', score: m1, maxScore: 100 },
  ]
}

export const seedScores = [
  ...makeScores('Maria Santos', [12, 10, 33, 74]),
  ...makeScores('Carlo Mendoza', [15, 14, 36, null]),
  ...makeScores('Joshua Dela Cruz', [14, 15, 27, 70]),
  ...makeScores('Andrei Castillo', [16, null, 38, 80]),
  ...makeScores('Angelica Reyes', [15, 17, 37, 82]),
  ...makeScores('Jasmine Garcia', [16, 17, 38, 85]),
  ...makeScores('Miguel Bautista', [14, 16, 35, 78]),
  ...makeScores('Sofia Ramos', [18, 18, 42, 90]),
  ...makeScores('Paolo Navarro', [14, 14, 34, 76]),
  ...makeScores('Elena Cruz', [17, 16, 40, 84]),
  ...makeScores('Diego Torres', [15, 15, 36, 79]),
  ...makeScores('Bianca Lim', [16, 18, 41, 88]),
]

// Same list under the name your App imports.
export const dashboardScores = seedScores

// Fixed for now. Real topic averages need the Learning Gaps page.
export const topicAverages = [
  { name: 'UI Design', percent: 82 },
  { name: 'Programming Basics', percent: 58 },
  { name: 'Databases', percent: 71 },
  { name: 'Web Development', percent: 76 },
  { name: 'Algorithms', percent: 64 },
]

// Topic Mapping
export const startingItems = [
  { name: 'Quiz 1 · Q1', hint: 'Identify visual hierarchy', topic: 'UI Design', outcome: 'LO1 · Apply design principles', maxScore: 5 },
  { name: 'Quiz 1 · Q2', hint: 'Write a conditional statement', topic: 'Programming Basics', outcome: 'LO2 · Use control structures', maxScore: 10 },
  { name: 'Quiz 1 · Q3', hint: 'Normalize a data table', topic: 'Databases', outcome: 'LO3 · Design relational schemas', maxScore: 10 },
  { name: 'Quiz 2 · Q1', hint: 'Build a responsive layout', topic: 'Web Development', outcome: 'LO4 · Create responsive interfaces', maxScore: 15 },
  { name: 'Midterm · Q4', hint: 'Analyze time complexity', topic: 'Algorithms', outcome: 'LO5 · Evaluate algorithm efficiency', maxScore: 20 },
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

// Student pages: My Submissions
export const mySubmissions = {
  description: 'Track your assessment scores and submission status.',
  rows: [
    { name: 'Quiz 1', due: 'Jan 22, 2025', score: 62, status: 'On Time', tone: 'success' },
    { name: 'Quiz 2', due: 'Feb 05, 2025', score: 58, status: 'Late', tone: 'warning' },
    { name: 'Midterm', due: 'Feb 19, 2025', score: 65, status: 'On Time', tone: 'success' },
    { name: 'Project Milestone 1', due: 'Mar 03, 2025', score: 74, status: 'On Time', tone: 'success' },
    { name: 'Project Milestone 2', due: 'Mar 21, 2025', score: null, status: 'Overdue', tone: 'danger' },
    { name: 'Final Project', due: 'Apr 12, 2025', score: null, status: 'Missing', tone: 'danger' },
  ],
}