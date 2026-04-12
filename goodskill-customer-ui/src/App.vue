<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from './stores'

const { checkLoginStatus } = useUserStore()

onMounted(() => {
  checkLoginStatus()
})
</script>

<style scoped>
/* 全局样式 */
</style>