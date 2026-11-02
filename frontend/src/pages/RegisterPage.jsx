import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../context/AuthContext';
import { UserPlus, User, Mail, Lock, Globe, AlertCircle, ArrowRight } from 'lucide-react';

export const RegisterPage = () => {
  const { t } = useTranslation();
  const { register } = useAuth();
  const navigate = useNavigate();

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [preferredLanguage, setPreferredLanguage] = useState('en');
  const [role, setRole] = useState('USER');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (password.length < 6) {
      setError('Password must be at least 6 characters');
      return;
    }

    setLoading(true);
    const res = await register(name, email, password, role, preferredLanguage);
    setLoading(false);

    if (res.success) {
      navigate('/questionnaire');
    } else {
      setError(res.message);
    }
  };

  return (
    <div className="container" style={{ padding: '60px 20px', maxWidth: '520px' }}>
      <div className="form-card" style={{ padding: '36px' }}>
        <div style={{ textAlign: 'center', marginBottom: '28px' }}>
          <div style={{ width: '56px', height: '56px', borderRadius: '14px', background: 'var(--secondary-light)', color: 'var(--secondary)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', marginBottom: '14px' }}>
            <UserPlus size={28} />
          </div>
          <h1 style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--primary)', marginBottom: '6px' }}>
            Create Student Account
          </h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem' }}>
            Find matched scholarships, save application checklists & set reminders
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
            <label className="form-label">Full Name *</label>
            <div style={{ position: 'relative' }}>
              <input
                type="text"
                className="form-control"
                placeholder="e.g. Arun Kumar"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
                style={{ paddingLeft: '40px' }}
              />
              <User size={18} color="#94a3b8" style={{ position: 'absolute', left: '14px', top: '15px' }} />
            </div>
          </div>

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
            <label className="form-label">Password * (Min 6 characters)</label>
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

          <div className="form-grid-2">
            <div className="form-group">
              <label className="form-label">Preferred Language</label>
              <select
                className="form-control"
                value={preferredLanguage}
                onChange={(e) => setPreferredLanguage(e.target.value)}
              >
                <option value="en">English</option>
                <option value="ta">தமிழ் (Tamil)</option>
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">Account Role</label>
              <select
                className="form-control"
                value={role}
                onChange={(e) => setRole(e.target.value)}
              >
                <option value="USER">Student (User)</option>
                <option value="ADMIN">Administrator</option>
              </select>
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="btn btn-secondary"
            style={{ width: '100%', padding: '14px', fontSize: '1rem', marginTop: '10px' }}
          >
            {loading ? 'Creating Account...' : 'Register & Start Finding Schemes'}
            <ArrowRight size={18} />
          </button>
        </form>

        <div style={{ textAlign: 'center', marginTop: '24px', fontSize: '0.92rem', color: 'var(--text-muted)' }}>
          Already have an account?{' '}
          <Link to="/login" style={{ fontWeight: 700, color: 'var(--primary)' }}>
            Sign In
          </Link>
        </div>
      </div>
    </div>
  );
};

