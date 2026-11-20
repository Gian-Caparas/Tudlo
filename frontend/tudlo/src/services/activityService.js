import { apiRequest } from '@/lib/apiClient.js'

export const activityService = {
  getByStudent(studentId) {
    return apiRequest(`/activities/student/${encodeURIComponent(studentId)}`)
  },
}