<template>
  <div class="min-h-screen bg-[var(--bg-primary)] transition-colors duration-300">
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
            <router-link to="/orders" class="text-[var(--text-muted)] hover:text-[var(--text-primary)] px-4 py-2 rounded-xl text-sm font-medium transition-all hover:bg-black/5 dark:hover:bg-white/5">我的订单</router-link>
            <router-link to="/profile" class="text-[var(--text-primary)] px-4 py-2 rounded-xl text-sm font-medium bg-black/5 dark:bg-white/10">个人中心</router-link>
            <!-- 主题切换 -->
            <button @click="toggleTheme" class="theme-toggle" title="切换主题">
              <svg class="sun-icon w-5 h-5 text-[var(--text-primary)]" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z" />
              </svg>
              <svg class="moon-icon w-5 h-5 text-[var(--text-primary)]" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z" />
              </svg>
            </button>
            <button @click="handleLogout" class="text-[var(--text-muted)] hover:text-[var(--text-primary)] px-4 py-2 rounded-xl text-sm font-medium transition-all hover:bg-black/5 dark:hover:bg-white/5">退出登录</button>
          </div>
        </div>
      </div>
    </nav>

    <!-- 个人中心内容 -->
    <div class="pt-24 pb-12">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <!-- Header -->
        <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-8">
          <div>
            <h1 class="text-3xl font-bold text-[var(--text-primary)] font-display mb-2">个人中心</h1>
            <p class="text-[var(--text-muted)]">管理您的个人信息和订单</p>
          </div>
          <router-link to="/" class="btn-secondary px-6 py-3 rounded-xl flex items-center gap-2">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
            返回首页
          </router-link>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <!-- 左侧：用户信息卡片 -->
          <div class="lg:col-span-2 space-y-6">
            <!-- 用户信息卡片 -->
            <div class="glass-card rounded-3xl p-8">
              <div class="flex items-center justify-between mb-6">
                <h2 class="text-xl font-semibold text-[var(--text-primary)]">基本信息</h2>
                <button
                  v-if="!isEditing"
                  @click="startEditing"
                  class="px-4 py-2 rounded-xl bg-gradient-to-r from-primary to-secondary text-[var(--text-primary)] text-sm font-medium hover:shadow-glow transition-all flex items-center gap-2"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                  </svg>
                  编辑资料
                </button>
                <div v-else class="flex gap-2">
                  <button
                    @click="cancelEditing"
                    class="px-4 py-2 rounded-xl bg-black/5 dark:bg-white/5 text-[var(--text-secondary)] text-sm font-medium hover:bg-black/10 dark:hover:bg-white/10 transition-all"
                  >
                    取消
                  </button>
                  <button
                    @click="saveUserInfo"
                    :disabled="saving"
                    class="px-4 py-2 rounded-xl bg-gradient-to-r from-primary to-secondary text-[var(--text-primary)] text-sm font-medium hover:shadow-glow transition-all flex items-center gap-2 disabled:opacity-50"
                  >
                    <svg v-if="saving" class="animate-spin h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    <span v-else>保存</span>
                  </button>
                </div>
              </div>

              <!-- 头像上传区域 -->
              <div class="flex flex-col sm:flex-row items-center gap-6 mb-8">
                <div class="relative">
                  <div
                    class="w-24 h-24 rounded-2xl overflow-hidden bg-gradient-to-br from-primary/20 to-secondary/20 flex items-center justify-center border-2 border-[var(--border-color)] group cursor-pointer"
                    @click="triggerAvatarUpload"
                  >
                    <img
                      v-if="userInfo.avatar && userInfo.avatar.trim() !== ''"
                      :src="userInfo.avatar"
                      alt="头像"
                      class="w-full h-full object-cover"
                      @error="userInfo.avatar = ''"
                    />
                    <svg
                      v-else
                      xmlns="http://www.w3.org/2000/svg"
                      class="h-12 w-12 text-[var(--text-muted)]"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                    >
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                    </svg>
                    <div class="absolute inset-0 bg-black/40 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
                      </svg>
                    </div>
                  </div>
                  <input
                    ref="avatarInput"
                    type="file"
                    accept="image/jpeg,image/png"
                    class="hidden"
                    @change="handleAvatarChange"
                  />
                  <div v-if="uploadingAvatar" class="absolute inset-0 flex items-center justify-center bg-black/50 rounded-2xl">
                    <svg class="animate-spin h-8 w-8 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                  </div>
                </div>
                <div class="text-center sm:text-left">
                  <h3 class="text-lg font-semibold text-[var(--text-primary)]">{{ userInfo.username || '用户' }}</h3>
                  <p class="text-sm text-[var(--text-muted)]">点击头像更换照片</p>
                  <p class="text-xs text-[var(--text-muted)] mt-1">支持 JPG、PNG 格式，最大 2MB</p>
                </div>
              </div>

              <!-- 用户信息表单 -->
              <div class="space-y-6">
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
                  <!-- 用户名 - 只读 -->
                  <div>
                    <label class="block text-sm font-medium text-[var(--text-secondary)] mb-2">用户名</label>
                    <div
                      class="w-full px-4 py-3 rounded-xl bg-black/5 dark:bg-white/5 text-[var(--text-muted)] flex items-center gap-2 opacity-60"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                      </svg>
                      {{ userInfo.username || '未设置' }}
                      <span class="text-xs text-[var(--text-muted)] ml-auto">暂不支持修改</span>
                    </div>
                  </div>

                  <!-- 手机号 - 只读 -->
                  <div>
                    <label class="block text-sm font-medium text-[var(--text-secondary)] mb-2">手机号</label>
                    <div
                      class="w-full px-4 py-3 rounded-xl bg-black/5 dark:bg-white/5 text-[var(--text-muted)] flex items-center gap-2 opacity-60"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                      </svg>
                      {{ maskedMobile }}
                      <span class="text-xs text-[var(--text-muted)] ml-auto">暂不支持修改</span>
                    </div>
                  </div>

                  <!-- 邮箱 -->
                  <div class="sm:col-span-2">
                    <label class="block text-sm font-medium text-[var(--text-secondary)] mb-2">邮箱地址</label>
                    <input
                      v-if="isEditing"
                      v-model="editForm.emailAddr"
                      type="email"
                      class="w-full px-4 py-3 rounded-xl bg-[var(--bg-secondary)] border border-[var(--border-color)] text-[var(--text-primary)] placeholder-[var(--text-muted)] focus:outline-none focus:border-primary/50 focus:ring-2 focus:ring-primary/20 transition-all"
                      placeholder="请输入邮箱地址"
                    />
                    <div
                      v-else
                      class="w-full px-4 py-3 rounded-xl bg-black/5 dark:bg-white/5 text-[var(--text-primary)]"
                    >
                      {{ userInfo.emailAddr || '未设置' }}
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 快捷入口 -->
            <div class="glass-card rounded-3xl p-8">
              <h2 class="text-xl font-semibold text-[var(--text-primary)] mb-6">快捷入口</h2>
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <router-link
                  to="/orders"
                  class="flex items-center gap-4 p-4 rounded-2xl bg-black/5 dark:bg-white/5 hover:bg-black/10 dark:hover:bg-white/10 transition-all group"
                >
                  <div class="w-12 h-12 rounded-xl bg-gradient-to-br from-primary/20 to-secondary/20 flex items-center justify-center group-hover:scale-110 transition-transform">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                    </svg>
                  </div>
                  <div>
                    <h3 class="font-medium text-[var(--text-primary)]">我的订单</h3>
                    <p class="text-sm text-[var(--text-muted)]">查看和管理您的订单</p>
                  </div>
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-[var(--text-muted)] ml-auto group-hover:translate-x-1 transition-transform" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                  </svg>
                </router-link>

                <router-link
                  to="/"
                  class="flex items-center gap-4 p-4 rounded-2xl bg-black/5 dark:bg-white/5 hover:bg-black/10 dark:hover:bg-white/10 transition-all group"
                >
                  <div class="w-12 h-12 rounded-xl bg-gradient-to-br from-success/20 to-primary/20 flex items-center justify-center group-hover:scale-110 transition-transform">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-success" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                    </svg>
                  </div>
                  <div>
                    <h3 class="font-medium text-[var(--text-primary)]">继续购物</h3>
                    <p class="text-sm text-[var(--text-muted)]">浏览更多商品</p>
                  </div>
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-[var(--text-muted)] ml-auto group-hover:translate-x-1 transition-transform" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                  </svg>
                </router-link>
              </div>
            </div>
          </div>

          <!-- 右侧：订单统计 -->
          <div class="lg:col-span-1">
            <div class="glass-card rounded-3xl p-8 sticky top-24">
              <h2 class="text-xl font-semibold text-[var(--text-primary)] mb-6">订单统计</h2>
              <div class="space-y-4">
                <div class="flex items-center justify-between p-4 rounded-2xl bg-black/5 dark:bg-white/5">
                  <div class="flex items-center gap-3">
                    <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-primary/20 to-secondary/20 flex items-center justify-center">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                      </svg>
                    </div>
                    <span class="text-[var(--text-secondary)]">全部订单</span>
                  </div>
                  <span class="text-2xl font-bold text-[var(--text-primary)] font-display">{{ orderStats.all }}</span>
                </div>

                <div class="flex items-center justify-between p-4 rounded-2xl bg-black/5 dark:bg-white/5">
                  <div class="flex items-center gap-3">
                    <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-warning/20 to-primary/20 flex items-center justify-center">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-warning" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                    </div>
                    <span class="text-[var(--text-secondary)]">待支付</span>
                  </div>
                  <span class="text-2xl font-bold text-[var(--text-primary)] font-display">{{ orderStats.pending }}</span>
                </div>

                <div class="flex items-center justify-between p-4 rounded-2xl bg-black/5 dark:bg-white/5">
                  <div class="flex items-center gap-3">
                    <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-success/20 to-primary/20 flex items-center justify-center">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-success" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                    </div>
                    <span class="text-[var(--text-secondary)]">已支付</span>
                  </div>
                  <span class="text-2xl font-bold text-[var(--text-primary)] font-display">{{ orderStats.paid }}</span>
                </div>

                <div class="flex items-center justify-between p-4 rounded-2xl bg-black/5 dark:bg-white/5">
                  <div class="flex items-center gap-3">
                    <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-danger/20 to-warning/20 flex items-center justify-center">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-danger" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                    </div>
                    <span class="text-[var(--text-secondary)]">已取消</span>
                  </div>
                  <span class="text-2xl font-bold text-[var(--text-primary)] font-display">{{ orderStats.cancelled }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Toast提示 -->
    <transition name="toast">
      <div
        v-if="toast.visible"
        class="fixed bottom-8 left-1/2 -translate-x-1/2 z-50 glass-card px-6 py-3 rounded-full flex items-center gap-2"
        :class="toast.type === 'success' ? 'border-success/30' : 'border-danger/30'"
      >
        <svg
          v-if="toast.type === 'success'"
          xmlns="http://www.w3.org/2000/svg"
          class="h-5 w-5 text-success"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
        </svg>
        <svg
          v-else
          xmlns="http://www.w3.org/2000/svg"
          class="h-5 w-5 text-danger"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
        <span class="text-[var(--text-primary)]">{{ toast.message }}</span>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../api'
import { useUserStore, useThemeStore } from '../stores'

const router = useRouter()
const { user, logout, updateUserInfo, updateAvatar } = useUserStore()
const { toggleTheme, initTheme } = useThemeStore()

// 用户信息
const userInfo = reactive({
  userId: null,
  username: '',
  mobile: '',
  avatar: '',
  emailAddr: ''
})

// 编辑状态
const isEditing = ref(false)
const saving = ref(false)
const uploadingAvatar = ref(false)
const avatarInput = ref(null)

// 编辑表单
const editForm = reactive({
  emailAddr: ''
})

// 订单统计
const orderStats = reactive({
  all: 0,
  pending: 0,
  paid: 0,
  cancelled: 0
})

// 手机号脱敏
const maskedMobile = computed(() => {
  if (!userInfo.mobile || userInfo.mobile.length !== 11) {
    return userInfo.mobile || '未绑定'
  }
  return userInfo.mobile.slice(0, 3) + '****' + userInfo.mobile.slice(7)
})

// Toast提示
const toast = reactive({
  visible: false,
  type: 'success',
  message: ''
})

// 显示Toast
const showToast = (message, type = 'success') => {
  toast.visible = true
  toast.type = type
  toast.message = message
  setTimeout(() => {
    toast.visible = false
  }, 2000)
}

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const response = await api.getCustomerUserInfo()
    if (response.code === 0 || response.code === 200) {
      const data = response.data
      userInfo.userId = data.userId
      userInfo.username = data.username
      userInfo.mobile = data.mobile
      userInfo.avatar = data.avatar
      userInfo.emailAddr = data.emailAddr
    }
  } catch (error) {
    showToast('获取用户信息失败', 'error')
  }
}

