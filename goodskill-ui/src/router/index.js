import {createRouter, createWebHistory} from 'vue-router'
import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import UserManagement from '../views/UserManagement.vue'
import ProductsManagement from '../views/ProductsManagement.vue'
import SeckillSimulation from '../views/SeckillSimulation.vue'
import OrderManagement from '../views/OrderManagement.vue'
import {hasAccessToken} from '../utils/auth'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录' }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    meta: { title: '控制台' }
  },
  {
    path: '/users',
    name: 'UserManagement',
    component: UserManagement,
    meta: { title: '用户管理' }
  },
  {
    path: '/products',
    name: 'ProductsManagement',
    component: ProductsManagement,
    meta: { title: '商品管理' }
  },
  {
    path: '/orders',
    name: 'OrderManagement',
    component: OrderManagement,
    meta: { title: '订单管理' }
  },
  {
    path: '/seckill-simulation',
    name: 'SeckillSimulation',
    component: SeckillSimulation,
    meta: { title: '秒杀模拟' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || '秒杀后台管理'} - 秒杀后台管理`

  if (to.path !== '/login') {
    if (!hasAccessToken()) {
      next('/login')
      return
    }
  }

  next()
})

export default router
