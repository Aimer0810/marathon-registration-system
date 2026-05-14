<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :lg="6" v-for="item in statItems" :key="item.key">
        <div class="stat-card">
          <div :class="['stat-icon', item.iconClass]"><el-icon><component :is="item.icon" /></el-icon></div>
          <div class="stat-body">
            <span class="stat-value">{{ animatedValues[item.key] }}</span>
            <span class="stat-label">{{ item.label }}</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="table-row">
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <div class="card-head">最近报名 <el-tag size="small" round>{{ recentRegistrations.length }}</el-tag></div>
          </template>
          <el-table :data="recentRegistrations" size="small" v-loading="loading" stripe>
            <el-table-column prop="realName" label="姓名" width="100" />
            <el-table-column prop="eventName" label="赛事" min-width="140" show-overflow-tooltip />
            <el-table-column label="时间" width="170"><template #default="{ row }">{{ fmt(row.applyTime) }}</template></el-table-column>
            <el-table-column label="状态" width="90" align="center">
              <template #default="{ row }"><el-tag :type="st(row.status)" size="small" effect="light" round>{{ ss(row.status) }}</el-tag></template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!loading && recentRegistrations.length === 0" description="暂无报名记录" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <div class="card-head">最新留言 <el-tag size="small" round>{{ recentMessages.length }}</el-tag></div>
          </template>
          <el-table :data="recentMessages" size="small" v-loading="loading" stripe>
            <el-table-column prop="userName" label="用户" width="100" />
            <el-table-column prop="content" label="内容" show-overflow-tooltip min-width="160" />
            <el-table-column label="时间" width="170"><template #default="{ row }">{{ fmt(row.createTime) }}</template></el-table-column>
          </el-table>
          <el-empty v-if="!loading && recentMessages.length === 0" description="暂无留言" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { User, Trophy, Document, Clock } from '@element-plus/icons-vue'
import api from '../../api'

const loading = ref(false)
const stats = ref({ userCount: 0, eventCount: 0, regCount: 0, pendingCount: 0 })
const animatedValues = reactive({ userCount: 0, eventCount: 0, regCount: 0, pendingCount: 0 })
const recentRegistrations = ref([])
const recentMessages = ref([])

const statItems = [
  { key: 'userCount', label: '注册用户', icon: User, iconClass: 'c-blue' },
  { key: 'eventCount', label: '赛事总数', icon: Trophy, iconClass: 'c-cyan' },
  { key: 'regCount', label: '报名总数', icon: Document, iconClass: 'c-amber' },
  { key: 'pendingCount', label: '待审核', icon: Clock, iconClass: 'c-rose' }
]

const ss = s => ['待审核', '已通过', '已拒绝', '已取消'][s] || '未知'
const st = s => ['warning', 'success', 'danger', 'info'][s] || 'info'
const fmt = s => s ? s.replace('T', ' ') : ''

const animate = (key, target) => {
  const start = animatedValues[key]
  const dur = 600
  const t0 = performance.now()
  const step = (t) => {
    const p = Math.min((t - t0) / dur, 1)
    animatedValues[key] = Math.round(start + (target - start) * (1 - Math.pow(1 - p, 3)))
    if (p < 1) requestAnimationFrame(step)
  }
  requestAnimationFrame(step)
}

onMounted(async () => {
  loading.value = true
  try {
    const [u, e, r, m] = await Promise.all([
      api.adminGetUsers(''),
      api.adminGetEvents(),
      api.adminGetRegistrations({}),
      api.adminGetMessages()
    ])
    if (u.code === 200) stats.value.userCount = u.data.length
    if (e.code === 200) stats.value.eventCount = e.data.length
    if (r.code === 200) {
      stats.value.regCount = r.data.length
      stats.value.pendingCount = r.data.filter(x => x.status === 0).length
      recentRegistrations.value = r.data.slice(0, 5)
    }
    if (m.code === 200) recentMessages.value = m.data.slice(0, 5)
    statItems.forEach(s => animate(s.key, stats.value[s.key]))
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.stat-card { display: flex; align-items: center; gap: 16px; padding: 24px; background: var(--surface); border: 1px solid var(--border); border-radius: var(--radius-lg); backdrop-filter: blur(20px); box-shadow: var(--shadow-sm); margin-bottom: 20px; transition: all 0.2s ease; cursor: pointer; }
.stat-card:hover { transform: translateY(-2px); box-shadow: var(--shadow-md); }
.stat-icon { width: 54px; height: 54px; border-radius: 14px; display: flex; align-items: center; justify-content: center; font-size: 24px; color: #fff; flex-shrink: 0; }
.c-blue { background: linear-gradient(135deg, #3B82F6, #1D4ED8); }
.c-cyan { background: linear-gradient(135deg, #06B6D4, #0891B2); }
.c-amber { background: linear-gradient(135deg, #F59E0B, #D97706); }
.c-rose { background: linear-gradient(135deg, #E11D48, #BE123C); }
.stat-value { display: block; font-family: var(--font-heading); font-size: 32px; font-weight: 700; color: var(--ink); line-height: 1.15; }
.stat-label { font-size: 13px; color: var(--muted); font-weight: 500; }
.table-row { margin-top: 0; }
.card-head { display: flex; justify-content: space-between; align-items: center; }
</style>
