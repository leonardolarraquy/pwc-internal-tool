import React, { useState, useEffect } from 'react'
import { organizationDetailAPI } from '../services/api'
import { Button } from '../components/ui/button'
import { Input } from '../components/ui/input'
import { Label } from '../components/ui/label'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '../components/ui/table'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '../components/ui/dialog'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../components/ui/select'
import { Plus, Edit, Trash2, Upload, FileSpreadsheet } from 'lucide-react'
import * as XLSX from 'xlsx'

export const OrganizationDetails = () => {
  const [organizationTypes, setOrganizationTypes] = useState([])
  const [selectedOrganizationType, setSelectedOrganizationType] = useState('')
  const [allOrganizationDetails, setAllOrganizationDetails] = useState([])
  const [filteredOrganizationDetails, setFilteredOrganizationDetails] = useState([])
  const [organizationSearchInput, setOrganizationSearchInput] = useState('')
  const [loading, setLoading] = useState(false)
  
  // Dialog states for create/edit/delete organization detail
  const [dialogOpen, setDialogOpen] = useState(false)
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [importDialogOpen, setImportDialogOpen] = useState(false)
  const [editingOrganizationDetail, setEditingOrganizationDetail] = useState(null)
  const [selectedFile, setSelectedFile] = useState(null)
  const [importing, setImporting] = useState(false)
  
  const [formData, setFormData] = useState({
    legacyOrganizationName: '',
    organization: '',
    organizationType: '',
    referenceId: '',
  })

  // Load organization types on mount
  useEffect(() => {
    loadOrganizationTypes()
  }, [])

  // Load all organization details when organization type is selected
  useEffect(() => {
    if (selectedOrganizationType && selectedOrganizationType !== 'all') {
      loadAllOrganizationDetailsForType(selectedOrganizationType)
      // Pre-fill organization type in form data
      setFormData(prev => ({ ...prev, organizationType: selectedOrganizationType }))
    } else {
      setAllOrganizationDetails([])
      setFilteredOrganizationDetails([])
      setSelectedOrganizationType('')
    }
  }, [selectedOrganizationType])

  // Filter organization details locally when search input changes
  useEffect(() => {
    if (!organizationSearchInput.trim()) {
      setFilteredOrganizationDetails(allOrganizationDetails)
    } else {
      const searchLower = organizationSearchInput.toLowerCase()
      const filtered = allOrganizationDetails.filter(org => {
        const legacyName = (org.legacyOrganizationName || '').toLowerCase()
        const orgName = (org.organization || '').toLowerCase()
        const refId = (org.referenceId || '').toLowerCase()
        const id = (org.id || '').toString()
        
        return legacyName.includes(searchLower) ||
               orgName.includes(searchLower) ||
               refId.includes(searchLower) ||
               id.includes(searchLower)
      })
      setFilteredOrganizationDetails(filtered)
    }
  }, [organizationSearchInput, allOrganizationDetails])

  const loadOrganizationTypes = async () => {
    try {
      const response = await organizationDetailAPI.getOrganizationTypes()
      const types = (response.data.types || []).filter(type => type && type.trim() !== '')
      setOrganizationTypes(types)
    } catch (error) {
      console.error('Error loading organization types:', error)
      setOrganizationTypes([])
    }
  }

  const loadAllOrganizationDetailsForType = async (orgType) => {
    setLoading(true)
    try {
      // Load all organization details of the selected type (no pagination)
      const response = await organizationDetailAPI.getAll(0, 10000, 'id', 'asc', '', orgType)
      const allOrgs = response.data.content || []
      setAllOrganizationDetails(allOrgs)
      setFilteredOrganizationDetails(allOrgs)
    } catch (error) {
      console.error('Error loading organization details for type:', error)
      alert('Error loading organization details. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  const handleCreate = () => {
    setEditingOrganizationDetail(null)
    setFormData({
      legacyOrganizationName: '',
      organization: '',
      organizationType: selectedOrganizationType || '',
      referenceId: '',
    })
    setDialogOpen(true)
  }

  const handleEdit = (orgDetail) => {
    setEditingOrganizationDetail(orgDetail)
    setFormData({
      legacyOrganizationName: orgDetail.legacyOrganizationName || '',
      organization: orgDetail.organization || '',
      organizationType: orgDetail.organizationType || '',
      referenceId: orgDetail.referenceId || '',
    })
    setDialogOpen(true)
  }

  const handleSave = async () => {
    try {
      if (editingOrganizationDetail) {
        await organizationDetailAPI.update(editingOrganizationDetail.id, formData)
      } else {
        await organizationDetailAPI.create(formData)
      }
      setDialogOpen(false)
      // Reload organization details if same type
      if (selectedOrganizationType && formData.organizationType === selectedOrganizationType) {
        loadAllOrganizationDetailsForType(selectedOrganizationType)
      }
    } catch (error) {
      alert(error.response?.data?.message || 'Error saving organization detail')
    }
  }

  const handleDelete = async () => {
    try {
      await organizationDetailAPI.delete(editingOrganizationDetail.id)
      setDeleteDialogOpen(false)
      setEditingOrganizationDetail(null)
      // Reload organization details if same type
      if (selectedOrganizationType) {
        loadAllOrganizationDetailsForType(selectedOrganizationType)
      }
    } catch (error) {
      alert(error.response?.data?.message || 'Error deleting organization detail')
    }
  }

  const handleFileSelect = (e) => {
    setSelectedFile(e.target.files[0])
  }

  const handleImport = async () => {
    if (!selectedFile) {
      alert('Please select a CSV file')
      return
    }

    setImporting(true)
    try {
      const response = await organizationDetailAPI.importCSV(selectedFile)
      alert(`Successfully imported ${response.data.imported} organization details`)
      setImportDialogOpen(false)
      setSelectedFile(null)
      // Reload if we have a type selected
      if (selectedOrganizationType) {
        loadAllOrganizationDetailsForType(selectedOrganizationType)
      }
    } catch (error) {
      alert(error.response?.data?.message || 'Error importing CSV')
    } finally {
      setImporting(false)
    }
  }

  const handleExportExcel = async () => {
    try {
      setLoading(true)
      // Get all organization details without pagination (all types)
      const response = await organizationDetailAPI.getAll(0, 10000, 'id', 'asc', '', 'all')
      const allOrgs = response.data.content
      
      // Prepare data for Excel
      const excelData = allOrgs.map(org => ({
        'ID': org.id,
        'Legacy Organization Name': org.legacyOrganizationName || '',
        'Organization': org.organization || '',
        'Organization Type': org.organizationType || '',
        'Reference ID': org.referenceId || ''
      }))
      
      // Create workbook and worksheet
      const ws = XLSX.utils.json_to_sheet(excelData)
      const wb = XLSX.utils.book_new()
      XLSX.utils.book_append_sheet(wb, ws, 'Organizations')
      
      // Generate file and download
      const fileName = `organizations_${new Date().toISOString().split('T')[0]}.xlsx`
      XLSX.writeFile(wb, fileName)
    } catch (error) {
      console.error('Error exporting to Excel:', error)
      alert('Error exporting to Excel: ' + (error.response?.data?.message || error.message))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="space-y-6 max-w-full">
      <div className="flex items-center justify-between flex-wrap gap-4">
        <div>
          <h1 className="text-3xl font-bold">Organization Details</h1>
          <p className="text-muted-foreground">Manage organization details</p>
        </div>
        <div className="flex gap-2">
          <Button 
            onClick={handleCreate}
            disabled={!selectedOrganizationType || selectedOrganizationType === 'all'}
            title={!selectedOrganizationType || selectedOrganizationType === 'all' ? 'Please select an organization type first' : ''}
          >
            <Plus className="mr-2 h-4 w-4" />
            Add Organization
          </Button>
          <Button onClick={() => setImportDialogOpen(true)} variant="outline">
            <Upload className="mr-2 h-4 w-4" />
            Import CSV
          </Button>
          <Button onClick={handleExportExcel} variant="outline">
            <FileSpreadsheet className="mr-2 h-4 w-4" />
            Export Excel
          </Button>
        </div>
      </div>

      {/* Filter by Organization Type */}
      <div className="space-y-2 border-b pb-4">
        <Label htmlFor="orgTypeSelect" className="text-base font-semibold">Filter by Organization Type</Label>
        <Select 
          value={selectedOrganizationType} 
          onValueChange={(value) => {
            setSelectedOrganizationType(value)
            setOrganizationSearchInput('')
          }}
        >
          <SelectTrigger id="orgTypeSelect" className="w-64">
            <SelectValue placeholder="Select an organization type..." />
          </SelectTrigger>
          <SelectContent>
            {organizationTypes.length === 0 ? (
              <div className="px-2 py-1.5 text-sm text-muted-foreground">
                No organization types available
              </div>
            ) : (
              <>
                <SelectItem value="all">All Types</SelectItem>
                {organizationTypes
                  .filter(type => type && type.trim() !== '')
                  .map((type) => (
                    <SelectItem key={type} value={type}>
                      {type}
                    </SelectItem>
                  ))}
              </>
            )}
          </SelectContent>
        </Select>
        {selectedOrganizationType && selectedOrganizationType !== 'all' && (
          <p className="text-sm text-muted-foreground">
            {allOrganizationDetails.length} organization(s) available for {selectedOrganizationType}
          </p>
        )}
      </div>

      {selectedOrganizationType && selectedOrganizationType !== 'all' && (
        <>
          {/* Organization Search */}
          <div className="flex items-center gap-4">
            <div className="flex-1">
              <Label htmlFor="orgSearch">Search Organization Details (local search)</Label>
              <Input
                id="orgSearch"
                placeholder="Search by name, legacy name, reference ID, or ID..."
                value={organizationSearchInput}
                onChange={(e) => setOrganizationSearchInput(e.target.value)}
                className="mt-1"
              />
              <p className="text-xs text-muted-foreground mt-1">
                Showing {filteredOrganizationDetails.length} of {allOrganizationDetails.length} organization(s)
              </p>
            </div>
          </div>

          {/* Organization Details Table */}
          {loading ? (
            <div className="border rounded-lg p-8 text-center text-muted-foreground">
              Loading organization details...
            </div>
          ) : filteredOrganizationDetails.length === 0 ? (
            <div className="border rounded-lg p-8 text-center text-muted-foreground">
              No organization details found for {selectedOrganizationType}
            </div>
          ) : (
            <div className="rounded-md border overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead className="w-[200px]">Legacy Organization Name</TableHead>
                    <TableHead className="w-[200px]">Organization</TableHead>
                    <TableHead className="w-[150px]">Reference ID</TableHead>
                    <TableHead className="w-[150px]">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {filteredOrganizationDetails.map((orgDetail) => (
                    <TableRow key={orgDetail.id}>
                      <TableCell>{orgDetail.legacyOrganizationName || '-'}</TableCell>
                      <TableCell>{orgDetail.organization || '-'}</TableCell>
                      <TableCell>
                        {(() => {
                          const refId = orgDetail.referenceId;
                          if (!refId || refId.trim() === '' || refId.startsWith('#')) {
                            return '-';
                          }
                          return refId;
                        })()}
                      </TableCell>
                      <TableCell>
                        <div className="flex gap-1">
                          <Button
                            variant="ghost"
                            size="icon"
                            onClick={() => handleEdit(orgDetail)}
                            title="Edit organization detail"
                          >
                            <Edit className="h-4 w-4" />
                          </Button>
                          <Button
                            variant="ghost"
                            size="icon"
                            onClick={() => {
                              setEditingOrganizationDetail(orgDetail)
                              setDeleteDialogOpen(true)
                            }}
                            title="Delete organization detail"
                          >
                            <Trash2 className="h-4 w-4 text-destructive" />
                          </Button>
                        </div>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
          )}
        </>
      )}

      {/* Create/Edit Dialog */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>{editingOrganizationDetail ? 'Edit Organization Detail' : 'Create Organization Detail'}</DialogTitle>
            <DialogDescription>
              {editingOrganizationDetail ? 'Update organization detail information' : 'Add a new organization detail to the system'}
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="legacyOrganizationName">Legacy Organization Name (max 100 characters)</Label>
              <Input
                id="legacyOrganizationName"
                value={formData.legacyOrganizationName}
                onChange={(e) => setFormData({ ...formData, legacyOrganizationName: e.target.value.substring(0, 100) })}
                maxLength={100}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="organization">Organization (max 100 characters)</Label>
              <Input
                id="organization"
                value={formData.organization}
                onChange={(e) => setFormData({ ...formData, organization: e.target.value.substring(0, 100) })}
                maxLength={100}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="organizationType">Organization Type (max 100 characters)</Label>
              <Input
                id="organizationType"
                value={formData.organizationType}
                onChange={(e) => setFormData({ ...formData, organizationType: e.target.value.substring(0, 100) })}
                maxLength={100}
                disabled={!editingOrganizationDetail && !!selectedOrganizationType}
                title={!editingOrganizationDetail && !!selectedOrganizationType ? 'Organization type is locked to the selected type. Change the organization type filter to create a different type.' : ''}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="referenceId">Reference ID (max 100 characters)</Label>
              <Input
                id="referenceId"
                value={formData.referenceId}
                onChange={(e) => setFormData({ ...formData, referenceId: e.target.value.substring(0, 100) })}
                maxLength={100}
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDialogOpen(false)}>
              Cancel
            </Button>
            <Button onClick={handleSave}>Save</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Delete Organization Detail</DialogTitle>
            <DialogDescription>
              Are you sure you want to delete this organization detail? This action cannot be undone.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDeleteDialogOpen(false)}>
              Cancel
            </Button>
            <Button variant="destructive" onClick={handleDelete}>
              Delete
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Import CSV Dialog */}
      <Dialog open={importDialogOpen} onOpenChange={setImportDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Import Organization Details from CSV</DialogTitle>
            <DialogDescription>
              Upload a CSV file with organization detail data. Supported columns (case-insensitive): 
              Legacy Organization Name, Organization, Organization Type, Reference ID. All fields are optional.
            </DialogDescription>
          </DialogHeader>
          <div className="py-4">
            <Input
              type="file"
              accept=".csv"
              onChange={handleFileSelect}
              disabled={importing}
            />
          </div>
          <DialogFooter>
            <Button 
              variant="outline" 
              onClick={() => setImportDialogOpen(false)}
              disabled={importing}
            >
              Cancel
            </Button>
            <Button 
              onClick={handleImport} 
              disabled={!selectedFile || importing}
            >
              {importing ? 'Importing...' : 'Import'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}
