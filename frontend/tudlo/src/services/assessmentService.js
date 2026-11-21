import { apiRequest } from '@/lib/apiClient.js'

export const assessmentService = {
  getBySubject(subjectName, classSection) {
    return apiRequest(
      `/assessments/subject/${encodeURIComponent(subjectName)}/section/${encodeURIComponent(classSection)}`,
    )
  },
}