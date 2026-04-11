<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100">
    <div class="bg-white p-8 rounded-xl shadow-xl w-full max-w-md transform transition-all duration-300 hover:shadow-2xl">
      <!-- Logo and Title -->
      <div class="text-center mb-8">
        <div class="w-16 h-16 bg-primary rounded-full flex items-center justify-center mx-auto mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
          </svg>
        </div>
        <h2 class="text-2xl font-bold text-gray-800">欢迎登录</h2>
        <p class="text-gray-500 mt-2">请输入您的账号和密码</p>
      </div>
      
      <form @submit.prevent="handleLogin" class="space-y-6">
        <!-- Username Field -->
        <div class="relative">
          <label for="username" class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
          <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
            </div>
            <input
              type="text"
              id="username"
              v-model="form.username"
              class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent transition-all duration-200"
              placeholder="请输入用户名"
              required
            />
          </div>
        </div>
        
        <!-- Password Field -->
        <div class="relative">
          <label for="password" class="block text-sm font-medium text-gray-700 mb-1">密码</label>
          <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
              </svg>
            </div>
            <input
              type="password"
              id="password"
              v-model="form.password"
              class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent transition-all duration-200"
              placeholder="请输入密码"
              required
            />
          </div>
        </div>
        
        <!-- Error Message -->
        <div v-if="error" class="bg-red-50 border border-red-200 rounded-lg p-3 text-red-600 text-sm">
          <div class="flex items-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-.77-1.667-2.502-3.002-2.502H5.732c-1.335 0-2.232 1.732-1.732 3L5.268 16c.77.77 1.667 2.502 3.002 2.502z" />
            </svg>
            {{ error }}
          </div>
        </div>
        
        <!-- Login Button -->
        <button
          type="submit"
          :disabled="loading"
          class="w-full bg-primary text-white py-3 px-4 rounded-lg hover:bg-blue-600 transition-all duration-200 transform hover:scale-105 disabled:bg-blue-300 disabled:cursor-not-allowed flex items-center justify-center"
        >
          <svg v-if="loading" class="animate-spin -ml-1 mr-2 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
      
      <!-- Test Account Info -->
      <div class="mt-8 p-4 bg-blue-50 rounded-lg">
        <h3 class="text-sm font-medium text-blue-800 mb-2">测试账号</h3>
        <div class="flex flex-col space-y-2 text-sm">
          <div class="flex justify-between">
            <span class="text-gray-600">用户名:</span>
            <span class="font-medium">admin</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-600">密码:</span>
            <span class="font-medium">123456</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../api'
import { useUserStore } from '../stores'

const router = useRouter()
const { login } = useUserStore()

const form = ref({
  username: '',
  password: ''
})

const loading = ref(false)
const error = ref('')

const handleLogin = async () => {
  loading.value = true
  error.value = ''
  try {
    const response = await api.login(form.value)
    if (response.code === 0 || response.code === 200) {
      const { accessToken } = response.data
      login(accessToken, 1, form.value.username, null)
      try {
        const userInfoResponse = await api.getCustomerUserInfo()
        if (userInfoResponse.code === 0 || userInfoResponse.code === 200) {
          login(accessToken, userInfoResponse.data.userId, userInfoResponse.data.username, userInfoResponse.data.mobile)
        }
      } catch (e) {
        console.error('获取用户信息失败:', e)
      }
      router.push('/')
    } else {
      error.value = response.message || response.msg || '登录失败'
    }
  } catch (err) {
    error.value = '登录失败，请稍后重试'
    console.error('登录失败:', err)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 自定义样式 */
</style>