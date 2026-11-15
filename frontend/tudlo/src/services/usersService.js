import { apiRequest } from '@/lib/apiClient.js'

export const usersService = {
  getAll() {
    return apiRequest('/users')
  },

  create(user) {
    return apiRequest('/users', {
      method: 'POST',
      body: JSON.stringify(user),
    })
  },

  update(id, user) {
    return apiRequest(`/users/${id}`, {
      method: 'PUT',
      body: JSON.stringify(user),
    })
  },
}
