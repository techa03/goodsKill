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
  { icon: 'bolt', title: '秒杀模拟', color: '#f97316', description: '秒杀策略测试' },
  { icon: 'chart-line', title: '数据统计', color: '#8b5cf6', description: '销售数据分析' },
  { icon: 'cog', title: '系统设置', color: '#6b7280', description: '系统参数配置' },
  { icon: 'shield-alt', title: '安全管理', color: '#14b8a6', description: '权限和安全' }
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
  } else if (item.title === '秒杀模拟') {
    router.push('/seckill-simulation')
  }
}

const getIconPath = (icon) => {
  const icons = {
    home: 'M3 3h7v7H3V3zm11 0h7v7h-7V3zm0 11h7v7h-7v-7zM3 14h7v7H3v-7z',
    users: 'M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2M9 7a4 4 0 1 0 0-8 4 4 0 0 0 0 8zM23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75',
    'shopping-cart': 'M6 2L3 6v14a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V6l-3-4zM3 6h18M16 10a4 4 0 0 1-8 0',
    box: 'M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16zM3.27 6.96L12 12.01l8.73-5.05M12 22.08V12',
    bolt: 'M13 2L3 14h9l-1 8 10-12h-9l1-8z',
    'chart-line': 'M18 20V10M12 20V4M6 20v-6M3 20h18',
    cog: 'M4 21v-7M4 10V3M12 21v-9M12 8V3M20 21v-5M20 12V3M1 14h6M9 8h6M17 16h6',
    'shield-alt': 'M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10zM9 12l2 2 4-4'
  }
  return icons[icon] || ''
}
</script>

<template>
  <div class="dashboard-container">
    <div class="bg-effects">
      <div class="glow-orb orb-1"></div>
      <div class="glow-orb orb-2"></div>
      <div class="grid-overlay"></div>
    </div>
    
    <PageHeader title="控制台" :show-back="false" class="glass-header" />

    <div class="dashboard-content">
      <main class="dashboard-main">
        <section class="welcome-section fade-in-up" style="--delay: 0s">
          <div class="welcome-card">
            <div class="welcome-text">
              <h1>早安，{{ currentUser.username }} <span class="wave">👋</span></h1>
              <p>今天是 {{ new Date().toLocaleDateString('zh-CN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }) }}，系统运行平稳。</p>
            </div>
            <div class="weather-widget">
              <span class="weather-icon">☀️</span>
              <span class="weather-temp">26°C</span>
            </div>
          </div>
        </section>
        
        <section class="stats-section fade-in-up" style="--delay: 0.1s">
          <div class="stat-card" v-for="(stat, index) in systemStats" :key="stat.label">
            <div class="stat-icon-bg" :class="`icon-bg-${index}`">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" v-if="index === 0">
                 <path d="M6 2L3 6v14a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V6l-3-4zM3 6h18M16 10a4 4 0 0 1-8 0"/>
              </svg>
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" v-if="index === 1">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2M9 7a4 4 0 1 0 0-8 4 4 0 0 0 0 8z"/>
              </svg>
               <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" v-if="index === 2">
                <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
              </svg>
               <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" v-if="index === 3">
                <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
              </svg>
            </div>
            <div class="stat-info">
              <div class="stat-label">{{ stat.label }}</div>
              <div class="stat-value-group">
                <div class="stat-value">{{ stat.value }}</div>
                <div class="stat-change" :class="stat.trend">
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                    <polyline v-if="stat.trend === 'up'" points="18 15 12 9 6 15"/>
                    <polyline v-else points="6 9 12 15 18 9"/>
                  </svg>
                  {{ stat.change }}
                </div>
              </div>
            </div>
            <div class="stat-chart-mini">
              <!-- 简单的 SVG 迷你图 -->
               <svg width="60" height="30" viewBox="0 0 60 30" fill="none" stroke="currentColor" stroke-width="2" class="sparkline" :class="stat.trend">
                  <path d="M0 25 Q10 20 20 22 T40 10 T60 5" v-if="stat.trend === 'up'" />
                  <path d="M0 5 Q10 10 20 15 T40 20 T60 25" v-else />
               </svg>
            </div>
          </div>
        </section>

        <div class="dashboard-grid">
          <section class="menu-section fade-in-up" style="--delay: 0.2s">
            <div class="section-header">
              <h2 class="section-title">快捷功能</h2>
              <button class="more-btn">查看全部</button>
            </div>
            <div class="menu-grid">
              <div
                v-for="item in menuItems"
                :key="item.title"
                class="menu-card"
                @click="handleMenuClick(item)"
              >
                <div class="menu-card-inner">
                  <div class="menu-icon-wrapper" :style="{ '--icon-color': item.color }">
                    <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="menu-svg">
                      <path :d="getIconPath(item.icon)"/>
                    </svg>
                  </div>
                  <div class="menu-info">
                    <h3>{{ item.title }}</h3>
                    <p>{{ item.description }}</p>
                  </div>
                  <div class="menu-action">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <line x1="5" y1="12" x2="19" y2="12"/>
                      <polyline points="12 5 19 12 12 19"/>
                    </svg>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <section class="activity-section fade-in-up" style="--delay: 0.3s">
            <div class="section-header">
              <h2 class="section-title">最近动态</h2>
              <button class="refresh-btn">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                   <path d="M23 4v6h-6M1 20v-6h6"/>
                   <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/>
                </svg>
              </button>
            </div>
            <div class="activity-card">
              <div class="activity-list">
                <div
                  v-for="(activity, index) in recentActivities"
                  :key="index"
                  class="activity-item"
                >
                  <div class="activity-timeline-line"></div>
                  <div class="activity-icon-box" :class="activity.type">
                    <svg v-if="activity.type === 'add'" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <line x1="12" y1="5" x2="12" y2="19"/>
                      <line x1="5" y1="12" x2="19" y2="12"/>
                    </svg>
                    <svg v-else-if="activity.type === 'order'" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M6 2L3 6v14a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V6l-3-4z"/>
                    </svg>
                    <svg v-else-if="activity.type === 'stock'" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                       <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
                    </svg>
                    <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                      <polyline points="22 4 12 14.01 9 11.01"/>
                    </svg>
                  </div>
                  <div class="activity-content">
                    <div class="activity-header">
                      <span class="activity-user">{{ activity.user }}</span>
                      <span class="activity-time">{{ activity.time }}</span>
                    </div>
                    <div class="activity-detail">
                      <span class="activity-action">{{ activity.action }}</span>
                      <span class="activity-target">{{ activity.target }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </section>
        </div>
      </main>
    </div>
  </div>
</template>

<style scoped>
/* 全局容器 & 背景特效 */
.dashboard-container {
  min-height: calc(100vh - 80px);
  background: var(--bg-primary);
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
}

.bg-effects {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.glow-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.6;
}

.orb-1 {
  top: -100px;
  right: -100px;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(14, 165, 233, 0.2) 0%, rgba(14, 165, 233, 0) 70%);
  animation: float-orb 20s ease-in-out infinite alternate;
}

.orb-2 {
  bottom: -50px;
  left: -50px;
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.15) 0%, rgba(99, 102, 241, 0) 70%);
  animation: float-orb 25s ease-in-out infinite alternate-reverse;
}

.grid-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    linear-gradient(rgba(226, 232, 240, 0.4) 1px, transparent 1px),
    linear-gradient(90deg, rgba(226, 232, 240, 0.4) 1px, transparent 1px);
  background-size: 40px 40px;
  opacity: 0.5;
}

