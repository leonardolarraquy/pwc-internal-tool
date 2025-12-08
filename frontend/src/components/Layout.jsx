import React from 'react'
import { Outlet, Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import { Button } from './ui/button'
import { PwcLogo } from './PwcLogo'
import { 
  LayoutDashboard, 
  Users, 
  Settings, 
  LogOut,
  Menu,
  X,
  Building2,
  GraduationCap,
  Gift,
  Building
} from 'lucide-react'
import { useState } from 'react'

export const Layout = () => {
  const { user, logout, isAdmin, hasCompanyAssignmentsAccess, hasAcademicUnitAssignmentsAccess, hasGiftAssignmentsAccess } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()
  const [sidebarOpen, setSidebarOpen] = useState(true)

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const menuItems = [
    { path: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  ]

  if (isAdmin()) {
    menuItems.push({ path: '/users', label: 'User Configuration', icon: Users })
    menuItems.push({ path: '/organization-details', label: 'Organization Details', icon: Building })
    menuItems.push({ path: '/configuration', label: 'Configuration', icon: Settings })
    // Admins always see all assignment pages
    menuItems.push({ path: '/company-assignments', label: 'Company Assignments', icon: Building2 })
    menuItems.push({ path: '/academic-unit-assignments', label: 'Academic Unit Assignments', icon: GraduationCap })
    menuItems.push({ path: '/gift-assignments', label: 'Gift Assignments', icon: Gift })
  } else {
    // Regular users only see pages they have access to
    if (hasCompanyAssignmentsAccess()) {
      menuItems.push({ path: '/company-assignments', label: 'Company Assignments', icon: Building2 })
    }

    if (hasAcademicUnitAssignmentsAccess()) {
      menuItems.push({ path: '/academic-unit-assignments', label: 'Academic Unit Assignments', icon: GraduationCap })
    }

    if (hasGiftAssignmentsAccess()) {
      menuItems.push({ path: '/gift-assignments', label: 'Gift Assignments', icon: Gift })
    }
  }

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="border-b bg-white sticky top-0 z-10">
        <div className="flex items-center justify-between px-6 py-4">
          <div className="flex items-center gap-4">
            <Button
              variant="ghost"
              size="icon"
              onClick={() => setSidebarOpen(!sidebarOpen)}
              className="md:hidden"
            >
              {sidebarOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
            </Button>
            <PwcLogo className="h-10" />
          </div>
          <div className="flex items-center gap-4">
            <span className="text-sm text-muted-foreground">
              {user?.firstName} {user?.lastName}
            </span>
            <Button variant="ghost" size="icon" onClick={handleLogout}>
              <LogOut className="h-5 w-5" />
            </Button>
          </div>
        </div>
      </header>

      <div className="flex">
        {/* Sidebar */}
        <aside
          className={`${
            sidebarOpen ? 'translate-x-0' : '-translate-x-full'
          } fixed md:sticky md:translate-x-0 top-[73px] left-0 h-[calc(100vh-73px)] w-64 border-r bg-white transition-transform duration-200 z-20`}
        >
          <nav className="p-4 space-y-2">
            {menuItems.map((item) => {
              const Icon = item.icon
              const isActive = location.pathname === item.path
              return (
                <Link
                  key={item.path}
                  to={item.path}
                  className={`flex items-center gap-3 px-4 py-2 rounded-lg transition-colors ${
                    isActive
                      ? 'bg-primary text-primary-foreground'
                      : 'text-muted-foreground hover:bg-accent hover:text-accent-foreground'
                  }`}
                  onClick={() => {
                    if (window.innerWidth < 768) {
                      setSidebarOpen(false)
                    }
                  }}
                >
                  <Icon className="h-5 w-5" />
                  <span>{item.label}</span>
                </Link>
              )
            })}
          </nav>
        </aside>

        {/* Main Content */}
        <main className="flex-1 p-6">
          <Outlet />
        </main>
      </div>

      {/* Overlay for mobile */}
      {sidebarOpen && (
        <div
          className="fixed inset-0 bg-black/50 z-10 md:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}
    </div>
  )
}


