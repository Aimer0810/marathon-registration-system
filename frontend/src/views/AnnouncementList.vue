<template>
  <div class="al-page">
    <header class="topbar">
      <div class="topbar-inner">
        <div class="brand" @click="router.push('/home')">
          <span class="brand-mark">M</span>
          <span class="brand-name">马拉松报名系统</span>
        </div>
        <div class="tb-actions">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>公告列表</el-breadcrumb-item>
          </el-breadcrumb>
          <el-button @click="router.push('/home')">返回首页</el-button>
        </div>
      </div>
    </header>

    <main class="al-main">
      <div class="page-header">
        <span class="page-kicker">BULLETIN BOARD</span>
        <h2>系统公告</h2>
        <p>查看平台通知与最新赛事动态。</p>
      </div>

      <el-card>
        <el-table :data="announcements" v-loading="loading" stripe>
          <el-table-column label="标题" min-width="320">
            <template #default="{ row }">
              <el-link type="primary" @click="router.push(`/announcement/${row.id}`)">{{ row.title }}</el-link>
            </template>
          </el-table-column>
          <el-table-column prop="publisherName" label="发布人" width="140" />
          <el-table-column label="发布时间" width="180">
            <template #default="{ row }">{{ fmt(row.publishTime) }}</template>
          </el-table-column>
        </el-table>
        <div class="pag-wrap">
          <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[5,10,20,50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="sz" @current-change="pg" background />
        </div>
      </el-card>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'

const router = useRouter()
const announcements = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fmt = s => s ? s.replace('T', ' ') : ''

const fetchData = async () => {
  loading.value = true
  try {
    const r = await api.getAnnouncementsPage(pageNum.value, pageSize.value)
    if (r.code === 200) {
      announcements.value = r.data.list
      total.value = r.data.total
    }
  } finally {
    loading.value = false
  }
}

const pg = v => { pageNum.value = v; fetchData() }
const sz = v => { pageSize.value = v; pageNum.value = 1; fetchData() }

onMounted(fetchData)
</script>

<style scoped>
.al-page { min-height: 100vh; background: var(--bg); }
.topbar { position: sticky; top: 0; z-index: 30; background: rgba(255,255,255,0.82); border-bottom: 1px solid var(--border); backdrop-filter: blur(20px); }
.topbar-inner { display: flex; justify-content: space-between; align-items: center; max-width: 1180px; margin: 0 auto; height: 64px; padding: 0 24px; }
.brand { display: flex; align-items: center; gap: 10px; cursor: pointer; }
.brand-mark { display: grid; width: 38px; height: 38px; place-items: center; color: #fff; font-family: var(--font-heading); font-weight: 700; font-size: 22px; background: linear-gradient(135deg, #3B82F6, #06B6D4); border-radius: 12px; }
.brand-name { font-family: var(--font-heading); font-weight: 700; font-size: 17px; color: var(--ink); }
.tb-actions { display: flex; align-items: center; gap: 14px; }
.al-main { max-width: 1180px; margin: 0 auto; padding: 32px 24px 80px; }
.page-header { padding: 24px 28px; background: linear-gradient(135deg, #1E3A8A, #3B82F6); border-radius: var(--radius-xl); color: #fff; margin-bottom: 24px; }
.page-kicker { font-size: 11px; font-weight: 700; letter-spacing: 0.12em; color: #FBBF24; font-family: var(--font-heading); }
.page-header h2 { font-family: var(--font-heading); font-size: 28px; font-weight: 700; margin: 6px 0 8px; }
.page-header p { color: rgba(255,255,255,0.7); font-size: 14px; }
.pag-wrap { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>
