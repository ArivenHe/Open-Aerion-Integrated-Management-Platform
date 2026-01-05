<script>

export default {
  name: 'AppHeader',
  props: {
    collapsed: Boolean
  },
  emits: ['toggle-sidebar'],
  data() {
    return {
      currentTime: '',
      timer: null,
      isDark: false,
      isUserOpen: false
    }
  },
  mounted() {
    this.updateTime()
    this.timer = setInterval(this.updateTime, 1000)
    
    // Check initial dark mode
    if (document.documentElement.classList.contains('dark')) {
      this.isDark = true
    }
    
    // Close dropdowns on click outside
    document.addEventListener('click', this.closeDropdowns)
  },
  beforeUnmount() {
    if (this.timer) {
      clearInterval(this.timer)
    }
    document.removeEventListener('click', this.closeDropdowns)
  },
  methods: {
    closeDropdowns(e) {
      if (!this.$el.contains(e.target)) {
        this.isUserOpen = false
      }
    },
    toggleUserDropdown() {
      this.isUserOpen = !this.isUserOpen
    },
    updateTime() {
      // Use a more readable format
      this.currentTime = new Date().toLocaleTimeString('en-US', { hour12: false }) + ' UTC'
    },
    toggleDark() {
      this.isDark = !this.isDark
      if (this.isDark) {
        document.documentElement.classList.add('dark')
      } else {
        document.documentElement.classList.remove('dark')
      }
    }
  }
}
</script>

<template>
  <header class="h-16 flex items-center justify-between px-6 border-b border-gray-200 dark:border-gray-800 bg-white dark:bg-gray-950 sticky top-0 z-30 transition-colors duration-300">
    <div class="flex items-center gap-4">
      <button 
        class="p-2 text-gray-500 hover:bg-gray-100 dark:text-gray-400 dark:hover:bg-gray-800 rounded-lg transition-colors"
        @click="$emit('toggle-sidebar')"
      >
        <UIcon name="i-heroicons-bars-3" class="w-6 h-6" />
      </button>
      <h2 class="text-lg font-medium text-gray-900 dark:text-white hidden md:block">仪表盘</h2>
    </div>

    <div class="flex items-center gap-3">
       <!-- Time -->
      <div class="hidden sm:flex items-center px-3 py-1 bg-gray-50 dark:bg-gray-900 rounded-full border border-gray-200 dark:border-gray-800">
        <UIcon name="i-heroicons-clock" class="w-4 h-4 text-gray-500 mr-2" />
        <span class="text-xs font-mono text-gray-600 dark:text-gray-400">{{ currentTime }}</span>
      </div>
      
      <div class="h-6 w-px bg-gray-200 dark:bg-gray-800 mx-2 hidden sm:block"></div>

      <!-- Dark Mode Toggle -->
      <button 
        @click="toggleDark" 
        class="flex items-center justify-center w-9 h-9 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors text-gray-600 dark:text-gray-400"
      >
        <UIcon :name="isDark ? 'i-heroicons-moon' : 'i-heroicons-sun'" class="w-5 h-5" />
      </button>

      <!-- User Dropdown -->
      <div class="relative ml-2">
        <button 
            @click.stop="toggleUserDropdown"
            class="flex items-center gap-2 hover:bg-gray-50 dark:hover:bg-gray-900 p-1 pr-3 rounded-full transition-colors border border-transparent hover:border-gray-200 dark:hover:border-gray-800"
        >
            <img src="https://avatars.githubusercontent.com/u/739984?v=4" class="w-8 h-8 rounded-full bg-gray-200" alt="User" />
            <span class="text-sm font-medium text-gray-700 dark:text-gray-200 hidden sm:block">用户</span>
            <UIcon name="i-heroicons-chevron-down" class="w-4 h-4 text-gray-400" />
        </button>

        <div v-if="isUserOpen" class="absolute right-0 mt-2 w-48 bg-white dark:bg-gray-900 rounded-xl shadow-lg border border-gray-200 dark:border-gray-800 py-1 z-50">
            <div class="px-4 py-3 border-b border-gray-100 dark:border-gray-800">
                <p class="text-sm text-gray-900 dark:text-white font-medium">登录身份</p>
                <p class="text-sm text-gray-500 dark:text-gray-400 truncate">user@example.com</p>
            </div>
            <a href="#" class="block px-4 py-2 text-sm text-gray-700 dark:text-gray-200 hover:bg-gray-50 dark:hover:bg-gray-800">个人资料</a>
            <a href="#" class="block px-4 py-2 text-sm text-gray-700 dark:text-gray-200 hover:bg-gray-50 dark:hover:bg-gray-800">设置</a>
            <div class="border-t border-gray-100 dark:border-gray-800 my-1"></div>
            <a href="#" class="block px-4 py-2 text-sm text-red-600 hover:bg-red-50 dark:hover:bg-red-900/20">退出登录</a>
        </div>
      </div>
    </div>
  </header>
</template>
