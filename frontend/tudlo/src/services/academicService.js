import { apiRequest } from '@/lib/apiClient.js'

export const academicService = {
  importClasslist(file, term = 'Current') {
    const form = new FormData()
    form.append('file', file)
    return apiRequest(`/classlists/import?term=${encodeURIComponent(term)}`, {
      method: 'POST',
      body: form,
    })
  },

  getOwnProgress() {
    return apiRequest('/students/me/progress')
  },

  getOwnSubmissions() {
    return apiRequest('/students/me/submissions')
  },

  getHeatmap(offeringId) {
    return apiRequest(`/offerings/${offeringId}/heatmap`)
  },

  getMyOfferings() {
    return apiRequest('/offerings')
  },

  recordSupport(activity) {
    return apiRequest('/support', {
      method: 'POST',
      body: JSON.stringify(activity),
    })
  },

  getSupportHistory() {
    return apiRequest('/support')
  },
}
