<script setup>
import {computed, onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import axios from 'axios'
import PageHeader from '../components/PageHeader.vue'

const router = useRouter()
const API_BASE = '/api/seckill'
const COMMON_API_BASE = '/api/common'

const api = axios.create({
  baseURL: API_BASE,
  timeout: 10000
})

const commonApi = axios.create({
  baseURL: COMMON_API_BASE,
  timeout: 10000
})

const setupAxiosInterceptors = (instance) => {
  instance.interceptors.request.use(
    (config) => {
      const token = localStorage.getItem('token')
      if (token) {
        config.headers['access_token'] = token
      }
      return config
    },
    (error) => Promise.reject(error)
  )

  instance.interceptors.response.use(
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
}

setupAxiosInterceptors(api)
setupAxiosInterceptors(commonApi)

const products = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedProducts = ref([])

const showProductModal = ref(false)
const showDeleteModal = ref(false)
const modalType = ref('create')
const deleteProductId = ref(null)

const productForm = ref({
  goodsId: null,
  name: '',
  title: '',
  photoUrl: '',
  price: 0,
  description: ''
})

const formErrors = ref({})
const uploadLoading = ref(false)
const previewImage = ref('')

const fetchProducts = async () => {
  loading.value = true
  try {
    const response = await api.get('/goods/list', {
      params: { page: currentPage.value, size: pageSize.value, keyword: searchKeyword.value }
    })
    if (response.data.code === 0) {
      const records = response.data.data.records
      // 为每个商品获取图片URL
      for (const product of records) {
        if (product.photoUrl) {
          try {
            const imageResponse = await commonApi.get(`/oss/files/download/${product.photoUrl}?responseType=url`)
            if (imageResponse.data.code === 0) {
              product.imageUrl = imageResponse.data.data
            }
          } catch (e) {
            console.error('获取图片URL失败:', e)
            product.imageUrl = ''
          }
        }
      }
      products.value = records
      total.value = response.data.data.total
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchProducts()
}

const resetSearch = () => {
  searchKeyword.value = ''
  currentPage.value = 1
  fetchProducts()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchProducts()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchProducts()
}

const openCreateModal = () => {
  modalType.value = 'create'
  productForm.value = {
    goodsId: null,
    name: '',
    title: '',
    photoUrl: '',
    price: 0,
    description: ''
  }
  formErrors.value = {}
  previewImage.value = ''
  showProductModal.value = true
}

const openEditModal = async (product) => {
  modalType.value = 'edit'
  formErrors.value = {}
  try {
    const response = await api.get(`/goods/${product.goodsId}`)
    if (response.data.code === 0) {
      productForm.value = { ...response.data.data }
      // 如果有图片，生成预览链接
      if (productForm.value.photoUrl) {
        try {
          const previewResponse = await commonApi.get(`/oss/files/download/${productForm.value.photoUrl}?responseType=url`)
          if (previewResponse.data.code === 0) {
            previewImage.value = previewResponse.data.data
          }
        } catch (e) {
          console.error('获取图片预览失败:', e)
          previewImage.value = ''
        }
      }
      showProductModal.value = true
    }
  } catch (error) {
    console.error('获取商品信息失败:', error)
  }
}

const closeModal = () => {
  showProductModal.value = false
  showDeleteModal.value = false
  productForm.value = {
    goodsId: null,
    name: '',
    title: '',
    photoUrl: '',
    price: 0,
    description: ''
  }
  formErrors.value = {}
  previewImage.value = ''
}

const validateForm = () => {
  formErrors.value = {}
  let isValid = true
  if (!productForm.value.name) {
    formErrors.value.name = '请输入商品名称'
    isValid = false
  }
  if (!productForm.value.price || productForm.value.price <= 0) {
    formErrors.value.price = '请输入有效的商品价格'
    isValid = false
  }
  return isValid
}

const saveProduct = async () => {
  if (!validateForm()) return
  loading.value = true
  try {
    let response
    if (modalType.value === 'create') {
      response = await api.post('/goods', productForm.value)
    } else {
      response = await api.put('/goods', productForm.value)
    }
    if (response.data.code === 0) {
      closeModal()
      fetchProducts()
    } else {
      formErrors.value.general = response.data.msg || '操作失败'
    }
  } catch (error) {
    formErrors.value.general = error.response?.data?.msg || '网络错误，请稍后重试'
  } finally {
    loading.value = false
  }
}

const openDeleteModal = (product) => {
  deleteProductId.value = product.goodsId
  showDeleteModal.value = true
}

const confirmDelete = async () => {
  if (!deleteProductId.value) return
  loading.value = true
  try {
    const response = await api.delete(`/goods/${deleteProductId.value}`)
    if (response.data.code === 0) {
      closeModal()
      fetchProducts()
    }
  } catch (error) {
    console.error('删除商品失败:', error)
  } finally {
    loading.value = false
  }
}

const batchDelete = async () => {
  if (selectedProducts.value.length === 0) return
  loading.value = true
  try {
    const response = await api.delete('/goods/batch', { data: selectedProducts.value })
    if (response.data.code === 0) {
      selectedProducts.value = []
      fetchProducts()
    }
  } catch (error) {
    console.error('批量删除失败:', error)
  } finally {
    loading.value = false
  }
}

const isAllSelected = computed(() => products.value.length > 0 && selectedProducts.value.length === products.value.length)
const isIndeterminate = computed(() => selectedProducts.value.length > 0 && selectedProducts.value.length < products.value.length)

const toggleSelect = (productId) => {
  const index = selectedProducts.value.indexOf(productId)
  if (index === -1) selectedProducts.value.push(productId)
  else selectedProducts.value.splice(index, 1)
}

const toggleSelectAll = () => {
  if (selectedProducts.value.length === products.value.length) {
    selectedProducts.value = []
  } else {
    selectedProducts.value = products.value.map(p => p.goodsId)
  }
}

const handleFileUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  uploadLoading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)

    const response = await commonApi.post('/oss/files/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    if (response.data.code === 0) {
      const fileId = response.data.data
      productForm.value.photoUrl = fileId
      // 生成预览链接
      const previewResponse = await commonApi.get(`/oss/files/download/${fileId}?responseType=url`)
      if (previewResponse.data.code === 0) {
        previewImage.value = previewResponse.data.data
      }
    } else {
      formErrors.value.photoUrl = response.data.msg || '文件上传失败'
    }
  } catch (error) {
    console.error('文件上传失败:', error)
    formErrors.value.photoUrl = '文件上传失败，请稍后重试'
  } finally {
    uploadLoading.value = false
    // 清空文件输入，允许重复选择同一个文件
    event.target.value = ''
  }
}

const goBack = () => router.push('/dashboard')

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

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

const totalPages = computed(() => {
  return Math.ceil(total.value / pageSize.value)
})

onMounted(() => {
  fetchProducts()
})
</script>

<template>
  <div class="products-management">
    <PageHeader title="商品管理" :show-back="true" @back="goBack" />

    <div class="toolbar">
      <div class="search-box">
        <svg class="search-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/>
          <line x1="21" y1="21" x2="16.65" y2="16.65"/>
        </svg>
        <input type="text" v-model="searchKeyword" placeholder="搜索商品名称..." @keyup.enter="handleSearch"/>
        <button class="search-btn" @click="handleSearch">搜索</button>
        <button class="reset-btn" @click="resetSearch">重置</button>
      </div>
      <div class="toolbar-actions">
        <button class="action-btn primary" @click="openCreateModal">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19"/>
            <line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          <span>新增商品</span>
        </button>
        <button class="action-btn danger" @click="batchDelete" :disabled="selectedProducts.length === 0">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="3 6 5 6 21 6"/>
            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
          </svg>
          <span>批量删除</span>
        </button>
      </div>
    </div>

    <div class="products-table-container">
      <div class="table-wrapper">
        <table class="products-table">
          <thead>
            <tr>
              <th class="checkbox-cell">
                <input type="checkbox" :checked="isAllSelected" :indeterminate="isIndeterminate" @change="toggleSelectAll"/>
              </th>
              <th class="id-cell">ID</th>
              <th class="name-cell">商品名称</th>
              <th class="price-cell">价格</th>
              <th class="image-cell">商品图片</th>
              <th class="time-cell">创建时间</th>
              <th class="action-cell">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading && products.length === 0">
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
            <tr v-else-if="products.length === 0">
              <td colspan="8" class="empty-cell">
                <div class="empty-state">
                  <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
                  </svg>
                  <p>暂无商品数据</p>
                </div>
              </td>
            </tr>
            <template v-else>
              <tr v-for="product in products" :key="product.goodsId">
                <td class="checkbox-cell">
                  <input type="checkbox" :checked="selectedProducts.includes(product.goodsId)" @change="toggleSelect(product.goodsId)"/>
                </td>
                <td class="id-cell">{{ product.goodsId }}</td>
                <td class="name-cell">{{ product.name }}</td>
                <td class="price-cell">¥{{ product.price }}</td>
                <td class="image-cell">
                  <div class="product-image">
                    <img
                      v-if="product.imageUrl"
                      :src="product.imageUrl"
                      :alt="product.name"
                    />
                    <div class="goods-img-placeholder" v-else>
                      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
                        <circle cx="8.5" cy="8.5" r="1.5"/>
                        <polyline points="21 15 16 10 5 21"/>
                      </svg>
                    </div>
                  </div>
                </td>
                <td class="time-cell">{{ formatTime(product.createTime) }}</td>
                <td class="action-cell">
                  <button class="icon-btn edit" @click="openEditModal(product)" title="编辑">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                      <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                    </svg>
                  </button>
                  <button class="icon-btn delete" @click="openDeleteModal(product)" title="删除">
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
          <button class="page-btn page-btn--prev" :disabled="currentPage === 1" @click="handlePageChange(currentPage - 1)">
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
          <button class="page-btn page-btn--next" :disabled="currentPage * pageSize >= total" @click="handlePageChange(currentPage + 1)">
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
        <div class="modal-overlay" v-if="showProductModal" @click.self="closeModal">
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
                {{ modalType === 'create' ? '新增商品' : '编辑商品' }}
              </h3>
              <button class="close-btn" @click="closeModal">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="18" y1="6" x2="6" y2="18"/>
                  <line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
              </button>
            </div>
            <div class="modal-body">
              <div class="form-group" :class="{ error: formErrors.name }">
                <label>
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                    <circle cx="12" cy="7" r="4"/>
                  </svg>
                  商品名称 <span class="required">*</span>
                </label>
                <input type="text" v-model="productForm.name" placeholder="请输入商品名称" />
                <span class="error-text" v-if="formErrors.name">{{ formErrors.name }}</span>
              </div>

              <div class="form-group">
                <label>
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                    <circle cx="12" cy="7" r="4"/>
                  </svg>
                  商品标题
                </label>
                <input type="text" v-model="productForm.title" placeholder="请输入商品标题" />
              </div>

              <div class="form-group" :class="{ error: formErrors.price }">
                <label>
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"/>
                    <polyline points="12 6 12 12 16 14"/>
                  </svg>
                  商品价格 <span class="required">*</span>
                </label>
                <input type="number" v-model.number="productForm.price" placeholder="请输入商品价格" step="0.01" min="0" />
                <span class="error-text" v-if="formErrors.price">{{ formErrors.price }}</span>
              </div>

              <div class="form-group" :class="{ error: formErrors.photoUrl }">
                <label>
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
                    <circle cx="8.5" cy="8.5" r="1.5"/>
                    <polyline points="21 15 16 10 5 21"/>
                  </svg>
                  商品图片
                </label>
                <div class="file-upload-container">
                  <input
                    type="file"
                    class="file-upload-input"
                    ref="fileInput"
                    @change="handleFileUpload"
                    accept="image/*"
                  />
                  <button class="file-upload-btn" type="button" @click="$refs.fileInput.click()">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                      <polyline points="7 10 12 15 17 10"/>
                      <line x1="12" y1="15" x2="12" y2="3"/>
                    </svg>
                    {{ uploadLoading ? '上传中...' : '选择图片' }}
                  </button>
                </div>
                <div v-if="previewImage" class="image-preview">
                  <img :src="previewImage" :alt="productForm.name" />
                  <button class="remove-image-btn" @click="previewImage = ''; productForm.photoUrl = ''">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <line x1="18" y1="6" x2="6" y2="18"/>
                      <line x1="6" y1="6" x2="18" y2="18"/>
                    </svg>
                  </button>
                </div>
                <span class="error-text" v-if="formErrors.photoUrl">{{ formErrors.photoUrl }}</span>
              </div>

              <div class="form-group">
                <label>
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                    <polyline points="22,6 12,13 2,6"/>
                  </svg>
                  商品描述
                </label>
                <textarea v-model="productForm.description" placeholder="请输入商品描述" rows="3"></textarea>
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
              <button class="btn primary" @click="saveProduct" :disabled="loading">
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
                <h3>确认删除商品</h3>
                <p>您确定要删除该商品吗？此操作将无法撤销，请谨慎操作。</p>
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
.products-management {
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
  gap: 12px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  border: none;
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.action-btn.primary {
  background: var(--accent-primary);
  color: white;
}

.action-btn.primary:hover {
  background: var(--accent-hover);
  box-shadow: 0 2px 6px rgba(59, 130, 246, 0.3);
  transform: translateY(-1px);
}

.action-btn.danger {
  background: var(--danger-light);
  color: var(--danger-color);
  border: 1px solid var(--danger-color);
}

.action-btn.danger:hover:not(:disabled) {
  opacity: 0.9;
  box-shadow: 0 2px 4px rgba(239, 68, 68, 0.2);
  transform: translateY(-1px);
}

.action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 10px;
  background: var(--bg-tertiary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  padding: 8px 16px;
  width: 380px;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.search-box:focus-within {
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.search-box .search-icon {
  color: var(--text-tertiary);
  flex-shrink: 0;
  font-size: 16px;
}

.search-box input {
  flex: 1;
  background: none;
  border: none;
  outline: none;
  color: var(--text-primary);
  font-size: 14px;
  font-weight: 400;
}

.search-box input::placeholder {
  color: var(--text-tertiary);
  font-style: italic;
}

.search-btn, .reset-btn {
  padding: 8px 14px;
  border: none;
  border-radius: var(--radius-sm);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
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
  transform: translateY(-1px);
}

.products-table-container {
  flex: 1;
  padding: 24px;
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
  border-radius: var(--radius-md);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  max-height: calc(100vh - 300px);
  min-height: 400px;
}

.products-table {
  width: 100%;
  border-collapse: collapse;
}

.products-table th, .products-table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid var(--border-light);
}

.products-table th {
  background: var(--bg-tertiary);
  color: var(--text-secondary);
  font-weight: 600;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  position: sticky;
  top: 0;
  z-index: 10;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.products-table tbody tr {
  transition: all 0.2s ease;
}

.products-table tbody tr:hover {
  background: var(--bg-tertiary);
  transform: translateX(2px);
}

.checkbox-cell {
  width: 50px;
}

.checkbox-cell input {
  width: 18px;
  height: 18px;
  accent-color: var(--accent-primary);
  cursor: pointer;
  transition: all 0.2s ease;
}

.checkbox-cell input:hover {
  transform: scale(1.1);
}

.id-cell {
  width: 80px;
  color: var(--text-tertiary);
  font-size: 13px;
  font-variant-numeric: tabular-nums;
  font-family: 'Monaco', 'Menlo', monospace;
}

.name-cell {
  min-width: 200px;
  font-weight: 500;
  color: var(--text-primary);
}

.price-cell {
  width: 110px;
  font-weight: 500;
}

.price-cell {
  color: var(--accent-primary);
}

.image-cell {
  width: 130px;
}

.product-image {
  width: 50px;
  height: 50px;
  overflow: hidden;
  border-radius: var(--radius-md);
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  background-color: var(--bg-tertiary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-image:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.3s ease;
}

.goods-img-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-tertiary);
}

.goods-img-placeholder svg {
  width: 24px;
  height: 24px;
  opacity: 0.5;
}

.time-cell {
  min-width: 160px;
  color: var(--text-tertiary);
  font-size: 13px;
  font-family: 'Monaco', 'Menlo', monospace;
}

.action-cell {
  width: 110px;
}

.icon-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-right: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.icon-btn.edit {
  background: var(--accent-light);
  color: var(--accent-primary);
}

.icon-btn.edit:hover {
  background: var(--accent-primary);
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(59, 130, 246, 0.3);
}

.icon-btn.delete {
  background: var(--danger-light);
  color: var(--danger-color);
}

.icon-btn.delete:hover {
  background: var(--danger-color);
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(239, 68, 68, 0.3);
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

.loading-spinner .spinner {
  width: 40px;
  height: 40px;
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
  gap: 16px;
  color: var(--text-tertiary);
}

.empty-state svg {
  width: 64px;
  height: 64px;
  opacity: 0.3;
}

.empty-state p {
  font-size: 16px;
  font-weight: 400;
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  margin-top: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.pagination-info {
  color: var(--text-tertiary);
  font-size: 14px;
  font-weight: 400;
  letter-spacing: 0.2px;
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
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 500;
  position: relative;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.page-btn:hover:not(:disabled) {
  background: var(--bg-tertiary);
  color: var(--text-primary);
  border-color: var(--accent-light);
  transform: translateY(-2px);
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.1);
}

.page-btn--active {
  background: var(--accent-primary);
  color: white;
  border-color: var(--accent-primary);
  box-shadow: 0 3px 8px rgba(59, 130, 246, 0.3);
  animation: pageBtnPulse 0.6s ease-out;
}

.page-btn--active:hover {
  background: var(--accent-hover);
  border-color: var(--accent-hover);
  transform: none;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.page-btn--prev, .page-btn--next {
  background: var(--bg-secondary);
  border-color: var(--border-color);
}

.page-btn--prev:hover:not(:disabled), .page-btn--next:hover:not(:disabled) {
  background: var(--accent-light);
  border-color: var(--accent-primary);
  color: var(--accent-primary);
}

.page-ellipsis {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  color: var(--text-tertiary);
  font-size: 16px;
  font-weight: 500;
  opacity: 0.7;
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
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
}

.page-size-select {
  padding: 10px 14px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  min-width: 90px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.page-size-select:hover {
  border-color: var(--accent-light);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.page-size-select:focus {
  outline: none;
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

@keyframes pageBtnPulse {
  0% {
    box-shadow: 0 0 0 0 rgba(59, 130, 246, 0.4);
  }
  70% {
    box-shadow: 0 0 0 12px rgba(59, 130, 246, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(59, 130, 246, 0);
  }
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.modal-container {
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  width: 100%;
  max-width: 560px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  animation: modalSlideIn 0.3s ease-out;
  overflow: hidden;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
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
  padding: 20px 24px;
  border-bottom: 1px solid var(--border-color);
  background: var(--bg-tertiary);
}

.modal-header h3 {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.modal-header h3 svg {
  color: var(--accent-primary);
  font-size: 20px;
}

.modal-header.danger h3 svg {
  color: var(--danger-color);
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.close-btn:hover {
  background: var(--border-color);
  color: var(--text-primary);
  transform: rotate(90deg);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);
}

.modal-body {
  padding: 24px;
}

.form-group {
  margin-bottom: 18px;
}

.form-group label {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
}

.form-group label svg {
  color: var(--accent-primary);
  font-size: 16px;
}

.required {
  color: var(--danger-color);
  font-size: 16px;
  font-weight: 600;
}

.optional {
  color: var(--text-tertiary);
  font-weight: 400;
  font-size: 12px;
  margin-left: 4px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  background: var(--bg-tertiary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  color: var(--text-primary);
  font-size: 14px;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  transform: translateY(-1px);
}

.form-group input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.form-group.error input,
.form-group.error textarea {
  border-color: var(--danger-color);
  box-shadow: 0 0 0 2px rgba(239, 68, 68, 0.1);
}

.error-text {
  display: block;
  margin-top: 6px;
  font-size: 13px;
  color: var(--danger-color);
  font-weight: 400;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
}

.file-upload-container {
  display: flex;
  gap: 12px;
  align-items: center;
}

.file-upload-input {
  display: none;
}

.file-upload-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  border: 1px solid var(--border-color);
  background: var(--bg-tertiary);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.file-upload-btn:hover {
  border-color: var(--accent-light);
  color: var(--accent-primary);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transform: translateY(-1px);
}

.image-preview {
  margin-top: 16px;
  position: relative;
  width: 140px;
  height: 140px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.image-preview:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transform: scale(1.02);
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.3s ease;
}

.remove-image-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 28px;
  height: 28px;
  border: none;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 50%;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}

.remove-image-btn:hover {
  background: rgba(0, 0, 0, 0.8);
  transform: scale(1.1);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.error-message {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  background: var(--danger-light);
  border: 1px solid var(--danger-color);
  border-radius: var(--radius-md);
  color: var(--danger-color);
  font-size: 14px;
  margin-top: 16px;
  box-shadow: 0 1px 3px rgba(239, 68, 68, 0.1);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid var(--border-color);
  background: var(--bg-tertiary);
}

.btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border: none;
  border-radius: var(--radius-md);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  min-width: 80px;
  justify-content: center;
}

.btn.primary {
  background: var(--accent-primary);
  color: white;
}

.btn.primary:hover:not(:disabled) {
  background: var(--accent-hover);
  box-shadow: 0 3px 6px rgba(59, 130, 246, 0.3);
  transform: translateY(-1px);
}

.btn.secondary {
  background: var(--bg-secondary);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}

.btn.secondary:hover {
  background: var(--border-color);
  color: var(--text-primary);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.btn.danger {
  background: var(--danger-color);
  color: white;
}

.btn.danger:hover:not(:disabled) {
  opacity: 0.9;
  box-shadow: 0 3px 6px rgba(239, 68, 68, 0.3);
  transform: translateY(-1px);
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
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
  transition: all 0.3s ease;
}

.modal-enter-from, .modal-leave-to {
  opacity: 0;
}

.modal-enter-from .modal-container,
.modal-leave-to .modal-container {
  transform: translateY(-20px) scale(0.95);
}

@media (max-width: 1200px) {
  .toolbar, .products-table-container {
    padding-left: 20px;
    padding-right: 20px;
  }

  .search-box {
    width: 340px;
  }

  .product-image {
    width: 60px;
    height: 60px;
  }
}

@media (max-width: 992px) {
  .products-table th:nth-child(5),
  .products-table td:nth-child(5),
  .products-table th:nth-child(6),
  .products-table td:nth-child(6) {
    display: none;
  }

  .toolbar {
    padding: 14px 20px;
  }

  .search-box {
    width: 300px;
  }
}

@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
    padding: 16px;
  }

  .toolbar-actions {
    width: 100%;
    justify-content: flex-end;
  }

  .search-box {
    width: 100%;
  }

  .products-table-container {
    padding: 16px;
  }

  .table-wrapper {
    max-height: calc(100vh - 320px);
  }

  .products-table th, .products-table td {
    padding: 10px 12px;
    font-size: 13px;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .pagination {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
    padding: 16px;
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
    margin-top: 8px;
    width: 100%;
    justify-content: center;
  }

  .page-btn {
    width: 32px;
    height: 32px;
    font-size: 13px;
  }

  .page-ellipsis {
    width: 32px;
    height: 32px;
  }

  .page-size-select {
    padding: 8px 12px;
    font-size: 13px;
  }

  .modal-container {
    margin: 20px;
    max-width: calc(100vw - 40px);
  }

  .modal-header,
  .modal-body,
  .modal-footer {
    padding: 16px 20px;
  }

  .modal-header h3 {
    font-size: 16px;
  }

  .form-group {
    margin-bottom: 16px;
  }

  .btn {
    padding: 8px 16px;
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .toolbar-actions {
    flex-direction: column;
    gap: 8px;
  }

  .action-btn {
    width: 100%;
    justify-content: center;
  }

  .products-table th:nth-child(4),
  .products-table td:nth-child(4) {
    display: none;
  }

  .product-image {
    width: 50px;
    height: 50px;
  }
}
</style>
