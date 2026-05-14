<template>
  <el-container class="admin-shell">
    <el-aside width="240px" class="admin-sidebar">
      <div class="sb-brand" @click="$router.push('/admin/dashboard')">
        <span class="sb-logo">M</span>
        <span class="sb-title">马拉松后台</span>
      </div>
      <el-menu :default-active="$route.path" router class="sb-menu" background-color="transparent" text-color="#94A3B8" active-text-color="#FFFFFF">
        <el-menu-item index="/admin/dashboard"><el-icon><DataLine /></el-icon><span>工作台</span></el-menu-item>
        <el-menu-item index="/admin/events"><el-icon><Trophy /></el-icon><span>赛事管理</span></el-menu-item>
        <el-menu-item index="/admin/users"><el-icon><User /></el-icon><span>用户管理</span></el-menu-item>
        <el-menu-item index="/admin/registrations"><el-icon><Document /></el-icon><span>报名审核</span></el-menu-item>
        <el-menu-item index="/admin/announcements"><el-icon><Bell /></el-icon><span>公告管理</span></el-menu-item>
        <el-menu-item index="/admin/messages"><el-icon><ChatDotSquare /></el-icon><span>留言管理</span></el-menu-item>
      </el-menu>
      <div class="sb-footer">
        <span>{{ user?.realName || user?.username }}</span>
        <el-button text @click="logout" class="sb-logout">退出登录</el-button>
      </div>
    </el-aside>

    <el-container>
      <el-header class="admin-header">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">管理后台</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $route.meta.title }}</el-breadcrumb-item>
        </el-breadcrumb>
      </el-header>
      <el-main class="admin-main"><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { DataLine, Trophy, User, Document, Bell, ChatDotSquare } from '@element-plus/icons-vue'
import api from '../../api'
import { clearAuth, getCurrentUser } from '../../utils/auth'

const router = useRouter()
const user = ref(getCurrentUser())

const logout = async () => {
  await api.logout()
  clearAuth()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.admin-shell { min-height: 100vh; background: var(--bg); }
.admin-sidebar { display: flex; flex-direction: column; background: #0B1120; border-right: 1px solid rgba(255,255,255,0.05); }
.sb-brand { display: flex; align-items: center; gap: 10px; height: 64px; padding: 0 22px; cursor: pointer; border-bottom: 1px solid rgba(255,255,255,0.05); }
.sb-logo { display: grid; width: 36px; height: 36px; place-items: center; color: #0B1120; font-family: var(--font-heading); font-weight: 700; font-size: 19px; background: linear-gradient(135deg, #F59E0B, #FBBF24); border-radius: 10px; }
.sb-title { font-family: var(--font-heading); font-weight: 700; font-size: 16px; color: #fff; }
.sb-menu { flex: 1; border-right: none !important; padding: 14px 12px; }
.sb-menu :deep(.el-menu-item) { height: 44px; margin-bottom: 4px; border-radius: 10px; font-size: 14px; font-weight: 500; transition: all 0.15s ease; }
.sb-menu :deep(.el-menu-item:hover) { background: rgba(255,255,255,0.04); }
.sb-menu :deep(.el-menu-item.is-active) { background: linear-gradient(135deg, #3B82F6, #2563EB); color: #fff; box-shadow: 0 4px 16px rgba(59,130,246,0.25); }
.sb-footer { display: flex; align-items: center; justify-content: space-between; padding: 16px 22px; border-top: 1px solid rgba(255,255,255,0.05); color: #94A3B8; font-size: 13px; font-weight: 500; }
.sb-logout { color: #94A3B8 !important; font-size: 13px; }
.sb-logout:hover { color: #fff !important; }
.admin-header { display: flex; align-items: center; height: 64px; padding: 0 30px; background: var(--surface); border-bottom: 1px solid var(--border); backdrop-filter: blur(20px); }
.admin-header h2 { font-family: var(--font-heading); font-size: 20px; font-weight: 700; }
.admin-main { padding: 30px; }
@media (max-width: 768px) {
  .admin-sidebar { width: 200px !important; }
  .admin-main { padding: 16px; }
}
</style>
