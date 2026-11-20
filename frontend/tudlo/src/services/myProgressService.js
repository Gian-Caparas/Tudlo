import { apiRequest } from '@/lib/apiClient.js'

export const myProgressService = {
  getByStudent(studentId) {
    return apiRequest(`/my-progress/student/${encodeURIComponent(studentId)}`)
  },
}