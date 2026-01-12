import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import { authAPI, parameterAPI } from '../services/api'
import { Button } from '../components/ui/button'
import { Input } from '../components/ui/input'
import { Label } from '../components/ui/label'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card'
import { PwcLogo } from '../components/PwcLogo'

export const Login = () => {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [loginTitle, setLoginTitle] = useState('Welcome Back')
  const [loginSubtitle, setLoginSubtitle] = useState('Sign in to your account to continue')
  const { login } = useAuth()
  const navigate = useNavigate()

  useEffect(() => {
    const loadLoginTexts = async () => {
      try {
        const [titleRes, subtitleRes] = await Promise.all([
          parameterAPI.getByKey('login_title').catch(() => null),
          parameterAPI.getByKey('login_subtitle').catch(() => null)
        ])
        if (titleRes?.data?.paramValue) setLoginTitle(titleRes.data.paramValue)
        if (subtitleRes?.data?.paramValue) setLoginSubtitle(subtitleRes.data.paramValue)
      } catch (error) {
        // Use defaults on error
      }
    }
    loadLoginTexts()
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      const response = await authAPI.login(email, password)
      const { 
        token, 
        email: userEmail, 
        role, 
        firstName, 
        lastName, 
        mustChangePassword,
        companyAssignmentsAccess,
        academicUnitAssignmentsAccess,
        giftAssignmentsAccess
      } = response.data
      
      const userData = {
        email: userEmail,
        role,
        firstName,
        lastName,
        companyAssignmentsAccess: companyAssignmentsAccess || false,
        academicUnitAssignmentsAccess: academicUnitAssignmentsAccess || false,
        giftAssignmentsAccess: giftAssignmentsAccess || false
      }
      
      if (mustChangePassword) {
        // User must change password, redirect to change password page
        login(userData, token)
        navigate('/change-password', { state: { email: userEmail, isFirstLogin: true } })
      } else {
        // Normal login
        login(userData, token)
        navigate('/dashboard')
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid email or password')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-blue-100 p-4">
      <Card className="w-full max-w-md">
        <CardHeader className="space-y-4">
          <div className="flex justify-center mb-4">
            <PwcLogo className="h-16" />
          </div>
          <CardTitle className="text-2xl text-center">{loginTitle}</CardTitle>
          <CardDescription className="text-center">
            {loginSubtitle}
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            {error && (
              <div className="bg-destructive/15 text-destructive text-sm p-3 rounded-md">
                {error}
              </div>
            )}
            <div className="space-y-2">
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                type="email"
                placeholder="admin@pwc.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                disabled={loading}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="password">Password</Label>
              <Input
                id="password"
                type="password"
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                disabled={loading}
              />
            </div>
            <Button type="submit" className="w-full" disabled={loading}>
              {loading ? 'Signing in...' : 'Sign In'}
            </Button>
            <div className="text-sm text-muted-foreground text-center">
              <p>Default admin: admin@pwc.com / admin123</p>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}

