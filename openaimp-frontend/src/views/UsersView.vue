<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createControlRecord,
  createFlightRecord,
  deleteControlRecord,
  deleteFlightRecord,
  fetchControlRecords,
  fetchFlightRecords,
  updateControlRecord,
  updateFlightRecord,
} from '@/api/records'
import { getCurrentCid, getCurrentUser } from '@/utils/session'

const userCid = getCurrentCid()
const currentUser = ref(getCurrentUser())
const loading = ref(false)
const flightRecords = ref([])
const controlRecords = ref([])
const activeTab = ref('flight')

const flightDialogVisible = ref(false)
const controlDialogVisible = ref(false)
const flightEditingId = ref(null)
const controlEditingId = ref(null)

const flightForm = reactive({
  callsign: '',
  departureAirport: '',
  arrivalAirport: '',
  startedAt: '',
  endedAt: '',
  durationMinutes: 0,
  remarks: '',
})

const controlForm = reactive({
  position: '',
  airport: '',
  facilityType: 3,
  startedAt: '',
  endedAt: '',
  durationMinutes: 0,
  remarks: '',
})

const facilityTypeOptions = [
  { label: 'Delivery', value: 1 },
  { label: 'Ground', value: 2 },
  { label: 'Tower', value: 3 },
  { label: 'Approach', value: 4 },
  { label: 'Center', value: 5 },
]

const formatDateTime = (value) => {
  if (!value) return '--'
  return new Date(value).toLocaleString('zh-CN', { hour12: false })
}

