<template>
  <div class="auth-page">
    <div class="auth-bg">
      <div class="aurora orb-1"></div>
      <div class="aurora orb-2"></div>
      <div class="aurora orb-3"></div>
    </div>

    <div class="auth-container">
      <div class="auth-brand">
        <span class="auth-logo">M</span>
        <h1>马拉松报名系统</h1>
        <p>账号登录</p>
      </div>

      <el-card class="auth-card">
        <h2>登录</h2>
        <el-form :model="form" :rules="rules" ref="formRef" @keyup.enter="handleLogin">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="用户名" size="large" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" @click="handleLogin" :loading="loading" class="btn-full">
              登录
            </el-button>
          </el-form-item>
        </el-form>
        <p class="auth-link">
          还没有账号？
          <el-link type="primary" @click="$router.push('/register')">立即注册</el-link>
        </p>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../api'
import { setAuth } from '../utils/auth'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await api.login(form)
    if (res.code === 200) {
      setAuth(res.data)
      ElMessage.success('登录成功')
      router.push(res.data.user?.role === 1 ? '/admin' : '/home')
    } else {
      ElMessage.error(res.msg || '登录失败')
    }
  } catch (e) {
    ElMessage.error('登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  position: relative;
  overflow: hidden;
  background: #0f172a;
}

.auth-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.aurora {
  position: absolute;
  border-radius: 50%;
  filter: blur(120px);
  opacity: 0.45;
  animation: float 14s ease-in-out infinite;
}

.orb-1 {
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, #3b82f6 0%, #1d4ed8 50%, transparent 70%);
  top: -15%;
  left: -10%;
  animation-delay: 0s;
}

.orb-2 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, #06b6d4 0%, #0891b2 50%, transparent 70%);
  bottom: -20%;
  right: -8%;
  animation-delay: -5s;
}

.orb-3 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, #f59e0b 0%, #d97706 50%, transparent 70%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: -9s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(40px, -30px) scale(1.08); }
  66% { transform: translate(-20px, 20px) scale(0.95); }
}

.auth-container {
  width: 420px;
  max-width: 100%;
  position: relative;
  z-index: 1;
}

.auth-brand {
  text-align: center;
  margin-bottom: 32px;
}

.auth-logo {
  display: inline-grid;
  width: 56px;
  height: 56px;
  place-items: center;
  color: #fff;
  font-family: var(--font-heading);
  font-weight: 700;
  font-size: 26px;
  background: linear-gradient(135deg, #3b82f6, #06b6d4);
  border-radius: 16px;
  margin-bottom: 16px;
  box-shadow: 0 8px 32px rgba(59, 130, 246, 0.35);
}

.auth-brand h1 {
  font-family: var(--font-heading);
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 4px;
}

.auth-brand p {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.55);
  letter-spacing: 0.04em;
}

.auth-card {
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  background: rgba(255, 255, 255, 0.06) !important;
  backdrop-filter: blur(24px) !important;
}

.auth-card h2 {
  font-family: var(--font-heading);
  font-size: 20px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 24px;
}

.auth-card :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.08) !important;
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.14) inset !important;
}

.auth-card :deep(.el-input__inner) { color: #fff; }
.auth-card :deep(.el-input__inner::placeholder) { color: rgba(255, 255, 255, 0.35); }

.btn-full { width: 100%; }

.auth-link {
  text-align: center;
  color: rgba(255, 255, 255, 0.5);
  font-size: 14px;
  margin-top: 16px;
}
</style>
