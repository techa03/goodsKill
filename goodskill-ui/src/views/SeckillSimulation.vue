<script setup>
import {computed, onMounted, onUnmounted, ref, watch} from 'vue'
import {useRouter} from 'vue-router'
import axios from 'axios'
import PageHeader from '../components/PageHeader.vue'
import Chart from 'chart.js/auto'
import SockJS from 'sockjs-client'
import {Client} from '@stomp/stompjs'

const router = useRouter()
const API_BASE = '/api/web'

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

// 秒杀策略枚举
const seckillStrategies = [
  { value: 'sychronized', label: '同步锁' },
  { value: 'redisson', label: 'Redisson分布式锁' },
  { value: 'kafka', label: 'Kafka消息队列' },
  { value: 'procedure', label: '数据库存储过程' },
  { value: 'zookeeperLock', label: 'Zookeeper分布式锁' },
  { value: 'redisReactiveMongo', label: 'Redis响应式+Mongo' },
  { value: 'rabbitmq', label: 'RabbitMQ消息队列' },
  { value: 'limit', label: 'Sentinel限流' },
  { value: 'atomicWithCanal', label: 'Atomic+Canal' }
]

// 状态管理
const selectedStrategy = ref('sychronized')
const requestCount = ref(100)
const seckillId = ref('1001')
const seckillCount = ref(10) // 商品数量，默认10
const isExecuting = ref(false)
const executionResult = ref(null)
const operationLogs = ref([])
const selectedTaskId = ref(null)
const taskDetails = ref(null)
const showDetailsModal = ref(false)
const chartData = ref(null)
const chartInstance = ref(null)
const loading = ref(false)
const taskInfoLoading = ref(false)

// WebSocket状态
const stompClient = ref(null)
const websocketConnected = ref(false)
const reconnectAttempts = ref(0)
const maxReconnectAttempts = 5
const reconnectDelay = 5000 // 5秒



// 执行模拟秒杀
const executeSeckill = async () => {
  if (isExecuting.value) return

  isExecuting.value = true
  loading.value = true
  // 不清除操作日志，保留历史记录
  // operationLogs.value = []
  executionResult.value = null
  chartData.value = null // 清除之前的图表数据
  taskDetails.value = null // 清除之前的任务详情

  try {
    // 记录开始时间
    const startTime = new Date().toISOString()

    // 添加操作日志
    addLog(`开始执行秒杀模拟：${seckillStrategies.find(s => s.value === selectedStrategy.value).label}`, 'info', startTime)

    // 验证输入参数
    if (!seckillId.value || !seckillCount.value || !requestCount.value) {
      throw new Error('请填写完整的秒杀参数')
    }

    if (seckillCount.value <= 0) {
      throw new Error('商品数量必须大于0')
    }

    if (requestCount.value <= 0) {
      throw new Error('请求数量必须大于0')
    }

    // 调用秒杀接口（异步执行，不等待完成）
    const response = await api.post(`/${selectedStrategy.value}`, {
      seckillId: seckillId.value,
      seckillCount: seckillCount.value,
      requestCount: requestCount.value
    })

    if (response.data.code === 0) {
      let taskId = response.data.data

      // 如果后端没有返回任务ID，尝试从/task-info接口获取
      if (!taskId) {
        try {
          addLog('后端未返回任务ID，尝试从task-info接口获取', 'info', new Date().toISOString())
          const taskInfoResponse = await api.post('/task-info', {
            seckillId: seckillId.value
          })
          if (taskInfoResponse.data.code === 0) {
            taskId = taskInfoResponse.data.data
            addLog(`成功获取任务ID：${taskId}`, 'success', new Date().toISOString())
          } else {
            addLog(`获取任务ID失败：${taskInfoResponse.data.msg}`, 'warning', new Date().toISOString())
          }
        } catch (error) {
          console.error('获取任务ID失败:', error)
          addLog(`获取任务ID失败：${error.message}`, 'warning', new Date().toISOString())
        }
      }

      if (taskId) {
        executionResult.value = {
          taskId,
          strategy: selectedStrategy.value,
          requestCount: requestCount.value,
          seckillId: seckillId.value,
          seckillCount: seckillCount.value
        }

        // 记录成功日志
        addLog(`秒杀模拟执行请求已提交，任务ID：${taskId}`, 'success', new Date().toISOString())
        addLog(`任务执行中，请等待...`, 'info', new Date().toISOString())

        // 延迟获取任务信息，给后端一些时间开始执行
        setTimeout(async () => {
          await fetchTaskInfo(taskId)
        }, 3000) // 3秒后开始获取任务信息
      } else {
        // 即使没有任务ID，也记录执行成功
        executionResult.value = {
          taskId: null,
          strategy: selectedStrategy.value,
          requestCount: requestCount.value,
          seckillId: seckillId.value,
          seckillCount: seckillCount.value
        }
        addLog(`秒杀模拟执行请求已提交，但未获取到任务ID`, 'warning', new Date().toISOString())
        addLog(`任务执行中，请等待...`, 'info', new Date().toISOString())
      }
    } else {
      addLog(`秒杀模拟执行失败：${response.data.msg}`, 'error', new Date().toISOString())
    }
  } catch (error) {
    console.error('执行秒杀失败:', error)
    addLog(`执行失败：${error.message}`, 'error', new Date().toISOString())
  } finally {
    isExecuting.value = false
    loading.value = false
  }
}

