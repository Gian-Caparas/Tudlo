import { useState, useEffect } from 'react'
import { MainLayout } from '@/layout/MainLayout.jsx'
import { LoginPage } from '@/pages/Login.jsx'
import { authService } from '@/services/authService.js'
import Dashboard from './pages/Dashboard.jsx'
import ImportData from './pages/ImportData.jsx'
import TopicMapping from './pages/TopicMapping.jsx'
import { PlaceholderPage } from './components/shared/PlaceholderPage.jsx'

const roleHomePages = {
  admin: 'Users',
  teacher: 'Dashboard',
  student: 'My Progress',
}

function resolveRole(userType) {
  const role = userType?.toLowerCase()
  const page = roleHomePages[role]
  if (!page) throw new Error('The backend returned an unsupported user role.')
  return { role, page }
}

function App() {
  const [role, setRole] = useState(null)
  const [page, setPage] = useState(null)
  const [userName, setUserName] = useState('')
  const [authLoading, setAuthLoading] = useState(true)
  const [sessionError, setSessionError] = useState('')

  useEffect(() => {
    let active = true
    const token = sessionStorage.getItem('tudlo_token')

    async function restoreSession() {
      if (!token) {
        setAuthLoading(false)
        return
      }

      try {
        const user = await authService.getCurrentUser(token)
        if (!active) return
        const resolved = resolveRole(user.userType)
        setRole(resolved.role)
        setPage(resolved.page)
        setUserName(user.fullName)
      } catch (error) {
        if (!active) return
        sessionStorage.removeItem('tudlo_token')
        setSessionError(
          error.status === 401
            ? 'Your session expired. Please sign in again.'
            : 'Could not verify your saved session. Please sign in again.'
        )
      } finally {
        if (active) setAuthLoading(false)
      }
    }

    restoreSession()
    return () => {
      active = false
    }
  }, [])

  const handleLogin = (user) => {
    const resolved = resolveRole(user.userType)
    sessionStorage.setItem('tudlo_token', user.token)
    setSessionError('')
    setRole(resolved.role)
    setPage(resolved.page)
    setUserName(user.fullName)
  }

  const handleLogout = () => {
    setRole(null)
    setUserName('')
    setPage(null)
    sessionStorage.removeItem('tudlo_token')
  }

  const handleNavigate = (label) => setPage(label)

  if (authLoading) {
    return <div className="flex min-h-screen items-center justify-center" role="status">Checking your session…</div>
  }

  if (!role) {
    return <LoginPage onLogin={handleLogin} initialError={sessionError} />
  }

  return (
    <MainLayout
      role={role}
      page={page}
      userName={userName}
      onNavigate={handleNavigate}
      onLogout={handleLogout}
    >
      {page === 'Dashboard' && <Dashboard role={role} />}
      {page === 'Import Data' && <ImportData />}
      {page === 'Topic Mapping' && <TopicMapping />}
      {page === 'Learning Gaps' && <PlaceholderPage title="Learning Gaps" />}
      {page === 'Students' && <PlaceholderPage title="Students" />}
      {page === 'Support' && <PlaceholderPage title="Support" />}
      {page === 'Users' && <PlaceholderPage title="Users" />}
      {page === 'System Maintenance' && <PlaceholderPage title="System Maintenance" />}
      {/* Student pages */}
      {page === 'My Progress' && <PlaceholderPage title="My Progress" />}
      {page === 'Topics to Review' && <PlaceholderPage title="Topics to Review" />}
      {page === 'My Submissions' && <PlaceholderPage title="My Submissions" />}
    </MainLayout>
  )
}

export default App