import { reactive, ref, computed } from 'vue'

// 主题状态
export const useThemeStore = () => {
  const theme = ref(localStorage.getItem('theme') || 'dark')

  const isDark = computed(() => theme.value === 'dark')
  const isLight = computed(() => theme.value === 'light')

  const setTheme = (newTheme) => {
    theme.value = newTheme
    localStorage.setItem('theme', newTheme)
    applyTheme()
  }

  const toggleTheme = () => {
    const newTheme = theme.value === 'dark' ? 'light' : 'dark'
    setTheme(newTheme)
  }

  const applyTheme = () => {
    const html = document.documentElement
    if (theme.value === 'dark') {
      html.classList.add('dark')
      html.classList.remove('light')
    } else {
      html.classList.add('light')
      html.classList.remove('dark')
    }
  }

  const initTheme = () => {
    applyTheme()
  }

  return {
    theme,
    isDark,
    isLight,
    setTheme,
    toggleTheme,
    initTheme
  }
}

// 用户状态
export const useUserStore = () => {
  const user = reactive({
    isLoggedIn: false,
    userId: null,
    username: null,
    phone: null,
    token: localStorage.getItem('access_token') || null
  })

  const login = (token, userId, username, phone) => {
    user.token = token
    user.userId = userId
    user.username = username
    user.phone = phone
    user.isLoggedIn = true
    localStorage.setItem('access_token', token)
    localStorage.setItem('userinfo', JSON.stringify({
      userId,
      username,
      phone
    }))
  }

  const logout = () => {
    user.token = null
    user.userId = null
    user.username = null
    user.phone = null
    user.isLoggedIn = false
    localStorage.removeItem('access_token')
    localStorage.removeItem('userinfo')
  }

  const checkLoginStatus = () => {
    const token = localStorage.getItem('access_token')
    if (token) {
      user.token = token
      user.isLoggedIn = true
      // 恢复用户信息
      const userinfoStr = localStorage.getItem('userinfo')
      if (userinfoStr) {
        try {
          const userinfo = JSON.parse(userinfoStr)
          user.userId = userinfo.userId
          user.username = userinfo.username
          user.phone = userinfo.phone
        } catch (e) {
          console.error('解析用户信息失败:', e)
        }
      }
    }
  }

  const updateUserPhone = (phone) => {
    user.phone = phone
    // 更新localStorage中的用户信息
    const userinfoStr = localStorage.getItem('userinfo')
    if (userinfoStr) {
      try {
        const userinfo = JSON.parse(userinfoStr)
        userinfo.phone = phone
        localStorage.setItem('userinfo', JSON.stringify(userinfo))
      } catch (e) {
        console.error('更新用户信息失败:', e)
      }
    }
  }

  return {
    user,
    login,
    logout,
    checkLoginStatus,
    updateUserPhone
  }
}

// 商品状态
export const useGoodsStore = () => {
  const goodsList = ref([])
  const currentGoods = ref(null)
  const loading = ref(false)
  const error = ref(null)

  const setGoodsList = (list) => {
    goodsList.value = list
  }

  const setCurrentGoods = (goods) => {
    currentGoods.value = goods
  }

  const setLoading = (status) => {
    loading.value = status
  }

  const setError = (err) => {
    error.value = err
  }

  return {
    goodsList,
    currentGoods,
    loading,
    error,
    setGoodsList,
    setCurrentGoods,
    setLoading,
    setError
  }
}

// 订单状态
export const useOrderStore = () => {
  const orderList = ref([])
  const currentOrder = ref(null)
  const loading = ref(false)
  const error = ref(null)

  const setOrderList = (list) => {
    orderList.value = list
  }

  const setCurrentOrder = (order) => {
    currentOrder.value = order
  }

  const setLoading = (status) => {
    loading.value = status
  }

  const setError = (err) => {
    error.value = err
  }

  return {
    orderList,
    currentOrder,
    loading,
    error,
    setOrderList,
    setCurrentOrder,
    setLoading,
    setError
  }
}
