import { useState, useId, useEffect } from "react";
import { Icon } from "@/components/ui/Icon.jsx";
import { authService } from "@/services/authService";

export function LoginView({ onLogin, initialError = "" }) {
  const [userIdNumber, setUserIdNumber] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(initialError);

  useEffect(() => {
    setError(initialError);
  }, [initialError]);
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [fieldErrors, setFieldErrors] = useState({ userIdNumber: "", password: "" });

  const userIdNumberId = useId();
  const passwordId = useId();
  const errorId = useId();

  function validateUserIdNumber(value) {
    if (!value) return "User ID number is required";
    if (value.length < 5) return "User ID number must be at least 5 characters";
    return "";
  }

  function validatePassword(value) {
    if (!value) return "Password is required";
    return "";
  }

  function validateForm() {
    const userIdNumberErr = validateUserIdNumber(userIdNumber);
    const passwordErr = validatePassword(password);
    setFieldErrors({ userIdNumber: userIdNumberErr, password: passwordErr });
    return !userIdNumberErr && !passwordErr;
  }

  function handleUserIdNumberChange(e) {
    setUserIdNumber(e.target.value);
    if (fieldErrors.userIdNumber) setFieldErrors(prev => ({ ...prev, userIdNumber: validateUserIdNumber(e.target.value) }));
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
      const user = await authService.login(userIdNumber, password);
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
          <label htmlFor={userIdNumberId}>
            User ID Number
            <input
              id={userIdNumberId}
              value={userIdNumber}
              onChange={handleUserIdNumberChange}
              type="text"
              placeholder="e.g., 2026-0001"
              autoComplete="username"
              className={fieldErrors.userIdNumber ? "invalid" : ""}
              aria-invalid={fieldErrors.userIdNumber ? "true" : "false"}
              aria-describedby={fieldErrors.userIdNumber ? `${userIdNumberId}-error` : undefined}
              disabled={loading}
            />
            {fieldErrors.userIdNumber && (
              <span id={`${userIdNumberId}-error`} className="field-error" role="alert">
                {fieldErrors.userIdNumber}
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