<template>
  <div class="my-msgs">
    <section class="page-hero">
      <div class="hero-copy">
        <span class="page-kicker">MESSAGE HUB</span>
        <h2>我的消息中心</h2>
        <p>统一查看普通留言与 AI 历史会话，让反馈、咨询和答复都留在一个更清晰的地方。</p>
      </div>
      <div class="hero-metrics">
        <div class="metric-card">
          <span class="metric-label">普通留言</span>
          <strong>{{ messages.length }}</strong>
        </div>
        <div class="metric-card">
          <span class="metric-label">AI 会话</span>
          <strong>{{ aiMessages.length }}</strong>
        </div>
        <div class="metric-card accent">
          <span class="metric-label">已回复留言</span>
          <strong>{{ repliedCount }}</strong>
        </div>
      </div>
    </section>

    <section class="content-grid">
      <el-card class="board-card board-primary">
        <template #header>
          <div class="card-head">
            <div>
              <span class="card-kicker">FEEDBACK</span>
              <h3>普通留言记录</h3>
            </div>
            <el-button type="primary" size="small" @click="showDialog = true">发表留言</el-button>
          </div>
        </template>

        <div v-if="messages.length" class="timeline-list">
          <article v-for="msg in messages" :key="`msg-${msg.id}`" class="timeline-item">
            <div class="timeline-track"></div>
            <div class="timeline-node user"></div>
            <div class="msg-bubble mine">
              <p class="msg-content">{{ msg.content }}</p>
              <span class="msg-time">{{ fmt(msg.createTime) }}</span>
            </div>

            <template v-if="msg.reply">
              <div class="timeline-node admin"></div>
              <div class="msg-bubble admin">
                <div class="admin-head">
                  <span class="admin-avatar">管</span>
                  <span class="admin-label">管理员回复</span>
                </div>
                <p class="msg-content">{{ msg.reply }}</p>
              </div>
            </template>

            <div v-else class="msg-status pending">
              <span class="waiting-dot"></span>
              等待回复中...
            </div>
          </article>
        </div>

        <div v-else class="empty-shell">
          <el-empty description="暂无普通留言">
            <el-button type="primary" @click="showDialog = true">发表第一条留言</el-button>
          </el-empty>
        </div>

        <el-dialog v-model="showDialog" title="发表留言" width="520px">
          <el-form :model="form" :rules="rules" ref="formRef">
            <el-form-item prop="content">
              <el-input v-model="form.content" type="textarea" :rows="6" placeholder="输入你想反馈的问题、建议或想咨询的内容..." />
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="showDialog = false">取消</el-button>
            <el-button type="primary" @click="submit">提交</el-button>
          </template>
        </el-dialog>
      </el-card>

      <el-card class="board-card board-secondary">
        <template #header>
          <div class="card-head">
            <div>
              <span class="card-kicker">AI HISTORY</span>
              <h3>AI 会话专区</h3>
            </div>
            <span class="card-tip">自动保存最近的 AI 问答记录</span>
          </div>
        </template>

        <div v-if="aiMessages.length" class="ai-stack">
          <article v-for="msg in aiMessages" :key="`ai-${msg.id}`" class="ai-session">
            <div class="session-mark"></div>
            <div class="msg-bubble mine ai-user">
              <p class="msg-tag">我的提问</p>
              <p class="msg-content">{{ formatAiQuestion(msg.question) }}</p>
              <span class="msg-time">{{ fmt(msg.createTime) }}</span>
            </div>
            <div class="msg-bubble ai-reply">
              <div class="admin-head">
                <span class="admin-avatar ai-avatar">AI</span>
                <span class="admin-label ai-label">AI 助手回复</span>
              </div>
              <p class="msg-content">{{ msg.answer || '暂无回复' }}</p>
            </div>
          </article>
        </div>

        <div v-else class="empty-shell">
          <el-empty description="暂无 AI 会话记录" />
        </div>
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../../api'

const messages = ref([])
const aiMessages = ref([])
const showDialog = ref(false)
const formRef = ref()
const form = ref({ content: '' })
const rules = { content: [{ required: true, message: '请输入留言内容', trigger: 'blur' }] }
const repliedCount = computed(() => messages.value.filter(m => m.reply).length)
const fmt = s => s ? s.replace('T', ' ') : ''

onMounted(fetchData)

async function fetchData() {
  messages.value = []
  aiMessages.value = []

  try {
    const messageRes = await api.getMyMessages()
    if (messageRes.code === 200) {
      messages.value = Array.isArray(messageRes.data) ? messageRes.data : []
    }
  } catch (error) {
    ElMessage.warning(error?.response?.data?.msg || error?.message || '普通留言加载失败')
  }

  try {
    const aiRes = await api.getMyAiMessages()
    if (aiRes.code === 200) {
      aiMessages.value = Array.isArray(aiRes.data) ? aiRes.data : []
    }
  } catch (error) {
    console.warn('AI messages load failed:', error)
  }
}

function formatAiQuestion(content) {
  return String(content || '')
}

const submit = async () => {
  const v = await formRef.value.validate().catch(() => false)
  if (!v) return
  const r = await api.submitMessage(form.value)
  if (r.code === 200) {
    ElMessage.success('留言提交成功')
    showDialog.value = false
    form.value.content = ''
    fetchData()
  } else {
    ElMessage.error(r.msg)
  }
}
</script>

<style scoped>
.my-msgs {
  display: flex;
  flex-direction: column;
  gap: 26px;
}

