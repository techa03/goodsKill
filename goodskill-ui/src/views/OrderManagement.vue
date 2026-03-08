<script setup>
import {computed, onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {createRequest} from '../utils/request'
import PageHeader from '../components/PageHeader.vue'

const router = useRouter()
const API_BASE = '/api/order'

const api = createRequest(API_BASE)

const orders = ref([])
const loading = ref(false)
const searchSeckillId = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedOrders = ref([])

const totalPages = computed(() => {
  return Math.ceil(total.value / pageSize.value) || 1
})

const pageNumbers = computed(() => {
  const pages = []
  const totalP = totalPages.value
  const current = currentPage.value
  if (totalP <= 5) {
    for (let i = 1; i <= totalP; i++) pages.push(i)
    return pages
  }
  pages.push(1)
  if (current > 3) pages.push('...')
  const startPage = Math.max(2, current - 1)
  const endPage = Math.min(totalP - 1, current + 1)
  for (let i = startPage; i <= endPage; i++) pages.push(i)
  if (current < totalP - 2) pages.push('...')
  pages.push(totalP)
  return pages
})

const showDeleteModal = ref(false)
const deleteOrderId = ref(null)
const deleteType = ref('single') // 'single' or 'batch'

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = { page: currentPage.value, size: pageSize.value }
    if (searchSeckillId.value !== '') {
      const sid = Number(searchSeckillId.value)
      if (!Number.isNaN(sid)) params.seckillId = sid
    }
    const response = await api.get('/list', { params })
    const data = response.data
    if (data != null) {
      orders.value = Array.isArray(data.records) ? data.records : []
      total.value = Number(data.total) >= 0 ? Number(data.total) : 0
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchOrders()
}

const resetSearch = () => {
  searchSeckillId.value = ''
  currentPage.value = 1
  fetchOrders()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchOrders()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchOrders()
}

const openDeleteModal = (order) => {
  deleteType.value = 'single'
  deleteOrderId.value = order.id
  showDeleteModal.value = true
}

const openBatchDeleteModal = () => {
  if (selectedOrders.value.length === 0) return
  deleteType.value = 'batch'
  showDeleteModal.value = true
}

const closeModal = () => {
  showDeleteModal.value = false
  deleteOrderId.value = null
  if (deleteType.value === 'batch') {
    selectedOrders.value = []
  }
}

const confirmDelete = async () => {
  loading.value = true
  try {
    let response
    if (deleteType.value === 'single') {
      if (!deleteOrderId.value) return
      response = await api.delete('/deleteById', { params: { id: deleteOrderId.value } })
    } else {
      if (selectedOrders.value.length === 0) return
      response = await api.delete('/batch', { data: selectedOrders.value })
    }
    if (response && (response.data === true || response.status === 200)) {
      closeModal()
      if (deleteType.value === 'batch') {
        selectedOrders.value = []
      }
      fetchOrders()
    }
  } catch (error) {
    console.error('删除订单失败:', error)
  } finally {
    loading.value = false
  }
}

const toggleSelect = (orderId) => {
  const index = selectedOrders.value.indexOf(orderId)
  if (index === -1) selectedOrders.value.push(orderId)
  else selectedOrders.value.splice(index, 1)
}

const toggleSelectAll = () => {
  if (selectedOrders.value.length === orders.value.length) {
    selectedOrders.value = []
  } else {
    selectedOrders.value = orders.value.map(o => o.id)
  }
}

const isAllSelected = computed(() => orders.value.length > 0 && selectedOrders.value.length === orders.value.length)
const isIndeterminate = computed(() => selectedOrders.value.length > 0 && selectedOrders.value.length < orders.value.length)

const goBack = () => router.push('/dashboard')

const formatTime = (time) => {
  if (!time) return '-'
  const d = new Date(time)
  return Number.isNaN(d.getTime()) ? '-' : d.toLocaleString('zh-CN')
}

const statusText = (status) => {
  if (status == null || status === undefined) return '-'
  const map = { 0: '待支付', 1: '已支付', 2: '已取消', 3: '已关闭' }
  return map[status] ?? `状态${status}`
}

onMounted(() => {
  fetchOrders()
})
</script>

<template>
  <div class="order-management">
    <PageHeader title="订单管理" :show-back="true" @back="goBack" />

    <div class="toolbar">
      <div class="search-box">
        <svg class="search-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/>
          <line x1="21" y1="21" x2="16.65" y2="16.65"/>
        </svg>
        <input type="text" v-model="searchSeckillId" placeholder="按秒杀ID筛选..." @keyup.enter="handleSearch"/>
        <button class="search-btn" @click="handleSearch">搜索</button>
        <button class="reset-btn" @click="resetSearch">重置</button>
      </div>
      <div class="toolbar-actions">
        <button class="action-btn danger" @click="openBatchDeleteModal" :disabled="selectedOrders.length === 0">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="3 6 5 6 21 6"/>
            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
          </svg>
          <span>批量删除</span>
        </button>
      </div>
    </div>

    <div class="order-table-container">
      <div class="table-wrapper">
        <table class="order-table">
          <thead>
            <tr>
              <th class="checkbox-cell">
                <input type="checkbox" :checked="isAllSelected" :indeterminate="isIndeterminate" @change="toggleSelectAll"/>
              </th>
              <th class="id-cell">订单ID</th>
              <th class="seckill-cell">秒杀ID</th>
              <th class="phone-cell">用户手机</th>
              <th class="user-id-cell">用户ID</th>
              <th class="status-cell">状态</th>
              <th class="time-cell">创建时间</th>
              <th class="action-cell">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading && orders.length === 0">
              <td colspan="8" class="loading-cell">
                <div class="loading-spinner">
                  <svg class="spinner" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10" stroke-opacity="0.25"/>
                    <path d="M12 2a10 10 0 0 1 10 10" stroke-linecap="round">
                      <animateTransform attributeName="transform" type="rotate" from="0 12 12" to="360 12 12" dur="1s" repeatCount="indefinite"/>
                    </path>
                  </svg>
                  <span>加载中...</span>
                </div>
              </td>
            </tr>
            <tr v-else-if="orders.length === 0">
              <td colspan="8" class="empty-cell">
                <div class="empty-state">
                  <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <path d="M6 2L3 6v14a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V6l-3-4zM3 6h18M16 10a4 4 0 0 1-8 0"/>
                  </svg>
                  <p>暂无订单数据</p>
                </div>
              </td>
            </tr>
            <template v-else>
              <tr v-for="order in orders" :key="order.id">
                <td class="checkbox-cell">
                  <input type="checkbox" :checked="selectedOrders.includes(order.id)" @change="toggleSelect(order.id)"/>
                </td>
                <td class="id-cell">{{ order.id || '-' }}</td>
                <td class="seckill-cell">{{ order.seckillId ?? '-' }}</td>
                <td class="phone-cell">{{ order.userPhone || '-' }}</td>
                <td class="user-id-cell">{{ order.userId || '-' }}</td>
                <td class="status-cell">
                  <span class="status-badge">{{ statusText(order.status) }}</span>
                </td>
                <td class="time-cell">{{ formatTime(order.createTime) }}</td>
                <td class="action-cell">
                  <button class="icon-btn delete" @click="openDeleteModal(order)" title="删除">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <polyline points="3 6 5 6 21 6"/>
                      <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                    </svg>
                  </button>
                </td>
              </tr>
            </template>
          </tbody>
        </table>
      </div>

      <div class="pagination" v-if="total > 0">
        <div class="pagination-info">
          显示 {{ (currentPage - 1) * pageSize + 1 }} 到 {{ Math.min(currentPage * pageSize, total) }} 条，共 {{ total }} 条
        </div>
        <div class="pagination-controls">
          <button
            class="page-btn page-btn--prev"
            :disabled="currentPage === 1"
            @click="handlePageChange(currentPage - 1)"
            aria-label="上一页"
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="15 18 9 12 15 6"/>
            </svg>
          </button>
          <template v-for="(page, index) in pageNumbers" :key="index">
            <span v-if="page === '...'" class="page-ellipsis">...</span>
            <button
              v-else
              class="page-btn page-btn--number"
              :class="{ 'page-btn--active': page === currentPage }"
              @click="handlePageChange(page)"
              :aria-label="`第 ${page} 页`"
            >
              {{ page }}
            </button>
          </template>
          <button
            class="page-btn page-btn--next"
            :disabled="currentPage * pageSize >= total"
            @click="handlePageChange(currentPage + 1)"
            aria-label="下一页"
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="9 18 15 12 9 6"/>
            </svg>
          </button>
          <div class="page-size-selector">
            <label for="pageSize" class="page-size-label">每页：</label>
            <select
              id="pageSize"
              class="page-size-select"
              v-model="pageSize"
              @change="handleSizeChange(pageSize)"
              aria-label="每页显示条数"
            >
              <option :value="10">10条</option>
              <option :value="20">20条</option>
              <option :value="50">50条</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <Teleport to="body">
      <Transition name="modal">
        <div class="modal-overlay" v-if="showDeleteModal" @click.self="closeModal">
          <div class="modal-container delete-modal">
            <button class="close-btn-absolute" @click="closeModal">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </button>
            <div class="modal-body">
              <div class="delete-warning">
                <div class="delete-icon-wrapper">
                  <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M3 6h18"/>
                    <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/>
                    <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/>
                    <line x1="10" y1="11" x2="10" y2="17"/>
                    <line x1="14" y1="11" x2="14" y2="17"/>
                  </svg>
                </div>
                <h3>确认删除</h3>
                <p v-if="deleteType === 'single'">确定要删除该订单吗？此操作不可恢复。</p>
                <p v-else>确定要批量删除选中的 {{ selectedOrders.length }} 个订单吗？此操作不可恢复。</p>
              </div>
            </div>
            <div class="modal-footer centered">
              <button class="btn secondary large" @click="closeModal">取消</button>
              <button class="btn danger large" @click="confirmDelete" :disabled="loading">
                <svg v-if="loading" class="spinner" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10" stroke-opacity="0.25"/>
                  <path d="M12 2a10 10 0 0 1 10 10" stroke-linecap="round">
                    <animateTransform attributeName="transform" type="rotate" from="0 12 12" to="360 12 12" dur="1s" repeatCount="indefinite"/>
                  </path>
                </svg>
                <span v-else>确认删除</span>
              </button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<style scoped>
.order-management {
  flex: 1;
  background: var(--bg-primary);
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-color);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.toolbar-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border: none;
  border-radius: var(--radius-sm);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn.danger {
  background: var(--danger-light);
  color: var(--danger-color);
  border: 1px solid var(--danger-color);
}

.action-btn.danger:hover:not(:disabled) {
  opacity: 0.9;
}

.action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 12px;
  background: var(--bg-tertiary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  padding: 8px 16px;
  width: 400px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.search-box:focus-within {
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  transform: translateY(-1px);
}

.search-box .search-icon {
  color: var(--text-tertiary);
  flex-shrink: 0;
  transition: color 0.2s ease;
}

.search-box:focus-within .search-icon {
  color: var(--accent-primary);
}

.search-box input {
  flex: 1;
  background: none;
  border: none;
  outline: none;
  color: var(--text-primary);
  font-size: 14px;
  transition: color 0.2s ease;
}

.search-box input::placeholder {
  color: var(--text-tertiary);
  transition: color 0.2s ease;
}

.search-box:focus-within input::placeholder {
  color: var(--text-secondary);
}

.search-btn, .reset-btn {
  padding: 8px 16px;
  border: none;
  border-radius: var(--radius-md);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.search-btn {
  background: var(--accent-primary);
  color: white;
  box-shadow: 0 2px 4px rgba(59, 130, 246, 0.2);
}

.search-btn:hover {
  background: var(--accent-secondary);
  box-shadow: 0 4px 8px rgba(59, 130, 246, 0.3);
  transform: translateY(-1px);
}

.reset-btn {
  background: var(--bg-secondary);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}

.reset-btn:hover {
  background: var(--bg-tertiary);
  border-color: var(--accent-light);
  color: var(--text-primary);
  transform: translateY(-1px);
}

.order-table-container {
  flex: 1;
  padding: 24px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.table-wrapper {
  flex: 1;
  overflow: auto;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  max-height: calc(100vh - 280px);
  min-height: 400px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.table-wrapper:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.order-table {
  width: 100%;
  border-collapse: collapse;
}

.order-table th, .order-table td {
  padding: 14px 16px;
  text-align: left;
  border-bottom: 1px solid var(--border-light);
  transition: all 0.2s ease;
}

.order-table th {
  background: var(--bg-tertiary);
  color: var(--text-secondary);
  font-weight: 600;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  position: sticky;
  top: 0;
  z-index: 10;
  backdrop-filter: blur(8px);
  border-bottom: 2px solid var(--border-color);
}

.order-table tbody tr {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: var(--radius-md);
}

.order-table tbody tr:hover {
  background: var(--bg-tertiary);
  transform: translateX(4px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.checkbox-cell {
  width: 44px;
}

.checkbox-cell input {
  width: 16px;
  height: 16px;
  accent-color: var(--accent-primary);
  cursor: pointer;
}

.id-cell {
  min-width: 220px;
  color: var(--text-tertiary);
  font-size: 13px;
  font-variant-numeric: tabular-nums;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.seckill-cell, .phone-cell, .user-id-cell {
  min-width: 120px;
  font-weight: 500;
  color: var(--text-primary);
}

.status-cell {
  width: 100px;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: capitalize;
  letter-spacing: 0.5px;
  transition: all 0.2s ease;
  background: var(--warning-light, #fef3c7);
  color: var(--warning-color, #d97706);
}

.status-badge:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.time-cell {
  min-width: 180px;
  font-variant-numeric: tabular-nums;
  color: var(--text-secondary);
  font-size: 13px;
}

.action-cell {
  width: 100px;
}

.icon-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.icon-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.icon-btn:hover::before {
  left: 100%;
}

.icon-btn.delete {
  background: var(--danger-light);
  color: var(--danger-color);
}

.icon-btn.delete:hover {
  background: var(--danger-color);
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(239, 68, 68, 0.3);
}

.loading-cell, .empty-cell {
  text-align: center;
  padding: 80px 20px !important;
}

.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  color: var(--text-secondary);
}

.loading-spinner span {
  font-size: 14px;
  font-weight: 500;
}

.spinner {
  animation: spin 1s linear infinite;
  color: var(--accent-primary);
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  color: var(--text-tertiary);
}

.empty-state svg {
  opacity: 0.3;
  font-size: 48px;
  transition: opacity 0.3s ease;
}

.empty-state:hover svg {
  opacity: 0.5;
}

.empty-state p {
  font-size: 14px;
  font-weight: 500;
  margin: 0;
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  margin-top: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.pagination:hover {
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.pagination-info {
  color: var(--text-tertiary);
  font-size: 13px;
  font-weight: 500;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.page-btn {
  width: 36px;
  height: 36px;
  border: 1px solid var(--border-color);
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 500;
}

.page-btn:hover:not(:disabled) {
  background: var(--bg-tertiary);
  border-color: var(--accent-light);
  color: var(--text-primary);
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.page-btn--active {
  background: var(--accent-primary);
  color: white;
  border-color: var(--accent-primary);
  box-shadow: 0 2px 4px rgba(59, 130, 246, 0.3);
}

.page-btn--active:hover:not(:disabled) {
  background: var(--accent-secondary);
  border-color: var(--accent-secondary);
}

.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.page-ellipsis {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  color: var(--text-tertiary);
  font-size: 14px;
  font-weight: 500;
}

.page-size-selector {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: 16px;
  padding-left: 16px;
  border-left: 1px solid var(--border-color);
}

.page-size-label {
  color: var(--text-secondary);
  font-size: 13px;
  font-weight: 500;
}

.page-size-select {
  padding: 8px 16px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  min-width: 90px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.page-size-select:hover {
  border-color: var(--accent-light);
  color: var(--text-primary);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.modal-container.delete-modal {
  max-width: 400px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform: translateY(-10px);
  opacity: 0;
  animation: modalSlideIn 0.3s ease forwards;
}

@keyframes modalSlideIn {
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.close-btn-absolute {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  border-radius: 50%;
  color: var(--text-tertiary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.close-btn-absolute:hover {
  background: var(--bg-tertiary);
  color: var(--text-primary);
  transform: rotate(90deg);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.delete-modal .modal-body {
  padding: 48px 32px 24px;
}

.delete-warning {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.delete-icon-wrapper {
  width: 96px;
  height: 96px;
  border-radius: 50%;
  background: var(--danger-light);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 28px;
  color: var(--danger-color);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 8px rgba(239, 68, 68, 0.2);
}

.delete-warning:hover .delete-icon-wrapper {
  transform: scale(1.05);
  box-shadow: 0 6px 12px rgba(239, 68, 68, 0.3);
}

.delete-warning h3 {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 16px;
  letter-spacing: -0.02em;
}

.delete-warning p {
  font-size: 15px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin: 0;
  max-width: 320px;
}

.modal-footer.centered {
  justify-content: center;
  padding: 24px 32px 36px;
  gap: 20px;
  border-top: 1px solid var(--border-color);
}

.btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border: none;
  border-radius: var(--radius-md);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  min-width: 120px;
  justify-content: center;
  letter-spacing: 0.5px;
}

.btn.secondary {
  background: var(--bg-tertiary);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}

.btn.secondary:hover {
  background: var(--border-color);
  color: var(--text-primary);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.btn.danger {
  background: var(--danger-color);
  color: white;
  box-shadow: 0 2px 4px rgba(239, 68, 68, 0.2);
}

.btn.danger:hover:not(:disabled) {
  background: var(--danger-secondary);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(239, 68, 68, 0.3);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn.large {
  padding: 14px 32px;
  font-size: 15px;
  min-width: 140px;
}

.modal-enter-active, .modal-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-enter-from, .modal-leave-to {
  opacity: 0;
  transform: scale(0.95);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .search-box {
    width: 320px;
  }
  
  .order-table-container {
    padding: 16px;
  }
  
  .table-wrapper {
    max-height: calc(100vh - 260px);
  }
  
  .id-cell {
    min-width: 120px;
    font-size: 12px;
  }
  
  .time-cell {
    min-width: 160px;
    font-size: 12px;
  }
}

@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
    padding: 16px;
  }
  
  .search-box {
    width: 100%;
  }
  
  .order-table th, .order-table td {
    padding: 10px 12px;
    font-size: 13px;
  }
  
  .pagination {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  
  .pagination-controls {
    justify-content: center;
  }
  
  .modal-container.delete-modal {
    margin: 20px;
    max-width: calc(100% - 40px);
  }
}
</style>
