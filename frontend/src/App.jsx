import React from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './contexts/AuthContext'
import { Layout } from './components/Layout'
import { Login } from './pages/Login'
import { ChangePassword } from './pages/ChangePassword'
import { Dashboard } from './pages/Dashboard'
import { EmployeeDetails } from './pages/EmployeeDetails'
import { UsersDetails } from './pages/UsersDetails'
import { Configuration } from './pages/Configuration'
import { OrganizationDetails } from './pages/OrganizationDetails'
import { CompanyAssignments } from './pages/CompanyAssignments'
import { AcademicUnitAssignments } from './pages/AcademicUnitAssignments'
import { GiftAssignments } from './pages/GiftAssignments'
import { LocationAssignments } from './pages/LocationAssignments'
import { ProjectAssignments } from './pages/ProjectAssignments'
import { GrantAssignments } from './pages/GrantAssignments'
import { PaygroupAssignments } from './pages/PaygroupAssignments'

const PrivateRoute = ({ children }) => {
  const { user, loading } = useAuth()

  if (loading) {
    return <div className="min-h-screen flex items-center justify-center">Loading...</div>
  }

  return user ? children : <Navigate to="/login" />
}

const AdminRoute = ({ children }) => {
  const { user, loading, isAdmin } = useAuth()

  if (loading) {
    return <div className="min-h-screen flex items-center justify-center">Loading...</div>
  }

  if (!user) {
    return <Navigate to="/login" />
  }

  return isAdmin() ? children : <Navigate to="/dashboard" />
}

const CompanyAssignmentsRoute = ({ children }) => {
  const { user, loading, isAdmin, hasCompanyAssignmentsAccess } = useAuth()

  if (loading) {
    return <div className="min-h-screen flex items-center justify-center">Loading...</div>
  }

  if (!user) {
    return <Navigate to="/login" />
  }

  // Admins always have access, regular users need permission
  if (isAdmin()) {
    return children
  }

  return hasCompanyAssignmentsAccess() ? children : <Navigate to="/dashboard" />
}

const AcademicUnitAssignmentsRoute = ({ children }) => {
  const { user, loading, isAdmin, hasAcademicUnitAssignmentsAccess } = useAuth()

  if (loading) {
    return <div className="min-h-screen flex items-center justify-center">Loading...</div>
  }

  if (!user) {
    return <Navigate to="/login" />
  }

  // Admins always have access, regular users need permission
  if (isAdmin()) {
    return children
  }

  return hasAcademicUnitAssignmentsAccess() ? children : <Navigate to="/dashboard" />
}

const GiftAssignmentsRoute = ({ children }) => {
  const { user, loading, isAdmin, hasGiftAssignmentsAccess } = useAuth()

  if (loading) {
    return <div className="min-h-screen flex items-center justify-center">Loading...</div>
  }

  if (!user) {
    return <Navigate to="/login" />
  }

  // Admins always have access, regular users need permission
  if (isAdmin()) {
    return children
  }

  return hasGiftAssignmentsAccess() ? children : <Navigate to="/dashboard" />
}

const LocationAssignmentsRoute = ({ children }) => {
  const { user, loading, isAdmin, hasLocationAssignmentsAccess } = useAuth()

  if (loading) {
    return <div className="min-h-screen flex items-center justify-center">Loading...</div>
  }

  if (!user) {
    return <Navigate to="/login" />
  }

  if (isAdmin()) {
    return children
  }

  return hasLocationAssignmentsAccess() ? children : <Navigate to="/dashboard" />
}

const ProjectAssignmentsRoute = ({ children }) => {
  const { user, loading, isAdmin, hasProjectAssignmentsAccess } = useAuth()

  if (loading) {
    return <div className="min-h-screen flex items-center justify-center">Loading...</div>
  }

  if (!user) {
    return <Navigate to="/login" />
  }

  if (isAdmin()) {
    return children
  }

  return hasProjectAssignmentsAccess() ? children : <Navigate to="/dashboard" />
}

const GrantAssignmentsRoute = ({ children }) => {
  const { user, loading, isAdmin, hasGrantAssignmentsAccess } = useAuth()

  if (loading) {
    return <div className="min-h-screen flex items-center justify-center">Loading...</div>
  }

  if (!user) {
    return <Navigate to="/login" />
  }

  if (isAdmin()) {
    return children
  }

  return hasGrantAssignmentsAccess() ? children : <Navigate to="/dashboard" />
}

const PaygroupAssignmentsRoute = ({ children }) => {
  const { user, loading, isAdmin, hasPaygroupAssignmentsAccess } = useAuth()

  if (loading) {
    return <div className="min-h-screen flex items-center justify-center">Loading...</div>
  }

  if (!user) {
    return <Navigate to="/login" />
  }

  if (isAdmin()) {
    return children
  }

  return hasPaygroupAssignmentsAccess() ? children : <Navigate to="/dashboard" />
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/change-password" element={<ChangePassword />} />
      <Route
        path="/"
        element={
          <PrivateRoute>
            <Layout />
          </PrivateRoute>
        }
      >
        <Route index element={<Navigate to="/dashboard" />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route
          path="employee-details"
          element={
            <AdminRoute>
              <EmployeeDetails />
            </AdminRoute>
          }
        />
        <Route
          path="users-details"
          element={
            <AdminRoute>
              <UsersDetails />
            </AdminRoute>
          }
        />
        <Route
          path="configuration"
          element={
            <AdminRoute>
              <Configuration />
            </AdminRoute>
          }
        />
        <Route
          path="organization-details"
          element={
            <AdminRoute>
              <OrganizationDetails />
            </AdminRoute>
          }
        />
        <Route
          path="company-assignments"
          element={
            <CompanyAssignmentsRoute>
              <CompanyAssignments />
            </CompanyAssignmentsRoute>
          }
        />
        <Route
          path="academic-unit-assignments"
          element={
            <AcademicUnitAssignmentsRoute>
              <AcademicUnitAssignments />
            </AcademicUnitAssignmentsRoute>
          }
        />
        <Route
          path="gift-assignments"
          element={
            <GiftAssignmentsRoute>
              <GiftAssignments />
            </GiftAssignmentsRoute>
          }
        />
        <Route
          path="location-assignments"
          element={
            <LocationAssignmentsRoute>
              <LocationAssignments />
            </LocationAssignmentsRoute>
          }
        />
        <Route
          path="project-assignments"
          element={
            <ProjectAssignmentsRoute>
              <ProjectAssignments />
            </ProjectAssignmentsRoute>
          }
        />
        <Route
          path="grant-assignments"
          element={
            <GrantAssignmentsRoute>
              <GrantAssignments />
            </GrantAssignmentsRoute>
          }
        />
        <Route
          path="paygroup-assignments"
          element={
            <PaygroupAssignmentsRoute>
              <PaygroupAssignments />
            </PaygroupAssignmentsRoute>
          }
        />
      </Route>
    </Routes>
  )
}

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  )
}

export default App

