import React, { useState, useEffect } from 'react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card'
import { useAuth } from '../contexts/AuthContext'
import { userAPI } from '../services/api'

export const Dashboard = () => {
  const { user } = useAuth()
  const [stats, setStats] = useState({ total: 0, admin: 0, user: 0 })
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadStats()
  }, [])

  const loadStats = async () => {
    try {
      setLoading(true)
      const response = await userAPI.getStats()
      setStats(response.data)
    } catch (error) {
      console.error('Error loading stats:', error)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold">Dashboard</h1>
        <p className="text-muted-foreground">Welcome back, {user?.firstName}!</p>
      </div>
      
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Users</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{loading ? '-' : stats.total}</div>
            <div className="text-xs text-muted-foreground mt-2 space-y-1">
              <div>ADMIN: {loading ? '-' : stats.admin}</div>
              <div>USER: {loading ? '-' : stats.user}</div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Your Role</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{user?.role}</div>
            <p className="text-xs text-muted-foreground">Current access level</p>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}


