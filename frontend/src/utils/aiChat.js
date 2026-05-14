/**
 * AI 聊天历史本地缓存工具
 *
 * 这个文件提供了一套基于 localStorage 的聊天记录缓存机制。
 * 注意：这只是前端本地缓存，真正的持久化存储在后端 ai_chat_message 表中。
 *
 * 前端加载聊天的优先级：
 *   1. 打开 AI 面板 → 先从后端 API 加载历史记录（最新、最全）
 *   2. 后端加载失败 → 降级使用 localStorage 中的缓存
 *
 * 存储 key 格式：aiChatHistory:<userId>
 * 比如用户 ID 为 5，key 就是 "aiChatHistory:5"
 */
const AI_CHAT_PREFIX = 'aiChatHistory:'

/** 加载缓存 */
export function loadAiChatHistory(userId) {
  if (!userId) return []
  try {
    return JSON.parse(localStorage.getItem(`${AI_CHAT_PREFIX}${userId}`) || '[]')
  } catch {
    return []
  }
}

/** 保存缓存 */
export function saveAiChatHistory(userId, messages) {
  if (!userId) return
  localStorage.setItem(`${AI_CHAT_PREFIX}${userId}`, JSON.stringify(messages || []))
}

/** 清除缓存 */
export function clearAiChatHistory(userId) {
  if (!userId) return
  localStorage.removeItem(`${AI_CHAT_PREFIX}${userId}`)
}