// 获取任务信息
const fetchTaskInfo = async (taskId) => {
  if (!taskId) return

  taskInfoLoading.value = true
  try {
    // 轮询获取任务信息，直到任务完成或超时
    let isTaskRunning = true
    let retryCount = 0
    const maxRetries = 30 // 最多重试30次
    let retryInterval = 2000 // 初始2秒重试一次
    const maxRetryInterval = 10000 // 最大10秒

    while (isTaskRunning && retryCount < maxRetries) {
      retryCount++

      try {
          const response = await api.get(`/task-info/${taskId}`)
          if (response.data.code === 0) {
            const taskInfo = response.data.data
            taskDetails.value = taskInfo

            // 无论任务是否完成，都生成图表数据
            generateChartData(taskInfo)

            // 检查任务是否仍在运行
            isTaskRunning = taskInfo.isRunning === true

            if (isTaskRunning) {
              // 任务仍在运行，继续轮询
              console.log(`任务 ${taskId} 仍在运行，第 ${retryCount} 次轮询`)

              // 实现指数退避策略
              retryInterval = Math.min(retryInterval * 1.5, maxRetryInterval)

              await new Promise(resolve => setTimeout(resolve, retryInterval))
            } else {
              // 任务已完成，添加成功日志
              addLog(`获取任务信息成功`, 'info', new Date().toISOString())
              break
            }
          } else {
            // API调用成功但返回错误码
            addLog(`获取任务信息失败：${response.data.msg}`, 'error', new Date().toISOString())
            break
          }
        } catch (error) {
          // API调用失败
          console.error('获取任务信息失败:', error)
          addLog(`获取任务信息失败：${error.message}`, 'error', new Date().toISOString())

          // 网络错误时延长重试间隔
          retryInterval = Math.min(retryInterval * 2, maxRetryInterval)

          if (retryCount < maxRetries) {
            await new Promise(resolve => setTimeout(resolve, retryInterval))
          } else {
            break
          }
        }
    }

    if (isTaskRunning) {
      // 任务仍在运行，但已达到最大重试次数
      addLog(`任务 ${taskId} 仍在运行中，已达到最大轮询次数`, 'warning', new Date().toISOString())
    }
  } catch (error) {
    console.error('获取任务信息失败:', error)
    addLog(`获取任务信息失败：${error.message}`, 'error', new Date().toISOString())
  } finally {
    taskInfoLoading.value = false
  }
}

