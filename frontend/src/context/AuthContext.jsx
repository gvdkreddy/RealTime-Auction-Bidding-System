import { createContext, useContext, useEffect, useMemo, useState } from "react";
import { login as loginRequest, register as registerRequest } from "../services/authService";
import { getMe } from "../services/userService";
import { clearAuth, getStoredUser, getToken, saveAuth } from "../utils/auth";
import { apiErrorMessage } from "../services/api";

const AuthContext = createContext(null);

function extractToken(data) {
  return data?.token || data?.accessToken || data?.jwt || data?.access_token || null;
}

function extractUser(data) {
  return data?.user || data?.data?.user || (data?.email ? data : null);
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(getToken());
  const [user, setUser] = useState(getStoredUser());
  const [loading, setLoading] = useState(Boolean(getToken()));

  const refreshUser = async () => {
    try {
      const me = await getMe();
      setUser(me);
      saveAuth(getToken(), me);
      return me;
    } catch {
      return null;
    }
  };

  useEffect(() => {
    const onExpired = () => {
      setToken(null);
      setUser(null);
      clearAuth();
    };
    window.addEventListener("auction-auth-expired", onExpired);

    if (getToken()) {
      refreshUser().finally(() => setLoading(false));
    } else {
      setLoading(false);
    }

    return () => window.removeEventListener("auction-auth-expired", onExpired);
  }, []);

  const login = async (payload) => {
    try {
      const data = await loginRequest(payload);
      const nextToken = extractToken(data);
      if (!nextToken) throw new Error("Login succeeded but no JWT token was returned by the backend.");
      const nextUser = extractUser(data);
      saveAuth(nextToken, nextUser);
      setToken(nextToken);
      setUser(nextUser);
      if (!nextUser) await refreshUser();
      return data;
    } catch (error) {
      throw new Error(apiErrorMessage(error));
    }
  };

  const register = async (payload) => {
    try {
      const data = await registerRequest(payload);
      return data;
    } catch (error) {
      throw new Error(apiErrorMessage(error));
    }
  };

  const logout = () => {
    clearAuth();
    setToken(null);
    setUser(null);
  };

  const value = useMemo(
    () => ({ token, user, loading, isAuthenticated: Boolean(token), login, register, logout, refreshUser }),
    [token, user, loading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
