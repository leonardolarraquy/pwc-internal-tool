import React, { useState, useEffect, useRef } from 'react'
import { grantAssignmentAPI, organizationDetailAPI, employeeAPI } from '../services/api'
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
import { Trash2, ChevronLeft, ChevronRight, ArrowUpDown, AlertCircle, CheckCircle2, Save, Plus, Users } from 'lucide-react'

export const GrantAssignments = () => {
  const [mode, setMode] = useState('show')
  const [assignments, setAssignments] = useState([])
  const [loading, setLoading] = useState(false)
  const [page, setPage] = useState(0)
  const [size, setSize] = useState(100)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [sortBy, setSortBy] = useState('id')
  const [sortDir, setSortDir] = useState('asc')
  const [search, setSearch] = useState('')
  const [searchInput, setSearchInput] = useState('')
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [selectedAssignment, setSelectedAssignment] = useState(null)
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

  useEffect(() => {
    if (mode === 'show') {
      loadAssignments()
    } else if (mode === 'assign') {
      loadGrantOrganizationDetails()
    }
  }, [mode, page, size, sortBy, sortDir, search])

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
          return legacyName.includes(searchLower) || orgName.includes(searchLower) || refId.includes(searchLower) || id.includes(searchLower)
        })
        setFilteredOrganizationDetails(filtered)
      }
    }
  }, [organizationSearchInput, allOrganizationDetails, mode])

  const loadAssignments = async () => {
    setLoading(true)
    try {
      const response = await grantAssignmentAPI.getAll(page, size, sortBy, sortDir, search)
      const data = response.data
      setAssignments(data.content)
      setTotalPages(data.totalPages)
      setTotalElements(data.totalElements)
    } catch (error) {
      console.error('Error loading grant assignments:', error)
      alert(error.response?.data?.message || 'Error loading grant assignments')
    } finally {
      setLoading(false)
    }
  }

  const loadGrantOrganizationDetails = async () => {
    setLoading(true)
    try {
      const response = await organizationDetailAPI.getAll(0, 10000, 'id', 'asc', '', 'Grant')
      const allOrgs = response.data.content || []
      setAllOrganizationDetails(allOrgs)
      setFilteredOrganizationDetails(allOrgs)
      const initialData = {}
      allOrgs.forEach(org => {
        initialData[org.id] = {
          workerId: '',
          emailId: '',
          positionId: '',
          validationStatus: null,
          validatedUser: null,
          error: null
        }
      })
      setAssignmentData(initialData)
      assignmentDataRef.current = initialData
    } catch (error) {
      console.error('Error loading grant organization details:', error)
      alert('Error loading organization details. Please try again.')
    } finally {
      setLoading(false)
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

  const handleDelete = async () => {
    try {
      await grantAssignmentAPI.delete(selectedAssignment.id)
      setDeleteDialogOpen(false)
      setSelectedAssignment(null)
      loadAssignments()
    } catch (error) {
      alert(error.response?.data?.message || 'Error deleting assignment')
    }
  }

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
      handleAssignmentChange(orgDetailId, 'validatedUser', null)
      handleAssignmentChange(orgDetailId, 'error', null)
      return
    }
    handleAssignmentChange(orgDetailId, 'validationStatus', 'validating')
    try {
      const response = await employeeAPI.findByWorkerId(workerId.trim())
      const employees = response.data
      
      if (employees.length === 0) {
        handleAssignmentChange(orgDetailId, 'validationStatus', 'failed')
        handleAssignmentChange(orgDetailId, 'validatedUser', null)
        handleAssignmentChange(orgDetailId, 'error', 'Employee not found with Worker ID: ' + workerId)
        handleAssignmentChange(orgDetailId, 'foundEmployees', null)
        alert('Employee not found with Worker ID: ' + workerId)
        return
      }
      
      if (employees.length === 1) {
        const employee = employees[0]
        handleAssignmentChange(orgDetailId, 'emailId', employee.email || '', true)
        handleAssignmentChange(orgDetailId, 'positionId', employee.positionId || '', true)
        handleAssignmentChange(orgDetailId, 'validationStatus', 'valid', true)
        handleAssignmentChange(orgDetailId, 'validatedUser', employee, true)
        handleAssignmentChange(orgDetailId, 'error', null, true)
        handleAssignmentChange(orgDetailId, 'foundEmployees', null)
      } else {
        handleAssignmentChange(orgDetailId, 'validationStatus', 'multiple')
        handleAssignmentChange(orgDetailId, 'validatedUser', null)
        handleAssignmentChange(orgDetailId, 'error', null)
        handleAssignmentChange(orgDetailId, 'foundEmployees', employees)
        // Open modal for selection
        setCurrentOrgDetailId(orgDetailId)
        setCurrentFoundEmployees(employees)
        setEmployeeSelectionModalOpen(true)
      }
    } catch (error) {
      handleAssignmentChange(orgDetailId, 'validationStatus', 'failed')
      handleAssignmentChange(orgDetailId, 'validatedUser', null)
      handleAssignmentChange(orgDetailId, 'error', error.response?.data?.message || 'Employee not found with Worker ID: ' + workerId)
      handleAssignmentChange(orgDetailId, 'foundEmployees', null)
      alert(error.response?.data?.message || 'Employee not found with Worker ID: ' + workerId)
    }
  }

  const validateEmailId = async (orgDetailId, emailId) => {
    if (!emailId?.trim()) {
      handleAssignmentChange(orgDetailId, 'validationStatus', 'skip')
      handleAssignmentChange(orgDetailId, 'validatedUser', null)
      handleAssignmentChange(orgDetailId, 'error', null)
      handleAssignmentChange(orgDetailId, 'foundEmployees', null)
      return
    }
    handleAssignmentChange(orgDetailId, 'validationStatus', 'validating')
    try {
      const response = await employeeAPI.findByEmail(emailId.trim())
      const employees = response.data
      
      if (employees.length === 0) {
        handleAssignmentChange(orgDetailId, 'validationStatus', 'failed')
        handleAssignmentChange(orgDetailId, 'validatedUser', null)
        handleAssignmentChange(orgDetailId, 'error', 'Employee not found with Email: ' + emailId)
        handleAssignmentChange(orgDetailId, 'foundEmployees', null)
        alert('Employee not found with Email: ' + emailId)
        return
      }
      
      if (employees.length === 1) {
        const employee = employees[0]
        handleAssignmentChange(orgDetailId, 'workerId', employee.employeeId || '', true)
        handleAssignmentChange(orgDetailId, 'positionId', employee.positionId || '', true)
        handleAssignmentChange(orgDetailId, 'validationStatus', 'valid', true)
        handleAssignmentChange(orgDetailId, 'validatedUser', employee, true)
        handleAssignmentChange(orgDetailId, 'error', null, true)
        handleAssignmentChange(orgDetailId, 'foundEmployees', null)
      } else {
        handleAssignmentChange(orgDetailId, 'validationStatus', 'multiple')
        handleAssignmentChange(orgDetailId, 'validatedUser', null)
        handleAssignmentChange(orgDetailId, 'error', null)
        handleAssignmentChange(orgDetailId, 'foundEmployees', employees)
        // Open modal for selection
        setCurrentOrgDetailId(orgDetailId)
        setCurrentFoundEmployees(employees)
        setEmployeeSelectionModalOpen(true)
      }
    } catch (error) {
      handleAssignmentChange(orgDetailId, 'validationStatus', 'failed')
      handleAssignmentChange(orgDetailId, 'validatedUser', null)
      handleAssignmentChange(orgDetailId, 'error', error.response?.data?.message || 'Employee not found with Email: ' + emailId)
      handleAssignmentChange(orgDetailId, 'foundEmployees', null)
      alert(error.response?.data?.message || 'Employee not found with Email: ' + emailId)
    }
  }

  const validatePositionId = async (orgDetailId, positionId) => {
    if (!positionId?.trim()) {
      handleAssignmentChange(orgDetailId, 'validationStatus', 'skip')
      handleAssignmentChange(orgDetailId, 'validatedUser', null)
      handleAssignmentChange(orgDetailId, 'error', null)
      handleAssignmentChange(orgDetailId, 'foundEmployees', null)
      return
    }
    handleAssignmentChange(orgDetailId, 'validationStatus', 'validating')
    try {
      const response = await employeeAPI.findByPositionId(positionId.trim())
      const employees = response.data
      
      if (employees.length === 0) {
        handleAssignmentChange(orgDetailId, 'validationStatus', 'failed')
        handleAssignmentChange(orgDetailId, 'validatedUser', null)
        handleAssignmentChange(orgDetailId, 'error', 'Employee not found with Position ID: ' + positionId)
        handleAssignmentChange(orgDetailId, 'foundEmployees', null)
        alert('Employee not found with Position ID: ' + positionId)
        return
      }
      
      if (employees.length === 1) {
        const employee = employees[0]
        handleAssignmentChange(orgDetailId, 'workerId', employee.employeeId || '', true)
        handleAssignmentChange(orgDetailId, 'emailId', employee.email || '', true)
        handleAssignmentChange(orgDetailId, 'validationStatus', 'valid', true)
        handleAssignmentChange(orgDetailId, 'validatedUser', employee, true)
        handleAssignmentChange(orgDetailId, 'error', null, true)
        handleAssignmentChange(orgDetailId, 'foundEmployees', null)
      } else {
        handleAssignmentChange(orgDetailId, 'validationStatus', 'multiple')
        handleAssignmentChange(orgDetailId, 'validatedUser', null)
        handleAssignmentChange(orgDetailId, 'error', null)
        handleAssignmentChange(orgDetailId, 'foundEmployees', employees)
        // Open modal for selection
        setCurrentOrgDetailId(orgDetailId)
        setCurrentFoundEmployees(employees)
        setEmployeeSelectionModalOpen(true)
      }
    } catch (error) {
      handleAssignmentChange(orgDetailId, 'validationStatus', 'failed')
      handleAssignmentChange(orgDetailId, 'validatedUser', null)
      handleAssignmentChange(orgDetailId, 'error', error.response?.data?.message || 'Employee not found with Position ID: ' + positionId)
      handleAssignmentChange(orgDetailId, 'foundEmployees', null)
      alert(error.response?.data?.message || 'Employee not found with Position ID: ' + positionId)
    }
  }

  const handleEmployeeSelection = (orgDetailId, selectedEmployee) => {
    handleAssignmentChange(orgDetailId, 'validatedUser', selectedEmployee, true)
    handleAssignmentChange(orgDetailId, 'validationStatus', 'valid', true)
    handleAssignmentChange(orgDetailId, 'error', null, true)
    
    if (selectedEmployee.employeeId) {
      handleAssignmentChange(orgDetailId, 'workerId', selectedEmployee.employeeId, true)
    }
    if (selectedEmployee.email) {
      handleAssignmentChange(orgDetailId, 'emailId', selectedEmployee.email, true)
    }
    if (selectedEmployee.positionId) {
      handleAssignmentChange(orgDetailId, 'positionId', selectedEmployee.positionId, true)
    }
    
    handleAssignmentChange(orgDetailId, 'foundEmployees', null, true)
    
    // Close modal
    setEmployeeSelectionModalOpen(false)
    setCurrentOrgDetailId(null)
    setCurrentFoundEmployees([])
  }

  const getLatestAssignmentData = () => {
    return assignmentDataRef.current
  }

  const validateAssignment = async (orgDetailId, currentData = null) => {
    const orgData = currentData || assignmentData[orgDetailId] || {}
    const { workerId, emailId, positionId } = orgData
    if (workerId?.trim()) {
      await validateWorkerId(orgDetailId, workerId)
    } else if (emailId?.trim()) {
      await validateEmailId(orgDetailId, emailId)
    } else if (positionId?.trim()) {
      await validatePositionId(orgDetailId, positionId)
    } else {
      handleAssignmentChange(orgDetailId, 'validationStatus', 'skip')
      handleAssignmentChange(orgDetailId, 'validatedUser', null)
      handleAssignmentChange(orgDetailId, 'error', null)
    }
  }

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
        await validateAssignment(parseInt(orgDetailId), data)
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
      alert('No valid assignments to save. Please ensure all assignments have valid user data and have been validated (shown as "Valid" in the Status column).')
      return
    }
    setSavingAssignments(true)
    const results = { success: [], errors: [] }
    try {
      for (const [orgDetailId, data] of validAssignments) {
        try {
          if (!data.validatedUser || !data.validatedUser.id) {
            throw new Error('Validated user is missing or has no ID')
          }
          const assignmentPayload = { userId: data.validatedUser.id }
          await organizationDetailAPI.assignEmployee(parseInt(orgDetailId), assignmentPayload)
          const orgDetail = allOrganizationDetails.find(org => org.id.toString() === orgDetailId)
          const orgName = orgDetail ? (orgDetail.organization || orgDetail.legacyOrganizationName || `ID: ${orgDetail.id}`) : `ID: ${orgDetailId}`
          const userName = `${data.validatedUser.firstName} ${data.validatedUser.lastName} (${data.validatedUser.email})`
          results.success.push({ organization: orgName, user: userName })
        } catch (error) {
          const orgDetail = allOrganizationDetails.find(org => org.id.toString() === orgDetailId)
          const orgName = orgDetail ? (orgDetail.organization || orgDetail.legacyOrganizationName || `ID: ${orgDetail.id}`) : `ID: ${orgDetailId}`
          const userName = data.validatedUser ? `${data.validatedUser.firstName} ${data.validatedUser.lastName} (${data.validatedUser.email})` : 'Unknown user'
          results.errors.push({
            organization: orgName,
            user: userName,
            error: error.response?.data?.message || 'Error creating assignment'
          })
        }
      }
      setSaveResults(results)
      const savedOrgDetailIds = results.success.map(s => {
        const entry = validAssignments.find(([id, data]) => {
          const orgDetail = allOrganizationDetails.find(org => org.id.toString() === id)
          const orgName = orgDetail ? (orgDetail.organization || orgDetail.legacyOrganizationName || `ID: ${orgDetail.id}`) : `ID: ${id}`
          const userName = data.validatedUser ? `${data.validatedUser.firstName} ${data.validatedUser.lastName} (${data.validatedUser.email})` : 'Unknown user'
          return s.organization === orgName && s.user === userName
        })
        return entry ? parseInt(entry[0]) : null
      }).filter(id => id !== null)
      setAssignmentData(prev => {
        const updated = { ...prev }
        savedOrgDetailIds.forEach(id => {
          updated[id] = {
            workerId: '',
            emailId: '',
            positionId: '',
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

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Grant Assignments</h1>
          <p className="text-muted-foreground">Manage grant assignments for users</p>
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
        </div>
      </div>

      {mode === 'show' ? (
        <>
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

          <div className="rounded-md border overflow-x-auto">
            <Table>
              <TableHeader>
                <TableRow>
                  <SortableHeader column="id">ID</SortableHeader>
                  <SortableHeader column="employeeFirstName">First Name</SortableHeader>
                  <SortableHeader column="employeeLastName">Last Name</SortableHeader>
                  <SortableHeader column="employeeEmail">Email</SortableHeader>
                  <SortableHeader column="organizationName">Organization</SortableHeader>
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
                ) : assignments.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={6} className="text-center py-8">
                      No grant assignments found
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
                        <Button
                          variant="ghost"
                          size="icon"
                          onClick={() => {
                            setSelectedAssignment(assignment)
                            setDeleteDialogOpen(true)
                          }}
                          title="Delete assignment"
                        >
                          <Trash2 className="h-4 w-4 text-destructive" />
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </div>

          <div className="flex items-center justify-between">
            <div className="text-sm text-muted-foreground">
              Showing {page * size + 1} to {Math.min((page + 1) * size, totalElements)} of {totalElements} assignments
            </div>
            <div className="flex gap-2">
              <Button variant="outline" onClick={() => setPage(page - 1)} disabled={page === 0}>
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
              <Button variant="outline" onClick={() => setPage(page + 1)} disabled={page >= totalPages - 1}>
                Next
                <ChevronRight className="h-4 w-4" />
              </Button>
            </div>
          </div>
        </>
      ) : (
        <>
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
            <div className="flex gap-2 pt-6">
              <Button 
                onClick={handleSaveAll}
                disabled={savingAssignments || filteredOrganizationDetails.length === 0}
              >
                <Save className="mr-2 h-4 w-4" />
                {savingAssignments ? 'Saving...' : 'Save All'}
              </Button>
            </div>
          </div>

          {saveResults && (
            <div className="border rounded-lg p-4 space-y-3">
              <h4 className="font-semibold">Save Results</h4>
              {saveResults.success.length > 0 && (
                <div className="space-y-1">
                  <div className="flex items-center gap-2 text-green-600">
                    <CheckCircle2 className="h-4 w-4" />
                    <span className="font-medium">{saveResults.success.length} assignment(s) created successfully:</span>
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
                    <span className="font-medium">{saveResults.errors.length} error(s):</span>
                  </div>
                  <ul className="list-disc list-inside text-sm text-muted-foreground ml-6">
                    {saveResults.errors.map((error, idx) => (
                      <li key={idx}>{error.organization} → {error.user}: {error.error}</li>
                    ))}
                  </ul>
                </div>
              )}
            </div>
          )}

          {loading ? (
            <div className="border rounded-lg p-8 text-center text-muted-foreground">
              Loading organization details...
            </div>
          ) : filteredOrganizationDetails.length === 0 ? (
            <div className="border rounded-lg p-8 text-center text-muted-foreground">
              No organization details found for Grant type
            </div>
          ) : (
            <div className="rounded-md border overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead className="w-[200px]">Legacy Organization Name</TableHead>
                    <TableHead className="w-[200px]">Organization</TableHead>
                    <TableHead className="w-[150px]">Reference ID</TableHead>
                    <TableHead className="w-[150px]">Worker ID</TableHead>
                    <TableHead className="w-[200px]">Email ID</TableHead>
                    <TableHead className="w-[150px]">Position ID</TableHead>
                    <TableHead className="w-[120px]">Status</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {filteredOrganizationDetails.map((orgDetail) => {
                    const orgData = assignmentData[orgDetail.id] || {}
                    const workerId = (orgData.workerId || '').trim()
                    const emailId = (orgData.emailId || '').trim()
                    const positionId = (orgData.positionId || '').trim()
                    const hasAnyId = workerId || emailId || positionId
                    return (
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
                          <Input
                            value={orgData.workerId || ''}
                            onChange={(e) => handleAssignmentChange(orgDetail.id, 'workerId', e.target.value)}
                            onBlur={async (e) => {
                              const value = e.target.value.trim()
                              if (value) {
                                await validateWorkerId(orgDetail.id, value)
                              } else {
                                handleAssignmentChange(orgDetail.id, 'validationStatus', 'skip')
                                handleAssignmentChange(orgDetail.id, 'validatedUser', null)
                                handleAssignmentChange(orgDetail.id, 'error', null)
                              }
                            }}
                            placeholder="Worker ID"
                            className="w-[150px]"
                          />
                        </TableCell>
                        <TableCell>
                          <Input
                            value={orgData.emailId || ''}
                            onChange={(e) => handleAssignmentChange(orgDetail.id, 'emailId', e.target.value)}
                            onBlur={async (e) => {
                              const value = e.target.value.trim()
                              if (value) {
                                await validateEmailId(orgDetail.id, value)
                              } else {
                                handleAssignmentChange(orgDetail.id, 'validationStatus', 'skip')
                                handleAssignmentChange(orgDetail.id, 'validatedUser', null)
                                handleAssignmentChange(orgDetail.id, 'error', null)
                              }
                            }}
                            placeholder="Email ID"
                            className="w-[200px]"
                          />
                        </TableCell>
                        <TableCell>
                          <Input
                            value={orgData.positionId || ''}
                            onChange={(e) => handleAssignmentChange(orgDetail.id, 'positionId', e.target.value)}
                            onBlur={async (e) => {
                              const value = e.target.value.trim()
                              if (value) {
                                await validatePositionId(orgDetail.id, value)
                              } else {
                                handleAssignmentChange(orgDetail.id, 'validationStatus', 'skip')
                                handleAssignmentChange(orgDetail.id, 'validatedUser', null)
                                handleAssignmentChange(orgDetail.id, 'error', null)
                              }
                            }}
                            placeholder="Position ID"
                            className="w-[150px]"
                          />
                        </TableCell>
                        <TableCell>
                          {!hasAnyId ? (
                            <span className="text-xs text-muted-foreground">Empty</span>
                          ) : orgData.validationStatus === 'validating' ? (
                            <span className="text-xs text-blue-600">Validating...</span>
                          ) : orgData.validationStatus === 'multiple' ? (
                            <div className="flex items-center gap-2">
                              <div className="flex items-center gap-1 text-xs text-orange-600">
                                <AlertCircle className="h-3 w-3" />
                                <span>Multiple found ({orgData.foundEmployees?.length || 0})</span>
                              </div>
                              <Button
                                variant="outline"
                                size="sm"
                                className="h-6 text-xs"
                                onClick={() => {
                                  setCurrentOrgDetailId(orgDetail.id)
                                  setCurrentFoundEmployees(orgData.foundEmployees || [])
                                  setEmployeeSelectionModalOpen(true)
                                }}
                              >
                                Select
                              </Button>
                            </div>
                          ) : orgData.validationStatus === 'valid' ? (
                            <div className="flex items-center gap-1 text-xs text-green-600">
                              <CheckCircle2 className="h-3 w-3" />
                              <span>Valid</span>
                            </div>
                          ) : orgData.validationStatus === 'failed' ? (
                            <div className="flex items-center gap-1 text-xs text-red-600" title={orgData.error}>
                              <AlertCircle className="h-3 w-3" />
                              <span>Failed</span>
                            </div>
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

      <Dialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Delete Grant Assignment</DialogTitle>
            <DialogDescription>
              Are you sure you want to delete this grant assignment? This action cannot be undone.
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

      {/* Employee Selection Modal */}
      <Dialog open={employeeSelectionModalOpen} onOpenChange={setEmployeeSelectionModalOpen}>
        <DialogContent className="max-w-2xl max-h-[80vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>Select Employee</DialogTitle>
            <DialogDescription>
              Multiple employees found. Please select the correct employee to assign.
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-2 py-4">
            {currentFoundEmployees.map((emp) => (
              <div
                key={emp.id}
                className="flex items-center justify-between p-3 border rounded-lg hover:bg-muted cursor-pointer"
                onClick={() => {
                  if (currentOrgDetailId) {
                    handleEmployeeSelection(currentOrgDetailId, emp)
                  }
                }}
              >
                <div className="flex-1">
                  <div className="font-medium">
                    {emp.firstName || ''} {emp.lastName || ''}
                  </div>
                  <div className="text-sm text-muted-foreground space-y-1">
                    <div>Worker ID: {emp.employeeId || 'N/A'}</div>
                    {emp.email && <div>Email: {emp.email}</div>}
                    {emp.positionId && <div>Position ID: {emp.positionId}</div>}
                    <div>Position Title: {emp.positionTitle || 'N/A'}</div>
                  </div>
                </div>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={(e) => {
                    e.stopPropagation()
                    if (currentOrgDetailId) {
                      handleEmployeeSelection(currentOrgDetailId, emp)
                    }
                  }}
                >
                  Select
                </Button>
              </div>
            ))}
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => {
              setEmployeeSelectionModalOpen(false)
              setCurrentOrgDetailId(null)
              setCurrentFoundEmployees([])
            }}>
              Cancel
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}

