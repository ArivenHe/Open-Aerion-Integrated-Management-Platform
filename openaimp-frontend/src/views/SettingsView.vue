<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { loadConfig, updateConfig } from '@/api/config'

const loading = ref(false)
const saving = ref(false)
const form = reactive({
  id: null,
  platformName: '',
  platformDescription: '',
  platformLogo: '',
  platformUrl: '',
  platformSignedUserCount: '',
  platformCreateTime: '',
})

const fetchConfig = async () => {
  loading.value = true
  try {
    const payload = await loadConfig()
    Object.assign(form, payload.data || {})
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}

const saveConfig = async () => {
  saving.value = true
  try {
    await updateConfig({ ...form })
    ElMessage.success('平台配置已更新')
    await fetchConfig()
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    saving.value = false
  }
}

onMounted(fetchConfig)
</script>

<template>
  <div class="page-shell">
    <div class="page-head">
      <div>
        <h1>平台设置</h1>
        <p>这里对接的是后端已有的 `platform/config` 与 `platform/config/update` 接口。</p>
      </div>
      <div class="head-actions">
        <el-button plain :loading="loading" @click="fetchConfig">刷新</el-button>
        <el-button type="primary" :loading="saving" @click="saveConfig">保存配置</el-button>
      </div>
    </div>

    <el-card class="settings-card" shadow="never">
      <el-form label-position="top">
        <div class="settings-grid settings-grid--two">
          <el-form-item label="平台名称">
            <el-input v-model="form.platformName" />
          </el-form-item>
          <el-form-item label="平台地址">
            <el-input v-model="form.platformUrl" />
          </el-form-item>
        </div>

        <el-form-item label="平台描述">
          <el-input v-model="form.platformDescription" type="textarea" :rows="4" />
        </el-form-item>

        <div class="settings-grid settings-grid--two">
          <el-form-item label="平台 Logo">
            <el-input v-model="form.platformLogo" />
          </el-form-item>
          <el-form-item label="注册用户数">
            <el-input v-model="form.platformSignedUserCount" />
          </el-form-item>
        </div>

        <el-form-item label="创建时间">
          <el-input v-model="form.platformCreateTime" />
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.head-actions {
  display: flex;
  gap: 12px;
}

.settings-card {
  border-radius: 24px;
  border: 1px solid var(--border-color);
  background: var(--surface);
  box-shadow: var(--shadow-soft);
}

.settings-grid {
  display: grid;
  gap: 16px;
}

.settings-grid--two {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

@media (max-width: 760px) {
  .settings-grid--two {
    grid-template-columns: 1fr;
  }
}
</style>
