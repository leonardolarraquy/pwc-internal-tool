import React, { useState, useEffect } from 'react'
import { parameterAPI } from '../services/api'

export const PwcLogo = ({ className = "h-8 w-auto" }) => {
  const [logoUrl, setLogoUrl] = useState(null)
  const [loading, setLoading] = useState(true)
  const [hasError, setHasError] = useState(false)

  const checkLogo = async () => {
    try {
      const response = await parameterAPI.getByKey('logo')
      if (response.data && response.data.paramValue) {
        setLogoUrl(parameterAPI.getImageUrl('logo') + '?t=' + Date.now())
        setHasError(false)
      } else {
        setLogoUrl(null)
      }
    } catch (error) {
      // Parameter not found - use default logo
      setLogoUrl(null)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    checkLogo()
    
    // Listen for logo updates
    const handleLogoUpdate = () => {
      setLoading(true)
      checkLogo()
    }
    window.addEventListener('logoUpdated', handleLogoUpdate)
    return () => window.removeEventListener('logoUpdated', handleLogoUpdate)
  }, [])

  // Handle image load error - fallback to default logo
  const handleImageError = () => {
    setHasError(true)
    setLogoUrl(null)
  }

  // If custom logo exists and hasn't errored, show it
  if (logoUrl && !hasError && !loading) {
    return (
      <div className={`flex items-center ${className}`}>
        <img 
          src={logoUrl} 
          alt="Logo" 
          className="h-full w-auto object-contain"
          onError={handleImageError}
        />
      </div>
    )
  }

  // Default PWC logo
  return (
    <div className={`flex items-center ${className}`}>
      <svg
        viewBox="0 0 200 60"
        className="h-full w-full"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
      >
        {/* PWC Text Logo */}
        <text
          x="10"
          y="40"
          fontSize="40"
          fontWeight="bold"
          fill="#3B82F6"
          fontFamily="Arial, sans-serif"
        >
          PWC
        </text>
        {/* Optional: Add a simple icon */}
        <circle cx="160" cy="30" r="15" fill="#3B82F6" opacity="0.2" />
        <circle cx="160" cy="30" r="8" fill="#3B82F6" />
      </svg>
    </div>
  )
}
