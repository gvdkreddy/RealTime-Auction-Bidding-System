import { Link, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { formatCurrency } from "../../utils/formatCurrency";
import { useTheme } from "../../context/ThemeContext";

export default function Navbar() {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();
  const { theme, toggleTheme } = useTheme();
  const doLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <header className="navbar">
      <div className="nav-inner">
        <Link className="brand" to="/">Auction<span>Hub</span></Link>
        <nav className="nav-links">
          <NavLink to="/auctions">Auctions</NavLink>
          {isAuthenticated && <NavLink to="/dashboard">Dashboard</NavLink>}
          {isAuthenticated && <NavLink to="/my-bids">My Bids</NavLink>}
          {isAuthenticated && <NavLink to="/my-auctions">My Auctions</NavLink>}
          {isAuthenticated && <NavLink to="/create-auction">Sell</NavLink>}
        </nav>
        <button
  type="button"
  className="btn small secondary theme-toggle"
  onClick={toggleTheme}
  aria-label={`Switch to ${theme === "dark" ? "light" : "dark"} mode`}
  title="Toggle color theme"
>
  {theme === "dark" ? "☀️ Light" : "🌙 Dark"}
</button>
        <div className="nav-user">
          {isAuthenticated ? (
            <>
              <Link to="/profile" className="wallet-pill">{formatCurrency(user?.availableBalance ?? 0)} available</Link>
              <button className="btn small secondary" onClick={doLogout}>Logout</button>
            </>
          ) : (
            <>
              <Link className="btn small secondary" to="/login">Login</Link>
              <Link className="btn small primary" to="/register">Register</Link>
            </>
          )}
        </div>
      </div>
    </header>
  );
}
