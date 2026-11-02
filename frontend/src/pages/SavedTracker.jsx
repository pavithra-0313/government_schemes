import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import api from '../api/axios';
import { Bookmark, FileCheck, ExternalLink, Trash2, CheckCircle2, Clock } from 'lucide-react';

export const SavedTracker = () => {
  const { t, i18n } = useTranslation();
  const [savedSchemes, setSavedSchemes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [toast, setToast] = useState('');

  const isTamil = i18n.language === 'ta';

  const fetchSaved = () => {
    setLoading(true);
    api.get('/saved-schemes')
      .then((res) => {
        setSavedSchemes(res.data?.data || []);
      })
      .catch((err) => {
        console.error('Error fetching tracker items:', err);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchSaved();
  }, []);

  const handleStatusChange = async (schemeId, newStatus) => {
    try {
      await api.patch(`/saved-schemes/${schemeId}/status`, { status: newStatus });
      setSavedSchemes((prev) =>
        prev.map((item) =>
          item.scheme.id === schemeId ? { ...item, applicationStatus: newStatus } : item
        )
      );
      setToast(`Status updated to "${newStatus}"`);
      setTimeout(() => setToast(''), 3000);
    } catch {
      setToast('Failed to update status.');
    }
  };

  const handleRemove = async (schemeId) => {
    if (!window.confirm('Remove this scheme from your application tracker?')) return;
    try {
      await api.delete(`/saved-schemes/${schemeId}`);
      setSavedSchemes((prev) => prev.filter((item) => item.scheme.id !== schemeId));
      setToast('Scheme removed from tracker.');
      setTimeout(() => setToast(''), 3000);
    } catch {
      setToast('Failed to remove scheme.');
    }
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
          padding: '12px 20px',
          borderRadius: '10px',
          fontWeight: 700,
          boxShadow: 'var(--shadow)',
          zIndex: 9999
        }}>
          {toast}
        </div>
      )}

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '16px', marginBottom: '28px' }}>
        <div>
          <h1 style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--primary)', marginBottom: '8px' }}>
            {t('tracker.title')}
          </h1>
          <p style={{ color: 'var(--text-muted)' }}>
            {t('tracker.subtitle')}
          </p>
        </div>

        <Link to="/checklist" className="btn btn-secondary">
          <FileCheck size={18} />
          View All Document Checklists
        </Link>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '60px 0' }}>
          <p style={{ color: '#64748b', fontSize: '1.2rem' }}>Loading your tracked applications...</p>
        </div>
      ) : savedSchemes.length > 0 ? (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {savedSchemes.map((item) => {
            const scheme = item.scheme;
            const name = isTamil && scheme.nameTa ? scheme.nameTa : scheme.nameEn;

            const statusColors = {
              'Saved': { bg: '#f1f5f9', color: '#475569' },
              'In Progress': { bg: '#fef3c7', color: '#b45309' },
              'Applied': { bg: '#dbeafe', color: '#1d4ed8' },
              'Approved': { bg: '#dcfce7', color: '#15803d' },
              'Rejected': { bg: '#fee2e2', color: '#b91c1c' }
            };

            const currentColor = statusColors[item.applicationStatus] || statusColors['Saved'];

            return (
              <div key={item.id} className="form-card" style={{ padding: '24px', marginBottom: 0 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '16px' }}>
                  <div style={{ flex: 1, minWidth: '280px' }}>
                    <div className="badge-row" style={{ marginBottom: '8px' }}>
                      <span className={`badge ${scheme.level === 'Central' ? 'badge-blue' : 'badge-amber'}`}>
                        {scheme.level}
                      </span>
                      <span className="badge badge-purple">{scheme.benefitType}</span>
                    </div>

                    <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: 'var(--primary)', marginBottom: '6px' }}>
                      <Link to={`/schemes/${scheme.id}`}>{name}</Link>
                    </h3>

                    <p style={{ fontSize: '0.88rem', color: '#64748b', marginBottom: '12px' }}>
                      {scheme.department} • Benefit: {scheme.benefitAmount > 0 ? `₹${scheme.benefitAmount.toLocaleString('en-IN')}` : scheme.benefits}
                    </p>

                    <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
                      <Link to="/checklist" className="btn btn-outline" style={{ padding: '6px 12px', fontSize: '0.85rem' }}>
                        <FileCheck size={15} color="#059669" />
                        Document Checklist
                      </Link>

                      {scheme.applyLink && (
                        <a
                          href={scheme.applyLink}
                          target="_blank"
                          rel="noreferrer"
                          className="btn btn-primary"
                          style={{ padding: '6px 12px', fontSize: '0.85rem' }}
                        >
                          Application Portal
                          <ExternalLink size={14} />
                        </a>
                      )}
                    </div>
                  </div>

                  {/* Status Dropdown and Actions */}
                  <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: '10px' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      <span style={{ fontSize: '0.88rem', fontWeight: 700, color: '#475569' }}>Status:</span>
                      <select
                        value={item.applicationStatus}
                        onChange={(e) => handleStatusChange(scheme.id, e.target.value)}
                        className="form-control"
                        style={{
                          width: '160px',
                          padding: '8px 12px',
                          fontWeight: 700,
                          backgroundColor: currentColor.bg,
                          color: currentColor.color,
                          borderColor: currentColor.color
                        }}
                      >
                        <option value="Saved">{t('tracker.saved')}</option>
                        <option value="In Progress">{t('tracker.in_progress')}</option>
                        <option value="Applied">{t('tracker.applied')}</option>
                        <option value="Approved">{t('tracker.approved')}</option>
                        <option value="Rejected">{t('tracker.rejected')}</option>
                      </select>
                    </div>

                    <button
                      onClick={() => handleRemove(scheme.id)}
                      className="btn btn-outline"
                      style={{ padding: '6px 12px', fontSize: '0.82rem', color: '#dc2626', borderColor: '#fca5a5' }}
                      title="Remove from tracker"
                    >
                      <Trash2 size={14} />
                      {t('tracker.remove')}
                    </button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      ) : (
        <div className="form-card" style={{ textAlign: 'center', padding: '60px 20px' }}>
          <Bookmark size={48} color="#94a3b8" style={{ marginBottom: '16px' }} />
          <h3>{t('tracker.empty')}</h3>
          <p style={{ color: 'var(--text-muted)', marginTop: '8px', marginBottom: '20px' }}>
            Discover scholarships matching your education level and click "Save to Tracker" to keep track here.
          </p>
          <Link to="/questionnaire" className="btn btn-primary">
            Run Eligibility Calculator
          </Link>
        </div>
      )}
    </div>
  );
};

