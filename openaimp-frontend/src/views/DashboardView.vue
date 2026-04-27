<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Calendar, DataAnalysis, Flag, Timer } from '@element-plus/icons-vue'
import { loadConfig } from '@/api/config'
import { fetchControlRecords, fetchFlightRecords } from '@/api/records'
import { getCurrentCid, getCurrentRbac, getCurrentUser, hasAdminAccess } from '@/utils/session'

const currentUser = ref(getCurrentUser())
const currentRbac = ref(getCurrentRbac())
const platform = ref(null)
const flightRecords = ref([])
const controlRecords = ref([])
const loading = ref(false)
const adminAccess = ref(hasAdminAccess())
const userCid = getCurrentCid()

const formatMinutes = (minutes) => {
  const safe = Number(minutes || 0)
  const hours = Math.floor(safe / 60)
  const mins = safe % 60
  return `${hours}h ${mins}m`
}

const formatDateTime = (value) => {
  if (!value) return '暂无'
  return new Date(value).toLocaleString('zh-CN', { hour12: false })
}

const displayName = computed(() => {
  const user = currentUser.value || {}
  return user.nickname || user.email || `CID ${user.cid || '--'}`
})

const statCards = computed(() => {
  const user = currentUser.value || {}
  return [
    {
      title: '飞行记录',
      value: user.flightRecordCount ?? 0,
      desc: `累计 ${formatMinutes(user.flightDurationMinutes)}`,
      icon: Flag,
      tone: 'sky',
    },
    {
      title: '管制记录',
      value: user.controlRecordCount ?? 0,
      desc: `累计 ${formatMinutes(user.controlDurationMinutes)}`,
      icon: Timer,
      tone: 'violet',
    },
    {
      title: '当前角色',
      value: currentRbac.value.roles?.length ?? 0,
      desc: (currentRbac.value.roles || []).join(' / ') || '普通用户',
      icon: DataAnalysis,
      tone: 'emerald',
    },
    {
      title: '最近登录',
      value: currentUser.value?.last_login_at ? '已记录' : '首次',
      desc: formatDateTime(currentUser.value?.last_login_at),
      icon: Calendar,
      tone: 'amber',
    },
  ]
})

const recentTimeline = computed(() => {
  const flights = flightRecords.value.slice(0, 3).map((item) => ({
    type: '飞行',
    title: `${item.callsign || '未命名航班'} · ${item.departureAirport || '----'} → ${item.arrivalAirport || '----'}`,
    time: item.startedAt,
    duration: item.durationMinutes,
  }))
  const controls = controlRecords.value.slice(0, 3).map((item) => ({
    type: '管制',
    title: `${item.position || item.facilityTypeName || '未命名席位'} · ${item.airport || '----'}`,
    time: item.startedAt,
    duration: item.durationMinutes,
  }))

  return [...flights, ...controls]
    .sort((a, b) => new Date(b.time || 0) - new Date(a.time || 0))
    .slice(0, 6)
})

const overviewItems = computed(() => [
  { label: '平台', value: platform.value?.platformName || 'Open AIMP' },
  { label: '用户规模', value: platform.value?.platformSignedUserCount || '--' },
  { label: '最后登录', value: formatDateTime(currentUser.value?.last_login_at) },
  { label: '后台状态', value: adminAccess.value ? '可访问 /admin' : '普通工作台' },
])

