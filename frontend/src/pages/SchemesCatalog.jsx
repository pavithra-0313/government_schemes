import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';
import { SchemeCard } from '../components/SchemeCard';
import { Search, Filter, RefreshCw } from 'lucide-react';

export const SchemesCatalog = () => {
  const { t } = useTranslation();
  const [searchParams, setSearchParams] = useSearchParams();
  const { isAuthenticated } = useAuth();

  const [schemes, setSchemes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [savedIds, setSavedIds] = useState(new Set());
  const [toast, setToast] = useState('');

  // Filters state
  const [keyword, setKeyword] = useState(searchParams.get('keyword') || '');
  const [educationLevel, setEducationLevel] = useState(searchParams.get('level') || '');
  const [schemeLevel, setSchemeLevel] = useState('');
  const [benefitType, setBenefitType] = useState('');
  const [state, setState] = useState('');

  const fetchSchemes = () => {
    setLoading(true);
    const params = {};
    if (keyword) params.keyword = keyword;
    if (educationLevel) params.educationLevel = educationLevel;
    if (schemeLevel) params.schemeLevel = schemeLevel;
    if (benefitType) params.benefitType = benefitType;
    if (state) params.state = state;

    api.get('/schemes', { params })
      .then((res) => {
        setSchemes(res.data?.data || []);
      })
      .catch((err) => {
        console.error('Error fetching schemes:', err);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchSchemes();
  }, [educationLevel, schemeLevel, benefitType, state]);

  useEffect(() => {
    if (isAuthenticated) {
      api.get('/saved-schemes')
        .then((res) => {
          if (res.data?.data) {
            setSavedIds(new Set(res.data.data.map(i => i.scheme.id)));
          }
        })
        .catch(() => {});
    }
  }, [isAuthenticated]);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    fetchSchemes();
  };

  const handleSave = async (schemeId) => {
    if (!isAuthenticated) {
      window.location.href = '/login?returnTo=/schemes';
      return;
    }
    try {
      await api.post(`/saved-schemes/${schemeId}`);
      setSavedIds(prev => new Set([...prev, schemeId]));
      setToast('Scheme saved to tracker!');
      setTimeout(() => setToast(''), 4000);
    } catch {
      setToast('Error saving scheme.');
    }
  };

  const handleResetFilters = () => {
    setKeyword('');
    setEducationLevel('');
    setSchemeLevel('');
    setBenefitType('');
    setState('');
    setSearchParams({});
    api.get('/schemes').then(res => setSchemes(res.data?.data || []));
  };

  return (
    <div className="container" style={{ padding: '40px 20px' }}>
      {toast && (
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
          {toast}
        </div>
      )}

      <div style={{ marginBottom: '30px' }}>
        <h1 style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--primary)', marginBottom: '8px' }}>
          Explore All Scholarships & Schemes
        </h1>
        <p style={{ color: 'var(--text-muted)' }}>
          Comprehensive catalog of {schemes.length} active Central and Tamil Nadu schemes.
        </p>
      </div>

      {/* Filter Panel */}
      <div className="form-card" style={{ padding: '24px', marginBottom: '30px' }}>
        <form onSubmit={handleSearchSubmit}>
          <div style={{ display: 'flex', gap: '12px', marginBottom: '18px' }}>
            <div style={{ position: 'relative', flex: 1 }}>
              <input
                type="text"
                className="form-control"
                placeholder="Search by scheme name or keywords..."
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
                style={{ paddingLeft: '42px' }}
              />
              <Search size={18} color="#94a3b8" style={{ position: 'absolute', left: '14px', top: '15px' }} />
            </div>
            <button type="submit" className="btn btn-primary">Search</button>
            <button type="button" onClick={handleResetFilters} className="btn btn-outline" title="Reset Filters">
              <RefreshCw size={16} />
            </button>
          </div>

          <div className="form-grid-3">
            <div>
              <label className="form-label" style={{ fontSize: '0.85rem' }}>Education Level</label>
              <select
                className="form-control"
                value={educationLevel}
                onChange={(e) => setEducationLevel(e.target.value)}
              >
                <option value="">All Education Levels</option>
                <option value="SCHOOL_PRIMARY">School: Primary (1-5)</option>
                <option value="SCHOOL_MIDDLE">School: Middle (6-8)</option>
                <option value="SCHOOL_SECONDARY">School: Secondary (9-10)</option>
                <option value="SCHOOL_HIGHER_SECONDARY">School: Higher Secondary (11-12)</option>
                <option value="DIPLOMA">Diploma / ITI</option>
                <option value="UNDERGRADUATE">Undergraduate (UG)</option>
                <option value="POSTGRADUATE">Postgraduate (PG)</option>
                <option value="RESEARCH">Research (M.Phil/Ph.D)</option>
              </select>
            </div>

            <div>
              <label className="form-label" style={{ fontSize: '0.85rem' }}>Government Level</label>
              <select
                className="form-control"
                value={schemeLevel}
                onChange={(e) => setSchemeLevel(e.target.value)}
              >
                <option value="">Central & State (All)</option>
                <option value="Central">Central Government</option>
                <option value="State">Tamil Nadu State Government</option>
              </select>
            </div>

            <div>
              <label className="form-label" style={{ fontSize: '0.85rem' }}>Benefit Type</label>
              <select
                className="form-control"
                value={benefitType}
                onChange={(e) => setBenefitType(e.target.value)}
              >
                <option value="">All Benefit Types</option>
                <option value="Scholarship">Scholarship</option>
                <option value="Fee Waiver">Fee Waiver / Concession</option>
                <option value="Loan">Education Loan</option>
                <option value="Skill Training">Skill Training Course</option>
                <option value="Free Items">Free Items (Bicycle, Laptop, Breakfast)</option>
              </select>
            </div>
          </div>
        </form>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '60px 0' }}>
          <p style={{ color: '#64748b', fontSize: '1.2rem' }}>Loading schemes...</p>
        </div>
      ) : schemes.length > 0 ? (
        <div className="schemes-list">
          {schemes.map((scheme) => (
            <SchemeCard
              key={scheme.id}
              scheme={scheme}
              isSaved={savedIds.has(scheme.id)}
              onSave={handleSave}
            />
          ))}
        </div>
      ) : (
        <div className="form-card" style={{ textAlign: 'center', padding: '60px 20px' }}>
          <h3>No schemes matched your search criteria</h3>
          <p style={{ color: 'var(--text-muted)', marginTop: '8px', marginBottom: '20px' }}>
            Try resetting your filters to explore all available schemes.
          </p>
          <button onClick={handleResetFilters} className="btn btn-primary">
            Reset All Filters
          </button>
        </div>
      )}
    </div>
  );
};

