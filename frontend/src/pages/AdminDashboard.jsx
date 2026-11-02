import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import api from '../api/axios';
import { ShieldCheck, Plus, Trash2, Edit2, Send, RefreshCw, FileText, CheckCircle } from 'lucide-react';

export const AdminDashboard = () => {
  const { t } = useTranslation();
  const [schemes, setSchemes] = useState([]);
  const [documents, setDocuments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [toast, setToast] = useState('');
  const [triggering, setTriggering] = useState(false);

  // New Scheme Form Modal / Inline State
  const [showAddScheme, setShowAddScheme] = useState(false);
  const [newScheme, setNewScheme] = useState({
    nameEn: '',
    nameTa: '',
    descriptionEn: '',
    descriptionTa: '',
    benefits: '',
    benefitAmount: 10000,
    benefitType: 'Scholarship',
    department: '',
    level: 'State',
    state: 'Tamil Nadu',
    applyLink: 'https://scholarships.gov.in',
    openDate: '2026-07-01',
    lastDate: '2026-11-30',
    status: 'ACTIVE',
    renewalRequired: false,
    educationLevels: ['UNDERGRADUATE']
  });

  // New Document state
  const [newDocEn, setNewDocEn] = useState('');
  const [newDocTa, setNewDocTa] = useState('');

  const loadData = () => {
    setLoading(true);
    Promise.all([
      api.get('/schemes'),
      api.get('/admin/documents')
    ])
      .then(([schemesRes, docsRes]) => {
        setSchemes(schemesRes.data?.data || []);
        setDocuments(docsRes.data?.data || []);
      })
      .catch((err) => console.error('Admin load error:', err))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleCreateScheme = async (e) => {
    e.preventDefault();
    try {
      await api.post('/admin/schemes', newScheme);
      setToast('New scheme created successfully!');
      setShowAddScheme(false);
      loadData();
    } catch (err) {
      setToast('Failed to create scheme.');
    }
  };

  const handleDeleteScheme = async (id) => {
    if (!window.confirm('Delete this scheme?')) return;
    try {
      await api.delete(`/admin/schemes/${id}`);
      setSchemes(prev => prev.filter(s => s.id !== id));
      setToast('Scheme deleted.');
    } catch {
      setToast('Failed to delete.');
    }
  };

  const handleAddDocument = async (e) => {
    e.preventDefault();
    if (!newDocEn.trim()) return;
    try {
      const res = await api.post('/admin/documents', {
        nameEn: newDocEn.trim(),
        nameTa: newDocTa.trim() || newDocEn.trim()
      });
      setDocuments(prev => [...prev, res.data.data]);
      setNewDocEn('');
      setNewDocTa('');
      setToast('Document definition created.');
    } catch {
      setToast('Failed to add document.');
    }
  };

  const handleTriggerReminders = async () => {
    setTriggering(true);
    try {
      const res = await api.post('/admin/reminders/trigger');
      const data = res.data?.data;
      setToast(`Reminders triggered! Closing emails: ${data?.closingRemindersDispatched}, Renewals: ${data?.renewalRemindersDispatched}`);
    } catch (err) {
      setToast('Error triggering reminder job.');
    } finally {
      setTriggering(false);
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
          <h1 style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--primary)', display: 'flex', alignItems: 'center', gap: '10px' }}>
            <ShieldCheck size={32} />
            Admin Operations Dashboard
          </h1>
          <p style={{ color: 'var(--text-muted)' }}>
            Manage scholarship schemes, rule definitions, documents catalog, and trigger email reminder jobs.
          </p>
        </div>

        <div style={{ display: 'flex', gap: '10px' }}>
          <button
            onClick={handleTriggerReminders}
            disabled={triggering}
            className="btn btn-secondary"
            title="Execute the 7-day closing reminder & renewal email job immediately"
          >
            <Send size={16} />
            {triggering ? 'Dispatching Emails...' : 'Trigger Daily Reminders Job'}
          </button>
          <button
            onClick={() => setShowAddScheme(!showAddScheme)}
            className="btn btn-primary"
          >
            <Plus size={16} />
            {showAddScheme ? 'Close Scheme Form' : 'Add New Scheme'}
          </button>
        </div>
      </div>

      {/* Add Scheme Form */}
      {showAddScheme && (
        <form onSubmit={handleCreateScheme} className="form-card" style={{ marginBottom: '32px' }}>
          <h3 style={{ fontSize: '1.3rem', fontWeight: 800, color: 'var(--primary)', marginBottom: '16px' }}>
            Add New Education Scheme
          </h3>

          <div className="form-grid-2">
            <div className="form-group">
              <label className="form-label">Scheme Name (English) *</label>
              <input
                type="text"
                className="form-control"
                value={newScheme.nameEn}
                onChange={(e) => setNewScheme({ ...newScheme, nameEn: e.target.value })}
                required
              />
            </div>
            <div className="form-group">
              <label className="form-label">Scheme Name (Tamil) *</label>
              <input
                type="text"
                className="form-control"
                value={newScheme.nameTa}
                onChange={(e) => setNewScheme({ ...newScheme, nameTa: e.target.value })}
                required
              />
            </div>
          </div>

          <div className="form-grid-2">
            <div className="form-group">
              <label className="form-label">Department *</label>
              <input
                type="text"
                className="form-control"
                value={newScheme.department}
                onChange={(e) => setNewScheme({ ...newScheme, department: e.target.value })}
                required
              />
            </div>
            <div className="form-group">
              <label className="form-label">Benefit Amount (₹)</label>
              <input
                type="number"
                className="form-control"
                value={newScheme.benefitAmount}
                onChange={(e) => setNewScheme({ ...newScheme, benefitAmount: parseFloat(e.target.value) })}
              />
            </div>
          </div>

          <div className="form-grid-3">
            <div className="form-group">
              <label className="form-label">Benefit Type</label>
              <select
                className="form-control"
                value={newScheme.benefitType}
                onChange={(e) => setNewScheme({ ...newScheme, benefitType: e.target.value })}
              >
                <option value="Scholarship">Scholarship</option>
                <option value="Fee Waiver">Fee Waiver</option>
                <option value="Loan">Education Loan</option>
                <option value="Skill Training">Skill Training</option>
                <option value="Free Items">Free Items</option>
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Level</label>
              <select
                className="form-control"
                value={newScheme.level}
                onChange={(e) => setNewScheme({ ...newScheme, level: e.target.value })}
              >
                <option value="State">Tamil Nadu State</option>
                <option value="Central">Central Government</option>
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Application Last Date</label>
              <input
                type="date"
                className="form-control"
                value={newScheme.lastDate}
                onChange={(e) => setNewScheme({ ...newScheme, lastDate: e.target.value })}
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Official Apply Link</label>
            <input
              type="url"
              className="form-control"
              value={newScheme.applyLink}
              onChange={(e) => setNewScheme({ ...newScheme, applyLink: e.target.value })}
            />
          </div>

          <div className="form-group">
            <label className="form-label">Description (English)</label>
            <textarea
              className="form-control"
              rows={3}
              value={newScheme.descriptionEn}
              onChange={(e) => setNewScheme({ ...newScheme, descriptionEn: e.target.value })}
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px' }}>
            <button type="button" onClick={() => setShowAddScheme(false)} className="btn btn-outline">
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              Save Scheme
            </button>
          </div>
        </form>
      )}

      {/* Document Definition Manager */}
      <div className="form-card" style={{ marginBottom: '32px' }}>
        <h3 style={{ fontSize: '1.25rem', fontWeight: 800, marginBottom: '14px', color: 'var(--primary)' }}>
          Master Document Definitions ({documents.length})
        </h3>
        <form onSubmit={handleAddDocument} style={{ display: 'flex', gap: '10px', marginBottom: '16px' }}>
          <input
            type="text"
            className="form-control"
            placeholder="Document Name (English) e.g. Nativity Certificate"
            value={newDocEn}
            onChange={(e) => setNewDocEn(e.target.value)}
            required
          />
          <input
            type="text"
            className="form-control"
            placeholder="Document Name (Tamil) e.g. இருப்பிடச் சான்றிதழ்"
            value={newDocTa}
            onChange={(e) => setNewDocTa(e.target.value)}
          />
          <button type="submit" className="btn btn-primary" style={{ minWidth: '150px' }}>
            Add Document
          </button>
        </form>

        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
          {documents.map((doc) => (
            <span key={doc.id} className="badge badge-gray" style={{ padding: '6px 12px', fontSize: '0.85rem' }}>
              {doc.nameEn} ({doc.nameTa})
            </span>
          ))}
        </div>
      </div>

      {/* Schemes List Table */}
      <div className="form-card">
        <h3 style={{ fontSize: '1.25rem', fontWeight: 800, marginBottom: '16px', color: 'var(--primary)' }}>
          Active Schemes Database ({schemes.length})
        </h3>
        <div style={{ overflowX: 'auto' }}>
          <table className="checklist-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Scheme Name</th>
                <th>Level</th>
                <th>Benefit Type</th>
                <th>Amount</th>
                <th>Deadline</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {schemes.map((s) => (
                <tr key={s.id}>
                  <td><strong>#{s.id}</strong></td>
                  <td>
                    <strong>{s.nameEn}</strong>
                    <div style={{ fontSize: '0.82rem', color: '#64748b' }}>{s.department}</div>
                  </td>
                  <td>
                    <span className={`badge ${s.level === 'Central' ? 'badge-blue' : 'badge-amber'}`}>
                      {s.level}
                    </span>
                  </td>
                  <td>{s.benefitType}</td>
                  <td>{s.benefitAmount > 0 ? `₹${s.benefitAmount.toLocaleString('en-IN')}` : s.benefits}</td>
                  <td>{s.lastDate || 'Rolling'}</td>
                  <td>
                    <button
                      onClick={() => handleDeleteScheme(s.id)}
                      className="btn btn-outline"
                      style={{ padding: '4px 8px', color: '#dc2626' }}
                      title="Delete scheme"
                    >
                      <Trash2 size={14} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

