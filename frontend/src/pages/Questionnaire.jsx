import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';
import { BookOpen, User, Sparkles, ArrowRight, ArrowLeft, Info, CheckCircle2 } from 'lucide-react';

export const Questionnaire = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { user, isAuthenticated } = useAuth();

  const [step, setStep] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const initialLevel = searchParams.get('level') || 'UNDERGRADUATE';
  const defaultInstName = initialLevel.startsWith('SCHOOL_')
    ? 'Government Higher Secondary School'
    : 'Government College of Technology';

  const [formData, setFormData] = useState({
    educationLevel: initialLevel,
    currentClassOrYear: 'First Year',
    course: '',
    stream: 'Science',
    institutionName: defaultInstName,
    institutionType: 'Government',
    institutionLocationType: 'Urban',
    boardOrUniversity: 'State Board / Anna University',
    previousExamPercentage: 75.0,
    entranceExamScore: 0.0,
    admissionType: 'Merit',
    dateOfBirth: '2004-05-15',
    gender: 'Female',
    state: 'Tamil Nadu',
    district: 'Chennai',
    communityCategory: 'BC',
    religionMinorityFlag: false,
    annualFamilyIncome: 180000,
    disabilityStatus: false,
    singleGirlChild: false,
    firstGraduate: false,
    orphanOrSingleParent: false,
    hosteller: false
  });

  const isSchool = formData.educationLevel.startsWith('SCHOOL_');
  const isUG = formData.educationLevel === 'UNDERGRADUATE';
  const isPG = formData.educationLevel === 'POSTGRADUATE';
  const isResearch = formData.educationLevel === 'RESEARCH';
  const isDiploma = formData.educationLevel === 'DIPLOMA';

  // Load existing profile if user is logged in
  useEffect(() => {
    if (isAuthenticated) {
      api.get('/profile/me')
        .then((res) => {
          if (res.data?.data) {
            setFormData((prev) => ({
              ...prev,
              ...res.data.data
            }));
          }
        })
        .catch(() => {});
    }
  }, [isAuthenticated]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleSubmit = async (e) => {
    if (e && e.preventDefault) e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const effectiveInst = (formData.institutionName && formData.institutionName.trim())
        ? formData.institutionName.trim()
        : defaultInstName;

      // Clean and sanitize numbers and date fields
      const sanitized = {
        ...formData,
        dateOfBirth: (formData.dateOfBirth && typeof formData.dateOfBirth === 'string' && formData.dateOfBirth.trim())
          ? formData.dateOfBirth.trim()
          : null,
        institutionName: effectiveInst,
        annualFamilyIncome: formData.annualFamilyIncome !== '' && formData.annualFamilyIncome !== null ? Number(formData.annualFamilyIncome) : null,
        previousExamPercentage: formData.previousExamPercentage !== '' && formData.previousExamPercentage !== null ? Number(formData.previousExamPercentage) : 0.0,
        entranceExamScore: formData.entranceExamScore !== '' && formData.entranceExamScore !== null ? Number(formData.entranceExamScore) : 0.0
      };

      // 1. If logged in, save/update profile in backend
      if (isAuthenticated) {
        await api.post('/profile', sanitized).catch(() => {});
      }

      // 2. Evaluate eligibility
      const res = await api.post('/eligibility/evaluate', sanitized);
      const matchResults = res.data?.data || [];

      // Persist in session storage so page refresh or rerun preserves the matches
      try {
        sessionStorage.setItem('last_evaluated_results', JSON.stringify(matchResults));
        sessionStorage.setItem('last_evaluated_profile', JSON.stringify(sanitized));
      } catch (storageErr) {
        console.warn('Could not cache results in sessionStorage:', storageErr);
      }

      // Navigate to results page with state
      navigate('/results', {
        state: {
          results: matchResults,
          profile: sanitized
        }
      });
    } catch (err) {
      console.error('Eligibility evaluation error:', err);
      if (!err.response) {
        setError('Cannot connect to backend server. Please make sure the backend is running on http://localhost:8080 (Run run_project.bat).');
      } else if (err.response.status >= 500) {
        setError('Backend server error or proxy failure (Status ' + err.response.status + '). Ensure the Spring Boot backend is active on port 8080.');
      } else if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError('Error calculating eligibility. Please check that all required fields are filled correctly.');
      }
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container" style={{ padding: '40px 20px', maxWidth: '860px' }}>
      <div style={{ textAlign: 'center', marginBottom: '32px' }}>
        <h1 style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--primary)', marginBottom: '8px' }}>
          {t('questionnaire.title')}
        </h1>
        <p style={{ color: 'var(--text-muted)', fontSize: '1.05rem' }}>
          {t('questionnaire.subtitle')}
        </p>
      </div>

      {isSchool && (
        <div className="school-friendly-banner">
          <Info size={22} style={{ flexShrink: 0 }} />
          <span>{t('questionnaire.school_help_note')}</span>
        </div>
      )}

      {/* Stepper Tabs */}
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '28px', borderBottom: '2px solid var(--border)', paddingBottom: '12px', gap: '8px' }}>
        <button
          type="button"
          onClick={() => { setError(''); setStep(1); }}
          style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: '0.95rem', fontWeight: 700, color: step === 1 ? 'var(--primary)' : 'var(--text-muted)', borderBottom: step === 1 ? '3px solid var(--primary)' : 'none', paddingBottom: '8px' }}
        >
          {t('questionnaire.step_education')}
        </button>
        <button
          type="button"
          onClick={() => { setError(''); setStep(2); }}
          style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: '0.95rem', fontWeight: 700, color: step === 2 ? 'var(--primary)' : 'var(--text-muted)', borderBottom: step === 2 ? '3px solid var(--primary)' : 'none', paddingBottom: '8px' }}
        >
          {t('questionnaire.step_personal')}
        </button>
        <button
          type="button"
          onClick={() => { setError(''); setStep(3); }}
          style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: '0.95rem', fontWeight: 700, color: step === 3 ? 'var(--primary)' : 'var(--text-muted)', borderBottom: step === 3 ? '3px solid var(--primary)' : 'none', paddingBottom: '8px' }}
        >
          {t('questionnaire.step_special')}
        </button>
      </div>

      {error && (
        <div style={{ background: '#fee2e2', color: '#b91c1c', padding: '12px 16px', borderRadius: '10px', marginBottom: '20px' }}>
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="form-card">
        {/* STEP 1: EDUCATION DETAILS */}
        {step === 1 && (
          <div>
            <div className="form-group">
              <label className="form-label">{t('questionnaire.level')} *</label>
              <select
                name="educationLevel"
                className="form-control"
                value={formData.educationLevel}
                onChange={handleChange}
                style={{ fontSize: isSchool ? '1.1rem' : '1rem' }}
              >
                <option value="SCHOOL_PRIMARY">School: Primary (Classes 1 - 5)</option>
                <option value="SCHOOL_MIDDLE">School: Middle (Classes 6 - 8)</option>
                <option value="SCHOOL_SECONDARY">School: Secondary / High School (Classes 9 - 10)</option>
                <option value="SCHOOL_HIGHER_SECONDARY">School: Higher Secondary (Classes 11 - 12)</option>
                <option value="DIPLOMA">Diploma / Polytechnic / ITI</option>
                <option value="UNDERGRADUATE">Undergraduate (UG Degree - B.E, B.Sc, B.A, B.Com, MBBS)</option>
                <option value="POSTGRADUATE">Postgraduate (PG Degree - M.E, M.Sc, MBA, MCA, MD)</option>
                <option value="RESEARCH">Research Scholar (M.Phil / Ph.D)</option>
              </select>
            </div>

            <div className="form-grid-2">
              <div className="form-group">
                <label className="form-label">
                  {isSchool ? 'Current Class / Standard' : 'Current Class or Year of Study'} *
                </label>
                <input
                  type="text"
                  name="currentClassOrYear"
                  className="form-control"
                  placeholder={isSchool ? 'e.g. Class 8' : 'e.g. 2nd Year'}
                  value={formData.currentClassOrYear}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">
                  {isSchool ? 'School / Board Stream' : t('questionnaire.stream')} *
                </label>
                <select
                  name="stream"
                  className="form-control"
                  value={formData.stream}
                  onChange={handleChange}
                >
                  <option value="General">General / All Subjects</option>
                  <option value="Science">Science (Bio-Maths / Computer Science)</option>
                  <option value="Commerce">Commerce & Accountancy</option>
                  <option value="Arts">Arts & Humanities</option>
                  <option value="Engineering">Engineering & Technology</option>
                  <option value="Medical">Medical / Dental / Allied Health</option>
                  <option value="Vocational">Vocational / Technical</option>
                </select>
              </div>
            </div>

            <div className="form-grid-2">
              <div className="form-group">
                <label className="form-label">
                  {isSchool ? 'School Name' : 'College / Institution Name'} *
                </label>
                <input
                  type="text"
                  name="institutionName"
                  className="form-control"
                  placeholder={isSchool ? 'e.g. Govt Higher Secondary School, Adyar' : 'e.g. College of Engineering, Guindy'}
                  value={formData.institutionName}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">{t('questionnaire.institution_type')} *</label>
                <select
                  name="institutionType"
                  className="form-control"
                  value={formData.institutionType}
                  onChange={handleChange}
                >
                  <option value="Government">{t('questionnaire.govt')}</option>
                  <option value="Aided">{t('questionnaire.aided')}</option>
                  <option value="Private">{t('questionnaire.private')}</option>
                </select>
              </div>
            </div>

            <div className="form-grid-2">
              <div className="form-group">
                <label className="form-label">{t('questionnaire.percentage')} *</label>
                <input
                  type="number"
                  step="0.1"
                  min="0"
                  max="100"
                  name="previousExamPercentage"
                  className="form-control"
                  placeholder="e.g. 82.5"
                  value={formData.previousExamPercentage}
                  onChange={handleChange}
                  required
                />
              </div>

              {(isUG || isPG || isResearch) && (
                <div className="form-group">
                  <label className="form-label">Entrance Exam Score (Cutoff / GATE / NET Score)</label>
                  <input
                    type="number"
                    step="0.1"
                    name="entranceExamScore"
                    className="form-control"
                    placeholder="e.g. 185.5 or GATE Score"
                    value={formData.entranceExamScore}
                    onChange={handleChange}
                  />
                </div>
              )}
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '24px' }}>
              <button
                type="button"
                onClick={() => {
                  setError('');
                  setStep(2);
                }}
                className={`btn btn-primary ${isSchool ? 'btn-large' : ''}`}
              >
                {t('questionnaire.next')}
                <ArrowRight size={18} />
              </button>
            </div>
          </div>
        )}

        {/* STEP 2: DEMOGRAPHIC & SOCIAL BACKGROUND */}
        {step === 2 && (
          <div>
            <div className="form-grid-2">
              <div className="form-group">
                <label className="form-label">{t('questionnaire.dob')} *</label>
                <input
                  type="date"
                  name="dateOfBirth"
                  className="form-control"
                  value={formData.dateOfBirth}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">{t('questionnaire.gender')} *</label>
                <select
                  name="gender"
                  className="form-control"
                  value={formData.gender}
                  onChange={handleChange}
                >
                  <option value="Female">{t('questionnaire.female')}</option>
                  <option value="Male">{t('questionnaire.male')}</option>
                  <option value="Other">{t('questionnaire.transgender')}</option>
                </select>
              </div>
            </div>

            <div className="form-grid-2">
              <div className="form-group">
                <label className="form-label">{t('questionnaire.state')} *</label>
                <select
                  name="state"
                  className="form-control"
                  value={formData.state}
                  onChange={handleChange}
                >
                  <option value="Tamil Nadu">Tamil Nadu</option>
                  <option value="Kerala">Kerala</option>
                  <option value="Karnataka">Karnataka</option>
                  <option value="Andhra Pradesh">Andhra Pradesh</option>
                  <option value="Other State">Other Indian State</option>
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">{t('questionnaire.district')}</label>
                <input
                  type="text"
                  name="district"
                  className="form-control"
                  placeholder="e.g. Chennai, Madurai, Coimbatore"
                  value={formData.district}
                  onChange={handleChange}
                />
              </div>
            </div>

            <div className="form-grid-2">
              <div className="form-group">
                <label className="form-label">{t('questionnaire.community')} *</label>
                <select
                  name="communityCategory"
                  className="form-control"
                  value={formData.communityCategory}
                  onChange={handleChange}
                >
                  <option value="BC">BC (Backward Classes)</option>
                  <option value="MBC">MBC / DNC (Most Backward Classes)</option>
                  <option value="SC">SC (Scheduled Caste)</option>
                  <option value="ST">ST (Scheduled Tribe)</option>
                  <option value="OBC">OBC (Other Backward Class - Central)</option>
                  <option value="Minority">Minority Community</option>
                  <option value="General">General / Open Competition (OC)</option>
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">{t('questionnaire.income')} *</label>
                <input
                  type="number"
                  name="annualFamilyIncome"
                  className="form-control"
                  placeholder="e.g. 150000"
                  value={formData.annualFamilyIncome}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '24px' }}>
              <button
                type="button"
                onClick={() => setStep(1)}
                className="btn btn-outline"
              >
                <ArrowLeft size={18} />
                {t('questionnaire.back')}
              </button>

              <button
                type="button"
                onClick={() => setStep(3)}
                className={`btn btn-primary ${isSchool ? 'btn-large' : ''}`}
              >
                {t('questionnaire.next')}
                <ArrowRight size={18} />
              </button>
            </div>
          </div>
        )}

        {/* STEP 3: SPECIAL CATEGORIES */}
        {step === 3 && (
          <div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '14px', marginBottom: '24px' }}>
              <label className="checkbox-group">
                <input
                  type="checkbox"
                  name="singleGirlChild"
                  checked={formData.singleGirlChild}
                  onChange={handleChange}
                />
                <div>
                  <strong>{t('questionnaire.single_girl_child')}</strong>
                  <p style={{ fontSize: '0.85rem', color: '#64748b' }}>
                    Family has only one child and she is a girl (eligible for CBSE & UGC single girl child schemes).
                  </p>
                </div>
              </label>

              {(isUG || isDiploma) && (
                <label className="checkbox-group">
                  <input
                    type="checkbox"
                    name="firstGraduate"
                    checked={formData.firstGraduate}
                    onChange={handleChange}
                  />
                  <div>
                    <strong>{t('questionnaire.first_graduate')}</strong>
                    <p style={{ fontSize: '0.85rem', color: '#64748b' }}>
                      No one in your family lineage (parents or siblings) holds a graduate degree (100% tuition waiver in TN).
                    </p>
                  </div>
                </label>
              )}

              <label className="checkbox-group">
                <input
                  type="checkbox"
                  name="disabilityStatus"
                  checked={formData.disabilityStatus}
                  onChange={handleChange}
                />
                <div>
                  <strong>{t('questionnaire.disability')}</strong>
                  <p style={{ fontSize: '0.85rem', color: '#64748b' }}>
                    Benchmark disability certified by medical authority (eligible for AICTE Saksham & DEPwD scholarships).
                  </p>
                </div>
              </label>

              <label className="checkbox-group">
                <input
                  type="checkbox"
                  name="religionMinorityFlag"
                  checked={formData.religionMinorityFlag}
                  onChange={handleChange}
                />
                <div>
                  <strong>{t('questionnaire.minority')}</strong>
                  <p style={{ fontSize: '0.85rem', color: '#64748b' }}>
                    Muslim, Christian, Sikh, Buddhist, Jain, or Parsi community members.
                  </p>
                </div>
              </label>

              <label className="checkbox-group">
                <input
                  type="checkbox"
                  name="orphanOrSingleParent"
                  checked={formData.orphanOrSingleParent}
                  onChange={handleChange}
                />
                <div>
                  <strong>{t('questionnaire.orphan')}</strong>
                  <p style={{ fontSize: '0.85rem', color: '#64748b' }}>
                    Children without parents or wards of martyred armed personnel (eligible for AICTE Swanath scheme).
                  </p>
                </div>
              </label>

              <label className="checkbox-group">
                <input
                  type="checkbox"
                  name="hosteller"
                  checked={formData.hosteller}
                  onChange={handleChange}
                />
                <div>
                  <strong>{t('questionnaire.hosteller')}</strong>
                  <p style={{ fontSize: '0.85rem', color: '#64748b' }}>
                    Staying in a recognized school/college or government hostel (qualifies for higher hostel maintenance allowances).
                  </p>
                </div>
              </label>
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '24px' }}>
              <button
                type="button"
                onClick={() => setStep(2)}
                className="btn btn-outline"
              >
                <ArrowLeft size={18} />
                {t('questionnaire.back')}
              </button>

              <button
                type="submit"
                disabled={loading}
                className={`btn btn-secondary ${isSchool ? 'btn-large' : ''}`}
                style={{ minWidth: '220px' }}
              >
                {loading ? 'Evaluating Rules...' : t('questionnaire.submit')}
                <Sparkles size={18} />
              </button>
            </div>
          </div>
        )}
      </form>
    </div>
  );
};

