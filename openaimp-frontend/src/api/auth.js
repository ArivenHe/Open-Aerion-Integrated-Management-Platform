import api from '@/api/http'

export const fetchCaptcha = async () => api.get('/captcha/image')

export const loginByEmail = async (payload) => api.post('/auth/login/email', payload)

export const loginByCid = async (payload) => api.post('/auth/login/cid', payload)

export const register = async (payload) => api.post('/auth/register/email', payload)

export const sendRegisterCode = async (payload) => api.post('/captcha/email/send', payload)

export const fetchCurrentAuthorization = async () => api.get('/auth/me')
