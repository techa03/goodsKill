<script setup>
import {computed, onMounted, onUnmounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {createRequest, GLOBAL_ERROR_EVENT} from '../utils/request'
import PageHeader from '../components/PageHeader.vue'

const router = useRouter()
const API_BASE = '/api/order'

const api = createRequest(API_BASE)

const orders = ref([])
const loading = ref(false)
const searchParams = ref({
  orderId: '',
  seckillId: '',
  userPhone: '',
  userId: '',
  status: '',
  startTime: '',
  endTime: ''
})
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

// 订单详情相关
const showDetailModal = ref(false)
const currentOrder = ref(null)
const detailLoading = ref(false)

// 全局通知
const globalNotice = ref({
  visible: false,
  type: 'error',
  message: ''
})
let noticeTimer = null

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = { page: currentPage.value, size: pageSize.value }

    // 处理订单ID
    if (searchParams.value.orderId !== '') {
      params.orderId = searchParams.value.orderId
    }

    // 处理秒杀ID
    if (searchParams.value.seckillId !== '') {
      const sid = Number(searchParams.value.seckillId)
      if (!Number.isNaN(sid)) params.seckillId = sid
    }

    // 处理用户手机
    if (searchParams.value.userPhone !== '') {
      params.userPhone = searchParams.value.userPhone
    }

    // 处理用户ID
    if (searchParams.value.userId !== '') {
      const uid = Number(searchParams.value.userId)
      if (!Number.isNaN(uid)) params.userId = uid
    }

    // 处理订单状态
    if (searchParams.value.status !== '') {
      const status = Number(searchParams.value.status)
      if (!Number.isNaN(status)) params.status = status
    }

    // 处理时间范围
    if (searchParams.value.startTime !== '') {
      params.startTime = searchParams.value.startTime
    }
    if (searchParams.value.endTime !== '') {
      params.endTime = searchParams.value.endTime
    }

    const response = await api.get('/admin/list', { params })
    const resData = response.data
    if (resData && resData.code === 0 && resData.data) {
      orders.value = Array.isArray(resData.data.records) ? resData.data.records : []
      total.value = Number(resData.data.total) >= 0 ? Number(resData.data.total) : 0
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
  searchParams.value = {
    orderId: '',
    seckillId: '',
    userPhone: '',
    userId: '',
    status: '',
    startTime: '',
    endTime: ''
  }
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
      response = await api.delete('/admin/deleteById', { params: { id: deleteOrderId.value } })
    } else {
      if (selectedOrders.value.length === 0) return
      response = await api.delete('/admin/batch', { data: selectedOrders.value })
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
  // 与后端 OrderStatusEnum 保持一致
  const map = { 1: '待支付', 2: '已支付', 3: '已取消' }
  return map[status] ?? `状态${status}`
}

// 订单详情相关函数
const openDetailModal = (order) => {
  currentOrder.value = order
  showDetailModal.value = true
  fetchOrderDetail(order.id)
}

const closeDetailModal = () => {
  showDetailModal.value = false
  currentOrder.value = null
}

const fetchOrderDetail = async (orderId) => {
  if (!orderId) return

  detailLoading.value = true
  try {
    const response = await api.get('/admin/detail', { params: { id: orderId } })
    const resData = response.data
    if (resData && resData.code === 0 && resData.data) {
      currentOrder.value = resData.data
    } else if (resData && resData.data) {
      currentOrder.value = resData.data
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    showGlobalNotice('获取订单详情失败，请稍后重试', 'error')
  } finally {
    detailLoading.value = false
  }
}

// 全局通知函数
const closeGlobalNotice = () => {
  globalNotice.value.visible = false
}

const showGlobalNotice = (message, type = 'error') => {
  if (!message) return

  globalNotice.value = {
    visible: true,
    type,
    message
  }

  if (noticeTimer) {
    clearTimeout(noticeTimer)
  }
  noticeTimer = setTimeout(() => {
    closeGlobalNotice()
  }, 2600)
}

const handleGlobalError = (event) => {
  const message = event?.detail?.message || '系统异常，请稍后重试'
  const type = event?.detail?.type || 'error'
  showGlobalNotice(message, type)
}

onMounted(() => {
  fetchOrders()
  window.addEventListener(GLOBAL_ERROR_EVENT, handleGlobalError)
})

onUnmounted(() => {
  window.removeEventListener(GLOBAL_ERROR_EVENT, handleGlobalError)
  if (noticeTimer) {
    clearTimeout(noticeTimer)
  }
})
</script>

<template>
  <div class="order-management">
    <PageHeader title="订单管理" :show-back="true" @back="goBack" />

    <transition name="notice-slide">
      <div
          v-if="globalNotice.visible"
          class="global-notice"
          :class="`global-notice-${globalNotice.type}`"
      >
        <i class="fas fa-exclamation-triangle"></i>
        <span>{{ globalNotice.message }}</span>
      </div>
    </transition>

    <div class="toolbar">
      <div class="search-form">
        <div class="search-row">
          <div class="search-item">
            <label>订单 ID</label>
            <input type="text" v-model="searchParams.orderId" placeholder="输入订单 ID"/>
          </div>
          <div class="search-item">
            <label>秒杀 ID</label>
            <input type="number" v-model="searchParams.seckillId" placeholder="输入秒杀 ID"/>
          </div>
          <div class="search-item">
            <label>用户手机</label>
            <input type="text" v-model="searchParams.userPhone" placeholder="输入用户手机"/>
          </div>
          <div class="search-item">
            <label>用户 ID</label>
            <input type="number" v-model="searchParams.userId" placeholder="输入用户 ID"/>
          </div>
        </div>
        <div class="search-row">
          <div class="search-item">
            <label>订单状态</label>
            <select v-model="searchParams.status">
              <option value="">全部状态</option>
              <option value="1">待支付</option>
              <option value="2">已支付</option>
              <option value="3">已取消</option>
            </select>
          </div>
          <div class="search-item">
            <label>开始时间</label>
            <input type="datetime-local" v-model="searchParams.startTime"/>
          </div>
          <div class="search-item">
            <label>结束时间</label>
            <input type="datetime-local" v-model="searchParams.endTime"/>
          </div>
          <div class="search-item">
            <label>&nbsp;</label>
            <div class="search-actions">
              <button class="search-btn" @click="handleSearch" :disabled="loading">
                <svg v-if="loading" class="spinner" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10" stroke-opacity="0.25"/>
                  <path d="M12 2a10 10 0 0 1 10 10" stroke-linecap="round">
                    <animateTransform attributeName="transform" type="rotate" from="0 12 12" to="360 12 12" dur="1s" repeatCount="indefinite"/>
                  </path>
                </svg>
                <span v-else>搜索</span>
              </button>
              <button class="reset-btn" @click="resetSearch" :disabled="loading">重置</button>
              <button class="action-btn danger" @click="openBatchDeleteModal" :disabled="selectedOrders.length === 0">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="3 6 5 6 21 6"/>
                  <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                </svg>
                <span>批量删除</span>
              </button>
            </div>
          </div>
        </div>
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
                <button class="icon-btn detail" @click="openDetailModal(order)" title="详情">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                    <circle cx="12" cy="12" r="3"/>
                  </svg>
                </button>
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

      <Transition name="modal">
        <div class="modal-overlay" v-if="showDetailModal" @click.self="closeDetailModal">
          <div class="modal-container detail-modal">
            <button class="close-btn-absolute" @click="closeDetailModal">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </button>
            <div class="modal-header">
              <h2>订单详情</h2>
            </div>
            <div class="modal-body">
              <div v-if="detailLoading" class="loading-spinner">
                <svg class="spinner" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10" stroke-opacity="0.25"/>
                  <path d="M12 2a10 10 0 0 1 10 10" stroke-linecap="round">
                    <animateTransform attributeName="transform" type="rotate" from="0 12 12" to="360 12 12" dur="1s" repeatCount="indefinite"/>
                  </path>
                </svg>
                <span>加载中...</span>
              </div>
              <div v-else-if="currentOrder" class="order-detail-content">
                <div class="detail-section">
                  <h3>基本信息</h3>
                  <div class="detail-grid">
                    <div class="detail-item">
                      <label>订单ID</label>
                      <span>{{ currentOrder.id || '-' }}</span>
                    </div>
                    <div class="detail-item">
                      <label>秒杀ID</label>
                      <span>{{ currentOrder.seckillId || '-' }}</span>
                    </div>
                    <div class="detail-item">
                      <label>订单状态</label>
                      <span>{{ statusText(currentOrder.status) }}</span>
                    </div>
                    <div class="detail-item">
                      <label>创建时间</label>
                      <span>{{ formatTime(currentOrder.createTime) }}</span>
                    </div>
                  </div>
                </div>

                <div class="detail-section">
                  <h3>用户信息</h3>
                  <div class="detail-grid">
                    <div class="detail-item">
                      <label>用户ID</label>
                      <span>{{ currentOrder.userId || '-' }}</span>
                    </div>
                    <div class="detail-item">
                      <label>用户手机</label>
                      <span>{{ currentOrder.userPhone || '-' }}</span>
                    </div>
                  </div>
                </div>

                <div class="detail-section">
                  <h3>商品信息</h3>
                  <div class="detail-grid">
                    <div class="detail-item">
                      <label>商品名称</label>
                      <span>{{ currentOrder.goodsName || '-' }}</span>
                    </div>
                    <div class="detail-item">
                      <label>商品价格</label>
                      <span>{{ currentOrder.goodsPrice || '-' }}</span>
                    </div>
                    <div class="detail-item">
                      <label>购买数量</label>
                      <span>{{ currentOrder.quantity || 1 }}</span>
                    </div>
                    <div class="detail-item">
                      <label>总价</label>
                      <span>{{ currentOrder.totalPrice || '-' }}</span>
                    </div>
                  </div>
                </div>

                <div class="detail-section">
                  <h3>支付信息</h3>
                  <div class="detail-grid">
                    <div class="detail-item">
                      <label>支付状态</label>
                      <span>{{ currentOrder.payStatus ? '已支付' : '未支付' }}</span>
                    </div>
                    <div class="detail-item">
                      <label>支付时间</label>
                      <span>{{ formatTime(currentOrder.payTime) }}</span>
                    </div>
                    <div class="detail-item">
                      <label>支付方式</label>
                      <span>{{ currentOrder.payMethod || '-' }}</span>
                    </div>
                    <div class="detail-item">
                      <label>交易号</label>
                      <span>{{ currentOrder.transactionId || '-' }}</span>
                    </div>
                  </div>
                </div>
              </div>
              <div v-else class="empty-state">
                <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                  <path d="M6 2L3 6v14a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V6l-3-4zM3 6h18M16 10a4 4 0 0 1-8 0"/>
                </svg>
                <p>暂无订单详情数据</p>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn secondary large" @click="closeDetailModal">关闭</button>
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
  padding: 16px 24px;
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-color);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.search-form {
  width: 100%;
}

.search-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  align-items: end;
}

