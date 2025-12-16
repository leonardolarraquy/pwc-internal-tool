import React, { useState, useEffect } from 'react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card'
import { Button } from '../components/ui/button'
import { Input } from '../components/ui/input'
import { Label } from '../components/ui/label'
import { parameterAPI } from '../services/api'
import { Upload, Trash2, Loader2, ImageIcon } from 'lucide-react'

export const Configuration = () => {
  const [parameters, setParameters] = useState([])
  const [loading, setLoading] = useState(true)
  const [logoFile, setLogoFile] = useState(null)
  const [logoPreview, setLogoPreview] = useState(null)
  const [currentLogo, setCurrentLogo] = useState(null)
  const [uploading, setUploading] = useState(false)
  const [message, setMessage] = useState(null)

  useEffect(() => {
    loadParameters()
  }, [])

  const loadParameters = async () => {
    try {
      const response = await parameterAPI.getAll()
      setParameters(response.data)
      
      // Check if logo parameter exists
      const logoParam = response.data.find(p => p.paramKey === 'logo')
      if (logoParam) {
        setCurrentLogo(parameterAPI.getImageUrl('logo') + '?t=' + Date.now())
      }
    } catch (error) {
      console.error('Error loading parameters:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleFileSelect = (e) => {
    const file = e.target.files[0]
    if (file) {
      // Validate file type
      if (!file.type.startsWith('image/')) {
        setMessage({ type: 'error', text: 'Please select an image file' })
        return
      }
      // Validate file size (max 5MB)
      if (file.size > 5 * 1024 * 1024) {
        setMessage({ type: 'error', text: 'File size must be less than 5MB' })
        return
      }
      
      setLogoFile(file)
      const reader = new FileReader()
      reader.onloadend = () => setLogoPreview(reader.result)
      reader.readAsDataURL(file)
      setMessage(null)
    }
  }

  const handleUploadLogo = async () => {
    if (!logoFile) return
    
    setUploading(true)
    setMessage(null)
    try {
      await parameterAPI.uploadImage('logo', logoFile, 'Application Logo')
      setMessage({ type: 'success', text: 'Logo uploaded successfully!' })
      setCurrentLogo(parameterAPI.getImageUrl('logo') + '?t=' + Date.now())
      setLogoFile(null)
      setLogoPreview(null)
      // Dispatch event to notify other components
      window.dispatchEvent(new CustomEvent('logoUpdated'))
    } catch (error) {
      console.error('Error uploading logo:', error)
      setMessage({ type: 'error', text: 'Error uploading logo. Please try again.' })
    } finally {
      setUploading(false)
    }
  }

  const handleDeleteLogo = async () => {
    if (!confirm('Are you sure you want to delete the logo? The default logo will be used.')) return
    
    try {
      await parameterAPI.delete('logo')
      setCurrentLogo(null)
      setMessage({ type: 'success', text: 'Logo deleted successfully!' })
      window.dispatchEvent(new CustomEvent('logoUpdated'))
    } catch (error) {
      console.error('Error deleting logo:', error)
      setMessage({ type: 'error', text: 'Error deleting logo. Please try again.' })
    }
  }

  const handleCancelUpload = () => {
    setLogoFile(null)
    setLogoPreview(null)
    // Reset the file input
    const fileInput = document.getElementById('logo-upload')
    if (fileInput) fileInput.value = ''
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold">Configuration</h1>
        <p className="text-muted-foreground">System configuration and settings</p>
      </div>
      
      {message && (
        <div className={`p-4 rounded-md ${message.type === 'success' ? 'bg-green-100 text-green-800 border border-green-200' : 'bg-red-100 text-red-800 border border-red-200'}`}>
          {message.text}
        </div>
      )}
      
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <ImageIcon className="h-5 w-5" />
            Application Logo
          </CardTitle>
          <CardDescription>
            Upload a custom logo for the application. This will appear in the login page and navigation header.
            Recommended size: 200x60 pixels. Supported formats: PNG, JPG, SVG.
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-6">
          {/* Current Logo Section */}
          <div className="space-y-3">
            <Label className="text-sm font-medium">Current Logo</Label>
            <div className="flex items-center gap-4 p-4 bg-muted/50 rounded-lg">
              {loading ? (
                <div className="flex items-center gap-2 text-muted-foreground">
                  <Loader2 className="h-4 w-4 animate-spin" />
                  Loading...
                </div>
              ) : currentLogo ? (
                <>
                  <div className="bg-white border rounded-lg p-3 shadow-sm">
                    <img 
                      src={currentLogo} 
                      alt="Current Logo" 
                      className="h-12 w-auto object-contain"
                      onError={(e) => {
                        e.target.style.display = 'none'
                        setCurrentLogo(null)
                      }}
                    />
                  </div>
                  <Button variant="destructive" size="sm" onClick={handleDeleteLogo}>
                    <Trash2 className="h-4 w-4 mr-2" />
                    Remove Logo
                  </Button>
                </>
              ) : (
                <div className="text-muted-foreground italic">
                  Using default PWC logo
                </div>
              )}
            </div>
          </div>
          
          {/* Upload Section */}
          <div className="space-y-3 pt-4 border-t">
            <Label htmlFor="logo-upload" className="text-sm font-medium">
              {currentLogo ? 'Upload New Logo' : 'Upload Logo'}
            </Label>
            <div className="flex flex-col gap-4">
              <Input 
                id="logo-upload" 
                type="file" 
                accept="image/*"
                onChange={handleFileSelect}
                className="max-w-md cursor-pointer"
              />
              
              {/* Preview Section */}
              {logoPreview && (
                <div className="flex items-center gap-4 p-4 bg-blue-50 border border-blue-200 rounded-lg">
                  <div className="bg-white border rounded-lg p-3 shadow-sm">
                    <img 
                      src={logoPreview} 
                      alt="Preview" 
                      className="h-12 w-auto object-contain"
                    />
                  </div>
                  <div className="flex flex-col gap-1">
                    <span className="text-sm font-medium text-blue-800">Preview</span>
                    <span className="text-xs text-blue-600">{logoFile?.name}</span>
                  </div>
                </div>
              )}
              
              {/* Action Buttons */}
              {logoFile && (
                <div className="flex gap-2">
                  <Button onClick={handleUploadLogo} disabled={uploading}>
                    {uploading ? (
                      <>
                        <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                        Uploading...
                      </>
                    ) : (
                      <>
                        <Upload className="h-4 w-4 mr-2" />
                        Upload Logo
                      </>
                    )}
                  </Button>
                  <Button variant="outline" onClick={handleCancelUpload} disabled={uploading}>
                    Cancel
                  </Button>
                </div>
              )}
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Parameters Table - for future expansion */}
      {parameters.length > 0 && (
        <Card>
          <CardHeader>
            <CardTitle>All Parameters</CardTitle>
            <CardDescription>System configuration parameters</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="rounded-md border">
              <table className="w-full text-sm">
                <thead className="bg-muted/50">
                  <tr>
                    <th className="text-left p-3 font-medium">Key</th>
                    <th className="text-left p-3 font-medium">Type</th>
                    <th className="text-left p-3 font-medium">Description</th>
                  </tr>
                </thead>
                <tbody>
                  {parameters.map((param) => (
                    <tr key={param.id} className="border-t">
                      <td className="p-3 font-mono text-xs">{param.paramKey}</td>
                      <td className="p-3">
                        <span className="px-2 py-1 bg-muted rounded text-xs">
                          {param.paramType || 'text'}
                        </span>
                      </td>
                      <td className="p-3 text-muted-foreground">{param.description || '-'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  )
}
