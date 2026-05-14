<template>
  <div class="ad-page">
    <header class="topbar">
      <div class="topbar-inner">
        <div class="brand" @click="$router.push('/home')">
          <span class="brand-mark">M</span>
          <span class="brand-name">马拉松报名系统</span>
        </div>
        <el-button @click="$router.back()" :icon="ArrowLeft">返回</el-button>
      </div>
    </header>

    <main class="ad-main">
      <div class="page-header">
        <span class="page-kicker">ANNOUNCEMENT</span>
        <h1>{{ announcement.title || '公告详情' }}</h1>
        <div class="meta-row">
          <span>{{ announcement.publisherName || '系统管理员' }}</span>
          <span class="meta-divider">/</span>
          <span>{{ fmt(announcement.publishTime) }}</span>
        </div>
      </div>

      <el-card>
        <article class="article" v-html="announcement.content || '公告内容正在更新中。'"></article>
      </el-card>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import api from '../api'

const route = useRoute()
const router = useRouter()
const announcement = ref({})
const fmt = s => s ? s.replace('T', ' ') : ''

onMounted(async () => {
  const r = await api.getAnnouncementDetail(route.params.id)
  if (r.code === 200) announcement.value = r.data
  else router.back()
})
</script>

<style scoped>
.ad-page { min-height: 100vh; background: var(--bg); }
.topbar { position: sticky; top: 0; z-index: 30; background: rgba(255,255,255,0.82); border-bottom: 1px solid var(--border); backdrop-filter: blur(20px); }
.topbar-inner { display: flex; justify-content: space-between; align-items: center; max-width: 1080px; margin: 0 auto; height: 64px; padding: 0 24px; }
.brand { display: flex; align-items: center; gap: 10px; cursor: pointer; }
.brand-mark { display: grid; width: 38px; height: 38px; place-items: center; color: #fff; font-family: var(--font-heading); font-weight: 700; font-size: 22px; background: linear-gradient(135deg, #3B82F6, #06B6D4); border-radius: 12px; }
.brand-name { font-family: var(--font-heading); font-weight: 700; font-size: 17px; color: var(--ink); }
.ad-main { max-width: 1080px; margin: 0 auto; padding: 32px 24px 80px; }
.page-header { padding: 28px 30px; background: linear-gradient(135deg, #0F172A, #1E293B); border-radius: var(--radius-xl); color: #fff; margin-bottom: 24px; }
.page-kicker { font-size: 11px; font-weight: 700; letter-spacing: 0.12em; color: #FBBF24; font-family: var(--font-heading); }
.page-header h1 { font-family: var(--font-heading); font-size: 32px; font-weight: 700; margin: 10px 0 16px; line-height: 1.2; }
.meta-row { display: flex; align-items: center; gap: 8px; font-size: 14px; color: rgba(255,255,255,0.6); }
.meta-divider { opacity: 0.4; }
.article { color: var(--muted); font-size: 16px; line-height: 1.9; }
.article :deep(h1), .article :deep(h2), .article :deep(h3) { margin: 1.2em 0 0.7em; color: var(--ink); }
.article :deep(p) { margin-bottom: 1em; }
.article :deep(img) { max-width: 100%; border-radius: var(--radius-md); }
.article :deep(blockquote) { margin: 1.2em 0; padding: 16px 20px; background: var(--bg); border-left: 4px solid var(--brand); border-radius: 0 14px 14px 0; }
</style>
