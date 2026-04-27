<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchCurrentRbac, fetchRbacCatalog } from '@/api/rbac'
import { loadConfig } from '@/api/config'

const loading = ref(false)
const currentAuth = ref({ roles: [], permissions: [] })
const catalog = ref({ roles: [], permissions: [] })
const platform = ref(null)
const catalogError = ref('')

const summaryCards = computed(() => [
  { title: '角色总数', value: catalog.value.roles?.length || 0, desc: '当前 RBAC 角色目录' },
  { title: '权限总数', value: catalog.value.permissions?.length || 0, desc: '可分配权限项' },
  { title: '我的管理角色', value: currentAuth.value.roles?.length || 0, desc: (currentAuth.value.roles || []).join(' / ') || '暂无' },
  { title: '平台名称', value: platform.value?.platformName || 'Open AIMP', desc: platform.value?.platformUrl || '未配置地址' },
])

const fetchAdminOverview = async () => {
  loading.value = true
  catalogError.value = ''
  try {
    const [authPayload, catalogPayload, platformPayload] = await Promise.allSettled([
      fetchCurrentRbac(),
      fetchRbacCatalog(),
      loadConfig(),
    ])

    if (authPayload.status === 'fulfilled') {
      currentAuth.value = {
        roles: authPayload.value.data.roles || [],
        permissions: authPayload.value.data.permissions || [],
      }
    }
    if (catalogPayload.status === 'fulfilled') {
      catalog.value = catalogPayload.value.data || { roles: [], permissions: [] }
    } else {
      catalogError.value = catalogPayload.reason?.message || 'RBAC 目录不可读取'
    }
    if (platformPayload.status === 'fulfilled') {
      platform.value = platformPayload.value.data || null
    }
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}

onMounted(fetchAdminOverview)
</script>

<template>
  <div class="page-shell admin-page">
    <section class="admin-strip">
      <div class="admin-strip__main">
        <span class="admin-strip__eyebrow">Admin Console</span>
        <h1>后台管理中心</h1>
        <p>把平台配置、RBAC 目录和管理能力收拢到单独的 `/admin` 入口，保证普通工作台和后台操作分层清晰。</p>
      </div>
      <div class="admin-strip__actions">
        <router-link to="/admin/roles">
          <el-button type="primary">进入 RBAC 管理</el-button>
        </router-link>
        <router-link to="/admin/settings">
          <el-button plain>编辑平台配置</el-button>
        </router-link>
      </div>
    </section>

    <section class="stats-grid">
      <article v-for="card in summaryCards" :key="card.title" class="metric-card">
        <span>{{ card.title }}</span>
        <strong>{{ card.value }}</strong>
        <p>{{ card.desc }}</p>
      </article>
    </section>

    <section class="content-grid">
      <el-card class="app-panel-card" shadow="never">
        <template #header>
          <div class="panel-head">
            <span>我的管理权限</span>
            <el-button link :loading="loading" @click="fetchAdminOverview">刷新</el-button>
          </div>
        </template>

        <div class="tag-cloud">
          <el-tag v-for="role in currentAuth.roles || []" :key="role" effect="dark">{{ role }}</el-tag>
        </div>
        <div class="tag-cloud tag-cloud--secondary">
          <el-tag
            v-for="permission in currentAuth.permissions || []"
            :key="permission"
            type="success"
            effect="plain"
          >
            {{ permission }}
          </el-tag>
        </div>
      </el-card>

      <el-card class="app-panel-card" shadow="never">
        <template #header>
          <div class="panel-head">
            <span>平台状态</span>
          </div>
        </template>

        <el-alert v-if="catalogError" :title="catalogError" type="warning" :closable="false" show-icon />
        <el-descriptions v-else :column="1" border>
          <el-descriptions-item label="平台名称">{{ platform?.platformName || '暂无' }}</el-descriptions-item>
          <el-descriptions-item label="平台描述">{{ platform?.platformDescription || '暂无' }}</el-descriptions-item>
          <el-descriptions-item label="用户规模">{{ platform?.platformSignedUserCount || '暂无' }}</el-descriptions-item>
          <el-descriptions-item label="地址">{{ platform?.platformUrl || '暂无' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
    </section>
  </div>
</template>

<style scoped>
.admin-page {
  gap: 18px;
}

.admin-strip {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 18px;
  padding: 22px 24px;
  border: 1px solid var(--border-color);
  background: var(--surface);
  box-shadow: var(--shadow-soft);
  border-radius: 22px;
}

.admin-strip__eyebrow {
  display: inline-block;
  font-size: 0.78rem;
  letter-spacing: 0.24em;
  text-transform: uppercase;
  color: var(--admin-accent);
}

.admin-strip h1 {
  margin: 12px 0 10px;
  font-size: 1.9rem;
  letter-spacing: -0.03em;
  color: var(--text-primary);
}

.admin-strip p {
  margin: 0;
  max-width: 760px;
  color: var(--text-secondary);
  line-height: 1.72;
}

.admin-strip__actions {
  display: flex;
  gap: 12px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 18px;
}

.metric-card {
  padding: 20px;
  border-radius: 20px;
  border: 1px solid var(--border-color);
  background: var(--surface-elevated);
  box-shadow: var(--shadow-soft);
}

.metric-card span {
  display: block;
  font-size: 0.84rem;
  color: var(--text-secondary);
}

.metric-card strong {
  display: block;
  margin-top: 8px;
  font-size: 1.7rem;
  color: var(--text-primary);
}

.metric-card p {
  margin: 8px 0 0;
  color: var(--text-secondary);
}

.content-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(320px, 0.85fr);
  gap: 18px;
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  font-weight: 700;
  color: var(--text-primary);
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-cloud--secondary {
  margin-top: 16px;
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 960px) {
  .admin-strip,
  .content-grid {
    flex-direction: column;
    grid-template-columns: 1fr;
  }
}

@media (max-width: 760px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
