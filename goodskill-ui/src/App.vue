<script setup>
import {computed, ref} from 'vue'
import {RouterView, useRoute} from 'vue-router'
import Sidebar from './components/Sidebar.vue'

const sidebarCollapsed = ref(false)
const route = useRoute()
const isLoginPage = computed(() => route.path === '/login')
</script>

<template>
  <div class="app-container">
    <template v-if="!isLoginPage">
      <Sidebar v-model:collapsed="sidebarCollapsed" />
      <main class="main-content" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
        <RouterView />
      </main>
    </template>
    <template v-else>
      <RouterView />
    </template>
  </div>
</template>

<style>
.app-container {
  display: flex;
  min-height: 100vh;
  background: var(--bg-primary);
}

.main-content {
  flex: 1;
  margin-left: 260px;
  width: calc(100% - 260px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  min-height: 100vh;
  overflow-x: hidden;
  position: relative;
  display: flex;
  flex-direction: column;
}

.main-content.sidebar-collapsed {
  margin-left: 80px;
  width: calc(100% - 80px);
}

@media (max-width: 768px) {
  .main-content {
    margin-left: 0;
  }
}
</style>
