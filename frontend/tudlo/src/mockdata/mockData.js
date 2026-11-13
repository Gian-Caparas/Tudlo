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