[data-theme="dark"] .grid-overlay {
  background-image: 
    linear-gradient(rgba(71, 85, 105, 0.4) 1px, transparent 1px),
    linear-gradient(90deg, rgba(71, 85, 105, 0.4) 1px, transparent 1px);
}

@keyframes float-orb {
  0% { transform: translate(0, 0); }
  100% { transform: translate(30px, 50px); }
}

/* 玻璃头部 */
/* 玻璃头部样式已移至PageHeader组件 */

/* 仪表盘内容区 */
.dashboard-content {
  flex: 1;
  padding: 32px 40px;
  overflow-y: auto;
  position: relative;
  z-index: 1;
}

.dashboard-main {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

/* 欢迎卡片 */
.welcome-card {
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  border-radius: 24px;
  padding: 32px 40px;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 20px 40px -10px rgba(15, 23, 42, 0.3);
  position: relative;
  overflow: hidden;
}

.welcome-card::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  width: 40%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.05));
  pointer-events: none;
}

.welcome-text h1 {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
  letter-spacing: -0.02em;
}

.wave {
  display: inline-block;
  animation: wave 2.5s infinite;
  transform-origin: 70% 70%;
}

@keyframes wave {
  0% { transform: rotate(0deg); }
  10% { transform: rotate(14deg); }
  20% { transform: rotate(-8deg); }
  30% { transform: rotate(14deg); }
  40% { transform: rotate(-4deg); }
  50% { transform: rotate(10deg); }
  60% { transform: rotate(0deg); }
  100% { transform: rotate(0deg); }
}

.welcome-text p {
  font-size: 15px;
  color: #94a3b8;
  font-weight: 400;
}

