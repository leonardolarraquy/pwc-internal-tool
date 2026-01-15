import React, { useState, useEffect } from 'react'
import { Outlet, Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import { organizationTypeAPI } from '../services/api'
import { Button } from './ui/button'
import { PwcLogo } from './PwcLogo'
import { 
  LayoutDashboard, 
  Users, 
  Settings, 
  LogOut,
  Menu,
  X,
  Building,
  UsersRound,
  Gift,
  GraduationCap,
  Building2,
  MapPin,
  FolderKanban,
  Award,
  Wallet,
  DollarSign,
  Layers
} from 'lucide-react'

// Map icon names to Lucide components
const iconMap = {
  Gift: Gift,
  GraduationCap: GraduationCap,
  Building2: Building2,
  MapPin: MapPin,
  FolderKanban: FolderKanban,
  Award: Award,
  Users: Users,
  UsersRound: UsersRound,
  Wallet: Wallet,
  DollarSign: DollarSign,
  Layers: Layers,
  Building: Building,
  LayoutDashboard: LayoutDashboard,
  Settings: Settings,
}

const getIcon = (iconName) => {
  return iconMap[iconName] || Building2
}

export const Layout = () => {
  const { user, logout, isAdmin } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()
  const [sidebarOpen, setSidebarOpen] = useState(true)
  const [orgTypes, setOrgTypes] = useState([])
  const [loadingOrgTypes, setLoadingOrgTypes] = useState(true)

  useEffect(() => {
    loadOrganizationTypes()
  }, [])

  const loadOrganizationTypes = async () => {
    try {
      const response = await organizationTypeAPI.getActive()
      setOrgTypes(response.data)
    } catch (error) {
      console.error('Error loading organization types:', error)
    } finally {
      setLoadingOrgTypes(false)
    }
  }

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  // Build menu items dynamically
  const menuItems = [
    { path: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  ]

  if (isAdmin()) {
    menuItems.push({ path: '/users-details', label: 'Users Details', icon: UsersRound })
    menuItems.push({ path: '/organization-details', label: 'Organization Details', icon: Building })
    menuItems.push({ path: '/employee-details', label: 'Employee Details', icon: Users })
    
    // Add dynamic assignment pages from organization types - admin sees all
    orgTypes.forEach(orgType => {
      menuItems.push({
        path: `/assignments/${orgType.slug}`,
        label: orgType.displayName,
        icon: getIcon(orgType.iconName)
      })
    })
    
    // Configuration at the end
    menuItems.push({ path: '/configuration', label: 'Configuration', icon: Settings })
  } else {
    // Regular users - only show organization types they have access to
    const userOrgAccess = user?.organizationAccess || {}
    orgTypes.forEach(orgType => {
      // Check if user has access to this organization type
      if (userOrgAccess[orgType.id] === true) {
        menuItems.push({
          path: `/assignments/${orgType.slug}`,
          label: orgType.displayName,
          icon: getIcon(orgType.iconName)
        })
      }
    })
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
          } fixed md:sticky md:translate-x-0 top-[73px] left-0 h-[calc(100vh-73px)] w-64 border-r bg-white transition-transform duration-200 z-20 overflow-y-auto`}
        >
          <nav className="p-4 space-y-2">
            {loadingOrgTypes ? (
              <div className="px-4 py-2 text-muted-foreground text-sm">Loading...</div>
            ) : (
              menuItems.map((item) => {
                const Icon = item.icon
                const isActive = location.pathname === item.path || 
                  (item.path.startsWith('/assignments/') && location.pathname.startsWith(item.path))
                
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
              })
            )}
          </nav>
        </aside>

        {/* Main Content */}
        <main className="flex-1 p-6 overflow-x-hidden min-w-0">
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
