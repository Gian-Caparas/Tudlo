import { useState } from 'react'
import { useAuth } from '@/hooks/useAuth.js'
import { navigationByRole } from '@/routes'
import { homePage } from '@/utils/permissions.js'
import { dashboardScores, startingItems } from '@/mockdata/mockData.js'
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

function renderPage(page, onNavigate, userName, savedItems, onSaveMapping) {
  switch (page) {
    case 'Dashboard':
      return <DashboardPage scores={dashboardScores} userName={userName} onNavigate={onNavigate} />
    case 'Import Data':
      return <ImportDataPage />
    case 'Topic Mapping':
      return <TopicMappingPage savedItems={savedItems} onSave={onSaveMapping} />
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
  const { role, user, authLoading, sessionError, login, logout, retrySessionValidation } = useAuth()
  const [page, setPage] = useState(null)
  const [mappingItems, setMappingItems] = useState(startingItems)

  function handleLogout() {
    setPage(null)
    logout()
  }

  if (authLoading && !role) {
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
      onLogout={handleLogout}
    >
      {(authLoading || sessionError) && (
        <section
          role={authLoading ? 'status' : 'alert'}
          className="mb-4 rounded-xl border border-amber-200 bg-amber-50 p-4 text-amber-950"
        >
          <p className="font-semibold">
            {authLoading ? 'Checking your session…' : 'Could not verify your session'}
          </p>
          <p className="mt-1">
            {authLoading ? 'Your saved session is still available.' : sessionError}
          </p>
          <button
            className="btn primary mt-3"
            type="button"
            onClick={retrySessionValidation}
            disabled={authLoading}
          >
            Retry session check
          </button>
        </section>
      )}
      {pageAllowed
        ? renderPage(
          currentPage,
          setPage,
          user?.fullName ?? user?.name ?? '',
          mappingItems,
          setMappingItems,
        )
        : <AccessRestrictedPage onNavigateHome={() => setPage(homePage(role))} />}
    </MainLayout>
  )
}

export default App
