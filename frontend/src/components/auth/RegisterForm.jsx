import { useState } from "react";
import { Link } from "react-router-dom";

export default function RegisterForm({ onSubmit, loading, error }) {
  const [form, setForm] = useState({ name: "", email: "", password: "" });
  const update = (key, value) => setForm((f) => ({ ...f, [key]: value }));

  return (
    <form className="auth-form" onSubmit={(e) => { e.preventDefault(); onSubmit(form); }}>
      {error && <div className="alert error">{error}</div>}
      <label>Name<input value={form.name} onChange={(e) => update("name", e.target.value)} required /></label>
      <label>Email<input type="email" value={form.email} onChange={(e) => update("email", e.target.value)} required /></label>
      <label>Password<input type="password" minLength="6" value={form.password} onChange={(e) => update("password", e.target.value)} required /></label>
      <button className="btn primary full" disabled={loading}>{loading ? "Creating..." : "Create account"}</button>
      <p className="auth-switch">Already have an account? <Link to="/login">Sign in</Link></p>
    </form>
  );
}
