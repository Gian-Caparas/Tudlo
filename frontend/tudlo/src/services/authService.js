import { apiRequest } from '@/lib/apiClient.js'

export const authService = {
  async login(email, password) {
    return apiRequest('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
      isLogin: true,
    })
  },

  async getCurrentUser(token, options = {}) {
    return apiRequest('/auth/me', { ...options, token })
  },
}
