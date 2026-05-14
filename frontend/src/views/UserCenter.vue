<template>
  <div class="uc">
    <header class="topbar">
      <div class="topbar-inner">
        <div class="brand" @click="router.push('/home')">
          <span class="brand-mark">M</span>
          <span class="brand-name">马拉松报名系统</span>
        </div>
        <div class="tb-right">
          <span class="greet">{{ user?.realName || user?.username }}</span>
          <el-button size="small" @click="logout">退出登录</el-button>
        </div>
      </div>
    </header>

    <div class="uc-body">
      <aside class="uc-sidebar">
        <div class="sidebar-user">
          <span class="avatar">{{ initial }}</span>
          <div>
            <strong>{{ user?.realName || user?.username }}</strong>
            <span class="role">{{ user?.role === 1 ? '管理员' : '普通用户' }}</span>
          </div>
        </div>
        <el-menu :default-active="$route.path" router class="uc-menu">
          <el-menu-item index="/user/profile">个人资料</el-menu-item>
          <el-menu-item index="/user/registrations">我的报名</el-menu-item>
          <el-menu-item index="/user/messages">我的留言</el-menu-item>
          <el-menu-item index="/user/favorites">我的收藏</el-menu-item>
        </el-menu>
        <el-button @click="router.push('/home')" class="back-btn">返回首页</el-button>
      </aside>
      <main class="uc-content"><router-view /></main>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'
import { ElMessage } from 'element-plus'
import { clearAuth, getCurrentUser } from '../utils/auth'

const router = useRouter()
const user = ref(getCurrentUser())
const initial = computed(() => String(user.value?.realName || user.value?.username || 'M').slice(0, 1).toUpperCase())

const logout = async () => {
  await api.logout()
  clearAuth()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.uc { min-height: 100vh; background: var(--bg); }
.topbar { position: sticky; top: 0; z-index: 30; background: rgba(255,255,255,0.82); border-bottom: 1px solid var(--border); backdrop-filter: blur(20px); }
.topbar-inner { display: flex; justify-content: space-between; align-items: center; max-width: 1180px; margin: 0 auto; height: 64px; padding: 0 24px; }
.brand { display: flex; align-items: center; gap: 10px; cursor: pointer; }
.brand-mark { display: grid; width: 38px; height: 38px; place-items: center; color: #fff; font-family: var(--font-heading); font-weight: 700; font-size: 22px; background: linear-gradient(135deg, #3B82F6, #06B6D4); border-radius: 12px; }
.brand-name { font-family: var(--font-heading); font-weight: 700; font-size: 17px; color: var(--ink); }
.tb-right { display: flex; align-items: center; gap: 12px; }
.greet { font-weight: 600; font-family: var(--font-heading); }
.uc-body { max-width: 1180px; margin: 0 auto; padding: 28px 24px 80px; display: grid; grid-template-columns: 260px 1fr; gap: 28px; }
.uc-sidebar { display: flex; flex-direction: column; gap: 8px; }
.sidebar-user { display: flex; align-items: center; gap: 14px; padding: 20px; background: var(--surface); border: 1px solid var(--border); border-radius: var(--radius-lg); backdrop-filter: blur(20px); }
.avatar { display: grid; width: 44px; height: 44px; place-items: center; font-family: var(--font-heading); font-weight: 700; font-size: 20px; color: #fff; background: linear-gradient(135deg, #3B82F6, #06B6D4); border-radius: 12px; flex-shrink: 0; }
.sidebar-user strong { display: block; font-size: 15px; }
.role { font-size: 12px; color: var(--muted); }
.uc-menu { border-right: none !important; background: var(--surface); border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 8px 10px; }
.uc-menu :deep(.el-menu-item) { height: 42px; margin-bottom: 4px; border-radius: 10px; font-size: 14px; font-weight: 500; color: var(--muted); }
.uc-menu :deep(.el-menu-item.is-active) { color: var(--brand); background: rgba(59,130,246,0.06); }
.back-btn { width: 100%; margin-top: 8px; }
.uc-content { min-width: 0; }
@media (max-width: 768px) { .uc-body { grid-template-columns: 1fr; } }
</style>