.search-row:last-child {
  margin-top: 12px;
}

.search-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.search-item label {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.search-item input,
.search-item select {
  padding: 8px 12px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  background: var(--bg-tertiary);
  color: var(--text-primary);
  font-size: 14px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  width: 100%;
  box-sizing: border-box;
  height: 40px;
}

.search-item input:focus,
.search-item select:focus {
  outline: none;
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  transform: translateY(-1px);
}

.search-item input::placeholder {
  color: var(--text-tertiary);
  transition: color 0.2s ease;
}

.search-item input:focus::placeholder {
  color: var(--text-secondary);
}

.search-actions {
  display: flex;
  gap: 12px;
  align-items: center;
  height: 40px;
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
  justify-content: center;
  gap: 6px;
  height: 40px;
  min-width: 80px;
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
  padding: 0 24px 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  position: relative;
  min-height: 0;
}

.table-wrapper {
  flex: 1;
  overflow: auto;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md) var(--radius-md) 0 0;
  border-bottom: none;
  max-height: calc(100vh - 240px);
  min-height: 300px;
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
  width: 80px;
  white-space: nowrap;
}

.icon-btn {
  width: 26px;
  height: 26px;
  border: none;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-right: 4px;
}

.icon-btn.detail {
  background: var(--accent-light);
  color: var(--accent-primary);
}

