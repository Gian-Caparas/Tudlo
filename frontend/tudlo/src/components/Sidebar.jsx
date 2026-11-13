import { NavLink } from 'react-router-dom'

const links = [
  { to: '/dashboard', label: 'Dashboard' },
  { to: '/import', label: 'Import Data' },
  { to: '/topics', label: 'Topic Mapping' },
  { to: '/gaps', label: 'Learning Gaps' },
  { to: '/students', label: 'Students' },
  { to: '/support', label: 'Support' },
]

function Sidebar() {
  return (
    <aside className="w-52 shrink-0 border-r p-4">
      <h1 className="mb-4 text-xl font-bold">Tudlo</h1>
      <p className="mb-4 text-sm">Teaching: CSIT321 - G6</p>
      <nav className="flex flex-col gap-2">
        {links.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            className={({ isActive }) => (isActive ? 'font-bold underline' : '')}
          >
            {link.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  )
}

export default Sidebar