import { apiRequest } from '@/lib/apiClient.js'

export const authService = {
  async login(userIdNumber, password) {
    return apiRequest('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ userIdNumber, password }),
      isLogin: true,
    })
  },

  async getCurrentUser(token, options = {}) {
    return apiRequest('/auth/me', { ...options, token })
  },
}
