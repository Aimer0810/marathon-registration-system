<template>
  <div class="profile-page">
    <div class="page-header">
      <span class="page-kicker">PROFILE SETTINGS</span>
      <h2>个人资料</h2>
      <p>完善个人信息与联系方式，确保报名数据准确无误。</p>
      <div class="header-stats">
        <div class="hs-item">
          <span class="hs-value">{{ form.username || 'runner' }}</span>
          <span class="hs-label">用户名</span>
        </div>
        <div class="hs-divider"></div>
        <div class="hs-item">
          <span class="hs-value">{{ roleText }}</span>
          <span class="hs-label">账号类型</span>
        </div>
      </div>
    </div>

    <div class="profile-grid">
      <el-card>
        <template #header>
          <div class="card-head">
            <span class="card-title">基本信息</span>
            <span class="card-badge">必填</span>
          </div>
        </template>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="用户名">
                <el-input v-model="form.username" disabled />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="真实姓名" prop="realName">
                <el-input v-model="form.realName" placeholder="请输入真实姓名" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="form.phone" placeholder="请输入手机号" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="身份证号" prop="idCard">
                <el-input v-model="form.idCard" placeholder="请输入身份证号" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item>
            <el-button type="primary" @click="updateProfile" :loading="saving">保存修改</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card>
        <template #header>
          <div class="card-head">
            <span class="card-title">密码安全</span>
            <span class="card-badge badge-warn">敏感操作</span>
          </div>
        </template>
        <p class="card-desc">修改密码后需要重新登录，请使用更安全的密码组合。</p>
        <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-position="top">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="原密码" prop="oldPassword">
                <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
              </el-form-item>
            </el-col>
            <el-col :span="12"></el-col>
            <el-col :span="12">
              <el-form-item label="新密码" prop="newPassword">
                <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="至少 6 位字符" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="确认新密码" prop="confirmPassword">
                <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item>
            <el-button type="primary" plain @click="changePassword">修改密码</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../../api'
import { clearAuth, getCurrentUser, updateCurrentUser } from '../../utils/auth'

const user = getCurrentUser() || {}
const formRef = ref()
const pwdFormRef = ref()
const saving = ref(false)
const roleText = computed(() => user.role === 1 ? '管理员' : '普通用户')

const form = reactive({
  username: user.username || '',
  realName: user.realName || '',
  phone: user.phone || '',
  idCard: user.idCard || ''
})

const rules = {
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

const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: (_, v, cb) => cb(v !== pwdForm.newPassword ? new Error('两次输入的密码不一致') : undefined), trigger: 'blur' }
  ]
}

const updateProfile = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    const res = await api.updateProfile({ realName: form.realName, phone: form.phone, idCard: form.idCard })
    if (res.code === 200) {
      Object.assign(user, { realName: form.realName, phone: form.phone, idCard: form.idCard })
      updateCurrentUser(user)
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const changePassword = async () => {
  const valid = await pwdFormRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    const res = await api.changePassword(pwdForm.oldPassword, pwdForm.newPassword)
    if (res.code === 200) {
      ElMessage.success('密码修改成功，请重新登录')
      clearAuth()
      setTimeout(() => { window.location.href = '/login' }, 1500)
    } else {
      ElMessage.error(res.msg || '密码修改失败')
    }
  } catch (e) {
    ElMessage.error('密码修改失败')
  }
}
</script>

<style scoped>
.profile-page { display: flex; flex-direction: column; gap: 24px; }
.page-header { padding: 28px 30px; background: linear-gradient(135deg, #1E3A8A, #3B82F6); border-radius: var(--radius-xl); color: #fff; }
.page-kicker { font-size: 11px; font-weight: 700; letter-spacing: 0.12em; color: #FBBF24; font-family: var(--font-heading); }
.page-header h2 { font-family: var(--font-heading); font-size: 28px; font-weight: 700; margin: 6px 0 8px; }
.page-header p { color: rgba(255,255,255,0.7); font-size: 14px; }
.header-stats { display: flex; align-items: center; gap: 28px; margin-top: 22px; padding-top: 20px; border-top: 1px solid rgba(255,255,255,0.12); }
.hs-value { display: block; font-family: var(--font-heading); font-size: 22px; font-weight: 700; }
.hs-label { font-size: 12px; color: rgba(255,255,255,0.65); }
.hs-divider { width: 1px; height: 40px; background: rgba(255,255,255,0.15); }
.profile-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 24px; }
.card-head { display: flex; align-items: center; gap: 10px; }
.card-title { font-family: var(--font-heading); font-weight: 700; }
.card-badge { font-size: 11px; font-weight: 600; padding: 2px 10px; border-radius: 999px; background: rgba(59,130,246,0.1); color: var(--brand); }
.card-badge.badge-warn { background: rgba(245,158,11,0.1); color: #B45309; }
.card-desc { color: var(--muted); font-size: 13px; margin-bottom: 16px; line-height: 1.6; }
@media (max-width: 768px) { .profile-grid { grid-template-columns: 1fr; } }
</style>
