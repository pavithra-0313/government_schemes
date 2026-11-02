import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import api from '../api/axios';
import { FileCheck, Printer, CheckCircle, Clock, Info, ArrowLeft, Download } from 'lucide-react';

export const DocumentChecklistPage = () => {
  const { t, i18n } = useTranslation();
  const [checklist, setChecklist] = useState([]);
  const [loading, setLoading] = useState(true);
  const [updatingId, setUpdatingId] = useState(null);

  const isTamil = i18n.language === 'ta';

  const fetchChecklist = () => {
    setLoading(true);
    api.get('/checklist')
      .then((res) => {
        setChecklist(res.data?.data || []);
      })
      .catch((err) => {
        console.error('Error fetching checklist:', err);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchChecklist();
  }, []);

  const handleToggle = async (item) => {
    setUpdatingId(item.id);
    const newStatus = !item.isReady;
    try {
      await api.put(`/checklist/${item.schemeId}/${item.document.id}`, { isReady: newStatus });
      setChecklist((prev) =>
        prev.map((i) => (i.id === item.id ? { ...i, isReady: newStatus } : i))
      );
    } catch (err) {
      console.error('Failed to toggle document ready state:', err);
    } finally {
      setUpdatingId(null);
    }
  };

  const handlePrint = () => {
    window.print();
  };

  // Group checklist items by scheme
  const groupedByScheme = checklist.reduce((acc, item) => {
    const key = item.schemeId;
    if (!acc[key]) {
      acc[key] = {
        schemeId: item.schemeId,
        schemeNameEn: item.schemeNameEn,
        schemeNameTa: item.schemeNameTa,
        items: []
      };
    }
    acc[key].items.push(item);
    return acc;
  }, {});

  const totalDocs = checklist.length;
  const readyDocs = checklist.filter((i) => i.isReady).length;
  const progressPercent = totalDocs > 0 ? Math.round((readyDocs / totalDocs) * 100) : 0;

  return (
    <div className="container" style={{ padding: '40px 20px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '16px', marginBottom: '24px' }}>
        <div>
          <h1 style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--primary)', marginBottom: '8px' }}>
            {t('checklist.title')}
          </h1>
          <p style={{ color: 'var(--text-muted)' }}>
            {t('checklist.subtitle')}
          </p>
        </div>

        <div style={{ display: 'flex', gap: '10px' }} className="no-print">
          <button onClick={handlePrint} className="btn btn-primary">
            <Printer size={18} />
            {t('checklist.print_pdf')}
          </button>
        </div>
      </div>

      {/* Readiness Progress Bar */}
      {totalDocs > 0 && (
        <div className="form-card" style={{ padding: '20px', marginBottom: '24px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }}>
            <span style={{ fontWeight: 700, fontSize: '1.05rem', color: 'var(--text-dark)' }}>
              Overall Readiness: {readyDocs} of {totalDocs} Certificates Collected
            </span>
            <span style={{ fontWeight: 800, color: progressPercent === 100 ? '#15803d' : 'var(--primary)', fontSize: '1.1rem' }}>
              {progressPercent}% Complete
            </span>
          </div>
          <div style={{ width: '100%', height: '12px', background: '#e2e8f0', borderRadius: '10px', overflow: 'hidden' }}>
            <div style={{
              width: `${progressPercent}%`,
              height: '100%',
              background: progressPercent === 100 ? '#16a34a' : 'var(--primary)',
              transition: 'width 0.4s ease'
            }} />
          </div>
        </div>
      )}

      <div className="school-friendly-banner" style={{ background: '#ecfdf5', borderColor: '#10b981', color: '#065f46', marginBottom: '28px' }}>
        <Info size={20} style={{ flexShrink: 0 }} />
        <span>{t('checklist.tip')}</span>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '60px 0' }}>
          <p style={{ color: '#64748b', fontSize: '1.2rem' }}>Loading checklist...</p>
        </div>
      ) : Object.keys(groupedByScheme).length > 0 ? (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '28px' }}>
          {Object.values(groupedByScheme).map((schemeGroup) => {
            const sName = isTamil && schemeGroup.schemeNameTa ? schemeGroup.schemeNameTa : schemeGroup.schemeNameEn;
            const groupReady = schemeGroup.items.filter(i => i.isReady).length;
            const groupTotal = schemeGroup.items.length;

            return (
              <div key={schemeGroup.schemeId} className="form-card" style={{ padding: '24px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px', borderBottom: '1px solid var(--border)', paddingBottom: '12px' }}>
                  <h3 style={{ fontSize: '1.3rem', fontWeight: 800, color: 'var(--primary)' }}>
                    <Link to={`/schemes/${schemeGroup.schemeId}`}>{sName}</Link>
                  </h3>
                  <span className={`badge ${groupReady === groupTotal ? 'badge-green' : 'badge-amber'}`} style={{ fontSize: '0.88rem' }}>
                    {groupReady} / {groupTotal} Ready
                  </span>
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                  {schemeGroup.items.map((item) => (
                    <div
                      key={item.id}
                      onClick={() => handleToggle(item)}
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'space-between',
                        padding: '14px 18px',
                        borderRadius: '10px',
                        border: '1.5px solid',
                        borderColor: item.isReady ? '#86efac' : 'var(--border)',
                        background: item.isReady ? '#f0fdf4' : '#ffffff',
                        cursor: 'pointer',
                        transition: 'all 0.2s ease'
                      }}
                    >
                      <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
                        <input
                          type="checkbox"
                          checked={item.isReady}
                          onChange={() => {}} // handled by wrapper click
                          style={{ width: '20px', height: '20px', accentColor: '#16a34a', cursor: 'pointer' }}
                        />
                        <div>
                          <div style={{
                            fontWeight: 700,
                            fontSize: '1rem',
                            color: item.isReady ? '#15803d' : 'var(--text-dark)',
                            textDecoration: item.isReady ? 'line-through' : 'none'
                          }}>
                            {item.document.nameEn}
                          </div>
                          <div style={{ fontSize: '0.85rem', color: '#64748b' }}>
                            {item.document.nameTa}
                          </div>
                        </div>
                      </div>

                      <span className={`badge ${item.isReady ? 'badge-green' : 'badge-gray'}`}>
                        {item.isReady ? t('checklist.ready') : t('checklist.pending')}
                      </span>
                    </div>
                  ))}
                </div>
              </div>
            );
          })}
        </div>
      ) : (
        <div className="form-card" style={{ textAlign: 'center', padding: '60px 20px' }}>
          <FileCheck size={48} color="#94a3b8" style={{ marginBottom: '16px' }} />
          <h3>No document checklists active yet</h3>
          <p style={{ color: 'var(--text-muted)', marginTop: '8px', marginBottom: '20px' }}>
            Save schemes from your eligibility results to automatically populate your document verification checklists.
          </p>
          <Link to="/questionnaire" className="btn btn-primary">
            Find Schemes & Build Checklist
          </Link>
        </div>
      )}
    </div>
  );
};

