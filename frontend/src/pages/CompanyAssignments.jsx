import React, { useState, useEffect } from 'react'
import { companyAssignmentAPI } from '../services/api'
import { Button } from '../components/ui/button'
import { Input } from '../components/ui/input'
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
import { Trash2, ChevronLeft, ChevronRight, ArrowUpDown } from 'lucide-react'

export const CompanyAssignments = () => {
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

  useEffect(() => {
    loadAssignments()
  }, [page, size, sortBy, sortDir, search])

  const loadAssignments = async () => {
    setLoading(true)
    try {
      const response = await companyAssignmentAPI.getAll(page, size, sortBy, sortDir, search)
      const data = response.data
      setAssignments(data.content)
      setTotalPages(data.totalPages)
      setTotalElements(data.totalElements)
    } catch (error) {
      console.error('Error loading company assignments:', error)
      alert(error.response?.data?.message || 'Error loading company assignments')
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
      await companyAssignmentAPI.delete(selectedAssignment.id)
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
        <h1 className="text-3xl font-bold">Company Assignments</h1>
        <p className="text-muted-foreground">Manage company assignments for users</p>
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
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <SortableHeader column="id">ID</SortableHeader>
              <SortableHeader column="userFirstName">First Name</SortableHeader>
              <SortableHeader column="userLastName">Last Name</SortableHeader>
              <SortableHeader column="userEmail">Email</SortableHeader>
              <SortableHeader column="userEmployeeId">Employee ID</SortableHeader>
              <SortableHeader column="organizationName">Organization</SortableHeader>
              <SortableHeader column="referenceId">Reference ID</SortableHeader>
              <TableHead>Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {loading ? (
              <TableRow>
                <TableCell colSpan={8} className="text-center py-8">
                  Loading...
                </TableCell>
              </TableRow>
            ) : assignments.length === 0 ? (
              <TableRow>
                <TableCell colSpan={8} className="text-center py-8">
                  No company assignments found
                </TableCell>
              </TableRow>
            ) : (
              assignments.map((assignment) => (
                <TableRow key={assignment.id}>
                  <TableCell>{assignment.id}</TableCell>
                  <TableCell>{assignment.userFirstName}</TableCell>
                  <TableCell>{assignment.userLastName}</TableCell>
                  <TableCell>{assignment.userEmail}</TableCell>
                  <TableCell>{assignment.userEmployeeId}</TableCell>
                  <TableCell>{assignment.organizationName || '-'}</TableCell>
                  <TableCell>{assignment.referenceId || '-'}</TableCell>
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

      {/* Delete Confirmation Dialog */}
      <Dialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Delete Company Assignment</DialogTitle>
            <DialogDescription>
              Are you sure you want to delete this company assignment? This action cannot be undone.
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
