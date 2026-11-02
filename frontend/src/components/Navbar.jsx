import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../context/AuthContext';
import { GraduationCap, Globe, User, LogOut, Calendar, BookmarkCheck, FileCheck, ShieldCheck } from 'lucide-react';

export const Navbar = () => {
  const { t, i18n } = useTranslation();
  const { user, isAuthenticated, isAdmin, logout, changeLanguage } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const toggleLanguage = () => {
    const nextLang = i18n.language === 'ta' ? 'en' : 'ta';
    changeLanguage(nextLang);
  };

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <header className="navbar">
      <div className="container nav-container">
        <Link to="/" className="brand">
          <GraduationCap size={32} />
          <span>{t('nav.title')}</span>
        </Link>

        <nav>
          <ul className="nav-links">
            <li>
              <Link to="/" className={`nav-link ${location.pathname === '/' ? 'active' : ''}`}>
                {t('nav.home')}
              </Link>
            </li>
            <li>
              <Link to="/schemes" className={`nav-link ${location.pathname === '/schemes' ? 'active' : ''}`}>
                {t('nav.schemes')}
              </Link>
            </li>
            <li>
              <Link to="/calendar" className={`nav-link ${location.pathname === '/calendar' ? 'active' : ''}`}>
                <Calendar size={18} />
                {t('nav.calendar')}
              </Link>
            </li>

            {isAuthenticated ? (
              <>
                <li>
                  <Link to="/tracker" className={`nav-link ${location.pathname === '/tracker' ? 'active' : ''}`}>
                    <BookmarkCheck size={18} />
                    {t('nav.saved')}
                  </Link>
                </li>
                <li>
                  <Link to="/checklist" className={`nav-link ${location.pathname === '/checklist' ? 'active' : ''}`}>
                    <FileCheck size={18} />
                    {t('nav.checklist')}
                  </Link>
                </li>
                <li>
                  <Link to="/profile" className={`nav-link ${location.pathname === '/profile' ? 'active' : ''}`}>
                    <User size={18} />
                    {t('nav.profile')}
                  </Link>
                </li>
                {isAdmin && (
                  <li>
                    <Link to="/admin" className={`nav-link ${location.pathname === '/admin' ? 'active' : ''}`}>
                      <ShieldCheck size={18} />
                      {t('nav.admin')}
                    </Link>
                  </li>
                )}
                <li>
                  <button onClick={handleLogout} className="btn btn-outline" style={{ padding: '6px 12px' }}>
                    <LogOut size={16} />
                    {t('nav.logout')}
                  </button>
                </li>
              </>
            ) : (
              <>
                <li>
                  <Link to="/login" className="btn btn-outline" style={{ padding: '6px 14px' }}>
                    {t('nav.login')}
                  </Link>
                </li>
                <li>
                  <Link to="/register" className="btn btn-primary" style={{ padding: '6px 14px' }}>
                    {t('nav.register')}
                  </Link>
                </li>
              </>
            )}

            <li>
              <button onClick={toggleLanguage} className="lang-toggle-btn" title="Switch Language / மொழியை மாற்றுக">
                <Globe size={16} />
                <span>{i18n.language === 'ta' ? 'English' : 'தமிழ்'}</span>
              </button>
            </li>
          </ul>
        </nav>
      </div>
    </header>
  );
};

