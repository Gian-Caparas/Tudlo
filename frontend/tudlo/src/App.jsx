import { useState, useEffect } from 'react'
import { MainLayout } from '@/layout/MainLayout.jsx'
import { LoginPage } from '@/pages/Login.jsx'
import Dashboard from './pages/Dashboard.jsx'
import ImportData from './pages/ImportData.jsx'
import TopicMapping from './pages/TopicMapping.jsx'
import { PlaceholderPage } from './components/shared/PlaceholderPage.jsx'

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

  const handleLogin = (matchedRole) => {
    const names = { admin: 'Admin User', teacher: 'Prof. Garcia', student: 'Alex Rivera' }
    setRole(matchedRole)
    setUserName(names[matchedRole])
    localStorage.setItem('tudlo_role', matchedRole)
    localStorage.setItem('tudlo_userName', names[matchedRole])
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