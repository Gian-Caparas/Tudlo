import { apiRequest } from '@/lib/apiClient.js'

const encode = encodeURIComponent

export const academicService = {
  // Student endpoints
  getOwnProgress() {
    return apiRequest('/my-progress')
  },

  getOwnSubmissions() {
    return apiRequest('/activities/student/me')
  },

  getMyOfferings() {
    return apiRequest('/subject-offerings')
  },

  // Learning gaps / support
  getAtRiskByOffering(offeringId) {
    return apiRequest(`/learning-gaps/at-risk/offering/${offeringId}`)
  },

  getHeatmap(offeringId) {
    return apiRequest(`/learning-gaps/heatmap/offering/${offeringId}`)
  },
}
