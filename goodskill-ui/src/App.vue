<script setup>
import {computed, onMounted, ref} from 'vue'
import {RouterView, useRoute} from 'vue-router'
import Sidebar from './components/Sidebar.vue'
import AIChat from './views/AIChat.vue'

const sidebarCollapsed = ref(false)
const route = useRoute()
const isLoginPage = computed(() => route.path === '/login')
const isLoading = ref(true)

// 页面加载动画
onMounted(() => {
  setTimeout(() => {
    isLoading.value = false
  }, 500)
})
</script>

<template>
  <div class="app-container" :class="{ 'app-loaded': !isLoading }">
    <!-- 页面加载动画 -->
    <div v-if="isLoading" class="page-loader">
      <div class="loader-content">
        <div class="loader-logo">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/>
          </svg>
        </div>
        <h2 class="loader-title">秒杀后台管理</h2>
        <div class="loader-progress">
          <div class="progress-bar"></div>
        </div>
      </div>
    </div>

    <template v-else>
      <template v-if="!isLoginPage">
        <Sidebar v-model:collapsed="sidebarCollapsed" />
        <main class="main-content" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
          <RouterView v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </RouterView>
        </main>
        <!-- AI聊天助手 - 全局可访问 -->
        <AIChat />
      </template>
      <template v-else>
        <RouterView v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </RouterView>
      </template>
    </template>
  </div>
</template>

<style>
/* 全局动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 页面加载器 */
.page-loader {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: var(--bg-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  transition: opacity 0.5s ease, visibility 0.5s ease;
}

.loader-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
}

.loader-logo {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, var(--accent-primary) 0%, var(--accent-secondary) 100%);
  border-radius: var(--radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: var(--shadow-xl);
  animation: pulse 2s infinite;
}

.loader-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  font-family: 'Poppins', sans-serif;
  margin: 0;
}

.loader-progress {
  width: 200px;
  height: 4px;
  background: var(--bg-tertiary);
  border-radius: var(--radius-full);
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, var(--accent-primary), var(--accent-secondary));
  border-radius: var(--radius-full);
  animation: progress 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    box-shadow: 0 0 0 0 rgba(14, 165, 233, 0.4);
  }
  50% {
    box-shadow: 0 0 0 20px rgba(14, 165, 233, 0);
  }
}

@keyframes progress {
  0% {
    width: 0%;
  }
  50% {
    width: 70%;
  }
  100% {
    width: 100%;
  }
}

/* 主容器 */
.app-container {
  display: flex;
  min-height: 100vh;
  background: var(--bg-primary);
  transition: all 0.5s ease;
}

.app-loaded {
  opacity: 1;
}

/* 主内容区域 */
.main-content {
  flex: 1;
  margin-left: 260px;
  width: calc(100% - 260px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  min-height: 100vh;
  height: 100%;
  overflow-x: hidden;
  position: relative;
  display: flex;
  flex-direction: column;
  background: var(--bg-primary);
}

.main-content > * {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.main-content.sidebar-collapsed {
  margin-left: 80px;
  width: calc(100% - 80px);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .main-content {
    margin-left: 240px;
    width: calc(100% - 240px);
  }

  .main-content.sidebar-collapsed {
    margin-left: 72px;
    width: calc(100% - 72px);
  }
}

@media (max-width: 768px) {
  .main-content {
    margin-left: 0;
    width: 100%;
  }

  .main-content.sidebar-collapsed {
    margin-left: 0;
    width: 100%;
  }
}

@media (max-width: 480px) {
  .loader-logo {
    width: 48px;
    height: 48px;
  }

  .loader-title {
    font-size: 18px;
  }

  .loader-progress {
    width: 150px;
  }
}
</style>
