import api from '@/api/http'

export const loadConfig = async () => api.get('/platform/config')

export const updateConfig = async (payload) => api.post('/platform/config/update', payload)
