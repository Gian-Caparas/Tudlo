import { useLocation } from 'react-router-dom'

// path in the URL -> name shown in the breadcrumb
const pageNames = {
  '/dashboard': 'Dashboard',
  '/import': 'Import Data',
  '/topics': 'Topic Mapping',
  '/gaps': 'Learning Gaps',
  '/students': 'Students',
  '/support': 'Support',
}

function Header() {
  const location = useLocation()
  const pageName = pageNames[location.pathname] || ''

  return (
    <header className="flex items-center justify-between border-b px-6 py-3">
      <p className="text-sm">
        CSIT321 - G6 &gt; <span className="font-bold">{pageName}</span>
      </p>
      <div className="flex items-center gap-3 text-sm">
        <div>
          <p className="font-bold">Prof. Ramon Villanueva</p>
          <p className="text-xs">Teacher</p>
        </div>
        <button className="border px-2 py-1">Log out</button>
      </div>
    </header>
  )
}

export default Header