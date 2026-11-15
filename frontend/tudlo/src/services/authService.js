import { apiRequest } from '@/lib/apiClient.js'

export const authService = {
  async login(email, password) {
    return apiRequest('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
      isLogin: true,
    })
  },

  async getCurrentUser(token) {
    return apiRequest('/auth/me', { token })
  },
}
