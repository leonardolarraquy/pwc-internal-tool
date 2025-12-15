import axios from 'axios'

const API_BASE_URL = '/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Add token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Handle 401 errors (unauthorized, including expired JWT)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Clear authentication data
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      // Redirect to login (using replace to prevent back button navigation)
      window.location.replace('/login')
    }
    return Promise.reject(error)
  }
)

export const authAPI = {
  login: (email, password) =>
    api.post('/auth/login', { email, password }),
  changePassword: (email, newPassword) =>
    api.post('/auth/change-password', { email, newPassword }),
}

export const employeeAPI = {
  getAll: (page = 0, size = 100, sortBy = 'id', sortDir = 'asc', search = '') =>
    api.get('/employees', {
      params: { page, size, sortBy, sortDir, search },
    }),
  getById: (id) => api.get(`/employees/${id}`),
  create: (data) => api.post('/employees', data),
  update: (id, data) => api.put(`/employees/${id}`, data),
  delete: (id) => api.delete(`/employees/${id}`),
  search: (query) => api.get('/employees/search', { params: { query } }),
  findByWorkerId: (workerId) => api.get('/employees/find-by-worker-id', { params: { workerId } }),
  findByEmail: (email) => api.get('/employees/find-by-email', { params: { email } }),
  findByPositionId: (positionId) => api.get('/employees/find-by-position-id', { params: { positionId } }),
  importCSV: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/employees/import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
  },
}

export const userAPI = {
  getAll: (page = 0, size = 100, sortBy = 'id', sortDir = 'asc', search = '', accessFilter = 'all') =>
    api.get('/users', {
      params: { page, size, sortBy, sortDir, search, accessFilter },
    }),
  getById: (id) => api.get(`/users/${id}`),
  create: (data) => api.post('/users', data),
  update: (id, data) => api.put(`/users/${id}`, data),
  delete: (id) => api.delete(`/users/${id}`),
  resetPassword: (id) => api.post(`/users/${id}/reset-password`),
  getStats: () => api.get('/users/stats'),
  getMyAssignmentStats: () => api.get('/users/my-assignment-stats'),
}

export const organizationDetailAPI = {
  getAll: (page = 0, size = 100, sortBy = 'id', sortDir = 'asc', search = '', organizationTypeFilter = 'all') =>
    api.get('/organization-details', {
      params: { page, size, sortBy, sortDir, search, organizationTypeFilter },
    }),
  getOrganizationTypes: () => api.get('/organization-details/organization-types'),
  getById: (id) => api.get(`/organization-details/${id}`),
  create: (data) => api.post('/organization-details', data),
  update: (id, data) => api.put(`/organization-details/${id}`, data),
  delete: (id) => api.delete(`/organization-details/${id}`),
  assignEmployee: (id, data) => api.post(`/organization-details/${id}/assign-employee`, data),
  getAssignments: (id) => api.get(`/organization-details/${id}/assignments`),
  importCSV: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/organization-details/import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
  },
}

export const giftAssignmentAPI = {
  getAll: (page = 0, size = 100, sortBy = 'id', sortDir = 'asc', search = '') =>
    api.get('/gift-assignments', {
      params: { page, size, sortBy, sortDir, search },
    }),
  getById: (id) => api.get(`/gift-assignments/${id}`),
  getByEmployee: (employeeId, page = 0, size = 100) =>
    api.get(`/gift-assignments/employee/${employeeId}`, {
      params: { page, size },
    }),
  create: (data) => api.post('/gift-assignments', data),
  update: (id, data) => api.put(`/gift-assignments/${id}`, data),
  delete: (id) => api.delete(`/gift-assignments/${id}`),
}

