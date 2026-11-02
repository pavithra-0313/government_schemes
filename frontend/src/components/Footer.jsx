import React from 'react';
import { useTranslation } from 'react-i18next';
import { Shield, Phone, ExternalLink } from 'lucide-react';

export const Footer = () => {
  const { t } = useTranslation();

  return (
    <footer className="footer">
      <div className="container">
        <div className="footer-grid">
          <div>
            <h4 style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Shield size={20} color="#60a5fa" />
              {t('nav.title')}
            </h4>
            <p style={{ fontSize: '0.9rem', color: '#94a3b8', marginTop: '10px', lineHeight: 1.6 }}>
              A centralized digital platform designed to bridge information asymmetry, empowering students across Tamil Nadu and all Indian states with transparent access to government scholarships, fee waivers, educational loans, and skilling programs.
            </p>
          </div>

          <div>
            <h4>Quick Links</h4>
            <ul>
              <li><a href="/schemes">Browse All Schemes</a></li>
              <li><a href="/calendar">Scholarship Calendar</a></li>
              <li><a href="/questionnaire">Eligibility Calculator</a></li>
              <li><a href="/checklist">Required Document Checklist</a></li>
            </ul>
          </div>

          <div>
            <h4>Government Portals</h4>
            <ul>
              <li>
                <a href="https://scholarships.gov.in" target="_blank" rel="noreferrer">
                  National Scholarship Portal (NSP) <ExternalLink size={14} style={{ display: 'inline' }} />
                </a>
              </li>
              <li>
                <a href="https://www.tn.gov.in" target="_blank" rel="noreferrer">
                  Tamil Nadu Govt Portal <ExternalLink size={14} style={{ display: 'inline' }} />
                </a>
              </li>
              <li>
                <a href="https://naanmudhalvan.tn.gov.in" target="_blank" rel="noreferrer">
                  Naan Mudhalvan TN <ExternalLink size={14} style={{ display: 'inline' }} />
                </a>
              </li>
              <li>
                <a href="https://www.vidyalakshmi.co.in" target="_blank" rel="noreferrer">
                  Vidya Lakshmi Portal <ExternalLink size={14} style={{ display: 'inline' }} />
                </a>
              </li>
            </ul>
          </div>

          <div>
            <h4>Citizen Helplines</h4>
            <ul style={{ fontSize: '0.9rem' }}>
              <li style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <Phone size={14} color="#60a5fa" /> NSP Helpline: 0120-6619540
              </li>
              <li style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <Phone size={14} color="#60a5fa" /> TN ADTW Helpline: 1800-425-1333
              </li>
              <li style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <Phone size={14} color="#60a5fa" /> TN Higher Education: 044-24344799
              </li>
              <li style={{ marginTop: '6px', color: '#64748b', fontSize: '0.8rem' }}>
                Operational on all working days 9:30 AM to 6:00 PM.
              </li>
            </ul>
          </div>
        </div>

        <div className="footer-bottom">
          <p>© {new Date().getFullYear()} Student Scholarship and Education Scheme Finder. Public Information & Career Guidance Initiative.</p>
        </div>
      </div>
    </footer>
  );
};

