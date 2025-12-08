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
  search: (query) => api.get('/users/search', { params: { query } }),
  importCSV: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/users/import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
  },
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
  assignUser: (id, data) => api.post(`/organization-details/${id}/assign-user`, data),
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
  getByUser: (userId, page = 0, size = 100) =>
    api.get(`/gift-assignments/user/${userId}`, {
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
  getByUser: (userId, page = 0, size = 100) =>
    api.get(`/academic-unit-assignments/user/${userId}`, {
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
  getByUser: (userId, page = 0, size = 100) =>
    api.get(`/company-assignments/user/${userId}`, {
      params: { page, size },
    }),
  create: (data) => api.post('/company-assignments', data),
  update: (id, data) => api.put(`/company-assignments/${id}`, data),
  delete: (id) => api.delete(`/company-assignments/${id}`),
}

export default api

