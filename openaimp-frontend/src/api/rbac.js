import api from '@/api/http'

export const fetchCurrentRbac = async () => api.get('/rbac/me')

export const fetchRbacCatalog = async () => api.get('/rbac/catalog')
