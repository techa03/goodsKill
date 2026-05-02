<template>
  <Teleport to="body">
    <Transition name="dialog-fade">
      <div v-if="visible" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm">
        <div class="glass-card w-full max-w-md rounded-3xl p-8 transform transition-all hover:shadow-lg">
          <!-- Icon -->
          <div class="flex justify-center mb-6">
            <div class="w-20 h-20 rounded-full bg-gradient-to-br from-danger/20 to-danger/10 flex items-center justify-center shadow-lg">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 text-danger" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
            </div>
          </div>

          <!-- Title -->
          <h3 class="text-2xl font-bold text-[var(--text-primary)] text-center mb-4 font-display">{{ title }}</h3>

          <!-- Message -->
          <p class="text-[var(--text-muted)] text-center mb-8 text-lg leading-relaxed">{{ message }}</p>

          <!-- Buttons -->
          <div class="flex gap-4">
            <button
              @click="handleCancel"
              class="flex-1 py-3.5 rounded-xl bg-black/5 dark:bg-white/5 text-[var(--text-secondary)] hover:text-[var(--text-primary)] hover:bg-black/10 dark:hover:bg-white/10 transition-all font-medium text-lg shadow-sm hover:shadow"
            >
              {{ cancelText }}
            </button>
            <button
              @click="handleConfirm"
              class="flex-1 py-3.5 rounded-xl bg-gradient-to-r from-danger to-danger/80 text-white font-medium hover:shadow-danger transition-all text-lg shadow-md hover:shadow-lg"
            >
              {{ confirmText }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '确认操作'
  },
  message: {
    type: String,
    default: '确定要执行此操作吗？'
  },
  confirmText: {
    type: String,
    default: '确定'
  },
  cancelText: {
    type: String,
    default: '取消'
  }
})

const emit = defineEmits(['confirm', 'cancel'])

const handleConfirm = () => {
  emit('confirm')
}

const handleCancel = () => {
  emit('cancel')
}
</script>

<style scoped>
.dialog-fade-enter-active,
.dialog-fade-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.dialog-fade-enter-from,
.dialog-fade-leave-to {
  opacity: 0;
  transform: scale(0.9) translateY(20px);
}

.shadow-danger {
  box-shadow: 0 8px 25px rgba(239, 68, 68, 0.4);
}
</style>
