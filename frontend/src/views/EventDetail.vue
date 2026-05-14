<template>
  <div class="detail-page">
    <header class="topbar">
      <div class="topbar-inner">
        <div class="brand" @click="router.push('/home')">
          <span class="brand-mark">M</span>
          <span class="brand-name">马拉松报名系统</span>
        </div>
        <el-button @click="router.back()">返回</el-button>
      </div>
    </header>

    <main class="detail-main">
      <div class="hero-mini">
        <span class="hero-kicker">EVENT DETAIL</span>
        <h1>{{ event.name || '赛事详情' }}</h1>
        <div class="hero-tags">
          <el-tag :type="tagType(event.status)" effect="light" round>{{ statusText(event.status) }}</el-tag>
          <span class="hero-meta">{{ event.location }}</span>
          <span class="hero-meta">{{ event.eventDate }}</span>
        </div>
      </div>

      <div class="detail-grid">
        <div class="detail-body">
          <el-card>
            <template #header><strong>赛事信息</strong></template>
            <div class="info-grid">
              <div class="info-cell" v-for="it in infoItems" :key="it.label">
                <span class="info-label">{{ it.label }}</span>
                <span class="info-value">{{ it.value }}</span>
              </div>
            </div>
          </el-card>

          <el-card>
            <template #header><strong>赛事介绍</strong></template>
            <div class="article" v-html="event.description || '赛事介绍正在更新中。'"></div>
          </el-card>
        </div>

        <aside class="detail-sidebar">
          <el-card>
            <template #header><strong>报名操作</strong></template>
            <div class="action-area">
              <div class="q-row">
                <div class="q-cell">
                  <span class="lbl">状态</span>
                  <strong>{{ statusText(event.status) }}</strong>
                </div>
                <div class="q-cell">
                  <span class="lbl">剩余名额</span>
                  <strong>{{ rem }}</strong>
                </div>
              </div>

              <div v-if="!user" class="abox">
                <el-alert title="登录后可报名或收藏该赛事" type="info" :closable="false" />
                <el-button type="primary" size="large" @click="$router.push('/login')" class="w-full">登录后报名</el-button>
              </div>
              <div v-else-if="hasReg" class="abox">
                <el-alert title="你已报名该赛事" type="success" :closable="false" />
                <p class="reg-status">审核状态：<el-tag :type="rt(reg?.status)" size="small" effect="light">{{ rs(reg?.status) }}</el-tag></p>
              </div>
              <div v-else-if="event.status !== 1" class="abox">
                <el-alert title="当前不在报名时间内" type="warning" :closable="false" />
              </div>
              <div v-else class="abox">
                <el-button type="primary" size="large" @click="showReg = true" class="w-full">立即报名</el-button>
                <p class="hint">提交报名后，将进入管理员审核流程。</p>
              </div>

              <el-button class="w-full" :type="fav ? 'warning' : ''" @click="toggleFav" :disabled="!user" :icon="Star">
                {{ fav ? '取消收藏' : '收藏赛事' }}
              </el-button>
            </div>
          </el-card>

          <el-card>
            <template #header><strong>时间节点</strong></template>
            <div class="tl">
              <div class="tl-item"><span class="tl-dot"></span><div><span class="lbl">报名开始</span><strong>{{ fmt(event.regStartTime) || '待定' }}</strong></div></div>
              <div class="tl-item"><span class="tl-dot"></span><div><span class="lbl">报名截止</span><strong>{{ fmt(event.regEndTime) || '待定' }}</strong></div></div>
              <div class="tl-item"><span class="tl-dot tld"></span><div><span class="lbl">比赛日期</span><strong>{{ event.eventDate || '待定' }}</strong></div></div>
            </div>
          </el-card>
        </aside>
      </div>
    </main>

    <el-dialog v-model="showReg" title="赛事报名" width="480px">
      <el-form :model="rf" :rules="rr" ref="rfRef" label-position="top">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="真实姓名" prop="realName"><el-input v-model="rf.realName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="身份证号" prop="idCard"><el-input v-model="rf.idCard" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="服装尺码" prop="shirtSize">
              <el-select v-model="rf.shirtSize" placeholder="请选择" style="width: 100%">
                <el-option v-for="s in ['S', 'M', 'L', 'XL', 'XXL']" :key="s" :label="s" :value="s" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急联系人" prop="ec"><el-input v-model="rf.ec" placeholder="手机号" /></el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="showReg = false">取消</el-button>
        <el-button type="primary" @click="submit" :loading="sub">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star } from '@element-plus/icons-vue'
import api from '../api'
import { getCurrentUser } from '../utils/auth'

const route = useRoute()
const router = useRouter()
const eid = route.params.id
const event = ref({})
const user = ref(getCurrentUser())
const reg = ref(null)
const fav = ref(false)
const cnt = ref(0)
const showReg = ref(false)
const sub = ref(false)
const rfRef = ref()
const rf = ref({ realName: user.value?.realName || '', idCard: user.value?.idCard || '', shirtSize: '', ec: '' })

