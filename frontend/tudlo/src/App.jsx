import { useState, useEffect } from 'react'
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

function App() {
  const [role, setRole] = useState(null)
  const [page, setPage] = useState(null)
  const [userName, setUserName] = useState('')

  // Check for saved auth on mount
  useEffect(() => {
    const savedRole = localStorage.getItem('tudlo_role')
    const savedName = localStorage.getItem('tudlo_userName')
    if (savedRole) {
      setRole(savedRole)
      setUserName(savedName || '')
      // Set default page based on role
      if (savedRole === 'admin') setPage('Users')
      else if (savedRole === 'teacher') setPage('Dashboard')
      else if (savedRole === 'student') setPage('My Progress')
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
    localStorage.removeItem('tudlo_role')
    localStorage.removeItem('tudlo_userName')
  }

  const handleNavigate = (label) => setPage(label)

  if (!role) {
    return <LoginPage onLogin={handleLogin} />
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