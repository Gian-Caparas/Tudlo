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

export const rules = {
  passMark: 60,          // a student passes with an average of 60% or more
  supportThreshold: 60,  // below 60% is red in Students and flags the student on the Dashboard
  goodThreshold: 80,     // 80% and above is green in Students
}

// Settings used by Import Class List
export const importSettings = {
  stepNames: ['Upload CSV', 'Done'],
  expectedColumns: ['Student ID', 'Student First Name', 'Student Last Name'],
  expectedNaming: ['File Name: SubjectName', 'Sheet Name: SectionName'],
  maxFileSize: '10 MB',
}

export const subjects = ['Programming Basics', 'UI Design', 'Databases', 'Web Development', 'Algorithms']

export const assessmentNames = ['Quiz 1', 'Quiz 2', 'Prelim', 'Midterm', 'Prefinal', 'Final']
const defaultMaxScores = [20, 20, 50, 50, 50, 100]

function makeItems(outcomes = []) {
  return assessmentNames.map((name, i) => ({
    name,
    outcome: outcomes[i] ?? '',
    maxScore: defaultMaxScores[i],
  }))
}

// Subject Mapping: the assessment items, learning outcomes and max scores of each subject
export const startingMapping = {
  'Programming Basics': makeItems([
    'Use variables and data types in simple programs.',
    'Apply conditional statements and loops.',
    'Combine functions, conditionals, and loops.',
    'Process arrays and strings to solve problems.',
    'Debug programs and evaluate basic algorithms.',
    'Build a complete program using core concepts.',
  ]),
  'UI Design': makeItems(),
  Databases: makeItems(),
  'Web Development': makeItems(),
  Algorithms: makeItems(),
}

// The class list (this is what Import Class List adds to)
export const startingStudents = [
  { id: '2024-0001', firstName: 'Maria', lastName: 'Santos' },
  { id: '2024-0002', firstName: 'Joshua', lastName: 'Dela Cruz' },
  { id: '2024-0003', firstName: 'Angelica', lastName: 'Reyes' },
  { id: '2024-0004', firstName: 'Carlo', lastName: 'Mendoza' },
  { id: '2024-0005', firstName: 'Jasmine', lastName: 'Garcia' },
  { id: '2024-0006', firstName: 'Miguel', lastName: 'Bautista' },
]

// One record per score. score: null means not submitted.
function makeScores(subject, studentId, values) {
  return assessmentNames.map((assessment, i) => ({
    subject,
    studentId,
    assessment,
    score: values[i],
  }))
}

export const startingScores = [
  ...makeScores('Programming Basics', '2024-0001', [9, 11, 28, 31, 35, 74]),
  ...makeScores('Programming Basics', '2024-0002', [11, 11, 32, 34, 37, 78]),
  ...makeScores('Programming Basics', '2024-0003', [17, 18, 43, 44, 46, 94]),
  ...makeScores('Programming Basics', '2024-0004', [8, 10, 25, 27, 31, 66]),
  ...makeScores('Programming Basics', '2024-0005', [16, 17, 41, 43, 44, 90]),
  ...makeScores('Programming Basics', '2024-0006', [12, 13, 33, 35, 38, 80]),
]
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