import { apiRequest } from '@/lib/apiClient.js'

// Service object that groups all user-related API calls.
// Each method returns a Promise, so callers use .then() / await.
export const usersService = {
  // GET /users — fetch the full list of users
  getAll() {
    return apiRequest('/users')
  },

  // POST /users — create a new user
  // The user object is serialized to JSON for the request body
  create(user) {
    return apiRequest('/users', {
      method: 'POST',
      body: JSON.stringify(user),
    })
  },

  // PUT /users/:id — update an existing user by ID
  update(id, user) {
    return apiRequest(`/users/${id}`, {
      method: 'PUT',
      body: JSON.stringify(user),
    })
  },
}