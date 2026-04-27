import axios from 'axios'
import { clearSession, getToken } from '@/utils/session'

const api = axios.create({
  timeout: 10000,
})

api.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers = config.headers || {}
    config.headers.aerionstudio = token
  }
  return config
})

api.interceptors.response.use(
  (response) => {
    const payload = response.data
    if (payload?.code && payload.code !== 200) {
      return Promise.reject(new Error(payload.message || '请求失败'))
    }
    return payload
  },
  (error) => {
    if (error?.response?.status === 401) {
      clearSession()
    }
    const message = error?.response?.data?.message || error.message || '请求失败'
    return Promise.reject(new Error(message))
  }
)

export default api
