<script setup>
import { ref } from 'vue'
import { resetPassword, sendForgotPasswordCode } from '@/api/auth'

const email = ref('')
const code = ref('')
const newPassword = ref('')
const message = ref('')
const sending = ref(false)
const loading = ref(false)

const handleSendCode = async () => {
  if (sending.value) return
  message.value = ''
  if (!email.value) {
    message.value = '请先填写邮箱'
    return
  }
  sending.value = true
  try {
    await sendForgotPasswordCode(email.value)
    message.value = '验证码已发送，请查收邮箱'
  } catch (error) {
    message.value = error.message
  } finally {
    sending.value = false
  }
}

const handleReset = async () => {
  if (loading.value) return
  message.value = ''
  loading.value = true
  try {
    await resetPassword({
      email: email.value,
      code: code.value,
      newPassword: newPassword.value,
    })
    message.value = '密码已重置，请使用新密码登录'
  } catch (error) {
    message.value = error.message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen flex bg-white dark:bg-gray-950">
    <!-- Left Side - Image/Branding -->
    <div class="hidden lg:flex lg:w-1/2 relative bg-gray-900 overflow-hidden">
      <div class="absolute inset-0 bg-gradient-to-br from-primary-600/90 to-cyan-900/90 z-10"></div>
      <img 
        src="https://images.unsplash.com/photo-1542296332-2e44a40f0a52?q=80&w=2070&auto=format&fit=crop" 
        alt="Aviation Background" 
        class="absolute inset-0 w-full h-full object-cover"
      />
      <div class="relative z-20 flex flex-col justify-center px-12 h-full text-white">
        <h1 class="text-5xl font-bold mb-6">找回密码</h1>
        <p class="text-xl text-gray-200 max-w-md leading-relaxed">
          不用担心，我们来帮您。<br>只需几步简单操作，即可重置您的账户密码。
        </p>
      </div>
      <!-- Decorative circles -->
      <div class="absolute -bottom-24 -right-24 w-96 h-96 bg-primary-500/20 rounded-full blur-3xl z-10"></div>
      <div class="absolute top-12 left-12 w-32 h-32 bg-cyan-400/20 rounded-full blur-2xl z-10"></div>
    </div>

    <!-- Right Side - Forgot Password Form -->
    <div class="w-full lg:w-1/2 flex items-center justify-center p-8 sm:p-12 lg:p-16">
      <div class="w-full max-w-md space-y-8">
        <div class="text-center lg:text-left">
          <h2 class="text-3xl font-bold tracking-tight text-gray-900 dark:text-white">重置密码</h2>
          <p class="mt-2 text-sm text-gray-600 dark:text-gray-400">
            请输入注册邮箱并完成验证码校验以重置密码
          </p>
        </div>

        <form class="mt-8 space-y-6" @submit.prevent="handleReset">
          <div class="space-y-5">
            <div>
              <label for="email" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">电子邮箱</label>
              <div class="relative">
                 <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <svg class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                       <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 12a4 4 0 10-8 0 4 4 0 008 0zm0 0v1.5a2.5 2.5 0 005 0V12a9 9 0 10-9 9m4.5-1.206a8.959 8.959 0 01-4.5 1.207" />
                    </svg>
                 </div>
                 <input id="email" v-model="email" name="email" type="email" autocomplete="email" required 
                   class="appearance-none block w-full pl-10 pr-3 py-2.5 border border-gray-300 dark:border-gray-700 rounded-xl placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent dark:bg-gray-900 dark:text-white transition-all duration-200" 
                   placeholder="name@example.com">
              </div>
            </div>

            <div>
              <label for="reset-code" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">邮箱验证码</label>
              <div class="flex gap-2">
                <div class="relative flex-1">
                   <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                      <svg class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                         <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                      </svg>
                   </div>
                   <input id="reset-code" v-model="code" name="reset-code" type="text" required 
                     class="appearance-none block w-full pl-10 pr-3 py-2.5 border border-gray-300 dark:border-gray-700 rounded-xl placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent dark:bg-gray-900 dark:text-white transition-all duration-200" 
                     placeholder="输入验证码">
                </div>
                <button type="button" @click="handleSendCode" :disabled="sending" class="px-3 py-2.5 bg-primary-600 text-white text-sm font-medium rounded-xl hover:bg-primary-700 transition-colors whitespace-nowrap disabled:opacity-60 disabled:cursor-not-allowed">
                  {{ sending ? '发送中' : '获取验证码' }}
                </button>
              </div>
            </div>

            <div>
              <label for="new-password" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">新密码</label>
              <div class="relative">
                 <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <svg class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                       <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                    </svg>
                 </div>
                 <input id="new-password" v-model="newPassword" name="new-password" type="password" autocomplete="new-password" required 
                   class="appearance-none block w-full pl-10 pr-3 py-2.5 border border-gray-300 dark:border-gray-700 rounded-xl placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent dark:bg-gray-900 dark:text-white transition-all duration-200" 
                   placeholder="••••••••">
              </div>
            </div>
          </div>

          <div>
            <button type="submit" :disabled="loading" class="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-semibold rounded-xl text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 transition-all duration-200 shadow-lg shadow-primary-500/30 hover:shadow-primary-500/40 transform hover:-translate-y-0.5 disabled:opacity-60 disabled:cursor-not-allowed">
              {{ loading ? '重置中...' : '重置密码' }}
            </button>
          </div>

          <div v-if="message" class="text-sm text-red-600 dark:text-red-400 text-center">
            {{ message }}
          </div>
          
          <div class="text-center text-sm mt-6">
              <router-link to="/login" class="font-medium text-primary-600 hover:text-primary-500 dark:text-primary-400 dark:hover:text-primary-300 flex items-center justify-center gap-2 transition-colors">
                <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                   <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                </svg>
                返回登录
              </router-link>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
