import { useState } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import RegisterForm from "../components/auth/RegisterForm";
import { useAuth } from "../context/AuthContext";

export default function Register() {
  const { register, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  if (isAuthenticated) return <Navigate to="/dashboard" replace />;

  const submit = async (payload) => {
    setLoading(true); setError("");
    try {
      await register(payload);
      navigate("/login", { replace: true, state: { registered: true } });
    } catch (e) { setError(e.message); }
    finally { setLoading(false); }
  };

  return <div className="auth-page"><div className="auth-card"><span className="eyebrow">JOIN AUCTIONHUB</span><h1>Create account</h1><p className="muted">Register to bid or sell.</p><RegisterForm onSubmit={submit} loading={loading} error={error} /></div></div>;
}
