<template>
  <div class="ev-page">
    <header class="topbar">
      <div class="topbar-inner">
        <div class="brand" @click="router.push('/home')">
          <span class="brand-mark">M</span>
          <span class="brand-name">马拉松报名系统</span>
        </div>
        <div class="tb-actions">
          <el-input v-model="keyword" placeholder="搜索赛事..." clearable @keyup.enter="search" class="search-inp" />
          <el-button @click="router.push('/home')">返回首页</el-button>
        </div>
      </div>
    </header>

    <main class="ev-main">
      <div class="ev-head">
        <div>
          <span class="ev-kicker">EXPLORE EVENTS</span>
          <h1>赛事列表</h1>
        </div>
        <el-radio-group v-model="showAll" @change="handleModeChange" size="small">
          <el-radio-button :label="false">分页</el-radio-button>
          <el-radio-button :label="true">全部</el-radio-button>
        </el-radio-group>
      </div>

      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :lg="8" v-for="event in events" :key="event.id" class="ev-col">
          <div class="ev-card" @click="$router.push(`/event/${event.id}`)">
            <div class="ev-cover" :style="{ background: coverColor(event.id) }">
              <el-tag :type="tagType(event.status)" size="small" effect="light" class="ev-tag">{{ statusText(event.status) }}</el-tag>
              <span class="ev-code">{{ String(event.id).padStart(2, '0') }}</span>
            </div>
            <div class="ev-body">
              <h3>{{ event.name }}</h3>
              <div class="ev-row"><span>{{ event.location }}</span><span>{{ event.eventDate }}</span></div>
              <div class="ev-meta">
                <span>￥{{ fee(event.entryFee) }}</span>
                <span>剩余 {{ remaining(event) }} 个名额</span>
              </div>
              <div class="ev-actions" @click.stop>
                <el-button type="primary" size="small" @click="$router.push(`/event/${event.id}`)">查看详情</el-button>
                <el-button :type="fav(event.id) ? 'warning' : ''" size="small" circle @click="toggleFav(event.id)" :icon="Star" />
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
      <el-empty v-if="events.length === 0" description="暂无赛事" />

      <div class="pag-wrap" v-if="!showAll && total > pageSize">
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[8,12,16,20]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="sz" @current-change="pg" background />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star } from '@element-plus/icons-vue'
import api from '../api'
import { getCurrentUser } from '../utils/auth'

const router = useRouter()
const keyword = ref('')
const events = ref([])
const favs = ref([])
const user = ref(getCurrentUser())
const showAll = ref(false)
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)

const statusText = s => ['未开始', '报名中', '已截止', '已结束'][s] || '未知'
const tagType = s => ['info', '', 'warning', 'danger'][s] || 'info'
const coverColor = id => ['#1E3A8A', '#0F766E', '#9A3412', '#6D28D9', '#0F172A'][id % 5]
const fee = v => Number(v || 0).toFixed(0)
const remaining = e => Math.max((e.quota || 0) - (e.registeredCount || 0), 0)
const fav = id => favs.value.includes(id)

const extract = (d, all) => {
  if (all) return Array.isArray(d) ? d.map(e => ({ ...e, registeredCount: e.registeredCount || 0 })) : []
  return (d?.list || []).map(e => ({ ...e, registeredCount: e.registeredCount || 0 }))
}

const fetchEvents = async () => {
  const res = await api.getEventList({ keyword: keyword.value, all: showAll.value, pageNum: pageNum.value, pageSize: pageSize.value })
  if (res.code === 200) {
    events.value = extract(res.data, showAll.value)
    total.value = showAll.value ? events.value.length : (res.data?.total || 0)
  }
}

const fetchFavs = async () => {
  if (!user.value) return
  const r = await api.getMyFavorites()
  if (r.code === 200 && Array.isArray(r.data)) favs.value = r.data.map(f => f.eventId)
}

