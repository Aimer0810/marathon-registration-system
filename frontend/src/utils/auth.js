/**
 * 认证工具模块 —— 管理登录状态（Token + 用户信息）
 *
 * 存储位置：sessionStorage
 *   为什么用 sessionStorage 而不是 localStorage？
 *   - sessionStorage：关闭浏览器标签页后自动清除（更安全）
 *   - localStorage：除非手动清除，否则永久保留
 *
 * 存储的两个 key：
 *   - authToken   → 身份令牌（mtk_xxxxxxxx）
 *   - currentUser → 当前用户信息的 JSON 字符串
 *
 * 数据流：
 *   登录成功 → setAuth({token, user}) → 存到 sessionStorage
 *   每次 API 请求 → getToken() → 取出 token 放到请求头
 *   页面刷新后 → getCurrentUser() → 取出用户信息恢复状态
 *   退出登录 → clearAuth() → 删除这两个 key
 */
const USER_KEY = 'currentUser'
const TOKEN_KEY = 'authToken'

/**
 * 获取当前登录用户信息
 * @returns 用户对象 或 null（未登录）
 *
 * JSON.parse 的作用：把 JSON 字符串还原成 JavaScript 对象
 * 因为 sessionStorage 只能存字符串，所以存的时候用 JSON.stringify 转字符串，
 * 取的时候用 JSON.parse 转回对象。
 */
export function getCurrentUser() {
  try {
    return JSON.parse(sessionStorage.getItem(USER_KEY) || 'null')
  } catch {
    return null  // 解析失败也返回 null（数据损坏等情况）
  }
}

/**
 * 保存登录凭证（登录成功后调用）
 * @param {{ token: string, user: object }} payload
 */
export function setAuth(payload) {
  if (!payload) return
  if (payload.token) sessionStorage.setItem(TOKEN_KEY, payload.token)
  if (payload.user) sessionStorage.setItem(USER_KEY, JSON.stringify(payload.user))
}

/**
 * 更新当前用户信息（修改资料后调用，同步更新本地存储）
 */
export function updateCurrentUser(user) {
  if (!user) return
  sessionStorage.setItem(USER_KEY, JSON.stringify(user))
}

/**
 * 获取存储的 Token
 * @returns Token 字符串（没有则返回空字符串）
 */
export function getToken() {
  return sessionStorage.getItem(TOKEN_KEY) || ''
}

/**
 * 清除登录状态（退出登录时调用）
 */
export function clearAuth() {
  sessionStorage.removeItem(USER_KEY)
  sessionStorage.removeItem(TOKEN_KEY)
}
