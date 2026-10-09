import { useState } from "react";
import { Link } from "react-router-dom";

export default function LoginForm({ onSubmit, loading, error }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  return (
    <form className="auth-form" onSubmit={(e) => { e.preventDefault(); onSubmit({ email, password }); }}>
      {error && <div className="alert error">{error}</div>}
      <label>Email<input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required /></label>
      <label>Password<input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required /></label>
      <button className="btn primary full" disabled={loading}>{loading ? "Signing in..." : "Sign in"}</button>
      <p className="auth-switch">Don't have an account? <Link to="/register">Create one</Link></p>
    </form>
  );
}
