import { useTheme } from "../../context/ThemeContext";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { formatCurrency } from "../../utils/formatCurrency";
import { useState } from "react";

export default function Navbar() {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);
  const { theme, toggleTheme } = useTheme();
  const doLogout = () => {
    setMenuOpen(false);
    logout();
    navigate("/login");
  };

  const closeMenu = () => setMenuOpen(false);

  return (
    <header className="navbar">
      <div className="nav-inner">
        <Link className="brand" to="/" onClick={closeMenu}>
          Auction<span>Hub</span>
        </Link>

        <nav
          className={`nav-links${menuOpen ? " nav-links-open" : ""}`}
          aria-label="Main navigation"
        >
          <NavLink to="/auctions" onClick={closeMenu}>
            Auctions
          </NavLink>

          {isAuthenticated && (
            <NavLink to="/dashboard" onClick={closeMenu}>
              Dashboard
            </NavLink>
          )}

          {isAuthenticated && (
            <NavLink to="/my-bids" onClick={closeMenu}>
              My Bids
            </NavLink>
          )}

          {isAuthenticated && (
            <NavLink to="/my-auctions" onClick={closeMenu}>
              My Auctions
            </NavLink>
          )}

          {isAuthenticated && (
            <NavLink to="/create-auction" onClick={closeMenu}>
              Sell
            </NavLink>
          )}
        </nav>

          <button
          type="button"
          className="btn small secondary theme-toggle"
          onClick={toggleTheme}
          aria-label={`Switch to ${theme === "dark" ? "light" : "dark"} mode`}
          title="Toggle theme"
        >
          {theme === "dark" ? "☀️" : "🌙"}
          <span className="theme-label">
            {theme === "dark" ? "Light" : "Dark"}
          </span>
        </button>
        
        <div className="nav-user">
          {isAuthenticated ? (
            <>
              <span className="wallet-pill">
                {formatCurrency(user?.balance ?? 0)} available
              </span>

              <button
                type="button"
                className="btn small secondary"
                onClick={doLogout}
              >
                Logout
              </button>
            </>
          ) : (
            <>
              <Link
                className="btn small secondary"
                to="/login"
                onClick={closeMenu}
              >
                Login
              </Link>

              <Link
                className="btn small"
                to="/register"
                onClick={closeMenu}
              >
                Get Started
              </Link>
            </>
          )}
        </div>

        <button
          type="button"
          className="mobile-menu-toggle"
          aria-label={menuOpen ? "Close navigation menu" : "Open navigation menu"}
          aria-expanded={menuOpen}
          onClick={() => setMenuOpen((open) => !open)}
        >
          <span>{menuOpen ? "✕" : "☰"}</span>
        </button>
      </div>
    </header>
  );
}
