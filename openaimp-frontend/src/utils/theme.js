const THEME_KEY = 'openaimp.theme'
export const themeEventName = 'openaimp:theme-changed'

export const getStoredTheme = () => localStorage.getItem(THEME_KEY) || 'light'

export const isDarkTheme = () => document.documentElement.classList.contains('dark')

export const applyTheme = (theme) => {
  const nextTheme = theme === 'dark' ? 'dark' : 'light'
  const isDark = nextTheme === 'dark'

  document.documentElement.classList.toggle('dark', isDark)
  document.documentElement.style.colorScheme = isDark ? 'dark' : 'light'
  localStorage.setItem(THEME_KEY, nextTheme)
  window.dispatchEvent(new Event(themeEventName))

  return isDark
}

export const initializeTheme = () => applyTheme(getStoredTheme())

export const toggleTheme = () => applyTheme(isDarkTheme() ? 'light' : 'dark')
