const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8081/api'

async function readJson(response) {
  const contentType = response.headers.get('content-type') || ''
  if (!contentType.includes('application/json')) return null
  return response.json()
}

async function request(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, options)
  const data = await readJson(response)

  if (!response.ok) {
    const error = new Error(data?.message || `Request failed (${response.status})`)
    error.status = response.status
    throw error
  }

  return data
}

export const authService = {
  async login(email, password) {
    const data = await request('/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    })

    if (!data?.token || !data.userType) {
      throw new Error('The login response is missing the token or user role.')
    }

    return data
  },

  getCurrentUser(token) {
    return request('/auth/me', {
      headers: { Authorization: `Bearer ${token}` },
    })
  },
}
