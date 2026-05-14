<template>
  <div id="app">
    <router-view v-slot="{ Component }">
      <transition name="page-fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
    <AiAssistant />
  </div>
</template>

<script setup>
import AiAssistant from './components/AiAssistant.vue'
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@400;500;600;700&family=DM+Sans:wght@400;500;600;700&display=swap');

:root {
  --brand: #3B82F6;
  --brand-deep: #1D4ED8;
  --accent: #06B6D4;
  --cta: #F59E0B;
  --cta-glow: rgba(245, 158, 11, 0.35);
  --bg: #FAFBFF;
  --surface: rgba(255, 255, 255, 0.78);
  --surface-solid: #FFFFFF;
  --ink: #0F172A;
  --muted: #64748B;
  --border: rgba(148, 163, 184, 0.18);
  --border-solid: #E2E8F0;
  --shadow-sm: 0 1px 3px rgba(15, 23, 42, 0.06), 0 1px 2px rgba(15, 23, 42, 0.04);
  --shadow-md: 0 4px 24px rgba(15, 23, 42, 0.08), 0 1px 4px rgba(15, 23, 42, 0.04);
  --shadow-lg: 0 12px 48px rgba(15, 23, 42, 0.10), 0 2px 8px rgba(15, 23, 42, 0.04);
  --shadow-glow: 0 0 60px rgba(59, 130, 246, 0.12), 0 8px 32px rgba(59, 130, 246, 0.10);
  --radius-sm: 10px;
  --radius-md: 16px;
  --radius-lg: 22px;
  --radius-xl: 28px;
  --radius-full: 9999px;
  --font-heading: 'Space Grotesk', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  --font-body: 'DM Sans', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

* { margin: 0; padding: 0; box-sizing: border-box; }

html {
  background: var(--bg);
  scroll-behavior: smooth;
}

body {
  min-width: 320px;
  color: var(--ink);
  font-family: var(--font-body);
  font-size: 15px;
  line-height: 1.6;
  background: var(--bg);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

h1, h2, h3, h4, h5, h6 {
  font-family: var(--font-heading);
  letter-spacing: -0.02em;
}

::-webkit-scrollbar { width: 6px; }
::-webkit-scrollbar-track { background: transparent; }
::-webkit-scrollbar-thumb { background: #94A3B8; border-radius: 999px; }
::-webkit-scrollbar-thumb:hover { background: #64748B; }

.el-card {
  border: 1px solid var(--border) !important;
  border-radius: var(--radius-lg) !important;
  background: var(--surface) !important;
  backdrop-filter: blur(20px) !important;
  box-shadow: var(--shadow-sm) !important;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

.el-card:hover {
  box-shadow: var(--shadow-md) !important;
}

.el-card__header {
  border-bottom: 1px solid var(--border) !important;
  padding: 22px 26px !important;
  font-family: var(--font-heading);
  font-weight: 700;
  font-size: 16px;
  color: var(--ink);
  background: rgba(255, 255, 255, 0.4);
}

.el-card__body { padding: 26px !important; }

.el-button {
  border-radius: var(--radius-sm) !important;
  font-weight: 600 !important;
  font-size: 14px !important;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

.el-button--primary {
  --el-button-bg-color: var(--brand);
  --el-button-border-color: transparent;
  --el-button-hover-bg-color: #2563EB;
  --el-button-hover-border-color: transparent;
  background: linear-gradient(135deg, var(--brand), var(--brand-deep)) !important;
  border: none !important;
  box-shadow: 0 6px 24px rgba(59, 130, 246, 0.28) !important;
}

.el-button--primary:hover {
  box-shadow: 0 8px 32px rgba(59, 130, 246, 0.40) !important;
  transform: translateY(-1px);
}

.el-input__wrapper,
.el-select__wrapper,
.el-textarea__inner {
  border-radius: var(--radius-sm) !important;
  box-shadow: 0 0 0 1px var(--border) inset !important;
  transition: all 0.2s ease !important;
}

.el-input__wrapper.is-focus,
.el-select__wrapper.is-focus {
  box-shadow: 0 0 0 2px var(--brand) inset, 0 0 0 4px rgba(59, 130, 246, 0.10) !important;
}

.el-table {
  border-radius: var(--radius-md);
  overflow: hidden;
}

.el-table th.el-table__cell {
  background: #F8FAFC !important;
  color: var(--muted);
  font-weight: 600;
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  border-bottom: 2px solid var(--border-solid) !important;
  font-family: var(--font-heading);
}

.el-table td.el-table__cell {
  border-bottom: 1px solid rgba(148, 163, 184, 0.10);
}

.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell {
  background: rgba(248, 250, 252, 0.6);
}

.el-tag {
  border-radius: 6px !important;
  font-weight: 600 !important;
  font-size: 12px !important;
  letter-spacing: 0.01em;
}

.el-menu { border-right: none !important; }

.el-dialog {
  border-radius: var(--radius-xl) !important;
  box-shadow: var(--shadow-lg) !important;
  backdrop-filter: blur(20px);
  background: var(--surface) !important;
}

.el-dialog__header {
  padding: 26px 30px 0 !important;
  font-family: var(--font-heading);
  font-weight: 700;
  font-size: 20px;
}

.el-dialog__body { padding: 22px 30px !important; }
.el-dialog__footer { padding: 0 30px 26px !important; }

.el-pagination.is-background .el-pager li:not(.is-disabled).is-active {
  background: linear-gradient(135deg, var(--brand), var(--brand-deep)) !important;
}

.el-empty__description { color: var(--muted) !important; }

.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.2s cubic-bezier(0.4, 0, 0.2, 1), transform 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.page-fade-enter-from { opacity: 0; transform: translateY(8px); }
.page-fade-leave-to { opacity: 0; transform: translateY(-6px); }

:focus-visible {
  outline: 2px solid var(--brand);
  outline-offset: 2px;
  border-radius: 4px;
}

@media (prefers-reduced-motion: reduce) {
  *, *::before, *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}
</style>
