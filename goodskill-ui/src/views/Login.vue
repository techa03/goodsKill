<script setup>
import {ref} from 'vue'
import axios from 'axios'
import {useRouter} from 'vue-router'

const router = useRouter()

const username = ref('')
const password = ref('')
const loading = ref(false)
const errorMessage = ref('')
const isFocused = ref({ username: false, password: false })

const handleLogin = async () => {
  if (!username.value || !password.value) {
    errorMessage.value = '请输入用户名和密码'
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    const response = await axios.post('http://127.0.0.1:80/api/auth/login', {
      username: username.value,
      password: password.value
    })

    if (response.data.code === 0) {
      localStorage.setItem('token', response.data.data)
      localStorage.setItem('currentUser', JSON.stringify({
        username: username.value,
        role: '管理员',
        lastLogin: new Date().toLocaleString('zh-CN')
      }))
      router.push('/dashboard')
    } else {
      errorMessage.value = response.data.msg || '登录失败'
    }
  } catch (error) {
    errorMessage.value = '网络错误，请稍后重试'
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-container">
    <div class="login-backdrop"></div>
    <div class="login-content">
      <div class="brand-section">
        <div class="logo-circle">
          <i class="fas fa-bolt logo-icon"></i>
        </div>
        <h1>GoodSkill</h1>
        <p class="subtitle">高性能秒杀后台管理系统</p>
      </div>

      <div class="login-card">
        <div class="card-header">
          <h2>欢迎回来</h2>
          <p>请输入您的账号密码进行登录</p>
        </div>

        <form @submit.prevent="handleLogin" class="login-form">
          <div class="form-group" :class="{ 'focused': isFocused.username || username }">
            <label for="username">用户名</label>
            <div class="input-wrapper">
              <div class="icon-container">
                <i class="fas fa-user input-icon"></i>
              </div>
              <input
                type="text"
                id="username"
                v-model="username"
                @focus="isFocused.username = true"
                @blur="isFocused.username = false"
                :disabled="loading"
                autocomplete="username"
              />
            </div>
          </div>

          <div class="form-group" :class="{ 'focused': isFocused.password || password }">
            <label for="password">密码</label>
            <div class="input-wrapper">
              <div class="icon-container">
                <i class="fas fa-lock input-icon"></i>
              </div>
              <input
                type="password"
                id="password"
                v-model="password"
                @focus="isFocused.password = true"
                @blur="isFocused.password = false"
                :disabled="loading"
                autocomplete="current-password"
              />
            </div>
          </div>

          <div v-if="errorMessage" class="error-banner">
            <i class="fas fa-exclamation-triangle"></i>
            <span>{{ errorMessage }}</span>
          </div>

          <button
            type="submit"
            class="submit-btn"
            :disabled="loading"
          >
            <span v-if="!loading">登 录</span>
            <div v-else class="spinner"></div>
          </button>
        </form>

        <div class="card-footer">
          <p>© 2026 GoodSkill Admin. All rights reserved.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

.login-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #0f172a;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
  overflow: hidden;
  position: relative;
}

.login-backdrop {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background:
    radial-gradient(circle at 15% 50%, rgba(79, 70, 229, 0.15) 0%, transparent 25%),
    radial-gradient(circle at 85% 30%, rgba(236, 72, 153, 0.15) 0%, transparent 25%);
  z-index: 1;
}

.login-backdrop::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.03'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
}

.login-content {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 80px;
  z-index: 10;
  max-width: 1200px;
  width: 90%;
  justify-content: center;
}

/* Brand Section */
.brand-section {
  color: white;
  text-align: left;
  animation: slideInLeft 0.8s ease-out;
  display: none; /* Hidden on mobile by default */
}

@media (min-width: 1024px) {
  .brand-section {
    display: block;
    width: 400px;
  }
}

.logo-circle {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #4f46e5 0%, #ec4899 100%);
  border-radius: 16px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 24px;
  box-shadow: 0 10px 25px -5px rgba(79, 70, 229, 0.4);
}

.logo-icon {
  font-size: 32px;
  color: white;
}

.brand-section h1 {
  font-size: 48px;
  font-weight: 800;
  margin: 0 0 16px 0;
  letter-spacing: -1px;
  background: linear-gradient(to right, #ffffff, #cbd5e1);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.subtitle {
  font-size: 18px;
  color: #94a3b8;
  line-height: 1.6;
  margin: 0;
}

/* Login Card */
.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  padding: 48px;
  border-radius: 24px;
  width: 100%;
  max-width: 440px;
  box-shadow:
    0 20px 25px -5px rgba(0, 0, 0, 0.1),
    0 8px 10px -6px rgba(0, 0, 0, 0.1);
  animation: slideInUp 0.8s ease-out;
  position: relative;
  overflow: hidden;
}

