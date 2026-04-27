const STORAGE_KEY = 'openaimp.session'
const AUTH_EVENT = 'openaimp:auth-changed'

export const getSession = () => {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (!raw) return null
    return JSON.parse(raw)
  } catch {
    return null
  }
}

export const setSession = (payload) => {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(payload))
  window.dispatchEvent(new Event(AUTH_EVENT))
}

export const clearSession = () => {
  localStorage.removeItem(STORAGE_KEY)
  localStorage.removeItem('cid')
  window.dispatchEvent(new Event(AUTH_EVENT))
}

export const getToken = () => getSession()?.token || ''

export const getCurrentUser = () => getSession()?.user || null

export const getCurrentCid = () => {
  const cid = getSession()?.cid
  return typeof cid === 'number' ? cid : null
}

export const getCurrentRbac = () => getSession()?.rbac || { roles: [], permissions: [] }

export const isAuthenticated = () => Boolean(getToken())

export const hasAdminAccess = () => {
  const rbac = getCurrentRbac()
  const roles = rbac.roles || []
  const permissions = rbac.permissions || []

  return (
    roles.includes('super-admin') ||
    permissions.includes('system:rbac:catalog:read') ||
    permissions.some((permission) => permission.startsWith('system:rbac:'))
  )
}

export const authEventName = AUTH_EVENT
