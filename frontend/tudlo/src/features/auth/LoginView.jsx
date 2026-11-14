import { useState, useId } from "react";
import { Icon } from "@/components/ui/Icon.jsx";
import { authService } from "@/services/authService";

export function LoginView({ onLogin }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
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
    if (error) setError("");
  }

  function handlePasswordChange(e) {
    setPassword(e.target.value);
    if (fieldErrors.password) setFieldErrors(prev => ({ ...prev, password: validatePassword(e.target.value) }));
    if (error) setError("");
  }

  async function submit(e) {
    e.preventDefault();
    if (!validateForm()) return;

    setLoading(true);
    setError("");

    try {
      const user = await authService.login(email, password);
      onLogin(user);
    } catch (loginError) {
      setError(loginError.message);
    } finally {
      setLoading(false);
    }
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
                <span>{error}</span>
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
          
        </form>
      </div>
    </div>
  );
}