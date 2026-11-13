import { Outlet } from 'react-router-dom'
import Sidebar from './Sidebar.jsx'
import Header from './Header.jsx'

function Layout() {
  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <div className="flex-1">
        <Header />
        <main className="p-6">
          <Outlet /> {/* the current page shows up here */}
        </main>
      </div>
    </div>
  )
}

export default Layout