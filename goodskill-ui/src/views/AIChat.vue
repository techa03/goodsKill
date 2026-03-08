<script setup>
import {nextTick, onMounted, onUnmounted, ref} from 'vue'
import {handleUnauthorized} from '../utils/request'
import {getAccessToken} from '../utils/auth'

const messages = ref([])
const inputMessage = ref('')
const isLoading = ref(false)
const eventSource = ref(null)
const isChatOpen = ref(false)
// 计算初始聊天窗口位置，确保完全显示在屏幕内
const calculateInitialPosition = () => {
  const windowWidth = window.innerWidth
  const windowHeight = window.innerHeight
  const chatWidth = 500
  const chatHeight = 700
  
  // 确保窗口完全在屏幕内
  const x = Math.max(0, windowWidth - chatWidth - 20) // 20px 边距
  const y = Math.max(0, windowHeight - chatHeight - 20) // 20px 边距
  
  return { x, y }
}

const chatPosition = ref(calculateInitialPosition())
const iconPosition = ref({ x: window.innerWidth - 88, y: window.innerHeight - 88 })
const isDragging = ref(false)
const isIconDragging = ref(false)
const dragStart = ref({ x: 0, y: 0 })
const dragOffset = ref({ x: 0, y: 0 })
const dragIconOffset = ref({ x: 0, y: 0 })
const chatWindowRef = ref(null)
const iconRef = ref(null)
const chatWindowScale = ref(0)
const chatWindowOpacity = ref(0)
const iconScale = ref(1)

