import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { Search, Sparkles, BookOpen, Wrench, GraduationCap, Award, Compass, ArrowRight, CheckCircle, ShieldCheck } from 'lucide-react';

export const Home = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [searchKeyword, setSearchKeyword] = useState('');

  const levels = [
    {
      id: 'SCHOOL_SECONDARY',
      name: t('hero.school'),
      description: t('hero.school_desc'),
      icon: <BookOpen size={32} color="#1d4ed8" />,
      color: '#dbeafe',
      tag: 'Classes 1 - 12'
    },
    {
      id: 'DIPLOMA',
      name: t('hero.diploma'),
      description: t('hero.diploma_desc'),
      icon: <Wrench size={32} color="#059669" />,
      color: '#d1fae5',
      tag: 'Polytechnic & ITI'
    },
    {
      id: 'UNDERGRADUATE',
      name: t('hero.ug'),
      description: t('hero.ug_desc'),
      icon: <GraduationCap size={32} color="#7c3aed" />,
      color: '#ede9fe',
      tag: 'Degree Programs'
    },
    {
      id: 'POSTGRADUATE',
      name: t('hero.pg'),
      description: t('hero.pg_desc'),
      icon: <Award size={32} color="#d97706" />,
      color: '#fef3c7',
      tag: 'Master Degrees'
    },
    {
      id: 'RESEARCH',
      name: t('hero.research'),
      description: t('hero.research_desc'),
      icon: <Compass size={32} color="#0284c7" />,
      color: '#e0f2fe',
      tag: 'M.Phil / Ph.D'
    }
  ];

  const handleSelectLevel = (levelId) => {
    navigate(`/questionnaire?level=${levelId}`);
  };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    if (searchKeyword.trim()) {
      navigate(`/schemes?keyword=${encodeURIComponent(searchKeyword.trim())}`);
    } else {
      navigate('/schemes');
    }
  };

  return (
    <div>
      {/* Hero Section */}
      <section className="hero-section">
        <div className="container">
          <span className="hero-badge">
            <Sparkles size={14} style={{ display: 'inline', marginRight: '6px' }} />
            {t('hero.badge')}
          </span>
          <h1 className="hero-title">{t('hero.title')}</h1>
          <p className="hero-subtitle">{t('hero.subtitle')}</p>

          <form onSubmit={handleSearchSubmit} style={{ maxWidth: '650px', margin: '0 auto 30px', display: 'flex', gap: '10px' }}>
            <div style={{ position: 'relative', flex: 1 }}>
              <input
                type="text"
                className="form-control"
                style={{ paddingLeft: '44px', height: '52px', fontSize: '1.05rem' }}
                placeholder="Search scheme name, department, or keyword (e.g. Pudhumai Penn, First Graduate, Pragati)..."
                value={searchKeyword}
                onChange={(e) => setSearchKeyword(e.target.value)}
              />
              <Search size={20} color="#94a3b8" style={{ position: 'absolute', left: '16px', top: '16px' }} />
            </div>
            <button type="submit" className="btn btn-primary" style={{ height: '52px', padding: '0 24px', fontSize: '1rem' }}>
              Search
            </button>
          </form>

          <div className="hero-actions">
            <button onClick={() => navigate('/questionnaire')} className="btn btn-primary btn-large">
              {t('hero.cta')}
              <ArrowRight size={20} />
            </button>
            <button onClick={() => navigate('/schemes')} className="btn btn-outline btn-large">
              {t('hero.browse')}
            </button>
          </div>
        </div>
      </section>

      {/* Education Levels Picker */}
      <section className="container" style={{ padding: '60px 20px' }}>
        <div style={{ textAlign: 'center', marginBottom: '40px' }}>
          <h2 style={{ fontSize: '2rem', fontWeight: 800, color: '#0f172a', marginBottom: '10px' }}>
            {t('hero.choose_level_title')}
          </h2>
          <p style={{ color: '#64748b', fontSize: '1.05rem' }}>
            Tailored schemes, specific eligibility criteria, and step-by-step application guidance for your stage.
          </p>
        </div>

        <div className="level-grid">
          {levels.map((lvl) => (
            <div
              key={lvl.id}
              className="level-card"
              onClick={() => handleSelectLevel(lvl.id)}
            >
              <div>
                <div style={{
                  width: '56px',
                  height: '56px',
                  borderRadius: '12px',
                  background: lvl.color,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  marginBottom: '16px'
                }}>
                  {lvl.icon}
                </div>
                <span className="badge badge-gray" style={{ marginBottom: '8px', display: 'inline-block' }}>
                  {lvl.tag}
                </span>
                <h3>{lvl.name}</h3>
                <p>{lvl.description}</p>
              </div>

              <div style={{ marginTop: '20px', display: 'flex', alignItems: 'center', color: 'var(--primary)', fontWeight: 700, fontSize: '0.9rem' }}>
                <span>Start Questionnaire</span>
                <ArrowRight size={16} style={{ marginLeft: '6px' }} />
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* Value Pillars */}
      <section style={{ background: '#ffffff', borderTop: '1px solid var(--border)', borderBottom: '1px solid var(--border)', padding: '60px 0' }}>
        <div className="container">
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '30px' }}>
            <div style={{ display: 'flex', gap: '16px', alignItems: 'flex-start' }}>
              <div style={{ background: '#dcfce7', padding: '12px', borderRadius: '12px' }}>
                <CheckCircle size={28} color="#15803d" />
              </div>
              <div>
                <h4 style={{ fontSize: '1.2rem', marginBottom: '6px', fontWeight: 700 }}>Strict Eligibility Matching</h4>
                <p style={{ color: '#64748b', fontSize: '0.95rem' }}>
                  Our automated rule engine compares your age, family income, marks, community, and institution type to prevent invalid applications.
                </p>
              </div>
            </div>

            <div style={{ display: 'flex', gap: '16px', alignItems: 'flex-start' }}>
              <div style={{ background: '#dbeafe', padding: '12px', borderRadius: '12px' }}>
                <ShieldCheck size={28} color="#1d4ed8" />
              </div>
              <div>
                <h4 style={{ fontSize: '1.2rem', marginBottom: '6px', fontWeight: 700 }}>Document Checklist & Tracker</h4>
                <p style={{ color: '#64748b', fontSize: '0.95rem' }}>
                  Tick off mandatory certificates (bonafide, income, community) and print ready-to-use checklist sheets for college verification.
                </p>
              </div>
            </div>

            <div style={{ display: 'flex', gap: '16px', alignItems: 'flex-start' }}>
              <div style={{ background: '#fef3c7', padding: '12px', borderRadius: '12px' }}>
                <Award size={28} color="#b45309" />
              </div>
              <div>
                <h4 style={{ fontSize: '1.2rem', marginBottom: '6px', fontWeight: 700 }}>Deadline & Renewal Alerts</h4>
                <p style={{ color: '#64748b', fontSize: '0.95rem' }}>
                  Automatic 7-day closing email reminders and annual scholarship renewal alerts so you never miss funding opportunities.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

