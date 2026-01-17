import React, { useState, useEffect, useRef } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import { assignmentAPI, organizationTypeAPI, organizationDetailAPI, employeeAPI, fieldDefinitionAPI } from '../services/api'
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
import { Edit, Trash2, ChevronLeft, ChevronRight, ArrowUpDown, AlertCircle, CheckCircle2, Save, Plus, Users, FileSpreadsheet } from 'lucide-react'
import * as XLSX from 'xlsx'

export const Assignments = () => {
  const { orgTypeSlug } = useParams()
  const navigate = useNavigate()
  const { user, isAdmin } = useAuth()
  
  // Access denied state
  const [accessDenied, setAccessDenied] = useState(false)
  
  // Organization type and field definitions
  const [orgType, setOrgType] = useState(null)
  const [fieldDefinitions, setFieldDefinitions] = useState([])
  
  // Mode: 'assign' or 'show'
  const [mode, setMode] = useState('show')
  
  // For "Show assigned employees" mode
  const [assignments, setAssignments] = useState([])
  const [loading, setLoading] = useState(false)
  const [page, setPage] = useState(0)
  const [size, setSize] = useState(100)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [sortBy, setSortBy] = useState('id')
  const [sortDir, setSortDir] = useState('desc')
  const [search, setSearch] = useState('')
  const [searchInput, setSearchInput] = useState('')
  
  const [editDialogOpen, setEditDialogOpen] = useState(false)
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [selectedAssignment, setSelectedAssignment] = useState(null)
  const [formData, setFormData] = useState({})

  // For "Assign new employees" mode
  const [allOrganizationDetails, setAllOrganizationDetails] = useState([])
  const [filteredOrganizationDetails, setFilteredOrganizationDetails] = useState([])
  const [organizationSearchInput, setOrganizationSearchInput] = useState('')
  const [assignmentData, setAssignmentData] = useState({})
  const assignmentDataRef = useRef({})
  const [savingAssignments, setSavingAssignments] = useState(false)
  const [saveResults, setSaveResults] = useState(null)
  const [employeeSelectionModalOpen, setEmployeeSelectionModalOpen] = useState(false)
  const [currentOrgDetailId, setCurrentOrgDetailId] = useState(null)
  const [currentFoundEmployees, setCurrentFoundEmployees] = useState([])

  // Load organization type and field definitions when slug changes
  useEffect(() => {
    if (orgTypeSlug) {
      // Clear state when switching to a different org type
      setSaveResults(null)
      setAssignmentData({})
      assignmentDataRef.current = {}
      setAllOrganizationDetails([])
      setFilteredOrganizationDetails([])
      setOrganizationSearchInput('')
      loadOrgTypeAndFields()
    }
  }, [orgTypeSlug])

  // Load data when mode or pagination changes
  useEffect(() => {
    if (orgType) {
      // Clear save results when mode changes
      setSaveResults(null)
      
      if (mode === 'show') {
        loadAssignments()
      } else if (mode === 'assign') {
        loadOrganizationDetails()
      }
    }
  }, [mode, page, size, sortBy, sortDir, search, orgType])

  // Filter organization details locally when search input changes
  useEffect(() => {
    if (mode === 'assign') {
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
    }
  }, [organizationSearchInput, allOrganizationDetails, mode])

  const loadOrgTypeAndFields = async () => {
    try {
      const [orgTypeRes, fieldsRes] = await Promise.all([
        organizationTypeAPI.getBySlug(orgTypeSlug),
        fieldDefinitionAPI.getByOrgTypeSlug(orgTypeSlug)
      ])
      
      const loadedOrgType = orgTypeRes.data
      
      // Check access permissions for non-admin users
      if (!isAdmin()) {
        const userOrgAccess = user?.organizationAccess || {}
        if (userOrgAccess[loadedOrgType.id] !== true) {
          setAccessDenied(true)
          return
        }
      }
      
      setAccessDenied(false)
      setOrgType(loadedOrgType)
      setFieldDefinitions(fieldsRes.data)
    } catch (error) {
      console.error('Error loading organization type:', error)
    }
  }

  const loadAssignments = async () => {
    if (!orgType) return
    setLoading(true)
    try {
      const response = await assignmentAPI.getAll(orgTypeSlug, page, size, sortBy, sortDir, search)
      const data = response.data
      setAssignments(data.content)
      setTotalPages(data.totalPages)
      setTotalElements(data.totalElements)
    } catch (error) {
      console.error('Error loading assignments:', error)
      alert(error.response?.data?.message || 'Error loading assignments')
    } finally {
      setLoading(false)
    }
  }

  const loadOrganizationDetails = async () => {
    if (!orgType) return
    setLoading(true)
    try {
      // Load all organization details of the current type
      const response = await organizationDetailAPI.getAll(0, 10000, 'id', 'asc', '', orgType.name)
      const allOrgs = response.data.content || []
      setAllOrganizationDetails(allOrgs)
      setFilteredOrganizationDetails(allOrgs)
      
      // Initialize assignment data for each organization
      const initialData = {}
      allOrgs.forEach(org => {
        const fieldValues = {}
        fieldDefinitions.forEach(fd => {
          fieldValues[fd.fieldKey] = false
        })
        initialData[org.id] = {
          workerId: '',
          emailId: '',
          positionId: '',
          ...fieldValues,
          validationStatus: null,
          validatedUser: null,
          error: null
        }
      })
      setAssignmentData(initialData)
      assignmentDataRef.current = initialData
    } catch (error) {
      console.error('Error loading organization details:', error)
      alert('Error loading organization details. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = () => {
    setSearch(searchInput)
    setPage(0)
  }

  const handleExportExcel = async () => {
    if (!orgType) return
    try {
      setLoading(true)
      const response = await assignmentAPI.getAll(orgTypeSlug, 0, 10000, 'id', 'asc', '')
      const allAssignments = response.data.content
      
      // Build headers dynamically
      const baseHeaders = ['ID', 'First Name', 'Last Name', 'Email', 'Worker ID', 'Organization']
      const fieldHeaders = fieldDefinitions.map(fd => fd.fieldTitle)
      const allHeaders = [...baseHeaders, ...fieldHeaders]
      
      // Build data rows
      const excelData = [
        [orgType.displayName],
        [''],
        allHeaders,
        ...allAssignments.map(assignment => {
          const baseData = [
            assignment.id,
            assignment.employeeFirstName || '',
            assignment.employeeLastName || '',
            assignment.employeeEmail || '',
            assignment.employeeWorkerId || '',
            assignment.organizationName || ''
          ]
          const fieldData = fieldDefinitions.map(fd => 
            assignment.fieldValues?.[fd.fieldKey] ? 'Yes' : 'No'
          )
          return [...baseData, ...fieldData]
        })
      ]
      
      const ws = XLSX.utils.aoa_to_sheet(excelData)
      const wb = XLSX.utils.book_new()
      
      // Set column widths
      ws['!cols'] = allHeaders.map(() => ({ wch: 20 }))
      ws['!merges'] = [{ s: { r: 0, c: 0 }, e: { r: 0, c: allHeaders.length - 1 } }]
      
      XLSX.utils.book_append_sheet(wb, ws, orgType.displayName.substring(0, 31))
      
      const fileName = `${orgTypeSlug}_assignments_${new Date().toISOString().split('T')[0]}.xlsx`
      XLSX.writeFile(wb, fileName)
    } catch (error) {
      console.error('Error exporting to Excel:', error)
      alert('Error exporting to Excel: ' + (error.response?.data?.message || error.message))
    } finally {
      setLoading(false)
    }
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

  const handleEdit = (assignment) => {
    setSelectedAssignment(assignment)
    const fieldData = {}
    fieldDefinitions.forEach(fd => {
      fieldData[fd.fieldKey] = assignment.fieldValues?.[fd.fieldKey] || false
    })
    setFormData(fieldData)
    setEditDialogOpen(true)
  }

  const handleSave = async () => {
    try {
      await assignmentAPI.update(selectedAssignment.id, { fieldValues: formData })
      setEditDialogOpen(false)
      setSelectedAssignment(null)
      loadAssignments()
    } catch (error) {
      alert(error.response?.data?.message || 'Error updating assignment')
    }
  }

  const handleDelete = async () => {
    try {
      await assignmentAPI.delete(selectedAssignment.id)
      setDeleteDialogOpen(false)
      setSelectedAssignment(null)
      loadAssignments()
    } catch (error) {
      alert(error.response?.data?.message || 'Error deleting assignment')
    }
  }

  // Assignment data change handler
  const handleAssignmentChange = (orgDetailId, field, value, skipValidationReset = false) => {
    setAssignmentData(prev => {
      const orgData = prev[orgDetailId] || {}
      const updated = { ...orgData, [field]: value }
      
      if (!skipValidationReset && (field === 'workerId' || field === 'emailId' || field === 'positionId')) {
        updated.validationStatus = null
        updated.validatedUser = null
        updated.error = null
      }
      
      const newState = { ...prev, [orgDetailId]: updated }
      assignmentDataRef.current = newState
      return newState
    })
  }

  const validateWorkerId = async (orgDetailId, workerId) => {
    if (!workerId?.trim()) {
      handleAssignmentChange(orgDetailId, 'validationStatus', 'skip')
      return
    }

    handleAssignmentChange(orgDetailId, 'validationStatus', 'validating')
    
    try {
      const response = await employeeAPI.findByWorkerId(workerId.trim())
      const employees = response.data
      
      if (employees.length === 0) {
        handleAssignmentChange(orgDetailId, 'validationStatus', 'failed')
        handleAssignmentChange(orgDetailId, 'error', 'Employee not found')
        alert('Employee not found with Worker ID: ' + workerId)
        return
      }
      
      if (employees.length === 1) {
        const employee = employees[0]
        handleAssignmentChange(orgDetailId, 'emailId', employee.email || '', true)
        handleAssignmentChange(orgDetailId, 'positionId', employee.positionId || '', true)
        handleAssignmentChange(orgDetailId, 'validationStatus', 'valid', true)
        handleAssignmentChange(orgDetailId, 'validatedUser', employee, true)
      } else {
        handleAssignmentChange(orgDetailId, 'validationStatus', 'multiple')
        handleAssignmentChange(orgDetailId, 'foundEmployees', employees)
        setCurrentOrgDetailId(orgDetailId)
        setCurrentFoundEmployees(employees)
        setEmployeeSelectionModalOpen(true)
      }
    } catch (error) {
      handleAssignmentChange(orgDetailId, 'validationStatus', 'failed')
      handleAssignmentChange(orgDetailId, 'error', error.response?.data?.message || 'Error validating')
      alert(error.response?.data?.message || 'Employee not found')
    }
  }

  const validateEmailId = async (orgDetailId, emailId) => {
    if (!emailId?.trim()) {
      handleAssignmentChange(orgDetailId, 'validationStatus', 'skip')
      return
    }

    handleAssignmentChange(orgDetailId, 'validationStatus', 'validating')
    
    try {
      const response = await employeeAPI.findByEmail(emailId.trim())
      const employees = response.data
      
      if (employees.length === 0) {
        handleAssignmentChange(orgDetailId, 'validationStatus', 'failed')
        handleAssignmentChange(orgDetailId, 'error', 'Employee not found')
        alert('Employee not found with Email: ' + emailId)
        return
      }
      
      if (employees.length === 1) {
        const employee = employees[0]
        handleAssignmentChange(orgDetailId, 'workerId', employee.employeeId || '', true)
        handleAssignmentChange(orgDetailId, 'positionId', employee.positionId || '', true)
        handleAssignmentChange(orgDetailId, 'validationStatus', 'valid', true)
        handleAssignmentChange(orgDetailId, 'validatedUser', employee, true)
      } else {
        handleAssignmentChange(orgDetailId, 'validationStatus', 'multiple')
        handleAssignmentChange(orgDetailId, 'foundEmployees', employees)
        setCurrentOrgDetailId(orgDetailId)
        setCurrentFoundEmployees(employees)
        setEmployeeSelectionModalOpen(true)
      }
    } catch (error) {
      handleAssignmentChange(orgDetailId, 'validationStatus', 'failed')
      alert(error.response?.data?.message || 'Employee not found')
    }
  }

  const validatePositionId = async (orgDetailId, positionId) => {
    if (!positionId?.trim()) {
      handleAssignmentChange(orgDetailId, 'validationStatus', 'skip')
      return
    }

    handleAssignmentChange(orgDetailId, 'validationStatus', 'validating')
    
    try {
      const response = await employeeAPI.findByPositionId(positionId.trim())
      const employees = response.data
      
      if (employees.length === 0) {
        handleAssignmentChange(orgDetailId, 'validationStatus', 'failed')
        handleAssignmentChange(orgDetailId, 'error', 'Employee not found')
        alert('Employee not found with Position ID: ' + positionId)
        return
      }
      
      if (employees.length === 1) {
        const employee = employees[0]
        handleAssignmentChange(orgDetailId, 'workerId', employee.employeeId || '', true)
        handleAssignmentChange(orgDetailId, 'emailId', employee.email || '', true)
        handleAssignmentChange(orgDetailId, 'validationStatus', 'valid', true)
        handleAssignmentChange(orgDetailId, 'validatedUser', employee, true)
      } else {
        handleAssignmentChange(orgDetailId, 'validationStatus', 'multiple')
        handleAssignmentChange(orgDetailId, 'foundEmployees', employees)
        setCurrentOrgDetailId(orgDetailId)
        setCurrentFoundEmployees(employees)
        setEmployeeSelectionModalOpen(true)
      }
    } catch (error) {
      handleAssignmentChange(orgDetailId, 'validationStatus', 'failed')
      alert(error.response?.data?.message || 'Employee not found')
    }
  }

  const handleEmployeeSelection = (orgDetailId, selectedEmployee) => {
    handleAssignmentChange(orgDetailId, 'validatedUser', selectedEmployee, true)
    handleAssignmentChange(orgDetailId, 'validationStatus', 'valid', true)
    handleAssignmentChange(orgDetailId, 'workerId', selectedEmployee.employeeId || '', true)
    handleAssignmentChange(orgDetailId, 'emailId', selectedEmployee.email || '', true)
    handleAssignmentChange(orgDetailId, 'positionId', selectedEmployee.positionId || '', true)
    handleAssignmentChange(orgDetailId, 'foundEmployees', null, true)
    
    setEmployeeSelectionModalOpen(false)
    setCurrentOrgDetailId(null)
    setCurrentFoundEmployees([])
  }

  const getLatestAssignmentData = () => assignmentDataRef.current

  const validateAll = async () => {
    const currentState = getLatestAssignmentData()
    const orgDetailIds = Object.keys(currentState)
    
    for (const orgDetailId of orgDetailIds) {
      const data = currentState[orgDetailId]
      if (!data) continue
      
      const workerId = (data.workerId || '').trim()
      const emailId = (data.emailId || '').trim()
      const positionId = (data.positionId || '').trim()
      
      if ((workerId || emailId || positionId) && data.validationStatus !== 'valid') {
        if (workerId) {
          await validateWorkerId(parseInt(orgDetailId), workerId)
        } else if (emailId) {
          await validateEmailId(parseInt(orgDetailId), emailId)
        } else if (positionId) {
          await validatePositionId(parseInt(orgDetailId), positionId)
        }
        await new Promise(resolve => setTimeout(resolve, 100))
      }
    }
  }

  const handleSaveAll = async () => {
    await validateAll()
    await new Promise(resolve => setTimeout(resolve, 800))
    
    const latestState = getLatestAssignmentData()
    const validAssignments = Object.entries(latestState)
      .filter(([orgDetailId, data]) => {
        if (!data) return false
        const workerId = (data.workerId || '').trim()
        const emailId = (data.emailId || '').trim()
        const positionId = (data.positionId || '').trim()
        
        if (!workerId && !emailId && !positionId) return false
        return data.validationStatus === 'valid' && data.validatedUser && data.validatedUser.id
      })
    
    if (validAssignments.length === 0) {
      alert('No valid assignments to save.')
      return
    }

    setSavingAssignments(true)
    const results = { success: [], errors: [] }

    try {
      for (const [orgDetailId, data] of validAssignments) {
        try {
          // Build field values
          const fieldValues = {}
          fieldDefinitions.forEach(fd => {
            fieldValues[fd.fieldKey] = data[fd.fieldKey] || false
          })
          
          const assignmentPayload = {
            employeeId: data.validatedUser.id,
            organizationDetailId: parseInt(orgDetailId),
            fieldValues
          }

          await assignmentAPI.create(assignmentPayload)
          
          const orgDetail = allOrganizationDetails.find(org => org.id.toString() === orgDetailId)
          const orgName = orgDetail ? (orgDetail.organization || orgDetail.legacyOrganizationName || `ID: ${orgDetail.id}`) : `ID: ${orgDetailId}`
          const userName = `${data.validatedUser.firstName} ${data.validatedUser.lastName} (${data.validatedUser.email})`
          
          results.success.push({ organization: orgName, user: userName })
        } catch (error) {
          const orgDetail = allOrganizationDetails.find(org => org.id.toString() === orgDetailId)
          const orgName = orgDetail ? (orgDetail.organization || orgDetail.legacyOrganizationName) : `ID: ${orgDetailId}`
          const userName = data.validatedUser ? `${data.validatedUser.firstName} ${data.validatedUser.lastName}` : 'Unknown'
          
          results.errors.push({
            organization: orgName,
            user: userName,
            error: error.response?.data?.message || 'Error creating assignment'
          })
        }
      }

      setSaveResults(results)
      
      // Clear saved assignments
      const savedOrgDetailIds = results.success.map(s => {
        const entry = validAssignments.find(([id, data]) => {
          const orgDetail = allOrganizationDetails.find(org => org.id.toString() === id)
          const orgName = orgDetail ? (orgDetail.organization || orgDetail.legacyOrganizationName || `ID: ${orgDetail.id}`) : `ID: ${id}`
          const userName = data.validatedUser ? `${data.validatedUser.firstName} ${data.validatedUser.lastName} (${data.validatedUser.email})` : 'Unknown'
          return s.organization === orgName && s.user === userName
        })
        return entry ? parseInt(entry[0]) : null
      }).filter(id => id !== null)

      setAssignmentData(prev => {
        const updated = { ...prev }
        savedOrgDetailIds.forEach(id => {
          const fieldValues = {}
          fieldDefinitions.forEach(fd => {
            fieldValues[fd.fieldKey] = false
          })
          updated[id] = {
            workerId: '',
            emailId: '',
            positionId: '',
            ...fieldValues,
            validationStatus: null,
            validatedUser: null,
            error: null
          }
        })
        assignmentDataRef.current = updated
        return updated
      })
      
    } catch (error) {
      alert('Error saving assignments: ' + (error.response?.data?.message || error.message))
    } finally {
      setSavingAssignments(false)
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

  if (!orgType) {
    return (
      <div className="flex items-center justify-center h-64">
        <p className="text-muted-foreground">Loading...</p>
      </div>
    )
  }

  // Access denied for non-admin users without permission
  if (accessDenied) {
    return (
      <div className="flex flex-col items-center justify-center h-64 space-y-4">
        <AlertCircle className="h-16 w-16 text-destructive" />
        <h2 className="text-2xl font-bold text-destructive">Access Denied</h2>
        <p className="text-muted-foreground text-center">
          You don't have permission to access this assignment type.<br />
          Please contact your administrator to request access.
        </p>
        <Button onClick={() => navigate('/dashboard')}>
          Return to Dashboard
        </Button>
      </div>
    )
  }

  return (
    <div className="space-y-6 max-w-full">
      <div className="flex items-center justify-between flex-wrap gap-4">
        <div>
          <h1 className="text-3xl font-bold">{orgType.displayName}</h1>
          <p className="text-muted-foreground">Manage {orgType.name.toLowerCase()} assignments</p>
        </div>
        <div className="flex gap-2">
          <Button 
            variant={mode === 'assign' ? 'default' : 'outline'}
            onClick={() => setMode('assign')}
          >
            <Plus className="mr-2 h-4 w-4" />
            Assign New Employees
          </Button>
          <Button 
            variant={mode === 'show' ? 'default' : 'outline'}
            onClick={() => setMode('show')}
          >
            <Users className="mr-2 h-4 w-4" />
            Show Assigned Employees
          </Button>
          {mode === 'show' && (
            <Button onClick={handleExportExcel} variant="outline">
              <FileSpreadsheet className="mr-2 h-4 w-4" />
              Export Excel
            </Button>
          )}
        </div>
      </div>
      
      {mode === 'show' ? (
        <>
          {/* Search and Filters */}
          <div className="flex items-center gap-4">
            <div className="flex-1 flex gap-2">
              <Input
                placeholder="Search by name, email, employee ID, or organization..."
                value={searchInput}
                onChange={(e) => setSearchInput(e.target.value)}
                onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
              />
              <Button onClick={handleSearch}>Search</Button>
            </div>
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

          {/* Assignments Table */}
          <div className="rounded-md border overflow-x-auto">
            <Table>
              <TableHeader>
                <TableRow>
                  <SortableHeader column="id">ID</SortableHeader>
                  <SortableHeader column="employeeFirstName">First Name</SortableHeader>
                  <SortableHeader column="employeeLastName">Last Name</SortableHeader>
                  <SortableHeader column="employeeEmail">Email</SortableHeader>
                  <SortableHeader column="organizationName">Organization</SortableHeader>
                  <TableHead>Roles</TableHead>
                  <TableHead>Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {loading ? (
                  <TableRow>
                    <TableCell colSpan={7} className="text-center py-8">
                      Loading...
                    </TableCell>
                  </TableRow>
                ) : assignments.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={7} className="text-center py-8">
                      No assignments found
                    </TableCell>
                  </TableRow>
                ) : (
                  assignments.map((assignment) => (
                    <TableRow key={assignment.id}>
                      <TableCell>{assignment.id}</TableCell>
                      <TableCell>{assignment.employeeFirstName || '-'}</TableCell>
                      <TableCell>{assignment.employeeLastName || '-'}</TableCell>
                      <TableCell>{assignment.employeeEmail || '-'}</TableCell>
                      <TableCell>{assignment.organizationName || '-'}</TableCell>
                      <TableCell>
                        <div className="flex flex-wrap gap-1">
                          {fieldDefinitions.filter(fd => assignment.fieldValues?.[fd.fieldKey]).map(fd => (
                            <span key={fd.fieldKey} className="text-xs bg-blue-100 text-blue-800 px-2 py-1 rounded">
                              {fd.fieldTitle}
                            </span>
                          ))}
                        </div>
                      </TableCell>
                      <TableCell>
                        <div className="flex gap-2">
                          <Button variant="ghost" size="icon" onClick={() => handleEdit(assignment)} title="Edit">
                            <Edit className="h-4 w-4" />
                          </Button>
                          <Button variant="ghost" size="icon" onClick={() => { setSelectedAssignment(assignment); setDeleteDialogOpen(true) }} title="Delete">
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
              Showing {page * size + 1} to {Math.min((page + 1) * size, totalElements)} of {totalElements} assignments
            </div>
            <div className="flex gap-2">
              <Button variant="outline" onClick={() => setPage(page - 1)} disabled={page === 0}>
                <ChevronLeft className="h-4 w-4" /> Previous
              </Button>
              <Button variant="outline" onClick={() => setPage(page + 1)} disabled={page >= totalPages - 1}>
                Next <ChevronRight className="h-4 w-4" />
              </Button>
            </div>
          </div>
        </>
      ) : (
        <>
          {/* Organization Search */}
          <div className="flex items-center gap-4">
            <div className="flex-1">
              <Label htmlFor="orgSearch">Search Organization Details</Label>
              <Input
                id="orgSearch"
                placeholder="Search by name, reference ID, or ID..."
                value={organizationSearchInput}
                onChange={(e) => setOrganizationSearchInput(e.target.value)}
                className="mt-1"
              />
              <p className="text-xs text-muted-foreground mt-1">
                Showing {filteredOrganizationDetails.length} of {allOrganizationDetails.length} organization(s)
              </p>
            </div>
            <div className="flex gap-2 pt-6">
              <Button onClick={handleSaveAll} disabled={savingAssignments || filteredOrganizationDetails.length === 0}>
                <Save className="mr-2 h-4 w-4" />
                {savingAssignments ? 'Saving...' : 'Save All'}
              </Button>
            </div>
          </div>

          {/* Save Results */}
          {saveResults && (
            <div className="border rounded-lg p-4 space-y-3">
              <h4 className="font-semibold">Save Results</h4>
              {saveResults.success.length > 0 && (
                <div className="space-y-1">
                  <div className="flex items-center gap-2 text-green-600">
                    <CheckCircle2 className="h-4 w-4" />
                    <span className="font-medium">{saveResults.success.length} assignment(s) created</span>
                  </div>
                  <ul className="list-disc list-inside text-sm text-muted-foreground ml-6">
                    {saveResults.success.map((result, idx) => (
                      <li key={idx}>{result.organization} → {result.user}</li>
                    ))}
                  </ul>
                </div>
              )}
              {saveResults.errors.length > 0 && (
                <div className="space-y-1">
                  <div className="flex items-center gap-2 text-red-600">
                    <AlertCircle className="h-4 w-4" />
                    <span className="font-medium">{saveResults.errors.length} error(s)</span>
                  </div>
                  <ul className="list-disc list-inside text-sm text-muted-foreground ml-6">
                    {saveResults.errors.map((error, idx) => (
                      <li key={idx}>{error.organization}: {error.error}</li>
                    ))}
                  </ul>
                </div>
              )}
            </div>
          )}

          {/* Assignment Table */}
          {loading ? (
            <div className="border rounded-lg p-8 text-center text-muted-foreground">Loading...</div>
          ) : filteredOrganizationDetails.length === 0 ? (
            <div className="border rounded-lg p-8 text-center text-muted-foreground">
              No organization details found for {orgType.name}
            </div>
          ) : (
            <div className="rounded-md border overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead className="w-[200px]">Organization</TableHead>
                    <TableHead className="w-[150px]">Reference ID</TableHead>
                    <TableHead className="w-[150px]">Worker ID</TableHead>
                    <TableHead className="w-[200px]">Email</TableHead>
                    <TableHead className="w-[150px]">Position ID</TableHead>
                    {fieldDefinitions.map(fd => (
                      <TableHead key={fd.fieldKey} className="w-[120px]" title={fd.fieldDescription}>
                        {fd.fieldTitle}
                      </TableHead>
                    ))}
                    <TableHead className="w-[100px]">Status</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {filteredOrganizationDetails.map((orgDetail) => {
                    const orgData = assignmentData[orgDetail.id] || {}
                    const hasAnyId = (orgData.workerId || '').trim() || (orgData.emailId || '').trim() || (orgData.positionId || '').trim()
                    
                    return (
                      <TableRow key={orgDetail.id}>
                        <TableCell>{orgDetail.organization || orgDetail.legacyOrganizationName || '-'}</TableCell>
                        <TableCell>{orgDetail.referenceId && !orgDetail.referenceId.startsWith('#') ? orgDetail.referenceId : '-'}</TableCell>
                        <TableCell>
                          <Input
                            value={orgData.workerId || ''}
                            onChange={(e) => handleAssignmentChange(orgDetail.id, 'workerId', e.target.value)}
                            onBlur={(e) => e.target.value.trim() && validateWorkerId(orgDetail.id, e.target.value)}
                            placeholder="Worker ID"
                            className="w-[140px]"
                          />
                        </TableCell>
                        <TableCell>
                          <Input
                            value={orgData.emailId || ''}
                            onChange={(e) => handleAssignmentChange(orgDetail.id, 'emailId', e.target.value)}
                            onBlur={(e) => e.target.value.trim() && validateEmailId(orgDetail.id, e.target.value)}
                            placeholder="Email"
                            className="w-[180px]"
                          />
                        </TableCell>
                        <TableCell>
                          <Input
                            value={orgData.positionId || ''}
                            onChange={(e) => handleAssignmentChange(orgDetail.id, 'positionId', e.target.value)}
                            onBlur={(e) => e.target.value.trim() && validatePositionId(orgDetail.id, e.target.value)}
                            placeholder="Position ID"
                            className="w-[140px]"
                          />
                        </TableCell>
                        {fieldDefinitions.map(fd => (
                          <TableCell key={fd.fieldKey}>
                            <Checkbox
                              checked={orgData[fd.fieldKey] || false}
                              onChange={(e) => handleAssignmentChange(orgDetail.id, fd.fieldKey, e.target.checked)}
                            />
                          </TableCell>
                        ))}
                        <TableCell>
                          {!hasAnyId ? (
                            <span className="text-xs text-muted-foreground">-</span>
                          ) : orgData.validationStatus === 'validating' ? (
                            <span className="text-xs text-blue-600">Validating...</span>
                          ) : orgData.validationStatus === 'multiple' ? (
                            <Button variant="outline" size="sm" className="h-6 text-xs" onClick={() => {
                              setCurrentOrgDetailId(orgDetail.id)
                              setCurrentFoundEmployees(orgData.foundEmployees || [])
                              setEmployeeSelectionModalOpen(true)
                            }}>
                              Select
                            </Button>
                          ) : orgData.validationStatus === 'valid' ? (
                            <span className="text-xs text-green-600 flex items-center gap-1">
                              <CheckCircle2 className="h-3 w-3" /> Valid
                            </span>
                          ) : orgData.validationStatus === 'failed' ? (
                            <span className="text-xs text-red-600 flex items-center gap-1">
                              <AlertCircle className="h-3 w-3" /> Failed
                            </span>
                          ) : (
                            <span className="text-xs text-muted-foreground">-</span>
                          )}
                        </TableCell>
                      </TableRow>
                    )
                  })}
                </TableBody>
              </Table>
            </div>
          )}
        </>
      )}

      {/* Edit Dialog */}
      <Dialog open={editDialogOpen} onOpenChange={setEditDialogOpen}>
        <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>Edit Assignment</DialogTitle>
            <DialogDescription>
              Update roles for {selectedAssignment?.employeeFirstName} {selectedAssignment?.employeeLastName}
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="space-y-3">
              {fieldDefinitions.map(fd => (
                <div key={fd.fieldKey} className="flex items-start space-x-2">
                  <Checkbox
                    id={fd.fieldKey}
                    checked={formData[fd.fieldKey] || false}
                    onChange={(e) => setFormData({ ...formData, [fd.fieldKey]: e.target.checked })}
                  />
                  <div className="space-y-1 leading-none">
                    <Label htmlFor={fd.fieldKey} className="font-medium cursor-pointer">
                      {fd.fieldTitle}
                    </Label>
                    {fd.fieldDescription && (
                      <p className="text-sm text-muted-foreground">{fd.fieldDescription}</p>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setEditDialogOpen(false)}>Cancel</Button>
            <Button onClick={handleSave}>Save</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Delete Dialog */}
      <Dialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Delete Assignment</DialogTitle>
            <DialogDescription>
              Are you sure you want to delete this assignment? This action cannot be undone.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDeleteDialogOpen(false)}>Cancel</Button>
            <Button variant="destructive" onClick={handleDelete}>Delete</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Employee Selection Modal */}
      <Dialog open={employeeSelectionModalOpen} onOpenChange={setEmployeeSelectionModalOpen}>
        <DialogContent className="max-w-2xl max-h-[80vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>Select Employee</DialogTitle>
            <DialogDescription>
              Multiple employees found. Please select the correct employee.
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-2 py-4">
            {currentFoundEmployees.map((emp) => (
              <div
                key={emp.id}
                className="flex items-center justify-between p-3 border rounded-lg hover:bg-muted cursor-pointer"
                onClick={() => currentOrgDetailId && handleEmployeeSelection(currentOrgDetailId, emp)}
              >
                <div className="flex-1">
                  <div className="font-medium">{emp.firstName} {emp.lastName}</div>
                  <div className="text-sm text-muted-foreground">
                    <div>Worker ID: {emp.employeeId || 'N/A'}</div>
                    {emp.email && <div>Email: {emp.email}</div>}
                    {emp.positionId && <div>Position ID: {emp.positionId}</div>}
                  </div>
                </div>
                <Button variant="outline" size="sm">Select</Button>
              </div>
            ))}
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => { setEmployeeSelectionModalOpen(false); setCurrentOrgDetailId(null); setCurrentFoundEmployees([]) }}>
              Cancel
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}
