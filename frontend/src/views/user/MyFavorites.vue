<template>
  <div class="my-favs">
    <div class="page-header">
      <span class="page-kicker">MY FAVORITES</span>
      <h2>我的收藏</h2>
      <p>快速访问关注的赛事，并随时管理收藏记录。</p>
      <div class="header-stats">
        <div class="hs-item">
          <span class="hs-value">{{ favorites.length }}</span>
          <span class="hs-label">收藏赛事</span>
        </div>
        <div class="hs-divider"></div>
        <div class="hs-item">
          <span class="hs-value">{{ upcomingCount }}</span>
          <span class="hs-label">即将开赛</span>
        </div>
      </div>
    </div>

    <el-card>
      <el-table :data="favorites" v-loading="loading" stripe>
        <el-table-column prop="eventName" label="赛事名称" min-width="220">
          <template #default="{ row }">
            <span class="event-name-link" @click="$router.push(`/event/${row.eventId}`)">{{ row.eventName || `赛事 #${row.eventId}` }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="eventLocation" label="比赛地点" min-width="160">
          <template #default="{ row }"><span>{{ row.eventLocation || '-' }}</span></template>
        </el-table-column>
        <el-table-column label="比赛日期" width="140">
          <template #default="{ row }"><span>{{ fmtDate(row.eventDate) }}</span></template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" plain @click="$router.push(`/event/${row.eventId}`)">查看</el-button>
            <el-button type="danger" size="small" plain @click="remove(row.eventId)">取消收藏</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && favorites.length === 0" description="暂无收藏赛事">
        <el-button type="primary" @click="$router.push('/events')">浏览赛事</el-button>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../../api'

const favorites = ref([])
const loading = ref(false)
const fmtDate = d => d ? String(d).split('T')[0] : '-'

const upcomingCount = computed(() => {
  const now = new Date()
  return favorites.value.filter(f => {
    if (!f.eventDate) return false
    return new Date(f.eventDate) >= now
  }).length
})

onMounted(fetchData)

async function fetchData() {
  loading.value = true
  try {
    const r = await api.getMyFavorites()
    if (r.code === 200) favorites.value = Array.isArray(r.data) ? r.data : []
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

function remove(eventId) {
  ElMessageBox.confirm('确认取消收藏该赛事吗？', '提示', { type: 'warning' }).then(async () => {
    const r = await api.removeFavorite(eventId)
    if (r.code === 200) {
      ElMessage.success('已取消收藏')
      favorites.value = favorites.value.filter(i => i.eventId !== eventId)
    } else {
      ElMessage.error(r.msg || '操作失败')
    }
  }).catch(() => {})
}
</script>

<style scoped>
.my-favs { display: flex; flex-direction: column; gap: 24px; }
.page-header { padding: 28px 30px; background: linear-gradient(135deg, #B45309, #F59E0B); border-radius: var(--radius-xl); color: #fff; }
.page-kicker { font-size: 11px; font-weight: 700; letter-spacing: 0.12em; color: #FEF3C7; font-family: var(--font-heading); }
.page-header h2 { font-family: var(--font-heading); font-size: 28px; font-weight: 700; margin: 6px 0 8px; }
.page-header p { color: rgba(255,255,255,0.75); font-size: 14px; }
.header-stats { display: flex; align-items: center; gap: 28px; margin-top: 22px; padding-top: 20px; border-top: 1px solid rgba(255,255,255,0.12); }
.hs-value { display: block; font-family: var(--font-heading); font-size: 28px; font-weight: 700; }
.hs-label { font-size: 12px; color: rgba(255,255,255,0.65); }
.hs-divider { width: 1px; height: 40px; background: rgba(255,255,255,0.15); }
.event-name-link { color: var(--brand); font-weight: 500; cursor: pointer; }
.event-name-link:hover { text-decoration: underline; }
</style>