export const academicUnitAssignmentAPI = {
  getAll: (page = 0, size = 100, sortBy = 'id', sortDir = 'asc', search = '') =>
    api.get('/academic-unit-assignments', {
      params: { page, size, sortBy, sortDir, search },
    }),
  getById: (id) => api.get(`/academic-unit-assignments/${id}`),
  getByEmployee: (employeeId, page = 0, size = 100) =>
    api.get(`/academic-unit-assignments/employee/${employeeId}`, {
      params: { page, size },
    }),
  create: (data) => api.post('/academic-unit-assignments', data),
  update: (id, data) => api.put(`/academic-unit-assignments/${id}`, data),
  delete: (id) => api.delete(`/academic-unit-assignments/${id}`),
}

export const companyAssignmentAPI = {
  getAll: (page = 0, size = 100, sortBy = 'id', sortDir = 'asc', search = '') =>
    api.get('/company-assignments', {
      params: { page, size, sortBy, sortDir, search },
    }),
  getById: (id) => api.get(`/company-assignments/${id}`),
  getByEmployee: (employeeId, page = 0, size = 100) =>
    api.get(`/company-assignments/employee/${employeeId}`, {
      params: { page, size },
    }),
  create: (data) => api.post('/company-assignments', data),
  update: (id, data) => api.put(`/company-assignments/${id}`, data),
  delete: (id) => api.delete(`/company-assignments/${id}`),
}

export const locationAssignmentAPI = {
  getAll: (page = 0, size = 100, sortBy = 'id', sortDir = 'asc', search = '') =>
    api.get('/location-assignments', {
      params: { page, size, sortBy, sortDir, search },
    }),
  getById: (id) => api.get(`/location-assignments/${id}`),
  getByEmployee: (employeeId, page = 0, size = 100) =>
    api.get(`/location-assignments/employee/${employeeId}`, {
      params: { page, size },
    }),
  create: (data) => api.post('/location-assignments', data),
  update: (id, data) => api.put(`/location-assignments/${id}`, data),
  delete: (id) => api.delete(`/location-assignments/${id}`),
}

export const projectAssignmentAPI = {
  getAll: (page = 0, size = 100, sortBy = 'id', sortDir = 'asc', search = '') =>
    api.get('/project-assignments', {
      params: { page, size, sortBy, sortDir, search },
    }),
  getById: (id) => api.get(`/project-assignments/${id}`),
  getByEmployee: (employeeId, page = 0, size = 100) =>
    api.get(`/project-assignments/employee/${employeeId}`, {
      params: { page, size },
    }),
  create: (data) => api.post('/project-assignments', data),
  update: (id, data) => api.put(`/project-assignments/${id}`, data),
  delete: (id) => api.delete(`/project-assignments/${id}`),
}

export const grantAssignmentAPI = {
  getAll: (page = 0, size = 100, sortBy = 'id', sortDir = 'asc', search = '') =>
    api.get('/grant-assignments', {
      params: { page, size, sortBy, sortDir, search },
    }),
  getById: (id) => api.get(`/grant-assignments/${id}`),
  getByEmployee: (employeeId, page = 0, size = 100) =>
    api.get(`/grant-assignments/employee/${employeeId}`, {
      params: { page, size },
    }),
  create: (data) => api.post('/grant-assignments', data),
  update: (id, data) => api.put(`/grant-assignments/${id}`, data),
  delete: (id) => api.delete(`/grant-assignments/${id}`),
}

export const paygroupAssignmentAPI = {
  getAll: (page = 0, size = 100, sortBy = 'id', sortDir = 'asc', search = '') =>
    api.get('/paygroup-assignments', {
      params: { page, size, sortBy, sortDir, search },
    }),
  getById: (id) => api.get(`/paygroup-assignments/${id}`),
  getByEmployee: (employeeId, page = 0, size = 100) =>
    api.get(`/paygroup-assignments/employee/${employeeId}`, {
      params: { page, size },
    }),
  create: (data) => api.post('/paygroup-assignments', data),
  update: (id, data) => api.put(`/paygroup-assignments/${id}`, data),
  delete: (id) => api.delete(`/paygroup-assignments/${id}`),
}

export default api

