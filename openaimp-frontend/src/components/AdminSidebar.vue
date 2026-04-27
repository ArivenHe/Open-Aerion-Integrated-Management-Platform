<script setup>
import { DataBoard, Key, Setting } from '@element-plus/icons-vue'
import BaseSidebar from '@/components/BaseSidebar.vue'

defineProps({
  collapsed: {
    type: Boolean,
    default: false,
  },
})

const items = [
  { label: '管理概览', route: '/admin', icon: DataBoard },
  { label: 'RBAC 管理', route: '/admin/roles', icon: Key },
  { label: '平台配置', route: '/admin/settings', icon: Setting },
]
</script>

<template>
  <BaseSidebar
    :collapsed="collapsed"
    mode="admin"
    mark="AD"
    title="Admin Control"
    subtitle="RBAC & Platform Ops"
    :items="items"
  />
</template>
