<template>
  <div class="min-h-screen bg-[var(--bg-primary)] transition-colors duration-300">
    <!-- 顶部导航栏 -->
    <header class="fixed top-0 left-0 right-0 z-50 glass-card border-b border-[var(--border-color)]">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16 items-center">
          <!-- 左侧Logo和导航 -->
          <div class="flex items-center">
            <router-link to="/" class="flex items-center space-x-3">
              <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-primary to-secondary flex items-center justify-center shadow-glow">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                </svg>
              </div>
              <span class="text-xl font-bold gradient-text font-display">秒杀商城</span>
            </router-link>
            <nav class="hidden md:ml-10 md:flex space-x-1">
              <a href="#" class="text-[var(--text-secondary)] hover:text-[var(--text-primary)] px-4 py-2 rounded-xl text-sm font-medium transition-all hover:bg-black/5 dark:hover:bg-white/5">首页</a>
            </nav>
          </div>
          <!-- 右侧搜索和用户中心 -->
          <div class="flex items-center space-x-4">
            <!-- 搜索框 -->
            <div class="hidden md:block">
              <div class="relative">
                <input
                  type="text"
                  v-model="searchQuery"
                  placeholder="搜索商品..."
                  class="w-64 pl-11 pr-4 py-2.5 bg-[var(--bg-secondary)] border border-[var(--border-color)] rounded-2xl text-sm text-[var(--text-primary)] placeholder-[var(--text-muted)] focus:outline-none focus:border-primary/50 focus:ring-2 focus:ring-primary/20 transition-all"
                />
                <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-[var(--text-muted)]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                  </svg>
                </div>
              </div>
            </div>
            <!-- 主题切换 -->
            <button @click="toggleTheme" class="theme-toggle" title="切换主题">
              <svg class="sun-icon w-5 h-5 text-[var(--text-primary)]" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z" />
              </svg>
              <svg class="moon-icon w-5 h-5 text-[var(--text-primary)]" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z" />
              </svg>
            </button>
            <!-- 购物车 -->
            <button class="p-2.5 rounded-xl text-[var(--text-secondary)] hover:text-[var(--text-primary)] hover:bg-black/5 dark:hover:bg-white/5 transition-all relative">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
              </svg>
              <span class="absolute -top-1 -right-1 w-5 h-5 bg-gradient-to-br from-danger to-warning text-white text-xs rounded-full flex items-center justify-center font-medium">0</span>
            </button>
            <!-- 用户中心 -->
            <div class="relative">
              <button @click="toggleUserMenu" class="flex items-center space-x-2 text-[var(--text-secondary)] hover:text-[var(--text-primary)] p-2 rounded-xl hover:bg-black/5 dark:hover:bg-white/5 transition-all">
                <div class="w-8 h-8 rounded-full bg-gradient-to-br from-primary/30 to-secondary/30 flex items-center justify-center border border-[var(--border-color)] overflow-hidden">
                  <img
                    v-if="user.avatar && user.avatar.trim() !== ''"
                    :src="user.avatar"
                    alt="头像"
                    class="w-full h-full object-cover"
                    @error="user.avatar = ''"
                  />
                  <svg
                    v-else
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-4 w-4 text-[var(--text-primary)]"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                  </svg>
                </div>
                <span class="hidden md:inline text-sm font-medium">我的账户</span>
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 hidden md:block text-[var(--text-muted)]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                </svg>
              </button>
              <div v-if="showUserMenu" class="absolute right-0 mt-2 w-48 glass-card rounded-2xl py-2 z-50 shadow-card border border-[var(--border-color)]">
                <router-link to="/profile" class="block px-4 py-2.5 text-sm text-[var(--text-secondary)] hover:text-[var(--text-primary)] hover:bg-black/5 dark:hover:bg-white/5 transition-colors">个人中心</router-link>
                <router-link to="/orders" class="block px-4 py-2.5 text-sm text-[var(--text-secondary)] hover:text-[var(--text-primary)] hover:bg-black/5 dark:hover:bg-white/5 transition-colors">我的订单</router-link>
                <div class="my-1 border-t border-[var(--border-color)]"></div>
                <button @click="handleLogout" class="block w-full text-left px-4 py-2.5 text-sm text-danger hover:bg-black/5 dark:hover:bg-white/5 transition-colors">退出登录</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </header>

    <!-- Hero Section -->
    <section class="relative pt-32 pb-20 overflow-hidden">
      <!-- Background Effects -->
      <div class="absolute inset-0 overflow-hidden">
        <div class="absolute top-0 left-1/4 w-96 h-96 bg-primary/20 rounded-full blur-[128px] dark:opacity-100 opacity-30"></div>
        <div class="absolute bottom-0 right-1/4 w-96 h-96 bg-secondary/20 rounded-full blur-[128px] dark:opacity-100 opacity-30"></div>
        <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-accent/10 rounded-full blur-[150px] dark:opacity-100 opacity-20"></div>
      </div>

      <div class="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex flex-col lg:flex-row items-center gap-12">
          <!-- Left Content -->
          <div class="lg:w-1/2 text-center lg:text-left">
            <div class="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-black/5 dark:bg-white/5 border border-[var(--border-color)] mb-6">
              <span class="w-2 h-2 rounded-full bg-success animate-pulse"></span>
              <span class="text-sm text-[var(--text-secondary)]">限时秒杀火热进行中</span>
            </div>
            <h1 class="text-4xl md:text-5xl lg:text-6xl font-bold text-[var(--text-primary)] mb-6 font-display leading-tight">
              发现超值好物<br/>
              <span class="gradient-text">限时抢购不停</span>
            </h1>
            <p class="text-lg text-[var(--text-secondary)] mb-8 max-w-lg mx-auto lg:mx-0">
              每日精选优质商品，限时特价优惠，抢到就是赚到！品质保证，售后无忧。
            </p>
            <div class="flex flex-col sm:flex-row gap-4 justify-center lg:justify-start">
              <button class="btn-primary px-8 py-4 text-base font-semibold rounded-2xl flex items-center justify-center gap-2">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
                </svg>
                立即抢购
              </button>
              <button class="btn-secondary px-8 py-4 text-base font-semibold rounded-2xl flex items-center justify-center gap-2">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                了解更多
              </button>
            </div>
            <!-- Stats -->
            <div class="flex gap-8 mt-12 justify-center lg:justify-start">
              <div class="text-center">
                <div class="text-2xl font-bold text-[var(--text-primary)] font-display">10K+</div>
                <div class="text-sm text-[var(--text-muted)]">活跃用户</div>
              </div>
              <div class="w-px bg-[var(--border-color)]"></div>
              <div class="text-center">
                <div class="text-2xl font-bold text-[var(--text-primary)] font-display">50K+</div>
                <div class="text-sm text-[var(--text-muted)]">商品数量</div>
              </div>
              <div class="w-px bg-[var(--border-color)]"></div>
              <div class="text-center">
                <div class="text-2xl font-bold text-[var(--text-primary)] font-display">99%</div>
                <div class="text-sm text-[var(--text-muted)]">好评率</div>
              </div>
            </div>
          </div>
          <!-- Right Image -->
          <div class="lg:w-1/2 relative">
            <div class="relative">
              <div class="absolute -inset-4 bg-gradient-to-r from-primary/20 to-secondary/20 rounded-3xl blur-2xl dark:opacity-100 opacity-40"></div>
              <img
                src="https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=800&h=600&fit=crop"
                alt="限时秒杀"
                class="relative rounded-3xl shadow-2xl w-full h-[400px] object-cover border border-[var(--border-color)]"
              />
              <!-- Floating Card -->
              <div class="absolute -bottom-6 -left-6 glass-card p-4 rounded-2xl shadow-card animate-float">
                <div class="flex items-center gap-3">
                  <div class="w-12 h-12 rounded-xl bg-gradient-to-br from-success to-primary flex items-center justify-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-[var(--text-primary)]" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
                  </div>
                  <div>
                    <div class="text-sm text-[var(--text-muted)]">今日已售</div>
                    <div class="text-lg font-bold text-[var(--text-primary)] font-display">2,847 件</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Categories -->
    <section class="py-12">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex gap-4 overflow-x-auto pb-4 scrollbar-hide">
          <button
            v-for="(category, index) in categories"
            :key="index"
            @click="activeCategory = index"
            :class="[
              'flex items-center gap-3 px-6 py-3 rounded-2xl whitespace-nowrap transition-all',
              activeCategory === index
                ? 'bg-gradient-to-r from-primary to-secondary text-white shadow-glow'
                : 'bg-[var(--bg-secondary)] text-[var(--text-muted)] hover:text-[var(--text-primary)] hover:bg-[var(--bg-card)] border border-[var(--border-color)]'
            ]"
          >
            <span class="text-xl">{{ category.icon }}</span>
            <span class="font-medium">{{ category.name }}</span>
          </button>
        </div>
      </div>
    </section>

    <!-- 商品列表 -->
    <section class="py-12">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <!-- Section Header -->
        <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-10">
          <div>
            <h2 class="text-3xl font-bold text-[var(--text-primary)] font-display mb-2">热门秒杀</h2>
            <p class="text-[var(--text-muted)]">精选优质商品，限时特价优惠</p>
          </div>
          <div class="flex gap-2">
            <button
              v-for="filter in filters"
              :key="filter.value"
              @click="activeFilter = filter.value"
              :class="[
                'px-4 py-2 rounded-xl text-sm font-medium transition-all',
                activeFilter === filter.value
                  ? 'bg-black/10 dark:bg-white/10 text-[var(--text-primary)]'
                  : 'text-[var(--text-muted)] hover:text-[var(--text-primary)] hover:bg-black/5 dark:hover:bg-white/5'
              ]"
            >
              {{ filter.label }}
            </button>
          </div>
        </div>

        <!-- Loading State -->
        <div v-if="loading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          <div v-for="i in 8" :key="i" class="glass-card rounded-3xl overflow-hidden">
            <div class="h-56 shimmer"></div>
            <div class="p-5 space-y-3">
              <div class="h-4 shimmer rounded w-3/4"></div>
              <div class="h-3 shimmer rounded w-1/2"></div>
              <div class="flex justify-between pt-2">
                <div class="h-6 shimmer rounded w-20"></div>
                <div class="h-8 shimmer rounded w-24"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- Error State -->
        <div v-else-if="error" class="text-center py-20">
          <div class="w-20 h-20 mx-auto mb-6 rounded-full bg-danger/10 flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 text-danger" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
          </div>
          <p class="text-white/60 text-lg">{{ error }}</p>
          <button @click="fetchGoodsList" class="mt-4 btn-primary px-6 py-3 rounded-xl">重新加载</button>
        </div>

        <!-- Products Grid -->
        <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          <div
            v-for="goods in filteredGoodsList"
            :key="goods.id"
            class="group glass-card rounded-3xl overflow-hidden glass-card-hover cursor-pointer"
            @click="$router.push(`/goods/${goods.id}`)"
          >
            <!-- Image Container -->
            <div class="relative h-56 overflow-hidden">
              <img
                :src="goods.goodsImg"
                :alt="goods.goodsName"
                class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
              />
              <!-- Discount Badge -->
              <div class="absolute top-4 left-4">
                <span class="badge badge-danger">
                  -{{ Math.round((1 - goods.seckillPrice / goods.goodsPrice) * 100) }}%
                </span>
              </div>
              <!-- Stock Badge -->
              <div class="absolute top-4 right-4">
                <span v-if="goods.stockCount <= 5" class="badge badge-warning">
                  仅剩 {{ goods.stockCount }} 件
                </span>
                <span v-else class="badge badge-success">
                  有货
                </span>
              </div>
              <!-- Timer Overlay -->
              <div class="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black/80 to-transparent p-4">
                <div class="flex items-center justify-between">
                  <span class="text-xs text-white/70">剩余时间</span>
                  <span class="text-sm font-semibold text-white font-display">{{ formatTimeLeft(goods.endTime - Date.now()) }}</span>
                </div>
              </div>
            </div>

            <!-- Content -->
            <div class="p-5">
              <h3 class="text-lg font-semibold text-[var(--text-primary)] mb-2 line-clamp-1 group-hover:text-primary transition-colors">{{ goods.goodsName }}</h3>
              <p class="text-sm text-[var(--text-muted)] mb-4 line-clamp-2">{{ goods.goodsTitle }}</p>

              <!-- Price Row -->
              <div class="flex items-end justify-between mb-4">
                <div class="flex items-baseline gap-2">
                  <span class="text-2xl font-bold text-[var(--text-primary)] font-display">¥{{ goods.seckillPrice }}</span>
                  <span class="text-sm text-[var(--text-muted)] line-through">¥{{ goods.goodsPrice }}</span>
                </div>
              </div>

              <!-- Action Button -->
              <button class="w-full py-3 rounded-xl bg-gradient-to-r from-primary to-secondary text-white font-medium transition-all hover:shadow-glow flex items-center justify-center gap-2 group-hover:scale-[1.02]">
                <span>立即抢购</span>
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 transition-transform group-hover:translate-x-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                </svg>
              </button>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-if="!loading && !error && filteredGoodsList.length === 0" class="text-center py-20">
          <div class="w-20 h-20 mx-auto mb-6 rounded-full bg-dark-700 flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10 text-white/30" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
            </svg>
          </div>
          <p class="text-white/50 text-lg">暂无商品</p>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="py-20 relative overflow-hidden">
      <div class="absolute inset-0 bg-gradient-to-b from-[var(--bg-secondary)] to-[var(--bg-primary)]"></div>
      <div class="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-16">
          <h2 class="text-3xl md:text-4xl font-bold text-[var(--text-primary)] font-display mb-4">为什么选择我们</h2>
          <p class="text-[var(--text-muted)] max-w-2xl mx-auto">我们致力于为您提供最优质的购物体验，让您买得放心，用得舒心</p>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div v-for="(feature, index) in features" :key="index" class="glass-card p-8 rounded-3xl text-center group hover:border-primary/30 transition-all">
            <div class="w-16 h-16 mx-auto mb-6 rounded-2xl bg-gradient-to-br from-primary/20 to-secondary/20 flex items-center justify-center group-hover:scale-110 transition-transform">
              <span class="text-3xl">{{ feature.icon }}</span>
            </div>
            <h3 class="text-xl font-semibold text-[var(--text-primary)] mb-3">{{ feature.title }}</h3>
            <p class="text-[var(--text-muted)]">{{ feature.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 底部信息 -->
    <footer class="bg-[var(--bg-secondary)] border-t border-[var(--border-color)] pt-16 pb-8">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-12 mb-12">
          <!-- Brand -->
          <div class="md:col-span-1">
            <div class="flex items-center space-x-3 mb-6">
              <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-primary to-secondary flex items-center justify-center">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                </svg>
              </div>
              <span class="text-xl font-bold gradient-text font-display">秒杀商城</span>
            </div>
            <p class="text-[var(--text-muted)] mb-6">限时秒杀，抢购不停。每日精选商品，限时特价，抢完即止！</p>
            <div class="flex space-x-4">
              <a href="#" class="w-10 h-10 rounded-xl bg-black/5 dark:bg-white/5 flex items-center justify-center text-[var(--text-muted)] hover:text-[var(--text-primary)] hover:bg-black/10 dark:hover:bg-white/10 transition-all">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M24 4.557c-.883.392-1.832.656-2.828.775 1.017-.609 1.798-1.574 2.165-2.724-.951.564-2.005.974-3.127 1.195-.897-.957-2.178-1.555-3.594-1.555-3.179 0-5.515 2.966-4.797 6.045-4.091-.205-7.719-2.165-10.148-5.144-1.29 2.213-.669 5.108 1.523 6.574-.806-.026-1.566-.247-2.229-.616-.054 2.281 1.581 4.415 3.949 4.89-.693.188-1.452.232-2.224.084.626 1.956 2.444 3.379 4.6 3.419-2.07 1.623-4.678 2.348-7.29 2.04 2.179 1.397 4.768 2.212 7.548 2.212 9.142 0 14.307-7.721 13.995-14.646.962-.695 1.797-1.562 2.457-2.549z"/>
                </svg>
              </a>
              <a href="#" class="w-10 h-10 rounded-xl bg-black/5 dark:bg-white/5 flex items-center justify-center text-[var(--text-muted)] hover:text-[var(--text-primary)] hover:bg-black/10 dark:hover:bg-white/10 transition-all">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M12 2.163c3.204 0 3.584.012 4.85.07 3.252.148 4.771 1.691 4.919 4.919.058 1.265.069 1.645.069 4.849 0 3.205-.012 3.584-.069 4.849-.149 3.225-1.664 4.771-4.919 4.919-1.266.058-1.644.07-4.85.07-3.204 0-3.584-.012-4.849-.07-3.26-.149-4.771-1.699-4.919-4.92-.058-1.265-.07-1.644-.07-4.849 0-3.204.013-3.583.07-4.849.149-3.227 1.664-4.771 4.919-4.919 1.266-.057 1.645-.069 4.849-.069zm0-2.163c-3.259 0-3.667.014-4.947.072-4.358.2-6.78 2.618-6.98 6.98-.059 1.281-.073 1.689-.073 4.948 0 3.259.014 3.668.072 4.948.2 4.358 2.618 6.78 6.98 6.98 1.281.058 1.689.072 4.948.072 3.259 0 3.668-.014 4.948-.072 4.354-.2 6.782-2.618 6.979-6.98.059-1.28.073-1.689.073-4.948 0-3.259-.014-3.667-.072-4.947-.196-4.354-2.617-6.78-6.979-6.98-1.281-.059-1.69-.073-4.949-.073zm0 5.838c-3.403 0-6.162 2.759-6.162 6.162s2.759 6.163 6.162 6.163 6.162-2.759 6.162-6.163c0-3.403-2.759-6.162-6.162-6.162zm0 10.162c-2.209 0-4-1.79-4-4 0-2.209 1.791-4 4-4s4 1.791 4 4c0 2.21-1.791 4-4 4zm6.406-11.845c-.796 0-1.441.645-1.441 1.44s.645 1.44 1.441 1.44c.795 0 1.439-.645 1.439-1.44s-.644-1.44-1.439-1.44z"/>
                </svg>
              </a>
            </div>
          </div>

          <!-- Links -->
          <div>
            <h4 class="text-[var(--text-primary)] font-semibold mb-6">关于我们</h4>
            <ul class="space-y-3">
              <li><a href="#" class="text-[var(--text-muted)] hover:text-[var(--text-primary)] transition-colors">公司简介</a></li>
              <li><a href="#" class="text-[var(--text-muted)] hover:text-[var(--text-primary)] transition-colors">联系我们</a></li>
              <li><a href="#" class="text-[var(--text-muted)] hover:text-[var(--text-primary)] transition-colors">加入我们</a></li>
              <li><a href="#" class="text-[var(--text-muted)] hover:text-[var(--text-primary)] transition-colors">商家入驻</a></li>
            </ul>
          </div>
          <div>
            <h4 class="text-[var(--text-primary)] font-semibold mb-6">帮助中心</h4>
            <ul class="space-y-3">
              <li><a href="#" class="text-[var(--text-muted)] hover:text-[var(--text-primary)] transition-colors">购物指南</a></li>
              <li><a href="#" class="text-[var(--text-muted)] hover:text-[var(--text-primary)] transition-colors">支付方式</a></li>
              <li><a href="#" class="text-[var(--text-muted)] hover:text-[var(--text-primary)] transition-colors">配送方式</a></li>
              <li><a href="#" class="text-[var(--text-muted)] hover:text-[var(--text-primary)] transition-colors">售后服务</a></li>
            </ul>
          </div>
          <div>
            <h4 class="text-[var(--text-primary)] font-semibold mb-6">联系我们</h4>
            <ul class="space-y-3">
              <li class="flex items-center gap-3 text-[var(--text-muted)]">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                </svg>
                400-123-4567
              </li>
              <li class="flex items-center gap-3 text-[var(--text-muted)]">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                </svg>
                support@seckill.com
              </li>
              <li class="flex items-center gap-3 text-[var(--text-muted)]">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                9:00 - 21:00
              </li>
            </ul>
          </div>
        </div>
        <div class="border-t border-[var(--border-color)] pt-8 text-center">
          <p class="text-[var(--text-muted)]">© 2026 秒杀商城. 保留所有权利.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../api'
import { useUserStore, useGoodsStore, useThemeStore } from '../stores'

const router = useRouter()
const { user, logout, updateUserInfo } = useUserStore()
const { goodsList, loading, error, setGoodsList, setLoading, setError } = useGoodsStore()
const { toggleTheme, initTheme } = useThemeStore()

// Search and filter states
const searchQuery = ref('')
const activeCategory = ref(0)
const activeFilter = ref('all')
const showUserMenu = ref(false)

const categories = [
  { name: '全部商品', icon: '🛍️' },
  { name: '数码电子', icon: '📱' },
  { name: '服装鞋包', icon: '👕' },
  { name: '家居生活', icon: '🏠' },
  { name: '美妆护肤', icon: '💄' },
  { name: '运动户外', icon: '⚽' },
]

const filters = [
  { label: '全部', value: 'all' },
  { label: '热门推荐', value: 'hot' },
  { label: '最新上架', value: 'new' },
  { label: '价格最低', value: 'price' },
]

const features = [
  { icon: '⚡', title: '极速秒杀', description: '限时抢购，手快有手慢无，抢到就是赚到' },
  { icon: '🛡️', title: '正品保障', description: '100%正品保证，假一赔十，购物无忧' },
  { icon: '🚚', title: '快速配送', description: '全国包邮，24小时发货，极速送达' },
]

const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
}

const handleLogout = () => {
  logout()
  router.push('/login')
}

const formatTimeLeft = (milliseconds) => {
  if (milliseconds <= 0) return '已结束'

  const seconds = Math.floor((milliseconds / 1000) % 60)
  const minutes = Math.floor((milliseconds / (1000 * 60)) % 60)
  const hours = Math.floor((milliseconds / (1000 * 60 * 60)) % 24)

  return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
}

const filteredGoodsList = computed(() => {
  let result = goodsList.value || []

  // Search filter
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(goods =>
      goods.goodsName.toLowerCase().includes(query) ||
      goods.goodsTitle.toLowerCase().includes(query)
    )
  }

  // Category filter (mock implementation)
  if (activeCategory.value > 0) {
    // In real implementation, filter by category
  }

  // Sort filter
  if (activeFilter.value === 'price') {
    result = [...result].sort((a, b) => a.seckillPrice - b.seckillPrice)
  } else if (activeFilter.value === 'new') {
    result = [...result].sort((a, b) => b.id - a.id)
  }

  return result
})

