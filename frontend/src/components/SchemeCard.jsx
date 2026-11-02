import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { ExternalLink, Bookmark, CheckCircle2, ChevronDown, ChevronUp, Calendar, AlertCircle } from 'lucide-react';

export const SchemeCard = ({ scheme, matchScore, reasons, isSaved, onSave, isEligible }) => {
  const { t, i18n } = useTranslation();
  const [showReasons, setShowReasons] = useState(false);
  const isTamil = i18n.language === 'ta';

  const name = isTamil && scheme.nameTa ? scheme.nameTa : scheme.nameEn;
  const description = isTamil && scheme.descriptionTa ? scheme.descriptionTa : scheme.descriptionEn;

  const score = matchScore !== undefined ? Math.round(matchScore) : null;
  const badgeClass = score >= 80 ? 'match-high' : score >= 50 ? 'match-medium' : 'match-low';

  return (
    <div className="scheme-card">
      <div className="scheme-header">
        <div className="scheme-title-group">
          <div className="badge-row" style={{ marginBottom: '8px' }}>
            <span className={`badge ${scheme.level === 'Central' ? 'badge-blue' : 'badge-amber'}`}>
              {scheme.level === 'Central' ? 'Central Govt' : 'Tamil Nadu State'}
            </span>
            <span className="badge badge-purple">{scheme.benefitType}</span>
            {scheme.renewalRequired && (
              <span className="badge badge-gray">Renewal Required</span>
            )}
            {scheme.educationLevels && scheme.educationLevels.map((lvl) => (
              <span key={lvl} className="badge badge-gray">{lvl.replace('_', ' ')}</span>
            ))}
          </div>

          <h3>{name}</h3>
          <p className="dept-name">{scheme.department}</p>
        </div>

        {score !== null && (
          <div className={`match-badge ${badgeClass}`} title="Match Score">
            <div>{score}%</div>
            <div style={{ fontSize: '0.72rem', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
              {isEligible ? 'Eligible' : 'Partial'}
            </div>
          </div>
        )}
      </div>

      <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem', lineHeight: 1.6 }}>
        {description}
      </p>

      <div className="scheme-meta-grid">
        <div className="meta-item">
          <div className="meta-label">Financial Benefit</div>
          <div className="meta-value">
            {scheme.benefitAmount > 0 ? `₹${scheme.benefitAmount.toLocaleString('en-IN')}` : scheme.benefits || 'Free Item / Waiver'}
          </div>
        </div>

        <div className="meta-item">
          <div className="meta-label">Application Last Date</div>
          <div className="meta-value" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <Calendar size={14} color="#64748b" />
            {scheme.lastDate || 'Rolling / Open'}
          </div>
        </div>

        <div className="meta-item">
          <div className="meta-label">Documents Required</div>
          <div className="meta-value">
            {scheme.documents ? `${scheme.documents.length} verified documents` : 'Checklist available'}
          </div>
        </div>
      </div>

      {reasons && reasons.length > 0 && (
        <div>
          <button
            onClick={() => setShowReasons(!showReasons)}
            className="btn btn-outline"
            style={{ width: '100%', justifyContent: 'space-between', padding: '8px 14px', fontSize: '0.88rem' }}
          >
            <span style={{ display: 'flex', alignItems: 'center', gap: '6px', fontWeight: 600 }}>
              <AlertCircle size={16} color={score >= 80 ? '#16a34a' : '#d97706'} />
              {t('results.reasons_title')} ({reasons.length})
            </span>
            {showReasons ? <ChevronUp size={16} /> : <ChevronDown size={16} />}
          </button>

          {showReasons && (
            <div className="reasons-box">
              <ul className="reasons-list">
                {reasons.map((r, idx) => (
                  <li key={idx} className={r.startsWith('✓') ? 'reason-matched' : 'reason-unmet'}>
                    {r}
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>
      )}

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '12px', marginTop: '4px' }}>
        <Link to={`/schemes/${scheme.id}`} className="btn btn-outline" style={{ fontSize: '0.9rem' }}>
          {t('results.view_details')}
        </Link>

        <div style={{ display: 'flex', gap: '10px' }}>
          {onSave && (
            <button
              onClick={() => onSave(scheme.id)}
              disabled={isSaved}
              className={`btn ${isSaved ? 'btn-outline' : 'btn-secondary'}`}
              style={{ fontSize: '0.9rem' }}
            >
              {isSaved ? (
                <>
                  <CheckCircle2 size={16} color="#16a34a" />
                  {t('results.already_saved')}
                </>
              ) : (
                <>
                  <Bookmark size={16} />
                  {t('results.save_scheme')}
                </>
              )}
            </button>
          )}

          {scheme.applyLink && (
            <a
              href={scheme.applyLink}
              target="_blank"
              rel="noreferrer"
              className="btn btn-primary"
              style={{ fontSize: '0.9rem' }}
            >
              {t('results.apply_now')}
              <ExternalLink size={15} />
            </a>
          )}
        </div>
      </div>
    </div>
  );
};

