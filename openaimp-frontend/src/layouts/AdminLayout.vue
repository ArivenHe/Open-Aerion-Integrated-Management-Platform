<script setup>
import { ref } from 'vue'
import AppHeader from '@/components/AppHeader.vue'
import AdminSidebar from '@/components/AdminSidebar.vue'

const sidebarCollapsed = ref(false)

const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}
</script>

<template>
  <el-container class="admin-layout">
    <el-aside :width="sidebarCollapsed ? '76px' : '280px'" class="admin-layout__aside">
      <AdminSidebar :collapsed="sidebarCollapsed" />
    </el-aside>
    <el-container>
      <el-header class="admin-layout__header">
        <AppHeader mode="admin" @toggle-sidebar="toggleSidebar" />
      </el-header>
      <el-main class="admin-layout__main">
        <RouterView />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.admin-layout {
  height: 100vh;
  background: var(--app-bg);
  overflow: hidden;
}

.admin-layout__aside {
  height: 100vh;
  transition: width 0.25s ease;
  background: var(--surface);
  border-right: 1px solid var(--border-color);
  overflow: hidden;
}

.admin-layout__header {
  padding: 0;
  height: 68px;
}

.admin-layout__main {
  padding: 20px;
  height: calc(100vh - 68px);
  overflow-y: auto;
  min-height: 0;
}

:deep(.el-container) {
  min-height: 0;
}

@media (max-width: 900px) {
  .admin-layout__main {
    padding: 16px;
  }
}

</style>
