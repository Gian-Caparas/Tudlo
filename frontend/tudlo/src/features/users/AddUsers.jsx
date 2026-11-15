import { useState } from 'react'
import { Modal } from '@/components/ui/Modal.jsx'
import { usersService } from '@/services/usersService.js'

const roleLabels = {
  ADMIN: 'Admin',
  TEACHER: 'Teacher',
  STUDENT: 'Student',
}

export function AddUsers({ roles, onClose, onCreated }) {
  const [form, setForm] = useState({
    fullName: '',
    email: '',
    password: '',
    userType: roles[0] ?? 'STUDENT',
  })
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  function updateField(event) {
    setForm((current) => ({ ...current, [event.target.name]: event.target.value }))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setError('')
    setSaving(true)
    try {
      const user = await usersService.create({
        ...form,
        fullName: form.fullName.trim(),
        email: form.email.trim(),
        status: 'ACTIVE',
      })
      onCreated(user)
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <Modal
      title="Add user"
      onClose={saving ? () => {} : onClose}
      footer={(
        <>
          <button className="btn ghost" type="button" onClick={onClose} disabled={saving}>
            Cancel
          </button>
          <button className="btn primary" type="submit" form="add-user-form" disabled={saving}>
            {saving ? 'Adding...' : 'Add user'}
          </button>
        </>
      )}
    >
      <form id="add-user-form" className="form-grid add-user-form" onSubmit={handleSubmit}>
        <p className="modal-copy">Create an account for a teacher or student.</p>
        {error && <p className="users-form-error" role="alert">{error}</p>}
        <label>
          Full name
          <input
            name="fullName"
            type="text"
            value={form.fullName}
            onChange={updateField}
            autoComplete="name"
            maxLength="150"
            required
          />
        </label>
        <label>
          Email
          <input
            name="email"
            type="email"
            value={form.email}
            onChange={updateField}
            autoComplete="email"
            maxLength="150"
            required
          />
        </label>
        <label>
          Temporary password
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