.page-hero {
  position: relative;
  overflow: hidden;
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) minmax(320px, 0.9fr);
  gap: 24px;
  padding: 34px 34px 30px;
  border-radius: var(--radius-xl);
  background:
    radial-gradient(circle at 12% 20%, rgba(255, 255, 255, 0.18), transparent 28%),
    radial-gradient(circle at 85% 18%, rgba(45, 212, 191, 0.18), transparent 24%),
    linear-gradient(135deg, #111827 0%, #4c1d95 52%, #7c3aed 100%);
  color: #fff;
  box-shadow: 0 20px 50px rgba(76, 29, 149, 0.22);
}

.page-hero::after {
  content: '';
  position: absolute;
  inset: auto -80px -120px auto;
  width: 260px;
  height: 260px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  filter: blur(2px);
}

.hero-copy {
  position: relative;
  z-index: 1;
}

.page-kicker {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.16em;
  color: rgba(221, 214, 254, 0.9);
  font-family: var(--font-heading);
}

.page-hero h2 {
  margin: 10px 0 10px;
  font-family: var(--font-heading);
  font-size: clamp(28px, 4vw, 38px);
  line-height: 1.08;
}

.page-hero p {
  max-width: 560px;
  font-size: 15px;
  line-height: 1.75;
  color: rgba(255, 255, 255, 0.74);
}

.hero-metrics {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 1fr;
  gap: 14px;
}

.metric-card {
  padding: 18px 18px 16px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.14);
  backdrop-filter: blur(16px);
}

.metric-card.accent {
  background: rgba(251, 191, 36, 0.14);
  border-color: rgba(251, 191, 36, 0.24);
}

.metric-label {
  display: block;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.68);
  margin-bottom: 6px;
  letter-spacing: 0.04em;
}

.metric-card strong {
  font-family: var(--font-heading);
  font-size: 34px;
  line-height: 1;
}

.content-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(320px, 0.95fr);
  gap: 24px;
  align-items: start;
}

.board-card {
  min-height: 100%;
}

.board-primary :deep(.el-card__body),
.board-secondary :deep(.el-card__body) {
  padding-top: 22px !important;
}

.card-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 14px;
}

.card-kicker {
  display: block;
  margin-bottom: 4px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.12em;
  color: var(--brand);
  font-family: var(--font-heading);
}

.card-head h3 {
  font-size: 20px;
  font-family: var(--font-heading);
}

.card-tip {
  padding-top: 6px;
  font-size: 12px;
  color: var(--muted);
  text-align: right;
}

.timeline-list,
.ai-stack {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.timeline-item,
.ai-session {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 2px 0 0 18px;
}

.timeline-track,
.session-mark {
  position: absolute;
  left: 3px;
  top: 0;
  bottom: -10px;
  width: 2px;
  background: linear-gradient(180deg, rgba(59, 130, 246, 0.36), rgba(148, 163, 184, 0.08));
}

.timeline-node {
  position: absolute;
  left: -2px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid #fff;
  box-shadow: 0 4px 14px rgba(15, 23, 42, 0.12);
}

.timeline-node.user {
  top: 18px;
  background: #2563eb;
}

.timeline-node.admin {
  top: auto;
  bottom: 22px;
  background: #7c3aed;
}

.msg-bubble {
  max-width: 86%;
  padding: 16px 18px;
  border-radius: 18px;
  line-height: 1.7;
}

.msg-bubble.mine {
  align-self: flex-end;
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  color: #fff;
  border-bottom-right-radius: 6px;
  box-shadow: 0 14px 30px rgba(37, 99, 235, 0.18);
}

.msg-bubble.admin {
  align-self: flex-start;
  background: linear-gradient(180deg, #ffffff, #f8fafc);
  border: 1px solid #e2e8f0;
  border-bottom-left-radius: 6px;
}

.msg-bubble.ai-reply {
  align-self: flex-start;
  background: linear-gradient(180deg, #eff6ff, #f8fbff);
  border: 1px solid rgba(96, 165, 250, 0.26);
  border-bottom-left-radius: 6px;
}

.msg-bubble.ai-user {
  background: linear-gradient(135deg, #0f766e, #0d9488);
  box-shadow: 0 14px 30px rgba(13, 148, 136, 0.18);
}

.msg-content {
  font-size: 14px;
  white-space: pre-wrap;
  word-break: break-word;
}

.msg-time {
  display: block;
  margin-top: 7px;
  font-size: 11px;
  opacity: 0.68;
}

.msg-tag {
  margin-bottom: 6px;
  font-size: 11px;
  font-weight: 700;
  opacity: 0.76;
  letter-spacing: 0.08em;
}

.admin-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.admin-avatar {
  display: grid;
  width: 24px;
  height: 24px;
  place-items: center;
  background: #7c3aed;
  color: #fff;
  border-radius: 7px;
  font-size: 12px;
  font-weight: 700;
}

.admin-label {
  font-size: 12px;
  font-weight: 600;
  color: #7c3aed;
}

.ai-avatar {
  background: #2563eb;
}

.ai-label {
  color: #2563eb;
}

.msg-status.pending {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin-left: 6px;
  font-size: 13px;
  color: var(--muted);
}

.waiting-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #94a3b8;
  animation: pulse 1.5s ease-in-out infinite;
}

.empty-shell {
  padding: 10px 0 2px;
}

@keyframes pulse {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 1; }
}

@media (max-width: 1100px) {
  .page-hero,
  .content-grid {
    grid-template-columns: 1fr;
  }

  .hero-metrics {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .page-hero {
    padding: 24px 20px 22px;
  }

  .hero-metrics {
    grid-template-columns: 1fr;
  }

  .card-head {
    flex-direction: column;
    align-items: stretch;
  }

  .card-tip {
    text-align: left;
    padding-top: 0;
  }

  .msg-bubble {
    max-width: 100%;
  }
}
</style>
