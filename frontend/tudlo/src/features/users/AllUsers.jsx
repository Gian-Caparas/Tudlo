import { useEffect, useMemo, useState } from 'react'
import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { Avatar } from '@/components/ui/Avatar.jsx'
import { Badge } from '@/components/ui/Badge.jsx'
import { Icon } from '@/components/ui/Icon.jsx'
import { useAuth } from '@/hooks/useAuth.js'
import { assignableRoles } from '@/utils/permissions.js'
import { usersService } from '@/services/usersService.js'
import { AddUsers } from './AddUsers.jsx'

// Human-readable labels for each role
const roleLabels = {
  ADMIN: 'Admin',
  TEACHER: 'Teacher',
  STUDENT: 'Student',
}

// Combine first + last name into a single display name
function getFullName(user) {
  return [user.firstName, user.lastName].filter(Boolean).join(' ')
}

export function AllUsers() {
  const { role: actorRole } = useAuth()

  // --- Data state ---
  const [users, setUsers] = useState([])          // list of users from server
  const [loading, setLoading] = useState(true)    // fetch in progress
  const [loadError, setLoadError] = useState('')  // fetch error message
  const [retryCount, setRetryCount] = useState(0) // bump to re-run fetch

  // --- UI state ---
  const [search, setSearch] = useState('')             // search input
  const [roleFilter, setRoleFilter] = useState('ALL')  // role dropdown
  const [showAddUser, setShowAddUser] = useState(false) // show add modal
  const [notice, setNotice] = useState('')              // success message

  // Roles the current user is allowed to assign
  const availableRoles = assignableRoles(actorRole)

  // Fetch all users on mount (and whenever retryCount changes)
  useEffect(() => {
    let active = true
    setLoading(true)
    setLoadError('')

    usersService.getAll()
      .then((result) => { if (active) setUsers(result) })
      .catch((err) => { if (active) setLoadError(err.message) })
      .finally(() => { if (active) setLoading(false) })

    // Prevent state updates if component unmounted mid-fetch
    return () => { active = false }
  }, [retryCount])

  // Filter users by search text (name or ID) and selected role
  const filteredUsers = useMemo(() => {
    const query = search.trim().toLowerCase()
    return users.filter((user) => {
      const fullName = getFullName(user).toLowerCase()
      const matchesSearch = !query
        || fullName.includes(query)
        || user.userIdNumber?.toLowerCase().includes(query)
      const matchesRole = roleFilter === 'ALL' || user.userType === roleFilter
      return matchesSearch && matchesRole
    })
  }, [users, search, roleFilter])

  // Counts for the summary strip
  const teacherCount = users.filter((u) => u.userType === 'TEACHER').length
  const studentCount = users.filter((u) => u.userType === 'STUDENT').length
  const adminCount = users.filter((u) => u.userType === 'ADMIN').length

  // Called when a new user is created in the AddUsers modal
  function handleCreated(user) {
    setUsers((current) => [...current, user])
    setShowAddUser(false)
    setNotice(`${getFullName(user)} was added with user ID ${user.userIdNumber}.`)
  }

  return (
    <div className="users-page">
      {/* Page title + "Add user" button (only if actor can assign roles) */}
      <PageHeader
        eyebrow="ADMINISTRATION"
        title="User Management"
        description="View accounts and manage user access."
        action={availableRoles.length > 0 && (
          <button className="btn primary" type="button" onClick={() => setShowAddUser(true)}>
            <Icon name="plus" size={15} />
            Add user
          </button>
        )}
      />

      {/* Summary metrics: total, admins, teachers, students */}
      <section className="metric-strip users-metrics" aria-label="User summary">
        <div><strong>{users.length}</strong><span>Total users</span></div>
        <div><strong>{adminCount}</strong><span>Admins</span></div>
        <div><strong>{teacherCount}</strong><span>Teachers</span></div>
        <div><strong>{studentCount}</strong><span>Students</span></div>
      </section>

      {/* Success notice after adding a user */}
      {notice && <p className="users-message" role="status">{notice}</p>}

      <section className="card users-card" aria-label="All users">
        {/* Search + role filter toolbar */}
        <div className="toolbar">
          <label className="search">
            <Icon name="search" size={15} />
            <input
              type="search"
              placeholder="Search name or user ID..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              aria-label="Search users by name or user ID number"
            />
          </label>
          <select
            value={roleFilter}
            onChange={(e) => setRoleFilter(e.target.value)}
            aria-label="Filter users by role"
          >
            <option value="ALL">All roles</option>
            {Object.entries(roleLabels).map(([value, label]) => (
              <option key={value} value={value}>{label}s</option>
            ))}
          </select>
        </div>

        {/* Error banner with retry button */}
        {loadError && (
          <div className="users-message users-error" role="alert">
            <span>{loadError}</span>
            <button
              type="button"
              className="btn secondary"
              onClick={() => setRetryCount((c) => c + 1)}
            >
              Retry
            </button>
          </div>
        )}

        {/* Users table: shows loading / empty / rows */}
        <div className="table-wrap users-table-wrap">
          <table className="users-table">
            <thead>
              <tr>
                <th scope="col">Full name</th>
                <th scope="col">User ID number</th>
                <th scope="col">Role</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr><td className="users-empty" colSpan="3">Loading users...</td></tr>
              ) : filteredUsers.length === 0 && !loadError ? (
                <tr>
                  <td className="users-empty" colSpan="3">
                    {users.length === 0 ? 'No users found.' : 'No users match your search.'}
                  </td>
                </tr>
              ) : filteredUsers.map((user) => {
                const fullName = getFullName(user)
                return (
                  <tr key={user.id}>
                    <td>
                      <div className="person">
                        <Avatar name={fullName || '?'} size="sm" />
                        <strong>{fullName}</strong>
                      </div>
                    </td>
                    <td className="muted">{user.userIdNumber}</td>
                    <td>
                      <Badge tone={user.userType?.toLowerCase() ?? 'neutral'}>
                        {roleLabels[user.userType] ?? user.userType}
                      </Badge>
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>
      </section>

      {/* Add user modal */}
      {showAddUser && (
        <AddUsers
          roles={availableRoles}
          onClose={() => setShowAddUser(false)}
          onCreated={handleCreated}
        />
      )}
    </div>
  )
}