import { useState } from 'react'
import Sidebar from './components/Sidebar.jsx'
import Header from './components/Header.jsx'
import PlaceholderPage from './components/PlaceholderPage.jsx'
import Dashboard from './pages/Dashboard.jsx'
import ImportData from './pages/ImportData.jsx'
import TopicMapping from './pages/TopicMapping.jsx'

function App() {
  const [page, setPage] = useState('dashboard') // which page is showing

  return (
    <div className="flex min-h-screen">
      <Sidebar page={page} setPage={setPage} />
      <div className="flex-1">
        <Header page={page} />
        <main className="p-6">
          {page === 'dashboard' && <Dashboard setPage={setPage} />}
          {page === 'import' && <ImportData setPage={setPage} />}
          {page === 'topics' && <TopicMapping />}
          {page === 'gaps' && <PlaceholderPage title="Learning Gaps" />}
          {page === 'students' && <PlaceholderPage title="Students" />}
          {page === 'support' && <PlaceholderPage title="Support" />}
        </main>
      </div>
    </div>
  )
}

export default App