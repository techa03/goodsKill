<script setup>
import {computed, onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import axios from 'axios'
import PageHeader from '../components/PageHeader.vue'

const router = useRouter()
const API_BASE = '/api/auth'

const api = axios.create({
  baseURL: API_BASE,
  timeout: 10000
})

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['access_token'] = token
    }
    return config
  },
  (error) => Promise.reject(error)
)

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('currentUser')
      router.push('/login')
    }
    return Promise.reject(error)
  }
)

const users = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedUsers = ref([])

const totalPages = computed(() => {
  return Math.ceil(total.value / pageSize.value)
})

const pageNumbers = computed(() => {
  const pages = []
  const total = totalPages.value
  const current = currentPage.value

  // 总页数小于等于5时，显示所有页码
  if (total <= 5) {
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
    return pages
  }

  // 总页数大于5时，智能显示页码

  // 显示第一页
  pages.push(1)

  // 显示第一页后的省略号（如果当前页大于3）
  if (current > 3) {
    pages.push('...')
  }

  // 显示当前页附近的3个页码
  const startPage = Math.max(2, current - 1)
  const endPage = Math.min(total - 1, current + 1)

  for (let i = startPage; i <= endPage; i++) {
    pages.push(i)
  }

  // 显示最后一页前的省略号（如果当前页小于总页数-2）
  if (current < total - 2) {
    pages.push('...')
  }

  // 显示最后一页
  pages.push(total)

  return pages
})

const showUserModal = ref(false)
const showDeleteModal = ref(false)
const modalType = ref('create')
const deleteUserId = ref(null)

const userForm = ref({
  id: null,
  username: '',
  password: '',
  account: '',
  mobile: '',
  emailAddr: '',
  locked: 0
})

const formErrors = ref({})

