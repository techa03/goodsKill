<template>
  <div class="min-h-screen bg-[var(--bg-primary)] transition-colors duration-300">
    <!-- 确认弹窗 -->
    <ConfirmDialog
      :visible="showConfirmDialog"
      title="取消订单"
      message="确定要取消这个订单吗？"
      confirm-text="确定取消"
      cancel-text="再想想"
      @confirm="handleConfirmCancelOrder"
      @cancel="showConfirmDialog = false"
    />

    <!-- 导航栏 -->
    <nav class="fixed top-0 left-0 right-0 z-50 glass-card border-b border-[var(--border-color)]">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16 items-center">
          <div class="flex items-center">
            <router-link to="/" class="flex items-center space-x-3">
              <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-primary to-secondary flex items-center justify-center shadow-glow">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-[var(--text-primary)]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                </svg>
              </div>
              <span class="text-xl font-bold gradient-text font-display">秒杀商城</span>
            </router-link>
          </div>
          <div class="flex items-center space-x-4">
            <router-link to="/" class="text-[var(--text-muted)] hover:text-[var(--text-primary)] px-4 py-2 rounded-xl text-sm font-medium transition-all hover:bg-black/5 dark:hover:bg-white/5">首页</router-link>
            <router-link to="/orders" class="text-[var(--text-primary)] px-4 py-2 rounded-xl text-sm font-medium bg-black/5 dark:bg-white/10">我的订单</router-link>
            <!-- 主题切换 -->
            <button @click="toggleTheme" class="theme-toggle" title="切换主题">
              <svg class="sun-icon w-5 h-5 text-[var(--text-primary)]" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z" />
              </svg>
              <svg class="moon-icon w-5 h-5 text-[var(--text-primary)]" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z" />
              </svg>
            </button>
            <!-- 用户中心 -->
            <div class="relative">
              <button @click="toggleUserMenu" class="flex items-center space-x-2 text-[var(--text-secondary)] hover:text-[var(--text-primary)] p-2 rounded-xl hover:bg-black/5 dark:hover:bg-white/5 transition-all">
                <div class="w-8 h-8 rounded-full bg-gradient-to-br from-primary/30 to-secondary/30 flex items-center justify-center border border-[var(--border-color)] overflow-hidden">
                  <img
                    v-if="user.avatar && user.avatar.trim() !== ''"
                    :src="user.avatar"
                    alt="头像"
                    class="w-full h-full object-cover"
                    @error="user.avatar = ''"
                  />
                  <svg
                    v-else
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-4 w-4 text-[var(--text-primary)]"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                  </svg>
                </div>
                <span class="hidden md:inline text-sm font-medium">我的账户</span>
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 hidden md:block text-[var(--text-muted)]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                </svg>
              </button>
              <div v-if="showUserMenu" class="absolute right-0 mt-2 w-48 glass-card rounded-2xl py-2 z-50 shadow-card border border-[var(--border-color)]">
                <router-link to="/profile" class="block px-4 py-2.5 text-sm text-[var(--text-secondary)] hover:text-[var(--text-primary)] hover:bg-black/5 dark:hover:bg-white/5 transition-colors">个人中心</router-link>
                <router-link to="/orders" class="block px-4 py-2.5 text-sm text-[var(--text-secondary)] hover:text-[var(--text-primary)] hover:bg-black/5 dark:hover:bg-white/5 transition-colors">我的订单</router-link>
                <div class="my-1 border-t border-[var(--border-color)]"></div>
                <button @click="handleLogout" class="block w-full text-left px-4 py-2.5 text-sm text-danger hover:bg-black/5 dark:hover:bg-white/5 transition-colors">退出登录</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <!-- 订单列表 -->
    <div class="pt-24 pb-12">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <!-- Header -->
        <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-8">
          <div>
            <h1 class="text-3xl font-bold text-[var(--text-primary)] font-display mb-2">我的订单</h1>
            <p class="text-[var(--text-muted)]">查看和管理您的所有订单</p>
          </div>
          <router-link to="/" class="btn-secondary px-6 py-3 rounded-xl flex items-center gap-2">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
            继续购物
          </router-link>
        </div>

        <!-- 订单状态筛选 -->
        <div class="flex gap-2 mb-8 overflow-x-auto pb-2 scrollbar-hide">
          <button
            v-for="status in orderStatuses"
            :key="status.value"
            @click="activeStatus = status.value"
            :class="[
              'flex items-center gap-2 px-5 py-3 rounded-xl text-sm font-medium whitespace-nowrap transition-all',
              activeStatus === status.value
                ? 'bg-gradient-to-r from-primary to-secondary text-[var(--text-primary)] shadow-glow'
                : 'bg-black/5 dark:bg-white/5 text-[var(--text-muted)] hover:text-[var(--text-primary)] hover:bg-black/10 dark:hover:bg-white/10 border border-[var(--border-color)]'
            ]"
          >
            <span v-if="status.icon" class="text-lg">{{ status.icon }}</span>
            <span>{{ status.label }}</span>
            <span
              v-if="status.count > 0"
              class="ml-1 px-2 py-0.5 rounded-full text-xs"
              :class="activeStatus === status.value ? 'bg-black/20 dark:bg-white/20' : 'bg-black/10 dark:bg-white/10'"
            >
              {{ status.count }}
            </span>
          </button>
        </div>

        <!-- Loading State -->
        <div v-if="loading" class="space-y-6">
          <div v-for="i in 3" :key="i" class="glass-card rounded-3xl p-6">
            <div class="flex items-center gap-4 mb-4">
              <div class="w-20 h-20 rounded-xl shimmer"></div>
              <div class="flex-1 space-y-2">
                <div class="h-5 shimmer rounded w-1/3"></div>
                <div class="h-4 shimmer rounded w-1/4"></div>
              </div>
              <div class="h-8 shimmer rounded w-24"></div>
            </div>
            <div class="flex justify-between items-center pt-4 border-t border-[var(--border-color)]">
              <div class="h-4 shimmer rounded w-32"></div>
              <div class="flex gap-3">
                <div class="h-10 shimmer rounded-xl w-24"></div>
                <div class="h-10 shimmer rounded-xl w-24"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- Error State -->
        <div v-else-if="error" class="text-center py-20">
          <div class="w-20 h-20 mx-auto mb-6 rounded-full bg-danger/10 flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 text-danger" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
          </div>
          <p class="text-[var(--text-muted)] text-lg">{{ error }}</p>
          <button @click="fetchOrderList" class="mt-4 btn-primary px-6 py-3 rounded-xl">重新加载</button>
        </div>

        <!-- Empty State -->
        <div v-else-if="filteredOrders.length === 0" class="text-center py-20">
          <div class="w-24 h-24 mx-auto mb-6 rounded-full bg-[var(--bg-secondary)] flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-[var(--text-muted)]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-[var(--text-primary)] mb-2">暂无订单</h3>
          <p class="text-[var(--text-muted)] mb-6">您还没有任何订单，快去选购心仪的商品吧</p>
          <router-link to="/" class="btn-primary px-8 py-3 rounded-xl inline-flex items-center gap-2">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
            </svg>
            去购物
          </router-link>
        </div>

        <!-- Orders List -->
        <div v-else class="space-y-6">
          <div
            v-for="order in filteredOrders"
            :key="order.id"
            class="glass-card rounded-3xl overflow-hidden group hover:border-primary/20 transition-all"
          >
            <!-- Order Header -->
            <div class="p-6 border-b border-[var(--border-color)]">
              <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
                <div class="flex items-center gap-4">
                  <div class="w-12 h-12 rounded-xl bg-gradient-to-br from-primary/20 to-secondary/20 flex items-center justify-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                    </svg>
                  </div>
                  <div>
                    <div class="flex items-center gap-2 mb-1">
                      <span class="text-[var(--text-muted)] text-sm">订单号:</span>
                      <span class="text-[var(--text-primary)] font-mono">{{ order.id }}</span>
                    </div>
                    <div class="flex items-center gap-2 text-sm text-[var(--text-muted)]">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                      {{ formatTime(order.createTime) }}
                    </div>
                  </div>
                </div>
                <div :class="[
                  'flex items-center gap-2 px-4 py-2 rounded-full text-sm font-medium',
                  getStatusStyle(order.status)
                ]">
                  <span class="w-2 h-2 rounded-full" :class="getStatusDotColor(order.status)"></span>
                  {{ getStatusText(order.status) }}
                </div>
              </div>
            </div>

            <!-- Order Content -->
            <div class="p-6">
              <div class="flex flex-col sm:flex-row gap-6">
                <!-- Product Image -->
                <div class="w-full sm:w-32 h-32 rounded-2xl overflow-hidden bg-[var(--bg-secondary)] flex-shrink-0">
                  <img
                    :src="order.goodsImg || 'https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=200&h=200&fit=crop'"
                    :alt="order.goodsName"
                    class="w-full h-full object-cover"
                  />
                </div>

                <!-- Product Info -->
                <div class="flex-1 min-w-0">
                  <h3 class="text-lg font-semibold text-[var(--text-primary)] mb-2 line-clamp-1">{{ order.goodsName || '商品名称' }}</h3>
                  <p class="text-[var(--text-muted)] text-sm mb-4 line-clamp-2">{{ order.goodsTitle || '商品描述' }}</p>
                  <div class="flex items-center gap-4">
                    <span class="text-2xl font-bold text-[var(--text-primary)] font-display">¥{{ order.seckillPrice || 0 }}</span>
                    <span class="text-[var(--text-muted)] line-through">¥{{ (order.seckillPrice || 0) * 1.2 }}</span>
                    <span class="badge badge-danger">-20%</span>
                  </div>
                </div>

                <!-- Actions -->
                <div class="flex flex-row sm:flex-col gap-3 justify-end">
                  <router-link
                    :to="`/order/${order.id}`"
                    class="px-6 py-3 rounded-xl bg-black/5 dark:bg-white/5 text-[var(--text-secondary)] hover:text-[var(--text-primary)] hover:bg-black/10 dark:hover:bg-white/10 transition-all text-sm font-medium text-center whitespace-nowrap"
                  >
                    查看详情
                  </router-link>
                  <button
                    v-if="order.status === 0 || order.status === 1"
                    @click="handlePay(order)"
                    class="px-6 py-3 rounded-xl bg-gradient-to-r from-primary to-secondary text-[var(--text-primary)] font-medium hover:shadow-glow transition-all text-sm whitespace-nowrap"
                  >
                    立即支付
                  </button>
                  <button
                    v-if="order.status === 0 || order.status === 1"
                    @click="handleCancelOrder(order)"
                    class="px-6 py-3 rounded-xl bg-danger/20 text-danger border border-danger/30 font-medium hover:bg-danger/30 transition-all text-sm whitespace-nowrap"
                  >
                    取消订单
                  </button>
                  <button
                    v-if="order.status === 2"
                    class="px-6 py-3 rounded-xl bg-success/20 text-success border border-success/30 font-medium hover:bg-success/30 transition-all text-sm whitespace-nowrap"
                  >
                    查看物流
                  </button>
                </div>
              </div>
            </div>

            <!-- Order Footer -->
            <div class="px-6 py-4 bg-black/5 dark:bg-white/5 border-t border-[var(--border-color)]">
              <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
                <div class="flex items-center gap-6 text-sm">
                  <span class="text-[var(--text-muted)]">
                    共 <span class="text-[var(--text-primary)]">1</span> 件商品
                  </span>
                  <span class="text-[var(--text-muted)]">
                    实付: <span class="text-[var(--text-primary)] font-bold text-lg">¥{{ order.seckillPrice || 0 }}</span>
                  </span>
                </div>
                <div class="flex items-center gap-2 text-sm text-[var(--text-muted)]">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                  </svg>
                  安全支付保障
                </div>
              </div>
            </div>
          </div>

          <!-- 分页控件 -->
          <div v-if="total > 0" class="flex justify-center mt-8">
            <nav class="flex items-center gap-2">
              <button
                @click="handlePageChange(1)"
                :disabled="currentPage === 1"
                class="p-3 rounded-xl bg-black/5 dark:bg-white/5 text-[var(--text-muted)] hover:text-[var(--text-primary)] hover:bg-black/10 dark:hover:bg-white/10 disabled:opacity-30 disabled:cursor-not-allowed transition-all"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 19l-7-7 7-7m8 14l-7-7 7-7" />
                </svg>
              </button>
              <button
                @click="handlePageChange(currentPage - 1)"
                :disabled="currentPage === 1"
                class="p-3 rounded-xl bg-black/5 dark:bg-white/5 text-[var(--text-muted)] hover:text-[var(--text-primary)] hover:bg-black/10 dark:hover:bg-white/10 disabled:opacity-30 disabled:cursor-not-allowed transition-all"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
                </svg>
              </button>

              <div class="flex items-center gap-1 px-4">
                <span class="text-[var(--text-primary)] font-medium">{{ currentPage }}</span>
                <span class="text-[var(--text-muted)]">/</span>
                <span class="text-[var(--text-muted)]">{{ totalPages }}</span>
              </div>

              <button
                @click="handlePageChange(currentPage + 1)"
                :disabled="currentPage === totalPages"
                class="p-3 rounded-xl bg-black/5 dark:bg-white/5 text-[var(--text-muted)] hover:text-[var(--text-primary)] hover:bg-black/10 dark:hover:bg-white/10 disabled:opacity-30 disabled:cursor-not-allowed transition-all"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                </svg>
              </button>
              <button
                @click="handlePageChange(totalPages)"
                :disabled="currentPage === totalPages"
                class="p-3 rounded-xl bg-black/5 dark:bg-white/5 text-[var(--text-muted)] hover:text-[var(--text-primary)] hover:bg-black/10 dark:hover:bg-white/10 disabled:opacity-30 disabled:cursor-not-allowed transition-all"
              >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 5l7 7-7 7M5 5l7 7-7 7" />
                </svg>
              </button>
            </nav>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../api'
