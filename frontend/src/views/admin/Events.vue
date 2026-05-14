<template>
  <div class="admin-events">
    <div class="page-header">
      <div class="ph-top">
        <div>
          <span class="page-kicker">EVENT MANAGEMENT</span>
          <h2>赛事管理</h2>
        </div>
        <el-button type="primary" size="large" @click="handleAdd" :icon="Plus">新增赛事</el-button>
      </div>
    </div>

    <el-card>
      <el-table :data="events" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="name" label="赛事名称" min-width="180" />
        <el-table-column prop="location" label="地点" width="120" />
        <el-table-column prop="eventDate" label="比赛日期" width="130" />
        <el-table-column prop="quota" label="名额" width="80" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" effect="light" round>{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" plain @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" plain @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" top="5vh">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-row :gutter="16">
          <el-col :span="24"><el-form-item label="赛事名称" prop="name"><el-input v-model="form.name" placeholder="例如：2026 北京马拉松" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="地点" prop="location"><el-input v-model="form.location" placeholder="例如：北京" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="比赛日期" prop="eventDate"><el-date-picker v-model="form.eventDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="报名开始" prop="regStartTime"><el-date-picker v-model="form.regStartTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="报名截止" prop="regEndTime"><el-date-picker v-model="form.regEndTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8"><el-form-item label="名额" prop="quota"><el-input-number v-model="form.quota" :min="1" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="报名费（元）" prop="entryFee"><el-input-number v-model="form.entryFee" :precision="2" :min="0" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="状态" prop="status"><el-select v-model="form.status" style="width:100%"><el-option :value="0" label="未开始" /><el-option :value="1" label="报名中" /><el-option :value="2" label="已截止" /><el-option :value="3" label="已结束" /></el-select></el-form-item></el-col>
        </el-row>
        <el-form-item label="赛事介绍" prop="description"><el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入路线、规则等相关信息" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEvent">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import api from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const events = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = ref({})
const formRef = ref()
const editId = ref(null)

const statusText = s => ['未开始', '报名中', '已截止', '已结束'][s] || '未知'
const statusType = s => ['info', '', 'warning', 'danger'][s] || 'info'
const normalizeDateTime = v => v ? String(v).replace('T', ' ').slice(0, 19) : ''

const validateSchedule = (_, __, cb) => {
  const { eventDate, regStartTime, regEndTime } = form.value
  if (!eventDate || !regStartTime || !regEndTime) { cb(); return }
  const ed = new Date(`${eventDate} 00:00:00`.replace(/-/g, '/'))
  const s = new Date(regStartTime.replace(/-/g, '/'))
  const e = new Date(regEndTime.replace(/-/g, '/'))
  if (isNaN(s.getTime()) || isNaN(e.getTime()) || isNaN(ed.getTime())) { cb(new Error('时间格式不正确')); return }
  if (s > e) { cb(new Error('开始时间不能晚于结束时间')); return }
  if (e >= ed) { cb(new Error('报名截止时间必须早于比赛日期')); return }
  cb()
}

const rules = {
  name: [{ required: true, message: '请输入赛事名称', trigger: 'blur' }],
  location: [{ required: true, message: '请输入地点', trigger: 'blur' }],
  eventDate: [{ required: true, message: '请选择比赛日期', trigger: 'change' }],
  regStartTime: [{ required: true, message: '请选择报名开始时间', trigger: 'change' }, { validator: validateSchedule, trigger: 'change' }],
  regEndTime: [{ required: true, message: '请选择报名截止时间', trigger: 'change' }, { validator: validateSchedule, trigger: 'change' }],
  quota: [{ required: true, message: '请输入名额', trigger: 'change' }],
  entryFee: [{ required: true, message: '请输入报名费', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

onMounted(() => fetchEvents())

const fetchEvents = async () => {
  loading.value = true
  try {
    const r = await api.adminGetEvents()
    if (r.code === 200) events.value = r.data
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增赛事'
  form.value = { name: '', location: '', eventDate: '', regStartTime: '', regEndTime: '', quota: 100, entryFee: 0, status: 0, description: '' }
  editId.value = null
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑赛事'
  form.value = { ...row, eventDate: row.eventDate ? String(row.eventDate).slice(0, 10) : '', regStartTime: normalizeDateTime(row.regStartTime), regEndTime: normalizeDateTime(row.regEndTime) }
  editId.value = row.id
  dialogVisible.value = true
}

const saveEvent = async () => {
  try {
    const v = await formRef.value.validate().catch(() => false)
    if (!v) return
    const p = { ...form.value, eventDate: form.value.eventDate, regStartTime: normalizeDateTime(form.value.regStartTime), regEndTime: normalizeDateTime(form.value.regEndTime) }
    const r = editId.value ? await api.adminUpdateEvent(editId.value, p) : await api.adminAddEvent(p)
    if (r.code !== 200) { ElMessage.error(r.msg || '保存失败'); return }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchEvents()
  } catch (e) {
    ElMessage.error(e?.response?.data?.msg || e?.message || '保存失败')
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确认删除该赛事吗？', '提示', { type: 'warning' }).then(async () => {
    await api.adminDeleteEvent(id)
    ElMessage.success('删除成功')
    fetchEvents()
  })
}
</script>

<style scoped>
.admin-events { display: flex; flex-direction: column; gap: 24px; }
.page-header { padding: 24px 30px; background: linear-gradient(135deg, #1E3A8A, #3B82F6); border-radius: var(--radius-xl); }
.ph-top { display: flex; justify-content: space-between; align-items: flex-start; }
.page-kicker { font-size: 11px; font-weight: 700; letter-spacing: 0.12em; color: #FBBF24; font-family: var(--font-heading); }
.page-header h2 { font-family: var(--font-heading); font-size: 26px; font-weight: 700; color: #fff; margin-top: 4px; }
</style>
