import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';
import { User, Save, CheckCircle2, Shield } from 'lucide-react';

export const ProfilePage = () => {
  const { t } = useTranslation();
  const { user } = useAuth();

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [toast, setToast] = useState('');

  const [profile, setProfile] = useState({
    educationLevel: 'UNDERGRADUATE',
    currentClassOrYear: 'First Year',
    course: 'B.E Computer Science',
    stream: 'Engineering',
    institutionName: 'Government College of Technology',
    institutionType: 'Government',
    institutionLocationType: 'Urban',
    boardOrUniversity: 'Anna University',
    previousExamPercentage: 85.0,
    entranceExamScore: 185.0,
    admissionType: 'Merit',
    dateOfBirth: '2004-06-15',
    gender: 'Female',
    state: 'Tamil Nadu',
    district: 'Coimbatore',
    communityCategory: 'BC',
    religionMinorityFlag: false,
    annualFamilyIncome: 180000,
    disabilityStatus: false,
    singleGirlChild: false,
    firstGraduate: true,
    orphanOrSingleParent: false,
    hosteller: false
  });

  useEffect(() => {
    api.get('/profile/me')
      .then((res) => {
        if (res.data?.data) {
          setProfile(res.data.data);
        }
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setProfile(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleSave = async (e) => {
    e.preventDefault();
    if (!profile.institutionName || !profile.institutionName.trim()) {
      setToast('Please enter your School / College / Institution Name.');
      return;
    }
    setSaving(true);
    try {
      await api.post('/profile', profile);
      setToast('Profile updated successfully! New criteria will reflect across matching schemes.');
      setTimeout(() => setToast(''), 4000);
    } catch {
      setToast('Failed to save profile. Check fields.');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="container" style={{ padding: '80px 20px', textAlign: 'center' }}>
        <p style={{ color: '#64748b' }}>Loading profile...</p>
      </div>
    );
  }

  return (
    <div className="container" style={{ padding: '40px 20px', maxWidth: '860px' }}>
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

      <div style={{ marginBottom: '28px' }}>
        <h1 style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--primary)', marginBottom: '8px' }}>
          My Student Profile
        </h1>
        <p style={{ color: 'var(--text-muted)' }}>
          Keep your academic and socio-economic details up to date to get exact scholarship match percentages.
        </p>
      </div>

      <form onSubmit={handleSave} className="form-card">
        {/* User Identity Info */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '16px', background: '#f8fafc', padding: '16px', borderRadius: '12px', marginBottom: '24px' }}>
          <div style={{ width: '48px', height: '48px', borderRadius: '50%', background: 'var(--primary)', color: '#ffffff', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 800, fontSize: '1.2rem' }}>
            {user?.name?.charAt(0) || 'S'}
          </div>
          <div>
            <h3 style={{ fontSize: '1.2rem', fontWeight: 700 }}>{user?.name}</h3>
            <p style={{ color: '#64748b', fontSize: '0.9rem' }}>{user?.email} • Role: {user?.role}</p>
          </div>
        </div>

        <h3 style={{ fontSize: '1.2rem', fontWeight: 700, marginBottom: '16px', color: 'var(--primary)' }}>
          1. Current Education
        </h3>

        <div className="form-group">
          <label className="form-label">Education Level *</label>
          <select
            name="educationLevel"
            className="form-control"
            value={profile.educationLevel}
            onChange={handleChange}
          >
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

        <div className="form-grid-2">
          <div className="form-group">
            <label className="form-label">Current Class / Year *</label>
            <input
              type="text"
              name="currentClassOrYear"
              className="form-control"
              value={profile.currentClassOrYear || ''}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Course / Degree Name</label>
            <input
              type="text"
              name="course"
              className="form-control"
              value={profile.course || ''}
              onChange={handleChange}
            />
          </div>
        </div>

        <div className="form-grid-2">
          <div className="form-group">
            <label className="form-label">School / College / Institution Name *</label>
            <input
              type="text"
              name="institutionName"
              className="form-control"
              placeholder="e.g. Government Higher Secondary School / College of Engineering"
              value={profile.institutionName || ''}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Institution Type *</label>
            <select
              name="institutionType"
              className="form-control"
              value={profile.institutionType || 'Government'}
              onChange={handleChange}
            >
              <option value="Government">Government Institution</option>
              <option value="Aided">Government Aided</option>
              <option value="Private">Private / Self-Financing</option>
            </select>
          </div>
        </div>

        <div className="form-group">
          <label className="form-label">Previous Exam Percentage (%) *</label>
          <input
            type="number"
            step="0.1"
            name="previousExamPercentage"
            className="form-control"
            value={profile.previousExamPercentage || ''}
            onChange={handleChange}
            required
          />
        </div>

        <h3 style={{ fontSize: '1.2rem', fontWeight: 700, margin: '24px 0 16px', color: 'var(--primary)' }}>
          2. Social & Socio-Economic Details
        </h3>

        <div className="form-grid-2">
          <div className="form-group">
            <label className="form-label">Community Category *</label>
            <select
              name="communityCategory"
              className="form-control"
              value={profile.communityCategory || 'BC'}
              onChange={handleChange}
            >
              <option value="BC">BC (Backward Classes)</option>
              <option value="MBC">MBC / DNC</option>
              <option value="SC">SC (Scheduled Caste)</option>
              <option value="ST">ST (Scheduled Tribe)</option>
              <option value="OBC">OBC (Other Backward Class)</option>
              <option value="Minority">Minority</option>
              <option value="General">General / Open</option>
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Annual Family Income (₹) *</label>
            <input
              type="number"
              name="annualFamilyIncome"
              className="form-control"
              value={profile.annualFamilyIncome || ''}
              onChange={handleChange}
              required
            />
          </div>
        </div>

        <div className="form-grid-2">
          <div className="form-group">
            <label className="form-label">Gender *</label>
            <select
              name="gender"
              className="form-control"
              value={profile.gender || 'Female'}
              onChange={handleChange}
            >
              <option value="Female">Female</option>
              <option value="Male">Male</option>
              <option value="Other">Other</option>
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">State of Domicile *</label>
            <input
              type="text"
              name="state"
              className="form-control"
              value={profile.state || 'Tamil Nadu'}
              onChange={handleChange}
              required
            />
          </div>
        </div>

        <h3 style={{ fontSize: '1.2rem', fontWeight: 700, margin: '24px 0 16px', color: 'var(--primary)' }}>
          3. Special Entitlements
        </h3>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px', marginBottom: '28px' }}>
          <label className="checkbox-group">
            <input
              type="checkbox"
              name="singleGirlChild"
              checked={profile.singleGirlChild || false}
              onChange={handleChange}
            />
            <span>Single Girl Child in Family</span>
          </label>

          <label className="checkbox-group">
            <input
              type="checkbox"
              name="firstGraduate"
              checked={profile.firstGraduate || false}
              onChange={handleChange}
            />
            <span>First Graduate in Family</span>
          </label>

          <label className="checkbox-group">
            <input
              type="checkbox"
              name="disabilityStatus"
              checked={profile.disabilityStatus || false}
              onChange={handleChange}
            />
            <span>Differently-Abled (Disability &gt;= 40%)</span>
          </label>

          <label className="checkbox-group">
            <input
              type="checkbox"
              name="religionMinorityFlag"
              checked={profile.religionMinorityFlag || false}
              onChange={handleChange}
            />
            <span>Religious Minority</span>
          </label>
        </div>

        <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
          <button type="submit" disabled={saving} className="btn btn-primary btn-large">
            <Save size={18} />
            {saving ? 'Saving Changes...' : 'Save Profile Changes'}
          </button>
        </div>
      </form>
    </div>
  );
};

