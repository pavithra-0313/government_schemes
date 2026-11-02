import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import api from '../api/axios';
import { Calendar, Clock, ExternalLink, Bookmark, CheckCircle, AlertTriangle } from 'lucide-react';

export const ScholarshipCalendar = () => {
  const { t, i18n } = useTranslation();
  const [events, setEvents] = useState([]);
  const [filter, setFilter] = useState('ALL'); // ALL, CLOSING_SOON, SAVED_ONLY
  const [loading, setLoading] = useState(true);

  const isTamil = i18n.language === 'ta';

  useEffect(() => {
    setLoading(true);
    api.get('/calendar')
      .then((res) => {
        setEvents(res.data?.data || []);
      })
      .catch((err) => {
        console.error('Error fetching calendar:', err);
      })
      .finally(() => setLoading(false));
  }, []);

  const filteredEvents = events.filter((ev) => {
    if (filter === 'CLOSING_SOON') {
      return ev.eventType === 'CLOSING' && ev.daysRemaining >= 0 && ev.daysRemaining <= 45;
    }
    if (filter === 'SAVED_ONLY') {
      return ev.saved;
    }
    return true;
  });

  return (
    <div className="container" style={{ padding: '40px 20px' }}>
      <div style={{ marginBottom: '28px' }}>
        <h1 style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--primary)', marginBottom: '8px' }}>
          {t('calendar.title')}
        </h1>
        <p style={{ color: 'var(--text-muted)' }}>
          {t('calendar.subtitle')}
        </p>
      </div>

      {/* Filter Tabs */}
      <div style={{ display: 'flex', gap: '10px', marginBottom: '24px' }}>
        <button
          onClick={() => setFilter('ALL')}
          className={`btn ${filter === 'ALL' ? 'btn-primary' : 'btn-outline'}`}
        >
          {t('calendar.all_deadlines')} ({events.length})
        </button>
        <button
          onClick={() => setFilter('CLOSING_SOON')}
          className={`btn ${filter === 'CLOSING_SOON' ? 'btn-primary' : 'btn-outline'}`}
        >
          <Clock size={16} />
          {t('calendar.closing_soon')}
        </button>
        <button
          onClick={() => setFilter('SAVED_ONLY')}
          className={`btn ${filter === 'SAVED_ONLY' ? 'btn-primary' : 'btn-outline'}`}
        >
          <Bookmark size={16} />
          My Tracked Deadlines
        </button>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '60px 0' }}>
          <p style={{ color: '#64748b', fontSize: '1.2rem' }}>Loading scholarship calendar...</p>
        </div>
      ) : filteredEvents.length > 0 ? (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '20px' }}>
          {filteredEvents.map((ev, idx) => {
            const isClosing = ev.eventType === 'CLOSING';
            const isUrgent = isClosing && ev.daysRemaining <= 15 && ev.daysRemaining >= 0;
            const name = isTamil && ev.schemeNameTa ? ev.schemeNameTa : ev.schemeNameEn;

            return (
              <div
                key={idx}
                className="form-card"
                style={{
                  borderLeft: isUrgent ? '6px solid #dc2626' : isClosing ? '6px solid #d97706' : '6px solid #16a34a',
                  display: 'flex',
                  flexDirection: 'column',
                  justifyContent: 'space-between',
                  gap: '16px'
                }}
              >
                <div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }}>
                    <span className={`badge ${isUrgent ? 'badge-amber' : isClosing ? 'badge-purple' : 'badge-green'}`}>
                      {ev.eventType}
                    </span>
                    {ev.saved && (
                      <span className="badge badge-blue" style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                        <Bookmark size={12} />
                        Tracked
                      </span>
                    )}
                  </div>

                  <h3 style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--text-dark)', marginBottom: '8px' }}>
                    {name}
                  </h3>

                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: '#475569', fontSize: '0.95rem', marginBottom: '8px' }}>
                    <Calendar size={16} />
                    <strong>Date: {ev.date}</strong>
                  </div>

                  {ev.daysRemaining >= 0 ? (
                    <div style={{
                      display: 'inline-flex',
                      alignItems: 'center',
                      gap: '6px',
                      fontSize: '0.9rem',
                      fontWeight: 700,
                      color: isUrgent ? '#dc2626' : '#059669',
                      background: isUrgent ? '#fef2f2' : '#ecfdf5',
                      padding: '4px 10px',
                      borderRadius: '6px'
                    }}>
                      {isUrgent && <AlertTriangle size={14} />}
                      {ev.daysRemaining} {t('calendar.days_left')}
                    </div>
                  ) : (
                    <span className="badge badge-gray">Closed</span>
                  )}
                </div>

                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderTop: '1px solid var(--border)', paddingTop: '12px' }}>
                  <Link to={`/schemes/${ev.schemeId}`} style={{ fontWeight: 600, fontSize: '0.88rem' }}>
                    View Scheme Info
                  </Link>

                  {ev.applyLink && (
                    <a
                      href={ev.applyLink}
                      target="_blank"
                      rel="noreferrer"
                      className="btn btn-outline"
                      style={{ padding: '6px 12px', fontSize: '0.85rem' }}
                    >
                      Apply Link
                      <ExternalLink size={14} />
                    </a>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      ) : (
        <div className="form-card" style={{ textAlign: 'center', padding: '60px 20px' }}>
          <h3>No deadlines found for this filter</h3>
        </div>
      )}
    </div>
  );
};