// 发送消息到AI助手
const sendMessage = () => {
  console.log('sendMessage called!', { inputMessage: inputMessage.value, trimmed: inputMessage.value?.trim() })
  if (!inputMessage.value || !inputMessage.value.trim()) {
    console.log('Empty message, returning')
    return
  }

  // 添加用户消息到聊天记录
  const userMessage = {
    id: Date.now(),
    text: inputMessage.value.trim(),
    sender: 'user',
    timestamp: new Date().toLocaleTimeString()
  }
  messages.value.push(userMessage)

  // 清空输入框
  const messageText = inputMessage.value.trim()
  inputMessage.value = ''

  // 滚动到底部
  nextTick(() => {
    scrollToBottom()
  })

  // 开始加载状态
  isLoading.value = true

  // 发送消息到后端API
  const chatId = 'user_' + Date.now()
  console.log('Sending message to API:', {
    chatId,
    userMessage: messageText,
    access_token: getAccessToken()
  })

  fetch(`/api/ai/assistant/chat?chatId=${encodeURIComponent(chatId)}&userMessage=${encodeURIComponent(messageText)}`, {
    method: 'GET',
    headers: {
      'access_token': getAccessToken()
    }
  })
  .then(response => {
    console.log('API Response Status:', response.status)
    console.log('API Response Headers:', Object.fromEntries(response.headers))

    if (response.status === 401) {
      handleUnauthorized()
      return
    }

    if (!response.ok) {
      throw new Error('Failed to send message')
    }

    // 处理SSE响应
    const reader = response.body.getReader()
    const decoder = new TextDecoder('utf-8', { fatal: false, ignoreBOM: true })
    let aiMessageId = Date.now() + 1
    let currentAiMessage = null
    let buffer = ''

    // 队列控制变量
    const textQueue = []
    let isTyping = false
    let isStreamDone = false

    // 创建AI消息对象
    const aiMessage = {
      id: aiMessageId,
      text: '',
      sender: 'ai',
      timestamp: new Date().toLocaleTimeString(),
      isStreaming: true
    }
    messages.value.push(aiMessage)
    // 获取响应式对象引用，确保修改能触发视图更新
    currentAiMessage = messages.value[messages.value.length - 1]
    isLoading.value = false

    console.log('Created AI message object:', currentAiMessage)
    console.log('Current messages array:', messages.value)

    // 滚动到底部
    nextTick(() => {
      scrollToBottom()
    })

    // 处理打字机效果队列
    const processQueue = () => {
      console.log('Processing queue:', {
        queueLength: textQueue.length,
        isTyping,
        isStreamDone,
        currentAiMessage: currentAiMessage ? currentAiMessage.id : null
      })

      if (isTyping) return

      if (textQueue.length === 0) {
        // 如果队列为空且流已结束，标记消息完成
        if (isStreamDone) {
          if (currentAiMessage) {
            console.log('Marking AI message as completed:', currentAiMessage.id)
            currentAiMessage.isStreaming = false
          }
          isLoading.value = false
          nextTick(() => {
            scrollToBottom()
          })
        }
        return
      }

      isTyping = true
      const text = textQueue.shift()
      let index = 0
      const typingSpeed = 30 // 打字速度（毫秒/字符）

      console.log('Starting to type text:', text)

      const typeChar = () => {
        if (index < text.length) {
          if (currentAiMessage) {
            console.log('Adding character:', text[index], 'to message:', currentAiMessage.id)
            currentAiMessage.text += text[index]
            console.log('Current message text:', currentAiMessage.text)
          }
          index++

          // 每几个字符滚动一次，优化性能
          if (index % 5 === 0) {
            nextTick(() => {
              scrollToBottom()
            })
          }

          setTimeout(typeChar, typingSpeed)
        } else {
          isTyping = false
          console.log('Finished typing text:', text)
          nextTick(() => {
            scrollToBottom()
          })
          // 继续处理队列中的下一段文本
          processQueue()
        }
      }

      typeChar()
    }

    // 添加文本到队列
    const addTextToMessage = (text) => {
      if (!text) return
      console.log('Adding text to queue:', text)
      textQueue.push(text)
      console.log('Queue length after adding:', textQueue.length)
      processQueue()
    }

    // 处理SSE事件数据
    const processSSEEvent = (data) => {
      if (!data) return

      console.log('Received SSE data:', data)

      try {
        // 清理数据，移除可能的乱码和特殊字符
        const cleanedData = data.replace(/[\x00-\x1F\x7F]/g, '').trim()
        if (!cleanedData) {
          console.log('Empty data after cleaning')
          return
        }

        console.log('Cleaned SSE data:', cleanedData)

        // 尝试解析JSON
        try {
          const parsedData = JSON.parse(cleanedData)
          console.log('Parsed JSON data:', parsedData)
          const text = parsedData.text || parsedData.message || parsedData.content || cleanedData
          console.log('Extracted text:', text)
          if (text) {
            addTextToMessage(text)
          } else {
            console.log('No text found in parsed data')
            addTextToMessage('收到消息，但内容为空')
          }
        } catch (jsonError) {
          console.log('Not JSON, processing as text:', cleanedData)
          // 如果不是JSON，直接作为文本处理
          addTextToMessage(cleanedData)
        }
      } catch (e) {
        console.error('Error processing SSE event:', e)
        addTextToMessage('处理消息时出现错误，请稍后重试')
      }
    }

    const readChunk = () => {
      reader.read().then(({ done, value }) => {
        console.log('Reading chunk:', {
          done,
          value: value ? `Length: ${value.length}` : null
        })

        if (done) {
          // 处理剩余的缓冲区内容
          if (buffer) {
            console.log('Processing remaining buffer:', buffer)
            const lines = buffer.split('\n')
            lines.forEach(line => {
              if (line.startsWith('data:')) {
                const data = line.substring(5).trim()
                if (data) processSSEEvent(data)
              }
            })
          }

          isStreamDone = true
          console.log('Stream done, setting isStreamDone to true')
          // 尝试触发队列处理以完成状态更新
          processQueue()
          return
        }

        // 处理接收到的chunk
        const chunk = decoder.decode(value, { stream: true })
        console.log('Decoded chunk:', chunk)
        buffer += chunk

        // 处理SSE事件（按换行符分割）
        const lines = buffer.split('\n')
        console.log('Processing lines:', lines.length)
        for (let i = 0; i < lines.length - 1; i++) {
          const line = lines[i]
          if (line.startsWith('data:')) {
            // 提取SSE data字段
            const data = line.substring(5).trim()
            if (data) {
              processSSEEvent(data)
            }
          } else if (line === '') {
            // 空行表示事件结束
            // buffer = ''
          }
        }

        // 保留最后一行作为缓冲区
        buffer = lines[lines.length - 1]
        console.log('Buffer after processing:', buffer)

        // 继续读取
        readChunk()
      })
    }

    console.log('Starting to read SSE stream')
    readChunk()
  })
  .catch(error => {
    console.error('Error sending message:', error)
    isLoading.value = false

    // 添加错误消息到聊天记录
    const errorMessage = {
      id: Date.now() + 2,
      text: '发送消息失败，请稍后重试',
      sender: 'system',
      timestamp: new Date().toLocaleTimeString()
    }
    messages.value.push(errorMessage)
    // 滚动到底部
    nextTick(() => {
      scrollToBottom()
    })
  })
}

