import axios from 'axios'
import router from '../router'
import {clearAuthState, getAccessToken, getRefreshToken, setAuthTokens} from './auth'

export const GLOBAL_ERROR_EVENT = 'app:global-error'
export const SESSION_EXPIRED_MESSAGE = '会话过期，请重新登录'

let isHandlingUnauthorized = false
let refreshPromise = null

const notifyGlobalError = (message) => {
  window.dispatchEvent(new CustomEvent(GLOBAL_ERROR_EVENT, {
    detail: {
      type: 'error',
      message
    }
  }))
}

export const handleUnauthorized = (message = SESSION_EXPIRED_MESSAGE) => {
  if (isHandlingUnauthorized) return

  isHandlingUnauthorized = true
  clearAuthState()
  notifyGlobalError(message || SESSION_EXPIRED_MESSAGE)

  const redirectTask = router.currentRoute.value.path === '/login'
    ? Promise.resolve()
    : router.replace('/login')

  redirectTask.finally(() => {
    setTimeout(() => {
      isHandlingUnauthorized = false
    }, 300)
  })
}

const isAuthEndpoint = (url = '') => {
  return url.includes('/auth/login')
    || url.includes('/auth/refresh')
    || url.includes('/auth/register')
}

const requestRefreshToken = async () => {
  const refreshToken = getRefreshToken()
  if (!refreshToken) {
    throw new Error('refresh token缺失')
  }

  const response = await axios.post('/api/auth/refresh', { refreshToken }, { timeout: 10000 })
  const result = response?.data
  const payload = result?.data

  if (result?.code !== 0 || !payload?.accessToken) {
    throw new Error(result?.msg || 'refresh token无效或已过期')
  }

  setAuthTokens(payload)
  return payload.accessToken
}

const getRefreshPromise = () => {
  if (!refreshPromise) {
    refreshPromise = requestRefreshToken()
      .finally(() => {
        refreshPromise = null
      })
  }
  return refreshPromise
}

const attachInterceptors = (instance) => {
  instance.interceptors.request.use(
    (config) => {
      const token = getAccessToken()
      if (token) {
        config.headers = config.headers || {}
        config.headers['access_token'] = token
      }
      return config
    },
    (error) => Promise.reject(error)
  )

  instance.interceptors.response.use(
    (response) => response,
    async (error) => {
      const status = error.response?.status
      const originalRequest = error.config || {}
      if (status === 401) {
        const requestUrl = String(originalRequest.url || '')
        const shouldTryRefresh = !originalRequest.__retry && !isAuthEndpoint(requestUrl)
        if (shouldTryRefresh) {
          originalRequest.__retry = true
          try {
            const newAccessToken = await getRefreshPromise()
            originalRequest.headers = originalRequest.headers || {}
            originalRequest.headers['access_token'] = newAccessToken
            return instance(originalRequest)
          } catch {
            handleUnauthorized(SESSION_EXPIRED_MESSAGE)
            return Promise.reject(error)
          }
        }
        handleUnauthorized(SESSION_EXPIRED_MESSAGE)
      }
      return Promise.reject(error)
    }
  )
}

export const createRequest = (baseURL = '/api') => {
  const requestConfig = typeof baseURL === 'string'
    ? { baseURL }
    : (baseURL || {})

  const instance = axios.create({
    timeout: 10000,
    ...requestConfig
  })
  attachInterceptors(instance)
  return instance
}

const request = createRequest('/api')

export default request
