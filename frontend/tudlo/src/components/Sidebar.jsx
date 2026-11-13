const links = [
  { id: 'dashboard', label: 'Dashboard' },
  { id: 'import', label: 'Import Data' },
  { id: 'topics', label: 'Topic Mapping' },
  { id: 'gaps', label: 'Learning Gaps' },
  { id: 'students', label: 'Students' },
  { id: 'support', label: 'Support' },
]

function Sidebar({ page, setPage }) {
  return (
    <aside className="w-52 shrink-0 border-r p-4">
      <h1 className="mb-4 text-xl font-bold">Tudlo</h1>
      <p className="mb-4 text-sm">Teaching: CSIT321 - G6</p>
      <nav className="flex flex-col items-start gap-2">
        {links.map((link) => (
          <button
            key={link.id}
            onClick={() => setPage(link.id)}
            className={page === link.id ? 'font-bold underline' : ''}
          >
            {link.label}
          </button>
        ))}
      </nav>
    </aside>
  )
}

export default Sidebar