.icon-btn.detail:hover {
  background: var(--accent-primary);
  color: white;
}

.icon-btn.delete {
  background: var(--danger-light);
  color: var(--danger-color);
}

.icon-btn.delete:hover {
  background: var(--danger-color);
  color: white;
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
  padding: 16px 20px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 0 0 var(--radius-md) var(--radius-md);
  position: sticky;
  bottom: 0;
  z-index: 20;
  box-shadow: 0 -1px 8px rgba(0, 0, 0, 0.05);
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

.modal-container.detail-modal {
  max-width: 800px;
  width: 90%;
  max-height: 90vh;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform: translateY(-10px);
  opacity: 0;
  animation: modalSlideIn 0.3s ease forwards;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 24px 32px;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h2 {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.detail-modal .modal-body {
  padding: 32px;
  flex: 1;
  overflow-y: auto;
}

.order-detail-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.detail-section {
  background: var(--bg-tertiary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.detail-section:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.detail-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border-color);
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-item label {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.detail-item span {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  word-break: break-all;
}

.detail-modal .modal-footer {
  padding: 24px 32px;
  border-top: 1px solid var(--border-color);
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 768px) {
  .modal-container.detail-modal {
    width: 95%;
    max-height: 95vh;
  }

  .detail-modal .modal-body {
    padding: 20px;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }
}

/* 全局通知样式 */
.notice-slide-enter-active,
.notice-slide-leave-active {
  transition: all 0.25s ease;
}

.notice-slide-enter-from,
.notice-slide-leave-to {
  opacity: 0;
  transform: translate(-50%, -10px);
}

.global-notice {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 12000;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.22);
}

.global-notice-error {
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fecaca;
}

.global-notice-success {
  background: #dcfce7;
  color: #166534;
  border: 1px solid #bbf7d0;
}

.global-notice-warning {
  background: #fef3c7;
  color: #92400e;
  border: 1px solid #fde68a;
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

.table-wrapper::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.table-wrapper::-webkit-scrollbar-track {
  background: transparent;
}

.table-wrapper::-webkit-scrollbar-thumb {
  background: var(--border-color);
  border-radius: 3px;
}

.table-wrapper::-webkit-scrollbar-thumb:hover {
  background: var(--text-tertiary);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .toolbar, .order-table-container {
    padding-left: 24px;
    padding-right: 24px;
  }

  .search-row {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 992px) {
  .search-row {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
}

@media (max-width: 768px) {
  .toolbar {
    padding: 16px;
  }

  .search-row {
    grid-template-columns: 1fr;
  }

  .search-actions {
    justify-content: flex-end;
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
