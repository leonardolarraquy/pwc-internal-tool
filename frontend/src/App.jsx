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
import { Assignments } from './pages/Assignments'

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
        {/* Dynamic assignments route - handles all organization types */}
        <Route
          path="assignments/:orgTypeSlug"
          element={
            <PrivateRoute>
              <Assignments />
            </PrivateRoute>
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
