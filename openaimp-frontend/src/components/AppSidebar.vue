<script>
export default {
  name: 'AppSidebar',
  props: {
    collapsed: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      items: [
        {
          label: '仪表盘',
          icon: 'i-heroicons-home',
          to: '/'
        },
        {
          label: '系统管理',
          icon: 'i-heroicons-cog-6-tooth',
          children: [
            { label: '用户管理', icon: 'i-heroicons-users', to: '/users' },
            { label: '角色管理', icon: 'i-heroicons-user-group', to: '/roles' }
          ]
        },
        {
          label: '设置',
          icon: 'i-heroicons-adjustments-horizontal',
          to: '/settings'
        }
      ],
      openMenus: {}
    }
  },
  methods: {
    toggleMenu(label) {
      if (this.collapsed) return
      this.openMenus[label] = !this.openMenus[label]
    },
    isActive(item) {
        // Simple active check, can be improved with useRoute() in setup if needed, 
        // but for Options API we can use $route
        if (item.to) {
            return this.$route.path === item.to
        }
        return false
    }
  }
}
</script>

<template>
  <div :class="[
    'flex flex-col border-r border-gray-200 dark:border-gray-800 bg-white dark:bg-gray-950 transition-all duration-300 h-full shadow-sm z-20',
    collapsed ? 'w-20' : 'w-72'
  ]">
    <!-- Logo Area -->
    <div class="flex items-center justify-center h-16 shrink-0 mb-2">
       <div v-if="!collapsed" class="flex items-center gap-2 px-6 w-full">
         <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-primary-500 to-primary-600 flex items-center justify-center text-white font-bold shadow-lg shadow-primary-500/30">
           OA
         </div>
         <span class="text-xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-gray-900 to-gray-600 dark:from-white dark:to-gray-400 truncate">Open AIMP</span>
       </div>
       <div v-else class="w-10 h-10 rounded-xl bg-gradient-to-br from-primary-500 to-primary-600 flex items-center justify-center text-white font-bold text-xl shadow-lg shadow-primary-500/30">
         OA
       </div>
    </div>

    <!-- Navigation -->
    <nav class="flex-1 overflow-y-auto px-3 py-4 space-y-1 scrollbar-thin scrollbar-thumb-gray-200 dark:scrollbar-thumb-gray-800">
        <div v-for="item in items" :key="item.label">
          <!-- Single Item -->
          <template v-if="!item.children">
             <RouterLink 
               :to="item.to"
               class="group relative flex items-center px-3 py-2.5 rounded-xl transition-all duration-200 mb-1"
               :class="[
                 isActive(item) 
                   ? 'bg-primary-50 dark:bg-primary-950/30 text-primary-600 dark:text-primary-400 font-medium' 
                   : 'text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-900 hover:text-gray-900 dark:hover:text-gray-200',
                 collapsed ? 'justify-center' : ''
               ]"
             >
               <UIcon 
                 :name="item.icon" 
                 class="w-6 h-6 flex-shrink-0 transition-colors"
                 :class="isActive(item) ? 'text-primary-500' : 'text-gray-500 dark:text-gray-400 group-hover:text-gray-700 dark:group-hover:text-gray-200'" 
               />
               
               <span v-if="!collapsed" class="ml-3 truncate text-sm">{{ item.label }}</span>
               
               <!-- Tooltip for collapsed state (optional visual cue) -->
               <div v-if="collapsed" class="absolute left-16 bg-gray-900 text-white text-xs px-2 py-1 rounded opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none whitespace-nowrap z-50">
                 {{ item.label }}
               </div>
             </RouterLink>
          </template>

          <!-- Dropdown Item -->
          <template v-else>
            <button 
              @click="toggleMenu(item.label)"
              class="w-full group relative flex items-center px-3 py-2.5 rounded-xl transition-all duration-200 mb-1"
              :class="[
                 openMenus[item.label]
                   ? 'text-gray-900 dark:text-gray-200 bg-gray-50 dark:bg-gray-900/50' 
                   : 'text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-900 hover:text-gray-900 dark:hover:text-gray-200',
                 collapsed ? 'justify-center' : ''
               ]"
            >
               <Icon 
                 :name="item.icon" 
                 class="w-6 h-6 flex-shrink-0 transition-colors"
                 :class="openMenus[item.label] ? 'text-primary-500' : 'text-gray-500 dark:text-gray-400 group-hover:text-gray-700 dark:group-hover:text-gray-200'"
               />
               
               <template v-if="!collapsed">
                 <span class="ml-3 flex-1 text-left truncate text-sm font-medium">{{ item.label }}</span>
                 <Icon 
                   :name="openMenus[item.label] ? 'i-heroicons-chevron-down' : 'i-heroicons-chevron-right'" 
                   class="w-4 h-4 flex-shrink-0 text-gray-400 transition-transform duration-200" 
                 />
               </template>

               <!-- Tooltip for collapsed state -->
               <div v-if="collapsed" class="absolute left-16 bg-gray-900 text-white text-xs px-2 py-1 rounded opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none whitespace-nowrap z-50">
                 {{ item.label }}
               </div>
            </button>
            
            <div v-if="!collapsed" 
                 class="overflow-hidden transition-all duration-300 ease-in-out space-y-1 mt-1"
                 :class="openMenus[item.label] ? 'max-h-96 opacity-100' : 'max-h-0 opacity-0'"
            >
               <RouterLink v-for="child in item.children" :key="child.label"
                 :to="child.to"
                 class="flex items-center pl-12 pr-4 py-2 text-sm rounded-lg transition-colors"
                 :class="[
                   isActive(child)
                     ? 'text-primary-600 dark:text-primary-400 bg-primary-50/50 dark:bg-primary-950/20'
                     : 'text-gray-500 dark:text-gray-500 hover:text-gray-900 dark:hover:text-gray-300'
                 ]"
               >
                 <span class="w-1.5 h-1.5 rounded-full mr-2" :class="isActive(child) ? 'bg-primary-500' : 'bg-gray-300 dark:bg-gray-600'"></span>
                 <span class="truncate">{{ child.label }}</span>
               </RouterLink>
            </div>
          </template>
        </div>
    </nav>
    
  </div>
</template>