// 处理回车键发送消息
const handleKeyPress = (event) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    sendMessage()
  }
}

// 滚动到底部
const scrollToBottom = () => {
  const chatBody = document.querySelector('.chat-body')
  if (chatBody) {
    chatBody.scrollTop = chatBody.scrollHeight
  }
}

// 清空聊天记录
const clearChat = () => {
  messages.value = []
}

// 切换聊天窗口显示状态
const toggleChat = () => {
  isChatOpen.value = !isChatOpen.value
  
  if (isChatOpen.value) {
    // 动画打开聊天窗口
    chatWindowScale.value = 0
    chatWindowOpacity.value = 0
    
    nextTick(() => {
      chatWindowScale.value = 1
      chatWindowOpacity.value = 1
      scrollToBottom()
    })
  }
}

// 拖拽开始
const startDrag = (event) => {
  if (event.button !== 0) return
  isDragging.value = true
  const rect = chatWindowRef.value?.getBoundingClientRect()
  if (rect) {
    dragOffset.value = {
      x: event.clientX - rect.left,
      y: event.clientY - rect.top
    }
  }
  event.preventDefault()
  event.stopPropagation()
}

// 图标拖拽开始
const startDragIcon = (event) => {
  if (event.button !== 0) return
  isIconDragging.value = true
  dragStart.value = { x: event.clientX, y: event.clientY }
  const rect = iconRef.value?.getBoundingClientRect()
  if (rect) {
    dragIconOffset.value = {
      x: event.clientX - rect.left,
      y: event.clientY - rect.top
    }
  }
  event.preventDefault()
  event.stopPropagation()
}

// 拖拽移动
const onDrag = (event) => {
  if (isDragging.value) {
    requestAnimationFrame(() => {
      const windowWidth = window.innerWidth
      const windowHeight = window.innerHeight
      const rect = chatWindowRef.value?.getBoundingClientRect()
      const chatWidth = rect?.width || 400
      const chatHeight = rect?.height || 550
      let newX = event.clientX - dragOffset.value.x
      let newY = event.clientY - dragOffset.value.y
      newX = Math.max(0, Math.min(newX, windowWidth - chatWidth))
      newY = Math.max(0, Math.min(newY, windowHeight - chatHeight))
      chatPosition.value = { x: newX, y: newY }
    })
  } else if (isIconDragging.value) {
    requestAnimationFrame(() => {
      const windowWidth = window.innerWidth
      const windowHeight = window.innerHeight
      const rect = iconRef.value?.getBoundingClientRect()
      const iconWidth = rect?.width || 56
      const iconHeight = rect?.height || 56
      let newX = event.clientX - dragIconOffset.value.x
      let newY = event.clientY - dragIconOffset.value.y
      newX = Math.max(0, Math.min(newX, windowWidth - iconWidth))
      newY = Math.max(0, Math.min(newY, windowHeight - iconHeight))
      iconPosition.value = { x: newX, y: newY }
    })
  }
}

