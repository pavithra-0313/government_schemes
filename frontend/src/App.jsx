import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { Navbar } from './components/Navbar';
import { Footer } from './components/Footer';
import { ProtectedRoute } from './components/ProtectedRoute';

import { Home } from './pages/Home';
import { Questionnaire } from './pages/Questionnaire';
import { Results } from './pages/Results';
import { SchemesCatalog } from './pages/SchemesCatalog';
import { SchemeDetails } from './pages/SchemeDetails';
import { ScholarshipCalendar } from './pages/ScholarshipCalendar';
import { SavedTracker } from './pages/SavedTracker';
import { DocumentChecklistPage } from './pages/DocumentChecklistPage';
import { ProfilePage } from './pages/ProfilePage';
import { AdminDashboard } from './pages/AdminDashboard';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';

import './assets/styles.css';

export function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Navbar />
        <main style={{ flex: 1 }}>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/questionnaire" element={<Questionnaire />} />
            <Route path="/results" element={<Results />} />
            <Route path="/schemes" element={<SchemesCatalog />} />
            <Route path="/schemes/:id" element={<SchemeDetails />} />
            <Route path="/calendar" element={<ScholarshipCalendar />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            {/* Protected Student Routes */}
            <Route
              path="/tracker"
              element={
                <ProtectedRoute>
                  <SavedTracker />
                </ProtectedRoute>
              }
            />
            <Route
              path="/checklist"
              element={
                <ProtectedRoute>
                  <DocumentChecklistPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="/profile"
              element={
                <ProtectedRoute>
                  <ProfilePage />
                </ProtectedRoute>
              }
            />

            {/* Protected Admin Routes */}
            <Route
              path="/admin"
              element={
                <ProtectedRoute requireAdmin={true}>
                  <AdminDashboard />
                </ProtectedRoute>
              }
            />
          </Routes>
        </main>
        <Footer />
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;

