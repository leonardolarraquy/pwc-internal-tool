import React from 'react'

export const PwcLogo = ({ className = "h-8 w-auto" }) => {
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











