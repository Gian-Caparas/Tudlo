import { useState } from 'react'
import { Modal } from '@/components/ui/Modal.jsx'
import { usersService } from '@/services/usersService.js'

// Human-readable role names
const roleLabels = {
  ADMIN: 'Admin',
  TEACHER: 'Teacher',
  STUDENT: 'Student',
}

export function AddUsers({ roles, onClose, onCreated }) {
  // Form fields (role defaults to the first assignable role)
  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    password: '',
    userType: roles[0] ?? 'STUDENT',
  })

  // Submit state
  const [error, setError] = useState('')   // error message from API
  const [saving, setSaving] = useState(false) // request in progress

  // Generic input handler: updates the field matching input's name
  function updateField(event) {
    setForm((current) => ({ ...current, [event.target.name]: event.target.value }))
  }

  // Submit form: create user, then notify parent; show error on failure
  async function handleSubmit(event) {
    event.preventDefault()
    setError('')
    setSaving(true)
    try {
      const user = await usersService.create({
        firstName: form.firstName.trim(),
        lastName: form.lastName.trim(),
        password: form.password,
        userType: form.userType,
      })
      onCreated(user) // parent adds it to the list + closes modal
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <Modal
      title="Add user"
      // Block closing while saving
      onClose={saving ? () => {} : onClose}
      footer={
        <>
          <button className="btn ghost" type="button" onClick={onClose} disabled={saving}>
            Cancel
          </button>
          {/* Submits the form below via its id */}
          <button className="btn primary" type="submit" form="add-user-form" disabled={saving}>
            {saving ? 'Adding...' : 'Add user'}
          </button>
        </>
      }
    >
      <form id="add-user-form" className="form-grid add-user-form" onSubmit={handleSubmit}>
        <p className="modal-copy">The user ID number will be assigned automatically.</p>

        {/* Error banner (only when there's an error) */}
        {error && <p className="users-form-error" role="alert">{error}</p>}

        {/* First name */}
        <label>
          First name
          <input
            name="firstName"
            type="text"
            value={form.firstName}
            onChange={updateField}
            autoComplete="given-name"
            maxLength="150"
            required
          />
        </label>

        {/* Last name */}
        <label>
          Last name
          <input
            name="lastName"
            type="text"
            value={form.lastName}
            onChange={updateField}
            autoComplete="family-name"
            maxLength="150"
            required
          />
        </label>

        {/* Password with hint */}
        <label>
          Password
          <input
            name="password"
            type="password"
            value={form.password}
            onChange={updateField}
            autoComplete="new-password"
            minLength="8"
            maxLength="72"
            required
          />
          <small className="users-field-hint">Use at least 8 characters.</small>
        </label>

        {/* Role dropdown — only roles the actor can assign */}
        <label>
          Role
          <select name="userType" value={form.userType} onChange={updateField} required>
            {roles.map((role) => (
              <option key={role} value={role}>{roleLabels[role] ?? role}</option>
            ))}
          </select>
        </label>
      </form>
    </Modal>
  )
}