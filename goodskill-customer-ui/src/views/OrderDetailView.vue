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
            <button @click="handleLogout" class="text-[var(--text-muted)] hover:text-[var(--text-primary)] px-4 py-2 rounded-xl text-sm font-medium transition-all hover:bg-black/5 dark:hover:bg-white/5">退出登录</button>
          </div>
        </div>
      </div>
    </nav>

    <!-- 订单详情 -->
    <div class="pt-24 pb-12">
      <div class="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
        <!-- Breadcrumb -->
        <div class="flex items-center gap-2 text-sm text-[var(--text-muted)] mb-8">
          <router-link to="/" class="hover:text-[var(--text-primary)] transition-colors">首页</router-link>
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
          </svg>
          <router-link to="/orders" class="hover:text-[var(--text-primary)] transition-colors">我的订单</router-link>
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
          </svg>
          <span class="text-[var(--text-secondary)]">订单详情</span>
        </div>

        <!-- Loading State -->
        <div v-if="loading" class="glass-card rounded-3xl p-8">
          <div class="space-y-6">
            <div class="h-8 shimmer rounded w-1/3"></div>
            <div class="h-4 shimmer rounded w-1/4"></div>
            <div class="border-t border-white/5 pt-6">
              <div class="flex gap-6">
                <div class="w-32 h-32 rounded-2xl shimmer flex-shrink-0"></div>
                <div class="flex-1 space-y-3">
                  <div class="h-6 shimmer rounded w-2/3"></div>
                  <div class="h-4 shimmer rounded w-1/2"></div>
                  <div class="h-8 shimmer rounded w-32"></div>
                </div>
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
          <button @click="fetchOrderDetail" class="mt-4 btn-primary px-6 py-3 rounded-xl">重新加载</button>
        </div>

        <!-- Order Detail Content -->
        <div v-else-if="currentOrder" class="space-y-6">
          <!-- Order Status Card -->
          <div class="glass-card rounded-3xl p-8">
            <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-8">
              <div>
                <h1 class="text-2xl font-bold text-[var(--text-primary)] font-display mb-2">订单详情</h1>
                <div class="flex items-center gap-2 text-[var(--text-muted)]">
                  <span>订单号:</span>
                  <span class="font-mono text-[var(--text-primary)]">{{ currentOrder.id }}</span>
                  <button
                    @click="copyOrderId"
                    class="ml-2 p-1.5 rounded-lg hover:bg-black/10 dark:hover:bg-white/10 transition-colors"
                    title="复制订单号"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z" />
                    </svg>
                  </button>
                </div>
              </div>
              <div :class="[
                'flex items-center gap-2 px-5 py-2.5 rounded-full text-sm font-medium',
                getStatusStyle(currentOrder.state)
              ]">
                <span class="w-2 h-2 rounded-full" :class="getStatusDotColor(currentOrder.state)"></span>
                {{ currentOrder.stateDesc || '待支付' }}
              </div>
            </div>

            <!-- Progress Steps -->
            <div class="relative">
              <div class="absolute top-5 left-0 right-0 h-0.5 bg-black/10 dark:bg-white/10"></div>
              <div class="relative flex justify-between">
                <div
                  v-for="(step, index) in orderSteps"
                  :key="index"
                  class="flex flex-col items-center"
                  :class="[
                    step.completed ? 'text-primary' : 'text-[var(--text-muted)]'
                  ]"
                >
                  <div
                    class="w-10 h-10 rounded-full flex items-center justify-center mb-3 transition-all"
                    :class="[
                      step.completed
                        ? 'bg-gradient-to-br from-primary to-secondary shadow-glow'
                        : 'bg-[var(--bg-secondary)] border border-[var(--border-color)]'
                    ]"
                  >
                    <svg v-if="step.completed" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-[var(--text-primary)]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                    <span v-else class="text-sm font-medium">{{ index + 1 }}</span>
                  </div>
                  <span class="text-sm font-medium">{{ step.label }}</span>
                  <span v-if="step.time" class="text-xs text-[var(--text-muted)] mt-1">{{ step.time }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <!-- Left Column: Product Info -->
            <div class="lg:col-span-2 space-y-6">
              <!-- Product Card -->
              <div class="glass-card rounded-3xl p-6">
                <h3 class="text-lg font-semibold text-[var(--text-primary)] mb-6 flex items-center gap-2">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                  </svg>
                  商品信息
                </h3>
                <div class="flex gap-6">
                  <div class="w-32 h-32 rounded-2xl overflow-hidden bg-[var(--bg-secondary)] flex-shrink-0">
                    <img
                      :src="currentOrder.goodsImg || 'https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=200&h=200&fit=crop'"
                      :alt="currentOrder.goodsName"
                      class="w-full h-full object-cover"
                    />
                  </div>
                  <div class="flex-1 min-w-0">
                    <h4 class="text-lg font-semibold text-[var(--text-primary)] mb-2">{{ currentOrder.goodsName || '商品名称' }}</h4>
                    <p class="text-[var(--text-muted)] text-sm mb-4">{{ currentOrder.goodsTitle || '商品描述' }}</p>
                    <div class="flex items-center gap-4">
                      <span class="text-2xl font-bold text-[var(--text-primary)] font-display">¥{{ currentOrder.seckillPrice || 0 }}</span>
                      <span class="text-[var(--text-muted)] line-through">¥{{ (currentOrder.seckillPrice || 0) * 1.2 }}</span>
                      <span class="badge badge-danger">-20%</span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Order Info -->
              <div class="glass-card rounded-3xl p-6">
                <h3 class="text-lg font-semibold text-[var(--text-primary)] mb-6 flex items-center gap-2">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                  订单信息
                </h3>
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
                  <div class="space-y-4">
                    <div class="flex justify-between">
                      <span class="text-[var(--text-muted)]">订单编号</span>
                      <span class="text-[var(--text-primary)] font-mono">{{ currentOrder.id }}</span>
                    </div>
                    <div class="flex justify-between">
                      <span class="text-[var(--text-muted)]">创建时间</span>
                      <span class="text-[var(--text-primary)]">{{ formatTime(currentOrder.createTime) }}</span>
                    </div>
                    <div class="flex justify-between">
                      <span class="text-[var(--text-muted)]">用户手机</span>
                      <span class="text-[var(--text-primary)]">{{ maskPhone(currentOrder.userPhone) }}</span>
                    </div>
                  </div>
                  <div class="space-y-4">
                    <div class="flex justify-between">
                      <span class="text-[var(--text-muted)]">支付方式</span>
                      <span class="text-[var(--text-primary)]">{{ currentOrder.alipayTradeNo ? '支付宝' : '待支付' }}</span>
                    </div>
                    <div v-if="currentOrder.alipayTradeNo" class="flex justify-between">
                      <span class="text-[var(--text-muted)]">支付流水</span>
                      <span class="text-[var(--text-primary)] font-mono text-sm">{{ currentOrder.alipayTradeNo }}</span>
                    </div>
                    <div class="flex justify-between">
                      <span class="text-[var(--text-muted)]">订单状态</span>
                      <span :class="getStatusTextColor(currentOrder.state)">{{ currentOrder.stateDesc || '待支付' }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Right Column: Payment Summary -->
            <div class="space-y-6">
              <!-- Payment Card -->
              <div class="glass-card rounded-3xl p-6">
                <h3 class="text-lg font-semibold text-[var(--text-primary)] mb-6 flex items-center gap-2">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
                  </svg>
                  支付信息
                </h3>
                <div class="space-y-4 mb-6">
                  <div class="flex justify-between text-sm">
                    <span class="text-[var(--text-muted)]">商品金额</span>
                    <span class="text-[var(--text-primary)]">¥{{ (currentOrder.seckillPrice || 0) * 1.2 }}</span>
                  </div>
                  <div class="flex justify-between text-sm">
                    <span class="text-[var(--text-muted)]">优惠金额</span>
                    <span class="text-success">-¥{{ ((currentOrder.seckillPrice || 0) * 0.2).toFixed(2) }}</span>
                  </div>
                  <div class="flex justify-between text-sm">
                    <span class="text-[var(--text-muted)]">运费</span>
                    <span class="text-[var(--text-primary)]">¥0.00</span>
                  </div>
                  <div class="border-t border-[var(--border-color)] pt-4">
                    <div class="flex justify-between items-center">
                      <span class="text-[var(--text-primary)]">实付金额</span>
                      <span class="text-3xl font-bold text-[var(--text-primary)] font-display">¥{{ currentOrder.seckillPrice || 0 }}</span>
                    </div>
                  </div>
                </div>

                <!-- Action Buttons -->
                <div class="space-y-3">
                  <button
                    v-if="currentOrder.state === 1"
                    @click="handlePay"
                    :disabled="isPaying"
                    class="w-full py-4 rounded-xl bg-gradient-to-r from-primary to-secondary text-[var(--text-primary)] font-semibold hover:shadow-glow transition-all flex items-center justify-center gap-2 disabled:opacity-50"
                  >
                    <svg v-if="isPaying" class="animate-spin h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
                    </svg>
                    {{ isPaying ? '支付中...' : '立即支付' }}
                  </button>
                  <button
                    v-if="currentOrder.state === 1"
                    class="w-full py-3 rounded-xl bg-black/5 dark:bg-white/5 text-[var(--text-secondary)] hover:text-[var(--text-primary)] hover:bg-black/10 dark:hover:bg-white/10 transition-all"
                  >
                    取消订单
                  </button>
                  <button
                    v-if="currentOrder.state === 2"
                    class="w-full py-4 rounded-xl bg-success/20 text-success border border-success/30 font-semibold hover:bg-success/30 transition-all flex items-center justify-center gap-2"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                    确认收货
                  </button>
                  <router-link
                    to="/orders"
                    class="w-full py-3 rounded-xl bg-black/5 dark:bg-white/5 text-[var(--text-secondary)] hover:text-[var(--text-primary)] hover:bg-black/10 dark:hover:bg-white/10 transition-all flex items-center justify-center gap-2"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                    </svg>
                    返回订单列表
                  </router-link>
                </div>
              </div>

              <!-- Help Card -->
              <div class="glass-card rounded-3xl p-6">
                <h3 class="text-lg font-semibold text-[var(--text-primary)] mb-4">需要帮助?</h3>
                <div class="space-y-3">
                  <a href="#" class="flex items-center gap-3 text-[var(--text-muted)] hover:text-[var(--text-primary)] transition-colors">
                    <div class="w-10 h-10 rounded-xl bg-black/5 dark:bg-white/5 flex items-center justify-center">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-[var(--text-primary)]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994.54-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                    </div>
                    <span>常见问题</span>
                  </a>
                  <a href="#" class="flex items-center gap-3 text-[var(--text-muted)] hover:text-[var(--text-primary)] transition-colors">
                    <div class="w-10 h-10 rounded-xl bg-black/5 dark:bg-white/5 flex items-center justify-center">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-[var(--text-primary)]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 5.636l-3.536 3.536m0 5.656l3.536 3.536M9.172 9.172L5.636 5.636m3.536 9.192l-3.536 3.536M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-5 0a4 4 0 11-8 0 4 4 0 018 0z" />
                      </svg>
                    </div>
                    <span>联系客服</span>
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-else class="text-center py-20">
          <div class="w-20 h-20 mx-auto mb-6 rounded-full bg-[var(--bg-secondary)] flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 text-[var(--text-muted)]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
          </div>
          <p class="text-[var(--text-muted)] text-lg">订单不存在</p>
          <router-link to="/orders" class="mt-4 inline-block btn-primary px-6 py-3 rounded-xl">返回订单列表</router-link>
        </div>
      </div>
    </div>

    <!-- Toast Notification -->
    <Transition name="fade">
      <div v-if="showToast" class="fixed bottom-8 left-1/2 -translate-x-1/2 z-50">
        <div class="glass-card px-6 py-3 rounded-full flex items-center gap-2">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-success" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
          </svg>
          <span class="text-[var(--text-primary)]">{{ toastMessage }}</span>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { api } from '../api'
import { useUserStore, useOrderStore, useThemeStore } from '../stores'

const router = useRouter()
const route = useRoute()
const { logout } = useUserStore()
const { currentOrder, loading, error, setCurrentOrder, setLoading, setError } = useOrderStore()
const { toggleTheme, initTheme } = useThemeStore()

const orderId = computed(() => route.params.id)
const isPaying = ref(false)
const showToast = ref(false)
const toastMessage = ref('')

const orderSteps = computed(() => {
  const state = currentOrder.value?.state || 1
  return [
    { label: '提交订单', completed: true, time: formatTime(currentOrder.value?.createTime) },
    { label: '支付订单', completed: state >= 2, time: state >= 2 ? formatTime(currentOrder.value?.payTime) : '' },
    { label: '商家发货', completed: state >= 3, time: '' },
    { label: '确认收货', completed: state >= 4, time: '' },
  ]
})

const handleLogout = () => {
  logout()
  router.push('/login')
}

const formatTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const maskPhone = (phone) => {
  if (!phone) return '未知'
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

const getStatusStyle = (state) => {
  const styles = {
    1: 'bg-warning/10 text-warning border border-warning/30',
    2: 'bg-success/10 text-success border border-success/30',
    3: 'bg-danger/10 text-danger border border-danger/30',
  }
  return styles[state] || 'bg-black/5 dark:bg-white/5 text-[var(--text-muted)] border border-[var(--border-color)]'
}

const getStatusDotColor = (state) => {
  const colors = {
    1: 'bg-warning shadow-[0_0_8px_#f59e0b]',
    2: 'bg-success shadow-[0_0_8px_#10b981]',
    3: 'bg-danger shadow-[0_0_8px_#ef4444]',
  }
  return colors[state] || 'bg-[var(--text-muted)]'
}

const getStatusTextColor = (state) => {
  const colors = {
    1: 'text-warning',
    2: 'text-success',
    3: 'text-danger',
  }
  return colors[state] || 'text-[var(--text-muted)]'
}

const copyOrderId = () => {
  if (currentOrder.value?.id) {
    navigator.clipboard.writeText(currentOrder.value.id)
    showToastMessage('订单号已复制')
  }
}

const showToastMessage = (message) => {
  toastMessage.value = message
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 2000)
}

const fetchOrderDetail = async () => {
  setLoading(true)
  setError(null)
  try {
    const response = await api.getOrderDetail(orderId.value)
    console.log('订单详情响应:', response)
    if (response.code === 0) {
      const orderData = {
        ...response.data,
        state: Number(response.data.status) || 1,
        stateDesc: response.data.stateDesc || '待支付',
        seckillPrice: response.data.seckillPrice || 0,
        goodsName: response.data.goodsName || '商品名称',
        goodsTitle: response.data.goodsTitle || '商品描述',
        goodsImg: response.data.goodsImg || 'https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=200&h=200&fit=crop'
      }
      console.log('处理后orderData:', orderData)
      setCurrentOrder(orderData)
    } else {
      setError(response.message)
    }
  } catch (err) {
    setError('获取订单详情失败，请稍后重试')
    console.error('获取订单详情失败:', err)
  } finally {
    setLoading(false)
  }
}

const handlePay = async () => {
  if (isPaying.value) return

  isPaying.value = true
  try {
    const isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)

    console.log('开始创建支付订单，参数:', {
      orderId: currentOrder.value.id,
      amount: (currentOrder.value.seckillPrice || 0).toString(),
      subject: currentOrder.value.goodsName || '商品名称',
      payType: isMobile ? 'h5' : 'pc'
    })

    const response = await api.createPayOrder({
      orderId: currentOrder.value.id,
      amount: (currentOrder.value.seckillPrice || 0).toString(),
      subject: currentOrder.value.goodsName || '商品名称',
      payType: isMobile ? 'h5' : 'pc'
    })

    console.log('创建支付订单响应:', response)

    if (response.code === 0 || response.code === 200) {
      console.log('支付表单:', response.data.form)

      const payWindow = window.open('', '_blank', 'width=800,height=600')
      if (payWindow) {
        payWindow.document.write(response.data.form)
        payWindow.document.close()
      } else {
        console.error('浏览器阻止了弹出窗口，请允许弹出窗口后重试')
        showToastMessage('请允许弹出窗口后重试')
      }

      pollPaymentStatus()
    } else {
      console.error('创建支付订单失败:', response.message || response.msg)
      showToastMessage('创建支付订单失败')
    }
  } catch (error) {
    console.error('支付失败:', error)
    showToastMessage('支付失败，请稍后重试')
  } finally {
    isPaying.value = false
  }
}

let pollInterval = null

const pollPaymentStatus = async () => {
  if (pollInterval) {
    clearInterval(pollInterval)
  }
  
  pollInterval = setInterval(async () => {
    try {
      const response = await api.queryPayStatus(currentOrder.value.id)
      if (response.code === 0) {
        if (response.data.status === 'SUCCESS') {
          clearInterval(pollInterval)
          pollInterval = null
          showToastMessage('支付成功！')
          fetchOrderDetail()
        } else if (response.data.status === 'FAILED' || response.data.status === 'CLOSED') {
          clearInterval(pollInterval)
          pollInterval = null
          showToastMessage('支付失败或已关闭')
        }
      }
    } catch (error) {
      console.error('查询支付状态失败:', error)
    }
  }, 3000)
}

onMounted(() => {
  initTheme()
  fetchOrderDetail()
})

onUnmounted(() => {
  if (pollInterval) {
    clearInterval(pollInterval)
    pollInterval = null
  }
})
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