const fetchGoodsList = async () => {
  setLoading(true)
  setError(null)
  try {
    const response = await api.getGoodsList({ pageNum: 1, pageSize: 12 })
    if (response.code === 0 || response.code === 200) {
      if (response.data && response.data.records) {
        const transformedGoods = response.data.records.map((goods) => {
          let goodsImg = `https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=400&h=300&fit=crop`
          if (goods.goodsImg) {
            goodsImg = goods.goodsImg
          } else if (goods.photoUrl) {
            goodsImg = goods.photoUrl
          }
          return {
            id: goods.id || goods.seckillId,
            goodsName: goods.goodsName || goods.name,
            goodsTitle: goods.goodsTitle || goods.name,
            seckillPrice: goods.seckillPrice || goods.price,
            goodsPrice: goods.goodsPrice || (goods.price * 1.2),
            stockCount: goods.stockCount || goods.number,
            endTime: new Date(goods.endTime).getTime(),
            goodsImg: goodsImg
          }
        })
        setGoodsList(transformedGoods)
      } else if (response.records) {
        setGoodsList(response.records)
      } else {
        setGoodsList([])
      }
    } else {
      setError(response.message || response.msg || '获取商品列表失败')
    }
  } catch (err) {
    setError('获取商品列表失败，请稍后重试')
    console.error('获取商品列表失败:', err)
  } finally {
    setLoading(false)
  }
}

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const response = await api.getCustomerUserInfo()
    if (response.code === 0 || response.code === 200) {
      updateUserInfo(response.data)
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

onMounted(() => {
  initTheme()
  fetchGoodsList()
  fetchUserInfo()
})
</script>

<style scoped>
.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}
</style>