// 拖拽结束
const endDrag = (event) => {
  if (isIconDragging.value) {
    const dragDistance = Math.sqrt(
      Math.pow(event.clientX - dragStart.value.x, 2) +
      Math.pow(event.clientY - dragStart.value.y, 2)
    )
    if (dragDistance < 5) {
      toggleChat()
    }
  }
  isDragging.value = false
  isIconDragging.value = false
}

// 窗口大小变化处理函数
const handleResize = () => {
  chatPosition.value = calculateInitialPosition()
}

// 初始化AI聊天界面
onMounted(() => {
  // 添加欢迎消息
  const welcomeMessage = {
    id: 1,
    text: '你好！我是AI聊天助手，可以帮你执行秒杀操作、查询系统状态等。请问有什么可以帮助你的？',
    sender: 'ai',
    timestamp: new Date().toLocaleTimeString()
  }
  messages.value.push(welcomeMessage)

  // 添加全局鼠标移动监听
  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', endDrag)
  
  // 添加窗口大小变化监听
  window.addEventListener('resize', handleResize)
})

// 清理资源
onUnmounted(() => {
  if (eventSource.value) {
    eventSource.value.close()
  }

  // 移除全局鼠标监听
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', endDrag)
  
  // 移除窗口大小变化监听
  window.removeEventListener('resize', handleResize)
})
</script>

