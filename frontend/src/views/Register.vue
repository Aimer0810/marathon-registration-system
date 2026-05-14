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
        <h1>创建账号</h1>
        <p>加入马拉松报名系统</p>
      </div>

      <el-card class="auth-card">
        <h2>用户注册</h2>
        <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="form.username" placeholder="3-20 个字符" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="真实姓名" prop="realName">
                <el-input v-model="form.realName" placeholder="真实姓名" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="密码" prop="password">
                <el-input v-model="form.password" type="password" placeholder="至少 6 位字符" show-password />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="form.confirmPassword" type="password" placeholder="再次输入密码" show-password />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="form.phone" placeholder="手机号" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="身份证号" prop="idCard">
                <el-input v-model="form.idCard" placeholder="身份证号" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item>
            <el-button type="primary" size="large" @click="handleRegister" :loading="loading" class="btn-full">注册</el-button>
          </el-form-item>
        </el-form>
        <p class="auth-link">已有账号？<el-link type="primary" @click="$router.push('/login')">立即登录</el-link></p>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../api'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: '',
  idCard: ''
})

const validateConfirm = (_, value, cb) => {
  cb(value !== form.password ? new Error('两次输入的密码不一致') : undefined)
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度必须为 3-20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少需要 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证号格式不正确', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const { confirmPassword, ...data } = form
    const res = await api.register(data)
    if (res.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(res.msg || '注册失败')
    }
  } catch (e) {
    ElMessage.error('注册失败')
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
.auth-bg { position: absolute; inset: 0; overflow: hidden; }
.aurora { position: absolute; border-radius: 50%; filter: blur(120px); opacity: 0.45; animation: float 14s ease-in-out infinite; }
.orb-1 { width: 600px; height: 600px; background: radial-gradient(circle, #3b82f6, #1d4ed8 50%, transparent 70%); top: -15%; left: -10%; }
.orb-2 { width: 500px; height: 500px; background: radial-gradient(circle, #06b6d4, #0891b2 50%, transparent 70%); bottom: -20%; right: -8%; animation-delay: -5s; }
.orb-3 { width: 350px; height: 350px; background: radial-gradient(circle, #f59e0b, #d97706 50%, transparent 70%); top: 50%; left: 50%; transform: translate(-50%, -50%); animation-delay: -9s; }
@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(40px, -30px) scale(1.08); }
  66% { transform: translate(-20px, 20px) scale(0.95); }
}
.auth-container { width: 580px; max-width: 100%; position: relative; z-index: 1; }
.auth-brand { text-align: center; margin-bottom: 28px; }
.auth-logo {
  display: inline-grid; width: 52px; height: 52px; place-items: center;
  color: #fff; font-family: var(--font-heading); font-weight: 700; font-size: 24px;
  background: linear-gradient(135deg, #3b82f6, #06b6d4); border-radius: 14px;
  margin-bottom: 12px; box-shadow: 0 8px 32px rgba(59, 130, 246, 0.35);
}
.auth-brand h1 { font-family: var(--font-heading); font-size: 24px; font-weight: 700; color: #fff; margin-bottom: 4px; }
.auth-brand p { font-size: 13px; color: rgba(255, 255, 255, 0.55); }
.auth-card { border: 1px solid rgba(255, 255, 255, 0.12) !important; background: rgba(255, 255, 255, 0.06) !important; backdrop-filter: blur(24px) !important; }
.auth-card h2 { font-family: var(--font-heading); font-size: 18px; font-weight: 700; color: #fff; margin-bottom: 24px; }
.auth-card :deep(.el-form-item__label) { color: rgba(255, 255, 255, 0.65) !important; font-weight: 500; }
.auth-card :deep(.el-input__wrapper) { background: rgba(255, 255, 255, 0.08) !important; box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.14) inset !important; }
.auth-card :deep(.el-input__inner) { color: #fff; }
.auth-card :deep(.el-input__inner::placeholder) { color: rgba(255, 255, 255, 0.35); }
.btn-full { width: 100%; }
.auth-link { text-align: center; color: rgba(255, 255, 255, 0.5); font-size: 14px; margin-top: 16px; }
</style>
