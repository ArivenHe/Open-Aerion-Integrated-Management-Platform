<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import { Menu, Moon, Promotion, Sunny } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { authEventName, clearSession, getCurrentRbac, getCurrentUser, hasAdminAccess } from '@/utils/session'
import { isDarkTheme, themeEventName, toggleTheme } from '@/utils/theme'

const emit = defineEmits(['toggle-sidebar'])
const props = defineProps({
  mode: {
    type: String,
    default: 'workspace',
  },
})
const router = useRouter()

const currentTime = ref('')
const timer = ref(null)
const isDark = ref(isDarkTheme())
const user = ref(getCurrentUser())
const rbac = ref(getCurrentRbac())
const adminVisible = ref(hasAdminAccess())

const syncSession = () => {
  user.value = getCurrentUser()
  rbac.value = getCurrentRbac()
  adminVisible.value = hasAdminAccess()
}

const syncTheme = () => {
  isDark.value = isDarkTheme()
}

const updateTime = () => {
  currentTime.value = new Date().toLocaleString('zh-CN', {
    hour12: false,
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  })
}

const handleToggleDark = () => {
  isDark.value = toggleTheme()
}

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定退出当前登录状态吗？', '退出登录', {
        type: 'warning',
        confirmButtonText: '退出',
        cancelButtonText: '取消',
      })
      clearSession()
      window.location.href = '/login'
    } catch {
      return
    }
  }
}

const displayName = computed(() => {
  const current = user.value
  if (!current) return '未登录'
  return current.nickname || [current.firstName, current.lastName].filter(Boolean).join(' ') || current.email || `CID ${current.cid}`
})

const displayEmail = computed(() => user.value?.email || '暂无邮箱')
const displayRoles = computed(() => (rbac.value?.roles || []).join(' / ') || '普通用户')
const avatarText = computed(() => (displayName.value || 'U').trim().charAt(0).toUpperCase())
const adminButtonLabel = computed(() => (props.mode === 'admin' ? '返回工作台' : '后台管理'))
const adminButtonIcon = computed(() => Promotion)
const pageTitle = computed(() => (props.mode === 'admin' ? 'Open AIMP 后台' : 'Open AIMP 控制台'))
const pageSubtitle = computed(() => (props.mode === 'admin' ? 'RBAC、平台配置与管理操作' : '账号、权限与记录统一入口'))

const jumpAdmin = () => {
  router.push(props.mode === 'admin' ? '/' : '/admin')
}

onMounted(() => {
  updateTime()
  timer.value = setInterval(updateTime, 1000)
  window.addEventListener(authEventName, syncSession)
  window.addEventListener(themeEventName, syncTheme)
})

onBeforeUnmount(() => {
  if (timer.value) clearInterval(timer.value)
  window.removeEventListener(authEventName, syncSession)
  window.removeEventListener(themeEventName, syncTheme)
})
</script>

<template>
  <div class="header-shell" :class="`header-shell--${props.mode}`">
    <div class="header-left">
      <el-button circle plain class="header-button" @click="emit('toggle-sidebar')">
        <el-icon><Menu /></el-icon>
      </el-button>
      <div class="header-copy">
        <h2>{{ pageTitle }}</h2>
        <p>{{ pageSubtitle }}</p>
      </div>
    </div>

    <div class="header-right">
      <el-button v-if="adminVisible" class="admin-cta" @click="jumpAdmin">
        <el-icon><component :is="adminButtonIcon" /></el-icon>
        <span>{{ adminButtonLabel }}</span>
      </el-button>

      <div class="header-time">{{ currentTime }}</div>

      <el-button circle plain class="header-button" @click="handleToggleDark">
        <el-icon><component :is="isDark ? Moon : Sunny" /></el-icon>
      </el-button>

      <el-dropdown trigger="click" @command="handleCommand">
        <div class="header-user">
          <div class="header-user__avatar">{{ avatarText }}</div>
          <div class="header-user__meta">
            <strong>{{ displayName }}</strong>
            <span>{{ displayRoles }}</span>
          </div>
        </div>

        <template #dropdown>
          <el-dropdown-menu>
            <div class="dropdown-summary">
              <strong>{{ displayName }}</strong>
              <span>{{ displayEmail }}</span>
            </div>
            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<style scoped>
.header-shell {
  height: 68px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 0 20px 0 18px;
  box-sizing: border-box;
  border-bottom: 1px solid var(--border-color);
  background: color-mix(in srgb, var(--surface) 94%, transparent);
  backdrop-filter: blur(20px);
  --header-accent: var(--workspace-accent);
  --header-accent-soft: var(--workspace-accent-soft);
  --header-accent-border: var(--workspace-accent-border);
}

.header-shell--admin {
  --header-accent: var(--admin-accent);
  --header-accent-soft: var(--admin-accent-soft);
  --header-accent-border: var(--admin-accent-border);
}

.header-left,
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-copy h2 {
  margin: 0;
  font-size: 0.96rem;
  font-weight: 700;
  letter-spacing: -0.01em;
  color: var(--text-primary);
}

.header-copy p {
  margin: 4px 0 0;
  font-size: 0.78rem;
  color: var(--text-secondary);
}

.header-button {
  width: 38px;
  height: 38px;
  border-radius: 12px;
  background: var(--surface-soft);
  border-color: var(--border-color);
}

.header-time {
  min-width: 116px;
  padding: 8px 14px;
  border-radius: 999px;
  border: 1px solid var(--border-color);
  background: color-mix(in srgb, var(--surface-soft) 82%, transparent);
  color: var(--text-secondary);
  font-size: 0.8rem;
  text-align: center;
}

.admin-cta {
  border-radius: 999px;
  border: 1px solid var(--header-accent-border);
  background: var(--header-accent-soft);
  color: var(--header-accent);
  box-shadow: none;
  padding-inline: 14px;
}

.admin-cta:hover {
  color: var(--header-accent);
  background: var(--header-accent-soft);
}

.header-user {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 5px 10px 5px 5px;
  border-radius: 999px;
  border: 1px solid transparent;
  cursor: pointer;
}

.header-user:hover {
  background: var(--surface-soft);
  border-color: var(--border-color);
}

.header-user__avatar {
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: var(--header-accent-soft);
  color: var(--header-accent);
  border: 1px solid var(--header-accent-border);
  font-weight: 700;
}

.header-user__meta {
  display: flex;
  flex-direction: column;
}

.header-user__meta strong {
  color: var(--text-primary);
  font-size: 0.9rem;
}

.header-user__meta span {
  color: var(--text-secondary);
  font-size: 0.76rem;
  margin-top: 2px;
}

.dropdown-summary {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 10px 16px 12px;
  color: var(--text-primary);
  border-bottom: 1px solid var(--border-color);
}

.dropdown-summary span {
  font-size: 0.82rem;
  color: var(--text-secondary);
}

@media (max-width: 860px) {
  .header-copy,
  .header-time,
  .header-user__meta {
    display: none;
  }

  .header-shell {
    padding: 0 14px;
  }
}

</style>
