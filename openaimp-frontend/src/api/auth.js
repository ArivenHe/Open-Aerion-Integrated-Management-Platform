import axios from 'axios'

const api = axios.create({
  timeout: 10000,
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
    const message = error?.response?.data?.message || error.message || '请求失败'
    return Promise.reject(new Error(message))
  }
)

export const fetchCaptcha = async () => api.get('/captcha/image')

export const login = async (payload) => api.post('/auth/login', payload)

export const register = async (payload) => api.post('/auth/register', payload)

export const sendRegisterCode = async (email) => api.post('/auth/register-code', { email })

export const sendForgotPasswordCode = async (email) => api.post('/auth/forgot-password', { email })

export const resetPassword = async (payload) => api.post('/auth/reset-password', payload)
