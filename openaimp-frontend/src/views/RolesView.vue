<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchCurrentRbac, fetchRbacCatalog } from '@/api/rbac'
import { getCurrentRbac } from '@/utils/session'

const loading = ref(false)
const currentAuth = ref(getCurrentRbac())
const catalog = ref({ roles: [], permissions: [] })
const catalogError = ref('')

const loadRbac = async () => {
  loading.value = true
  catalogError.value = ''
  try {
    const [currentPayload, catalogPayload] = await Promise.allSettled([
      fetchCurrentRbac(),
      fetchRbacCatalog(),
    ])

    if (currentPayload.status === 'fulfilled') {
      currentAuth.value = {
        roles: currentPayload.value.data.roles || [],
        permissions: currentPayload.value.data.permissions || [],
      }
    }

    if (catalogPayload.status === 'fulfilled') {
      catalog.value = catalogPayload.value.data || { roles: [], permissions: [] }
    } else {
      catalogError.value = catalogPayload.reason?.message || '当前账号没有 RBAC 目录查看权限'
    }
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}

onMounted(loadRbac)
</script>

<template>
  <div class="page-shell">
    <div class="page-head">
      <div>
        <h1>权限中心</h1>
        <p>这里展示当前账号拿到的角色、权限，以及系统 RBAC 目录。</p>
      </div>
      <el-button plain :loading="loading" @click="loadRbac">刷新</el-button>
    </div>

    <el-row :gutter="18">
      <el-col :xs="24" :lg="10">
        <el-card class="panel-card" shadow="never">
          <template #header>
            <div class="panel-head">
              <span>我的角色</span>
              <el-tag type="primary" effect="plain">{{ currentAuth.roles?.length || 0 }}</el-tag>
            </div>
          </template>

          <div class="tag-cloud">
            <el-tag v-for="role in currentAuth.roles || []" :key="role" effect="plain">{{ role }}</el-tag>
            <el-empty v-if="!(currentAuth.roles || []).length" description="暂无角色" />
          </div>
        </el-card>

        <el-card class="panel-card panel-card--stacked" shadow="never">
          <template #header>
            <div class="panel-head">
              <span>我的权限</span>
              <el-tag type="success" effect="plain">{{ currentAuth.permissions?.length || 0 }}</el-tag>
            </div>
          </template>

          <div class="tag-cloud">
            <el-tag v-for="permission in currentAuth.permissions || []" :key="permission" type="success" effect="plain">
              {{ permission }}
            </el-tag>
            <el-empty v-if="!(currentAuth.permissions || []).length" description="暂无权限" />
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="14">
        <el-card class="panel-card" shadow="never">
          <template #header>
            <div class="panel-head">
              <span>角色目录</span>
              <el-tag effect="plain">{{ catalog.roles?.length || 0 }}</el-tag>
            </div>
          </template>

          <el-alert v-if="catalogError" :title="catalogError" type="warning" show-icon :closable="false" class="panel-alert" />

          <el-table v-else :data="catalog.roles || []" stripe>
            <el-table-column prop="code" label="角色编码" min-width="180" />
            <el-table-column prop="name" label="角色名称" min-width="160" />
            <el-table-column prop="description" label="描述" min-width="220" show-overflow-tooltip />
            <el-table-column label="内置" width="90">
              <template #default="{ row }">
                <el-tag :type="row.builtin ? 'info' : 'primary'" effect="plain">
                  {{ row.builtin ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="权限" min-width="240">
              <template #default="{ row }">
                <div class="tag-cloud tag-cloud--inline">
                  <el-tag v-for="permission in row.permissions || []" :key="permission" size="small" effect="plain">
                    {{ permission }}
                  </el-tag>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-card class="panel-card panel-card--stacked" shadow="never">
          <template #header>
            <div class="panel-head">
              <span>权限目录</span>
              <el-tag effect="plain">{{ catalog.permissions?.length || 0 }}</el-tag>
            </div>
          </template>

          <el-table v-if="!catalogError" :data="catalog.permissions || []" stripe>
            <el-table-column prop="code" label="权限编码" min-width="220" />
            <el-table-column prop="name" label="权限名称" min-width="180" />
            <el-table-column prop="description" label="描述" min-width="220" show-overflow-tooltip />
            <el-table-column label="内置" width="90">
              <template #default="{ row }">
                <el-tag :type="row.builtin ? 'info' : 'primary'" effect="plain">
                  {{ row.builtin ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.page-shell {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.page-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.panel-card {
  border-radius: 24px;
  border: 1px solid var(--border-color);
  background: var(--surface);
  box-shadow: var(--shadow-soft);
}

.panel-card--stacked {
  margin-top: 18px;
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  font-weight: 700;
  color: var(--text-primary);
}

.panel-alert {
  margin-bottom: 16px;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-cloud--inline {
  gap: 6px;
}

@media (max-width: 900px) {
  .page-head {
    flex-direction: column;
    align-items: flex-start;
  }
}

</style>
