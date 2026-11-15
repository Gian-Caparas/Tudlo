import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { AuthContext } from '@/context/authContext.js'
import { authService } from '@/services/authService.js'

const SESSION_KEY = 'tudlo_session'
const SESSION_VALIDATION_TIMEOUT_MS = 10000

function readSession() {
  localStorage.removeItem('tudlo_role')
  localStorage.removeItem('tudlo_userName')
  try {
    return JSON.parse(localStorage.getItem(SESSION_KEY) ?? 'null')
  } catch {
    localStorage.removeItem(SESSION_KEY)
    return null
  }
}

function createSession(loginResponse) {
  const user = {
    id: loginResponse.id ?? loginResponse.userId,
    fullName: loginResponse.fullName,
    email: loginResponse.email,
    userType: loginResponse.userType,
    status: loginResponse.status,
    createdAt: loginResponse.createdAt,
  }
  return {
    token: loginResponse.token,
    tokenType: loginResponse.tokenType ?? 'Bearer',
    expiresInSeconds: loginResponse.expiresInSeconds,
    profileId: loginResponse.profileId ?? null,
    role: user.userType.toLowerCase(),
    user,
  }
}

export function AuthProvider({ children }) {
  const [initialState] = useState(() => {
    const storedSession = readSession()
    return { session: storedSession, authLoading: Boolean(storedSession?.token) }
  })
  const [session, setSession] = useState(initialState.session)
  const [authLoading, setAuthLoading] = useState(initialState.authLoading)
  const [sessionError, setSessionError] = useState('')
  const [validationAttempt, setValidationAttempt] = useState(0)
  const validatedToken = useRef(null)

  const logout = useCallback((message = '') => {
    localStorage.removeItem(SESSION_KEY)
    setSession(null)
    setAuthLoading(false)
    setSessionError(message)
  }, [])

  const login = useCallback((loginResponse) => {
    const nextSession = createSession(loginResponse)
    localStorage.setItem(SESSION_KEY, JSON.stringify(nextSession))
    setSession(nextSession)
    setSessionError('')
  }, [])

  const retrySessionValidation = useCallback(() => {
    if (!session?.token) return
    validatedToken.current = null
    setSessionError('')
    setAuthLoading(true)
    setValidationAttempt((attempt) => attempt + 1)
  }, [session])

  useEffect(() => {
    function handleSessionExpired() {
      logout('Your session expired. Please sign in again.')
    }
    window.addEventListener('tudlo:session-expired', handleSessionExpired)
    return () => window.removeEventListener('tudlo:session-expired', handleSessionExpired)
  }, [logout])

  useEffect(() => {
    if (!session?.token) return undefined
    if (validatedToken.current === session.token) return undefined
    validatedToken.current = session.token
    let active = true
    let timedOut = false
    const controller = new AbortController()
    const timeoutId = window.setTimeout(() => {
      timedOut = true
      controller.abort()
    }, SESSION_VALIDATION_TIMEOUT_MS)

    authService.getCurrentUser(session.token, { signal: controller.signal })
      .then((currentUser) => {
        if (!active) return
        const nextSession = {
          ...session,
          role: currentUser.userType.toLowerCase(),
          profileId: currentUser.profileId ?? null,
          user: {
            ...session.user,
            ...currentUser,
            fullName: currentUser.fullName,
          },
        }
        localStorage.setItem(SESSION_KEY, JSON.stringify(nextSession))
        setSession(nextSession)
      })
      .catch((error) => {
        if (!active) return
        if (error.status !== 401) setSessionError(error.message)
        if (timedOut) {
          setSessionError('Session verification timed out. Check that the backend is running and try again.')
        }
      })
      .finally(() => {
        window.clearTimeout(timeoutId)
        if (active) setAuthLoading(false)
      })
    return () => {
      active = false
      window.clearTimeout(timeoutId)
      controller.abort()
    }
  }, [session, validationAttempt])

  const value = useMemo(() => ({
    token: session?.token ?? null,
    user: session?.user ?? null,
    role: session?.role ?? null,
    profileId: session?.profileId ?? null,
    session,
    authLoading,
    sessionError,
    setSessionError,
    login,
    logout,
    retrySessionValidation,
  }), [session, authLoading, sessionError, login, logout, retrySessionValidation])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
