const ACCESS_TOKEN_KEY = 'accessToken'
const REFRESH_TOKEN_KEY = 'refreshToken'
const ACCESS_EXPIRE_AT_KEY = 'accessTokenExpireAt'
const REFRESH_EXPIRE_AT_KEY = 'refreshTokenExpireAt'
const LEGACY_TOKEN_KEY = 'token'

export const getAccessToken = () => {
  return localStorage.getItem(ACCESS_TOKEN_KEY) || localStorage.getItem(LEGACY_TOKEN_KEY) || ''
}

export const getRefreshToken = () => {
  return localStorage.getItem(REFRESH_TOKEN_KEY) || ''
}

export const hasAccessToken = () => {
  return Boolean(getAccessToken())
}

export const setAuthTokens = (tokenData = {}) => {
  const accessToken = tokenData.accessToken || tokenData.token || ''
  const refreshToken = tokenData.refreshToken || ''

  if (accessToken) {
    localStorage.setItem(ACCESS_TOKEN_KEY, accessToken)
    localStorage.setItem(LEGACY_TOKEN_KEY, accessToken)
  }
  if (refreshToken) {
    localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken)
  }
  if (typeof tokenData.accessExpireSeconds === 'number') {
    localStorage.setItem(ACCESS_EXPIRE_AT_KEY, String(Date.now() + tokenData.accessExpireSeconds * 1000))
  }
  if (typeof tokenData.refreshExpireSeconds === 'number') {
    localStorage.setItem(REFRESH_EXPIRE_AT_KEY, String(Date.now() + tokenData.refreshExpireSeconds * 1000))
  }
}

export const clearAuthState = () => {
  localStorage.removeItem(ACCESS_TOKEN_KEY)
  localStorage.removeItem(REFRESH_TOKEN_KEY)
  localStorage.removeItem(ACCESS_EXPIRE_AT_KEY)
  localStorage.removeItem(REFRESH_EXPIRE_AT_KEY)
  localStorage.removeItem(LEGACY_TOKEN_KEY)
  localStorage.removeItem('currentUser')
}

