import React, { useState, useEffect } from 'react'
import { giftAssignmentAPI } from '../services/api'
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
import { Edit, Trash2, ChevronLeft, ChevronRight, ArrowUpDown } from 'lucide-react'

export const GiftAssignments = () => {
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
  
  const [editDialogOpen, setEditDialogOpen] = useState(false)
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [selectedAssignment, setSelectedAssignment] = useState(null)
  const [formData, setFormData] = useState({})

  useEffect(() => {
    loadAssignments()
  }, [page, size, sortBy, sortDir, search])

  const loadAssignments = async () => {
    setLoading(true)
    try {
      const response = await giftAssignmentAPI.getAll(page, size, sortBy, sortDir, search)
      const data = response.data
      setAssignments(data.content)
      setTotalPages(data.totalPages)
      setTotalElements(data.totalElements)
    } catch (error) {
      console.error('Error loading gift assignments:', error)
      alert(error.response?.data?.message || 'Error loading gift assignments')
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

  const handleEdit = (assignment) => {
    setSelectedAssignment(assignment)
    setFormData({
      finGiftFinancialAnalyst: assignment.finGiftFinancialAnalyst || false,
      finGiftManager: assignment.finGiftManager || false,
      finProfessorshipPartnerGift: assignment.finProfessorshipPartnerGift || false,
    })
    setEditDialogOpen(true)
  }

  const handleSave = async () => {
    try {
      await giftAssignmentAPI.update(selectedAssignment.id, formData)
      setEditDialogOpen(false)
      setSelectedAssignment(null)
      loadAssignments()
    } catch (error) {
      alert(error.response?.data?.message || 'Error updating assignment')
    }
  }

  const handleDelete = async () => {
    try {
      await giftAssignmentAPI.delete(selectedAssignment.id)
      setDeleteDialogOpen(false)
      setSelectedAssignment(null)
      loadAssignments()
    } catch (error) {
      alert(error.response?.data?.message || 'Error deleting assignment')
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
      <div>
        <h1 className="text-3xl font-bold">Gift Assignments</h1>
        <p className="text-muted-foreground">Manage gift assignments for users</p>
      </div>

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
              <SortableHeader column="userFirstName">First Name</SortableHeader>
              <SortableHeader column="userLastName">Last Name</SortableHeader>
              <SortableHeader column="userEmail">Email</SortableHeader>
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
                  No gift assignments found
                </TableCell>
              </TableRow>
            ) : (
              assignments.map((assignment) => (
                <TableRow key={assignment.id}>
                  <TableCell>{assignment.id}</TableCell>
                  <TableCell>{assignment.userFirstName}</TableCell>
                  <TableCell>{assignment.userLastName}</TableCell>
                  <TableCell>{assignment.userEmail}</TableCell>
                  <TableCell>{assignment.organizationName || '-'}</TableCell>
                  <TableCell>
                    <div className="flex flex-wrap gap-1">
                      {assignment.finGiftFinancialAnalyst && <span className="text-xs bg-blue-100 text-blue-800 px-2 py-1 rounded">Financial Analyst</span>}
                      {assignment.finGiftManager && <span className="text-xs bg-green-100 text-green-800 px-2 py-1 rounded">Gift Manager</span>}
                      {assignment.finProfessorshipPartnerGift && <span className="text-xs bg-purple-100 text-purple-800 px-2 py-1 rounded">Professorship Partner</span>}
                    </div>
                  </TableCell>
                  <TableCell>
                    <div className="flex gap-2">
                      <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => handleEdit(assignment)}
                        title="Edit assignment"
                      >
                        <Edit className="h-4 w-4" />
                      </Button>
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

      {/* Edit Dialog */}
      <Dialog open={editDialogOpen} onOpenChange={setEditDialogOpen}>
        <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>Edit Gift Assignment</DialogTitle>
            <DialogDescription>
              Update roles for {selectedAssignment?.userFirstName} {selectedAssignment?.userLastName}
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
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
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setEditDialogOpen(false)}>
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
            <DialogTitle>Delete Gift Assignment</DialogTitle>
            <DialogDescription>
              Are you sure you want to delete this gift assignment? This action cannot be undone.
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
    </div>
  )
}
