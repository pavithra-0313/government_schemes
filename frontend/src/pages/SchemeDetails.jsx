import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';
import { Calendar, Bookmark, CheckCircle2, ExternalLink, ArrowLeft, Building2, Award, FileText, Check, ShieldAlert } from 'lucide-react';

export const SchemeDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { t, i18n } = useTranslation();
  const { isAuthenticated } = useAuth();

  const [scheme, setScheme] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isSaved, setIsSaved] = useState(false);
  const [toast, setToast] = useState('');

  const isTamil = i18n.language === 'ta';

  useEffect(() => {
    setLoading(true);
    api.get(`/schemes/${id}`)
      .then((res) => {
        if (res.data?.data) {
          setScheme(res.data.data);
        }
      })
      .catch((err) => {
        console.error('Error fetching scheme:', err);
      })
      .finally(() => setLoading(false));

    if (isAuthenticated) {
      api.get('/saved-schemes')
        .then((res) => {
          if (res.data?.data) {
            const saved = res.data.data.some((item) => item.scheme.id === Number(id));
            setIsSaved(saved);
          }
        })
        .catch(() => {});
    }
  }, [id, isAuthenticated]);

  const handleSave = async () => {
    if (!isAuthenticated) {
      navigate('/login', { state: { returnTo: `/schemes/${id}` } });
      return;
    }

    try {
      await api.post(`/saved-schemes/${id}`);
      setIsSaved(true);
      setToast('Scheme saved to tracker & checklist initialized!');
      setTimeout(() => setToast(''), 4000);
    } catch (err) {
      setToast('Failed to save scheme.');
    }
  };

  if (loading) {
    return (
      <div className="container" style={{ padding: '80px 20px', textAlign: 'center' }}>
        <p style={{ fontSize: '1.2rem', color: '#64748b' }}>Loading scheme details...</p>
      </div>
    );
  }

  if (!scheme) {
    return (
      <div className="container" style={{ padding: '80px 20px', textAlign: 'center' }}>
        <h2>Scheme not found</h2>
        <button onClick={() => navigate('/schemes')} className="btn btn-primary" style={{ marginTop: '20px' }}>
          Back to Schemes Catalog
        </button>
      </div>
    );
  }

  const name = isTamil && scheme.nameTa ? scheme.nameTa : scheme.nameEn;
  const description = isTamil && scheme.descriptionTa ? scheme.descriptionTa : scheme.descriptionEn;
  const rule = scheme.eligibilityRule;

  return (
    <div className="container" style={{ padding: '40px 20px', maxWidth: '960px' }}>
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

      <button onClick={() => navigate(-1)} className="btn btn-outline" style={{ marginBottom: '20px' }}>
        <ArrowLeft size={16} />
        Back
      </button>

      <div className="form-card">
        <div className="badge-row" style={{ marginBottom: '12px' }}>
          <span className={`badge ${scheme.level === 'Central' ? 'badge-blue' : 'badge-amber'}`}>
            {scheme.level === 'Central' ? 'Central Government Scheme' : 'Tamil Nadu State Government Scheme'}
          </span>
          <span className="badge badge-purple">{scheme.benefitType}</span>
          {scheme.renewalRequired && (
            <span className="badge badge-gray">Annual Renewal Required</span>
          )}
        </div>

        <h1 style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--primary)', marginBottom: '10px' }}>
          {name}
        </h1>

        <p style={{ display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--text-muted)', fontSize: '0.95rem', marginBottom: '24px' }}>
          <Building2 size={18} />
          {scheme.department}
        </p>

        <p style={{ fontSize: '1.1rem', lineHeight: 1.7, color: '#334155', marginBottom: '28px' }}>
          {description}
        </p>

        {/* Highlight Grid */}
        <div className="scheme-meta-grid" style={{ marginBottom: '32px' }}>
          <div className="meta-item">
            <div className="meta-label">Financial Benefit</div>
            <div className="meta-value" style={{ color: '#15803d', fontSize: '1.25rem' }}>
              {scheme.benefitAmount > 0 ? `₹${scheme.benefitAmount.toLocaleString('en-IN')}` : scheme.benefits}
            </div>
          </div>

          <div className="meta-item">
            <div className="meta-label">Application Period</div>
            <div className="meta-value" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
              <Calendar size={16} color="#64748b" />
              {scheme.openDate || 'Open'} to {scheme.lastDate || 'Closing date not fixed'}
            </div>
          </div>

          <div className="meta-item">
            <div className="meta-label">Applicable Level(s)</div>
            <div className="meta-value" style={{ fontSize: '0.95rem' }}>
              {scheme.educationLevels?.map(l => l.replace('_', ' ')).join(', ') || 'All levels'}
            </div>
          </div>
        </div>

        {/* Benefits Section */}
        <div style={{ marginBottom: '32px' }}>
          <h3 style={{ fontSize: '1.3rem', fontWeight: 700, marginBottom: '12px', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Award size={22} color="var(--primary)" />
            Benefits & Entitlements
          </h3>
          <p style={{ fontSize: '1rem', color: '#475569', lineHeight: 1.6, background: '#f8fafc', padding: '16px', borderRadius: '10px' }}>
            {scheme.benefits || 'Financial assistance, fee waivers, and course maintenance allowances as specified.'}
          </p>
        </div>

        {/* Eligibility Criteria Breakdown */}
        {rule && (
          <div style={{ marginBottom: '32px' }}>
            <h3 style={{ fontSize: '1.3rem', fontWeight: 700, marginBottom: '14px', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <ShieldAlert size={22} color="var(--accent)" />
              Eligibility Criteria
            </h3>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))', gap: '14px' }}>
              {rule.maxIncome && (
                <div style={{ background: '#f8fafc', padding: '14px', borderRadius: '10px', border: '1px solid var(--border)' }}>
                  <div style={{ fontSize: '0.8rem', color: '#64748b', fontWeight: 700 }}>ANNUAL FAMILY INCOME</div>
                  <div style={{ fontWeight: 700, marginTop: '4px' }}>Maximum ₹{rule.maxIncome.toLocaleString('en-IN')}</div>
                </div>
              )}
              {rule.minPercentage && (
                <div style={{ background: '#f8fafc', padding: '14px', borderRadius: '10px', border: '1px solid var(--border)' }}>
                  <div style={{ fontSize: '0.8rem', color: '#64748b', fontWeight: 700 }}>MINIMUM ACADEMIC SCORE</div>
                  <div style={{ fontWeight: 700, marginTop: '4px' }}>{rule.minPercentage}% in previous exam</div>
                </div>
              )}
              {rule.gender && (
                <div style={{ background: '#f8fafc', padding: '14px', borderRadius: '10px', border: '1px solid var(--border)' }}>
                  <div style={{ fontSize: '0.8rem', color: '#64748b', fontWeight: 700 }}>GENDER REQUIREMENT</div>
                  <div style={{ fontWeight: 700, marginTop: '4px' }}>Exclusively for {rule.gender} applicants</div>
                </div>
              )}
              {rule.communityCategory && (
                <div style={{ background: '#f8fafc', padding: '14px', borderRadius: '10px', border: '1px solid var(--border)' }}>
                  <div style={{ fontSize: '0.8rem', color: '#64748b', fontWeight: 700 }}>COMMUNITY CATEGORY</div>
                  <div style={{ fontWeight: 700, marginTop: '4px' }}>{rule.communityCategory}</div>
                </div>
              )}
              {rule.minorityRequired && (
                <div style={{ background: '#f8fafc', padding: '14px', borderRadius: '10px', border: '1px solid var(--border)' }}>
                  <div style={{ fontSize: '0.8rem', color: '#64748b', fontWeight: 700 }}>RELIGIOUS MINORITY</div>
                  <div style={{ fontWeight: 700, marginTop: '4px' }}>Notified Minority Communities</div>
                </div>
              )}
              {rule.singleGirlChildRequired && (
                <div style={{ background: '#f8fafc', padding: '14px', borderRadius: '10px', border: '1px solid var(--border)' }}>
                  <div style={{ fontSize: '0.8rem', color: '#64748b', fontWeight: 700 }}>FAMILY CRITERIA</div>
                  <div style={{ fontWeight: 700, marginTop: '4px' }}>Only Single Girl Child</div>
                </div>
              )}
              {rule.firstGraduateRequired && (
                <div style={{ background: '#f8fafc', padding: '14px', borderRadius: '10px', border: '1px solid var(--border)' }}>
                  <div style={{ fontSize: '0.8rem', color: '#64748b', fontWeight: 700 }}>GRADUATE STATUS</div>
                  <div style={{ fontWeight: 700, marginTop: '4px' }}>First Graduate in Family</div>
                </div>
              )}
              {rule.institutionType && (
                <div style={{ background: '#f8fafc', padding: '14px', borderRadius: '10px', border: '1px solid var(--border)' }}>
                  <div style={{ fontSize: '0.8rem', color: '#64748b', fontWeight: 700 }}>INSTITUTION TYPE</div>
                  <div style={{ fontWeight: 700, marginTop: '4px' }}>{rule.institutionType}</div>
                </div>
              )}
            </div>
          </div>
        )}

        {/* Required Documents Section */}
        {scheme.documents && scheme.documents.length > 0 && (
          <div style={{ marginBottom: '32px' }}>
            <h3 style={{ fontSize: '1.3rem', fontWeight: 700, marginBottom: '14px', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <FileText size={22} color="var(--primary)" />
              {t('details.docs_required')} ({scheme.documents.length})
            </h3>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '12px' }}>
              {scheme.documents.map((doc) => (
                <div key={doc.id} style={{ display: 'flex', alignItems: 'center', gap: '12px', background: '#f0fdf4', padding: '12px 16px', borderRadius: '10px', border: '1px solid #bbf7d0' }}>
                  <Check size={18} color="#15803d" />
                  <div>
                    <div style={{ fontWeight: 700, fontSize: '0.95rem' }}>{doc.nameEn}</div>
                    <div style={{ fontSize: '0.82rem', color: '#166534' }}>{doc.nameTa}</div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Action Buttons */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '16px', paddingTop: '20px', borderTop: '1px solid var(--border)' }}>
          <button
            onClick={handleSave}
            disabled={isSaved}
            className={`btn ${isSaved ? 'btn-outline' : 'btn-secondary'} btn-large`}
          >
            {isSaved ? (
              <>
                <CheckCircle2 size={20} color="#15803d" />
                Saved in Your Tracker
              </>
            ) : (
              <>
                <Bookmark size={20} />
                Save Scheme & Build Checklist
              </>
            )}
          </button>

          <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
            {scheme.applyLink && (
              <a
                href={scheme.applyLink}
                target="_blank"
                rel="noreferrer"
                className="btn btn-primary btn-large"
              >
                {t('details.apply_button')}
                <ExternalLink size={20} />
              </a>
            )}

            {/* Universal Fallback Portal */}
            <a
              href={scheme.level === 'Central' ? 'https://scholarships.gov.in' : 'https://www.tn.gov.in'}
              target="_blank"
              rel="noreferrer"
              className="btn btn-outline btn-large"
              title="Backup government gateway if specific department site is busy"
            >
              {scheme.level === 'Central' ? 'NSP Portal (Backup)' : 'TN Govt Portal (Backup)'}
              <ExternalLink size={18} />
            </a>
          </div>
        </div>
      </div>
    </div>
  );
};

