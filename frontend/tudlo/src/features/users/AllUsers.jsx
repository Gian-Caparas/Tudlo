import { useEffect, useMemo, useState } from 'react'
import { PageHeader } from '@/components/shared/PageHeader.jsx'
import { Avatar } from '@/components/ui/Avatar.jsx'
import { Badge } from '@/components/ui/Badge.jsx'
import { Icon } from '@/components/ui/Icon.jsx'
import { useAuth } from '@/hooks/useAuth.js'
import { assignableRoles, canManage } from '@/utils/permissions.js'
import { usersService } from '@/services/usersService.js'
import { AddUsers } from './AddUsers.jsx'

const roleLabels = {
  ADMIN: 'Admin',
  DEVELOPER: 'Developer',
  TEACHER: 'Teacher',
  STUDENT: 'Student',
}

function formatCreatedDate(value) {
  if (!value) return '—'
  const datePart = String(value).match(/^(\d{4})-(\d{2})-(\d{2})/)
  if (!datePart) return '—'
  const date = new Date(Number(datePart[1]), Number(datePart[2]) - 1, Number(datePart[3]))
  return new Intl.DateTimeFormat('en-US', {
    month: 'short',
    day: '2-digit',
    year: 'numeric',
  }).format(date)
}

export function AllUsers() {
  const { role: actorRole, user: currentUser } = useAuth()
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [loadError, setLoadError] = useState('')
  const [retryCount, setRetryCount] = useState(0)
  const [actionError, setActionError] = useState('')
  const [search, setSearch] = useState('')
  const [roleFilter, setRoleFilter] = useState('ALL')
  const [showAddUser, setShowAddUser] = useState(false)
  const [updatingId, setUpdatingId] = useState(null)
  const availableRoles = assignableRoles(actorRole)

  useEffect(() => {
    let active = true
    setLoading(true)
    setLoadError('')
    usersService.getAll()
      .then((result) => {
        if (active) {
          setUsers(result)
          setLoadError('')
        }
      })
      .catch((error) => {
        if (active) setLoadError(error.message)
      })
      .finally(() => {
        if (active) setLoading(false)
      })
    return () => {
      active = false
    }
  }, [retryCount])

  const filteredUsers = useMemo(() => {
    const query = search.trim().toLowerCase()
    return users.filter((user) => {
      const matchesSearch = !query
        || user.fullName?.toLowerCase().includes(query)
        || user.email?.toLowerCase().includes(query)
      const matchesRole = roleFilter === 'ALL' || user.userType === roleFilter
      return matchesSearch && matchesRole
    })
  }, [users, search, roleFilter])

  const activeCount = users.filter((user) => user.status === 'ACTIVE').length
  const teacherCount = users.filter((user) => user.userType === 'TEACHER').length
  const studentCount = users.filter((user) => user.userType === 'STUDENT').length

  function handleCreated(user) {
    setUsers((currentUsers) => [...currentUsers, user])
    setShowAddUser(false)
    setActionError('')
  }

  async function handleAccessChange(user) {
    const nextStatus = user.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
    setUpdatingId(user.id)
    setActionError('')
    try {
      const updatedUser = await usersService.update(user.id, {
        fullName: user.fullName,
        email: user.email,
        userType: user.userType,
        status: nextStatus,
      })
      setUsers((currentUsers) => currentUsers.map((current) => (
        current.id === updatedUser.id ? updatedUser : current
      )))
    } catch (error) {
      setActionError(error.message)
    } finally {
      setUpdatingId(null)
    }
  }

  return (
    <div className="users-page">
      <PageHeader
        eyebrow="ADMINISTRATION"
        title="Users"
        description="Manage who can access Tudlo and what they can see."
        action={availableRoles.length > 0 && (
          <button className="btn primary" type="button" onClick={() => setShowAddUser(true)}>
            <Icon name="plus" size={15} />
            Add user
          </button>
        )}
      />

      <section className="metric-strip users-metrics" aria-label="User summary">
        <div><strong>{users.length}</strong><span>Total accounts</span></div>
        <div><strong>{activeCount}</strong><span>Active</span></div>
        <div><strong>{teacherCount}</strong><span>Teachers</span></div>
        <div><strong>{studentCount}</strong><span>Students</span></div>
      </section>

      <section className="card users-card" aria-label="All users">
        <div className="toolbar">
          <label className="search">
            <Icon name="search" size={15} />
            <input
              type="search"
              placeholder="Search name or email..."
              value={search}
              onChange={(event) => setSearch(event.target.value)}
              aria-label="Search users by name or email"
            />
          </label>
          <select
            value={roleFilter}
            onChange={(event) => setRoleFilter(event.target.value)}
            aria-label="Filter users by role"
          >
            <option value="ALL">All roles</option>
            {Object.entries(roleLabels).map(([value, label]) => (
              <option key={value} value={value}>{label}s</option>
            ))}
          </select>
        </div>

        {loadError && (
          <div className="users-message users-error" role="alert">
            <span>{loadError}</span>
            <button
              type="button"
              className="btn secondary"
              onClick={() => setRetryCount((current) => current + 1)}
            >
              Retry
            </button>
          </div>
        )}
        {actionError && <p className="users-action-error" role="alert">{actionError}</p>}

        <div className="table-wrap users-table-wrap">
          <table className="users-table">
            <thead>
              <tr>
                <th scope="col">Full name</th>
                <th scope="col">Email</th>
                <th scope="col">Role</th>
                <th scope="col">Status</th>
                <th scope="col">Created</th>
                <th scope="col">Access</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr><td className="users-empty" colSpan="6">Loading users...</td></tr>
              ) : filteredUsers.length === 0 && !loadError ? (
                <tr>
                  <td className="users-empty" colSpan="6">
                    {users.length === 0 ? 'No users found.' : 'No users match your search.'}
                  </td>
                </tr>
              ) : filteredUsers.map((user) => {
                const canChangeAccess = canManage(actorRole, user.userType)
                  && String(user.id) !== String(currentUser?.id)
                const active = user.status === 'ACTIVE'
                return (
                  <tr key={user.id}>
                    <td>
                      <div className="person">
                        <Avatar name={user.fullName || '?'} size="sm" />
                        <strong>{user.fullName}</strong>
                      </div>
                    </td>
                    <td className="muted">{user.email}</td>
                    <td>
                      <Badge tone={user.userType?.toLowerCase() ?? 'neutral'}>
                        {roleLabels[user.userType] ?? user.userType}
                      </Badge>
                    </td>
                    <td>
                      <Badge tone={active ? 'success' : 'neutral'}>
                        <span className="dot" />
                        {active ? 'Active' : 'Inactive'}
                      </Badge>
                    </td>
                    <td className="muted">{formatCreatedDate(user.createdAt)}</td>
                    <td>
                      <button
                        type="button"
                        className={`switch${active ? ' on' : ''}`}
                        role="switch"
                        aria-checked={active}
                        aria-label={`${active ? 'Deactivate' : 'Activate'} ${user.fullName}`}
                        disabled={!canChangeAccess || updatingId === user.id}
                        onClick={() => handleAccessChange(user)}
                      >
                        <i />
                      </button>
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>
      </section>

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