.weather-widget {
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(255, 255, 255, 0.1);
  padding: 10px 20px;
  border-radius: 100px;
  backdrop-filter: blur(4px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.weather-icon {
  font-size: 24px;
}

.weather-temp {
  font-size: 18px;
  font-weight: 600;
}

/* 统计卡片 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.stat-card {
  background: var(--bg-secondary);
  border-radius: 20px;
  padding: 24px;
  box-shadow: var(--shadow-md);
  display: flex;
  align-items: flex-start;
  gap: 16px;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1), box-shadow 0.3s ease;
  border: 1px solid var(--border-light);
  position: relative;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-lg);
  border-color: var(--border-color);
}

.stat-icon-bg {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.icon-bg-0 { background: #eff6ff; color: #3b82f6; }
.icon-bg-1 { background: #f0fdf4; color: #22c55e; }
.icon-bg-2 { background: #fff7ed; color: #f97316; }
.icon-bg-3 { background: #fef2f2; color: #ef4444; }

.stat-info {
  flex: 1;
  min-width: 0;
}

.stat-label {
  font-size: 13px;
  color: var(--text-secondary);
  font-weight: 500;
  margin-bottom: 6px;
}

.stat-value-group {
  display: flex;
  align-items: baseline;
  gap: 8px;
  flex-wrap: wrap;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.02em;
}

.stat-change {
  font-size: 12px;
  font-weight: 600;
  padding: 2px 6px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 2px;
}

.stat-change.up {
  background: var(--success-light);
  color: var(--success-color);
}

.stat-change.down {
  background: var(--danger-light);
  color: var(--danger-color);
}

.stat-chart-mini {
  position: absolute;
  bottom: 24px;
  right: 24px;
  opacity: 0.5;
}

.sparkline.up { color: var(--success-color); }
.sparkline.down { color: var(--danger-color); }

/* 仪表盘网格布局 */
.dashboard-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 32px;
}

/* 通用区块头 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #0f172a;
  letter-spacing: -0.01em;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title::before {
  content: '';
  display: block;
  width: 4px;
  height: 18px;
  background: #3b82f6;
  border-radius: 2px;
}

.more-btn, .refresh-btn {
  background: none;
  border: none;
  color: #64748b;
  font-size: 13px;
  cursor: pointer;
  transition: color 0.2s;
}

.more-btn:hover, .refresh-btn:hover {
  color: #3b82f6;
}

/* 菜单卡片 */
.menu-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.menu-card {
  background: var(--bg-secondary);
  border-radius: 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-sm);
  position: relative;
  overflow: hidden;
}

.menu-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: var(--icon-color, #3b82f6);
  opacity: 0;
  transition: opacity 0.3s;
}

.menu-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: var(--border-color);
}

.menu-card:hover::before {
  opacity: 1;
}

.menu-card-inner {
  padding: 24px;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.menu-icon-wrapper {
  width: 52px;
  height: 52px;
  border-radius: 16px;
  background: var(--bg-tertiary);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  color: var(--icon-color);
  transition: all 0.3s ease;
}

.menu-card:hover .menu-icon-wrapper {
  background: var(--icon-color);
  color: white;
  transform: scale(1.1) rotate(-5deg);
}

.menu-info h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 6px;
}

.menu-info p {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.5;
  margin-bottom: 20px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.menu-action {
  margin-top: auto;
  align-self: flex-end;
  color: var(--text-tertiary);
  transition: all 0.3s;
  transform: translateX(-5px);
  opacity: 0;
}

.menu-card:hover .menu-action {
  color: var(--icon-color);
  transform: translateX(0);
  opacity: 1;
}

/* 动态列表 */
.activity-card {
  background: var(--bg-secondary);
  border-radius: 20px;
  padding: 24px;
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-md);
  height: fit-content;
}

.activity-list {
  display: flex;
  flex-direction: column;
}

.activity-item {
  display: flex;
  gap: 16px;
  padding-bottom: 24px;
  position: relative;
}

.activity-item:last-child {
  padding-bottom: 0;
}

.activity-timeline-line {
  position: absolute;
  top: 32px;
  left: 18px;
  bottom: -8px;
  width: 2px;
  background: var(--border-light);
}

.activity-item:last-child .activity-timeline-line {
  display: none;
}

.activity-icon-box {
  width: 38px;
  height: 38px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
  background: var(--bg-secondary);
  border: 2px solid var(--bg-secondary);
  box-shadow: var(--shadow-sm);
}

.activity-icon-box.add { background: var(--success-light); color: var(--success-color); }
.activity-icon-box.order { background: var(--warning-light); color: var(--warning-color); }
.activity-icon-box.stock { background: var(--danger-light); color: var(--danger-color); }
.activity-icon-box.approve { background: var(--accent-light); color: var(--accent-secondary); }

.activity-content {
  flex: 1;
  padding-top: 2px;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.activity-user {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.activity-time {
  font-size: 12px;
  color: var(--text-tertiary);
}

.activity-detail {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.5;
}

.activity-target {
  color: var(--accent-primary);
  font-weight: 500;
  margin-left: 6px;
}

/* 动画 */
.fade-in-up {
  animation: fadeInUp 0.6s cubic-bezier(0.16, 1, 0.3, 1) forwards;
  opacity: 0;
  transform: translateY(20px);
  animation-delay: var(--delay, 0s);
}

@keyframes fadeInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式 */
@media (max-width: 1280px) {
  .menu-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 1024px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-section {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .dashboard-content {
    padding: 24px 20px;
  }
  
  .stats-section {
    grid-template-columns: 1fr;
  }
  
  .menu-grid {
    grid-template-columns: 1fr;
  }
  
  .welcome-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
}
</style>
