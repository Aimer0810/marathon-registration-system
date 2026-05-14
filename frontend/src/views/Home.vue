<template>
  <div class="home">
    <header class="topbar">
      <div class="topbar-inner">
        <div class="brand" @click="router.push('/home')">
          <span class="brand-mark">M</span>
          <span class="brand-name">马拉松报名系统</span>
        </div>
        <nav class="nav-links">
          <el-button link @click="router.push('/home')">首页</el-button>
          <el-button link @click="router.push('/events')">赛事列表</el-button>
          <template v-if="user">
            <el-dropdown @command="handleCommand">
              <span class="user-name">{{ user.realName || user.username }}</span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="user">个人中心</el-dropdown-item>
                  <el-dropdown-item v-if="user.role === 1" command="admin">管理后台</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" size="small" @click="router.push('/login')">登录</el-button>
            <el-button size="small" @click="router.push('/register')">注册</el-button>
          </template>
        </nav>
      </div>
    </header>

    <main class="home-main">
      <section class="hero">
        <div class="hero-bg">
          <div class="aurora a1"></div>
          <div class="aurora a2"></div>
          <div class="aurora a3"></div>
          <div class="grid-pattern"></div>
        </div>
        <div class="hero-content">
          <span class="hero-badge">2026 RUNNING SEASON</span>
          <h1>发现城市赛道<br>开启你的下一场挑战</h1>
          <p>浏览赛事、在线报名、跟踪报名状态，一站式完成你的参赛流程。</p>
          <div class="hero-actions">
            <el-button type="primary" size="large" @click="router.push('/events')" class="hero-btn">浏览赛事</el-button>
            <el-button size="large" @click="router.push('/announcements')" class="hero-btn-outline">查看公告</el-button>
          </div>
          <div class="hero-stats">
            <div class="stat" v-for="s in statList" :key="s.label">
              <strong>{{ s.value }}</strong>
              <span>{{ s.label }}</span>
            </div>
          </div>
        </div>
      </section>

      <section class="content-section">
        <div class="section-header">
          <h2>最新赛事</h2>
          <el-link type="primary" @click="router.push('/events')">查看全部</el-link>
        </div>
        <el-table :data="recentEvents" v-loading="loadingEvents" stripe>
          <el-table-column prop="name" label="赛事名称" min-width="200" />
          <el-table-column prop="location" label="地点" width="160" />
          <el-table-column prop="eventDate" label="比赛日期" width="140" />
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="tagType(row.status)" size="small" effect="light">{{ statusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="128" align="center">
            <template #default="{ row }">
              <button class="event-action-btn" type="button" @click="router.push(`/event/${row.id}`)">
                <span>查看详情</span>
                <span class="event-action-arrow">›</span>
              </button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!loadingEvents && recentEvents.length === 0" description="暂无赛事" />
      </section>

      <section class="content-section">
        <div class="section-header">
          <h2>最新公告</h2>
          <el-link type="primary" @click="router.push('/announcements')">查看全部</el-link>
        </div>
        <div v-if="announcements.length" class="announce-list">
          <div v-for="item in announcements" :key="item.id" class="announce-item" @click="router.push(`/announcement/${item.id}`)">
            <span class="announce-dot"></span>
            <span class="announce-title">{{ item.title }}</span>
            <span class="announce-date">{{ formatDate(item.publishTime) }}</span>
          </div>
        </div>
        <el-empty v-else-if="!loadingAnnouncements" description="暂无公告" />
      </section>
    </main>

    <footer class="home-footer">
      <div class="footer-inner">
        <div class="footer-brand">
          <span class="footer-logo">M</span>
          <span>马拉松报名系统</span>
        </div>
        <span class="footer-copy">&copy; {{ year }} 马拉松报名系统</span>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../api'
import { clearAuth, getCurrentUser } from '../utils/auth'

const router = useRouter()
const year = new Date().getFullYear()
const user = ref(getCurrentUser())
const recentEvents = ref([])
const announcements = ref([])
const loadingEvents = ref(false)
const loadingAnnouncements = ref(false)

const statusText = (s) => ['未开始', '报名中', '已截止', '已结束'][s] || '未知'
const tagType = (s) => ['info', '', 'warning', 'danger'][s] || 'info'
const extractList = (d) => Array.isArray(d) ? d : (Array.isArray(d?.list) ? d.list : [])

const statList = computed(() => [
  { label: '近期赛事', value: recentEvents.value.length },
  { label: '查询方式', value: '一站式' },
  { label: '报名流程', value: '在线化' }
])

const loadData = async () => {
  loadingEvents.value = true
  loadingAnnouncements.value = true
  try {
    const [eRes, aRes] = await Promise.all([api.getEventList({ all: true }), api.getAnnouncements()])
    recentEvents.value = eRes.code === 200 ? extractList(eRes.data).slice(0, 5) : []
    announcements.value = aRes.code === 200 ? extractList(aRes.data).slice(0, 5) : []
  } catch (e) {
    ElMessage.error('数据加载失败')
  } finally {
    loadingEvents.value = false
    loadingAnnouncements.value = false
  }
}

const handleCommand = async (cmd) => {
  if (cmd === 'user') return router.push('/user')
  if (cmd === 'admin') return router.push('/admin')
  if (cmd === 'logout') {
    await api.logout()
    clearAuth()
    user.value = null
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}

const formatDate = (s) => s ? String(s).split('T')[0] : ''

onMounted(loadData)
</script>

<style scoped>
.home { min-height: 100vh; }
.topbar { position: sticky; top: 0; z-index: 30; background: rgba(255,255,255,0.82); border-bottom: 1px solid var(--border); backdrop-filter: blur(20px); }
.topbar-inner { display: flex; justify-content: space-between; align-items: center; max-width: 1180px; margin: 0 auto; height: 64px; padding: 0 24px; }
.brand { display: flex; align-items: center; gap: 10px; cursor: pointer; }
.brand-mark { display: grid; width: 38px; height: 38px; place-items: center; color: #fff; font-family: var(--font-heading); font-weight: 700; font-size: 22px; background: linear-gradient(135deg, #3B82F6, #06B6D4); border-radius: 12px; }
.brand-name { font-family: var(--font-heading); font-weight: 700; font-size: 17px; color: var(--ink); }
.nav-links { display: flex; align-items: center; gap: 10px; }
.user-name { cursor: pointer; font-weight: 600; color: var(--brand); font-family: var(--font-heading); }
.home-main { max-width: 1180px; margin: 0 auto; padding: 0 24px 0; }
.hero { position: relative; overflow: hidden; margin: 24px 0 48px; border-radius: var(--radius-xl); min-height: 460px; display: flex; align-items: center; background: #0F172A; }
.hero-bg { position: absolute; inset: 0; }
.aurora { position: absolute; border-radius: 50%; filter: blur(100px); opacity: 0.5; animation: drift 12s ease-in-out infinite; }
.a1 { width: 500px; height: 500px; background: radial-gradient(circle, #3B82F6, #1D4ED8 50%, transparent 70%); top: -20%; left: -10%; }
.a2 { width: 400px; height: 400px; background: radial-gradient(circle, #06B6D4, #0891B2 50%, transparent 70%); bottom: -15%; right: -5%; animation-delay: -4s; }
.a3 { width: 300px; height: 300px; background: radial-gradient(circle, #F59E0B, #D97706 50%, transparent 70%); top: 40%; right: 25%; animation-delay: -8s; }
.grid-pattern { position: absolute; inset: 0; background-image: linear-gradient(rgba(255,255,255,0.03) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,0.03) 1px, transparent 1px); background-size: 60px 60px; }
@keyframes drift {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -20px) scale(1.06); }
  66% { transform: translate(-15px, 15px) scale(0.96); }
}
.hero-content { position: relative; z-index: 1; padding: 56px; max-width: 700px; }
.hero-badge { display: inline-block; padding: 6px 14px; background: rgba(245,158,11,0.15); border: 1px solid rgba(245,158,11,0.3); color: #FBBF24; font-size: 12px; font-weight: 700; letter-spacing: 0.14em; border-radius: 999px; margin-bottom: 20px; font-family: var(--font-heading); }
.hero-content h1 { color: #fff; font-size: clamp(40px, 6vw, 62px); font-weight: 700; line-height: 1.08; margin-bottom: 16px; }
.hero-content p { color: rgba(255,255,255,0.65); font-size: 18px; max-width: 500px; line-height: 1.6; margin-bottom: 32px; }
.hero-actions { display: flex; gap: 14px; margin-bottom: 48px; }
.hero-btn-outline { background: rgba(255,255,255,0.08) !important; border: 1px solid rgba(255,255,255,0.18) !important; color: #fff !important; }
.hero-btn-outline:hover { background: rgba(255,255,255,0.14) !important; }
.hero-stats { display: flex; gap: 48px; flex-wrap: wrap; }
.hero-stats .stat strong { display: block; color: #fff; font-family: var(--font-heading); font-size: 32px; font-weight: 700; }
.hero-stats .stat span { color: rgba(255,255,255,0.5); font-size: 13px; }
.content-section { margin-top: 48px; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
.section-header h2 { font-family: var(--font-heading); font-size: 22px; font-weight: 700; }
.event-action-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 7px 12px;
  border: 1px solid rgba(59, 130, 246, 0.18);
  border-radius: 999px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1), rgba(6, 182, 212, 0.08));
  color: var(--brand-deep);
  font-size: 12px;
  font-weight: 700;
  font-family: var(--font-heading);
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease, background 0.18s ease;
}
.event-action-btn:hover {
  transform: translateY(-1px);
  border-color: rgba(59, 130, 246, 0.3);
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.18), rgba(6, 182, 212, 0.14));
  box-shadow: 0 10px 20px rgba(59, 130, 246, 0.12);
}
.event-action-arrow {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.72);
  font-size: 14px;
  line-height: 1;
}
.announce-list { background: var(--surface); border: 1px solid var(--border); border-radius: var(--radius-lg); overflow: hidden; backdrop-filter: blur(20px); }
.announce-item { display: flex; align-items: center; gap: 14px; padding: 16px 24px; border-bottom: 1px solid var(--border); cursor: pointer; transition: background 0.15s; }
.announce-item:last-child { border-bottom: none; }
.announce-item:hover { background: rgba(59,130,246,0.04); }
.announce-dot { width: 8px; height: 8px; border-radius: 50%; background: var(--cta); flex-shrink: 0; }
.announce-title { flex: 1; font-weight: 500; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.announce-date { font-size: 13px; color: var(--muted); flex-shrink: 0; }
.home-footer { margin-top: 80px; padding: 28px 24px; border-top: 1px solid var(--border); background: var(--surface); backdrop-filter: blur(20px); }
.footer-inner { max-width: 1180px; margin: 0 auto; display: flex; justify-content: space-between; align-items: center; gap: 16px; flex-wrap: wrap; }
.footer-brand { display: flex; align-items: center; gap: 10px; font-weight: 600; }
.footer-logo { display: grid; width: 30px; height: 30px; place-items: center; color: #fff; font-family: var(--font-heading); font-weight: 700; background: linear-gradient(135deg, #3B82F6, #06B6D4); border-radius: 8px; }
.footer-copy { font-size: 13px; color: var(--muted); }
@media (max-width: 768px) {
  .hero-content { padding: 36px 24px; }
  .hero-stats { gap: 24px; }
  .event-action-btn { padding: 6px 10px; font-size: 11px; }
}
</style>
