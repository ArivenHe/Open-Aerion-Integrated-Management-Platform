<script setup>
import { DataBoard, Document } from '@element-plus/icons-vue'
import BaseSidebar from '@/components/BaseSidebar.vue'

defineProps({
  collapsed: {
    type: Boolean,
    default: false,
  },
})

const items = [
  { label: '仪表盘', route: '/', icon: DataBoard },
  { label: '记录中心', route: '/users', icon: Document },
]
</script>

<template>
  <BaseSidebar
    :collapsed="collapsed"
    mode="workspace"
    mark="OA"
    title="Open AIMP"
    subtitle="Personal Workspace"
    :items="items"
  />
</template>