import { useUserStore, useOrderStore, useThemeStore } from '../stores'
import ConfirmDialog from '../components/ConfirmDialog.vue'

const router = useRouter()
const { user, logout, updateUserInfo } = useUserStore()
const { orderList, loading, error, setOrderList, setLoading, setError } = useOrderStore()
const { toggleTheme, initTheme } = useThemeStore()

// 用户菜单状态
const showUserMenu = ref(false)

// 切换用户菜单
const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
}

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const response = await api.getCustomerUserInfo()
    if (response.code === 0 || response.code === 200) {
      updateUserInfo(response.data)
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

const activeStatus = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = ref(0)
const showConfirmDialog = ref(false)
const currentOrder = ref(null)

const orderStatuses = [
  { label: '全部订单', value: 'all', icon: '📦', count: 0 },
  { label: '待支付', value: '1', icon: '⏰', count: 0 },
  { label: '已支付', value: '2', icon: '✅', count: 0 },
  { label: '已取消', value: '3', icon: '❌', count: 0 },
]

const handleLogout = () => {
  logout()
  router.push('/login')
}

const formatTime = (timestamp) => {
  if (!timestamp) return '未知时间'
  const date = new Date(timestamp)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const getStatusStyle = (state) => {
  const styles = {
    0: 'bg-warning/10 text-warning border border-warning/30',
    1: 'bg-warning/10 text-warning border border-warning/30',
    2: 'bg-success/10 text-success border border-success/30',
    3: 'bg-danger/10 text-danger border border-danger/30',
  }
  return styles[state] || 'bg-white/5 text-white/60 border border-white/10'
}

const getStatusDotColor = (state) => {
  const colors = {
    0: 'bg-warning shadow-[0_0_8px_#f59e0b]',
    1: 'bg-warning shadow-[0_0_8px_#f59e0b]',
    2: 'bg-success shadow-[0_0_8px_#10b981]',
    3: 'bg-danger shadow-[0_0_8px_#ef4444]',
  }
  return colors[state] || 'bg-white/40'
}

const getStatusText = (state) => {
  const statusMap = {
    0: '待支付',
    1: '待支付',
    2: '已支付',
    3: '已取消',
  }
  return statusMap[state] || '未知状态'
}

const filteredOrders = computed(() => {
  if (activeStatus.value === 'all') {
    return orderList.value || []
  }
  const targetStatus = parseInt(activeStatus.value)
  return (orderList.value || []).filter(order => {
    const orderStatus = typeof order.status === 'string' ? parseInt(order.status) : order.status
    return orderStatus === targetStatus
  })
})

const handlePay = (order) => {
  router.push(`/order/${order.id}`)
}

const handleCancelOrder = (order) => {
  currentOrder.value = order
  showConfirmDialog.value = true
}

const handleConfirmCancelOrder = async () => {
  if (!currentOrder.value) return

  try {
    const response = await api.cancelOrder(currentOrder.value.id)
    if (response.code === 0 || response.code === 200) {
      showToastMessage('取消订单成功')
      // 重新获取订单列表
      fetchOrderList()
    } else {
      showToastMessage(response.message || response.msg || '取消订单失败')
    }
  } catch (err) {
    showToastMessage('取消订单失败，请稍后重试')
    console.error('取消订单失败:', err)
  } finally {
    showConfirmDialog.value = false
    currentOrder.value = null
  }
}

const showToastMessage = (message) => {
  const toast = document.createElement('div')
  toast.className = 'fixed bottom-8 left-1/2 -translate-x-1/2 z-50 glass-card px-6 py-3 rounded-full flex items-center gap-2'
  toast.innerHTML = `
    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-success" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
    </svg>
    <span class="text-[var(--text-primary)]">${message}</span>
  `
  document.body.appendChild(toast)

  setTimeout(() => {
    toast.style.opacity = '0'
    toast.style.transition = 'opacity 0.3s ease'
    setTimeout(() => {
      document.body.removeChild(toast)
    }, 300)
  }, 2000)
}

const fetchOrderList = async () => {
  setLoading(true)
  setError(null)
  try {
    const response = await api.getOrderList({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      sortField: 'createTime',
      sortOrder: 'desc'
    })
    if (response.code === 0 || response.code === 200) {
      setOrderList(Array.isArray(response.data.records) ? response.data.records : [])
      total.value = response.data.total || 0
      totalPages.value = response.data.pages || 0

      // Update counts
      const orders = response.data.records || []
      orderStatuses[1].count = orders.filter(o => {
        const status = typeof o.status === 'string' ? parseInt(o.status) : o.status
        return status === 0 || status === 1
      }).length
      orderStatuses[2].count = orders.filter(o => {
        const status = typeof o.status === 'string' ? parseInt(o.status) : o.status
        return status === 2
      }).length
      orderStatuses[3].count = orders.filter(o => {
        const status = typeof o.status === 'string' ? parseInt(o.status) : o.status
        return status === 3
      }).length
    } else {
      setError(response.message || response.msg)
    }
  } catch (err) {
    setError('获取订单列表失败，请稍后重试')
    console.error('获取订单列表失败:', err)
  } finally {
    setLoading(false)
  }
}

const handlePageChange = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    fetchOrderList()
  }
}

onMounted(() => {
  initTheme()
  fetchOrderList()
  fetchUserInfo()
})
</script>

<style scoped>
.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}
</style>
