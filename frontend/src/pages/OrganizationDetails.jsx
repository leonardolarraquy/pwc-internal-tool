import React, { useState, useEffect } from 'react'
import { organizationDetailAPI, userAPI } from '../services/api'
import { Button } from '../components/ui/button'
import { Input } from '../components/ui/input'
import { Label } from '../components/ui/label'
import { Checkbox } from '../components/ui/checkbox'
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
import { Plus, Edit, Trash2, Upload, ChevronLeft, ChevronRight, ArrowUpDown, UserPlus } from 'lucide-react'

export const OrganizationDetails = () => {
  const [organizationDetails, setOrganizationDetails] = useState([])
  const [loading, setLoading] = useState(false)
  const [page, setPage] = useState(0)
  const [size, setSize] = useState(100)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [sortBy, setSortBy] = useState('id')
  const [sortDir, setSortDir] = useState('asc')
  const [search, setSearch] = useState('')
  const [searchInput, setSearchInput] = useState('')
  const [organizationTypeFilter, setOrganizationTypeFilter] = useState('all')
  const [organizationTypes, setOrganizationTypes] = useState([])
  
  const [dialogOpen, setDialogOpen] = useState(false)
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [importDialogOpen, setImportDialogOpen] = useState(false)
  const [addEmployeeDialogOpen, setAddEmployeeDialogOpen] = useState(false)
  const [editingOrganizationDetail, setEditingOrganizationDetail] = useState(null)
  const [selectedOrgDetailForEmployee, setSelectedOrgDetailForEmployee] = useState(null)
  const [selectedFile, setSelectedFile] = useState(null)
  const [importing, setImporting] = useState(false)
  
  // Add Employee state
  const [userSearchQuery, setUserSearchQuery] = useState('')
  const [searchedUsers, setSearchedUsers] = useState([])
  const [selectedUser, setSelectedUser] = useState(null)
  const [searchingUser, setSearchingUser] = useState(false)
  const [assigningUser, setAssigningUser] = useState(false)
  
  const [formData, setFormData] = useState({
    legacyOrganizationName: '',
    organization: '',
    organizationType: '',
    referenceId: '',
  })

  useEffect(() => {
    loadOrganizationDetails()
  }, [page, size, sortBy, sortDir, search, organizationTypeFilter])

  useEffect(() => {
    loadOrganizationTypes()
  }, [])

  const loadOrganizationDetails = async () => {
    setLoading(true)
    try {
      const response = await organizationDetailAPI.getAll(page, size, sortBy, sortDir, search, organizationTypeFilter)
      const data = response.data
      setOrganizationDetails(data.content)
      setTotalPages(data.totalPages)
      setTotalElements(data.totalElements)
    } catch (error) {
      console.error('Error loading organization details:', error)
    } finally {
      setLoading(false)
    }
  }

  const loadOrganizationTypes = async () => {
    try {
      const response = await organizationDetailAPI.getOrganizationTypes()
      setOrganizationTypes(response.data.types || [])
    } catch (error) {
      console.error('Error loading organization types:', error)
    }
  }

  const handleSearch = () => {
    setSearch(searchInput)
    setPage(0)
  }

  const handleSort = (column) => {
    if (sortBy === column) {
      setSortDir(sortDir === 'asc' ? 'desc' : 'asc')
    } else {
      setSortBy(column)
      setSortDir('asc')
    }
    setPage(0)
  }

  const handleCreate = () => {
    setEditingOrganizationDetail(null)
    setFormData({
      legacyOrganizationName: '',
      organization: '',
      organizationType: '',
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
      loadOrganizationDetails()
    } catch (error) {
      alert(error.response?.data?.message || 'Error saving organization detail')
    }
  }

  const handleDelete = async () => {
    try {
      await organizationDetailAPI.delete(editingOrganizationDetail.id)
      setDeleteDialogOpen(false)
      setEditingOrganizationDetail(null)
      loadOrganizationDetails()
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
      loadOrganizationDetails()
    } catch (error) {
      alert(error.response?.data?.message || 'Error importing CSV')
    } finally {
      setImporting(false)
    }
  }

  const handleSearchUser = async () => {
    if (!userSearchQuery.trim()) {
      alert('Please enter a Worker ID, Email ID, or Position ID')
      return
    }

    setSearchingUser(true)
    setSelectedUser(null)
    setSearchedUsers([])
    try {
      const response = await userAPI.search(userSearchQuery.trim())
      const users = Array.isArray(response.data) ? response.data : []
      
      console.log('Search results count:', users.length) // Debug log
      console.log('Search results data:', users) // Debug log
      console.log('Response structure:', response) // Debug log
      
      if (users.length === 0) {
        alert('No users found with the provided Worker ID, Email ID, or Position ID')
        setSearchedUsers([])
        setSelectedUser(null)
      } else if (users.length === 1) {
        // Si hay un solo resultado, seleccionarlo automáticamente
        setSearchedUsers(users)
        setSelectedUser(users[0])
      } else {
        // Si hay múltiples resultados, mostrar el desplegable
        setSearchedUsers(users)
        setSelectedUser(null) // No seleccionar automáticamente cuando hay múltiples
      }
    } catch (error) {
      console.error('Search error:', error)
      alert(error.response?.data?.message || 'Error searching for user. Please try again.')
      setSearchedUsers([])
      setSelectedUser(null)
    } finally {
      setSearchingUser(false)
    }
  }

  const handleAssignUser = async (assignmentData) => {
    if (!selectedOrgDetailForEmployee || !selectedUser) {
      return
    }

    setAssigningUser(true)
    try {
      const data = {
        userId: selectedUser.id,
        ...assignmentData
      }
      await organizationDetailAPI.assignUser(selectedOrgDetailForEmployee.id, data)
      alert('Employee successfully assigned to organization')
      setAddEmployeeDialogOpen(false)
      setSearchedUsers([])
      setSelectedUser(null)
      setUserSearchQuery('')
      setSelectedOrgDetailForEmployee(null)
    } catch (error) {
      alert(error.response?.data?.message || 'Error assigning employee')
    } finally {
      setAssigningUser(false)
    }
  }

  const SortableHeader = ({ column, children }) => (
    <TableHead className="cursor-pointer" onClick={() => handleSort(column)}>
      <div className="flex items-center gap-2">
        {children}
        <ArrowUpDown className="h-4 w-4" />
      </div>
    </TableHead>
  )

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Organization Details</h1>
          <p className="text-muted-foreground">Manage organization details and information</p>
        </div>
        <div className="flex gap-2">
          <Button onClick={() => setImportDialogOpen(true)} variant="outline">
            <Upload className="mr-2 h-4 w-4" />
            Import CSV
          </Button>
          <Button onClick={handleCreate}>
            <Plus className="mr-2 h-4 w-4" />
            Add Organization Detail
          </Button>
        </div>
      </div>

      {/* Search and Filters */}
      <div className="flex items-center gap-4">
        <div className="flex-1 flex gap-2">
          <Input
            placeholder="Search organization details..."
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
          />
          <Button onClick={handleSearch}>Search</Button>
        </div>
        <Select value={organizationTypeFilter} onValueChange={(val) => { setOrganizationTypeFilter(val); setPage(0) }}>
          <SelectTrigger className="w-48">
            <SelectValue placeholder="Filter by type" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">All Types</SelectItem>
            {organizationTypes.map((type) => (
              <SelectItem key={type} value={type}>
                {type}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
        <Select value={size.toString()} onValueChange={(val) => { setSize(parseInt(val)); setPage(0) }}>
          <SelectTrigger className="w-32">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="10">10 / page</SelectItem>
            <SelectItem value="50">50 / page</SelectItem>
            <SelectItem value="100">100 / page</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* Organization Details Table */}
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <SortableHeader column="id">ID</SortableHeader>
              <SortableHeader column="legacyOrganizationName">Legacy Organization Name</SortableHeader>
              <SortableHeader column="organization">Organization</SortableHeader>
              <SortableHeader column="organizationType">Organization Type</SortableHeader>
              <SortableHeader column="referenceId">Reference ID</SortableHeader>
              <TableHead>Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {loading ? (
              <TableRow>
                <TableCell colSpan={6} className="text-center py-8">
                  Loading...
                </TableCell>
              </TableRow>
            ) : organizationDetails.length === 0 ? (
              <TableRow>
                <TableCell colSpan={6} className="text-center py-8">
                  No organization details found
                </TableCell>
              </TableRow>
            ) : (
              organizationDetails.map((orgDetail) => (
                <TableRow key={orgDetail.id}>
                  <TableCell>{orgDetail.id}</TableCell>
                  <TableCell>{orgDetail.legacyOrganizationName || '-'}</TableCell>
                  <TableCell>{orgDetail.organization || '-'}</TableCell>
                  <TableCell>{orgDetail.organizationType || '-'}</TableCell>
                  <TableCell>
                    {(() => {
                      const refId = orgDetail.referenceId;
                      // Clean up Excel error values like "#VALUE!", "#REF!", "#N/A", etc.
                      if (!refId || refId.trim() === '' || refId.startsWith('#')) {
                        return '-';
                      }
                      return refId;
                    })()}
                  </TableCell>
                  <TableCell>
                    <div className="flex gap-2">
                      {(orgDetail.organizationType === 'Gift' || 
                        orgDetail.organizationType === 'Company' || 
                        orgDetail.organizationType === 'Academic Unit') && (
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => {
                            setSelectedOrgDetailForEmployee(orgDetail)
                            setAddEmployeeDialogOpen(true)
                            setSearchedUsers([])
                            setSelectedUser(null)
                            setUserSearchQuery('')
                          }}
                          title="Add Employee"
                        >
                          <UserPlus className="mr-1 h-3 w-3" />
                          Add Employee
                        </Button>
                      )}
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
              ))
            )}
          </TableBody>
        </Table>
      </div>

      {/* Pagination */}
      <div className="flex items-center justify-between">
        <div className="text-sm text-muted-foreground">
          Showing {page * size + 1} to {Math.min((page + 1) * size, totalElements)} of {totalElements} organization details
        </div>
        <div className="flex gap-2">
          <Button
            variant="outline"
            onClick={() => setPage(page - 1)}
            disabled={page === 0}
          >
            <ChevronLeft className="h-4 w-4" />
            Previous
          </Button>
          <div className="flex items-center gap-2">
            {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
              let pageNum
              if (totalPages <= 5) {
                pageNum = i
              } else if (page < 3) {
                pageNum = i
              } else if (page > totalPages - 4) {
                pageNum = totalPages - 5 + i
              } else {
                pageNum = page - 2 + i
              }
              return (
                <Button
                  key={pageNum}
                  variant={page === pageNum ? 'default' : 'outline'}
                  size="sm"
                  onClick={() => setPage(pageNum)}
                >
                  {pageNum + 1}
                </Button>
              )
            })}
          </div>
          <Button
            variant="outline"
            onClick={() => setPage(page + 1)}
            disabled={page >= totalPages - 1}
          >
            Next
            <ChevronRight className="h-4 w-4" />
          </Button>
        </div>
      </div>

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

      {/* Add Employee Dialog */}
      <Dialog open={addEmployeeDialogOpen} onOpenChange={setAddEmployeeDialogOpen}>
        <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>Add Employee to {selectedOrgDetailForEmployee?.organizationType || 'Organization'}</DialogTitle>
            <DialogDescription>
              Search for an employee by Worker ID, Email ID, or Position ID
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            {/* User Search */}
            <div className="space-y-2">
              <Label htmlFor="userSearch">Worker ID / Email ID / Position ID</Label>
              <div className="flex gap-2">
                <Input
                  id="userSearch"
                  value={userSearchQuery}
                  onChange={(e) => setUserSearchQuery(e.target.value)}
                  onKeyPress={(e) => e.key === 'Enter' && handleSearchUser()}
                  placeholder="Enter Worker ID, Email ID, or Position ID"
                  disabled={searchingUser}
                />
                <Button onClick={handleSearchUser} disabled={searchingUser || !userSearchQuery.trim()}>
                  {searchingUser ? 'Searching...' : 'Search'}
                </Button>
              </div>
            </div>

            {/* Multiple Users Selection */}
            {searchedUsers.length > 1 && (
              <div className="space-y-2 border rounded-lg p-4 bg-yellow-50">
                <Label htmlFor="userSelect" className="text-base font-semibold text-yellow-800">
                  {searchedUsers.length} users found. Please select one to continue:
                </Label>
                <Select 
                  value={selectedUser?.id?.toString() || ''} 
                  onValueChange={(value) => {
                    const user = searchedUsers.find(u => u.id && u.id.toString() === value)
                    if (user) {
                      setSelectedUser(user)
                    } else {
                      setSelectedUser(null)
                    }
                  }}
                >
                  <SelectTrigger id="userSelect" className="w-full">
                    <SelectValue placeholder="Select a user from the list" />
                  </SelectTrigger>
                  <SelectContent>
                    {searchedUsers.map((user) => (
                      <SelectItem key={user.id} value={user.id.toString()}>
                        {user.firstName || ''} {user.lastName || ''} ({user.email || ''}) - {user.employeeId || ''}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                {!selectedUser && (
                  <p className="text-sm text-muted-foreground mt-2">
                    Please select a user from the dropdown above to see their details and assign them.
                  </p>
                )}
              </div>
            )}

            {/* User Info - Only show when a user is selected and has valid data */}
            {selectedUser && selectedUser.id && selectedUser.firstName && (
              <div className="space-y-4 border rounded-lg p-4">
                <div>
                  <h4 className="font-semibold mb-2">Employee Information</h4>
                  <div className="grid grid-cols-2 gap-2 text-sm">
                    <div><strong>First Name:</strong> {selectedUser.firstName}</div>
                    <div><strong>Last Name:</strong> {selectedUser.lastName}</div>
                    <div><strong>Email:</strong> {selectedUser.email}</div>
                    <div><strong>Employee ID:</strong> {selectedUser.employeeId}</div>
                  </div>
                </div>

                {/* Role-specific fields */}
                {selectedOrgDetailForEmployee?.organizationType === 'Gift' && (
                  <div className="space-y-3 border-t pt-4">
                    <h4 className="font-semibold">Gift Assignment Roles</h4>
                    <AddEmployeeForm
                      organizationType="Gift"
                      onAssign={handleAssignUser}
                      assigning={assigningUser}
                    />
                  </div>
                )}

                {selectedOrgDetailForEmployee?.organizationType === 'Academic Unit' && (
                  <div className="space-y-3 border-t pt-4">
                    <h4 className="font-semibold">Academic Unit Assignment Roles</h4>
                    <AddEmployeeForm
                      organizationType="AcademicUnit"
                      onAssign={handleAssignUser}
                      assigning={assigningUser}
                    />
                  </div>
                )}

                {selectedOrgDetailForEmployee?.organizationType === 'Company' && (
                  <div className="space-y-3 border-t pt-4">
                    <Button
                      onClick={() => handleAssignUser({})}
                      disabled={assigningUser}
                      className="w-full"
                    >
                      {assigningUser ? 'Assigning...' : 'Assign Employee'}
                    </Button>
                  </div>
                )}
              </div>
            )}
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => {
              setAddEmployeeDialogOpen(false)
              setSearchedUsers([])
              setSelectedUser(null)
              setUserSearchQuery('')
              setSelectedOrgDetailForEmployee(null)
            }}>
              Cancel
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}

// Add Employee Form Component
const AddEmployeeForm = ({ organizationType, onAssign, assigning }) => {
  const [formData, setFormData] = useState({})

  const handleSubmit = () => {
    onAssign(formData)
  }

  if (organizationType === 'Gift') {
    return (
      <>
        <div className="space-y-3">
          <div className="flex items-start space-x-2">
            <Checkbox
              id="finGiftFinancialAnalyst"
              checked={formData.finGiftFinancialAnalyst || false}
              onChange={(e) => setFormData({ ...formData, finGiftFinancialAnalyst: e.target.checked })}
            />
            <div className="space-y-1 leading-none">
              <Label htmlFor="finGiftFinancialAnalyst" className="font-medium cursor-pointer">
                FIN_Gift_Financial_Analyst
              </Label>
              <p className="text-sm text-muted-foreground">
                In Workday I am responsible for preparing or analyzing financial reports for assigned gifts. I support Gift Managers or finance teams with financial data and insights. I maintain and review financial records for gift funds, but do not approve expenses, requisitions, etc.
              </p>
            </div>
          </div>

          <div className="flex items-start space-x-2">
            <Checkbox
              id="finGiftManager"
              checked={formData.finGiftManager || false}
              onChange={(e) => setFormData({ ...formData, finGiftManager: e.target.checked })}
            />
            <div className="space-y-1 leading-none">
              <Label htmlFor="finGiftManager" className="font-medium cursor-pointer">
                FIN_Gift_Manager
              </Label>
              <p className="text-sm text-muted-foreground">
                In Workday I am responsible for approving transactions impacting the financial results of a gift and upholding donor intent. I am the designated primary manager for specific gifts. I have responsibility for approving spend transactions charged to those gifts. I oversee the financial stewardship and compliance of assigned gift funds.
              </p>
            </div>
          </div>

          <div className="flex items-start space-x-2">
            <Checkbox
              id="finProfessorshipPartnerGift"
              checked={formData.finProfessorshipPartnerGift || false}
              onChange={(e) => setFormData({ ...formData, finProfessorshipPartnerGift: e.target.checked })}
            />
            <div className="space-y-1 leading-none">
              <Label htmlFor="finProfessorshipPartnerGift" className="font-medium cursor-pointer">
                FIN_Professorship_Partner_Gift
              </Label>
              <p className="text-sm text-muted-foreground">
                In Workday I am responsible for managing or overseeing named professorships funded by specific gifts. I need to review detailed reports on professorship funds and activities. I have authority to approve business processes related to the establishment, management, or modification of named professorships.
              </p>
            </div>
          </div>
        </div>
        <Button onClick={handleSubmit} disabled={assigning} className="w-full mt-4">
          {assigning ? 'Assigning...' : 'Assign Employee'}
        </Button>
      </>
    )
  }

  if (organizationType === 'AcademicUnit') {
    return (
      <>
        <div className="space-y-3">
          <div className="flex items-start space-x-2">
            <Checkbox
              id="hcmAcademicChairAu"
              checked={formData.hcmAcademicChairAu || false}
              onChange={(e) => setFormData({ ...formData, hcmAcademicChairAu: e.target.checked })}
            />
            <div className="space-y-1 leading-none">
              <Label htmlFor="hcmAcademicChairAu" className="font-medium cursor-pointer">
                HCM_Academic_Chair_AU
              </Label>
              <p className="text-sm text-muted-foreground">
                In support of my academic responsibilities, In Workday my primary responsibility is to do an initial review of new/changes to/ending academic appointments for an Academic Unit.
              </p>
            </div>
          </div>

          <div className="flex items-start space-x-2">
            <Checkbox
              id="hcmAcademicDeanAuh"
              checked={formData.hcmAcademicDeanAuh || false}
              onChange={(e) => setFormData({ ...formData, hcmAcademicDeanAuh: e.target.checked })}
            />
            <div className="space-y-1 leading-none">
              <Label htmlFor="hcmAcademicDeanAuh" className="font-medium cursor-pointer">
                HCM_Academic_Dean_AUH
              </Label>
              <p className="text-sm text-muted-foreground">
                In Workday, my primary responsibility is to provide final college level approval authority on academic appointments for an Academic Unit.
              </p>
            </div>
          </div>

          <div className="flex items-start space-x-2">
            <Checkbox
              id="hcmAcademicFacultyExecutiveAuh"
              checked={formData.hcmAcademicFacultyExecutiveAuh || false}
              onChange={(e) => setFormData({ ...formData, hcmAcademicFacultyExecutiveAuh: e.target.checked })}
            />
            <div className="space-y-1 leading-none">
              <Label htmlFor="hcmAcademicFacultyExecutiveAuh" className="font-medium cursor-pointer">
                HCM_Academic_Faculty_Executive_AUH
              </Label>
              <p className="text-sm text-muted-foreground">
                In Workday, my primary responsibility to view & monitor academic appointments for all Academic Units including Colleges, Libraries, Emeritus, & Honors.
              </p>
            </div>
          </div>

          <div className="flex items-start space-x-2">
            <Checkbox
              id="hcmAcademicFacultyHrAnalystAu"
              checked={formData.hcmAcademicFacultyHrAnalystAu || false}
              onChange={(e) => setFormData({ ...formData, hcmAcademicFacultyHrAnalystAu: e.target.checked })}
            />
            <div className="space-y-1 leading-none">
              <Label htmlFor="hcmAcademicFacultyHrAnalystAu" className="font-medium cursor-pointer">
                HCM_Academic_Faculty_HR_Analyst_AU
              </Label>
              <p className="text-sm text-muted-foreground">
                In Workday my primary responsibility is to support Academic Chairs & Academic Deans with new & changes to academic appointments for an Academic Unit. No Approval Authority
              </p>
            </div>
          </div>

          <div className="flex items-start space-x-2">
            <Checkbox
              id="hcmAcademicProvostPartnerAuh"
              checked={formData.hcmAcademicProvostPartnerAuh || false}
              onChange={(e) => setFormData({ ...formData, hcmAcademicProvostPartnerAuh: e.target.checked })}
            />
            <div className="space-y-1 leading-none">
              <Label htmlFor="hcmAcademicProvostPartnerAuh" className="font-medium cursor-pointer">
                HCM_Academic_Provost_Partner_AUH
              </Label>
              <p className="text-sm text-muted-foreground">
                In Workday my primary responsibility is to support Academic Appointment decisions across the colleges, schools, & departments at the provost level.
              </p>
            </div>
          </div>

          <div className="flex items-start space-x-2">
            <Checkbox
              id="hcmAcademicSchoolDirectorAuh"
              checked={formData.hcmAcademicSchoolDirectorAuh || false}
              onChange={(e) => setFormData({ ...formData, hcmAcademicSchoolDirectorAuh: e.target.checked })}
            />
            <div className="space-y-1 leading-none">
              <Label htmlFor="hcmAcademicSchoolDirectorAuh" className="font-medium cursor-pointer">
                HCM_Academic_School_Director_AUH
              </Label>
              <p className="text-sm text-muted-foreground">
                In Workday my primary responsibility is to support Academic Appointments at the institution at the Academic Unit Hierarchy of school level.
              </p>
            </div>
          </div>
        </div>
        <Button onClick={handleSubmit} disabled={assigning} className="w-full mt-4">
          {assigning ? 'Assigning...' : 'Assign Employee'}
        </Button>
      </>
    )
  }

  return null
}




