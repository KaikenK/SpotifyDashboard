import React, { createContext, useContext, useState, useEffect, useCallback, useRef } from 'react';
import api from '../api/axios';

const AuthContext = createContext(null);

function clearSession() {
  sessionStorage.removeItem('token');
  sessionStorage.removeItem('role');
  sessionStorage.removeItem('email');
}

/** Decode JWT payload (mirrors Java frontend atob approach) */
function decodeJwt(token) {
  try {
    return JSON.parse(atob(token.split('.')[1]));
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null); // null=checking, false=unauthenticated, object=authenticated
  const [loading, setLoading] = useState(true);
  const checkedRef = useRef(false);

  const checkAuth = useCallback(async () => {
    const token = sessionStorage.getItem('token');
    if (!token) {
      setUser(false);
      setLoading(false);
      return;
    }
    const payload = decodeJwt(token);
    if (!payload || (payload.exp && payload.exp * 1000 < Date.now())) {
      console.error('Auth check: token expired or invalid');
      clearSession();
      setUser(false);
      setLoading(false);
      return;
    }
    // We have a valid token — reconstruct user from JWT claims
    // (matches Java: sub=email, role=ARTIST|FAN|ADMIN)
    setUser({ email: payload.sub, role: payload.role });
    setLoading(false);
  }, []);

  useEffect(() => {
    if (!checkedRef.current) {
      checkedRef.current = true;
      checkAuth();
    }
  }, [checkAuth]);

  const login = useCallback(async (email, password) => {
    // Java AuthController returns: {token: "eyJ..."}
    const { data } = await api.post('/api/auth/login', { email, password });
    const token = data.token;
    const payload = decodeJwt(token);
    sessionStorage.setItem('token', token);
    sessionStorage.setItem('role', payload.role);
    sessionStorage.setItem('email', payload.sub);
    const userData = { email: payload.sub, role: payload.role };
    setUser(userData);
    return userData;
  }, []);

  const register = useCallback(async (username, email, password, role) => {
    // Java AuthController returns: {message: "Registered successfully"}
    await api.post('/api/auth/register', { username, email, password, role });
    return { message: 'Registered successfully' };
  }, []);

  const logout = useCallback(() => {
    clearSession();
    setUser(false);
  }, []);

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be inside AuthProvider');
  return ctx;
}