// 生成图表数据
const generateChartData = (timeInfo) => {
  if (!timeInfo) return

  // 处理taskTimeMap不存在的情况
  if (!timeInfo.taskTimeMap) {
    // 如果没有taskTimeMap，但有totalTimeMillis，创建一个简单的图表
    if (timeInfo.totalTimeMillis) {
      chartData.value = {
        labels: ['总耗时'],
        datasets: [
          {
            label: '执行耗时 (s)',
            data: [(timeInfo.totalTimeMillis / 1000).toFixed(2)],
            backgroundColor: 'rgba(59, 130, 246, 0.6)',
            borderColor: 'rgba(59, 130, 246, 1)',
            borderWidth: 1
          }
        ]
      }
    }
    return
  }

  // 处理标签，只保留活动ID和策略信息，移除任务ID
  const labels = Object.keys(timeInfo.taskTimeMap).map(label => {
    // 匹配并提取活动ID和策略信息
    const match = label.match(/活动id:\d+,\s*([^,]+)/);
    if (match) {
      return match[0]; // 只返回"活动id:xxx,策略"部分
    }
    return label;
  });
  const data = Object.values(timeInfo.taskTimeMap).map(time => (time / 1000).toFixed(2))

  // 确保有数据时才创建图表
  if (labels.length > 0 && data.length > 0) {
    chartData.value = {
      labels,
      datasets: [
        {
          label: '执行耗时 (s)',
          data,
          backgroundColor: 'rgba(59, 130, 246, 0.6)',
          borderColor: 'rgba(59, 130, 246, 1)',
          borderWidth: 1
        }
      ]
    }
  } else if (timeInfo.totalTimeMillis) {
    // 如果没有任务时间数据，但有总耗时，创建一个简单的图表
    chartData.value = {
      labels: ['总耗时'],
      datasets: [
        {
          label: '执行耗时 (s)',
          data: [(timeInfo.totalTimeMillis / 1000).toFixed(2)],
          backgroundColor: 'rgba(59, 130, 246, 0.6)',
          borderColor: 'rgba(59, 130, 246, 1)',
          borderWidth: 1
        }
      ]
    }
  }
}

// 创建或更新图表
const updateChart = () => {
  if (!chartData.value) return

  const ctx = document.getElementById('timeChart')
  if (!ctx) return

  // 根据标签数量决定旋转角度，元素少的时候斜度更小
  const labelCount = chartData.value.labels.length
  let rotation = 0
  if (labelCount === 1) {
    rotation = 0
  } else if (labelCount <= 3) {
    rotation = 15
  } else if (labelCount <= 6) {
    rotation = 30
  } else if (labelCount <= 9) {
    rotation = 45
  } else {
    rotation = 60
  }

  try {
    // 销毁旧的图表实例
    if (chartInstance.value) {
      chartInstance.value.destroy()
      chartInstance.value = null
    }

    // 创建新的图表实例
    chartInstance.value = new Chart(ctx, {
      type: 'bar',
      data: chartData.value,
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'top',
          },
          tooltip: {
            mode: 'index',
            intersect: false,
            callbacks: {
              title: function(context) {
                const label = context[0].chart.data.labels[context[0].dataIndex];
                return `执行任务: ${label}`;
              },
              label: function(context) {
                const datasetLabel = context.dataset.label || '';
                const value = context.parsed.y;
                const formattedValue = parseFloat(value).toFixed(3);
                return [
                  `${datasetLabel}: ${formattedValue}s`,
                  `详细耗时: ${formattedValue}秒`,
                  `毫秒数: ${(parseFloat(value) * 1000).toFixed(0)}ms`
                ];
              }
            }
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            title: {
              display: true,
              text: '耗时 (秒)'
            }
          },
          x: {
            title: {
              display: false,
              text: '执行任务'
            },
            ticks: {
              maxRotation: rotation,
              minRotation: rotation,
              autoSkip: false,
              font: {
                size: 10
              },
              padding: 10
            }
          }
        }
      },

    })
  } catch (error) {
    console.error('图表更新失败:', error)
  }
}

// 防抖函数
const debounce = (func, delay) => {
  let timeoutId
  return function(...args) {
    clearTimeout(timeoutId)
    timeoutId = setTimeout(() => func.apply(this, args), delay)
  }
}

// 带防抖的图表更新函数
const debouncedUpdateChart = debounce(updateChart, 300)

// 监听chartData变化
watch(chartData, () => {
  debouncedUpdateChart()
}, { deep: true })

// 添加操作日志
const addLog = (message, type, timestamp) => {
  operationLogs.value.push({
    id: Date.now() + Math.random(),
    message,
    type,
    timestamp
  })

  // 限制日志数量
  if (operationLogs.value.length > 50) {
    operationLogs.value.shift()
  }

  // 自动滚动到日志底部
  scrollToBottom()
}

