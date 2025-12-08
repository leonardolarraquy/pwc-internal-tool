import React, { useState, useEffect } from 'react'
import { academicUnitAssignmentAPI } from '../services/api'
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

export const AcademicUnitAssignments = () => {
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
      const response = await academicUnitAssignmentAPI.getAll(page, size, sortBy, sortDir, search)
      const data = response.data
      setAssignments(data.content)
      setTotalPages(data.totalPages)
      setTotalElements(data.totalElements)
    } catch (error) {
      console.error('Error loading academic unit assignments:', error)
      alert(error.response?.data?.message || 'Error loading academic unit assignments')
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
      hcmAcademicChairAu: assignment.hcmAcademicChairAu || false,
      hcmAcademicDeanAuh: assignment.hcmAcademicDeanAuh || false,
      hcmAcademicFacultyExecutiveAuh: assignment.hcmAcademicFacultyExecutiveAuh || false,
      hcmAcademicFacultyHrAnalystAu: assignment.hcmAcademicFacultyHrAnalystAu || false,
      hcmAcademicProvostPartnerAuh: assignment.hcmAcademicProvostPartnerAuh || false,
      hcmAcademicSchoolDirectorAuh: assignment.hcmAcademicSchoolDirectorAuh || false,
    })
    setEditDialogOpen(true)
  }

  const handleSave = async () => {
    try {
      await academicUnitAssignmentAPI.update(selectedAssignment.id, formData)
      setEditDialogOpen(false)
      setSelectedAssignment(null)
      loadAssignments()
    } catch (error) {
      alert(error.response?.data?.message || 'Error updating assignment')
    }
  }

  const handleDelete = async () => {
    try {
      await academicUnitAssignmentAPI.delete(selectedAssignment.id)
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
        <h1 className="text-3xl font-bold">Academic Unit Assignments</h1>
        <p className="text-muted-foreground">Manage academic unit assignments for users</p>
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
                  No academic unit assignments found
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
                      {assignment.hcmAcademicChairAu && <span className="text-xs bg-blue-100 text-blue-800 px-2 py-1 rounded">Chair</span>}
                      {assignment.hcmAcademicDeanAuh && <span className="text-xs bg-green-100 text-green-800 px-2 py-1 rounded">Dean</span>}
                      {assignment.hcmAcademicFacultyExecutiveAuh && <span className="text-xs bg-purple-100 text-purple-800 px-2 py-1 rounded">Executive</span>}
                      {assignment.hcmAcademicFacultyHrAnalystAu && <span className="text-xs bg-yellow-100 text-yellow-800 px-2 py-1 rounded">HR Analyst</span>}
                      {assignment.hcmAcademicProvostPartnerAuh && <span className="text-xs bg-pink-100 text-pink-800 px-2 py-1 rounded">Provost</span>}
                      {assignment.hcmAcademicSchoolDirectorAuh && <span className="text-xs bg-indigo-100 text-indigo-800 px-2 py-1 rounded">Director</span>}
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
            <DialogTitle>Edit Academic Unit Assignment</DialogTitle>
            <DialogDescription>
              Update roles for {selectedAssignment?.userFirstName} {selectedAssignment?.userLastName}
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
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
            <DialogTitle>Delete Academic Unit Assignment</DialogTitle>
            <DialogDescription>
              Are you sure you want to delete this academic unit assignment? This action cannot be undone.
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