const rr = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证号格式不正确', trigger: 'blur' }
  ],
  shirtSize: [{ required: true, message: '请选择服装尺码', trigger: 'change' }],
  ec: [
    { required: true, message: '请输入紧急联系人电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const statusText = s => ['未开始', '报名中', '已截止', '已结束'][s] || '未知'
const tagType = s => ['info', '', 'warning', 'danger'][s] || 'info'
const rs = s => ['待审核', '已通过', '已拒绝', '已取消'][s] || '未知'
const rt = s => ['warning', 'success', 'danger', 'info'][s] || 'info'
const fmt = s => s ? s.replace('T', ' ') : ''

const hasReg = computed(() => reg.value !== null)
const rem = computed(() => Math.max((event.value.quota || 0) - cnt.value, 0))
const infoItems = computed(() => [
  { label: '赛事名称', value: event.value.name || '未命名' },
  { label: '比赛地点', value: event.value.location || '待定' },
  { label: '比赛日期', value: event.value.eventDate || '待定' },
  { label: '报名费用', value: `￥${Number(event.value.entryFee || 0).toFixed(0)}` },
  { label: '报名开始', value: fmt(event.value.regStartTime) || '待定' },
  { label: '报名截止', value: fmt(event.value.regEndTime) || '待定' }
])

onMounted(async () => {
  const er = await api.getEventDetail(eid)
  if (er.code === 200) {
    event.value = er.data
  } else {
    ElMessage.error('赛事不存在')
    router.back()
    return
  }

  cnt.value = event.value.registeredCount || 0

  if (user.value) {
    const mr = await api.getMyRegistrations()
    if (mr.code === 200 && Array.isArray(mr.data)) reg.value = mr.data.find(r => r.eventId == eid && r.status !== 3) || null
    const fr = await api.checkFavorite(eid)
    if (fr.code === 200) fav.value = !!fr.data
  }
})

const toggleFav = async () => {
  if (!user.value) {
    router.push('/login')
    return
  }
  try {
    if (fav.value) {
      await api.removeFavorite(eid)
      fav.value = false
      ElMessage.success('已取消收藏')
    } else {
      await api.addFavorite(eid)
      fav.value = true
      ElMessage.success('收藏成功')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const submit = async () => {
  const v = await rfRef.value.validate().catch(() => false)
  if (!v) return
  sub.value = true
  try {
    const res = await api.submitRegistration({ eventId: eid, shirtSize: rf.value.shirtSize, emergencyContact: rf.value.ec })
    if (res.code === 200) {
      ElMessage.success('报名成功')
      showReg.value = false
      const mr = await api.getMyRegistrations()
      if (mr.code === 200 && Array.isArray(mr.data)) reg.value = mr.data.find(r => r.eventId == eid && r.status !== 3) || null
      const er2 = await api.getEventDetail(eid)
      if (er2.code === 200) {
        event.value = er2.data
        cnt.value = er2.data.registeredCount || 0
      }
    } else {
      ElMessage.error(res.msg || '报名失败')
    }
  } catch (e) {
    ElMessage.error('报名失败')
  } finally {
    sub.value = false
  }
}
</script>

<style scoped>
.detail-page { min-height: 100vh; background: var(--bg); }
.topbar { position: sticky; top: 0; z-index: 30; background: rgba(255,255,255,0.82); border-bottom: 1px solid var(--border); backdrop-filter: blur(20px); }
.topbar-inner { display: flex; justify-content: space-between; align-items: center; max-width: 1180px; margin: 0 auto; height: 64px; padding: 0 24px; }
.brand { display: flex; align-items: center; gap: 10px; cursor: pointer; }
.brand-mark { display: grid; width: 38px; height: 38px; place-items: center; color: #fff; font-family: var(--font-heading); font-weight: 700; font-size: 22px; background: linear-gradient(135deg, #3B82F6, #06B6D4); border-radius: 12px; }
.brand-name { font-family: var(--font-heading); font-weight: 700; font-size: 17px; color: var(--ink); }
.detail-main { max-width: 1180px; margin: 0 auto; padding: 32px 24px 80px; }
.hero-mini { margin-bottom: 28px; }
.hero-kicker { font-size: 12px; font-weight: 700; letter-spacing: 0.1em; color: var(--cta); font-family: var(--font-heading); }
.hero-mini h1 { font-family: var(--font-heading); font-size: 36px; font-weight: 700; margin: 8px 0 14px; }
.hero-tags { display: flex; gap: 12px; align-items: center; flex-wrap: wrap; }
.hero-meta { font-size: 14px; color: var(--muted); font-weight: 500; }
.detail-grid { display: grid; grid-template-columns: 1fr 340px; gap: 24px; }
.detail-body { display: flex; flex-direction: column; gap: 24px; }
.info-grid { display: grid; grid-template-columns: repeat(2,1fr); gap: 14px; }
.info-cell { padding: 14px 16px; background: var(--bg); border-radius: var(--radius-sm); }
.info-label { display: block; font-size: 11px; font-weight: 600; color: var(--muted); text-transform: uppercase; letter-spacing: 0.05em; margin-bottom: 4px; font-family: var(--font-heading); }
.info-value { font-weight: 600; font-size: 15px; }
.article { color: var(--muted); line-height: 1.8; font-size: 15px; }
.detail-sidebar { display: flex; flex-direction: column; gap: 24px; }
.action-area { display: flex; flex-direction: column; gap: 14px; }
.q-row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.q-cell { padding: 14px; background: var(--bg); border-radius: var(--radius-sm); }
.lbl { display: block; font-size: 11px; color: var(--muted); margin-bottom: 2px; }
.q-cell strong { font-family: var(--font-heading); font-size: 20px; color: var(--brand); }
.abox { display: flex; flex-direction: column; gap: 10px; }
.w-full { width: 100%; }
.hint { font-size: 13px; color: var(--muted); line-height: 1.5; }
.reg-status { display: flex; align-items: center; gap: 8px; font-size: 14px; font-weight: 500; }
.tl { display: flex; flex-direction: column; gap: 4px; }
.tl-item { display: flex; gap: 14px; align-items: flex-start; padding: 12px 0; }
.tl-dot { width: 10px; height: 10px; border-radius: 50%; background: var(--brand); flex-shrink: 0; margin-top: 5px; }
.tld { background: var(--cta); }
@media (max-width: 860px) {
  .detail-grid { grid-template-columns: 1fr; }
  .info-grid { grid-template-columns: 1fr; }
}
</style>