// 初始化WebSocket连接
const initWebSocket = () => {
  try {
    stompClient.value = new Client({
      webSocketFactory: () => {
        return new SockJS('/api/web/ws/logs')
      },
      onConnect: (frame) => {
        console.log('WebSocket连接成功:', frame)
        websocketConnected.value = true


        // 订阅日志主题
        stompClient.value.subscribe('/topic/logs', (message) => {
          try {
            const logMessage = JSON.parse(message.body)
            // 根据日志级别确定类型
            let logType = 'info'
            if (logMessage.level && logMessage.level.includes('ERROR')) {
              logType = 'error'
            } else if (logMessage.level && logMessage.level.includes('WARN')) {
              logType = 'warning'
            }

            // 添加到操作日志
            addLog(logMessage.message, logType, logMessage.timestamp)
          } catch (error) {
            console.error('解析日志消息失败:', error)
          }
        })
      },
      onStompError: (error) => {
        console.error('WebSocket连接失败:', error)
        websocketConnected.value = false

      },
      onWebSocketError: (error) => {
        console.error('WebSocket错误:', error)
        websocketConnected.value = false

      }
    })

    stompClient.value.activate()
  } catch (error) {
    console.error('初始化WebSocket失败:', error)

  }
}

// 断开WebSocket连接
const disconnectWebSocket = () => {
  if (stompClient.value) {
    stompClient.value.disconnect()
    websocketConnected.value = false

  }
}

// 清空操作日志
const clearLogs = () => {
  operationLogs.value = []
}



// 自动滚动日志
const scrollToBottom = () => {
  setTimeout(() => {
    const logsList = document.querySelector('.logs-list')
    if (logsList) {
      logsList.scrollTop = logsList.scrollHeight
    }
  }, 100)
}

// 查看任务详情
const viewTaskDetails = (taskId) => {
  selectedTaskId.value = taskId
  showDetailsModal.value = true
}



// 关闭详情模态框
const closeDetailsModal = () => {
  showDetailsModal.value = false
  selectedTaskId.value = null
}

// 格式化时间
const formatTime = (timestamp) => {
  if (!timestamp) return '-'
  return new Date(timestamp).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  })
}

// 计算总耗时（转换为秒，保留2位小数）
const totalTime = computed(() => {
  if (!taskDetails.value) return 0
  // 优先使用后端返回的总耗时
  if (taskDetails.value.totalTimeMillis) {
    return (taskDetails.value.totalTimeMillis / 1000).toFixed(2)
  }
  // 备用计算逻辑
  if (taskDetails.value.taskTimeMap) {
    const times = Object.values(taskDetails.value.taskTimeMap)
    const totalMs = times.reduce((sum, time) => sum + time, 0)
    return (totalMs / 1000).toFixed(2)
  }
  return 0
})

// 计算平均耗时（转换为秒，保留2位小数）
const averageTime = computed(() => {
  if (!taskDetails.value) return 0
  // 优先使用后端返回的总耗时计算平均耗时
  if (taskDetails.value.totalTimeMillis && taskDetails.value.taskCount && taskDetails.value.taskCount > 0) {
    return (taskDetails.value.totalTimeMillis / 1000 / taskDetails.value.taskCount).toFixed(2)
  }
  // 备用计算逻辑
  if (taskDetails.value.taskTimeMap) {
    const times = Object.values(taskDetails.value.taskTimeMap)
    if (times.length > 0) {
      const totalMs = times.reduce((sum, time) => sum + time, 0)
      return (totalMs / 1000 / times.length).toFixed(2)
    }
    return 0
  }
  return 0
})

onMounted(() => {
  addLog('系统初始化完成，可以开始执行秒杀模拟', 'info', new Date().toISOString())
  // 初始化WebSocket连接
  initWebSocket()
})

onUnmounted(() => {
  // 销毁图表实例
  if (chartInstance.value) {
    chartInstance.value.destroy()
  }
  // 断开WebSocket连接
  disconnectWebSocket()
})
</script>

