<script setup>
import {onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import PageHeader from '../components/PageHeader.vue'

const router = useRouter()
const currentUser = ref({
  username: '',
  role: '管理员',
  lastLogin: new Date().toLocaleString('zh-CN')
})

const menuItems = ref([
  { icon: 'home', title: '首页概览', color: '#3b82f6', description: '系统整体状态' },
  { icon: 'users', title: '用户管理', color: '#22c55e', description: '用户列表和权限' },
  { icon: 'shopping-cart', title: '订单管理', color: '#f59e0b', description: '订单处理和追踪' },
  { icon: 'box', title: '商品管理', color: '#ef4444', description: '商品上架和编辑' },
  { icon: 'chart-line', title: '数据统计', color: '#8b5cf6', description: '销售数据分析' },
  { icon: 'cog', title: '系统设置', color: '#6b7280', description: '系统参数配置' },
  { icon: 'shield-alt', title: '安全管理', color: '#14b8a6', description: '权限和安全' },
  { icon: 'history', title: '操作日志', color: '#ec4899', description: '系统操作记录' }
])

const systemStats = ref([
  { label: '今日订单', value: '128', change: '+12%', trend: 'up' },
  { label: '活跃用户', value: '1,234', change: '+8%', trend: 'up' },
  { label: '商品总数', value: '3,456', change: '+3%', trend: 'up' },
  { label: '待处理', value: '23', change: '-5%', trend: 'down' }
])

const recentActivities = ref([
  { user: '张三', action: '新增商品', target: 'iPhone 15 Pro', time: '10分钟前', type: 'add' },
  { user: '李四', action: '处理订单', target: '订单 #20240128-001', time: '25分钟前', type: 'order' },
  { user: '王五', action: '更新库存', target: 'MacBook Pro 14"', time: '1小时前', type: 'stock' },
  { user: '赵六', action: '审核通过', target: '用户注册申请', time: '2小时前', type: 'approve' }
])

onMounted(() => {
  const token = localStorage.getItem('token')
  if (!token) {
    router.push('/login')
    return
  }
  const savedUser = localStorage.getItem('currentUser')
  if (savedUser) {
    currentUser.value = JSON.parse(savedUser)
  } else {
    currentUser.value.username = 'Admin'
  }
})

const handleMenuClick = (item) => {
  if (item.title === '用户管理') {
    router.push('/users')
  } else if (item.title === '商品管理') {
    router.push('/products')
  }
}

const getIconPath = (icon) => {
  const icons = {
    home: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z',
    users: 'M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2',
    'shopping-cart': 'M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6',
    box: 'M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z',
    'chart-line': 'M23 6l-9.5 9.5-5-5L1 18',
    cog: 'M12 15a3 3 0 1 0 0-6 3 3 0 0 0 0 6Z',
    'shield-alt': 'M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10Z',
    history: 'M3 3v5h5M21 21v-5h-5'
  }
  return icons[icon] || ''
}
</script>

<template>
  <div class="dashboard-container">
    <PageHeader title="控制台" :show-back="false" />

    <div class="dashboard-content">
      <main class="dashboard-main">
        <section class="welcome-section">
          <div class="welcome-text">
            <h1>欢迎回来，{{ currentUser.username }}</h1>
            <p>今天是 {{ new Date().toLocaleDateString('zh-CN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }) }}</p>
          </div>
          <div class="quick-stats">
            <div class="stat-card" v-for="stat in systemStats" :key="stat.label">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
              <div class="stat-change" :class="stat.trend">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline v-if="stat.trend === 'up'" points="18 15 12 9 6 15"/>
                  <polyline v-else points="6 9 12 15 18 9"/>
                </svg>
                {{ stat.change }}
              </div>
            </div>
          </div>
        </section>

        <section class="menu-section">
          <h2 class="section-title">快捷功能</h2>
          <div class="menu-grid">
            <div
              v-for="item in menuItems"
              :key="item.title"
              class="menu-card"
              @click="handleMenuClick(item)"
            >
              <div class="menu-icon" :style="{ background: item.color }">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path :d="getIconPath(item.icon)"/>
                </svg>
              </div>
              <div class="menu-info">
                <h3>{{ item.title }}</h3>
                <p>{{ item.description }}</p>
              </div>
              <svg class="menu-arrow" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="9 18 15 12 9 6"/>
              </svg>
            </div>
          </div>
        </section>

        <section class="activity-section">
          <h2 class="section-title">最近活动</h2>
          <div class="activity-list">
            <div
              v-for="(activity, index) in recentActivities"
              :key="index"
              class="activity-item"
            >
              <div class="activity-icon" :class="activity.type">
                <svg v-if="activity.type === 'add'" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="12" y1="5" x2="12" y2="19"/>
                  <line x1="5" y1="12" x2="19" y2="12"/>
                </svg>
                <svg v-else-if="activity.type === 'order'" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="9" cy="21" r="1"/>
                  <circle cx="20" cy="21" r="1"/>
                  <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
                </svg>
                <svg v-else-if="activity.type === 'stock'" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
                </svg>
                <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="20 6 9 17 4 12"/>
                </svg>
              </div>
              <div class="activity-content">
                <div class="activity-header">
                  <span class="activity-user">{{ activity.user }}</span>
                  <span class="activity-action">{{ activity.action }}</span>
                  <span class="activity-target">{{ activity.target }}</span>
                </div>
                <div class="activity-time">{{ activity.time }}</div>
              </div>
            </div>
          </div>
        </section>
      </main>
    </div>
  </div>
</template>

<style>
.dashboard-container {
  min-height: calc(100vh - 80px);
  background: var(--bg-primary);
  display: flex;
  flex-direction: column;
}

.dashboard-content {
  flex: 1;
  padding: 0 32px 32px;
  overflow-y: auto;
}

.dashboard-main {
  max-width: 1400px;
  margin: 0 auto;
}

.welcome-section {
  margin-bottom: 32px;
}

.welcome-text h1 {
  font-size: 26px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.welcome-text p {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 24px;
}

.quick-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-card {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: 24px;
  transition: all 0.2s ease;
}

.stat-card:hover {
  box-shadow: var(--shadow-md);
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.stat-label {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 12px;
}

.stat-change {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-change.up {
  color: var(--success-color);
}

.stat-change.down {
  color: var(--danger-color);
}

.menu-section {
  margin-bottom: 32px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 20px;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.menu-card {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.menu-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.menu-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-md);
  display: flex;
  justify-content: center;
  align-items: center;
  flex-shrink: 0;
  color: white;
}

.menu-info {
  flex: 1;
  min-width: 0;
}

.menu-info h3 {
  font-size: 15px;
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.menu-info p {
  font-size: 12px;
  color: var(--text-tertiary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.menu-arrow {
  color: var(--text-tertiary);
  flex-shrink: 0;
}

.activity-section {
  margin-bottom: 32px;
}

.activity-list {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 18px 24px;
  border-bottom: 1px solid var(--border-light);
  transition: background 0.15s ease;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-item:hover {
  background: var(--bg-tertiary);
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  display: flex;
  justify-content: center;
  align-items: center;
  flex-shrink: 0;
}

.activity-icon.add {
  background: rgba(34, 197, 94, 0.1);
  color: var(--success-color);
}

.activity-icon.order {
  background: rgba(245, 158, 11, 0.1);
  color: #f59e0b;
}

.activity-icon.stock {
  background: rgba(239, 68, 68, 0.1);
  color: var(--danger-color);
}

.activity-icon.approve {
  background: rgba(139, 92, 246, 0.1);
  color: #8b5cf6;
}

.activity-content {
  flex: 1;
}

.activity-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
  flex-wrap: wrap;
}

.activity-user {
  font-weight: 500;
  color: var(--text-primary);
}

.activity-action {
  color: var(--text-secondary);
}

.activity-target {
  color: var(--accent-primary);
}

.activity-time {
  font-size: 12px;
  color: var(--text-tertiary);
}

@media (max-width: 1200px) {
  .dashboard-content {
    padding: 0 24px 24px;
  }

  .quick-stats {
    grid-template-columns: repeat(2, 1fr);
  }

  .menu-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 992px) {
  .menu-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .dashboard-content {
    padding: 0 16px 16px;
  }

  .quick-stats {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .stat-card {
    padding: 18px;
  }

  .stat-value {
    font-size: 22px;
  }

  .menu-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .menu-card {
    padding: 18px;
  }

  .menu-icon {
    width: 40px;
    height: 40px;
  }

  .menu-info h3 {
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .quick-stats {
    grid-template-columns: 1fr 1fr;
  }

  .menu-grid {
    grid-template-columns: 1fr;
  }
}
</style>
