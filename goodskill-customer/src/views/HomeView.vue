<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部导航栏 -->
    <header class="bg-white shadow-sm sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <!-- 左侧Logo和导航 -->
          <div class="flex items-center">
            <router-link to="/" class="flex items-center space-x-2">
              <div class="w-10 h-10 bg-primary rounded-lg flex items-center justify-center">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                </svg>
              </div>
              <span class="text-xl font-bold text-gray-800">秒杀商城</span>
            </router-link>
            <nav class="hidden md:ml-10 md:flex space-x-8">
              <a href="#" class="text-gray-600 hover:text-primary px-3 py-2 rounded-md text-sm font-medium transition-colors">首页</a>
              <a href="#" class="text-gray-600 hover:text-primary px-3 py-2 rounded-md text-sm font-medium transition-colors">商品分类</a>
              <a href="#" class="text-gray-600 hover:text-primary px-3 py-2 rounded-md text-sm font-medium transition-colors">限时秒杀</a>
              <a href="#" class="text-gray-600 hover:text-primary px-3 py-2 rounded-md text-sm font-medium transition-colors">热门活动</a>
            </nav>
          </div>
          <!-- 右侧搜索和用户中心 -->
          <div class="flex items-center space-x-4">
            <!-- 搜索框 -->
            <div class="hidden md:block">
              <div class="relative">
                <input
                  type="text"
                  placeholder="搜索商品..."
                  class="w-64 pl-10 pr-4 py-2 border border-gray-300 rounded-full focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                />
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                  </svg>
                </div>
              </div>
            </div>
            <!-- 购物车 -->
            <button class="p-2 rounded-full text-gray-600 hover:bg-gray-100 transition-colors relative">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
              </svg>
              <span class="absolute top-0 right-0 bg-primary text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">0</span>
            </button>
            <!-- 用户中心 -->
            <div class="relative">
              <button @click="toggleUserMenu" class="flex items-center space-x-2 text-gray-600 hover:text-primary transition-colors">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
                <span class="hidden md:inline">我的账户</span>
              </button>
              <div v-if="showUserMenu" class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-50">
                <a href="#" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">个人中心</a>
                <router-link to="/orders" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">我的订单</router-link>
                <a href="#" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">收货地址</a>
                <hr class="my-1 border-gray-200" />
                <button @click="handleLogout" class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">退出登录</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </header>

    <!-- 轮播图 -->
    <div class="relative bg-gradient-to-r from-blue-600 to-indigo-700 text-white py-16 md:py-24">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex flex-col md:flex-row items-center">
          <div class="md:w-1/2 mb-8 md:mb-0">
            <h1 class="text-3xl md:text-4xl font-bold mb-4">限时秒杀，抢购不停</h1>
            <p class="text-lg mb-6">每日精选商品，限时特价，抢完即止！</p>
            <button class="bg-white text-primary font-medium px-6 py-3 rounded-lg hover:bg-gray-100 transition-colors">
              立即抢购
            </button>
          </div>
          <div class="md:w-1/2">
            <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=e-commerce%20seckill%20banner%20with%20smartphones%20and%20discount%20tags&image_size=landscape_16_9" alt="限时秒杀" class="rounded-lg shadow-xl w-full h-64 object-cover" />
          </div>
        </div>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div class="flex justify-between items-center mb-8">
        <h2 class="text-2xl font-bold text-gray-800">秒杀商品</h2>
        <div class="flex space-x-2">
          <button class="px-4 py-2 bg-primary text-white rounded-lg hover:bg-blue-600 transition-colors">
            全部商品
          </button>
          <button class="px-4 py-2 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">
            热门推荐
          </button>
          <button class="px-4 py-2 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">
            最新上架
          </button>
        </div>
      </div>

      <!-- 商品卡片列表 -->
      <div v-if="loading" class="flex justify-center items-center py-12">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
      </div>
      <div v-else-if="error" class="text-center py-12 text-red-500">
        {{ error }}
      </div>
      <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        <div
          v-for="goods in goodsList"
          :key="goods.id"
          class="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1"
        >
          <div class="relative">
            <img :src="goods.goodsImg" :alt="goods.goodsName" class="w-full h-64 object-cover" />
            <div class="absolute top-4 left-4 bg-danger text-white text-sm font-bold px-3 py-1 rounded-full">
              {{ Math.round((1 - goods.seckillPrice / goods.goodsPrice) * 100) }}% OFF
            </div>
            <div class="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black/70 to-transparent text-white p-4">
              <div class="flex justify-between items-center">
                <span class="text-sm">剩余时间</span>
                <span class="text-sm font-medium">{{ formatTimeLeft(goods.endTime - Date.now()) }}</span>
              </div>
            </div>
          </div>
          <div class="p-4">
            <h3 class="text-lg font-semibold text-gray-800 mb-2 line-clamp-2">{{ goods.goodsName }}</h3>
            <p class="text-sm text-gray-600 mb-3 line-clamp-2">{{ goods.goodsTitle }}</p>
            <div class="flex items-center mb-4">
              <span class="text-danger font-bold text-xl">¥{{ goods.seckillPrice }}</span>
              <span class="text-gray-400 text-sm line-through ml-2">¥{{ goods.goodsPrice }}</span>
              <span class="ml-auto text-sm text-gray-600">库存: {{ goods.stockCount }}</span>
            </div>
            <router-link
              :to="`/goods/${goods.id}`"
              class="block w-full bg-primary text-white text-center py-2 px-4 rounded-lg hover:bg-blue-600 transition-colors"
            >
              详情
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部信息 -->
    <footer class="bg-gray-800 text-white py-12">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
          <div>
            <h3 class="text-lg font-semibold mb-4">关于我们</h3>
            <ul class="space-y-2">
              <li><a href="#" class="text-gray-300 hover:text-white transition-colors">公司简介</a></li>
              <li><a href="#" class="text-gray-300 hover:text-white transition-colors">联系我们</a></li>
              <li><a href="#" class="text-gray-300 hover:text-white transition-colors">加入我们</a></li>
              <li><a href="#" class="text-gray-300 hover:text-white transition-colors">商家入驻</a></li>
            </ul>
          </div>
          <div>
            <h3 class="text-lg font-semibold mb-4">帮助中心</h3>
            <ul class="space-y-2">
              <li><a href="#" class="text-gray-300 hover:text-white transition-colors">购物指南</a></li>
              <li><a href="#" class="text-gray-300 hover:text-white transition-colors">支付方式</a></li>
              <li><a href="#" class="text-gray-300 hover:text-white transition-colors">配送方式</a></li>
              <li><a href="#" class="text-gray-300 hover:text-white transition-colors">售后服务</a></li>
            </ul>
          </div>
          <div>
            <h3 class="text-lg font-semibold mb-4">商家服务</h3>
            <ul class="space-y-2">
              <li><a href="#" class="text-gray-300 hover:text-white transition-colors">商家入驻</a></li>
              <li><a href="#" class="text-gray-300 hover:text-white transition-colors">商家中心</a></li>
              <li><a href="#" class="text-gray-300 hover:text-white transition-colors">营销中心</a></li>
              <li><a href="#" class="text-gray-300 hover:text-white transition-colors">物流服务</a></li>
            </ul>
          </div>
          <div>
            <h3 class="text-lg font-semibold mb-4">关注我们</h3>
            <div class="flex space-x-4 mb-4">
              <a href="#" class="w-10 h-10 bg-gray-700 rounded-full flex items-center justify-center hover:bg-primary transition-colors">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M24 4.557c-.883.392-1.832.656-2.828.775 1.017-.609 1.798-1.574 2.165-2.724-.951.564-2.005.974-3.127 1.195-.897-.957-2.178-1.555-3.594-1.555-3.179 0-5.515 2.966-4.797 6.045-4.091-.205-7.719-2.165-10.148-5.144-1.29 2.213-.669 5.108 1.523 6.574-.806-.026-1.566-.247-2.229-.616-.054 2.281 1.581 4.415 3.949 4.89-.693.188-1.452.232-2.224.084.626 1.956 2.444 3.379 4.6 3.419-2.07 1.623-4.678 2.348-7.29 2.04 2.179 1.397 4.768 2.212 7.548 2.212 9.142 0 14.307-7.721 13.995-14.646.962-.695 1.797-1.562 2.457-2.549z"/>
                </svg>
              </a>
              <a href="#" class="w-10 h-10 bg-gray-700 rounded-full flex items-center justify-center hover:bg-primary transition-colors">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M9 8h-3v4h3v12h5v-12h3.642l.358-4h-4v-1.667c0-.955.192-1.333 1.115-1.333h2.885v-5h-3.808c-3.596 0-5.192 1.583-5.192 4.615v3.385z"/>
                </svg>
              </a>
              <a href="#" class="w-10 h-10 bg-gray-700 rounded-full flex items-center justify-center hover:bg-primary transition-colors">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M12 2.163c3.204 0 3.584.012 4.85.07 3.252.148 4.771 1.691 4.919 4.919.058 1.265.069 1.645.069 4.849 0 3.205-.012 3.584-.069 4.849-.149 3.225-1.664 4.771-4.919 4.919-1.266.058-1.644.07-4.85.07-3.204 0-3.584-.012-4.849-.07-3.26-.149-4.771-1.699-4.919-4.92-.058-1.265-.07-1.644-.07-4.849 0-3.204.013-3.583.07-4.849.149-3.227 1.664-4.771 4.919-4.919 1.266-.057 1.645-.069 4.849-.069zm0-2.163c-3.259 0-3.667.014-4.947.072-4.358.2-6.78 2.618-6.98 6.98-.059 1.281-.073 1.689-.073 4.948 0 3.259.014 3.668.072 4.948.2 4.358 2.618 6.78 6.98 6.98 1.281.058 1.689.072 4.948.072 3.259 0 3.668-.014 4.948-.072 4.354-.2 6.782-2.618 6.979-6.98.059-1.28.073-1.689.073-4.948 0-3.259-.014-3.667-.072-4.947-.196-4.354-2.617-6.78-6.979-6.98-1.281-.059-1.69-.073-4.949-.073zm0 5.838c-3.403 0-6.162 2.759-6.162 6.162s2.759 6.163 6.162 6.163 6.162-2.759 6.162-6.163c0-3.403-2.759-6.162-6.162-6.162zm0 10.162c-2.209 0-4-1.79-4-4 0-2.209 1.791-4 4-4s4 1.791 4 4c0 2.21-1.791 4-4 4zm6.406-11.845c-.796 0-1.441.645-1.441 1.44s.645 1.44 1.441 1.44c.795 0 1.439-.645 1.439-1.44s-.644-1.44-1.439-1.44z"/>
                </svg>
              </a>
            </div>
            <p class="text-gray-300">客服热线: 400-123-4567</p>
            <p class="text-gray-300">工作时间: 9:00-21:00</p>
          </div>
        </div>
        <div class="mt-8 pt-8 border-t border-gray-700 text-center text-gray-400">
          <p>© 2026 秒杀商城. 保留所有权利.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../api'
