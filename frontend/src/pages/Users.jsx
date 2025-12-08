import React, { useState, useEffect } from 'react'
import { userAPI } from '../services/api'
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
import { Plus, Edit, Trash2, Upload, ChevronLeft, ChevronRight, ArrowUpDown, KeyRound } from 'lucide-react'
import { Checkbox } from '../components/ui/checkbox'

export const Users = () => {
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(false)
  const [page, setPage] = useState(0)
  const [size, setSize] = useState(100)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [sortBy, setSortBy] = useState('id')
  const [sortDir, setSortDir] = useState('asc')
  const [search, setSearch] = useState('')
  const [searchInput, setSearchInput] = useState('')
  const [accessFilter, setAccessFilter] = useState('all') // 'all', 'company', 'academic', 'gift', 'none'
  const [filterCompany, setFilterCompany] = useState(null) // null = all, true = with access, false = without
  const [filterAcademicUnit, setFilterAcademicUnit] = useState(null)
  const [filterGift, setFilterGift] = useState(null)
  
  const [dialogOpen, setDialogOpen] = useState(false)
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [resetPasswordDialogOpen, setResetPasswordDialogOpen] = useState(false)
  const [importDialogOpen, setImportDialogOpen] = useState(false)
  const [editingUser, setEditingUser] = useState(null)
  const [selectedFile, setSelectedFile] = useState(null)
  const [importing, setImporting] = useState(false)
  
  const [formData, setFormData] = useState({
    employeeId: '',
    firstName: '',
    lastName: '',
    positionId: '',
    positionTitle: '',
    email: '',
    password: '',
    role: 'USER',
    companyAssignmentsAccess: false,
    academicUnitAssignmentsAccess: false,
    giftAssignmentsAccess: false,
  })

  useEffect(() => {
    loadUsers()
  }, [page, size, sortBy, sortDir, search, accessFilter])

  const loadUsers = async () => {
    setLoading(true)
    try {
      const response = await userAPI.getAll(page, size, sortBy, sortDir, search, accessFilter)
      const data = response.data
      setUsers(data.content)
      setTotalPages(data.totalPages)
      setTotalElements(data.totalElements)
    } catch (error) {
      console.error('Error loading users:', error)
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

  const handleCreate = () => {
    setEditingUser(null)
    setFormData({
      employeeId: '',
      firstName: '',
      lastName: '',
      positionId: '',
      positionTitle: '',
      email: '',
      password: '',
      role: 'USER',
      companyAssignmentsAccess: false,
      academicUnitAssignmentsAccess: false,
      giftAssignmentsAccess: false,
    })
    setDialogOpen(true)
  }

  const handleEdit = (user) => {
    setEditingUser(user)
    setFormData({
      employeeId: user.employeeId,
      firstName: user.firstName,
      lastName: user.lastName,
      positionId: user.positionId || '',
      positionTitle: user.positionTitle || '',
      email: user.email,
      password: '',
      role: user.role,
      companyAssignmentsAccess: user.companyAssignmentsAccess || false,
      academicUnitAssignmentsAccess: user.academicUnitAssignmentsAccess || false,
      giftAssignmentsAccess: user.giftAssignmentsAccess || false,
    })
    setDialogOpen(true)
  }

  const handleSave = async () => {
    try {
      if (editingUser) {
        await userAPI.update(editingUser.id, formData)
      } else {
        await userAPI.create(formData)
      }
      setDialogOpen(false)
      loadUsers()
    } catch (error) {
      alert(error.response?.data?.message || 'Error saving user')
    }
  }

  const handleDelete = async () => {
    try {
      await userAPI.delete(editingUser.id)
      setDeleteDialogOpen(false)
      setEditingUser(null)
      loadUsers()
    } catch (error) {
      alert(error.response?.data?.message || 'Error deleting user')
    }
  }

  const handleResetPassword = async () => {
    try {
      await userAPI.resetPassword(editingUser.id)
      alert('Password reset successfully. User must set a new password on next login.')
      setResetPasswordDialogOpen(false)
      setEditingUser(null)
      loadUsers()
    } catch (error) {
      alert(error.response?.data?.message || 'Error resetting password')
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
      const response = await userAPI.importCSV(selectedFile)
      alert(`Successfully imported ${response.data.imported} users`)
      setImportDialogOpen(false)
      setSelectedFile(null)
      loadUsers()
    } catch (error) {
      alert(error.response?.data?.message || 'Error importing CSV')
    } finally {
      setImporting(false)
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
          <h1 className="text-3xl font-bold">User Configuration</h1>
          <p className="text-muted-foreground">Manage employees and their information</p>
        </div>
        <div className="flex gap-2">
          <Button onClick={() => setImportDialogOpen(true)} variant="outline">
            <Upload className="mr-2 h-4 w-4" />
            Import CSV
          </Button>
          <Button onClick={handleCreate}>
            <Plus className="mr-2 h-4 w-4" />
            Add User
          </Button>
        </div>
      </div>

      {/* Search and Filters */}
      <div className="flex items-center gap-4">
        <div className="flex-1 flex gap-2">
          <Input
            placeholder="Search by name, email, or employee ID..."
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
          />
          <Button onClick={handleSearch}>Search</Button>
        </div>
        <Select value={accessFilter} onValueChange={(val) => { setAccessFilter(val); setPage(0) }}>
          <SelectTrigger className="w-48">
            <SelectValue placeholder="Filter by access" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">All Access Types</SelectItem>
            <SelectItem value="company">Company Access</SelectItem>
            <SelectItem value="academic">Academic Unit Access</SelectItem>
            <SelectItem value="gift">Gift Access</SelectItem>
            <SelectItem value="none">No Access</SelectItem>
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

      {/* Users Table */}
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <SortableHeader column="id">ID</SortableHeader>
              <SortableHeader column="employeeId">Employee ID</SortableHeader>
              <SortableHeader column="firstName">First Name</SortableHeader>
              <SortableHeader column="lastName">Last Name</SortableHeader>
              <SortableHeader column="email">Email</SortableHeader>
              <SortableHeader column="positionTitle">Position</SortableHeader>
              <SortableHeader column="role">Role</SortableHeader>
              <SortableHeader column="companyAssignmentsAccess">Access Permissions</SortableHeader>
              <TableHead>Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {loading ? (
              <TableRow>
                <TableCell colSpan={9} className="text-center py-8">
                  Loading...
                </TableCell>
              </TableRow>
            ) : users.length === 0 ? (
              <TableRow>
                <TableCell colSpan={9} className="text-center py-8">
                  No users found
                </TableCell>
              </TableRow>
            ) : (
              users.map((user) => (
                <TableRow key={user.id}>
                  <TableCell>{user.id}</TableCell>
                  <TableCell>{user.employeeId}</TableCell>
                  <TableCell>{user.firstName}</TableCell>
                  <TableCell>{user.lastName}</TableCell>
                  <TableCell>{user.email}</TableCell>
                  <TableCell>{user.positionTitle || '-'}</TableCell>
                  <TableCell>
                    <span className={`px-2 py-1 rounded text-xs ${
                      user.role === 'ADMIN' 
                        ? 'bg-blue-100 text-blue-800' 
                        : 'bg-gray-100 text-gray-800'
                    }`}>
                      {user.role}
                    </span>
                  </TableCell>
                  <TableCell>
                    <div className="flex flex-wrap gap-1">
                      {user.companyAssignmentsAccess && (
                        <span className="text-xs bg-blue-100 text-blue-800 px-2 py-1 rounded">Company</span>
                      )}
                      {user.academicUnitAssignmentsAccess && (
                        <span className="text-xs bg-green-100 text-green-800 px-2 py-1 rounded">Academic Unit</span>
                      )}
                      {user.giftAssignmentsAccess && (
                        <span className="text-xs bg-purple-100 text-purple-800 px-2 py-1 rounded">Gift</span>
                      )}
                      {!user.companyAssignmentsAccess && !user.academicUnitAssignmentsAccess && !user.giftAssignmentsAccess && (
                        <span className="text-xs text-muted-foreground">None</span>
                      )}
                    </div>
                  </TableCell>
                  <TableCell>
                    <div className="flex gap-2">
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => handleEdit(user)}
                        title="Edit user"
                      >
                        <Edit className="h-4 w-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => {
                          setEditingUser(user)
                          setResetPasswordDialogOpen(true)
                        }}
                        title="Reset password"
                      >
                        <KeyRound className="h-4 w-4 text-orange-600" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => {
                          setEditingUser(user)
                          setDeleteDialogOpen(true)
                        }}
                        title="Delete user"
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
          Showing {page * size + 1} to {Math.min((page + 1) * size, totalElements)} of {totalElements} users
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
        <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>{editingUser ? 'Edit User' : 'Create User'}</DialogTitle>
            <DialogDescription>
              {editingUser ? 'Update user information' : 'Add a new user to the system'}
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="employeeId">Employee ID *</Label>
                <Input
                  id="employeeId"
                  value={formData.employeeId}
                  onChange={(e) => setFormData({ ...formData, employeeId: e.target.value })}
                  required
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="email">Email *</Label>
                <Input
                  id="email"
                  type="email"
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  required
                />
              </div>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="firstName">First Name *</Label>
                <Input
                  id="firstName"
                  value={formData.firstName}
                  onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                  required
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="lastName">Last Name *</Label>
                <Input
                  id="lastName"
                  value={formData.lastName}
                  onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                  required
                />
              </div>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="positionId">Position ID</Label>
                <Input
                  id="positionId"
                  value={formData.positionId}
                  onChange={(e) => setFormData({ ...formData, positionId: e.target.value })}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="positionTitle">Position Title</Label>
                <Input
                  id="positionTitle"
                  value={formData.positionTitle}
                  onChange={(e) => setFormData({ ...formData, positionTitle: e.target.value })}
                />
              </div>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="password">
                  Password {editingUser ? '(leave blank to keep current)' : '*'}
                </Label>
                <Input
                  id="password"
                  type="password"
                  value={formData.password}
                  onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                  required={!editingUser}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="role">Role</Label>
                <Select value={formData.role} onValueChange={(val) => setFormData({ ...formData, role: val })}>
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="USER">USER</SelectItem>
                    <SelectItem value="ADMIN">ADMIN</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>
            <div className="space-y-4 border-t pt-4">
              <h4 className="font-semibold">Access Permissions</h4>
              <div className="space-y-3">
                <div className="flex items-start space-x-2">
                  <Checkbox
                    id="companyAssignmentsAccess"
                    checked={formData.companyAssignmentsAccess || false}
                    onChange={(e) => setFormData({ ...formData, companyAssignmentsAccess: e.target.checked })}
                  />
                  <div className="space-y-1 leading-none">
                    <Label htmlFor="companyAssignmentsAccess" className="font-medium cursor-pointer">
                      Company Assignments Access
                    </Label>
                    <p className="text-sm text-muted-foreground">
                      Grant access to view and manage company assignments.
                    </p>
                  </div>
                </div>

                <div className="flex items-start space-x-2">
                  <Checkbox
                    id="academicUnitAssignmentsAccess"
                    checked={formData.academicUnitAssignmentsAccess || false}
                    onChange={(e) => setFormData({ ...formData, academicUnitAssignmentsAccess: e.target.checked })}
                  />
                  <div className="space-y-1 leading-none">
                    <Label htmlFor="academicUnitAssignmentsAccess" className="font-medium cursor-pointer">
                      Academic Unit Assignments Access
                    </Label>
                    <p className="text-sm text-muted-foreground">
                      Grant access to view and manage academic unit assignments.
                    </p>
                  </div>
                </div>

                <div className="flex items-start space-x-2">
                  <Checkbox
                    id="giftAssignmentsAccess"
                    checked={formData.giftAssignmentsAccess || false}
                    onChange={(e) => setFormData({ ...formData, giftAssignmentsAccess: e.target.checked })}
                  />
                  <div className="space-y-1 leading-none">
                    <Label htmlFor="giftAssignmentsAccess" className="font-medium cursor-pointer">
                      Gift Assignments Access
                    </Label>
                    <p className="text-sm text-muted-foreground">
                      Grant access to view and manage gift assignments.
                    </p>
                  </div>
                </div>
              </div>
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

      {/* Reset Password Confirmation Dialog */}
      <Dialog open={resetPasswordDialogOpen} onOpenChange={setResetPasswordDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Reset Password</DialogTitle>
            <DialogDescription>
              Are you sure you want to reset the password for {editingUser?.firstName} {editingUser?.lastName}? 
              The user will be required to set a new password on their next login.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setResetPasswordDialogOpen(false)}>
              Cancel
            </Button>
            <Button variant="default" onClick={handleResetPassword}>
              Reset Password
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Delete User</DialogTitle>
            <DialogDescription>
              Are you sure you want to delete {editingUser?.firstName} {editingUser?.lastName}? This action cannot be undone.
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
            <DialogTitle>Import Users from CSV</DialogTitle>
            <DialogDescription>
              Upload a CSV file with user data. Required columns: email, employeeId, firstName, lastName.
              Optional columns: positionId, positionTitle, password, role.
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

