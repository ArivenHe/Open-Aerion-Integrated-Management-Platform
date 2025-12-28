<script setup>
</script>

<template>
  <div class="w-screen h-screen relative bg-gray-200 dark:bg-gray-800">
    <!-- Map Placeholder -->
    <div class="absolute inset-0 flex items-center justify-center text-gray-500 dark:text-gray-400">
        <div class="text-center">
            <h1 class="text-4xl font-bold mb-4">Online Map</h1>
            <p>Interactive Map Component will be loaded here.</p>
        </div>
    </div>
    
    <!-- Overlay UI -->
    <div class="absolute top-4 left-4 z-10">
        <router-link to="/" class="px-4 py-2 bg-white dark:bg-gray-900 shadow-md rounded-lg text-sm font-medium text-gray-700 dark:text-gray-200 hover:bg-gray-50 dark:hover:bg-gray-800">
            &larr; 返回仪表盘
        </router-link>
    </div>
  </div>
</template>