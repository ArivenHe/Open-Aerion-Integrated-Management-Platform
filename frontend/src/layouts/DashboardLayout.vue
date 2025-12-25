<script>
import AppSidebar from '@/components/AppSidebar.vue'
import AppHeader from '@/components/AppHeader.vue'

export default {
  name: 'DashboardLayout',
  components: {
    AppSidebar,
    AppHeader
  },
  data() {
    return {
      sidebarCollapsed: false
    }
  },
  methods: {
    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed
    }
  }
}
</script>

<template>
  <div class="flex h-screen w-screen overflow-hidden bg-gray-50 dark:bg-gray-950 text-gray-900 dark:text-gray-100 font-sans">
    <AppSidebar :collapsed="sidebarCollapsed" />
    
    <div class="flex flex-col flex-1 overflow-hidden min-w-0 transition-all duration-300 relative">
      <AppHeader :collapsed="sidebarCollapsed" @toggle-sidebar="toggleSidebar" />
      
      <main class="flex-1 overflow-y-auto p-6 scroll-smooth">
        <div class="max-w-7xl mx-auto">
             <slot />
        </div>
      </main>
    </div>
  </div>
</template>
