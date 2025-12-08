import React, { createContext, useState, useContext, useEffect } from 'react'

const AuthContext = createContext(null)

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      setUser(JSON.parse(storedUser))
    }
    setLoading(false)
  }, [])

  const login = (userData, token) => {
    localStorage.setItem('token', token)
    localStorage.setItem('user', JSON.stringify(userData))
    setUser(userData)
  }

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setUser(null)
  }

  const isAdmin = () => {
    return user?.role === 'ADMIN'
  }

  const hasCompanyAssignmentsAccess = () => {
    return user?.companyAssignmentsAccess === true
  }

  const hasAcademicUnitAssignmentsAccess = () => {
    return user?.academicUnitAssignmentsAccess === true
  }

  const hasGiftAssignmentsAccess = () => {
    return user?.giftAssignmentsAccess === true
  }

  return (
    <AuthContext.Provider value={{ 
      user, 
      login, 
      logout, 
      isAdmin, 
      hasCompanyAssignmentsAccess,
      hasAcademicUnitAssignmentsAccess,
      hasGiftAssignmentsAccess,
      loading 
    }}>
      {children}
    </AuthContext.Provider>
  )
}



