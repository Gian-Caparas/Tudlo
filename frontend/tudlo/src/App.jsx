import { useState } from 'react'
import { useAuth } from '@/hooks/useAuth.js'
import { navigationByRole } from '@/routes'
import { homePage } from '@/utils/permissions.js'
import { MainLayout } from '@/layout/MainLayout.jsx'
import { LoginPage } from '@/pages/Login.jsx'
import { AccessRestrictedPage } from '@/pages/AccessRestrictedPage.jsx'
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

function renderPage(page, onNavigate) {
  switch (page) {
    case 'Dashboard':
      return <DashboardPage role="teacher" />
    case 'Import Data':
      return <ImportDataPage />
    case 'Topic Mapping':
      return <TopicMappingPage />
    case 'Learning Gaps':
      return <LearningGapsPage />
    case 'Students':
      return <StudentsPage />
    case 'Support':
      return <SupportPage />
    case 'Users':
      return <UsersPage />
    case 'System Maintenance':
      return <SystemMaintenancePage />
    case 'My Progress':
      return <MyProgressPage onNavigate={onNavigate} />
    case 'Topics to Review':
      return <TopicsToReviewPage />
    case 'My Submissions':
      return <MySubmissionsPage />
    default:
      return null
  }
}

function App() {
  const { role, user, authLoading, sessionError, login, logout } = useAuth()
  const [page, setPage] = useState(null)

  if (authLoading) {
    return <div className="flex min-h-screen items-center justify-center" role="status">Checking your session…</div>
  }

  if (!role) {
    return <LoginPage onLogin={login} initialError={sessionError} />
  }

  const currentPage = page ?? homePage(role)
  const allowedPages = navigationByRole[role] ?? []
  const pageAllowed = allowedPages.some((item) => item.label === currentPage)

  return (
    <MainLayout
      role={role}
      page={currentPage}
      userName={user?.fullName ?? user?.name ?? ''}
      onNavigate={setPage}
      onLogout={logout}
    >
      {pageAllowed
        ? renderPage(currentPage, setPage)
        : <AccessRestrictedPage onNavigateHome={() => setPage(homePage(role))} />}
    </MainLayout>
  )
}

export default App
