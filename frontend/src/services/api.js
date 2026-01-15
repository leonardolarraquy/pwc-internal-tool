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

// Organization Types API - for managing organization types
export const organizationTypeAPI = {
  getAll: () => api.get('/organization-types'),
  getActive: () => api.get('/organization-types/active'),
  getById: (id) => api.get(`/organization-types/${id}`),
  getBySlug: (slug) => api.get(`/organization-types/by-slug/${slug}`),
  create: (data) => api.post('/organization-types', data),
  update: (id, data) => api.put(`/organization-types/${id}`, data),
  delete: (id) => api.delete(`/organization-types/${id}`),
}

// Field Definitions API - for managing dynamic boolean fields
export const fieldDefinitionAPI = {
  getByOrgTypeId: (orgTypeId, activeOnly = false) => 
    api.get('/field-definitions', { params: { orgTypeId, activeOnly } }),
  getByOrgTypeSlug: (slug) => api.get(`/field-definitions/by-slug/${slug}`),
  getById: (id) => api.get(`/field-definitions/${id}`),
  create: (data) => api.post('/field-definitions', data),
  update: (id, data) => api.put(`/field-definitions/${id}`, data),
  delete: (id) => api.delete(`/field-definitions/${id}`),
  hardDelete: (id) => api.delete(`/field-definitions/${id}/hard`),
}

// Unified Assignments API - works for all organization types
export const assignmentAPI = {
  getAll: (orgTypeSlug, page = 0, size = 100, sortBy = 'id', sortDir = 'desc', search = '') =>
    api.get('/assignments', {
      params: { orgTypeSlug, page, size, sortBy, sortDir, search },
    }),
  getById: (id) => api.get(`/assignments/${id}`),
  getByOrgDetail: (orgDetailId) => api.get(`/assignments/by-org-detail/${orgDetailId}`),
  getStats: () => api.get('/assignments/stats'),
  create: (data) => api.post('/assignments', data),
  update: (id, data) => api.put(`/assignments/${id}`, data),
  delete: (id) => api.delete(`/assignments/${id}`),
}

export const parameterAPI = {
  getAll: () => api.get('/parameters'),
  getByKey: (key) => api.get(`/parameters/${key}`),
  getImageUrl: (key) => `${API_BASE_URL}/parameters/image/${key}`,
  save: (parameter) => api.post('/parameters', parameter),
  uploadImage: (key, file, description) => {
    const formData = new FormData()
    formData.append('file', file)
    if (description) formData.append('description', description)
    return api.post(`/parameters/upload/${key}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  delete: (key) => api.delete(`/parameters/${key}`)
}

// Reports API
export const reportAPI = {
  generateFullReport: () => 
    api.get('/reports/full-report', {
      responseType: 'blob',
    }),
}

export default api
