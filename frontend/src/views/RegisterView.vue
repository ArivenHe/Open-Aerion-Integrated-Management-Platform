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
      <div class="absolute inset-0 bg-gradient-to-tr from-primary-600/90 to-purple-900/90 z-10"></div>
      <img 
        src="https://images.unsplash.com/photo-1559067515-bf7d799b6d4d?q=80&w=1974&auto=format&fit=crop" 
        alt="Aviation Background" 
        class="absolute inset-0 w-full h-full object-cover"
      />
      <div class="relative z-20 flex flex-col justify-center px-12 h-full text-white">
        <h1 class="text-5xl font-bold mb-6">加入 Open AIMP</h1>
        <p class="text-xl text-gray-200 max-w-md leading-relaxed">
          开启您的航空信息之旅。<br>注册成为会员，享受更专业的飞行服务与数据支持。
        </p>
        <div class="mt-12 grid grid-cols-2 gap-6">
           <div class="flex items-center gap-3">
              <div class="p-2 bg-white/10 rounded-lg backdrop-blur-sm">
                 <svg class="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                 </svg>
              </div>
              <span class="font-medium">实名认证</span>
           </div>
           <div class="flex items-center gap-3">
              <div class="p-2 bg-white/10 rounded-lg backdrop-blur-sm">
                 <svg class="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
                 </svg>
              </div>
              <span class="font-medium">极速响应</span>
           </div>
           <div class="flex items-center gap-3">
              <div class="p-2 bg-white/10 rounded-lg backdrop-blur-sm">
                 <svg class="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                 </svg>
              </div>
              <span class="font-medium">全球覆盖</span>
           </div>
           <div class="flex items-center gap-3">
              <div class="p-2 bg-white/10 rounded-lg backdrop-blur-sm">
                 <svg class="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                 </svg>
              </div>
              <span class="font-medium">社区支持</span>
           </div>
        </div>
      </div>
      <!-- Decorative circles -->
      <div class="absolute -top-24 -right-24 w-96 h-96 bg-primary-500/20 rounded-full blur-3xl z-10"></div>
      <div class="absolute bottom-12 left-12 w-32 h-32 bg-purple-400/20 rounded-full blur-2xl z-10"></div>
    </div>

    <!-- Right Side - Register Form -->
    <div class="w-full lg:w-1/2 flex items-center justify-center p-8 sm:p-12 lg:p-16">
      <div class="w-full max-w-md space-y-8">
        <div class="text-center lg:text-left">
          <h2 class="text-3xl font-bold tracking-tight text-gray-900 dark:text-white">创建账户</h2>
          <p class="mt-2 text-sm text-gray-600 dark:text-gray-400">
            填写以下信息以完成注册
          </p>
        </div>

        <form class="mt-8 space-y-6" action="#" method="POST">
          <div class="space-y-5">
            <div>
              <label for="username" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">用户名</label>
              <div class="relative">
                 <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <svg class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                       <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                    </svg>
                 </div>
                 <input id="username" name="username" type="text" required 
                   class="appearance-none block w-full pl-10 pr-3 py-2.5 border border-gray-300 dark:border-gray-700 rounded-xl placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent dark:bg-gray-900 dark:text-white transition-all duration-200" 
                   placeholder="Your username">
              </div>
            </div>

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

            <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
               <div>
                  <label for="password" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">密码</label>
                  <div class="relative">
                     <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <svg class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                           <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                        </svg>
                     </div>
                     <input id="password" name="password" type="password" autocomplete="new-password" required 
                       class="appearance-none block w-full pl-10 pr-3 py-2.5 border border-gray-300 dark:border-gray-700 rounded-xl placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent dark:bg-gray-900 dark:text-white transition-all duration-200" 
                       placeholder="••••••••">
                  </div>
               </div>
               <div>
                  <label for="password-confirm" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">确认密码</label>
                  <div class="relative">
                     <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <svg class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                           <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                     </div>
                     <input id="password-confirm" name="password-confirm" type="password" autocomplete="new-password" required 
                       class="appearance-none block w-full pl-10 pr-3 py-2.5 border border-gray-300 dark:border-gray-700 rounded-xl placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent dark:bg-gray-900 dark:text-white transition-all duration-200" 
                       placeholder="••••••••">
                  </div>
               </div>
            </div>

            <!-- Verification Codes -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
               <!-- Graphic Captcha -->
               <div>
                  <label for="captcha" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">图形验证码</label>
                  <div class="flex gap-2">
                     <div class="relative flex-1">
                        <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                           <svg class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                           </svg>
                        </div>
                        <input id="captcha" name="captcha" type="text" required 
                          class="appearance-none block w-full pl-10 pr-3 py-2.5 border border-gray-300 dark:border-gray-700 rounded-xl placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent dark:bg-gray-900 dark:text-white transition-all duration-200" 
                          placeholder="输入验证码">
                     </div>
                     <div class="w-24 h-11 bg-gray-200 dark:bg-gray-700 rounded-xl flex items-center justify-center cursor-pointer overflow-hidden border border-gray-300 dark:border-gray-600">
                        <!-- Placeholder for Captcha Image -->
                        <span class="text-sm font-mono font-bold tracking-widest text-gray-500 dark:text-gray-400">XY3Z</span>
                     </div>
                  </div>
               </div>

               <!-- Email Verification Code -->
               <div>
                  <label for="email-code" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">邮箱验证码</label>
                  <div class="flex gap-2">
                     <div class="relative flex-1">
                        <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                           <svg class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                           </svg>
                        </div>
                        <input id="email-code" name="email-code" type="text" required 
                          class="appearance-none block w-full pl-10 pr-3 py-2.5 border border-gray-300 dark:border-gray-700 rounded-xl placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent dark:bg-gray-900 dark:text-white transition-all duration-200" 
                          placeholder="输入验证码">
                     </div>
                     <button type="button" class="px-3 py-2.5 bg-primary-50 dark:bg-primary-900/30 text-primary-600 dark:text-primary-400 text-sm font-medium rounded-xl hover:bg-primary-100 dark:hover:bg-primary-900/50 transition-colors whitespace-nowrap">
                        获取验证码
                     </button>
                  </div>
               </div>
            </div>
          </div>

          <div class="flex items-center">
            <input id="terms" name="terms" type="checkbox" class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded cursor-pointer" required>
            <label for="terms" class="ml-2 block text-sm text-gray-600 dark:text-gray-400 cursor-pointer select-none">
              我同意 <a href="#" class="font-medium text-primary-600 hover:text-primary-500 dark:text-primary-400">服务条款</a> 和 <a href="#" class="font-medium text-primary-600 hover:text-primary-500 dark:text-primary-400">隐私政策</a>
            </label>
          </div>

          <div>
            <button type="submit" class="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-semibold rounded-xl text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 transition-all duration-200 shadow-lg shadow-primary-500/30 hover:shadow-primary-500/40 transform hover:-translate-y-0.5">
              注册账户
            </button>
          </div>
          
          <div class="text-center text-sm mt-6">
              <span class="text-gray-600 dark:text-gray-400">已有账号? </span>
              <router-link to="/login" class="font-semibold text-primary-600 hover:text-primary-500 dark:text-primary-400 dark:hover:text-primary-300 transition-colors">
                直接登录
              </router-link>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>