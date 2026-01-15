import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card'
import { useAuth } from '../contexts/AuthContext'
import { organizationTypeAPI, assignmentAPI } from '../services/api'
import { Users, ArrowRight } from 'lucide-react'
import { Button } from '../components/ui/button'

export const Dashboard = () => {
  const { user, isAdmin } = useAuth()
  const navigate = useNavigate()
  const [orgTypes, setOrgTypes] = useState([])
  const [assignmentCounts, setAssignmentCounts] = useState({})
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    try {
      const [orgTypesRes, statsRes] = await Promise.all([
        organizationTypeAPI.getActive(),
        assignmentAPI.getStats()
      ])
      setOrgTypes(orgTypesRes.data)
      setAssignmentCounts(statsRes.data)
    } catch (error) {
      console.error('Error loading dashboard data:', error)
    } finally {
      setLoading(false)
    }
  }

  // Filter organization types based on user access
  const getUserOrgTypes = () => {
    if (isAdmin()) {
      return orgTypes
    }
    const userOrgAccess = user?.organizationAccess || {}
    return orgTypes.filter(orgType => userOrgAccess[orgType.id] === true)
  }

  const userOrgTypes = getUserOrgTypes()

  return (
    <div className="space-y-6 max-w-full">
      <div>
        <h1 className="text-3xl font-bold">Dashboard</h1>
        <p className="text-muted-foreground">Welcome back, {user?.firstName}!</p>
      </div>
      
      <div className="grid gap-4 md:grid-cols-2">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Your Role</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{user?.role}</div>
            <p className="text-xs text-muted-foreground">Current access level</p>
          </CardContent>
        </Card>
        
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Active Organization Types</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{loading ? '...' : userOrgTypes.length}</div>
            <p className="text-xs text-muted-foreground">
              {isAdmin() ? 'Total available' : 'Available for you'}
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Separator */}
      <div className="border-t my-6"></div>

      {/* Organization Types Overview */}
      <div>
        <h2 className="text-xl font-semibold mb-4">Available Assignment Types</h2>
        {loading ? (
          <p className="text-muted-foreground">Loading...</p>
        ) : userOrgTypes.length === 0 ? (
          <Card>
            <CardContent className="py-8 text-center">
              <p className="text-muted-foreground">
                {isAdmin() 
                  ? 'No organization types configured. Go to Configuration to add them.'
                  : 'You don\'t have access to any assignment types. Contact your administrator.'}
              </p>
            </CardContent>
          </Card>
        ) : (
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {userOrgTypes.map(orgType => (
              <Card 
                key={orgType.id} 
                className="cursor-pointer hover:bg-muted/50 transition-colors"
                onClick={() => navigate(`/assignments/${orgType.slug}`)}
              >
                <CardHeader className="pb-2">
                  <CardTitle className="text-base font-medium">{orgType.displayName}</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2 text-muted-foreground">
                      <Users className="h-4 w-4" />
                      <span className="text-sm">
                        {assignmentCounts[orgType.id] || 0} assigned employees
                      </span>
                    </div>
                    <ArrowRight className="h-4 w-4 text-muted-foreground" />
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        )}
      </div>

      {isAdmin() && (
        <>
          <div className="border-t my-6"></div>
          <div>
            <h2 className="text-xl font-semibold mb-4">Admin Quick Actions</h2>
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
              <Card className="cursor-pointer hover:bg-muted/50 transition-colors" onClick={() => navigate('/users-details')}>
                <CardHeader>
                  <CardTitle className="text-sm">Manage Users</CardTitle>
                  <CardDescription>Create and manage user accounts</CardDescription>
                </CardHeader>
              </Card>
              <Card className="cursor-pointer hover:bg-muted/50 transition-colors" onClick={() => navigate('/employee-details')}>
                <CardHeader>
                  <CardTitle className="text-sm">Manage Employees</CardTitle>
                  <CardDescription>Import and manage employee data</CardDescription>
                </CardHeader>
              </Card>
              <Card className="cursor-pointer hover:bg-muted/50 transition-colors" onClick={() => navigate('/organization-details')}>
                <CardHeader>
                  <CardTitle className="text-sm">Organization Details</CardTitle>
                  <CardDescription>Manage organization records</CardDescription>
                </CardHeader>
              </Card>
              <Card className="cursor-pointer hover:bg-muted/50 transition-colors" onClick={() => navigate('/configuration')}>
                <CardHeader>
                  <CardTitle className="text-sm">Configuration</CardTitle>
                  <CardDescription>System settings and field definitions</CardDescription>
                </CardHeader>
              </Card>
            </div>
          </div>
        </>
      )}
    </div>
  )
}
