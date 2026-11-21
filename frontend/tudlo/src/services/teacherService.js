import { apiRequest } from '@/lib/apiClient.js'

const encode = encodeURIComponent

// The backend keeps class lists, assessments and activities per subject and section
const forSubject = (subject) =>
  `/subject/${encode(subject.subjectName)}/section/${encode(subject.classSection)}`

export const teacherService = {
  // Subjects (with their section) that this teacher teaches
  getSubjects(teacherId) {
    return apiRequest(`/subjects/teacher/${teacherId}`)
  },

  // Students in the class list of one subject and section
  getClassList(subject) {
    return apiRequest(`/class-lists${forSubject(subject)}`)
  },

  // Assessment items (Subject Mapping) of one subject and section
  getAssessments(subject) {
    return apiRequest(`/assessments${forSubject(subject)}`)
  },

  // Student scores (one activity per student per assessment item)
  getActivities(subject) {
    return apiRequest(`/activities${forSubject(subject)}`)
  },

  // Adds many students at once. It is all or nothing.
  bulkImportClassList(subject, students) {
    return apiRequest('/class-lists/bulk-import', {
      method: 'POST',
      body: JSON.stringify({
        subjectName: subject.subjectName,
        classSection: subject.classSection,
        students: students.map((s) => ({
          studentId: s.id,
          firstName: s.firstName,
          lastName: s.lastName,
        })),
      }),
    })
  },

  // Adds one student to a class list
  addClassListStudent(subject, student) {
    return apiRequest('/class-lists', {
      method: 'POST',
      body: JSON.stringify({
        subjectName: subject.subjectName,
        classSection: subject.classSection,
        studentId: student.id,
        firstName: student.firstName,
        lastName: student.lastName,
      }),
    })
  },

  updateAssessment(item, subject) {
    return apiRequest(`/assessments/${item.id}`, {
      method: 'PUT',
      body: JSON.stringify({
        assessmentItem: item.name,
        learningOutcome: item.outcome.trim(),
        maxScore: Number(item.maxScore),
        subjectName: subject.subjectName,
        classSection: subject.classSection,
      }),
    })
  },

  createSubject({ subjectName, classSection, teacherId }) {
    return apiRequest('/subjects', {
      method: 'POST',
      body: JSON.stringify({ subjectName, classSection, teacherId }),
    })
  },

  createAssessment(item, subject) {
    return apiRequest('/assessments', {
      method: 'POST',
      body: JSON.stringify({
        assessmentItem: item.name.trim(),
        learningOutcome: item.outcome.trim(),
        maxScore: Number(item.maxScore),
        subjectName: subject.subjectName,
        classSection: subject.classSection,
      }),
    })
  },

  deleteAssessment(id) {
    return apiRequest(`/assessments/${id}`, { method: 'DELETE' })
  },

  deleteActivity(id) {
    return apiRequest(`/activities/${id}`, { method: 'DELETE' })
  },

  createActivity(activity) {
    return apiRequest('/activities', { method: 'POST', body: JSON.stringify(activity) })
  },

  updateActivity(id, activity) {
    return apiRequest(`/activities/${id}`, { method: 'PUT', body: JSON.stringify(activity) })
  },
}