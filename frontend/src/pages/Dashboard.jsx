import React, { useState, useEffect } from 'react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card'
import { useAuth } from '../contexts/AuthContext'
import { userAPI } from '../services/api'

export const Dashboard = () => {
  const { user } = useAuth()
  const [assignmentStats, setAssignmentStats] = useState({
    companyAssignments: 0,
    academicUnitAssignments: 0,
    giftAssignments: 0
  })

  useEffect(() => {
    loadAssignmentStats()
  }, [])

  const loadAssignmentStats = async () => {
    try {
      const response = await userAPI.getMyAssignmentStats()
      setAssignmentStats(response.data)
    } catch (error) {
      console.error('Error loading assignment stats:', error)
    }
  }

  return (
    <div className="space-y-6">
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
      </div>

      {/* Separator */}
      <div className="border-t my-6"></div>

      {/* Assignment Statistics */}
      <div>
        <h2 className="text-xl font-semibold mb-4">Assignment Statistics</h2>
        <div className="grid gap-4 md:grid-cols-3">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Company Assignments</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{assignmentStats.companyAssignments}</div>
              <p className="text-xs text-muted-foreground">Created by you</p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Academic Unit Assignments</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{assignmentStats.academicUnitAssignments}</div>
              <p className="text-xs text-muted-foreground">Created by you</p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Gift Assignments</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{assignmentStats.giftAssignments}</div>
              <p className="text-xs text-muted-foreground">Created by you</p>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}


