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

    <!-- 商品详情 -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div v-if="loading" class="flex justify-center items-center py-12">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
      </div>
      <div v-else-if="error" class="text-center py-12 text-red-500">
        {{ error }}
      </div>
      <div v-else-if="goods" class="bg-white rounded-lg shadow-md overflow-hidden">
        <div class="grid grid-cols-1 md:grid-cols-2">
          <!-- 商品图片 -->
          <div class="p-6">
            <img :src="goods.goodsImg" :alt="goods.goodsName" class="w-full h-96 object-cover rounded-md" />
          </div>
          <!-- 商品信息 -->
          <div class="p-6">
            <h2 class="text-2xl font-bold text-gray-800 mb-2">{{ goods.goodsName }}</h2>
            <p class="text-gray-600 mb-4">{{ goods.goodsTitle }}</p>
            <div class="flex items-center mb-4">
              <span class="text-danger font-bold text-3xl">¥{{ goods.seckillPrice }}</span>
              <span class="text-gray-400 text-sm line-through ml-4">¥{{ goods.goodsPrice }}</span>
              <span class="ml-4 bg-danger text-white text-xs px-2 py-1 rounded">
                {{ Math.round((1 - goods.seckillPrice / goods.goodsPrice) * 100) }}% OFF
              </span>
            </div>
            <div class="mb-4">
              <span class="text-gray-600">库存: </span>
              <span class="font-medium">{{ goods.stockCount }}</span>
            </div>
            <div class="mb-6">
              <span class="text-gray-600">剩余时间: </span>
              <span class="font-medium text-danger">{{ formatTimeLeft(goods.endTime - Date.now()) }}</span>
            </div>
            <div class="mb-6">
              <h3 class="text-lg font-semibold text-gray-800 mb-2">商品描述</h3>
              <p class="text-gray-600">{{ goods.goodsDesc }}</p>
            </div>
            <button
              @click="handleSeckill"
              :disabled="seckillLoading || goods.stockCount <= 0 || goods.endTime < Date.now()"
              class="w-full bg-primary text-white py-3 px-4 rounded-md hover:bg-blue-600 transition-colors disabled:bg-gray-300 disabled:cursor-not-allowed"
            >
              {{ seckillLoading ? '抢购中...' : (goods.stockCount <= 0 ? '已售罄' : (goods.endTime < Date.now() ? '已结束' : '立即抢购')) }}
            </button>
          </div>
        </div>
      </div>
      <div v-else class="text-center py-12 text-gray-600">
        商品不存在
      </div>
    </div>

    <!-- 秒杀结果弹窗 -->
    <div v-if="showResultModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg shadow-lg p-6 w-full max-w-md">
        <h3 class="text-xl font-bold text-center mb-4">{{ seckillResult.success ? '秒杀成功' : '秒杀失败' }}</h3>
        <p class="text-center mb-6">{{ seckillResult.message }}</p>
        <div v-if="seckillResult.success" class="mb-6">
          <p class="text-gray-600">订单号: {{ seckillResult.data?.orderId }}</p>
          <p class="text-gray-600">商品: {{ goods?.goodsName }}</p>
          <p class="text-gray-600">价格: ¥{{ goods?.seckillPrice }}</p>
        </div>
        <div class="flex space-x-4">
          <button
            @click="showResultModal = false"
            class="flex-1 bg-gray-200 text-gray-800 py-2 px-4 rounded-md hover:bg-gray-300 transition-colors"
          >
            关闭
          </button>
          <button
            v-if="seckillResult.success"
            @click="goToOrderDetail(seckillResult.data?.orderId)"
            class="flex-1 bg-primary text-white py-2 px-4 rounded-md hover:bg-blue-600 transition-colors"
          >
            立即支付
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { api } from '../api'
import { useUserStore, useGoodsStore } from '../stores'

