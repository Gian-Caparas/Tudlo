import { useState } from 'react'
import { Modal } from '@/components/ui/Modal.jsx'
import { usersService } from '@/services/usersService.js'

const roleLabels = {
  TEACHER: 'Teacher',
  STUDENT: 'Student',
}

export function EditUser({ user, onClose, onUpdated }) {
  const [form, setForm] = useState({
    firstName: user.firstName ?? '',
    lastName: user.lastName ?? '',
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
      const updatedUser = await usersService.update(user.id, {
        firstName: form.firstName.trim(),
        lastName: form.lastName.trim(),
        userType: user.userType,
      })
      onUpdated(updatedUser)
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setSaving(false)
    }
  }

  const nameLength = user.userType === 'STUDENT' ? 100 : 150

  return (
    <Modal
      title={`Edit ${roleLabels[user.userType] ?? 'user'}`}
      onClose={saving ? () => {} : onClose}
      footer={
        <>
          <button className="btn ghost" type="button" onClick={onClose} disabled={saving}>
            Cancel
          </button>
          <button className="btn primary" type="submit" form="edit-user-form" disabled={saving}>
            {saving ? 'Saving...' : 'Save changes'}
          </button>
        </>
      }
    >
      <form id="edit-user-form" className="form-grid add-user-form" onSubmit={handleSubmit}>
        <p className="modal-copy">
          Update this user's name. Their role and user ID cannot be changed here.
        </p>

        {error && <p className="users-form-error" role="alert">{error}</p>}

        <label>
          First name
          <input
            name="firstName"
            type="text"
            value={form.firstName}
            onChange={updateField}
            autoComplete="given-name"
            maxLength={nameLength}
            required
          />
        </label>

        <label>
          Last name
          <input
            name="lastName"
            type="text"
            value={form.lastName}
            onChange={updateField}
            autoComplete="family-name"
            maxLength={nameLength}
            required
          />
        </label>
      </form>
    </Modal>
  )
}
