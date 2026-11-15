import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { AuthContext } from '@/context/authContext.js'
import { authService } from '@/services/authService.js'

const SESSION_KEY = 'tudlo_session'

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
    authService.getCurrentUser(session.token)
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
        if (error.status !== 401) setSessionError(error.message)
      })
      .finally(() => {
        if (active) setAuthLoading(false)
      })
    return () => {
      active = false
    }
  }, [session])

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
  }), [session, authLoading, sessionError, login, logout])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
