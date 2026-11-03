import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate, Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';
import { SchemeCard } from '../components/SchemeCard';
import { Filter, Sparkles, RefreshCw, BookmarkCheck } from 'lucide-react';

export const Results = () => {
  const { t } = useTranslation();
  const location = useLocation();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  const getInitialResults = () => {
    if (location.state?.results && location.state.results.length > 0) {
      return location.state.results;
    }
    try {
      const cached = sessionStorage.getItem('last_evaluated_results');
      if (cached) return JSON.parse(cached);
    } catch (e) {}
    return [];
  };

  const getInitialProfile = () => {
    if (location.state?.profile) return location.state.profile;
    try {
      const cached = sessionStorage.getItem('last_evaluated_profile');
      if (cached) return JSON.parse(cached);
    } catch (e) {}
    return null;
  };

  const [results, setResults] = useState(getInitialResults);
  const [profile, setProfile] = useState(getInitialProfile);
  const [filterType, setFilterType] = useState('ALL'); // ALL, HIGH, ELIGIBLE
  const [savedIds, setSavedIds] = useState(new Set());
  const [loading, setLoading] = useState(false);
  const [toastMessage, setToastMessage] = useState('');

  // If page was loaded directly without results, recover from cached profile or fetch my-matches
  useEffect(() => {
    if (results.length === 0) {
      if (profile) {
        setLoading(true);
        api.post('/eligibility/evaluate', profile)
          .then((res) => {
            if (res.data?.data) {
              setResults(res.data.data);
              try {
                sessionStorage.setItem('last_evaluated_results', JSON.stringify(res.data.data));
              } catch (e) {}
            }
          })
          .catch((err) => console.error('Error re-evaluating cached profile:', err))
          .finally(() => setLoading(false));
      } else if (isAuthenticated) {
        setLoading(true);
        api.get('/eligibility/my-matches')
          .then((res) => {
            if (res.data?.data) {
              setResults(res.data.data);
            }
          })
          .catch(() => {})
          .finally(() => setLoading(false));
      }
    }
  }, [results.length, profile, isAuthenticated]);

  // Fetch student's existing saved schemes to mark "Already Saved"
  useEffect(() => {
    if (isAuthenticated) {
      api.get('/saved-schemes')
        .then((res) => {
          if (res.data?.data) {
            const ids = new Set(res.data.data.map((item) => item.scheme.id));
            setSavedIds(ids);
          }
        })
        .catch(() => {});
    }
  }, [isAuthenticated]);

  const handleSaveScheme = async (schemeId) => {
    if (!isAuthenticated) {
      // Direct user to register or login
      navigate('/login', { state: { returnTo: '/results' } });
      return;
    }

    try {
      await api.post(`/saved-schemes/${schemeId}`);
      setSavedIds((prev) => new Set([...prev, schemeId]));
      showToast('Scheme bookmarked in your tracker & document checklist!');
    } catch (err) {
      showToast('Could not save scheme. Please try again.');
    }
  };

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(''), 4000);
  };

  const filteredResults = results.filter((item) => {
    if (filterType === 'ELIGIBLE') return item.eligible;
    if (filterType === 'HIGH') return item.matchScore >= 80;
    return true;
  });

  return (
    <div className="container" style={{ padding: '40px 20px' }}>
      {toastMessage && (
        <div style={{
          position: 'fixed',
          top: '84px',
          right: '24px',
          background: '#15803d',
          color: '#ffffff',
          padding: '14px 22px',
          borderRadius: '12px',
          fontWeight: 700,
          boxShadow: '0 10px 15px -3px rgba(0,0,0,0.1)',
          zIndex: 9999
        }}>
          {toastMessage}
        </div>
      )}

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '16px', marginBottom: '24px' }}>
        <div>
          <h1 style={{ fontSize: '2rem', fontWeight: 800, color: 'var(--primary)', marginBottom: '6px' }}>
            {t('results.title')}
          </h1>
          <p style={{ color: 'var(--text-muted)' }}>
            Found {results.length} education-matched schemes. Showing schemes tailored to your profile.
          </p>
        </div>

        <div style={{ display: 'flex', gap: '10px' }}>
          <button
            onClick={() => navigate('/questionnaire')}
            className="btn btn-outline"
            style={{ fontSize: '0.9rem' }}
          >
            <RefreshCw size={16} />
            Modify Answers
          </button>
          {isAuthenticated && (
            <Link to="/tracker" className="btn btn-primary" style={{ fontSize: '0.9rem' }}>
              <BookmarkCheck size={16} />
              Open My Tracker ({savedIds.size})
            </Link>
          )}
        </div>
      </div>

      {/* Filter Tabs */}
      <div style={{ display: 'flex', gap: '10px', marginBottom: '20px' }}>
        <button
          onClick={() => setFilterType('ALL')}
          className={`btn ${filterType === 'ALL' ? 'btn-primary' : 'btn-outline'}`}
          style={{ padding: '8px 16px', fontSize: '0.9rem' }}
        >
          {t('results.filter_all')} ({results.length})
        </button>
        <button
          onClick={() => setFilterType('HIGH')}
          className={`btn ${filterType === 'HIGH' ? 'btn-primary' : 'btn-outline'}`}
          style={{ padding: '8px 16px', fontSize: '0.9rem' }}
        >
          {t('results.filter_high_match')} ({results.filter(r => r.matchScore >= 80).length})
        </button>
        <button
          onClick={() => setFilterType('ELIGIBLE')}
          className={`btn ${filterType === 'ELIGIBLE' ? 'btn-primary' : 'btn-outline'}`}
          style={{ padding: '8px 16px', fontSize: '0.9rem' }}
        >
          {t('results.filter_eligible')} ({results.filter(r => r.eligible).length})
        </button>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '60px 0' }}>
          <p style={{ fontSize: '1.2rem', color: '#64748b' }}>Evaluating schemes against criteria...</p>
        </div>
      ) : filteredResults.length > 0 ? (
        <div className="schemes-list">
          {filteredResults.map((item) => (
            <SchemeCard
              key={item.scheme.id}
              scheme={item.scheme}
              matchScore={item.matchScore}
              reasons={item.reasons}
              isEligible={item.eligible}
              isSaved={savedIds.has(item.scheme.id)}
              onSave={handleSaveScheme}
            />
          ))}
        </div>
      ) : (
        <div className="form-card" style={{ textAlign: 'center', padding: '60px 20px' }}>
          <Sparkles size={40} color="#94a3b8" style={{ marginBottom: '16px' }} />
          <h3>{t('results.no_matches')}</h3>
          <p style={{ color: 'var(--text-muted)', marginTop: '8px', marginBottom: '20px' }}>
            Try broadening your educational stream or checking schemes across other categories.
          </p>
          <button onClick={() => navigate('/schemes')} className="btn btn-primary">
            Browse All 40+ Schemes Catalog
          </button>
        </div>
      )}
    </div>
  );
};