const search = () => { pageNum.value = 1; fetchEvents() }
const handleModeChange = () => { pageNum.value = 1; fetchEvents() }
const pg = v => { pageNum.value = v; fetchEvents() }
const sz = v => { pageSize.value = v; pageNum.value = 1; fetchEvents() }

const toggleFav = async (id) => {
  if (!user.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    if (fav(id)) {
      await api.removeFavorite(id)
      favs.value = favs.value.filter(x => x !== id)
      ElMessage.success('已取消收藏')
    } else {
      await api.addFavorite(id)
      favs.value = [...favs.value, id]
      ElMessage.success('收藏成功')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  fetchEvents()
  fetchFavs()
})
</script>

<style scoped>
.ev-page { min-height: 100vh; background: var(--bg); }
.topbar { position: sticky; top: 0; z-index: 30; background: rgba(255,255,255,0.82); border-bottom: 1px solid var(--border); backdrop-filter: blur(20px); }
.topbar-inner { display: flex; justify-content: space-between; align-items: center; max-width: 1180px; margin: 0 auto; height: 64px; padding: 0 24px; gap: 16px; }
.brand { display: flex; align-items: center; gap: 10px; cursor: pointer; }
.brand-mark { display: grid; width: 38px; height: 38px; place-items: center; color: #fff; font-family: var(--font-heading); font-weight: 700; font-size: 22px; background: linear-gradient(135deg, #3B82F6, #06B6D4); border-radius: 12px; }
.brand-name { font-family: var(--font-heading); font-weight: 700; font-size: 17px; color: var(--ink); }
.tb-actions { display: flex; align-items: center; gap: 12px; }
.search-inp { width: 260px; }
.ev-main { max-width: 1180px; margin: 0 auto; padding: 32px 24px 80px; }
.ev-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 28px; }
.ev-kicker { font-size: 12px; font-weight: 700; letter-spacing: 0.1em; color: var(--cta); font-family: var(--font-heading); }
.ev-head h1 { font-family: var(--font-heading); font-size: 34px; font-weight: 700; margin-top: 4px; }
.ev-col { margin-bottom: 20px; }
.ev-card { border-radius: var(--radius-lg); overflow: hidden; background: var(--surface); border: 1px solid var(--border); backdrop-filter: blur(20px); box-shadow: var(--shadow-sm); transition: all 0.25s ease; cursor: pointer; }
.ev-card:hover { transform: translateY(-4px); box-shadow: var(--shadow-lg); }
.ev-cover { height: 110px; display: flex; align-items: flex-start; justify-content: space-between; padding: 14px; position: relative; overflow: hidden; }
.ev-cover::after { content: ''; position: absolute; right: -30px; bottom: -40px; width: 140px; height: 140px; border: 20px solid rgba(255,255,255,0.08); border-radius: 50%; }
.ev-tag { position: relative; z-index: 1; background: rgba(255,255,255,0.9) !important; border: none !important; font-weight: 700 !important; }
.ev-code { position: relative; z-index: 1; color: rgba(255,255,255,0.55); font-family: var(--font-heading); font-size: 28px; font-weight: 700; }
.ev-body { padding: 20px; }
.ev-body h3 { font-family: var(--font-heading); font-size: 17px; font-weight: 700; margin-bottom: 8px; line-height: 1.35; min-height: 46px; }
.ev-row { display: flex; font-size: 13px; color: var(--muted); margin-bottom: 8px; }
.ev-row span + span::before { content: ' / '; }
.ev-meta { display: flex; justify-content: space-between; font-weight: 700; color: var(--brand); font-size: 14px; padding: 10px 0; border-top: 1px solid var(--border); }
.ev-actions { display: flex; justify-content: space-between; align-items: center; margin-top: 14px; }
.pag-wrap { display: flex; justify-content: flex-end; margin-top: 12px; }
@media (max-width: 768px) {
  .ev-head h1 { font-size: 24px; }
  .topbar-inner { flex-wrap: wrap; height: auto; padding: 12px 16px; }
  .search-inp { width: 100%; }
}
</style>
