import { useState, useId } from "react";
import { Icon } from "@/components/ui/Icon.jsx";
import { authService } from "@/services/authService";

export function LoginView({ onLogin }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [fieldErrors, setFieldErrors] = useState({ email: "", password: "" });

  const emailId = useId();
  const passwordId = useId();
  const errorId = useId();

  function validateEmail(value) {
    if (!value) return "Email is required";
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) return "Enter a valid email address";
    return "";
  }

  function validatePassword(value) {
    if (!value) return "Password is required";
    if (value.length < 4) return "Password must be at least 4 characters";
    return "";
  }

  function validateForm() {
    const emailErr = validateEmail(email);
    const passwordErr = validatePassword(password);
    setFieldErrors({ email: emailErr, password: passwordErr });
    return !emailErr && !passwordErr;
  }

  function handleEmailChange(e) {
    setEmail(e.target.value);
    if (fieldErrors.email) setFieldErrors(prev => ({ ...prev, email: validateEmail(e.target.value) }));
    if (error) setError(false);
  }

  function handlePasswordChange(e) {
    setPassword(e.target.value);
    if (fieldErrors.password) setFieldErrors(prev => ({ ...prev, password: validatePassword(e.target.value) }));
    if (error) setError(false);
  }

  async function submit(e) {
    e.preventDefault();
    if (!validateForm()) return;

    setLoading(true);
    setError(false);

    // Simulate network delay
    await new Promise(r => setTimeout(r, 500));

    const matchedRole = authService.authenticate(email, password);
    setLoading(false);

    if (matchedRole) onLogin(matchedRole);
    else setError(true);
  }

  function fill(role) {
    setEmail(authService.accounts[role].email);
    setPassword(authService.accounts[role].password);
    setError(false);
    setFieldErrors({ email: "", password: "" });
  }

  return (
    <div className="login-page">
      <div className="login-brand">
        <div className="logo logo-large">
          <span>T</span> Tudlo
        </div>
        <div className="brand-copy">
          <p>LEARNING ANALYTICS</p>
          <h1>
            See beyond
            <br />
            the grade.
          </h1>
          <div className="brand-rule" />
          <p className="brand-sub">
            Discover where every learner is thriving—and where the right support can change their
            path.
          </p>
        </div>
        <div className="brand-footer">Built for meaningful learning, not just metrics.</div>
      </div>
      <div className="login-panel">
        <form className="login-card" onSubmit={submit} noValidate>
          <div className="mobile-logo">
            <div className="logo">
              <span>T</span> Tudlo
            </div>
          </div>
          <p className="eyebrow">WELCOME BACK</p>
          <h2>Sign in to Tudlo</h2>
          <p className="tagline">Find the gap. Close it.</p>
          {error && (
            <div className="login-error" role="alert" id={errorId}>
              <Icon name="alert" aria-hidden="true" />
              <div>
                <strong>We couldn't sign you in</strong>
                <span>Check your email and password, then try again.</span>
              </div>
            </div>
          )}
          <label htmlFor={emailId}>
            Email address
            <input
              id={emailId}
              value={email}
              onChange={handleEmailChange}
              type="email"
              placeholder="you@tudlo.edu"
              autoComplete="email"
              className={fieldErrors.email ? "invalid" : ""}
              aria-invalid={fieldErrors.email ? "true" : "false"}
              aria-describedby={fieldErrors.email ? `${emailId}-error` : undefined}
              disabled={loading}
            />
            {fieldErrors.email && (
              <span id={`${emailId}-error`} className="field-error" role="alert">
                {fieldErrors.email}
              </span>
            )}
          </label>
          <label htmlFor={passwordId}>
            Password
            <div className="password-field">
              <input
                id={passwordId}
                value={password}
                onChange={handlePasswordChange}
                type={showPassword ? "text" : "password"}
                placeholder="Enter your password"
                autoComplete="current-password"
                className={fieldErrors.password ? "invalid" : ""}
                aria-invalid={fieldErrors.password ? "true" : "false"}
                aria-describedby={fieldErrors.password ? `${passwordId}-error` : undefined}
                disabled={loading}
              />
              <button
                type="button"
                className="pw-toggle"
                onClick={() => setShowPassword(!showPassword)}
                aria-label={showPassword ? "Hide password" : "Show password"}
                aria-pressed={showPassword}
                disabled={loading}
              >
                {showPassword ? "Hide" : "Show"}
              </button>
            </div>
            {fieldErrors.password && (
              <span id={`${passwordId}-error`} className="field-error" role="alert">
                {fieldErrors.password}
              </span>
            )}
          </label>
          <button className="btn primary wide" type="submit" disabled={loading}>
            {loading ? (
              <>
                <span className="spinner" aria-hidden="true" />
                Signing in…
              </>
            ) : (
              <>Sign in <span aria-hidden="true">→</span></>
            )}
          </button>
          <div className="divider">
            <span>Explore with a demo account</span>
          </div>
          <div className="demo-buttons" role="group" aria-label="Demo accounts">
            <button type="button" onClick={() => fill("admin")} disabled={loading} aria-label="Demo Admin - Manage users & system">
              <span className="demo-icon" aria-hidden="true">A</span>
              <span>
                <b>Demo Admin</b>
                <small>Manage users & system</small>
              </span>
            </button>
            <button type="button" onClick={() => fill("teacher")} disabled={loading} aria-label="Demo Teacher - Analyze class learning">
              <span className="demo-icon" aria-hidden="true">T</span>
              <span>
                <b>Demo Teacher</b>
                <small>Analyze class learning</small>
              </span>
            </button>
            <button type="button" onClick={() => fill("student")} disabled={loading} aria-label="Demo Student - View personal progress">
              <span className="demo-icon" aria-hidden="true">S</span>
              <span>
                <b>Demo Student</b>
                <small>View personal progress</small>
              </span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}