<template>
  <!-- AI助手唤醒图标 -->
  <div 
    ref="iconRef"
    class="ai-assistant-icon" 
    :class="{ 'is-active': isChatOpen, 'dragging': isIconDragging }"
    :style="{
      left: `${iconPosition.x}px`,
      top: `${iconPosition.y}px`
    }"
    @mousedown="startDragIcon"
    @mouseenter="iconScale = 1.1"
    @mouseleave="iconScale = 1"
  >
    <div class="icon-inner" :style="{ transform: `scale(${iconScale})` }">
      <svg v-if="!isChatOpen" width="30" height="30" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M12 8V4H8"></path>
        <rect width="16" height="12" x="4" y="8" rx="2"></rect>
        <path d="M2 14h2"></path>
        <path d="M20 14h2"></path>
        <path d="M15 13v2"></path>
        <path d="M9 13v2"></path>
      </svg>
      <svg v-else width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
        <line x1="18" y1="6" x2="6" y2="18"></line>
        <line x1="6" y1="6" x2="18" y2="18"></line>
      </svg>
    </div>
    <div class="pulse-ring"></div>
    <div class="icon-glow"></div>
  </div>

  <!-- AI聊天窗口 -->
  <div
    v-if="isChatOpen"
    ref="chatWindowRef"
    class="ai-chat-window"
    :class="{ dragging: isDragging }"
    :style="{
      left: `${chatPosition.x}px`,
      top: `${chatPosition.y}px`,
      transform: `scale(${chatWindowScale}) translateZ(0)`,
      opacity: chatWindowOpacity,
      transition: 'transform 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275), opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1), box-shadow 0.3s cubic-bezier(0.4, 0, 0.2, 1)'
    }"
  >
    <!-- 聊天窗口头部（可拖拽区域） -->
    <div
      class="chat-header"
      @mousedown="startDrag"
    >
      <div class="header-left">
        <h2 class="chat-title">AI聊天助手</h2>
      </div>
      <div class="header-right">
        <button class="btn btn-sm btn-outline" @click="clearChat">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="3 6 5 6 21 6"></polyline>
            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
            <line x1="10" y1="11" x2="10" y2="17"></line>
            <line x1="14" y1="11" x2="14" y2="17"></line>
          </svg>
          清空聊天
        </button>
        <button class="close-button" @click="isChatOpen = false">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
            <line x1="18" y1="6" x2="6" y2="18"></line>
            <line x1="6" y1="6" x2="18" y2="18"></line>
          </svg>
        </button>
      </div>
    </div>

    <!-- 聊天主体 -->
    <div class="chat-body">
      <div v-if="messages.length === 0" class="empty-state">
        <div class="empty-icon">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1">
            <path d="M12 8V4H8"></path>
            <rect width="16" height="12" x="4" y="8" rx="2"></rect>
            <path d="M2 14h2"></path>
            <path d="M20 14h2"></path>
            <path d="M15 13v2"></path>
            <path d="M9 13v2"></path>
          </svg>
        </div>
        <h3>开始与AI助手聊天</h3>
        <p>输入消息开始与AI助手对话，它可以帮助你执行秒杀操作、查询系统状态等。</p>
        <div class="quick-actions">
          <button class="quick-action-btn" @click="inputMessage = '帮我执行一次秒杀操作'">执行秒杀</button>
          <button class="quick-action-btn" @click="inputMessage = '查询系统状态'">系统状态</button>
          <button class="quick-action-btn" @click="inputMessage = '如何使用这个系统'">使用指南</button>
        </div>
      </div>

      <div v-else class="messages-list">
        <div
          v-for="message in messages"
          :key="message.id"
          class="message"
          :class="[message.sender, { 'system-message': message.sender === 'system', 'streaming': message.isStreaming }]"
        >
          <div v-if="message.sender === 'ai'" class="message-avatar ai-avatar">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M12 8V4H8"></path>
              <rect width="16" height="12" x="4" y="8" rx="2"></rect>
              <path d="M2 14h2"></path>
              <path d="M20 14h2"></path>
              <path d="M15 13v2"></path>
              <path d="M9 13v2"></path>
            </svg>
          </div>
          <div v-else-if="message.sender === 'user'" class="message-avatar user-avatar">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
              <circle cx="12" cy="7" r="4"></circle>
            </svg>
          </div>
          <div v-else class="message-avatar system-avatar">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10"></circle>
              <line x1="12" y1="8" x2="12" y2="12"></line>
              <line x1="12" y1="16" x2="12.01" y2="16"></line>
            </svg>
          </div>
          <div class="message-content">
            <div class="message-text" :class="{ 'streaming-text': message.isStreaming }" v-html="message.text"></div>
            <div class="message-meta">
              <div class="message-time">{{ message.timestamp }}</div>
              <div v-if="message.isStreaming" class="streaming-indicator">
                <span class="streaming-dot"></span>
                <span class="streaming-dot"></span>
                <span class="streaming-dot"></span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="isLoading" class="loading-message">
          <div class="loading-spinner"></div>
          <span>AI正在思考...</span>
        </div>
      </div>
    </div>

    <!-- 聊天底部 -->
    <div class="chat-footer">
      <div class="input-container">
        <textarea
          v-model="inputMessage"
          @keydown="handleKeyPress"
          @input="console.log('Input changed:', inputMessage.value)"
          placeholder="输入消息..."
          class="message-input"
          rows="1"
          :disabled="isLoading"
        ></textarea>
        <button
          @click="console.log('Button clicked!', { inputMessage: inputMessage.value, isLoading: isLoading.value }); sendMessage()"
          class="send-button"
          :disabled="isLoading"
        >
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="22" y1="2" x2="11" y2="13"></line>
            <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
          </svg>
        </button>
      </div>
      <div class="chat-tips">
        <span>💡 提示：你可以询问AI助手执行秒杀操作、查询系统状态等</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* AI助手唤醒图标 */
.ai-assistant-icon {
  position: fixed;
  width: 56px;
  height: 56px;
  border-radius: 18px;
  background: rgba(15, 23, 42, 0.85);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.4);
  cursor: pointer;
  z-index: 1000;
  transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  user-select: none;
}

.ai-assistant-icon.dragging {
  transition: none;
  cursor: grabbing;
  opacity: 0.9;
  transform: scale(1.05);
}

