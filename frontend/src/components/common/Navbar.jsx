import { Link, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { formatCurrency } from "../../utils/formatCurrency";

export default function Navbar() {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

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
