<template>
  <div class="my-regs">
    <div class="page-header">
      <span class="page-kicker">MY REGISTRATIONS</span>
      <h2>我的报名</h2>
      <p>查看全部报名记录以及当前审核状态。</p>
      <div class="header-stats">
        <div class="hs-item">
          <span class="hs-value">{{ registrations.length }}</span>
          <span class="hs-label">全部报名</span>
        </div>
        <div class="hs-divider"></div>
        <div class="hs-item">
          <span class="hs-value text-warning">{{ pendingCount }}</span>
          <span class="hs-label">待审核</span>
        </div>
        <div class="hs-divider"></div>
        <div class="hs-item">
          <span class="hs-value text-success">{{ approvedCount }}</span>
          <span class="hs-label">已通过</span>
        </div>
      </div>
    </div>

    <el-card>
      <el-table :data="registrations" v-loading="loading" stripe>
        <el-table-column prop="eventName" label="赛事名称" min-width="180" />
        <el-table-column prop="realName" label="真实姓名" width="100" />
        <el-table-column prop="idCard" label="身份证号" width="190" />
        <el-table-column prop="shirtSize" label="尺码" width="70" align="center" />
        <el-table-column label="报名时间" width="170">
          <template #default="{ row }">{{ fmt(row.applyTime) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="st(row.status)" size="small" effect="light" round>{{ ss(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" type="danger" size="small" plain @click="cancel(row.id)">取消</el-button>
            <span v-else class="no-action">-</span>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && registrations.length === 0" description="暂无报名记录">
        <el-button type="primary" @click="$router.push('/events')">去报名</el-button>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../../api'

const registrations = ref([])
const loading = ref(false)
const ss = s => ['待审核', '已通过', '已拒绝', '已取消'][s] || '未知'
const st = s => ['warning', 'success', 'danger', 'info'][s] || 'info'
const fmt = s => s ? s.replace('T', ' ') : ''

const pendingCount = computed(() => registrations.value.filter(r => r.status === 0).length)
const approvedCount = computed(() => registrations.value.filter(r => r.status === 1).length)

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const r = await api.getMyRegistrations()
    if (r.code === 200) registrations.value = r.data
  } finally {
    loading.value = false
  }
}

const cancel = (id) => {
  ElMessageBox.confirm('确认取消该报名吗？', '提示', { type: 'warning' }).then(async () => {
    const r = await api.cancelRegistration(id)
    if (r.code === 200) {
      ElMessage.success('取消成功')
      fetchData()
    } else {
      ElMessage.error(r.msg)
    }
  })
}
</script>

<style scoped>
.my-regs { display: flex; flex-direction: column; gap: 24px; }
.page-header { padding: 28px 30px; background: linear-gradient(135deg, #0F766E, #06B6D4); border-radius: var(--radius-xl); color: #fff; }
.page-kicker { font-size: 11px; font-weight: 700; letter-spacing: 0.12em; color: #A7F3D0; font-family: var(--font-heading); }
.page-header h2 { font-family: var(--font-heading); font-size: 28px; font-weight: 700; margin: 6px 0 8px; }
.page-header p { color: rgba(255,255,255,0.7); font-size: 14px; }
.header-stats { display: flex; align-items: center; gap: 28px; margin-top: 22px; padding-top: 20px; border-top: 1px solid rgba(255,255,255,0.12); }
.hs-value { display: block; font-family: var(--font-heading); font-size: 28px; font-weight: 700; }
.hs-label { font-size: 12px; color: rgba(255,255,255,0.65); }
.hs-divider { width: 1px; height: 40px; background: rgba(255,255,255,0.15); }
.text-warning { color: #FDE68A; }
.text-success { color: #A7F3D0; }
.no-action { color: var(--muted); }
</style>