.ai-assistant-icon:hover {
  transform: scale(1.1) translateY(-4px);
  background: rgba(30, 41, 59, 0.9);
  border-color: var(--accent-primary);
  box-shadow: 0 12px 40px rgba(56, 189, 248, 0.3);
}

.ai-assistant-icon.is-active {
  background: var(--accent-primary);
  border-color: rgba(255, 255, 255, 0.2);
  transform: rotate(90deg) scale(0.9);
}

.icon-inner {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pulse-ring {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 16px;
  background: var(--accent-primary);
  opacity: 0;
  z-index: 1;
  animation: pulse-ring-new 3s infinite;
}

@keyframes pulse-ring-new {
  0% { transform: scale(0.9); opacity: 0; }
  50% { opacity: 0.2; }
  100% { transform: scale(1.4); opacity: 0; }
}

.icon-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  height: 100%;
  background: radial-gradient(circle, var(--accent-primary) 0%, transparent 70%);
  opacity: 0.2;
  filter: blur(10px);
  pointer-events: none;
}

/* AI聊天窗口 */
.ai-chat-window {
  position: fixed;
  width: 500px;
  max-width: 95vw;
  height: 700px;
  max-height: 90vh;
  background: white;
  border-radius: 16px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15), 0 0 0 1px rgba(255, 255, 255, 0.2) inset;
  z-index: 999;
  overflow: hidden;
  transition: opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1), transform 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275), box-shadow 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  will-change: left, top;
  backface-visibility: hidden;
  transform-style: preserve-3d;
}

.ai-chat-window:hover {
  box-shadow: 0 16px 50px rgba(0, 0, 0, 0.2), 0 0 0 1px rgba(255, 255, 255, 0.2) inset;
}

.ai-chat-window.dragging {
  transition: none;
  cursor: move;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.25), 0 0 0 1px rgba(255, 255, 255, 0.2) inset;
  transform: scale(0.98);
}

/* 聊天头部 */
.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: linear-gradient(135deg, var(--bg-secondary) 0%, var(--accent-light) 100%);
  backdrop-filter: blur(20px);
  color: var(--text-primary);
  cursor: move;
  user-select: none;
  position: relative;
  z-index: 10;
  border-bottom: 1px solid var(--border-color);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ai-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background: var(--accent-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: var(--shadow-md);
}

.chat-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
  letter-spacing: -0.02em;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.close-button {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--bg-tertiary);
  border: none;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: var(--shadow-sm);
}

.close-button:hover {
  background: var(--border-color);
  color: var(--text-primary);
  transform: scale(1.05);
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
}

.btn-outline {
  background: var(--bg-tertiary);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}

.btn-outline:hover {
  background: var(--border-color);
  color: var(--text-primary);
  transform: translateY(-1px);
}

.btn-sm {
  padding: 4px 10px;
  font-size: 11px;
}

/* 聊天主体 */
.chat-body {
  flex: 1;
  padding: 16px 0;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 1;
  scroll-behavior: smooth;
  background: #f8fafc;
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 0 16px;
  width: 100%;
}

.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: #64748b;
  padding: 24px;
}

.empty-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 18px;
  font-weight: 600;
  color: #0f172a;
  margin: 0 0 8px 0;
}

.empty-state p {
  font-size: 14px;
  line-height: 1.6;
  margin: 0 0 24px 0;
  color: #64748b;
}

.quick-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: center;
}

.quick-action-btn {
  padding: 8px 16px;
  border-radius: 100px;
  border: 1px solid rgba(226, 232, 240, 0.6);
  background: white;
  color: #475569;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 4px rgba(0,0,0,0.02);
}

.quick-action-btn:hover {
  border-color: #0ea5e9;
  color: #0ea5e9;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(14, 165, 233, 0.1);
}