// 获取订单统计
const fetchOrderStats = async () => {
  try {
    const response = await api.getOrderList({ pageNum: 1, pageSize: 100 })
    if (response.code === 0 || response.code === 200) {
      const orders = response.data.records || []
      orderStats.all = orders.length
      orderStats.pending = orders.filter(o => o.status === 0 || o.status === 1).length
      orderStats.paid = orders.filter(o => o.status === 2).length
      orderStats.cancelled = orders.filter(o => o.status === 3).length
    }
  } catch (error) {
    console.error('获取订单统计失败:', error)
  }
}

// 开始编辑
const startEditing = () => {
  editForm.emailAddr = userInfo.emailAddr
  isEditing.value = true
}

// 取消编辑
const cancelEditing = () => {
  isEditing.value = false
}

// 保存用户信息
const saveUserInfo = async () => {
  if (!editForm.emailAddr.trim()) {
    showToast('邮箱不能为空', 'error')
    return
  }

  saving.value = true
  try {
    const response = await api.updateCustomerUserInfo({
      username: userInfo.username,
      emailAddr: editForm.emailAddr
    })
    if (response.code === 0 || response.code === 200) {
      userInfo.emailAddr = response.data.emailAddr
      updateUserInfo(response.data)
      showToast('保存成功')
      isEditing.value = false
    } else {
      // 显示服务器返回的错误信息
      const errorMsg = response.msg || response.message || '保存失败'
      showToast(errorMsg, 'error')
    }
  } catch (error) {
    // 显示服务器返回的错误信息
    let errorMsg = '保存失败'
    if (error.response?.data?.msg) {
      errorMsg = error.response.data.msg
    } else if (error.response?.data?.message) {
      errorMsg = error.response.data.message
    } else if (error.msg) {
      errorMsg = error.msg
    } else if (error.message) {
      errorMsg = error.message
    }
    showToast(errorMsg, 'error')
  } finally {
    saving.value = false
  }
}

