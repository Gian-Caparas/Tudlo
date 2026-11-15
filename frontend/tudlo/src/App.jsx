import { useState, useEffect } from 'react'
import { authService } from '@/services/authService.js'
import { MainLayout } from '@/layout/MainLayout.jsx'
import { LoginPage } from '@/pages/Login.jsx'
import DashboardPage from '@/pages/teacher/DashboardPage.jsx'
import ImportDataPage from '@/pages/teacher/ImportDataPage.jsx'
import TopicMappingPage from '@/pages/teacher/TopicMappingPage.jsx'
import LearningGapsPage from '@/pages/teacher/LearningGapsPage.jsx'
import StudentsPage from '@/pages/teacher/StudentsPage.jsx'
import SupportPage from '@/pages/teacher/SupportPage.jsx'
import UsersPage from '@/pages/admin/UsersPage.jsx'
import SystemMaintenancePage from '@/pages/admin/SystemMaintenancePage.jsx'
import MyProgressPage from '@/pages/student/MyProgressPage.jsx'
import TopicsToReviewPage from '@/pages/student/TopicsToReviewPage.jsx'
import MySubmissionsPage from '@/pages/student/MySubmissionsPage.jsx'

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
    const matchedRole = user.userType.toLowerCase()
    setRole(matchedRole)
    setUserName(user.fullName)
    localStorage.setItem('tudlo_role', matchedRole)
    localStorage.setItem('tudlo_userName', user.fullName)
    // Set default page based on role
    if (matchedRole === 'admin') setPage('Users')
    else if (matchedRole === 'teacher') setPage('Dashboard')
    else if (matchedRole === 'student') setPage('My Progress')
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
      {page === 'Dashboard' && <DashboardPage role={role} />}
      {page === 'Import Data' && <ImportDataPage />}
      {page === 'Topic Mapping' && <TopicMappingPage />}
      {page === 'Learning Gaps' && <LearningGapsPage />}
      {page === 'Students' && <StudentsPage />}
      {page === 'Support' && <SupportPage />}
      {page === 'Users' && <UsersPage />}
      {page === 'System Maintenance' && <SystemMaintenancePage />}
      {/* Student pages */}
      {page === 'My Progress' && <MyProgressPage onNavigate={handleNavigate} />}
      {page === 'Topics to Review' && <TopicsToReviewPage />}
      {page === 'My Submissions' && <MySubmissionsPage />}
    </MainLayout>
  )
}

export default App