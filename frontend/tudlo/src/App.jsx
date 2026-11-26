import { useState } from 'react'
import { useAuth } from '@/hooks/useAuth.js'
import { useTeacherData } from '@/hooks/useTeacher.js'
import { getNeedsSupport } from '@/utils/scoring.js'
import { navigationByRole } from '@/routes'
import { homePage } from '@/utils/permissions.js'
import { MainLayout } from '@/layout/MainLayout.jsx'
import { LoginPage } from '@/pages/Login.jsx'
import { AccessRestrictedPage } from '@/pages/AccessRestrictedPage.jsx'
import DashboardPage from '@/pages/teacher/DashboardPage.jsx'
import ImportClassListPage from '@/pages/teacher/ImportClassListPage.jsx'
import SubjectMappingPage from '@/pages/teacher/SubjectMappingPage.jsx'
import LearningGapsPage from '@/pages/teacher/LearningGapsPage.jsx'
import StudentsPage from '@/pages/teacher/StudentsPage.jsx'
import SupportPage from '@/pages/teacher/SupportPage.jsx'
import UsersPage from '@/pages/admin/UsersPage.jsx'
import MyLearningPage from '@/pages/student/MyLearningPage.jsx'

// Everything a page needs is passed in through the second argument
function renderPage(page, { onNavigate, userName, teacherData }) {
  switch (page) {
    case 'Dashboard':
      return <DashboardPage data={teacherData} userName={userName} onNavigate={onNavigate} />
    case 'Import Class List':
      return <ImportClassListPage data={teacherData} onNavigate={onNavigate} />
    case 'Subject Mapping':
      return <SubjectMappingPage data={teacherData} />
    case 'Learning Gaps':
      return <LearningGapsPage data={teacherData} />
    case 'Students':
      return <StudentsPage data={teacherData} />
    case 'Support':
      return <SupportPage data={teacherData} onNavigate={onNavigate} />
    case 'Users':
      return <UsersPage />
    case 'My Learning':
      return <MyLearningPage />
    default:
      return null
  }
}

function App() {
  const { role, user, authLoading, sessionError, login, logout, retrySessionValidation } = useAuth()
  const [page, setPage] = useState(null)
  const teacherData = useTeacherData() // the backend data of the teacher pages

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
  const userName = user?.fullName ?? user?.name ?? ''

  return (
    <MainLayout
      role={role}
      page={currentPage}
      userName={userName}
      classLabel={teacherData.classLabel}
      studentCount={teacherData.students.length}
      attentionCount={getNeedsSupport(teacherData.scores, teacherData.students, teacherData.items).length}
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
        ? renderPage(currentPage, { onNavigate: setPage, userName, teacherData })
        : <AccessRestrictedPage onNavigateHome={() => setPage(homePage(role))} />}
    </MainLayout>
  )
}

export default App