const fetchDashboard = async () => {
  if (!userCid) return
  loading.value = true
  try {
    const [platformPayload, flightPayload, controlPayload] = await Promise.all([
      loadConfig(),
      fetchFlightRecords(userCid),
      fetchControlRecords(userCid),
    ])
    platform.value = platformPayload.data
    flightRecords.value = flightPayload.data || []
    controlRecords.value = controlPayload.data || []
    adminAccess.value = hasAdminAccess()
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}

onMounted(fetchDashboard)
</script>

<template>
  <div class="page-shell dashboard-page">
    <section class="overview-strip">
      <div class="overview-strip__main">
        <div class="overview-strip__eyebrow">Flight Workspace</div>
        <h1>{{ displayName }}</h1>
        <p>
          你的飞行记录、管制记录、权限信息和平台状态已经汇总在这里。
          <span v-if="adminAccess">当前账号具备后台访问能力。</span>
        </p>
        <div class="overview-strip__tags">
          <el-tag effect="plain">{{ currentUser?.email || '未绑定邮箱' }}</el-tag>
          <el-tag type="success" effect="plain">CID {{ currentUser?.cid || '--' }}</el-tag>
          <el-tag v-for="role in currentRbac.roles || []" :key="role" effect="plain">{{ role }}</el-tag>
        </div>
      </div>

      <div class="overview-strip__side">
        <div v-for="item in overviewItems" :key="item.label" class="overview-mini-card">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
        </div>
        <el-button plain class="refresh-button" :loading="loading" @click="fetchDashboard">刷新工作台</el-button>
      </div>
    </section>

    <section class="stats-grid">
      <article v-for="card in statCards" :key="card.title" class="metric-card" :class="`metric-card--${card.tone}`">
        <div class="metric-card__icon">
          <el-icon><component :is="card.icon" /></el-icon>
        </div>
        <div class="metric-card__body">
          <span>{{ card.title }}</span>
          <strong>{{ card.value }}</strong>
          <p>{{ card.desc }}</p>
        </div>
      </article>
    </section>

    <section class="content-grid">
      <el-card shadow="never" class="app-panel-card timeline-panel">
        <template #header>
          <div class="panel-head">
            <span>最近活动</span>
            <el-tag type="primary" effect="plain">{{ recentTimeline.length }} 条</el-tag>
          </div>
        </template>

        <el-empty v-if="!recentTimeline.length" description="还没有飞行或管制记录" />

        <el-timeline v-else>
          <el-timeline-item
            v-for="item in recentTimeline"
            :key="`${item.type}-${item.title}-${item.time}`"
            :timestamp="formatDateTime(item.time)"
          >
            <div class="timeline-title">{{ item.title }}</div>
            <div class="timeline-meta">
              <el-tag size="small" :type="item.type === '飞行' ? 'success' : 'warning'">{{ item.type }}</el-tag>
              <span>时长 {{ formatMinutes(item.duration) }}</span>
            </div>
          </el-timeline-item>
        </el-timeline>
      </el-card>

      <el-card shadow="never" class="app-panel-card profile-panel">
        <template #header>
          <div class="panel-head">
            <span>账号概览</span>
            <el-tag effect="plain">{{ currentUser?.cid || '--' }}</el-tag>
          </div>
        </template>

        <el-descriptions :column="1" border>
          <el-descriptions-item label="昵称">{{ currentUser?.nickname || '未设置' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ currentUser?.email || '暂无' }}</el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ formatDateTime(currentUser?.registered_at) }}</el-descriptions-item>
          <el-descriptions-item label="最后登录">{{ formatDateTime(currentUser?.last_login_at) }}</el-descriptions-item>
          <el-descriptions-item label="平台地址">{{ platform?.platformUrl || '暂无' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
    </section>
  </div>
</template>

<style scoped>
.dashboard-page {
  gap: 18px;
}

.overview-strip {
  display: grid;
  grid-template-columns: minmax(0, 1.45fr) minmax(320px, 0.9fr);
  gap: 18px;
}

.overview-strip__main,
.overview-strip__side {
  border: 1px solid var(--border-color);
  background: var(--surface);
  box-shadow: var(--shadow-soft);
  border-radius: 22px;
}

.overview-strip__main {
  padding: 24px 26px;
}

.overview-strip__eyebrow {
  display: inline-block;
  font-size: 0.78rem;
  letter-spacing: 0.26em;
  text-transform: uppercase;
  color: var(--workspace-accent);
}

.overview-strip__main h1 {
  margin: 14px 0 10px;
  font-size: 2rem;
  line-height: 1.08;
  letter-spacing: -0.03em;
  color: var(--text-primary);
}

.overview-strip__main p {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.75;
  max-width: 720px;
}

.overview-strip__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 18px;
}

.overview-strip__side {
  padding: 16px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  align-content: start;
}

.overview-mini-card {
  min-height: 92px;
  padding: 14px 16px;
  border-radius: 16px;
  background: color-mix(in srgb, var(--surface-soft) 86%, var(--surface) 14%);
  border: 1px solid var(--border-color);
}

.overview-mini-card span {
  display: block;
  font-size: 0.82rem;
  color: var(--text-secondary);
}

.overview-mini-card strong {
  display: block;
  margin-top: 10px;
  font-size: 1.2rem;
  line-height: 1.35;
  color: var(--text-primary);
}

.refresh-button {
  grid-column: 1 / -1;
  min-height: 44px;
  border-radius: 14px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 18px;
}

.metric-card {
  display: flex;
  gap: 16px;
  padding: 20px;
  border-radius: 20px;
  border: 1px solid var(--border-color);
  background: var(--surface-elevated);
  box-shadow: var(--shadow-soft);
}

.metric-card__icon {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16px;
  font-size: 20px;
}

.metric-card__body {
  min-width: 0;
}

.metric-card__body span {
  display: block;
  font-size: 0.84rem;
  color: var(--text-secondary);
}

.metric-card__body strong {
  display: block;
  margin-top: 8px;
  font-size: 1.75rem;
  color: var(--text-primary);
}

.metric-card__body p {
  margin: 8px 0 0;
  color: var(--text-secondary);
}

.metric-card--sky .metric-card__icon {
  background: rgba(37, 99, 235, 0.08);
  color: #2563eb;
}

.metric-card--violet .metric-card__icon {
  background: rgba(124, 58, 237, 0.08);
  color: #7c3aed;
}

.metric-card--emerald .metric-card__icon {
  background: rgba(5, 150, 105, 0.08);
  color: #059669;
}

.metric-card--amber .metric-card__icon {
  background: rgba(217, 119, 6, 0.08);
  color: #d97706;
}

.content-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) minmax(320px, 0.85fr);
  gap: 18px;
}

.timeline-panel,
.profile-panel {
  min-height: 100%;
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  font-weight: 700;
  color: var(--text-primary);
}

.timeline-title {
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.6;
}

.timeline-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 6px;
  color: var(--text-secondary);
}

@media (max-width: 1280px) {
  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 1080px) {
  .overview-strip,
  .content-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 760px) {
  .overview-strip__side,
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .overview-strip__main {
    padding: 22px;
  }

  .overview-strip__main h1 {
    font-size: 1.9rem;
  }
}
</style>
