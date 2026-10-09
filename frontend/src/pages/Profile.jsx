import { useAuth } from "../context/AuthContext";
import BalanceCard from "../components/wallet/BalanceCard";
import { formatDate } from "../utils/formatDate";

export default function Profile() {
  const { user } = useAuth();
  return <section className="page-section narrow"><span className="eyebrow">ACCOUNT</span><h1>Profile</h1><div className="profile-card"><div className="avatar">{(user?.name || "U").charAt(0).toUpperCase()}</div><h2>{user?.name || "—"}</h2><p className="muted">{user?.email || "—"}</p><div className="profile-meta"><span>Role <b>{user?.role || "USER"}</b></span><span>Joined <b>{formatDate(user?.createdAt)}</b></span><span>Status <b>{user?.active ? "Active" : "Inactive"}</b></span></div></div><BalanceCard user={user} /></section>;
}