const formatInputDateTime = (value) => {
  if (!value) return ''
  const date = new Date(value)
  const pad = (num) => String(num).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`
}

const normalizePayload = (form) => ({
  ...form,
  startedAt: form.startedAt || null,
  endedAt: form.endedAt || null,
  durationMinutes: Number(form.durationMinutes || 0),
})

const totalFlightMinutes = computed(() =>
  flightRecords.value.reduce((sum, item) => sum + Number(item.durationMinutes || 0), 0)
)

const totalControlMinutes = computed(() =>
  controlRecords.value.reduce((sum, item) => sum + Number(item.durationMinutes || 0), 0)
)

const formatMinutes = (minutes) => {
  const safe = Number(minutes || 0)
  const hours = Math.floor(safe / 60)
  const mins = safe % 60
  return `${hours}h ${mins}m`
}

const resetFlightForm = () => {
  flightEditingId.value = null
  Object.assign(flightForm, {
    callsign: '',
    departureAirport: '',
    arrivalAirport: '',
    startedAt: '',
    endedAt: '',
    durationMinutes: 0,
    remarks: '',
  })
}

const resetControlForm = () => {
  controlEditingId.value = null
  Object.assign(controlForm, {
    position: '',
    airport: '',
    facilityType: 3,
    startedAt: '',
    endedAt: '',
    durationMinutes: 0,
    remarks: '',
  })
}

const fetchAllRecords = async () => {
  if (!userCid) return
  loading.value = true
  try {
    const [flightPayload, controlPayload] = await Promise.all([
      fetchFlightRecords(userCid),
      fetchControlRecords(userCid),
    ])
    flightRecords.value = flightPayload.data || []
    controlRecords.value = controlPayload.data || []
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}

const openCreateFlight = () => {
  resetFlightForm()
  flightDialogVisible.value = true
}

const openEditFlight = (record) => {
  flightEditingId.value = record.id
  Object.assign(flightForm, {
    callsign: record.callsign || '',
    departureAirport: record.departureAirport || '',
    arrivalAirport: record.arrivalAirport || '',
    startedAt: formatInputDateTime(record.startedAt),
    endedAt: formatInputDateTime(record.endedAt),
    durationMinutes: Number(record.durationMinutes || 0),
    remarks: record.remarks || '',
  })
  flightDialogVisible.value = true
}

const submitFlight = async () => {
  try {
    const payload = normalizePayload(flightForm)
    if (flightEditingId.value) {
      await updateFlightRecord(flightEditingId.value, payload)
      ElMessage.success('飞行记录已更新')
    } else {
      await createFlightRecord(userCid, payload)
      ElMessage.success('飞行记录已创建')
    }
    flightDialogVisible.value = false
    resetFlightForm()
    await fetchAllRecords()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const removeFlight = async (record) => {
  try {
    await ElMessageBox.confirm(`确定删除飞行记录 ${record.callsign || record.id} 吗？`, '删除确认', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消',
    })
    await deleteFlightRecord(record.id)
    ElMessage.success('飞行记录已删除')
    await fetchAllRecords()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const openCreateControl = () => {
  resetControlForm()
  controlDialogVisible.value = true
}

const openEditControl = (record) => {
  controlEditingId.value = record.id
  Object.assign(controlForm, {
    position: record.position || '',
    airport: record.airport || '',
    facilityType: Number(record.facilityType || 3),
    startedAt: formatInputDateTime(record.startedAt),
    endedAt: formatInputDateTime(record.endedAt),
    durationMinutes: Number(record.durationMinutes || 0),
    remarks: record.remarks || '',
  })
  controlDialogVisible.value = true
}

const submitControl = async () => {
  try {
    const payload = normalizePayload(controlForm)
    if (controlEditingId.value) {
      await updateControlRecord(controlEditingId.value, payload)
      ElMessage.success('管制记录已更新')
    } else {
      await createControlRecord(userCid, payload)
      ElMessage.success('管制记录已创建')
    }
    controlDialogVisible.value = false
    resetControlForm()
    await fetchAllRecords()
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const removeControl = async (record) => {
  try {
    await ElMessageBox.confirm(`确定删除管制记录 ${record.position || record.id} 吗？`, '删除确认', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消',
    })
    await deleteControlRecord(record.id)
    ElMessage.success('管制记录已删除')
    await fetchAllRecords()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

onMounted(fetchAllRecords)
</script>

<template>
  <div class="page-shell">
    <div class="page-head">
      <div>
        <h1>记录中心</h1>
        <p>这里维护当前账号的飞行记录与管制记录，所有操作都会回写你的汇总数据。</p>
      </div>
      <div class="head-actions">
        <el-tag type="info" effect="plain">CID {{ currentUser?.cid || userCid || '--' }}</el-tag>
        <el-button plain :loading="loading" @click="fetchAllRecords">刷新</el-button>
      </div>
    </div>

    <el-row :gutter="18">
      <el-col :xs="24" :sm="12">
        <el-card class="summary-card" shadow="never">
          <span class="summary-card__label">飞行记录</span>
          <strong class="summary-card__value">{{ flightRecords.length }}</strong>
          <p class="summary-card__desc">累计时长 {{ formatMinutes(totalFlightMinutes) }}</p>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12">
        <el-card class="summary-card" shadow="never">
          <span class="summary-card__label">管制记录</span>
          <strong class="summary-card__value">{{ controlRecords.length }}</strong>
          <p class="summary-card__desc">累计时长 {{ formatMinutes(totalControlMinutes) }}</p>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="table-card" shadow="never">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="飞行记录" name="flight">
          <div class="table-toolbar">
            <div class="table-toolbar__copy">维护你的航班、机场、起降时间与备注。</div>
            <el-button type="primary" @click="openCreateFlight">新增飞行记录</el-button>
          </div>

          <el-table :data="flightRecords" stripe>
            <el-table-column prop="callsign" label="航班号" min-width="130" />
            <el-table-column label="航路" min-width="180">
              <template #default="{ row }">{{ row.departureAirport || '----' }} → {{ row.arrivalAirport || '----' }}</template>
            </el-table-column>
            <el-table-column label="开始时间" min-width="180">
              <template #default="{ row }">{{ formatDateTime(row.startedAt) }}</template>
            </el-table-column>
            <el-table-column label="结束时间" min-width="180">
              <template #default="{ row }">{{ formatDateTime(row.endedAt) }}</template>
            </el-table-column>
            <el-table-column prop="durationMinutes" label="分钟" width="100" />
            <el-table-column prop="remarks" label="备注" min-width="200" show-overflow-tooltip />
            <el-table-column label="操作" width="170" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="openEditFlight(row)">编辑</el-button>
                <el-button link type="danger" @click="removeFlight(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="管制记录" name="control">
          <div class="table-toolbar">
            <div class="table-toolbar__copy">维护席位、机场、管制时间与备注。</div>
            <el-button type="primary" @click="openCreateControl">新增管制记录</el-button>
          </div>

          <el-table :data="controlRecords" stripe>
            <el-table-column prop="position" label="席位" min-width="140" />
            <el-table-column prop="airport" label="机场" width="120" />
            <el-table-column prop="facilityTypeName" label="类型" width="140" />
            <el-table-column label="开始时间" min-width="180">
              <template #default="{ row }">{{ formatDateTime(row.startedAt) }}</template>
            </el-table-column>
            <el-table-column label="结束时间" min-width="180">
              <template #default="{ row }">{{ formatDateTime(row.endedAt) }}</template>
            </el-table-column>
            <el-table-column prop="durationMinutes" label="分钟" width="100" />
            <el-table-column prop="remarks" label="备注" min-width="200" show-overflow-tooltip />
            <el-table-column label="操作" width="170" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="openEditControl(row)">编辑</el-button>
                <el-button link type="danger" @click="removeControl(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="flightDialogVisible" :title="flightEditingId ? '编辑飞行记录' : '新增飞行记录'" width="620px" @closed="resetFlightForm">
      <el-form label-position="top">
        <div class="dialog-grid dialog-grid--three">
          <el-form-item label="航班号">
            <el-input v-model="flightForm.callsign" />
          </el-form-item>
          <el-form-item label="出发机场">
            <el-input v-model="flightForm.departureAirport" />
          </el-form-item>
          <el-form-item label="到达机场">
            <el-input v-model="flightForm.arrivalAirport" />
          </el-form-item>
        </div>

        <div class="dialog-grid dialog-grid--two">
          <el-form-item label="开始时间">
            <el-input v-model="flightForm.startedAt" type="datetime-local" />
          </el-form-item>
          <el-form-item label="结束时间">
            <el-input v-model="flightForm.endedAt" type="datetime-local" />
          </el-form-item>
        </div>

        <el-form-item label="时长（分钟）">
          <el-input-number v-model="flightForm.durationMinutes" :min="0" :step="10" />
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="flightForm.remarks" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="flightDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitFlight">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="controlDialogVisible" :title="controlEditingId ? '编辑管制记录' : '新增管制记录'" width="620px" @closed="resetControlForm">
      <el-form label-position="top">
        <div class="dialog-grid dialog-grid--three">
          <el-form-item label="席位">
            <el-input v-model="controlForm.position" />
          </el-form-item>
          <el-form-item label="机场">
            <el-input v-model="controlForm.airport" />
          </el-form-item>
          <el-form-item label="设施类型">
            <el-select v-model="controlForm.facilityType">
              <el-option v-for="option in facilityTypeOptions" :key="option.value" :label="option.label" :value="option.value" />
            </el-select>
          </el-form-item>
        </div>

        <div class="dialog-grid dialog-grid--two">
          <el-form-item label="开始时间">
            <el-input v-model="controlForm.startedAt" type="datetime-local" />
          </el-form-item>
          <el-form-item label="结束时间">
            <el-input v-model="controlForm.endedAt" type="datetime-local" />
          </el-form-item>
        </div>

        <el-form-item label="时长（分钟）">
          <el-input-number v-model="controlForm.durationMinutes" :min="0" :step="10" />
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="controlForm.remarks" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="controlDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitControl">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.head-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.summary-card,
.table-card {
  border-radius: 24px;
  border: 1px solid var(--border-color);
  background: var(--surface);
  box-shadow: var(--shadow-soft);
}

.summary-card__label {
  display: block;
  color: var(--text-secondary);
  font-size: 0.88rem;
}

.summary-card__value {
  display: block;
  margin-top: 10px;
  font-size: 2rem;
  color: var(--text-primary);
}

.summary-card__desc {
  margin: 10px 0 0;
  color: var(--text-secondary);
}

.table-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 18px;
}

.table-toolbar__copy {
  color: var(--text-secondary);
}

.dialog-grid {
  display: grid;
  gap: 16px;
}

.dialog-grid--two {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.dialog-grid--three {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

@media (max-width: 920px) {
  .page-head,
  .table-toolbar {
    flex-direction: column;
    align-items: flex-start;
  }
}

@media (max-width: 760px) {
  .dialog-grid--two,
  .dialog-grid--three {
    grid-template-columns: 1fr;
  }
}

</style>