.message {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  animation: messageSlideIn 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes messageSlideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

.ai-avatar {
  background: linear-gradient(135deg, #0ea5e9 0%, #2563eb 100%);
  color: white;
}

.user-avatar {
  background: white;
  color: #4f46e5;
  border: 1px solid rgba(226, 232, 240, 0.8);
}

.system-avatar {
  background: #fecaca;
  color: #dc2626;
}

.message-content {
  max-width: 75%;
  padding: 12px 16px;
  border-radius: 12px;
  position: relative;
  line-height: 1.6;
  font-size: 14px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.02);
}

.message.user .message-content {
  background: #4f46e5;
  color: white;
  border-radius: 12px 12px 4px 12px;
}

.message.ai .message-content {
  background: white;
  color: #334155;
  border-radius: 12px 12px 12px 4px;
  border: 1px solid rgba(241, 245, 249, 0.8);
}

.message.system-message .message-content {
  background: #fef2f2;
  color: #b91c1c;
  border: 1px solid #fecaca;
  font-size: 13px;
  padding: 8px 12px;
}

.message-text {
  white-space: normal;
  word-break: break-word;
  line-height: 1.5;
  text-align: left;
  letter-spacing: normal;
}

.message-text * {
  max-width: 100%;
  box-sizing: border-box;
}

.message-text p {
  margin: 0 0 8px 0;
  line-height: 1.5;
}

.message-text p:last-child {
  margin-bottom: 0;
}

.message-text h1,
.message-text h2,
.message-text h3,
.message-text h4,
.message-text h5,
.message-text h6 {
  margin: 12px 0 8px 0;
  font-weight: 600;
  line-height: 1.3;
}

.message-text h1 {
  font-size: 18px;
}

.message-text h2 {
  font-size: 16px;
}

.message-text h3,
.message-text h4,
.message-text h5,
.message-text h6 {
  font-size: 14px;
}

.message-text ul,
.message-text ol {
  margin: 8px 0;
  padding-left: 24px;
}

.message-text li {
  margin: 4px 0;
  line-height: 1.4;
}

.message-text strong,
.message-text b {
  font-weight: 600;
}

.message-text em,
.message-text i {
  font-style: italic;
}

.message-text code {
  background: rgba(148, 163, 184, 0.1);
  padding: 2px 4px;
  border-radius: 4px;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 12px;
  white-space: nowrap;
}

.message-text pre {
  background: rgba(148, 163, 184, 0.1);
  padding: 12px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 8px 0;
}

.message-text pre code {
  background: transparent;
  padding: 0;
  white-space: pre;
}

.message-text a {
  color: #0ea5e9;
  text-decoration: none;
  transition: color 0.2s ease;
}

.message-text a:hover {
  color: #2563eb;
  text-decoration: underline;
}

.message-text img {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 8px 0;
}

.message-text table {
  width: 100%;
  border-collapse: collapse;
  margin: 8px 0;
}

.message-text th,
.message-text td {
  padding: 6px 10px;
  border: 1px solid rgba(226, 232, 240, 0.8);
  text-align: left;
}

.message-text th {
  background: rgba(241, 245, 249, 0.8);
  font-weight: 600;
}

.message.user .message-text a {
  color: #93c5fd;
}

.message.user .message-text a:hover {
  color: #bfdbfe;
}

.message.user .message-text code {
  background: rgba(255, 255, 255, 0.1);
}

.message.user .message-text pre {
  background: rgba(255, 255, 255, 0.1);
}

.message.user .message-text th {
  background: rgba(255, 255, 255, 0.1);
}

.message.user .message-text th,
.message.user .message-text td {
  border-color: rgba(255, 255, 255, 0.2);
}

.message-meta {
  margin-top: 4px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 4px;
  opacity: 0.6;
}

.message-time {
  font-size: 11px;
  font-weight: 500;
}

/* 流式指示器 */
.streaming-indicator {
  display: inline-flex;
  gap: 2px;
}

.streaming-dot {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background-color: currentColor;
  animation: bounce 1.4s infinite ease-in-out both;
}

.streaming-dot:nth-child(1) { animation-delay: -0.32s; }
.streaming-dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

/* 打字机光标 */
.streaming-text::after {
  content: '';
  display: inline-block;
  width: 4px;
  height: 14px;
  background-color: #0ea5e9;
  margin-left: 2px;
  vertical-align: middle;
  animation: blink 1s step-end infinite;
}

@keyframes blink {
  50% { opacity: 0; }
}

.loading-message {
  align-self: flex-start;
  margin-left: 44px; /* 32px avatar + 12px gap */
  display: flex;
  align-items: center;
  gap: 6px;
  color: #64748b;
  font-size: 12px;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.5);
  padding: 6px 12px;
  border-radius: 100px;
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.loading-spinner {
  width: 12px;
  height: 12px;
  border: 2px solid rgba(14, 165, 233, 0.2);
  border-top-color: #0ea5e9;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 聊天底部 */
.chat-footer {
  padding: 16px 20px;
  background: white;
  border-top: 1px solid rgba(226, 232, 240, 0.8);
  position: relative;
  z-index: 10;
}

.input-container {
  position: relative;
  background: #f1f5f9;
  border-radius: 24px;
  border: 1px solid rgba(226, 232, 240, 0.8);
  transition: all 0.3s ease;
}

.input-container:focus-within {
  box-shadow: 0 0 0 3px rgba(14, 165, 233, 0.1);
  border-color: #0ea5e9;
  transform: translateY(-1px);
}

.message-input {
  width: 100%;
  padding: 12px 48px 12px 20px;
  border: none;
  background: transparent;
  font-size: 14px;
  line-height: 1.6;
  resize: none;
  max-height: 100px;
  border-radius: 24px;
  color: #0f172a;
  outline: none;
}

.message-input::placeholder {
  color: #94a3b8;
}

.send-button {
  position: absolute;
  right: 4px;
  top: 50%;
  transform: translateY(-50%);
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #0ea5e9 0%, #2563eb 100%);
  color: white;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(14, 165, 233, 0.3);
  z-index: 10;
  pointer-events: auto;
}

.send-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #2563eb 0%, #0ea5e9 100%);
  transform: translateY(-50%) scale(1.05);
  box-shadow: 0 6px 16px rgba(14, 165, 233, 0.4);
}

.send-button:disabled {
  background: #e2e8f0;
  color: #94a3b8;
  cursor: not-allowed;
  box-shadow: none;
}

.chat-tips {
  margin-top: 8px;
  text-align: center;
  font-size: 11px;
  color: #94a3b8;
  font-weight: 500;
}

/* 滚动条美化 */
.chat-body::-webkit-scrollbar {
  width: 4px;
}

.chat-body::-webkit-scrollbar-track {
  background: transparent;
}

.chat-body::-webkit-scrollbar-thumb {
  background: rgba(203, 213, 225, 0.5);
  border-radius: 10px;
}

.chat-body::-webkit-scrollbar-thumb:hover {
  background: rgba(148, 163, 184, 0.8);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .ai-chat-window {
    width: 90vw;
    height: 70vh;
  }

  .chat-header {
    padding: 12px 16px;
  }

  .chat-body {
    padding: 12px 0;
  }

  .messages-list {
    padding: 0 12px;
  }

  .chat-footer {
    padding: 12px 16px;
  }

  .message-content {
    max-width: 80%;
  }

  .ai-assistant-icon {
    width: 50px;
    height: 50px;
    right: 16px;
    bottom: 16px;
  }

  .ai-assistant-icon svg {
    width: 32px;
    height: 32px;
  }
}

@media (max-width: 480px) {
  .ai-chat-window {
    width: 95vw;
    height: 80vh;
  }

  .message-content {
    max-width: 85%;
  }

  .message-text {
    font-size: 13px;
  }
}
</style>
