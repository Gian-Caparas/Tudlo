import { API_BASE_URL } from '@/config/env.js'

const SESSION_KEY = 'tudlo_session'

export class ApiError extends Error {
  constructor(status, message, body = null) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.body = body
  }
}

export async function apiRequest(path, options = {}) {
  const { headers: suppliedHeaders = {}, token, isLogin = false, ...fetchOptions } = options
  const headers = new Headers(suppliedHeaders)
  const sessionToken = token ?? readSessionToken()
  if (sessionToken && !headers.has('Authorization')) {
    headers.set('Authorization', `Bearer ${sessionToken}`)
  }
  if (fetchOptions.body && !(fetchOptions.body instanceof FormData) && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }

  let response
  try {
    response = await fetch(`${API_BASE_URL}${path.startsWith('/') ? path : `/${path}`}`, {
      ...fetchOptions,
      headers,
    })
  } catch {
    throw new ApiError(0, 'Unable to reach the server. Check that the backend is running and try again.')
  }

  const body = await readBody(response)
  if (!response.ok) {
    if (response.status === 401 && !isLogin) {
      clearSession()
      window.dispatchEvent(new CustomEvent('tudlo:session-expired'))
    }
    const message = response.status === 403
      ? 'You are not allowed to perform this action.'
      : body?.message || (response.status === 401 ? 'Invalid email or password.' : 'The request failed.')
    throw new ApiError(response.status, message, body)
  }
  return body
}

function readSessionToken() {
  try {
    return JSON.parse(localStorage.getItem(SESSION_KEY) ?? 'null')?.token ?? null
  } catch {
    return null
  }
}

function clearSession() {
  localStorage.removeItem(SESSION_KEY)
}

async function readBody(response) {
  if (response.status === 204) return null
  const text = await response.text()
  if (!text) return null
  try {
    return JSON.parse(text)
  } catch {
    return { message: text }
  }
}
