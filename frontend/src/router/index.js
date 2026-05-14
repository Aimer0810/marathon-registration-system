/**
 * 路由配置 —— 定义 URL 路径和页面组件的映射关系
 *
 * 什么是前端路由？
 *   传统网站：每个 URL 对应一个 HTML 文件（多页面应用）
 *   SPA（单页面应用）：所有"页面切换"实际上是在同一个 HTML 里切换组件，
 *   不刷新浏览器。Vue Router 负责管理这些切换。
 *
 * 什么是懒加载？
 *   () => import('../views/Login.vue') 是 ES 模块的动态导入语法。
 *   它不是一次性加载所有页面的代码，而是"访问到这个路由时才加载对应的 JS 文件"。
 *   好处是：首次加载更快，用户不需要下载没访问的页面的代码。
 *
 * 路由守卫（beforeEach）：
 *   每次路由切换前都会执行，用于检查登录状态和权限。
 *   - /user/* 需要登录（requiresAuth: true）
 *   - /admin/* 需要管理员（requiresAdmin: true）
 *   - 不满足条件自动重定向到登录页或首页
 */
import { createRouter, createWebHistory } from 'vue-router'
import { getCurrentUser } from '../utils/auth'

// ===== 懒加载所有页面组件 =====
// 每个 () => import(...) 会生成一个独立的 JS chunk，按需加载
const Login = () => import('../views/Login.vue')
const Register = () => import('../views/Register.vue')
const Home = () => import('../views/Home.vue')
const EventList = () => import('../views/EventList.vue')
const EventDetail = () => import('../views/EventDetail.vue')
const UserCenter = () => import('../views/UserCenter.vue')
const AdminLayout = () => import('../views/admin/AdminLayout.vue')
const Dashboard = () => import('../views/admin/Dashboard.vue')
const AdminEvents = () => import('../views/admin/Events.vue')
const AdminUsers = () => import('../views/admin/Users.vue')
const AdminRegistrations = () => import('../views/admin/Registrations.vue')
const AdminAnnouncements = () => import('../views/admin/Announcements.vue')
const AdminMessages = () => import('../views/admin/Messages.vue')
const Profile = () => import('../views/user/Profile.vue')
const MyRegistrations = () => import('../views/user/MyRegistrations.vue')
const MyMessages = () => import('../views/user/MyMessages.vue')
const MyFavorites = () => import('../views/user/MyFavorites.vue')
const AnnouncementList = () => import('../views/AnnouncementList.vue')
const AnnouncementDetail = () => import('../views/AnnouncementDetail.vue')

/**
 * 路由表 —— 路径 → 组件的映射
 *
 * meta 是路由元信息，用于存储额外的配置：
 *   - title：页面标题（会显示在浏览器标签页上）
 *   - requiresAuth：是否需要登录
 *   - requiresAdmin：是否需要管理员权限
 *
 * 嵌套路由：
 *   /user 路径下有一个 children 数组，/user/profile、/user/registrations 等
 *   都是 /user 的子路由，它们的内容会在 UserCenter.vue 的 <router-view> 中渲染。
 */
const routes = [
  { path: '/announcements', component: AnnouncementList, meta: { title: '公告列表' } },
  { path: '/announcement/:id', component: AnnouncementDetail, meta: { title: '公告详情' } },
  { path: '/login', component: Login, meta: { title: '登录' } },
  { path: '/register', component: Register, meta: { title: '注册' } },
  { path: '/', redirect: '/login' },                // 根路径重定向到登录页
  { path: '/home', component: Home, meta: { title: '首页' } },
  { path: '/events', component: EventList, meta: { title: '赛事列表' } },
  { path: '/event/:id', component: EventDetail, meta: { title: '赛事详情' } },  // :id 是动态参数

  // 用户中心（嵌套路由——子页面在 UserCenter 的 <router-view> 中渲染）
  {
    path: '/user',
    component: UserCenter,
    meta: { requiresAuth: true },  // 需要登录
    children: [
      { path: '', redirect: '/user/profile' },
      { path: 'profile', component: Profile, meta: { title: '个人资料' } },
      { path: 'registrations', component: MyRegistrations, meta: { title: '我的报名' } },
      { path: 'messages', component: MyMessages, meta: { title: '我的留言' } },
      { path: 'favorites', component: MyFavorites, meta: { title: '我的收藏' } }
    ]
  },

  // 管理后台（需要登录 + 管理员权限）
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', component: Dashboard, meta: { title: '工作台' } },
      { path: 'events', component: AdminEvents, meta: { title: '赛事管理' } },
      { path: 'users', component: AdminUsers, meta: { title: '用户管理' } },
      { path: 'registrations', component: AdminRegistrations, meta: { title: '报名审核' } },
      { path: 'announcements', component: AdminAnnouncements, meta: { title: '公告管理' } },
      { path: 'messages', component: AdminMessages, meta: { title: '留言管理' } }
    ]
  }
]

// 创建路由实例
// createWebHistory() 使用 HTML5 History 模式（URL 没有 # 号，看起来更干净）
const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }  // 每次切换路由时滚动到页面顶部
  }
})

/**
 * 全局前置守卫 —— 每次路由切换前触发
 *
 * 检查逻辑：
 *   1. 判断用户是否登录（从 sessionStorage 读取）
 *   2. 如果需要登录但用户未登录 → 跳转到 /login
 *   3. 如果需要管理员但用户不是管理员 → 跳转到 /home
 *   4. 设置页面标题
 */
router.beforeEach((to, from, next) => {
  const user = getCurrentUser()
  document.title = (to.meta.title ? `${to.meta.title} - ` : '') + '马拉松报名系统'

  if (to.meta.requiresAuth && !user) {
    next('/login')     // 未登录，跳转到登录页
  } else if (to.meta.requiresAdmin && user?.role !== 1) {
    next('/home')      // 不是管理员，跳转到首页
  } else {
    next()             // 一切正常，放行
  }
})

export default router