<template>
  <div class="seckill-simulation">
    <PageHeader title="秒杀模拟效果展示" :show-back="true" @back="router.push('/dashboard')" />

    <div class="simulation-container">
      <!-- 左侧控制面板 -->
      <div class="control-panel">
        <div class="panel-header">
          <h3>模拟配置</h3>
          <p class="panel-description">选择秒杀策略并执行模拟测试</p>
        </div>

        <div class="form-group">
          <label for="strategy">秒杀策略</label>
          <select
            id="strategy"
            v-model="selectedStrategy"
            class="form-control"
            :disabled="isExecuting"
          >
            <option
              v-for="strategy in seckillStrategies"
              :key="strategy.value"
              :value="strategy.value"
            >
              {{ strategy.label }}
            </option>
          </select>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="seckillId">秒杀活动ID</label>
            <input
              type="text"
              id="seckillId"
              v-model="seckillId"
              class="form-control"
              :disabled="isExecuting"
              placeholder="默认: 1001"
            />
          </div>

          <div class="form-group">
            <label for="seckillCount">商品数量</label>
            <input
              type="number"
              id="seckillCount"
              v-model.number="seckillCount"
              class="form-control"
              :disabled="isExecuting"
              min="1"
              max="10000"
              placeholder="默认: 10"
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="requestCount">请求数量</label>
            <input
              type="number"
              id="requestCount"
              v-model.number="requestCount"
              class="form-control"
              :disabled="isExecuting"
              min="1"
              max="1000"
              placeholder="默认: 100"
            />
          </div>
        </div>

        <div class="action-buttons">
          <button
            class="btn btn-primary execute-btn"
            @click="executeSeckill"
            :disabled="isExecuting"
          >
            <svg v-if="loading" class="spinner" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10" stroke-opacity="0.25"/>
              <path d="M12 2a10 10 0 0 1 10 10" stroke-linecap="round">
                <animateTransform attributeName="transform" type="rotate" from="0 12 12" to="360 12 12" dur="1s" repeatCount="indefinite"/>
              </path>
            </svg>
            <span v-else>执行秒杀模拟</span>
          </button>
        </div>

        <!-- 执行结果 -->
        <div v-if="executionResult" class="execution-result">
          <h4>执行结果</h4>
          <div class="result-info">
            <div class="result-item">
              <span class="label">任务ID:</span>
              <span class="value">{{ executionResult.taskId }}</span>
            </div>
            <div class="result-item">
              <span class="label">策略:</span>
              <span class="value">{{ seckillStrategies.find(s => s.value === executionResult.strategy).label }}</span>
            </div>
            <div class="result-item">
              <span class="label">请求数:</span>
              <span class="value">{{ executionResult.requestCount }}</span>
            </div>
            <div class="result-item">
              <span class="label">商品数量:</span>
              <span class="value">{{ executionResult.seckillCount }}</span>
            </div>
            <div class="result-item">
              <span class="label">秒杀ID:</span>
              <span class="value">{{ executionResult.seckillId }}</span>
            </div>
            <div class="result-actions">
              <button
                class="btn btn-secondary"
                @click="viewTaskDetails(executionResult.taskId)"
                :disabled="taskInfoLoading"
              >
                查看详情
              </button>

            </div>
          </div>
        </div>
      </div>

      <!-- 右侧日志和图表区域 -->
      <div class="results-panel">
        <!-- 操作日志 -->
        <div class="logs-section">
          <div class="section-header">
            <h3>操作日志</h3>
            <div class="log-header-actions">
              <span class="log-count">{{ operationLogs.length }} 条</span>
              <button
                class="btn btn-sm btn-secondary"
                @click="clearLogs"
                :disabled="operationLogs.length === 0"
              >
                清空日志
              </button>
            </div>
          </div>
          <div class="logs-container">
            <div class="logs-list">
              <div
                v-for="log in operationLogs"
                :key="log.id"
                class="log-item"
                :class="`log-${log.type}`"
              >
                <div class="log-time">{{ formatTime(log.timestamp) }}</div>
                <div class="log-content">
                  <span class="log-type-indicator"></span>
                  <span class="log-message">{{ log.message }}</span>
                </div>
              </div>
              <div v-if="operationLogs.length === 0" class="empty-logs">
                <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                  <path d="M12 6v6l4 2"/>
                  <circle cx="12" cy="12" r="10"/>
                </svg>
                <p>暂无操作日志</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 执行耗时图表 -->
        <div v-if="chartData" class="chart-section">
          <div class="section-header">
            <h3>执行耗时统计</h3>
            <div class="time-summary">
              <span class="summary-item">总耗时: {{ totalTime }}s</span>
              <span class="summary-item">平均耗时: {{ averageTime }}s</span>


            </div>
          </div>
          <div class="chart-container">
            <canvas id="timeChart"></canvas>
          </div>
        </div>

        <div v-else-if="executionResult && !chartData" class="loading-chart">
          <div class="loading-spinner">
            <svg class="spinner" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10" stroke-opacity="0.25"/>
              <path d="M12 2a10 10 0 0 1 10 10" stroke-linecap="round">
                <animateTransform attributeName="transform" type="rotate" from="0 12 12" to="360 12 12" dur="1s" repeatCount="indefinite"/>
              </path>
            </svg>
            <span>正在加载耗时统计...</span>
          </div>
        </div>

        <div v-else class="empty-chart">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <line x1="18" y1="20" x2="18" y2="10"/>
            <line x1="12" y1="20" x2="12" y2="4"/>
            <line x1="6" y1="20" x2="6" y2="14"/>
          </svg>
          <p>执行秒杀模拟后将显示耗时统计图表</p>
        </div>
      </div>
    </div>

    <!-- 任务详情模态框 -->
    <Teleport to="body">
      <Transition name="modal">
        <div class="modal-overlay" v-if="showDetailsModal" @click.self="closeDetailsModal">
          <div class="modal-container">
            <div class="modal-header">
              <h3>任务执行详情</h3>
              <button class="close-btn" @click="closeDetailsModal">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="18" y1="6" x2="6" y2="18"/>
                  <line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
              </button>
            </div>
            <div class="modal-body">
              <div v-if="taskDetails" class="task-details">
                <div class="details-section">
                  <h4>基本信息</h4>
                  <div class="details-grid">
                    <div class="detail-item">
                      <span class="label">任务ID:</span>
                      <span class="value">{{ selectedTaskId }}</span>
                    </div>
                    <div class="detail-item">
                      <span class="label">总耗时:</span>
                      <span class="value">{{ totalTime }}s</span>
                    </div>
                    <div class="detail-item">
                      <span class="label">平均耗时:</span>
                      <span class="value">{{ averageTime }}s</span>
                    </div>
                    <div class="detail-item">
                      <span class="label">任务数:</span>
                      <span class="value">{{ taskDetails.taskTimeMap ? Object.keys(taskDetails.taskTimeMap).length : 0 }}</span>
                    </div>
                  </div>
                </div>

                <div class="details-section">
                  <h4>任务耗时详情</h4>
                  <div class="node-times">
                    <div
                      v-for="(time, node) in taskDetails.taskTimeMap"
                      :key="node"
                      class="node-time-item"
                    >
                      <span class="node-name">{{ node }}</span>
                      <div class="time-bar-container">
                        <div
                          class="time-bar"
                          :style="{ width: `${Math.min((time / (totalTime * 1000)) * 100, 100)}%` }"
                        ></div>
                      </div>
                      <span class="time-value">{{ (time / 1000).toFixed(2) }}s</span>
                    </div>
                  </div>
                </div>
              </div>
              <div v-else class="loading-details">
                <div class="loading-spinner">
                  <svg class="spinner" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10" stroke-opacity="0.25"/>
                    <path d="M12 2a10 10 0 0 1 10 10" stroke-linecap="round">
                      <animateTransform attributeName="transform" type="rotate" from="0 12 12" to="360 12 12" dur="1s" repeatCount="indefinite"/>
                    </path>
                  </svg>
                  <span>正在加载详情...</span>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" @click="closeDetailsModal">关闭</button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>


  </div>