// 触发头像上传
const triggerAvatarUpload = () => {
  avatarInput.value.click()
}

// 处理头像变更
const handleAvatarChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 验证文件类型
  if (!['image/jpeg', 'image/png'].includes(file.type)) {
    showToast('只支持 JPG、PNG 格式的图片', 'error')
    return
  }

  // 验证文件大小 (2MB)
  if (file.size > 2 * 1024 * 1024) {
    showToast('图片大小不能超过 2MB', 'error')
    return
  }

  uploadingAvatar.value = true
  try {
    // 上传头像到common服务
    const uploadResponse = await api.uploadAvatar(file)
    if (uploadResponse.code === 0 || uploadResponse.code === 200) {
      const avatarUrl = uploadResponse.data
      // 更新用户头像
      const updateResponse = await api.updateCustomerUserAvatar(avatarUrl)
      if (updateResponse.code === 0 || updateResponse.code === 200) {
        userInfo.avatar = avatarUrl
        updateAvatar(avatarUrl)
        showToast('头像上传成功')
      } else {
        showToast(updateResponse.message || '头像更新失败', 'error')
      }
    } else {
      showToast(uploadResponse.message || '头像上传失败', 'error')
    }
  } catch (error) {
    showToast('头像上传失败', 'error')
  } finally {
    uploadingAvatar.value = false
    // 清空input
    event.target.value = ''
  }
}

// 退出登录
const handleLogout = () => {
  logout()
  router.push('/login')
}

onMounted(() => {
  initTheme()
  fetchUserInfo()
  fetchOrderStats()
})
</script>

<style scoped>
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translate(-50%, 20px);
}
</style>