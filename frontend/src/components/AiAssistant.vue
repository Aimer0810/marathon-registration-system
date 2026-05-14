<template>
  <div class="ai-assistant">
    <transition name="assistant-panel">
      <section v-if="visible" class="assistant-panel">
        <header class="assistant-header">
          <div class="assistant-brand">
            <div class="assistant-icon">AI</div>
            <div>
              <h3>智能助手</h3>
              <p>赛事咨询与报名问答</p>
            </div>
          </div>
          <button class="assistant-close" @click="visible = false">关闭</button>
        </header>

        <div class="assistant-body">
          <div class="assistant-messages">
            <div v-for="(item, index) in messages" :key="index" :class="['msg', item.role]">
              <div class="bubble">{{ item.content }}</div>
            </div>
            <div v-if="loading" class="msg assistant">
              <div class="bubble">正在思考中...</div>
            </div>
          </div>

          <div v-if="quickPrompts.length" class="assistant-prompts">
            <button
              v-for="prompt in quickPrompts"
              :key="prompt"
              class="prompt-chip"
              @click="fillPrompt(prompt)"
            >
              {{ prompt }}
            </button>
          </div>

          <div class="assistant-input">
            <textarea
              v-model="input"
              class="input-box"
              rows="3"
              maxlength="1000"
              placeholder="输入问题..."
              @keydown.enter="handleEnter"
            />
            <div class="input-footer">
              <span class="input-tip">Enter 发送</span>
              <span class="input-count">{{ input.length }}/1000</span>
            </div>
            <div class="actions">
              <button class="ghost-btn" @click="clearChat">清空</button>
              <button class="primary-btn" :disabled="loading" @click="sendMessage">发送</button>
            </div>
          </div>
        </div>
      </section>
    </transition>

    <button v-if="!visible" class="assistant-trigger" @click="openAssistant">
      <span class="assistant-trigger-icon">AI</span>
      <span>助手</span>
    </button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const visible = ref(false)
const loading = ref(false)
const input = ref('')
const messages = ref([])
const loadedHistory = ref(false)

const defaultGreeting = {
  role: 'assistant',
  content: '你好，我是马拉松报名系统 AI 助手。你可以咨询赛事、公告、报名、收藏等问题。'
}

const quickPrompts = [
  '最近有哪些在报名？',
  '报名要注意什么？',
  '这场赛事适合新手吗？'
]

const openAssistant = async () => {
  visible.value = true
  if (!loadedHistory.value) {
    await loadHistory()
  }
}

const loadHistory = async () => {
  try {
    const res = await api.getAiHistory()
    if (res.code === 200 && Array.isArray(res.data) && res.data.length > 0) {
      messages.value = res.data.map(item => ({
        role: item.role,
        content: item.content
      }))
    } else {
      messages.value = [defaultGreeting]
    }
  } catch (e) {
    messages.value = [defaultGreeting]
  } finally {
    loadedHistory.value = true
  }
}

const sendMessage = async () => {
  const text = input.value.trim()
  if (!text) {
    ElMessage.warning('请输入问题')
    return
  }

  messages.value.push({ role: 'user', content: text })
  input.value = ''
  loading.value = true
  try {
    const res = await api.aiChat(text)
    if (res.code === 200) {
      messages.value.push({ role: 'assistant', content: res.data.answer || 'AI 没有返回内容。' })
    } else {
      messages.value.push({ role: 'assistant', content: res.msg || 'AI 调用失败。' })
    }
  } catch (e) {
    messages.value.push({ role: 'assistant', content: 'AI 调用失败，请稍后重试。' })
  } finally {
    loading.value = false
  }
}

const handleEnter = (event) => {
  if (!event.shiftKey) {
    event.preventDefault()
    sendMessage()
  }
}

const clearChat = () => {
  messages.value = [defaultGreeting]
}

const fillPrompt = (prompt) => {
  input.value = prompt
}
</script>

<style scoped>
.ai-assistant {
  position: fixed;
  right: 22px;
  bottom: 22px;
  z-index: 3000;
}

.assistant-trigger {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  border: none;
  border-radius: 999px;
  background: #111111;
  color: #ffffff;
  padding: 14px 20px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.25);
}

.assistant-trigger-icon {
  display: inline-grid;
  place-items: center;
  width: 28px;
  height: 28px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.14);
  font-size: 13px;
}

.assistant-panel {
  width: 388px;
  height: 654px;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(226, 232, 240, 0.8);
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.18);
  backdrop-filter: blur(18px);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.assistant-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 18px 16px 18px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.9);
  background: rgba(255, 255, 255, 0.92);
}

.assistant-brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.assistant-icon {
  display: inline-grid;
  place-items: center;
  width: 40px;
  height: 40px;
  border-radius: 14px;
  background: #141414;
  color: #fff;
  font-weight: 700;
  font-size: 15px;
}

.assistant-brand h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 800;
  color: #111827;
}

.assistant-brand p {
  margin: 4px 0 0;
  font-size: 12px;
  color: #6B7280;
}

.assistant-close {
  border: none;
  background: transparent;
  color: #6B7280;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
}

.assistant-body {
  display: flex;
  flex-direction: column;
  min-height: 0;
  flex: 1;
}

.assistant-messages {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 18px 18px 14px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.msg {
  display: flex;
}

.msg.user {
  justify-content: flex-end;
}

.msg.assistant {
  justify-content: flex-start;
}

.bubble {
  max-width: 86%;
  padding: 14px 16px;
  border-radius: 18px;
  line-height: 1.75;
  white-space: pre-wrap;
  word-break: break-word;
  font-size: 15px;
}

.msg.user .bubble {
  background: linear-gradient(135deg, #3B82F6, #2563EB);
  color: #fff;
  border-bottom-right-radius: 6px;
}

.msg.assistant .bubble {
  background: #F5F7FB;
  color: #1F2937;
  border: 1px solid #E5E7EB;
  border-bottom-left-radius: 6px;
}

.assistant-prompts {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 0 18px 14px;
}

.prompt-chip {
  border: 1px solid #E5E7EB;
  background: #fff;
  color: #4B5563;
  padding: 10px 14px;
  border-radius: 999px;
  font-size: 14px;
  cursor: pointer;
}

.assistant-input {
  border-top: 1px solid rgba(226, 232, 240, 0.9);
  padding: 16px 18px 18px;
  background: rgba(255, 255, 255, 0.96);
}

.input-box {
  width: 100%;
  resize: none;
  border: 1px solid #E5E7EB;
  border-radius: 16px;
  padding: 14px 16px;
  font-size: 15px;
  color: #111827;
  outline: none;
}

.input-box::placeholder {
  color: #9CA3AF;
}

.input-box:focus {
  border-color: #93C5FD;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.10);
}

.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
  font-size: 12px;
  color: #9CA3AF;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 12px;
}

.ghost-btn,
.primary-btn {
  border: none;
  border-radius: 999px;
  padding: 10px 20px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
}

.ghost-btn {
  background: #F3F4F6;
  color: #4B5563;
}

.primary-btn {
  background: linear-gradient(135deg, #3B82F6, #2563EB);
  color: #fff;
}

.primary-btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.assistant-panel-enter-active,
.assistant-panel-leave-active {
  transition: opacity 0.18s ease, transform 0.18s ease;
}

.assistant-panel-enter-from,
.assistant-panel-leave-to {
  opacity: 0;
  transform: translateY(12px) scale(0.98);
}
</style>