</template>

<style>
.seckill-simulation {
  flex: 1;
  background: var(--bg-primary);
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.simulation-container {
  flex: 1;
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 20px;
  padding: 20px;
  min-height: 0;
  overflow: hidden;
}

/* 控制面板优化 */
.control-panel {
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-md);
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  max-height: calc(100vh - 120px);
}

/* 结果面板优化 */
.results-panel {
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-height: 0;
  flex: 1;
  overflow-y: auto;
  max-height: calc(100vh - 120px);
}

/* 执行结果优化 */
.execution-result {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid var(--border-color);
  flex-shrink: 0;
}

/* 图表区域优化 */
.chart-section {
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  padding: 20px;
  box-shadow: var(--shadow-md);
  overflow: hidden;
  flex-shrink: 0;
}

/* MacBook Pro 14寸优化 */
@media (min-width: 1400px) and (max-width: 1500px) {
  .simulation-container {
    grid-template-columns: 260px 1fr;
    gap: 16px;
    padding: 16px;
  }

  .control-panel {
    padding: 20px;
  }

  .results-panel {
    gap: 16px;
  }

  .logs-section {
    padding: 16px;
  }

  .chart-section {
    padding: 16px;
  }
}

/* 大屏幕优化 */
@media (min-width: 1500px) {
  .simulation-container {
    grid-template-columns: 340px 1fr;
    gap: 24px;
    padding: 24px;
  }
}