.login-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, #4f46e5, #ec4899);
}

.card-header {
  margin-bottom: 40px;
}

.card-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 8px 0;
}

.card-header p {
  color: #64748b;
  font-size: 14px;
}

/* Form Styles */
.login-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-group {
  position: relative;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #334155;
  margin-bottom: 8px;
  transition: color 0.2s;
}

.form-group.focused label {
  color: #4f46e5;
}

.input-wrapper {
  display: flex;
  align-items: stretch;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.2s ease;
}

.form-group.focused .input-wrapper {
  border-color: #4f46e5;
  box-shadow: 0 0 0 4px rgba(79, 70, 229, 0.1);
}

.icon-container {
  width: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f1f5f9;
  border-right: 1px solid #e2e8f0;
  transition: all 0.2s ease;
}

.form-group.focused .icon-container {
  background-color: #eef2ff;
  border-right-color: #c7d2fe;
}

.input-icon {
  color: #94a3b8;
  font-size: 18px;
  width: 20px;
  height: 20px;
  transition: color 0.2s;
}

.form-group.focused .input-icon {
  color: #4f46e5;
}

input {
  flex: 1;
  padding: 0 20px;
  font-size: 15px;
  border: none;
  background-color: #ffffff;
  color: #1e293b;
  transition: all 0.2s ease;
  outline: none;
  height: 56px;
  line-height: 56px;
  box-sizing: border-box;
  -webkit-appearance: none;
  appearance: none;
}

/* 修复浏览器自动填充样式的背景色问题 */
input:-webkit-autofill,
input:-webkit-autofill:hover,
input:-webkit-autofill:focus,
input:-webkit-autofill:active {
  -webkit-box-shadow: 0 0 0 30px #f8fafc inset !important;
  -webkit-text-fill-color: #1e293b !important;
  transition: background-color 5000s ease-in-out 0s;
}

input:focus:-webkit-autofill {
  -webkit-box-shadow: 0 0 0 30px white inset !important;
}

input:focus {
  background-color: white;
  border-color: #4f46e5;
  box-shadow: 0 0 0 4px rgba(79, 70, 229, 0.1);
}

input::placeholder {
  color: #cbd5e1;
}

/* Error Banner */
.error-banner {
  background-color: #fef2f2;
  border: 1px solid #fee2e2;
  color: #ef4444;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 10px;
  animation: shake 0.4s ease-in-out;
}

/* Button */
.submit-btn {
  background: linear-gradient(135deg, #4f46e5 0%, #4338ca 100%);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 16px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 8px;
  position: relative;
  overflow: hidden;
  height: 54px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px -10px rgba(79, 70, 229, 0.5);
  background: linear-gradient(135deg, #4338ca 0%, #3730a3 100%);
}

.submit-btn:active:not(:disabled) {
  transform: translateY(0);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.spinner {
  width: 24px;
  height: 24px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 0.8s linear infinite;
}

.card-footer {
  margin-top: 32px;
  text-align: center;
}

.card-footer p {
  color: #94a3b8;
  font-size: 12px;
}

/* Animations */
@keyframes slideInLeft {
  from { opacity: 0; transform: translateX(-40px); }
  to { opacity: 1; transform: translateX(0); }
}

@keyframes slideInUp {
  from { opacity: 0; transform: translateY(40px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-4px); }
  75% { transform: translateX(4px); }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Responsive */
@media (max-width: 640px) {
  .login-card {
    padding: 32px 24px;
    max-width: 100%;
    border-radius: 20px 20px 0 0;
    position: absolute;
    bottom: 0;
    margin: 0;
    animation: slideInUp 0.5s ease-out;
  }

  .login-content {
    width: 100%;
    height: 100%;
    align-items: flex-end;
    gap: 0;
  }

  .brand-section {
    display: block;
    position: absolute;
    top: 60px;
    left: 24px;
    width: auto;
  }

  .logo-circle {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    margin-bottom: 16px;
  }

  .logo-icon {
    font-size: 24px;
  }

  .brand-section h1 {
    font-size: 32px;
  }

  .subtitle {
    font-size: 16px;
  }
}
</style>
