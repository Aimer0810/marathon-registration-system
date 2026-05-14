<template>
  <div class="admin-users">
    <div class="page-header">
      <div class="ph-top">
        <div>
          <span class="page-kicker">USER MANAGEMENT</span>
          <h2>用户管理</h2>
        </div>
        <div class="search-bar">
          <el-input v-model="keyword" placeholder="搜索用户名或真实姓名" clearable @keyup.enter="search">
            <template #append><el-button @click="search">搜索</el-button></template>
          </el-input>
        </div>
      </div>
    </div>

    <el-card>
      <el-table :data="users" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="username" label="用户名" width="140" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="idCard" label="身份证号" width="190" />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'danger'" effect="light" round>{{ row.status === 1 ? '正常' : '禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="创建时间" width="170"><template #default="{ row }">{{ fmt(row.createTime) }}</template></el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" plain @click="toggleStatus(row)">{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
            <el-button size="small" @click="showResetPwd(row)">重置密码</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="pwdDialogVisible" title="重置密码" width="400px">
      <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-position="top">
        <el-form-item label="新密码" prop="password"><el-input v-model="pwdForm.password" type="password" show-password /></el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword"><el-input v-model="pwdForm.confirmPassword" type="password" show-password /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="resetPassword">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../../api'

const keyword = ref('')
const users = ref([])
const loading = ref(false)
const pwdDialogVisible = ref(false)
const currentUserId = ref(null)
const pwdFormRef = ref()
const pwdForm = ref({ password: '', confirmPassword: '' })
const fmt = s => s ? s.replace('T', ' ') : ''

const pwdRules = {
  password: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '密码至少 6 位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }, { validator: (_, v, cb) => cb(v !== pwdForm.value.password ? new Error('两次输入的密码不一致') : undefined), trigger: 'blur' }]
}

onMounted(fetchData)

async function fetchData() {
  loading.value = true
  try {
    const r = await api.adminGetUsers(keyword.value)
    if (r.code === 200) users.value = r.data
  } finally {
    loading.value = false
  }
}

const search = () => fetchData()

const toggleStatus = (row) => {
  const ns = row.status === 1 ? 0 : 1
  const action = ns === 0 ? '禁用' : '启用'
  ElMessageBox.confirm(`确认${action}用户 ${row.username} 吗？`, '提示', { type: 'warning' }).then(async () => {
    const r = await api.adminUpdateUserStatus(row.id, ns)
    if (r.code === 200) {
      ElMessage.success('操作成功')
      fetchData()
    } else {
      ElMessage.error(r.msg)
    }
  })
}

const showResetPwd = (row) => {
  currentUserId.value = row.id
  pwdForm.value = { password: '', confirmPassword: '' }
  pwdDialogVisible.value = true
}

const resetPassword = async () => {
  const v = await pwdFormRef.value.validate().catch(() => false)
  if (!v) return
  const r = await api.adminResetPassword(currentUserId.value, pwdForm.value.password)
  if (r.code === 200) {
    ElMessage.success('密码重置成功')
    pwdDialogVisible.value = false
  } else {
    ElMessage.error(r.msg)
  }
}
</script>

<style scoped>
.admin-users { display: flex; flex-direction: column; gap: 24px; }
.page-header { padding: 24px 30px; background: linear-gradient(135deg, #1E3A8A, #3B82F6); border-radius: var(--radius-xl); }
.ph-top { display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 16px; }
.page-kicker { font-size: 11px; font-weight: 700; letter-spacing: 0.12em; color: #FBBF24; font-family: var(--font-heading); }
.page-header h2 { font-family: var(--font-heading); font-size: 26px; font-weight: 700; color: #fff; margin-top: 4px; }
.search-bar { width: 280px; }
</style>
