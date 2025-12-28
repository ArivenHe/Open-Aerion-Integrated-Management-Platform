<script setup>
import { ref, onMounted } from 'vue'

const isDark = ref(false)

const toggleDark = () => {
  isDark.value = !isDark.value
  if (isDark.value) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
}

onMounted(() => {
  isDark.value = document.documentElement.classList.contains('dark')
})
</script>

<template>
  <div class="min-h-screen flex bg-white dark:bg-gray-950 relative">
    <!-- Dark Mode Toggle -->
    <div class="absolute top-4 right-4 z-50">
      <button 
        @click="toggleDark" 
        class="flex items-center justify-center w-10 h-10 rounded-full bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm shadow-sm hover:bg-white dark:hover:bg-gray-800 transition-colors text-gray-600 dark:text-gray-400 border border-gray-200 dark:border-gray-700"
      >
        <UIcon :name="isDark ? 'i-heroicons-moon' : 'i-heroicons-sun'" class="w-5 h-5" />
      </button>
    </div>

    <!-- Left Side - Image/Branding -->
    <div class="hidden lg:flex lg:w-1/2 relative bg-gray-900 overflow-hidden">
      <div class="absolute inset-0 bg-gradient-to-br from-primary-600/90 to-blue-900/90 z-10"></div>
      <img 
        src="https://images.unsplash.com/photo-1436491865332-7a61a109cc05?q=80&w=2074&auto=format&fit=crop" 
        alt="Aviation Background" 
        class="absolute inset-0 w-full h-full object-cover"
      />
      <div class="relative z-20 flex flex-col justify-center px-12 h-full text-white">
        <h1 class="text-5xl font-bold mb-6">Open AIMP</h1>
        <p class="text-xl text-gray-200 max-w-md leading-relaxed">
          新一代航空信息管理平台。<br>连接飞行员与管制员，提供实时、精准的飞行数据服务。
        </p>
        <div class="mt-12 flex gap-4">
           <div class="flex -space-x-2">
              <img class="w-10 h-10 rounded-full border-2 border-white" src="https://avatars.githubusercontent.com/u/1?v=4" alt=""/>
              <img class="w-10 h-10 rounded-full border-2 border-white" src="https://avatars.githubusercontent.com/u/2?v=4" alt=""/>
              <img class="w-10 h-10 rounded-full border-2 border-white" src="https://avatars.githubusercontent.com/u/3?v=4" alt=""/>
           </div>
           <div class="flex flex-col justify-center">
              <span class="font-bold text-sm">12k+</span>
              <span class="text-xs text-gray-300">活跃用户</span>
           </div>
        </div>
      </div>
      <!-- Decorative circles -->
      <div class="absolute -bottom-24 -left-24 w-96 h-96 bg-primary-500/20 rounded-full blur-3xl z-10"></div>
      <div class="absolute top-12 right-12 w-32 h-32 bg-blue-400/20 rounded-full blur-2xl z-10"></div>
    </div>

    <!-- Right Side - Login Form -->
    <div class="w-full lg:w-1/2 flex items-center justify-center p-8 sm:p-12 lg:p-16">
      <div class="w-full max-w-md space-y-8">
        <div class="text-center lg:text-left">
          <h2 class="text-3xl font-bold tracking-tight text-gray-900 dark:text-white">欢迎回来</h2>
          <p class="mt-2 text-sm text-gray-600 dark:text-gray-400">
            请输入您的账号信息以登录平台
          </p>
        </div>

        <form class="mt-8 space-y-6" action="#" method="POST">
          <div class="space-y-5">
            <div>
              <label for="email" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">电子邮箱</label>
              <div class="relative">
                 <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <svg class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                       <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 12a4 4 0 10-8 0 4 4 0 008 0zm0 0v1.5a2.5 2.5 0 005 0V12a9 9 0 10-9 9m4.5-1.206a8.959 8.959 0 01-4.5 1.207" />
                    </svg>
                 </div>
                 <input id="email" name="email" type="email" autocomplete="email" required 
                   class="appearance-none block w-full pl-10 pr-3 py-2.5 border border-gray-300 dark:border-gray-700 rounded-xl placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent dark:bg-gray-900 dark:text-white transition-all duration-200" 
                   placeholder="name@example.com">
              </div>
            </div>

            <div>
              <div class="flex items-center justify-between mb-1">
                 <label for="password" class="block text-sm font-medium text-gray-700 dark:text-gray-300">密码</label>
              </div>
              <div class="relative">
                 <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <svg class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                       <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                    </svg>
                 </div>
                 <input id="password" name="password" type="password" autocomplete="current-password" required 
                   class="appearance-none block w-full pl-10 pr-3 py-2.5 border border-gray-300 dark:border-gray-700 rounded-xl placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent dark:bg-gray-900 dark:text-white transition-all duration-200" 
                   placeholder="••••••••">
              </div>
            </div>
          </div>

          <div class="flex items-center justify-between">
            <div class="flex items-center">
              <input id="remember-me" name="remember-me" type="checkbox" class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded cursor-pointer">
              <label for="remember-me" class="ml-2 block text-sm text-gray-600 dark:text-gray-400 cursor-pointer select-none">
                记住我
              </label>
            </div>

            <div class="text-sm">
              <router-link to="/forgot-password" class="font-medium text-primary-600 hover:text-primary-500 dark:text-primary-400 dark:hover:text-primary-300">
                忘记密码?
              </router-link>
            </div>
          </div>

          <div>
            <button type="submit" class="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-semibold rounded-xl text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 transition-all duration-200 shadow-lg shadow-primary-500/30 hover:shadow-primary-500/40 transform hover:-translate-y-0.5">
              登录
            </button>
          </div>

          <div class="text-center text-sm mt-6">
              <span class="text-gray-600 dark:text-gray-400">还没有账号? </span>
              <router-link to="/register" class="font-semibold text-primary-600 hover:text-primary-500 dark:text-primary-400 dark:hover:text-primary-300 transition-colors">
                立即注册
              </router-link>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>