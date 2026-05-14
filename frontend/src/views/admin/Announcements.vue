<template>
  <div class="admin-anns">
    <div class="page-header">
      <div class="ph-top">
        <div>
          <span class="page-kicker">ANNOUNCEMENT MANAGEMENT</span>
          <h2>公告管理</h2>
        </div>
        <el-button type="primary" size="large" @click="handleAdd" :icon="Plus">发布公告</el-button>
      </div>
    </div>

    <el-card>
      <el-table :data="announcements" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="title" label="标题" min-width="280" />
        <el-table-column prop="publisherName" label="发布人" width="120" />
        <el-table-column label="发布时间" width="180"><template #default="{ row }">{{ fmt(row.publishTime) }}</template></el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" plain @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" plain @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" top="5vh">
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" placeholder="请输入公告标题" /></el-form-item>
        <el-form-item label="内容" prop="content"><el-input v-model="form.content" type="textarea" :rows="8" placeholder="请输入公告内容" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAnnouncement">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../../api'

const announcements = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const form = ref({ title: '', content: '' })
const editId = ref(null)
const fmt = s => s ? s.replace('T', ' ') : ''
const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

onMounted(fetchData)

async function fetchData() {
  loading.value = true
  try {
    const r = await api.adminGetAnnouncements()
    if (r.code === 200) announcements.value = r.data
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '发布公告'
  form.value = { title: '', content: '' }
  editId.value = null
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑公告'
  form.value = { ...row }
  editId.value = row.id
  dialogVisible.value = true
}

const saveAnnouncement = async () => {
  const v = await formRef.value.validate().catch(() => false)
  if (!v) return
  try {
    if (editId.value) await api.adminUpdateAnnouncement(editId.value, form.value)
    else await api.adminAddAnnouncement(form.value)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确认删除这条公告吗？', '提示', { type: 'warning' }).then(async () => {
    await api.adminDeleteAnnouncement(id)
    ElMessage.success('删除成功')
    fetchData()
  })
}
</script>

<style scoped>
.admin-anns { display: flex; flex-direction: column; gap: 24px; }
.page-header { padding: 24px 30px; background: linear-gradient(135deg, #1E3A8A, #3B82F6); border-radius: var(--radius-xl); }
.ph-top { display: flex; justify-content: space-between; align-items: flex-start; }
.page-kicker { font-size: 11px; font-weight: 700; letter-spacing: 0.12em; color: #FBBF24; font-family: var(--font-heading); }
.page-header h2 { font-family: var(--font-heading); font-size: 26px; font-weight: 700; color: #fff; margin-top: 4px; }
</style>
