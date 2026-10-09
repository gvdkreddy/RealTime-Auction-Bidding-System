import { useState } from "react";
import { Navigate, useLocation, useNavigate } from "react-router-dom";
import LoginForm from "../components/auth/LoginForm";
import { useAuth } from "../context/AuthContext";

export default function Login() {
  const { login, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  if (isAuthenticated) return <Navigate to="/dashboard" replace />;

  const submit = async (payload) => {
    setLoading(true); setError("");
    try {
      await login(payload);
      navigate(location.state?.from || "/dashboard", { replace: true });
    } catch (e) { setError(e.message); }
    finally { setLoading(false); }
  };

  return <div className="auth-page"><div className="auth-card"><span className="eyebrow">WELCOME BACK</span><h1>Sign in</h1><p className="muted">Access your auctions and live bids.</p><LoginForm onSubmit={submit} loading={loading} error={error} /></div></div>;
}
