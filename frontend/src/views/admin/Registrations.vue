<template>
  <div class="admin-regs">
    <div class="page-header">
      <div class="ph-top">
        <div>
          <span class="page-kicker">REGISTRATION REVIEW</span>
          <h2>报名审核</h2>
        </div>
        <div class="filter-bar">
          <el-select v-model="filterEventId" placeholder="全部赛事" clearable @change="fetchData">
            <el-option v-for="e in events" :key="e.id" :label="e.name" :value="e.id" />
          </el-select>
          <el-select v-model="filterStatus" placeholder="全部状态" clearable @change="fetchData">
            <el-option :value="0" label="待审核" />
            <el-option :value="1" label="已通过" />
            <el-option :value="2" label="已拒绝" />
            <el-option :value="3" label="已取消" />
          </el-select>
        </div>
      </div>
    </div>

    <el-card>
      <el-table :data="registrations" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="eventName" label="赛事名称" min-width="160" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="100" />
        <el-table-column prop="idCard" label="身份证号" width="190" />
        <el-table-column prop="shirtSize" label="尺码" width="70" align="center" />
        <el-table-column prop="emergencyContact" label="紧急联系人" width="130" />
        <el-table-column label="报名时间" width="170"><template #default="{ row }">{{ fmt(row.applyTime) }}</template></el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }"><el-tag :type="st(row.status)" effect="light" round>{{ ss(row.status) }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button type="success" size="small" plain @click="audit(row.id, 1)">通过</el-button>
              <el-button type="danger" size="small" plain @click="audit(row.id, 2)">拒绝</el-button>
            </template>
            <span v-else class="no-action">-</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../../api'

const filterEventId = ref(null)
const filterStatus = ref(null)
const events = ref([])
const registrations = ref([])
const loading = ref(false)
const ss = s => ['待审核', '已通过', '已拒绝', '已取消'][s] || '未知'
const st = s => ['warning', 'success', 'danger', 'info'][s] || 'info'
const fmt = s => s ? s.replace('T', ' ') : ''

onMounted(async () => {
  const er = await api.adminGetEvents()
  if (er.code === 200) events.value = er.data
  fetchData()
})

async function fetchData() {
  loading.value = true
  try {
    const r = await api.adminGetRegistrations({ eventId: filterEventId.value, status: filterStatus.value })
    if (r.code === 200) registrations.value = r.data
  } finally {
    loading.value = false
  }
}

const audit = (id, status) => {
  const action = status === 1 ? '通过' : '拒绝'
  ElMessageBox.confirm(`确认${action}这条报名申请吗？`, '提示', { type: 'warning' }).then(async () => {
    const r = await api.adminAuditRegistration(id, status)
    if (r.code === 200) {
      ElMessage.success('操作成功')
      fetchData()
    } else {
      ElMessage.error(r.msg)
    }
  })
}
</script>

<style scoped>
.admin-regs { display: flex; flex-direction: column; gap: 24px; }
.page-header { padding: 24px 30px; background: linear-gradient(135deg, #0F766E, #06B6D4); border-radius: var(--radius-xl); }
.ph-top { display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 16px; }
.page-kicker { font-size: 11px; font-weight: 700; letter-spacing: 0.12em; color: #A7F3D0; font-family: var(--font-heading); }
.page-header h2 { font-family: var(--font-heading); font-size: 26px; font-weight: 700; color: #fff; margin-top: 4px; }
.filter-bar { display: flex; gap: 12px; }
.filter-bar .el-select { width: 160px; }
.no-action { color: var(--muted); }
</style>
