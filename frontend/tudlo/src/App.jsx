import { Routes, Route, Navigate } from 'react-router-dom'
import Layout from './components/Layout.jsx'
import PlaceholderPage from './components/PlaceholderPage.jsx'
import Dashboard from './pages/Dashboard.jsx'
import ImportData from './pages/ImportData.jsx'
import TopicMapping from './pages/TopicMapping.jsx'

function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/import" element={<ImportData />} />
        <Route path="/topics" element={<TopicMapping />} />
        <Route path="/gaps" element={<PlaceholderPage title="Learning Gaps" />} />
        <Route path="/students" element={<PlaceholderPage title="Students" />} />
        <Route path="/support" element={<PlaceholderPage title="Support" />} />
      </Route>
    </Routes>
  )
}

export default App