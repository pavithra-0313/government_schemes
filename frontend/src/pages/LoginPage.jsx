import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../context/AuthContext';
import { LogIn, Lock, Mail, AlertCircle, ArrowRight } from 'lucide-react';

export const LoginPage = () => {
  const { t } = useTranslation();
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const from = location.state?.from?.pathname || '/';

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    const res = await login(email, password);
    setLoading(false);

    if (res.success) {
      navigate(from, { replace: true });
    } else {
      setError(res.message);
    }
  };

  const handleQuickDemo = (demoEmail, demoPass) => {
    setEmail(demoEmail);
    setPassword(demoPass);
  };

  return (
    <div className="container" style={{ padding: '60px 20px', maxWidth: '480px' }}>
      <div className="form-card" style={{ padding: '36px' }}>
        <div style={{ textAlign: 'center', marginBottom: '28px' }}>
          <div style={{ width: '56px', height: '56px', borderRadius: '14px', background: 'var(--primary-light)', color: 'var(--primary)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', marginBottom: '14px' }}>
            <LogIn size={28} />
          </div>
          <h1 style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--primary)', marginBottom: '6px' }}>
            Welcome Back
          </h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem' }}>
            Sign in to access your saved tracker & checklists
          </p>
        </div>

        {error && (
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', background: '#fee2e2', color: '#b91c1c', padding: '12px 16px', borderRadius: '10px', marginBottom: '20px', fontSize: '0.9rem' }}>
            <AlertCircle size={18} />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Email Address *</label>
            <div style={{ position: 'relative' }}>
              <input
                type="email"
                className="form-control"
                placeholder="student@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                style={{ paddingLeft: '40px' }}
              />
              <Mail size={18} color="#94a3b8" style={{ position: 'absolute', left: '14px', top: '15px' }} />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Password *</label>
            <div style={{ position: 'relative' }}>
              <input
                type="password"
                className="form-control"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                style={{ paddingLeft: '40px' }}
              />
              <Lock size={18} color="#94a3b8" style={{ position: 'absolute', left: '14px', top: '15px' }} />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="btn btn-primary"
            style={{ width: '100%', padding: '14px', fontSize: '1rem', marginTop: '10px' }}
          >
            {loading ? 'Verifying Credentials...' : 'Sign In'}
            <ArrowRight size={18} />
          </button>
        </form>

        {/* Demo login shortcuts */}
        <div style={{ marginTop: '24px', paddingTop: '20px', borderTop: '1px solid var(--border)' }}>
          <p style={{ fontSize: '0.85rem', color: '#64748b', fontWeight: 600, marginBottom: '10px', textAlign: 'center' }}>
            Quick Demo Accounts:
          </p>
          <div style={{ display: 'flex', gap: '10px' }}>
            <button
              type="button"
              onClick={() => handleQuickDemo('admin@scholarshipfinder.gov.in', 'Admin@123')}
              className="btn btn-outline"
              style={{ flex: 1, padding: '8px', fontSize: '0.82rem' }}
            >
              Demo Admin
            </button>
            <button
              type="button"
              onClick={() => handleQuickDemo('kavitha@example.com', 'User@123')}
              className="btn btn-outline"
              style={{ flex: 1, padding: '8px', fontSize: '0.82rem' }}
            >
              Demo Student
            </button>
          </div>
        </div>

        <div style={{ textAlign: 'center', marginTop: '24px', fontSize: '0.92rem', color: 'var(--text-muted)' }}>
          Don't have an account?{' '}
          <Link to="/register" style={{ fontWeight: 700, color: 'var(--primary)' }}>
            Register Now
          </Link>
        </div>
      </div>
    </div>
  );
};

