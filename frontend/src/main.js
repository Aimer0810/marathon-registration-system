/**
 * Vue 应用的入口文件 —— 整个前端的启动点
 *
 * 这个文件做的事情（按顺序）：
 *   1. createApp(App) —— 创建一个 Vue 应用实例，根组件是 App.vue
 *   2. app.use(ElementPlus) —— 安装 Element Plus 组件库（全局注册所有 el-xxx 组件）
 *   3. app.use(router) —— 安装 Vue Router（这样每个页面都能使用路由功能）
 *   4. app.mount('#app') —— 把 Vue 应用挂载到 index.html 中 id="app" 的 div 上
 *
 * 什么是 Vue？
 *   Vue 是一个前端框架，核心思想是"响应式"——数据变了，页面自动跟着变。
 *   你需要关注的是数据（状态），而不是手动操作 DOM（document.getElementById 之类的）。
 *
 * 什么是 Element Plus？
 *   Element Plus 是专为 Vue 3 设计的 UI 组件库，提供了开箱即用的
 *   按钮、表格、输入框、弹窗等组件，不用自己从零写样式。
 *
 * 什么是 Vue Router？
 *   Vue 的官方路由库，实现"单页面应用（SPA）"——用户感觉在切换页面，
 *   但实际页面没有刷新，只是切换了显示的组件。
 */
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'                // Element Plus 的 CSS 样式
import App from './App.vue'                          // 根组件
import router from './router'                        // 路由配置

const app = createApp(App)
app.use(ElementPlus)   // 注册 Element Plus
app.use(router)        // 注册路由
app.mount('#app')      // 挂载到页面
