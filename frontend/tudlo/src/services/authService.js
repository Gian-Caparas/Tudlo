export const authService = {
  accounts: {
    admin: { email: 'admin@tudlo.edu', password: 'demo123', role: 'admin' },
    teacher: { email: 'teacher@tudlo.edu', password: 'demo123', role: 'teacher' },
    student: { email: 'student@tudlo.edu', password: 'demo123', role: 'student' },
  },
  authenticate(email, password) {
    const match = Object.values(this.accounts).find(
      a => a.email === email && a.password === password
    );
    return match?.role ?? null;
  }
};