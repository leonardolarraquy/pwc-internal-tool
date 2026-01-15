import React, { useState, useEffect } from 'react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card'
import { Button } from '../components/ui/button'
import { Input } from '../components/ui/input'
import { Label } from '../components/ui/label'
import { Checkbox } from '../components/ui/checkbox'
import { parameterAPI, organizationTypeAPI, fieldDefinitionAPI, reportAPI } from '../services/api'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '../components/ui/dialog'
import { Upload, Trash2, Loader2, ImageIcon, Type, Building2, Plus, Edit, FileSpreadsheet, Download } from 'lucide-react'

export const Configuration = () => {
  const [parameters, setParameters] = useState([])
  const [loading, setLoading] = useState(true)
  const [logoFile, setLogoFile] = useState(null)
  const [logoPreview, setLogoPreview] = useState(null)
  const [currentLogo, setCurrentLogo] = useState(null)
  const [uploading, setUploading] = useState(false)
  const [message, setMessage] = useState(null)
  const [loginTitle, setLoginTitle] = useState('Welcome Back')
  const [loginSubtitle, setLoginSubtitle] = useState('Sign in to your account to continue')
  const [savingTexts, setSavingTexts] = useState(false)
  
  // Organization Types state
  const [orgTypes, setOrgTypes] = useState([])
  const [loadingOrgTypes, setLoadingOrgTypes] = useState(true)
  
  // Organization Type Dialog state (includes field definitions)
  const [orgTypeDialogOpen, setOrgTypeDialogOpen] = useState(false)
  const [editingOrgType, setEditingOrgType] = useState(null)
  const [orgTypeForm, setOrgTypeForm] = useState({ name: '', slug: '', displayName: '', iconName: '', displayOrder: 0, active: true })
  const [fieldDefinitions, setFieldDefinitions] = useState([])
  const [loadingFields, setLoadingFields] = useState(false)
  
  // Field Definition Dialog state
  const [fieldDialogOpen, setFieldDialogOpen] = useState(false)
  const [editingField, setEditingField] = useState(null)
  const [fieldForm, setFieldForm] = useState({ fieldKey: '', fieldTitle: '', fieldDescription: '', displayOrder: 0, active: true })

  useEffect(() => {
    loadParameters()
    loadOrganizationTypes()
  }, [])

  const loadParameters = async () => {
    try {
      const response = await parameterAPI.getAll()
      setParameters(response.data)
      
      const logoParam = response.data.find(p => p.paramKey === 'logo')
      if (logoParam) {
        setCurrentLogo(parameterAPI.getImageUrl('logo') + '?t=' + Date.now())
      }
      
      const titleParam = response.data.find(p => p.paramKey === 'login_title')
      if (titleParam?.paramValue) setLoginTitle(titleParam.paramValue)
      const subtitleParam = response.data.find(p => p.paramKey === 'login_subtitle')
      if (subtitleParam?.paramValue) setLoginSubtitle(subtitleParam.paramValue)
    } catch (error) {
      console.error('Error loading parameters:', error)
    } finally {
      setLoading(false)
    }
  }

  const loadOrganizationTypes = async () => {
    try {
      const response = await organizationTypeAPI.getAll()
      setOrgTypes(response.data)
    } catch (error) {
      console.error('Error loading organization types:', error)
    } finally {
      setLoadingOrgTypes(false)
    }
  }

  const loadFieldDefinitions = async (orgTypeId) => {
    setLoadingFields(true)
    try {
      const response = await fieldDefinitionAPI.getByOrgTypeId(orgTypeId, false)
      setFieldDefinitions(response.data)
    } catch (error) {
      console.error('Error loading field definitions:', error)
    } finally {
      setLoadingFields(false)
    }
  }

  const handleFileSelect = (e) => {
    const file = e.target.files[0]
    if (file) {
      if (!file.type.startsWith('image/')) {
        setMessage({ type: 'error', text: 'Please select an image file' })
        return
      }
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
      window.dispatchEvent(new CustomEvent('logoUpdated'))
    } catch (error) {
      console.error('Error uploading logo:', error)
      setMessage({ type: 'error', text: 'Error uploading logo. Please try again.' })
    } finally {
      setUploading(false)
    }
  }

  const handleDeleteLogo = async () => {
    if (!confirm('Are you sure you want to delete the logo?')) return
    
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
    const fileInput = document.getElementById('logo-upload')
    if (fileInput) fileInput.value = ''
  }

  const handleSaveLoginTexts = async () => {
    setSavingTexts(true)
    setMessage(null)
    try {
      await Promise.all([
        parameterAPI.save({ paramKey: 'login_title', paramValue: loginTitle, paramType: 'text', description: 'Login page title' }),
        parameterAPI.save({ paramKey: 'login_subtitle', paramValue: loginSubtitle, paramType: 'text', description: 'Login page subtitle' })
      ])
      setMessage({ type: 'success', text: 'Login texts updated successfully!' })
      loadParameters()
    } catch (error) {
      console.error('Error saving login texts:', error)
      setMessage({ type: 'error', text: 'Error saving login texts. Please try again.' })
    } finally {
      setSavingTexts(false)
    }
  }

  // Organization Type handlers
  const handleOpenOrgTypeDialog = async (orgType = null) => {
    if (orgType) {
      setEditingOrgType(orgType)
      setOrgTypeForm({
        name: orgType.name,
        slug: orgType.slug,
        displayName: orgType.displayName,
        iconName: orgType.iconName || '',
        displayOrder: orgType.displayOrder || 0,
        active: orgType.active
      })
      // Load field definitions for this org type
      await loadFieldDefinitions(orgType.id)
    } else {
      setEditingOrgType(null)
      setOrgTypeForm({ name: '', slug: '', displayName: '', iconName: '', displayOrder: 0, active: true })
      setFieldDefinitions([])
    }
    setOrgTypeDialogOpen(true)
  }

  const handleCloseOrgTypeDialog = () => {
    setOrgTypeDialogOpen(false)
    setEditingOrgType(null)
    setFieldDefinitions([])
  }

  const handleSaveOrgType = async () => {
    try {
      if (editingOrgType) {
        await organizationTypeAPI.update(editingOrgType.id, orgTypeForm)
        setMessage({ type: 'success', text: 'Organization type updated!' })
      } else {
        const response = await organizationTypeAPI.create(orgTypeForm)
        setMessage({ type: 'success', text: 'Organization type created!' })
        // Set the newly created org type as editing so user can add fields
        setEditingOrgType(response.data)
      }
      loadOrganizationTypes()
    } catch (error) {
      setMessage({ type: 'error', text: error.response?.data?.message || 'Error saving organization type' })
    }
  }

  const handleToggleOrgTypeActive = async (orgType) => {
    try {
      await organizationTypeAPI.update(orgType.id, { active: !orgType.active })
      loadOrganizationTypes()
    } catch (error) {
      setMessage({ type: 'error', text: 'Error updating organization type' })
    }
  }

  // Field Definition handlers
  const handleOpenFieldDialog = (field = null) => {
    if (field) {
      setEditingField(field)
      setFieldForm({
        fieldKey: field.fieldKey,
        fieldTitle: field.fieldTitle,
        fieldDescription: field.fieldDescription || '',
        displayOrder: field.displayOrder || 0,
        active: field.active
      })
    } else {
      setEditingField(null)
      setFieldForm({ fieldKey: '', fieldTitle: '', fieldDescription: '', displayOrder: 0, active: true })
    }
    setFieldDialogOpen(true)
  }

  const handleSaveField = async () => {
    try {
      if (editingField) {
        await fieldDefinitionAPI.update(editingField.id, fieldForm)
        setMessage({ type: 'success', text: 'Field updated!' })
      } else {
        await fieldDefinitionAPI.create({ ...fieldForm, organizationTypeId: editingOrgType.id })
        setMessage({ type: 'success', text: 'Field created!' })
      }
      setFieldDialogOpen(false)
      loadFieldDefinitions(editingOrgType.id)
    } catch (error) {
      setMessage({ type: 'error', text: error.response?.data?.message || 'Error saving field' })
    }
  }

  const handleDeleteField = async (fieldId) => {
    if (!confirm('Are you sure you want to delete this field?')) return
    try {
      await fieldDefinitionAPI.hardDelete(fieldId)
      setMessage({ type: 'success', text: 'Field deleted!' })
      loadFieldDefinitions(editingOrgType.id)
    } catch (error) {
      setMessage({ type: 'error', text: 'Error deleting field' })
    }
  }

  const handleGenerateFullReport = async () => {
    try {
      setMessage({ type: 'info', text: 'Generating report...' })
      const response = await reportAPI.generateFullReport()
      
      // Create blob and download
      const blob = new Blob([response.data], { 
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
      })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      
      const filename = 'Assign_Roles.xlsx'
      
      link.setAttribute('download', filename)
      document.body.appendChild(link)
      link.click()
      link.remove()
      window.URL.revokeObjectURL(url)
      
      setMessage({ type: 'success', text: 'Report generated and downloaded successfully!' })
    } catch (error) {
      setMessage({ type: 'error', text: error.response?.data?.message || 'Error generating report' })
    }
  }

  return (
    <div className="space-y-6 max-w-full">
      <div>
        <h1 className="text-3xl font-bold">Configuration</h1>
        <p className="text-muted-foreground">System configuration and settings</p>
      </div>
      
      {message && (
        <div className={`p-4 rounded-md ${message.type === 'success' ? 'bg-green-100 text-green-800 border border-green-200' : 'bg-red-100 text-red-800 border border-red-200'}`}>
          {message.text}
        </div>
      )}
      
      {/* Organization Types Management */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Building2 className="h-5 w-5" />
            Organization Types
          </CardTitle>
          <CardDescription>
            Manage organization types and their assignment field definitions. Click on an organization type to edit it and manage its fields.
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-6">
          <div className="space-y-3">
            <div className="flex items-center justify-between">
              <Label className="text-sm font-medium">Organization Types</Label>
              <Button size="sm" onClick={() => handleOpenOrgTypeDialog()}>
                <Plus className="h-4 w-4 mr-1" /> Add Type
              </Button>
            </div>
            
            {loadingOrgTypes ? (
              <p className="text-muted-foreground">Loading...</p>
            ) : (
              <div className="rounded-md border overflow-x-auto">
                <table className="w-full text-sm">
                  <thead className="bg-muted/50">
                    <tr>
                      <th className="text-left p-3 font-medium">Name</th>
                      <th className="text-left p-3 font-medium">Slug</th>
                      <th className="text-left p-3 font-medium">Display Name</th>
                      <th className="text-left p-3 font-medium">Icon</th>
                      <th className="text-left p-3 font-medium">Active</th>
                      <th className="text-left p-3 font-medium">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {orgTypes.map((ot) => (
                      <tr key={ot.id} className="border-t">
                        <td className="p-3">{ot.name}</td>
                        <td className="p-3 font-mono text-xs">{ot.slug}</td>
                        <td className="p-3">{ot.displayName}</td>
                        <td className="p-3 font-mono text-xs">{ot.iconName || '-'}</td>
                        <td className="p-3">
                          <Checkbox
                            checked={ot.active}
                            onChange={() => handleToggleOrgTypeActive(ot)}
                          />
                        </td>
                        <td className="p-3">
                          <Button variant="ghost" size="icon" onClick={() => handleOpenOrgTypeDialog(ot)} title="Edit">
                            <Edit className="h-4 w-4" />
                          </Button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </CardContent>
      </Card>
      
      {/* Application Logo */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <ImageIcon className="h-5 w-5" />
            Application Logo
          </CardTitle>
          <CardDescription>
            Upload a custom logo for the application.
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-6">
          <div className="space-y-3">
            <Label className="text-sm font-medium">Current Logo</Label>
            <div className="flex items-center gap-4 p-4 bg-muted/50 rounded-lg">
              {loading ? (
                <div className="flex items-center gap-2 text-muted-foreground">
                  <Loader2 className="h-4 w-4 animate-spin" /> Loading...
                </div>
              ) : currentLogo ? (
                <>
                  <div className="bg-white border rounded-lg p-3 shadow-sm">
                    <img src={currentLogo} alt="Current Logo" className="h-12 w-auto object-contain"
                      onError={(e) => { e.target.style.display = 'none'; setCurrentLogo(null) }} />
                  </div>
                  <Button variant="destructive" size="sm" onClick={handleDeleteLogo}>
                    <Trash2 className="h-4 w-4 mr-2" /> Remove Logo
                  </Button>
                </>
              ) : (
                <div className="text-muted-foreground italic">Using default PWC logo</div>
              )}
            </div>
          </div>
          
          <div className="space-y-3 pt-4 border-t">
            <Label htmlFor="logo-upload" className="text-sm font-medium">
              {currentLogo ? 'Upload New Logo' : 'Upload Logo'}
            </Label>
            <div className="flex flex-col gap-4">
              <Input id="logo-upload" type="file" accept="image/*" onChange={handleFileSelect} className="max-w-md cursor-pointer" />
              
              {logoPreview && (
                <div className="flex items-center gap-4 p-4 bg-blue-50 border border-blue-200 rounded-lg">
                  <div className="bg-white border rounded-lg p-3 shadow-sm">
                    <img src={logoPreview} alt="Preview" className="h-12 w-auto object-contain" />
                  </div>
                  <div className="flex flex-col gap-1">
                    <span className="text-sm font-medium text-blue-800">Preview</span>
                    <span className="text-xs text-blue-600">{logoFile?.name}</span>
                  </div>
                </div>
              )}
              
              {logoFile && (
                <div className="flex gap-2">
                  <Button onClick={handleUploadLogo} disabled={uploading}>
                    {uploading ? <><Loader2 className="h-4 w-4 mr-2 animate-spin" /> Uploading...</> : <><Upload className="h-4 w-4 mr-2" /> Upload Logo</>}
                  </Button>
                  <Button variant="outline" onClick={handleCancelUpload} disabled={uploading}>Cancel</Button>
                </div>
              )}
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Login Page Texts */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Type className="h-5 w-5" />
            Login Page Texts
          </CardTitle>
          <CardDescription>Customize the welcome message displayed on the login page.</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="login-title">Title</Label>
            <Input id="login-title" value={loginTitle} onChange={(e) => setLoginTitle(e.target.value)} placeholder="Welcome Back" disabled={savingTexts} />
          </div>
          <div className="space-y-2">
            <Label htmlFor="login-subtitle">Subtitle</Label>
            <Input id="login-subtitle" value={loginSubtitle} onChange={(e) => setLoginSubtitle(e.target.value)} placeholder="Sign in to your account to continue" disabled={savingTexts} />
          </div>
          <Button onClick={handleSaveLoginTexts} disabled={savingTexts}>
            {savingTexts ? <><Loader2 className="h-4 w-4 mr-2 animate-spin" /> Saving...</> : 'Save Changes'}
          </Button>
        </CardContent>
      </Card>

      {/* Full Report Download */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <FileSpreadsheet className="h-5 w-5" />
            Excel Report
          </CardTitle>
          <CardDescription>
            Download a comprehensive Excel report with all assignments data. The report includes two sheets: Overview and Assign Roles.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <Button onClick={handleGenerateFullReport} className="w-full sm:w-auto">
            <Download className="h-4 w-4 mr-2" />
            Download Full Report
          </Button>
        </CardContent>
      </Card>

      {/* Organization Type Dialog - includes field definitions */}
      <Dialog open={orgTypeDialogOpen} onOpenChange={handleCloseOrgTypeDialog}>
        <DialogContent className="max-w-6xl max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>{editingOrgType ? 'Edit Organization Type' : 'Add Organization Type'}</DialogTitle>
            <DialogDescription>
              {editingOrgType ? 'Update the organization type details and manage its field definitions.' : 'Create a new organization type for assignments.'}
            </DialogDescription>
          </DialogHeader>
          
          {/* Organization Type Form */}
          <div className="grid gap-4 py-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="ot-name">Name (must match OrganizationDetail.organizationType)</Label>
                <Input id="ot-name" value={orgTypeForm.name} onChange={(e) => setOrgTypeForm({ ...orgTypeForm, name: e.target.value })} placeholder="Gift" />
              </div>
              <div className="space-y-2">
                <Label htmlFor="ot-slug">Slug (URL-friendly)</Label>
                <Input id="ot-slug" value={orgTypeForm.slug} onChange={(e) => setOrgTypeForm({ ...orgTypeForm, slug: e.target.value })} placeholder="gift" />
              </div>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="ot-display">Display Name</Label>
                <Input id="ot-display" value={orgTypeForm.displayName} onChange={(e) => setOrgTypeForm({ ...orgTypeForm, displayName: e.target.value })} placeholder="Gift Assignments" />
              </div>
              <div className="space-y-2">
                <Label htmlFor="ot-icon">Icon Name (Lucide icon)</Label>
                <Input id="ot-icon" value={orgTypeForm.iconName} onChange={(e) => setOrgTypeForm({ ...orgTypeForm, iconName: e.target.value })} placeholder="Gift, Building2, GraduationCap" />
              </div>
            </div>
            <div className="space-y-2">
              <Label htmlFor="ot-order">Display Order</Label>
              <Input id="ot-order" type="number" value={orgTypeForm.displayOrder} onChange={(e) => setOrgTypeForm({ ...orgTypeForm, displayOrder: parseInt(e.target.value) || 0 })} className="w-32" />
            </div>
            
            <div className="flex justify-end pt-2">
              <Button onClick={handleSaveOrgType}>
                {editingOrgType ? 'Update Organization Type' : 'Create Organization Type'}
              </Button>
            </div>
          </div>
          
          {/* Field Definitions Section - only show when editing */}
          {editingOrgType && (
            <div className="border-t pt-6 mt-2">
              <div className="flex items-center justify-between mb-4">
                <div>
                  <h3 className="text-lg font-semibold">Field Definitions</h3>
                  <p className="text-sm text-muted-foreground">Define the boolean fields that appear in the assignment form</p>
                </div>
                <Button size="sm" onClick={() => handleOpenFieldDialog()}>
                  <Plus className="h-4 w-4 mr-1" /> Add Field
                </Button>
              </div>
              
              {loadingFields ? (
                <p className="text-muted-foreground py-4">Loading fields...</p>
              ) : fieldDefinitions.length === 0 ? (
                <div className="text-center py-8 border rounded-md bg-muted/20">
                  <p className="text-muted-foreground">No field definitions yet.</p>
                  <p className="text-sm text-muted-foreground mt-1">Add fields to define what boolean options are available for assignments.</p>
                </div>
              ) : (
                <div className="rounded-md border overflow-x-auto">
                  <table className="w-full text-sm">
                    <thead className="bg-muted/50">
                      <tr>
                        <th className="text-left p-3 font-medium">Field Key</th>
                        <th className="text-left p-3 font-medium">Title</th>
                        <th className="text-left p-3 font-medium">Description</th>
                        <th className="text-left p-3 font-medium">Order</th>
                        <th className="text-left p-3 font-medium">Active</th>
                        <th className="text-left p-3 font-medium">Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {fieldDefinitions.map((fd) => (
                        <tr key={fd.id} className="border-t">
                          <td className="p-3 font-mono text-xs">{fd.fieldKey}</td>
                          <td className="p-3">{fd.fieldTitle}</td>
                          <td className="p-3 text-muted-foreground max-w-[200px] truncate" title={fd.fieldDescription}>
                            {fd.fieldDescription || '-'}
                          </td>
                          <td className="p-3">{fd.displayOrder}</td>
                          <td className="p-3">{fd.active ? 'Yes' : 'No'}</td>
                          <td className="p-3">
                            <div className="flex gap-1">
                              <Button variant="ghost" size="icon" onClick={() => handleOpenFieldDialog(fd)}>
                                <Edit className="h-4 w-4" />
                              </Button>
                              <Button variant="ghost" size="icon" onClick={() => handleDeleteField(fd.id)}>
                                <Trash2 className="h-4 w-4 text-destructive" />
                              </Button>
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          )}
          
          <DialogFooter className="mt-6">
            <Button variant="outline" onClick={handleCloseOrgTypeDialog}>Close</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Field Definition Dialog */}
      <Dialog open={fieldDialogOpen} onOpenChange={setFieldDialogOpen}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>{editingField ? 'Edit Field Definition' : 'Add Field Definition'}</DialogTitle>
            <DialogDescription>
              {editingField ? 'Update the field definition.' : 'Add a new boolean field for this organization type.'}
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="fd-key">Field Key (internal identifier)</Label>
              <Input id="fd-key" value={fieldForm.fieldKey} onChange={(e) => setFieldForm({ ...fieldForm, fieldKey: e.target.value })} placeholder="finGiftFinancialAnalyst" />
            </div>
            <div className="space-y-2">
              <Label htmlFor="fd-title">Field Title</Label>
              <Input id="fd-title" value={fieldForm.fieldTitle} onChange={(e) => setFieldForm({ ...fieldForm, fieldTitle: e.target.value })} placeholder="FIN_Gift_Financial_Analyst" />
            </div>
            <div className="space-y-2">
              <Label htmlFor="fd-desc">Description (optional, shown as tooltip)</Label>
              <textarea
                id="fd-desc"
                value={fieldForm.fieldDescription}
                onChange={(e) => setFieldForm({ ...fieldForm, fieldDescription: e.target.value })}
                placeholder="In Workday I am responsible for..."
                className="w-full min-h-[100px] px-3 py-2 border rounded-md text-sm"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="fd-order">Display Order</Label>
              <Input id="fd-order" type="number" value={fieldForm.displayOrder} onChange={(e) => setFieldForm({ ...fieldForm, displayOrder: parseInt(e.target.value) || 0 })} />
            </div>
            <div className="flex items-center space-x-2">
              <Checkbox id="fd-active" checked={fieldForm.active} onChange={(e) => setFieldForm({ ...fieldForm, active: e.target.checked })} />
              <Label htmlFor="fd-active">Active</Label>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setFieldDialogOpen(false)}>Cancel</Button>
            <Button onClick={handleSaveField}>Save</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}