const router = useRouter()
const route = useRoute()
const { user, logout, checkLoginStatus } = useUserStore()
const { currentGoods, loading, error, setCurrentGoods, setLoading, setError } = useGoodsStore()

const goodsId = computed(() => route.params.id)
const goods = computed(() => currentGoods.value)

const seckillLoading = ref(false)
const showResultModal = ref(false)
const seckillResult = ref({})

const handleLogout = () => {
  logout()
  router.push('/login')
}

const formatTimeLeft = (milliseconds) => {
  if (milliseconds <= 0) return '已结束'

  const seconds = Math.floor((milliseconds / 1000) % 60)
  const minutes = Math.floor((milliseconds / (1000 * 60)) % 60)
  const hours = Math.floor((milliseconds / (1000 * 60 * 60)) % 24)

  return `${hours}h ${minutes}m ${seconds}s`
}

const fetchGoodsDetail = async () => {
  setLoading(true)
  setError(null)
  try {
    const response = await api.getGoodsDetail(goodsId.value)
    if (response && (response.seckillId || response.goodsId)) {
      setCurrentGoods(response)
    } else if (response.code === 0) {
      let goodsImg = `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=e-commerce%20product%20placeholder%20image&image_size=landscape_4_3`
      if (response.data.photoUrl) {
        goodsImg = response.data.photoUrl
      }
      const transformedGoods = {
        id: response.data.seckillId,
        goodsName: response.data.name,
        goodsTitle: response.data.name,
        seckillPrice: response.data.price,
        goodsPrice: response.data.price * 1.2,
        stockCount: response.data.number,
        endTime: new Date(response.data.endTime).getTime(),
        goodsDesc: response.data.name + ' 是一款非常好的商品，欢迎购买！',
        goodsImg: goodsImg
      }
      setCurrentGoods(transformedGoods)
    } else {
      setError(response.message)
    }
  } catch (err) {
    setError('获取商品详情失败，请稍后重试')
    console.error('获取商品详情失败:', err)
  } finally {
    setLoading(false)
  }
}

const goToOrderDetail = (orderId) => {
  showResultModal.value = false
  if (orderId) {
    router.push(`/order/${orderId}`)
  } else {
    router.push('/orders')
  }
}

const handleSeckill = async () => {
  if (goods.value.stockCount <= 0 || goods.value.endTime < Date.now()) return

  // 检查登录状态和手机号
  if (!user.isLoggedIn || !user.phone) {
    seckillResult.value = {
      success: false,
      message: '请先登录并完善手机号信息'
    }
    showResultModal.value = true
    setTimeout(() => {
      showResultModal.value = false
      router.push('/login')
    }, 1500)
    return
  }

  seckillLoading.value = true
  try {
    const exposeResponse = await api.exportSeckillUrl(goodsId.value)
    if (exposeResponse.code === 0 && exposeResponse.data.exposed) {
      const executeResponse = await api.executeSeckill({
        seckillId: goodsId.value,
        userPhone: user.phone,
        userId: user.id,
        md5: exposeResponse.data.md5
      })
      if (executeResponse.code === 0) {
        seckillResult.value = {
          success: true,
          message: '秒杀成功！请在30分钟内完成支付。',
          data: executeResponse.data
        }
      } else {
        seckillResult.value = {
          success: false,
          message: executeResponse.msg || '秒杀失败'
        }
      }
      showResultModal.value = true
      fetchGoodsDetail()
    } else {
      seckillResult.value = {
        success: false,
        message: '秒杀活动未开始或已结束'
      }
      showResultModal.value = true
    }
  } catch (err) {
    seckillResult.value = {
      success: false,
      message: '秒杀失败，请稍后重试'
    }
    showResultModal.value = true
    console.error('秒杀失败:', err)
  } finally {
    seckillLoading.value = false
  }
}

onMounted(() => {
  checkLoginStatus()
  fetchGoodsDetail()
})
</script>

<style scoped>
/* 自定义样式 */
</style>