const fetchUsers = async () => {
  loading.value = true
  try {
    const response = await api.get('/user/list', {
      params: { page: currentPage.value, size: pageSize.value, keyword: searchKeyword.value }
    })
    if (response.data.code === 0) {
      users.value = response.data.data.records
      total.value = response.data.data.total
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchUsers()
}

const resetSearch = () => {
  searchKeyword.value = ''
  currentPage.value = 1
  fetchUsers()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchUsers()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchUsers()
}

const openCreateModal = () => {
  modalType.value = 'create'
  userForm.value = { id: null, username: '', password: '', account: '', mobile: '', emailAddr: '', locked: 0 }
  formErrors.value = {}
  showUserModal.value = true
}

const openEditModal = async (user) => {
  modalType.value = 'edit'
  formErrors.value = {}
  try {
    const response = await api.get(`/user/${user.id}`)
    if (response.data.code === 0) {
      const { password, ...formData } = response.data.data
      userForm.value = { ...formData, password: '' }
      showUserModal.value = true
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

const closeModal = () => {
  showDeleteModal.value = false
  userForm.value = { id: null, username: '', password: '', account: '', mobile: '', emailAddr: '', locked: 0 }
  formErrors.value = {}
}

const batchDelete = async () => {
  if (selectedUsers.value.length === 0) return
  loading.value = true
  try {
    const response = await api.delete('/user/batch', { data: selectedUsers.value })
    if (response.data.code === 0) {
      selectedUsers.value = []
      fetchUsers()
    } else {
      formErrors.value.general = response.data.msg || '操作失败'
    }
  } catch (error) {
    console.error('批量删除失败:', error)
    formErrors.value.general = error.response?.data?.msg || '网络错误，请稍后重试'
  } finally {
    loading.value = false
  }
}

const deleteType = ref('single') // 'single' or 'batch'

const openDeleteModal = (user) => {
  deleteType.value = 'single'
  deleteUserId.value = user.id
  showDeleteModal.value = true
}

const openBatchDeleteModal = () => {
  if (selectedUsers.value.length === 0) return
  deleteType.value = 'batch'
  showDeleteModal.value = true
}

const confirmDelete = async () => {
  loading.value = true
  try {
    let response
    if (deleteType.value === 'single') {
      if (!deleteUserId.value) return
      response = await api.delete(`/user/${deleteUserId.value}`)
    } else {
      if (selectedUsers.value.length === 0) return
      response = await api.delete('/user/batch', { data: selectedUsers.value })
    }

    if (response && response.data.code === 0) {
      closeModal()
      if (deleteType.value === 'batch') {
        selectedUsers.value = []
      }
      fetchUsers()
    }
  } catch (error) {
    console.error('删除失败:', error)
  } finally {
    loading.value = false
  }
}

const toggleSelect = (userId) => {
  const index = selectedUsers.value.indexOf(userId)
  if (index === -1) selectedUsers.value.push(userId)
  else selectedUsers.value.splice(index, 1)
}

const toggleSelectAll = () => {
  if (selectedUsers.value.length === users.value.length) {
    selectedUsers.value = []
  } else {
    selectedUsers.value = users.value.map(u => u.id)
  }
}

const isAllSelected = computed(() => users.value.length > 0 && selectedUsers.value.length === users.value.length)
const isIndeterminate = computed(() => selectedUsers.value.length > 0 && selectedUsers.value.length < users.value.length)

const goBack = () => router.push('/dashboard')

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

const lockedText = (locked) => (locked === 0 ? '正常' : '已锁定')

onMounted(() => {
  fetchUsers()
})
</script>

<template>
  <div class="user-management">
    <PageHeader title="用户管理" :show-back="true" @back="goBack" />

    <div class="toolbar">
      <div class="search-box">
        <svg class="search-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/>
          <line x1="21" y1="21" x2="16.65" y2="16.65"/>
        </svg>
        <input type="text" v-model="searchKeyword" placeholder="搜索用户名、账号或邮箱..." @keyup.enter="handleSearch"/>
        <button class="search-btn" @click="handleSearch">搜索</button>
        <button class="reset-btn" @click="resetSearch">重置</button>
      </div>
      <div class="toolbar-actions">
        <button class="action-btn primary" @click="openCreateModal">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19"/>
            <line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          <span>新增用户</span>
        </button>
        <button class="action-btn danger" @click="openBatchDeleteModal" :disabled="selectedUsers.length === 0">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="3 6 5 6 21 6"/>
            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
          </svg>
          <span>批量删除</span>
        </button>
      </div>
    </div>

    <div class="user-table-container">
      <div class="table-wrapper">
        <table class="user-table">
          <thead>
            <tr>
              <th class="checkbox-cell">
                <input type="checkbox" :checked="isAllSelected" :indeterminate="isIndeterminate" @change="toggleSelectAll"/>
              </th>
              <th class="id-cell">ID</th>
              <th class="username-cell">用户名</th>
              <th class="account-cell">账号</th>
              <th class="mobile-cell">手机号</th>
              <th class="email-cell">邮箱</th>
              <th class="status-cell">状态</th>
              <th class="time-cell">创建时间</th>
              <th class="action-cell">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading && users.length === 0">
              <td colspan="9" class="loading-cell">
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
            <tr v-else-if="users.length === 0">
              <td colspan="9" class="empty-cell">
                <div class="empty-state">
                  <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                    <circle cx="9" cy="7" r="4"/>
                    <line x1="17" y1="11" x2="23" y2="11"/>
                  </svg>
                  <p>暂无用户数据</p>
                </div>
              </td>
            </tr>
            <template v-else>
              <tr v-for="user in users" :key="user.id">
                <td class="checkbox-cell">
                  <input type="checkbox" :checked="selectedUsers.includes(user.id)" @change="toggleSelect(user.id)"/>
                </td>
                <td class="id-cell">{{ user.id }}</td>
                <td class="username-cell">
                  <div class="user-info">
                    <div class="avatar">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0-4 0 0 4v2"/>
                        <circle cx="12" cy="7" r="4"/>
                      </svg>
                    </div>
                    <span>{{ user.username }}</span>
                  </div>
                </td>
                <td class="account-cell">{{ user.account }}</td>
                <td class="mobile-cell">{{ user.mobile || '-' }}</td>
                <td class="email-cell">{{ user.emailAddr || '-' }}</td>
                <td class="status-cell">
                  <span class="status-badge" :class="{ locked: user.locked === 1 }">
                    {{ lockedText(user.locked) }}
                  </span>
                </td>
                <td class="time-cell">{{ formatTime(user.createTime) }}</td>
                <td class="action-cell">
                  <button class="icon-btn edit" @click="openEditModal(user)" title="编辑">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                      <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                    </svg>
                  </button>
                  <button class="icon-btn delete" @click="openDeleteModal(user)" title="删除">
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
            :aria-label="'上一页'"
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="15 18 9 12 15 6"/>
            </svg>
          </button>
          <template v-for="(page, index) in pageNumbers" :key="index">
            <span v-if="page === '...'" class="page-ellipsis" aria-label="省略号">...</span>
            <button
              v-else
              class="page-btn page-btn--number"
              :class="{ 'page-btn--active': page === currentPage }"
              @click="handlePageChange(page)"
              :aria-label="`第 ${page} 页`"
              :aria-current="page === currentPage ? 'page' : undefined"
            >
              {{ page }}
            </button>
          </template>
          <button
            class="page-btn page-btn--next"
            :disabled="currentPage * pageSize >= total"
            @click="handlePageChange(currentPage + 1)"
            :aria-label="'下一页'"
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
        <div class="modal-overlay" v-if="showUserModal" @click.self="closeModal">
          <div class="modal-container">
            <div class="modal-header">
              <h3>
                <svg v-if="modalType === 'create'" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                  <circle cx="8.5" cy="7" r="4"/>
                  <line x1="20" y1="8" x2="20" y2="14"/>
                  <line x1="23" y1="11" x2="17" y2="11"/>
                </svg>
                <svg v-else width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                  <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                </svg>
                {{ modalType === 'create' ? '新增用户' : '编辑用户' }}
              </h3>
              <button class="close-btn" @click="closeModal">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="18" y1="6" x2="6" y2="18"/>
                  <line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
              </button>
            </div>
            <div class="modal-body">
              <div class="form-group" :class="{ error: formErrors.username }">
                <label>
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                    <circle cx="12" cy="7" r="4"/>
                  </svg>
                  用户名 <span class="required">*</span>
                </label>
                <input type="text" v-model="userForm.username" placeholder="请输入用户名" :disabled="modalType === 'edit'"/>
                <span class="error-text" v-if="formErrors.username">{{ formErrors.username }}</span>
              </div>
              <div class="form-group" :class="{ error: formErrors.password }">
                <label>
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                  </svg>
                  密码 <span class="required" v-if="modalType === 'create'">*</span>
                  <span class="optional" v-else>(留空不修改)</span>
                </label>
                <input type="password" v-model="userForm.password" :placeholder="modalType === 'create' ? '请输入密码' : '如需修改请输入新密码'"/>
                <span class="error-text" v-if="formErrors.password">{{ formErrors.password }}</span>
              </div>
              <div class="form-row">
                <div class="form-group">
                  <label>
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72"/>
                    </svg>
                    手机号
                  </label>
                  <input type="tel" v-model="userForm.mobile" placeholder="请输入手机号"/>
                </div>
                <div class="form-group">
                  <label>
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                      <polyline points="22,6 12,13 2,6"/>
                    </svg>
                    邮箱
                  </label>
                  <input type="email" v-model="userForm.emailAddr" placeholder="请输入邮箱"/>
                </div>
              </div>
              <div class="form-group">
                <label>
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                    <path d="M7 11V7a5 5 0 0 1 9.9-1"/>
                  </svg>
                  账户状态
                </label>
                <div class="toggle-group">
                  <label class="toggle-item">
                    <input type="radio" :value="0" v-model="userForm.locked"/>
                    <span class="toggle-label normal">正常</span>
                  </label>
                  <label class="toggle-item">
                    <input type="radio" :value="1" v-model="userForm.locked"/>
                    <span class="toggle-label locked">已锁定</span>
                  </label>
                </div>
              </div>
              <div class="error-message" v-if="formErrors.general">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"/>
                  <line x1="12" y1="8" x2="12" y2="12"/>
                  <line x1="12" y1="16" x2="12.01" y2="16"/>
                </svg>
                {{ formErrors.general }}
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn secondary" @click="closeModal">取消</button>
              <button class="btn primary" @click="saveUser" :disabled="loading">
                <svg v-if="loading" class="spinner" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10" stroke-opacity="0.25"/>
                  <path d="M12 2a10 10 0 0 1 10 10" stroke-linecap="round">
                    <animateTransform attributeName="transform" type="rotate" from="0 12 12" to="360 12 12" dur="1s" repeatCount="indefinite"/>
                  </path>
                </svg>
                <span v-else>{{ modalType === 'create' ? '创建' : '保存' }}</span>
              </button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>

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
                <p v-if="deleteType === 'single'">确定要删除该用户吗？此操作不可恢复。</p>
                <p v-else>确定要批量删除选中的 {{ selectedUsers.length }} 个用户吗？此操作不可恢复。</p>
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



<style>
.user-management {
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
  padding: 12px 24px;
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-color);
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

.action-btn.primary {
  background: var(--accent-primary);
  color: white;
}

.action-btn.primary:hover {
  background: var(--accent-hover);
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
  gap: 8px;
  background: var(--bg-tertiary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  padding: 6px 12px;
  width: 360px;
  transition: all 0.2s ease;
}

.search-box:focus-within {
  border-color: var(--accent-primary);
}

.search-box .search-icon {
  color: var(--text-tertiary);
  flex-shrink: 0;
}

.search-box input {
  flex: 1;
  background: none;
  border: none;
  outline: none;
  color: var(--text-primary);
  font-size: 14px;
}

.search-box input::placeholder {
  color: var(--text-tertiary);
}

.search-btn, .reset-btn {
  padding: 6px 12px;
  border: none;
  border-radius: var(--radius-sm);
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.search-btn {
  background: var(--accent-primary);
  color: white;
}

.reset-btn {
  background: var(--bg-secondary);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}

.search-btn:hover, .reset-btn:hover {
  opacity: 0.9;
}

.user-table-container {
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

.user-table {
  width: 100%;
  border-collapse: collapse;
}

.user-table th, .user-table td {
  padding: 10px 14px;
  text-align: left;
  border-bottom: 1px solid var(--border-light);
}

.user-table th {
  background: var(--bg-tertiary);
  color: var(--text-secondary);
  font-weight: 500;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  position: sticky;
  top: 0;
  z-index: 10;
}

.user-table tbody tr {
  transition: background 0.15s ease;
}

.user-table tbody tr:hover {
  background: var(--bg-tertiary);
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
  width: 70px;
  color: var(--text-tertiary);
  font-size: 13px;
  font-variant-numeric: tabular-nums;
}

.username-cell {
  min-width: 140px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.avatar {
  width: 24px;
  height: 24px;
  background: var(--accent-light);
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--accent-primary);
  font-size: 12px;
}

.account-cell, .mobile-cell, .email-cell {
  min-width: 140px;
}

.status-cell {
  width: 90px;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  background: var(--success-light);
  color: var(--success-color);
}

.status-badge.locked {
  background: var(--danger-light);
  color: var(--danger-color);
}

.action-cell {
  width: 100px;
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

.icon-btn.edit {
  background: var(--accent-light);
  color: var(--accent-primary);
}

.icon-btn.edit:hover {
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
  padding: 60px 20px !important;
}

.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: var(--text-secondary);
}

.spinner {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: var(--text-tertiary);
}

.empty-state svg {
  opacity: 0.4;
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
  font-weight: 400;
  letter-spacing: 0.2px;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

/* 页码按钮基础样式 */
.page-btn {
  width: 32px;
  height: 32px;
  border: 1px solid var(--border-color);
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 500;
  position: relative;
  overflow: hidden;
}

/* 页码按钮悬停效果 */
.page-btn:hover:not(:disabled) {
  background: var(--bg-tertiary);
  color: var(--text-primary);
  border-color: var(--accent-light);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

/* 页码按钮激活状态 */
.page-btn--active {
  background: var(--accent-primary);
  color: white;
  border-color: var(--accent-primary);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
}

.page-btn--active:hover {
  background: var(--accent-hover);
  border-color: var(--accent-hover);
  transform: none;
}

/* 页码按钮禁用状态 */
.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 上一页和下一页按钮样式 */
.page-btn--prev, .page-btn--next {
  background: var(--bg-secondary);
  border-color: var(--border-color);
}

.page-btn--prev:hover:not(:disabled), .page-btn--next:hover:not(:disabled) {
  background: var(--accent-light);
  border-color: var(--accent-primary);
  color: var(--accent-primary);
}

/* 省略号样式 */
.page-ellipsis {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  color: var(--text-tertiary);
  font-size: 14px;
  font-weight: 500;
  opacity: 0.7;
}

/* 每页显示条数选择器样式 */
.page-size-selector {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: 12px;
  padding-left: 12px;
  border-left: 1px solid var(--border-color);
}

.page-size-label {
  color: var(--text-secondary);
  font-size: 13px;
  font-weight: 500;
  white-space: nowrap;
}

.page-size-select {
  padding: 8px 12px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  min-width: 80px;
}

.page-size-select:hover {
  border-color: var(--accent-light);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.page-size-select:focus {
  outline: none;
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

/* 动画效果 */
@keyframes pageBtnPulse {
  0% {
    box-shadow: 0 0 0 0 rgba(59, 130, 246, 0.4);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(59, 130, 246, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(59, 130, 246, 0);
  }
}

.page-btn--active {
  animation: pageBtnPulse 0.6s ease-out;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .pagination {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
    padding: 12px 16px;
  }

  .pagination-info {
    text-align: center;
  }

  .pagination-controls {
    justify-content: center;
  }

  .page-size-selector {
    margin-left: 0;
    padding-left: 0;
    border-left: none;
    margin-top: 4px;
  }

  .page-btn {
    width: 28px;
    height: 28px;
    font-size: 12px;
  }

  .page-ellipsis {
    width: 28px;
    height: 28px;
  }

  .page-size-select {
    padding: 6px 10px;
    font-size: 12px;
  }
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
}

.modal-container {
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
  width: 100%;
  max-width: 460px;
  box-shadow: var(--shadow-lg);
  animation: modalSlideIn 0.25s ease-out;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-16px) scale(0.97);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
}

.modal-header h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.modal-header h3 svg {
  color: var(--accent-primary);
}

.modal-header.danger h3 svg {
  color: var(--danger-color);
}

.close-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: var(--bg-tertiary);
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background: var(--border-color);
  color: var(--text-primary);
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 14px;
}

.form-group label {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
}

.form-group label svg {
  color: var(--accent-primary);
}

.required {
  color: var(--danger-color);
}

.optional {
  color: var(--text-tertiary);
  font-weight: 400;
}

.form-group input {
  width: 100%;
  padding: 10px 12px;
  background: var(--bg-tertiary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  color: var(--text-primary);
  font-size: 13px;
  transition: all 0.2s ease;
}

.form-group input:focus {
  outline: none;
  border-color: var(--accent-primary);
}

.form-group input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.form-group.error input {
  border-color: var(--danger-color);
}

.error-text {
  display: block;
  margin-top: 6px;
  font-size: 12px;
  color: var(--danger-color);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

.toggle-group {
  display: flex;
  gap: 12px;
}

.toggle-item {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.toggle-item input {
  display: none;
}

.toggle-label {
  padding: 9px 16px;
  border-radius: var(--radius-sm);
  font-size: 13px;
  transition: all 0.2s ease;
  background: var(--bg-tertiary);
  color: var(--text-secondary);
}

.toggle-label.normal {
  background: var(--success-light);
  color: var(--success-color);
}

.toggle-label.locked {
  background: var(--danger-light);
  color: var(--danger-color);
}

.toggle-item input:checked + .toggle-label.normal {
  background: var(--success-color);
  color: white;
}

.toggle-item input:checked + .toggle-label.locked {
  background: var(--danger-color);
  color: white;
}

.error-message {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 14px;
  background: var(--danger-light);
  border: 1px solid var(--danger-color);
  border-radius: var(--radius-sm);
  color: var(--danger-color);
  font-size: 13px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 16px 20px;
  border-top: 1px solid var(--border-color);
}

.btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: none;
  border-radius: var(--radius-sm);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn.primary {
  background: var(--accent-primary);
  color: white;
}

.btn.primary:hover:not(:disabled) {
  background: var(--accent-hover);
}

.btn.secondary {
  background: var(--bg-tertiary);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}

.btn.secondary:hover {
  background: var(--border-color);
  color: var(--text-primary);
}

.btn.danger {
  background: var(--danger-color);
  color: white;
}

.btn.danger:hover:not(:disabled) {
  opacity: 0.9;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.delete-modal {
  max-width: 360px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}

.close-btn-absolute {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  border-radius: 50%;
  color: var(--text-tertiary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  z-index: 10;
}

.close-btn-absolute:hover {
  background: var(--bg-tertiary);
  color: var(--text-primary);
  transform: rotate(90deg);
}

.delete-modal .modal-body {
  padding: 40px 32px 20px;
}

.delete-warning {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 0;
}

.delete-icon-wrapper {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: var(--danger-light);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
  color: var(--danger-color);
  animation: pulse-danger 2s infinite;
}

@keyframes pulse-danger {
  0% {
    box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.2);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(239, 68, 68, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(239, 68, 68, 0);
  }
}

.delete-warning h3 {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 12px;
}

.delete-warning p {
  font-size: 15px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin: 0;
  max-width: 280px;
}

.modal-footer.centered {
  justify-content: center;
  padding: 24px 32px 32px;
  border-top: none;
  background: transparent;
  gap: 16px;
}

.btn.large {
  padding: 12px 32px;
  font-size: 15px;
  min-width: 120px;
}

.modal-enter-active, .modal-leave-active {
  transition: all 0.25s ease;
}

.modal-enter-from, .modal-leave-to {
  opacity: 0;
}

.modal-enter-from .modal-container,
.modal-leave-to .modal-container {
  transform: translateY(-16px) scale(0.97);
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

@media (max-width: 1200px) {
  .toolbar, .user-table-container {
    padding-left: 24px;
    padding-right: 24px;
  }

  .search-box {
    width: 320px;
  }
}

@media (max-width: 992px) {
  .user-table th:nth-child(5),
  .user-table td:nth-child(5),
  .user-table th:nth-child(6),
  .user-table td:nth-child(6) {
    display: none;
  }
}

@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
    gap: 14px;
    align-items: flex-start;
  }

  .toolbar-actions {
    width: 100%;
    justify-content: flex-end;
  }

  .search-box {
    width: 100%;
  }

  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
