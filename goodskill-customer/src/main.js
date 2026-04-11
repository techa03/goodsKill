import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './style.css'
import { useUserStore } from './stores'

const app = createApp(App)

// 检查登录状态
const { checkLoginStatus } = useUserStore()
checkLoginStatus()

// 注册全局错误处理
app.config.errorHandler = (err, instance, info) => {
  console.error('全局错误:', err)
  console.error('错误信息:', info)
}

app.use(router)
app.mount('#app')