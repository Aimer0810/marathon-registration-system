<template>
  <div class="admin-msgs">
    <div class="page-header">
      <span class="page-kicker">MESSAGE MANAGEMENT</span>
      <h2>留言管理</h2>
    </div>

    <el-card>
      <el-table :data="messages" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column prop="content" label="留言内容" min-width="220" show-overflow-tooltip />
        <el-table-column label="回复内容" width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <span v-if="row.reply" class="reply-exists">{{ row.reply }}</span>
            <el-tag v-else type="info" size="small" effect="light" round>未回复</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="留言时间" width="170"><template #default="{ row }">{{ fmt(row.createTime) }}</template></el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" plain @click="showReply(row)">{{ row.reply ? '编辑' : '回复' }}</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="replyDialogVisible" title="回复留言" width="520px" top="5vh">
      <el-form :model="replyForm" :rules="replyRules" ref="replyFormRef" label-position="top">
        <el-form-item label="留言内容"><el-input :value="currentMessage?.content" type="textarea" disabled :rows="3" /></el-form-item>
        <el-form-item label="回复内容" prop="reply"><el-input v-model="replyForm.reply" type="textarea" :rows="4" placeholder="请输入回复内容" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply">提交回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../../api'

const messages = ref([])
const loading = ref(false)
const replyDialogVisible = ref(false)
const currentMessage = ref(null)
const replyFormRef = ref()
const replyForm = ref({ reply: '' })
const replyRules = { reply: [{ required: true, message: '请输入回复内容', trigger: 'blur' }] }
const fmt = s => s ? s.replace('T', ' ') : ''

onMounted(fetchData)

async function fetchData() {
  loading.value = true
  try {
    const r = await api.adminGetMessages()
    if (r.code === 200) messages.value = r.data
  } finally {
    loading.value = false
  }
}

const showReply = (row) => {
  currentMessage.value = row
  replyForm.value.reply = row.reply || ''
  replyDialogVisible.value = true
}

const submitReply = async () => {
  const v = await replyFormRef.value.validate().catch(() => false)
  if (!v) return
  const r = await api.adminReplyMessage(currentMessage.value.id, replyForm.value.reply)
  if (r.code === 200) {
    ElMessage.success('回复成功')
    replyDialogVisible.value = false
    fetchData()
  } else {
    ElMessage.error(r.msg)
  }
}
</script>

<style scoped>
.admin-msgs { display: flex; flex-direction: column; gap: 24px; }
.page-header { padding: 24px 30px; background: linear-gradient(135deg, #1E3A8A, #3B82F6); border-radius: var(--radius-xl); }
.page-kicker { font-size: 11px; font-weight: 700; letter-spacing: 0.12em; color: #FBBF24; font-family: var(--font-heading); }
.page-header h2 { font-family: var(--font-heading); font-size: 26px; font-weight: 700; color: #fff; margin-top: 4px; }
.reply-exists { color: var(--brand); font-size: 13px; font-weight: 500; }
</style>
