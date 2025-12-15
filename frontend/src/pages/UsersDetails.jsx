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
import { Plus, Edit, Trash2, ChevronLeft, ChevronRight, ArrowUpDown, KeyRound, FileSpreadsheet } from 'lucide-react'
import * as XLSX from 'xlsx'
import { Checkbox } from '../components/ui/checkbox'

export const UsersDetails = () => {
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
  const [accessFilter, setAccessFilter] = useState('all')
  
  const [dialogOpen, setDialogOpen] = useState(false)
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [resetPasswordDialogOpen, setResetPasswordDialogOpen] = useState(false)
  const [editingUser, setEditingUser] = useState(null)
  
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    company: '',
    email: '',
    password: '',
    role: 'USER',
    companyAssignmentsAccess: false,
    academicUnitAssignmentsAccess: false,
    giftAssignmentsAccess: false,
    locationAssignmentsAccess: false,
    projectAssignmentsAccess: false,
    grantAssignmentsAccess: false,
    paygroupAssignmentsAccess: false,
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
      firstName: '',
      lastName: '',
      company: '',
      email: '',
      password: '',
      role: 'USER',
      companyAssignmentsAccess: false,
      academicUnitAssignmentsAccess: false,
      giftAssignmentsAccess: false,
      locationAssignmentsAccess: false,
      projectAssignmentsAccess: false,
      grantAssignmentsAccess: false,
      paygroupAssignmentsAccess: false,
    })
    setDialogOpen(true)
  }

  const handleEdit = (user) => {
    setEditingUser(user)
    setFormData({
      firstName: user.firstName,
      lastName: user.lastName,
      company: user.company || '',
      email: user.email,
      password: '',
      role: user.role,
      companyAssignmentsAccess: user.companyAssignmentsAccess || false,
      academicUnitAssignmentsAccess: user.academicUnitAssignmentsAccess || false,
      giftAssignmentsAccess: user.giftAssignmentsAccess || false,
      locationAssignmentsAccess: user.locationAssignmentsAccess || false,
      projectAssignmentsAccess: user.projectAssignmentsAccess || false,
      grantAssignmentsAccess: user.grantAssignmentsAccess || false,
      paygroupAssignmentsAccess: user.paygroupAssignmentsAccess || false,
    })
    setDialogOpen(true)
  }

  const handleSave = async () => {
    try {
      const dataToSave = { ...formData }
      if (!dataToSave.password || dataToSave.password.trim() === '') {
        delete dataToSave.password
      }
      if (editingUser) {
        await userAPI.update(editingUser.id, dataToSave)
      } else {
        await userAPI.create(dataToSave)
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

  const handleExportExcel = async () => {
    try {
      setLoading(true)
      // Get all users without pagination
      const response = await userAPI.getAll(0, 10000, 'id', 'asc', '', 'all')
      const allUsers = response.data.content
      
      // Prepare data for Excel
      const excelData = allUsers.map(user => ({
        'ID': user.id,
        'First Name': user.firstName || '',
        'Last Name': user.lastName || '',
        'Company': user.company || '',
        'Email': user.email || '',
        'Role': user.role || '',
        'Company Assignments Access': user.companyAssignmentsAccess ? 'Yes' : 'No',
        'Academic Unit Assignments Access': user.academicUnitAssignmentsAccess ? 'Yes' : 'No',
        'Gift Assignments Access': user.giftAssignmentsAccess ? 'Yes' : 'No',
        'Location Assignments Access': user.locationAssignmentsAccess ? 'Yes' : 'No',
        'Project Assignments Access': user.projectAssignmentsAccess ? 'Yes' : 'No',
        'Grant Assignments Access': user.grantAssignmentsAccess ? 'Yes' : 'No',
        'Paygroup Assignments Access': user.paygroupAssignmentsAccess ? 'Yes' : 'No'
      }))
      
      // Create workbook and worksheet
      const ws = XLSX.utils.json_to_sheet(excelData)
      const wb = XLSX.utils.book_new()
      XLSX.utils.book_append_sheet(wb, ws, 'Users')
      
      // Generate file and download
      const fileName = `users_${new Date().toISOString().split('T')[0]}.xlsx`
      XLSX.writeFile(wb, fileName)
    } catch (error) {
      console.error('Error exporting to Excel:', error)
      alert('Error exporting to Excel: ' + (error.response?.data?.message || error.message))
    } finally {
      setLoading(false)
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
          <h1 className="text-3xl font-bold">Users Details</h1>
          <p className="text-muted-foreground">Manage application users and their permissions</p>
        </div>
        <div className="flex gap-2">
          <Button onClick={handleCreate}>
            <Plus className="mr-2 h-4 w-4" />
            Add User
          </Button>
          <Button onClick={handleExportExcel} variant="outline">
            <FileSpreadsheet className="mr-2 h-4 w-4" />
            Export Excel
          </Button>
        </div>
      </div>

      {/* Search and Filters */}
      <div className="flex items-center gap-4">
        <div className="flex-1 flex gap-2">
          <Input
            placeholder="Search by name, email, or company..."
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
            <SelectItem value="location">Location Access</SelectItem>
            <SelectItem value="project">Project Access</SelectItem>
            <SelectItem value="grant">Grant Access</SelectItem>
            <SelectItem value="paygroup">Paygroup Access</SelectItem>
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
              <SortableHeader column="firstName">First Name</SortableHeader>
              <SortableHeader column="lastName">Last Name</SortableHeader>
              <SortableHeader column="company">Company</SortableHeader>
              <SortableHeader column="email">Email</SortableHeader>
              <SortableHeader column="role">User Role</SortableHeader>
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
            ) : users.length === 0 ? (
              <TableRow>
                <TableCell colSpan={7} className="text-center py-8">
                  No users found
                </TableCell>
              </TableRow>
            ) : (
              users.map((user) => (
                <TableRow key={user.id}>
                  <TableCell>{user.id}</TableCell>
                  <TableCell>{user.firstName}</TableCell>
                  <TableCell>{user.lastName}</TableCell>
                  <TableCell>{user.company || '-'}</TableCell>
                  <TableCell>{user.email}</TableCell>
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
              {editingUser ? 'Update user information and permissions' : 'Add a new application user'}
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
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
                <Label htmlFor="company">Company</Label>
                <Input
                  id="company"
                  value={formData.company}
                  onChange={(e) => setFormData({ ...formData, company: e.target.value })}
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
                <Label htmlFor="password">Password {editingUser ? '(leave empty to keep current)' : '(optional)'}</Label>
                <Input
                  id="password"
                  type="password"
                  value={formData.password}
                  onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="role">User Role *</Label>
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
            
            <div className="border-t pt-4 mt-4">
              <Label className="text-base font-semibold">Access Permissions</Label>
              <div className="grid grid-cols-2 gap-4 mt-4">
                <div className="flex items-center space-x-2">
                  <Checkbox
                    id="companyAssignmentsAccess"
                    checked={formData.companyAssignmentsAccess || false}
                    onChange={(e) => setFormData({ ...formData, companyAssignmentsAccess: e.target.checked })}
                  />
                  <Label htmlFor="companyAssignmentsAccess" className="cursor-pointer">Company Assignments</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <Checkbox
                    id="academicUnitAssignmentsAccess"
                    checked={formData.academicUnitAssignmentsAccess || false}
                    onChange={(e) => setFormData({ ...formData, academicUnitAssignmentsAccess: e.target.checked })}
                  />
                  <Label htmlFor="academicUnitAssignmentsAccess" className="cursor-pointer">Academic Unit Assignments</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <Checkbox
                    id="giftAssignmentsAccess"
                    checked={formData.giftAssignmentsAccess || false}
                    onChange={(e) => setFormData({ ...formData, giftAssignmentsAccess: e.target.checked })}
                  />
                  <Label htmlFor="giftAssignmentsAccess" className="cursor-pointer">Gift Assignments</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <Checkbox
                    id="locationAssignmentsAccess"
                    checked={formData.locationAssignmentsAccess || false}
                    onChange={(e) => setFormData({ ...formData, locationAssignmentsAccess: e.target.checked })}
                  />
                  <Label htmlFor="locationAssignmentsAccess" className="cursor-pointer">Location Assignments</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <Checkbox
                    id="projectAssignmentsAccess"
                    checked={formData.projectAssignmentsAccess || false}
                    onChange={(e) => setFormData({ ...formData, projectAssignmentsAccess: e.target.checked })}
                  />
                  <Label htmlFor="projectAssignmentsAccess" className="cursor-pointer">Project Assignments</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <Checkbox
                    id="grantAssignmentsAccess"
                    checked={formData.grantAssignmentsAccess || false}
                    onChange={(e) => setFormData({ ...formData, grantAssignmentsAccess: e.target.checked })}
                  />
                  <Label htmlFor="grantAssignmentsAccess" className="cursor-pointer">Grant Assignments</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <Checkbox
                    id="paygroupAssignmentsAccess"
                    checked={formData.paygroupAssignmentsAccess || false}
                    onChange={(e) => setFormData({ ...formData, paygroupAssignmentsAccess: e.target.checked })}
                  />
                  <Label htmlFor="paygroupAssignmentsAccess" className="cursor-pointer">Paygroup Assignments</Label>
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

      {/* Reset Password Dialog */}
      <Dialog open={resetPasswordDialogOpen} onOpenChange={setResetPasswordDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Reset Password</DialogTitle>
            <DialogDescription>
              Are you sure you want to reset the password for {editingUser?.firstName} {editingUser?.lastName}? They will need to set a new password on their next login.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setResetPasswordDialogOpen(false)}>
              Cancel
            </Button>
            <Button onClick={handleResetPassword}>
              Reset Password
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}

