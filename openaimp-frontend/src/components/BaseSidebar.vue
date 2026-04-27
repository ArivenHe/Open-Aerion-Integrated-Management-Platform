<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const props = defineProps({
  collapsed: {
    type: Boolean,
    default: false,
  },
  mode: {
    type: String,
    default: 'workspace',
  },
  mark: {
    type: String,
    required: true,
  },
  title: {
    type: String,
    required: true,
  },
  subtitle: {
    type: String,
    required: true,
  },
  items: {
    type: Array,
    required: true,
  },
})

const sidebarClass = computed(() => `sidebar-shell sidebar-shell--${props.mode}`)
const route = useRoute()
const isActive = (path) => route.path === path
</script>

<template>
  <div :class="sidebarClass">
    <div class="sidebar-brand" :class="{ 'sidebar-brand--collapsed': collapsed }">
      <div class="sidebar-brand__mark">{{ mark }}</div>
      <div v-if="!collapsed" class="sidebar-brand__copy">
        <strong>{{ title }}</strong>
        <span>{{ subtitle }}</span>
      </div>
    </div>

    <nav class="sidebar-nav">
      <RouterLink
        v-for="item in items"
        :key="item.route"
        :to="item.route"
        class="sidebar-link"
        :class="{
          'sidebar-link--active': isActive(item.route),
          'sidebar-link--collapsed': collapsed,
        }"
      >
        <el-icon class="sidebar-link__icon"><component :is="item.icon" /></el-icon>
        <span v-if="!collapsed" class="sidebar-link__label">{{ item.label }}</span>
      </RouterLink>
    </nav>
  </div>
</template>

<style scoped>
.sidebar-shell {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 16px 12px;
  box-sizing: border-box;
  background: var(--surface);
  overflow-y: auto;
}

.sidebar-shell--workspace {
  --sidebar-accent: var(--workspace-accent);
  --sidebar-accent-soft: var(--workspace-accent-soft);
  --sidebar-accent-border: var(--workspace-accent-border);
}

.sidebar-shell--admin {
  --sidebar-accent: var(--admin-accent);
  --sidebar-accent-soft: var(--admin-accent-soft);
  --sidebar-accent-border: var(--admin-accent-border);
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px 8px 18px;
}

.sidebar-brand--collapsed {
  justify-content: center;
  padding-left: 0;
  padding-right: 0;
}

.sidebar-brand__mark {
  width: 42px;
  height: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 14px;
  background: var(--sidebar-accent-soft);
  border: 1px solid var(--sidebar-accent-border);
  color: var(--sidebar-accent);
  font-weight: 800;
  letter-spacing: 0.08em;
}

.sidebar-brand__copy {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.sidebar-brand__copy strong {
  color: var(--text-primary);
  font-size: 0.93rem;
  font-weight: 700;
}

.sidebar-brand__copy span {
  color: var(--text-tertiary);
  font-size: 0.78rem;
  margin-top: 4px;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.sidebar-link {
  display: flex;
  align-items: center;
  gap: 12px;
  min-height: 44px;
  padding: 0 12px;
  border-radius: 12px;
  text-decoration: none;
  color: var(--text-secondary);
  border: 1px solid transparent;
  transition: all 0.18s ease;
  font-weight: 500;
}

.sidebar-link:hover {
  color: var(--sidebar-accent);
  background: var(--surface-soft);
}

.sidebar-link--active {
  background: var(--sidebar-accent-soft);
  color: var(--sidebar-accent) !important;
  border: 1px solid var(--sidebar-accent-border);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.04);
}

.sidebar-link--collapsed {
  justify-content: center;
  padding: 0;
}

.sidebar-link__icon {
  font-size: 17px;
  flex-shrink: 0;
}

.sidebar-link__label {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

:global(.dark) .sidebar-link--active {
  color: var(--text-primary) !important;
}
</style>