import { useUserStore, useGoodsStore } from '../stores'

const router = useRouter()
const { logout } = useUserStore()
const { goodsList, loading, error, setGoodsList, setLoading, setError } = useGoodsStore()

// 用户菜单显示状态
const showUserMenu = ref(false)

// 切换用户菜单显示状态
const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
}

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

const fetchGoodsList = async () => {
  setLoading(true)
  setError(null)
  try {
    const response = await api.getGoodsList({ pageNum: 1, pageSize: 10 })
    if (response && response.records) {
      setGoodsList(response.records)
    } else if (response.code === 0) {
      // 转换后端返回的数据结构，使其与前端期望的结构一致
      const transformedGoods = response.data.records.map((goods) => {
        let goodsImg = `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=e-commerce%20product%20placeholder%20image&image_size=square`
        if (goods.photoUrl) {
          // 直接使用数据库中的OSS访问链接
          goodsImg = goods.photoUrl
        }
        return {
          id: goods.seckillId,
          goodsName: goods.name,
          goodsTitle: goods.name,
          seckillPrice: goods.price,
          goodsPrice: goods.price * 1.2, // 模拟原价
          stockCount: goods.number,
          endTime: new Date(goods.endTime).getTime(),
          goodsImg: goodsImg
        }
      })
      setGoodsList(transformedGoods)
    } else {
      setError(response.message)
    }
  } catch (err) {
    setError('获取商品列表失败，请稍后重试')
    console.error('获取商品列表失败:', err)
  } finally {
    setLoading(false)
  }
}

onMounted(() => {
  fetchGoodsList()
})
</script>

<style scoped>
/* 自定义样式 */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>