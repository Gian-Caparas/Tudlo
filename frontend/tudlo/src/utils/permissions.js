export const ROLE_RANK = Object.freeze({
  ADMIN: 3,
  TEACHER: 2,
  STUDENT: 1,
})

const homePages = Object.freeze({
  admin: 'Users',
  teacher: 'Dashboard',
  student: 'My Learning',
})

export function assignableRoles(actorRole) {
  const role = normalizeRole(actorRole)
  if (role === 'ADMIN') return ['TEACHER', 'STUDENT']
  if (role === 'TEACHER') return ['STUDENT']
  return []
}

export function canManage(actorRole, targetRole) {
  const actorRank = ROLE_RANK[normalizeRole(actorRole)]
  const targetRank = ROLE_RANK[normalizeRole(targetRole)]
  return actorRank !== undefined && targetRank !== undefined && actorRank > targetRank
}

export function homePage(role) {
  return homePages[String(role ?? '').toLowerCase()] ?? null
}

function normalizeRole(role) {
  return String(role ?? '').toUpperCase()
}
