const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api';

const testAccounts = [
  { role: 'admin', email: 'maya@example.com' },
  { role: 'teacher', email: 'gian@example.com' },
  { role: 'student', email: 'jay@example.com' },
];

export const authService = {
  testAccounts,

  async login(email, password) {
    let response;
    try {
      response = await fetch(`${API_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });
    } catch {
      throw new Error('Unable to reach the server. Check that the backend is running and try again.');
    }

    const body = await response.json().catch(() => null);
    if (!response.ok) {
      if (response.status === 401) throw new Error('Invalid email or password.');
      throw new Error(body?.message || 'Sign in failed. Please try again.');
    }
    return body;
  },
};