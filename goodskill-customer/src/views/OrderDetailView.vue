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

    <!-- 订单详情 -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h2 class="text-2xl font-bold text-gray-800 mb-6">订单详情</h2>

      <div v-if="loading" class="flex justify-center items-center py-12">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
      </div>
      <div v-else-if="error" class="text-center py-12 text-red-500">
        {{ error }}
…………      </div>
      <div v-else-if="currentOrder" class="bg-white rounded-lg shadow-md overflow-hidden">
        <!-- 订单状态 -->
        <div class="p-6 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-800 mb-4">订单信息</h3>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <p class="text-gray-600 mb-1">订单号: {{ currentOrder.id }}</p>
              <p class="text-gray-600 mb-1">下单时间: {{ formatTime(currentOrder.createTime) }}</p>
              <p class="text-gray-600 mb-1">用户手机号: {{ currentOrder.userPhone }}</p>
              <p v-if="currentOrder.alipayTradeNo" class="text-gray-600">支付流水号: {{ currentOrder.alipayTradeNo }}</p>
            </div>
            <div class="text-right">
              <p :class="[
                'text-lg font-bold mb-1',
                currentOrder.state === 1 ? 'text-warning' :
                currentOrder.state === 2 ? 'text-success' :
                currentOrder.state === 3 ? 'text-danger' :
                'text-gray-600'
              ]">
                {{ currentOrder.stateDesc || '待支付' }}
              </p>
              <p class="text-danger font-bold text-xl">¥{{ currentOrder.seckillPrice || 0 }}</p>
            </div>
          </div>
        </div>

        <!-- 商品信息 -->
        <div class="p-6 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-800 mb-4">商品信息</h3>
          <div class="flex items-center">
            <img :src="currentOrder.goodsImg || 'https://via.placeholder.com/200'" :alt="currentOrder.goodsName || '商品图片'" class="w-32 h-32 object-cover rounded-md mr-6" />
            <div class="flex-1">
              <h4 class="text-lg font-semibold text-gray-800 mb-2">{{ currentOrder.goodsName || '商品名称' }}</h4>
              <p class="text-gray-600 mb-4">{{ currentOrder.goodsTitle || '商品描述' }}</p>
              <div class="flex justify-between items-center">
                <span class="text-danger font-bold text-xl">¥{{ currentOrder.seckillPrice || 0 }}</span>
                <span class="text-gray-600">数量: 1</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="p-6 flex justify-end space-x-4">
          <router-link
            to="/orders"
            class="bg-gray-200 text-gray-800 py-2 px-6 rounded-md hover:bg-gray-300 transition-colors"
          >
            返回订单列表
          </router-link>
          <button
            v-if="currentOrder.state === 1"
            class="bg-primary text-white py-2 px-6 rounded-md hover:bg-blue-600 transition-colors"
            @click="handlePay"
            :disabled="isPaying"
          >
            <span v-if="isPaying">支付中...</span>
            <span v-else>立即支付</span>
          </button>
          <button
            v-if="currentOrder.state === 1"
            class="bg-gray-200 text-gray-800 py-2 px-6 rounded-md hover:bg-gray-300 transition-colors"
          >
            取消订单
          </button>
        </div>
      </div>
      <div v-else class="text-center py-12 text-gray-600">
        订单不存在
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { api } from '../api'
import { useUserStore, useOrderStore } from '../stores'

const router = useRouter()
const route = useRoute()
const { logout } = useUserStore()
const { currentOrder, loading, error, setCurrentOrder, setLoading, setError } = useOrderStore()

const orderId = computed(() => route.params.id)
const isPaying = ref(false)

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

const fetchOrderDetail = async () => {
  setLoading(true)
  setError(null)
  try {
    const response = await api.getOrderDetail(orderId.value)
    console.log('订单详情响应:', response)
    if (response.code === 0) {
      // 为缺失字段添加默认值
      console.log('原始state值:', response.data.state, '类型:', typeof response.data.state)
      console.log('原始status值:', response.data.status, '类型:', typeof response.data.status)
      const orderData = {
        ...response.data,
        state: Number(response.data.status) || 1, // 默认为待支付状态，确保是Number类型，使用status字段
        stateDesc: response.data.stateDesc || '待支付',
        seckillPrice: response.data.seckillPrice || 0,
        goodsName: response.data.goodsName || '商品名称',
        goodsTitle: response.data.goodsTitle || '商品描述',
        goodsImg: response.data.goodsImg || 'https://via.placeholder.com/200'
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
        alert('浏览器阻止了弹出窗口，请允许弹出窗口后重试')
      }

      pollPaymentStatus()
    } else {
      console.error('创建支付订单失败:', response.message || response.msg)
      alert('创建支付订单失败，请稍后重试')
    }
  } catch (error) {
    console.error('支付失败:', error)
    alert('支付失败，请稍后重试')
  } finally {
    isPaying.value = false
  }
}

const pollPaymentStatus = async () => {
  const interval = setInterval(async () => {
    try {
      const response = await api.queryPayStatus(currentOrder.value.id)
      if (response.code === 0) {
        if (response.data.status === 'SUCCESS') {
          clearInterval(interval)
          fetchOrderDetail()
        } else if (response.data.status === 'FAILED' || response.data.status === 'CLOSED') {
          clearInterval(interval)
        }
      }
    } catch (error) {
      console.error('查询支付状态失败:', error)
    }
  }, 3000)
}

onMounted(() => {
  fetchOrderDetail()
})
</script>

<style scoped>
</style>
