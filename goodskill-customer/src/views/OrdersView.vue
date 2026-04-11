<template>
  <div class="min-h-screen bg-gray-100">
    <!-- 导航栏 -->
    <nav class="bg-white shadow-md">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex items-center">
            <router-link to="/" class="text-xl font-bold text-primary">秒杀商城</router-link>
          </div>
          <div class="flex items-center space-x-4">
            <router-link to="/" class="text-gray-700 hover:text-primary px-3 py-2 rounded-md text-sm font-medium">首页</router-link>
            <router-link to="/orders" class="text-gray-700 hover:text-primary px-3 py-2 rounded-md text-sm font-medium">我的订单</router-link>
            <button @click="handleLogout" class="text-gray-700 hover:text-primary px-3 py-2 rounded-md text-sm font-medium">退出登录</button>
          </div>
        </div>
      </div>
    </nav>

    <!-- 订单列表 -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h2 class="text-2xl font-bold text-gray-800 mb-6">我的订单</h2>

      <!-- 订单状态筛选 -->
      <div class="mb-6 flex space-x-4">
        <button
          v-for="status in orderStatuses"
          :key="status.value"
          @click="activeStatus = status.value"
          :class="[
            'px-4 py-2 rounded-md text-sm font-medium transition-colors',
            activeStatus === status.value
              ? 'bg-primary text-white'
              : 'bg-gray-200 text-gray-800 hover:bg-gray-300'
          ]"
        >
          {{ status.label }}
        </button>
      </div>

      <!-- 订单列表 -->
      <div v-if="loading" class="flex justify-center items-center py-12">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
      </div>
      <div v-else-if="error" class="text-center py-12 text-red-500">
        {{ error }}
      </div>
      <div v-else-if="orderList.length === 0" class="text-center py-12 text-gray-600">
        暂无订单
      </div>
      <div v-else class="space-y-6">
        <div
          v-for="order in orderList"
          :key="order.id"
          class="bg-white rounded-lg shadow-md overflow-hidden"
        >
          <div class="p-4 border-b border-gray-200">
            <div class="flex justify-between items-center">
              <span class="text-gray-600">订单号: {{ order.id }}</span>
              <span :class="[
                'text-sm font-medium',
                order.state === 1 ? 'text-warning' :
                order.state === 2 ? 'text-success' :
                order.state === 3 ? 'text-danger' :
                'text-gray-600'
              ]">
                {{ order.stateDesc }}
              </span>
            </div>
            <span class="text-sm text-gray-500">下单时间: {{ formatTime(order.createTime) }}</span>
          </div>
          <div class="p-4">
            <div class="flex items-center">
              <img :src="order.goodsImg" :alt="order.goodsName" class="w-20 h-20 object-cover rounded-md mr-4" />
              <div class="flex-1">
                <h3 class="text-lg font-semibold text-gray-800 mb-1">{{ order.goodsName }}</h3>
                <div class="flex justify-between items-center">
                  <span class="text-danger font-bold">¥{{ order.seckillPrice }}</span>
                  <router-link
                    :to="`/order/${order.id}`"
                    class="text-primary hover:text-blue-600 text-sm font-medium"
                  >
                    查看详情
                  </router-link>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 分页控件 -->
        <div v-if="total > 0" class="flex justify-center mt-8">
          <nav class="flex items-center space-x-2">
            <button
              @click="handlePageChange(1)"
              :disabled="currentPage === 1"
              class="px-3 py-1 rounded border border-gray-300 text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              首页
            </button>
            <button
              @click="handlePageChange(currentPage - 1)"
              :disabled="currentPage === 1"
              class="px-3 py-1 rounded border border-gray-300 text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              上一页
            </button>
            <span class="px-3 py-1 text-gray-700">
              {{ currentPage }} / {{ totalPages }}
            </span>
            <button
              @click="handlePageChange(currentPage + 1)"
              :disabled="currentPage === totalPages"
              class="px-3 py-1 rounded border border-gray-300 text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              下一页
            </button>
            <button
              @click="handlePageChange(totalPages)"
              :disabled="currentPage === totalPages"
              class="px-3 py-1 rounded border border-gray-300 text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              末页
            </button>
          </nav>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../api'
import { useUserStore, useOrderStore } from '../stores'

const router = useRouter()
const { user, logout } = useUserStore()
const { orderList, loading, error, setOrderList, setLoading, setError } = useOrderStore()

const activeStatus = ref('all')
const orderStatuses = [
  { label: '全部', value: 'all' },
  { label: '已下单', value: '1' },
  { label: '已支付', value: '2' },
  { label: '已取消', value: '3' }
]

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = ref(0)

const handleLogout = () => {
  logout()
  router.push('/login')
}

const formatTime = (timestamp) => {
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
      // 后端返回分页结构，从content中获取订单列表
      setOrderList(Array.isArray(response.data.content) ? response.data.content : [])
      // 更新分页信息
      total.value = response.data.totalElements || 0
      totalPages.value = response.data.totalPages || 0
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
  fetchOrderList()
})
</script>

<style scoped>
/* 自定义样式 */
</style>