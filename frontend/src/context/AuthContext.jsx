import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../api/axios';
import i18n from '../i18n/i18n';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('user');
    return saved ? JSON.parse(saved) : null;
  });
  const [token, setToken] = useState(() => localStorage.getItem('token') || null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (user && user.preferredLanguage) {
      i18n.changeLanguage(user.preferredLanguage);
    }
  }, [user]);

  const login = async (email, password) => {
    setLoading(true);
    try {
      const res = await api.post('/auth/login', { email, password });
      if (res.data && res.data.data) {
        const authData = res.data.data;
        setToken(authData.token);
        const userData = {
          id: authData.id,
          name: authData.name,
          email: authData.email,
          role: authData.role,
          preferredLanguage: authData.preferredLanguage
        };
        setUser(userData);
        localStorage.setItem('token', authData.token);
        localStorage.setItem('user', JSON.stringify(userData));
        if (userData.preferredLanguage) {
          i18n.changeLanguage(userData.preferredLanguage);
          localStorage.setItem('preferred_language', userData.preferredLanguage);
        }
        return { success: true, user: userData };
      }
      return { success: false, message: 'Invalid response from server' };
    } catch (err) {
      const msg = err.response?.data?.message || 'Login failed. Please check your credentials.';
      return { success: false, message: msg };
    } finally {
      setLoading(false);
    }
  };

  const register = async (name, email, password, role = 'USER', preferredLanguage = 'en') => {
    setLoading(true);
    try {
      const res = await api.post('/auth/register', { name, email, password, role, preferredLanguage });
      if (res.data && res.data.data) {
        const authData = res.data.data;
        setToken(authData.token);
        const userData = {
          id: authData.id,
          name: authData.name,
          email: authData.email,
          role: authData.role,
          preferredLanguage: authData.preferredLanguage
        };
        setUser(userData);
        localStorage.setItem('token', authData.token);
        localStorage.setItem('user', JSON.stringify(userData));
        i18n.changeLanguage(preferredLanguage);
        localStorage.setItem('preferred_language', preferredLanguage);
        return { success: true, user: userData };
      }
      return { success: false, message: 'Registration response invalid' };
    } catch (err) {
      const msg = err.response?.data?.message || 'Registration failed. Email might already be taken.';
      return { success: false, message: msg };
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  const changeLanguage = (lang) => {
    i18n.changeLanguage(lang);
    localStorage.setItem('preferred_language', lang);
    if (user) {
      const updatedUser = { ...user, preferredLanguage: lang };
      setUser(updatedUser);
      localStorage.setItem('user', JSON.stringify(updatedUser));
    }
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout, changeLanguage, isAuthenticated: !!token, isAdmin: user?.role === 'ADMIN' }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);

