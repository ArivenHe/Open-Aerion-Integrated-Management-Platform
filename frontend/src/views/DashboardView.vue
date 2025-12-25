<script>
import DashboardLayout from '@/layouts/DashboardLayout.vue'

export default {
  name: 'DashboardView',
  components: {
    DashboardLayout
  }
}
</script>

<template>
  <DashboardLayout>
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Overview</h1>
      <p class="text-gray-500 dark:text-gray-400">Welcome back, here's what's happening today.</p>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <!-- Welcome Card -->
      <div class="bg-white dark:bg-gray-950 rounded-xl border border-gray-100 dark:border-gray-800 p-6 shadow-sm hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between mb-4">
          <h3 class="font-semibold text-gray-900 dark:text-white">Welcome Back</h3>
          <UIcon name="i-heroicons-hand-raised" class="w-5 h-5 text-primary-500" />
        </div>
        <p class="text-gray-600 dark:text-gray-400 mb-6">
            Your dashboard is ready. You have <span class="font-bold text-primary-500">3</span> new notifications today.
        </p>
        <button class="w-full py-2 px-4 bg-primary-50 dark:bg-primary-900/20 text-primary-600 dark:text-primary-400 rounded-lg text-sm font-medium hover:bg-primary-100 dark:hover:bg-primary-900/40 transition-colors">
          View Notifications
        </button>
      </div>
      
      <!-- Stats Card -->
      <div class="bg-white dark:bg-gray-950 rounded-xl border border-gray-100 dark:border-gray-800 p-6 shadow-sm hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between mb-4">
          <h3 class="font-semibold text-gray-900 dark:text-white">Active Users</h3>
          <div class="p-2 bg-green-50 dark:bg-green-900/20 rounded-lg">
            <UIcon name="i-heroicons-users" class="w-5 h-5 text-green-500" />
          </div>
        </div>
        <div class="flex items-baseline gap-2 mb-1">
            <div class="text-3xl font-bold text-gray-900 dark:text-white">1,234</div>
            <div class="text-sm text-green-500 flex items-center font-medium">
                <UIcon name="i-heroicons-arrow-trending-up" class="w-4 h-4 mr-1" />
                +12%
            </div>
        </div>
        <div class="text-sm text-gray-500 dark:text-gray-400">vs. last month</div>
      </div>
      
      <!-- Performance Card -->
      <div class="bg-white dark:bg-gray-950 rounded-xl border border-gray-100 dark:border-gray-800 p-6 shadow-sm hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between mb-6">
          <h3 class="font-semibold text-gray-900 dark:text-white">System Status</h3>
          <div class="flex items-center gap-2 px-2 py-1 bg-green-50 dark:bg-green-900/20 rounded-full border border-green-100 dark:border-green-900/30">
              <span class="relative flex h-2 w-2">
                <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-green-400 opacity-75"></span>
                <span class="relative inline-flex rounded-full h-2 w-2 bg-green-500"></span>
              </span>
              <span class="text-xs text-green-700 dark:text-green-400 font-medium">Online</span>
          </div>
        </div>
        <div class="space-y-4">
            <div class="space-y-2">
                <div class="flex justify-between text-sm">
                    <span class="text-gray-600 dark:text-gray-400">CPU Usage</span>
                    <span class="font-medium text-gray-900 dark:text-white">75%</span>
                </div>
                <div class="h-2 bg-gray-100 dark:bg-gray-800 rounded-full overflow-hidden">
                    <div class="h-full bg-primary-500 rounded-full" style="width: 75%"></div>
                </div>
            </div>
            <div class="space-y-2">
                <div class="flex justify-between text-sm">
                    <span class="text-gray-600 dark:text-gray-400">Memory</span>
                    <span class="font-medium text-gray-900 dark:text-white">42%</span>
                </div>
                <div class="h-2 bg-gray-100 dark:bg-gray-800 rounded-full overflow-hidden">
                    <div class="h-full bg-indigo-500 rounded-full" style="width: 42%"></div>
                </div>
            </div>
        </div>
      </div>
    </div>
  </DashboardLayout>
</template>
