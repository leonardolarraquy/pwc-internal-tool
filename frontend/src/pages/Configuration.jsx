import React from 'react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card'

export const Configuration = () => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold">Configuration</h1>
        <p className="text-muted-foreground">System configuration and settings</p>
      </div>
      
      <Card>
        <CardHeader>
          <CardTitle>System Settings</CardTitle>
          <CardDescription>Configure system-wide settings</CardDescription>
        </CardHeader>
        <CardContent>
          <p className="text-muted-foreground">Configuration options will be available here.</p>
        </CardContent>
      </Card>
    </div>
  )
}