.control-panel {
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-md);
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.panel-header {
  margin-bottom: 24px;
}

.panel-header h3 {
  margin: 0 0 8px 0;
  color: var(--text-primary);
  font-size: 16px;
  font-weight: 600;
}

.panel-description {
  margin: 0;
  color: var(--text-tertiary);
  font-size: 13px;
}

.form-group {
  margin-bottom: 16px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  color: var(--text-secondary);
  font-weight: 500;
  font-size: 13px;
}

.form-control {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  background: var(--bg-tertiary);
  color: var(--text-primary);
  font-size: 13px;
  transition: all 0.2s ease;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

/* MacBook Pro 14寸优化 */
@media (min-width: 1400px) and (max-width: 1500px) {
  .form-control {
    font-size: 12px;
    padding: 7px 10px;
  }

  .btn {
    font-size: 12px;
    padding: 8px 14px;
  }

  .logs-section {
    height: 380px;
    padding: 16px;
  }

  .logs-container {
    padding: 12px;
    min-height: 280px;
  }

  .log-message {
    font-size: 12px;
  }

  .log-time {
    font-size: 10px;
    width: 120px;
  }

  .chart-container {
    height: 320px;
  }

  .execution-result {
    margin-top: 20px;
    padding-top: 16px;
  }

  .result-item {
    margin-bottom: 8px;
  }

  .result-item .label {
    font-size: 12px;
    min-width: 70px;
  }

  .result-item .value {
    font-size: 12px;
    max-width: 140px;
  }

  .panel-header h3 {
    font-size: 14px;
  }

  .panel-description {
    font-size: 12px;
  }

  .form-group label {
    font-size: 12px;
  }

  .execution-result h4 {
    font-size: 14px;
    margin-bottom: 12px;
  }

  .result-info {
    padding: 12px;
  }

  .chart-section {
    padding: 16px;
  }

  .time-summary {
    gap: 8px;
  }

  .summary-item {
    font-size: 11px;
    padding: 3px 6px;
  }

  .chart-container {
    height: 240px;
    margin-top: 12px;
  }

  .results-panel {
    gap: 20px;
  }

  .control-panel {
    padding: 20px;
  }

  .action-buttons {
    margin: 16px 0;
  }
}

.form-control:focus {
  outline: none;
  border-color: var(--accent-primary);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-control:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.action-buttons {
  margin: 20px 0;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 9px 16px;
  border: none;
  border-radius: var(--radius-md);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  min-width: 80px;
  justify-content: center;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.btn-primary {
  background: var(--accent-primary);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: var(--accent-hover);
  box-shadow: 0 2px 6px rgba(59, 130, 246, 0.3);
  transform: translateY(-1px);
}

.btn-secondary {
  background: var(--bg-tertiary);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}

.btn-secondary:hover:not(:disabled) {
  background: var(--border-color);
  color: var(--text-primary);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn.active {
  background: var(--accent-primary);
  color: white;
  border-color: var(--accent-primary);
}





.execution-result {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid var(--border-color);
}

.execution-result h4 {
  margin: 0 0 14px 0;
  color: var(--text-primary);
  font-size: 15px;
  font-weight: 600;
}

.result-info {
  background: var(--bg-tertiary);
  border-radius: var(--radius-md);
  padding: 14px;
  overflow: hidden;
}

.result-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  flex-wrap: nowrap;
  align-items: center;
}

.result-item:last-child {
  margin-bottom: 14px;
}

.result-item .label {
  color: var(--text-tertiary);
  font-size: 13px;
  flex-shrink: 0;
  min-width: 75px;
}

.result-item .value {
  color: var(--text-primary);
  font-weight: 400;
  font-size: 12px;
  font-family: 'Monaco', 'Menlo', monospace;
  flex-shrink: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 200px;
}

.result-actions {
  margin-top: 8px;
  display: flex;
  justify-content: flex-end;
}

.results-panel {
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-height: 0;
  flex: 1;
  overflow-y: auto;
}

.logs-section {
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-md);
  display: flex;
  flex-direction: column;
  height: 440px;
  position: relative;
  z-index: 1;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h3 {
  margin: 0;
  color: var(--text-primary);
  font-size: 16px;
  font-weight: 600;
}

.log-header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.log-count {
  color: var(--text-tertiary);
  font-size: 13px;
  background: var(--bg-tertiary);
  padding: 4px 8px;
  border-radius: var(--radius-sm);
}

.btn-sm {
  padding: 4px 8px;
  font-size: 11px;
  min-width: 60px;
}

.logs-container {
  flex: 1;
  overflow-y: auto;
  background: var(--bg-tertiary);
  border-radius: var(--radius-md);
  padding: 20px;
  min-height: 300px;
}

.logs-list {
  height: 100%;
  overflow-y: auto;
}

.log-item {
  display: flex;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--border-light);
}

.log-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.log-time {
  width: 130px;
  color: var(--text-tertiary);
  font-size: 11px;
  font-family: 'Monaco', 'Menlo', monospace;
  flex-shrink: 0;
}

.log-content {
  flex: 1;
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.log-type-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-top: 6px;
  flex-shrink: 0;
}

.log-info .log-type-indicator {
  background: var(--accent-primary);
}

.log-success .log-type-indicator {
  background: var(--success-color);
}

.log-error .log-type-indicator {
  background: var(--danger-color);
}

.log-message {
  color: var(--text-secondary);
  font-size: 13px;
  line-height: 1.4;
}

.empty-logs {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: var(--text-tertiary);
}

.empty-logs svg {
  opacity: 0.3;
  margin-bottom: 16px;
}

.chart-section {
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  padding: 20px;
  box-shadow: var(--shadow-md);
  overflow: hidden;
}

.time-summary {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.summary-item {
  color: var(--text-secondary);
  font-size: 12px;
  background: var(--bg-tertiary);
  padding: 4px 8px;
  border-radius: var(--radius-sm);
  white-space: nowrap;
}

.chart-container {
  margin-top: 16px;
  height: 350px;
  position: relative;
  overflow: hidden;
}

.loading-chart, .empty-chart {
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-md);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: var(--text-tertiary);
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

.empty-chart svg {
  opacity: 0.3;
  margin-bottom: 16px;
}

/* 模态框样式 */
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
  width: 90%;
  max-width: 800px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: var(--shadow-xl);
  animation: modalSlideIn 0.3s ease-out;
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
  margin: 0;
  color: var(--text-primary);
  font-size: 18px;
  font-weight: 600;
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
}

.close-btn:hover {
  background: var(--border-color);
  color: var(--text-primary);
  transform: rotate(90deg);
}

.modal-body {
  padding: 24px;
  max-height: 50vh;
  overflow-y: auto;
}

.task-details {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.details-section h4 {
  margin: 0 0 16px 0;
  color: var(--text-primary);
  font-size: 16px;
  font-weight: 600;
}

.details-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-item .label {
  color: var(--text-tertiary);
  font-size: 13px;
}

.detail-item .value {
  color: var(--text-primary);
  font-weight: 500;
  font-family: 'Monaco', 'Menlo', monospace;
}

.node-times {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.node-time-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.node-name {
  width: 120px;
  color: var(--text-secondary);
  font-size: 13px;
  flex-shrink: 0;
}

.time-bar-container {
  flex: 1;
  height: 8px;
  background: var(--bg-tertiary);
  border-radius: 4px;
  overflow: hidden;
}

.time-bar {
  height: 100%;
  background: var(--accent-primary);
  border-radius: 4px;
  transition: width 0.5s ease;
}

.time-value {
  width: 60px;
  color: var(--text-primary);
  font-weight: 500;
  font-family: 'Monaco', 'Menlo', monospace;
  flex-shrink: 0;
  text-align: right;
}

.loading-details {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 300px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  padding: 20px 24px;
  border-top: 1px solid var(--border-color);
  background: var(--bg-tertiary);
}



/* 响应式设计 */
@media (max-width: 1200px) {
  .simulation-container {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 20px;
  }

  .control-panel {
    order: 2;
  }

  .results-panel {
    order: 1;
  }

  .logs-container {
    max-height: 300px;
  }
}

@media (max-width: 768px) {
  .simulation-container {
    padding: 16px;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .time-summary {
    flex-direction: column;
    gap: 8px;
  }

  .details-grid {
    grid-template-columns: 1fr;
  }

  .modal-container {
    width: 95%;
    margin: 20px;
  }

  .node-name {
    width: 100px;
    font-size: 12px;
  }

  .time-value {
    width: 50px;
    font-size: 12px;
  }
}
</style>
