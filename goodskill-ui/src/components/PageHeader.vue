<script setup>
import {computed, onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'

const props = defineProps({
  title: {
    type: String,
    default: ''
  },
  showBack: {
    type: Boolean,
    default: false
  },
  showThemeToggle: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['back'])

const router = useRouter()
const isDark = ref(false)

const currentTheme = computed(() => isDark.value ? 'dark' : 'light')

const toggleTheme = () => {
  isDark.value = !isDark.value
  document.documentElement.setAttribute('data-theme', currentTheme.value)
  localStorage.setItem('theme', currentTheme.value)
}

const handleBack = () => {
  if (props.showBack) {
    emit('back')
  } else {
    router.push('/dashboard')
  }
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('currentUser')
  router.push('/login')
}

const currentUser = computed(() => {
  try {
    const user = localStorage.getItem('currentUser')
    return user ? JSON.parse(user) : { username: 'Admin' }
  } catch {
    return { username: 'Admin' }
  }
})

onMounted(() => {
  const savedTheme = localStorage.getItem('theme')
  isDark.value = savedTheme === 'dark'
  document.documentElement.setAttribute('data-theme', isDark.value ? 'dark' : 'light')
})

defineExpose({ toggleTheme, isDark })
</script>

<template>
  <header class="page-header">
    <div class="header-left">
      <button v-if="showBack" class="back-btn" @click="handleBack">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
      </button>
      <div class="header-title">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/>
        </svg>
        <span>{{ title }}</span>
      </div>
    </div>
    <div class="header-right">
      <button v-if="showThemeToggle" class="theme-toggle" @click="toggleTheme">
        <svg v-if="!isDark" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>
        </svg>
        <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="5"/>
          <line x1="12" y1="1" x2="12" y2="3"/>
          <line x1="12" y1="21" x2="12" y2="23"/>
          <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/>
          <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/>
          <line x1="1" y1="12" x2="3" y2="12"/>
          <line x1="21" y1="12" x2="23" y2="12"/>
          <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/>
          <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/>
        </svg>
      </button>
      <div class="user-info">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
          <circle cx="12" cy="7" r="4"/>
        </svg>
        <span>{{ currentUser.username }}</span>
      </div>
      <button class="logout-btn" @click="handleLogout">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
          <polyline points="16 17 21 12 16 7"/>
          <line x1="21" y1="12" x2="9" y2="12"/>
        </svg>
        <span>退出</span>
      </button>
    </div>
  </header>
</template>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 32px;
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-color);
}

/* 确保夜间模式下的一致性 */
[data-theme="dark"] .page-header {
  background: var(--bg-secondary) !important;
  border-bottom: 1px solid var(--border-color) !important;
}

/* 为glass-header类添加专门的样式 */
.glass-header {
  background: var(--bg-secondary) !important;
  border-bottom: 1px solid var(--border-color) !important;
}

/* 确保glass-header在夜间模式下的一致性 */
[data-theme="dark"] .glass-header {
  background: var(--bg-secondary) !important;
  border-bottom: 1px solid var(--border-color) !important;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.back-btn {
  width: 40px;
  height: 40px;
  border: none;
  background: var(--bg-tertiary);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.back-btn:hover {
  background: var(--border-color);
  color: var(--text-primary);
}

.header-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
}

.header-title svg {
  color: var(--accent-primary);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.theme-toggle {
  width: 40px;
  height: 40px;
  border: none;
  background: var(--bg-tertiary);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.theme-toggle:hover {
  background: var(--border-color);
  color: var(--text-primary);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-secondary);
  font-size: 14px;
}

.user-info svg {
  color: var(--text-tertiary);
}

.logout-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border: 1px solid var(--border-color);
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.logout-btn:hover {
  background: var(--bg-tertiary);
  color: var(--text-primary);
}

@media (max-width: 768px) {
  .page-header {
    padding: 16px;
  }

  .user-info span {
    display: none;
  }

  .logout-btn span {
    display: none;
  }
}
</style>
