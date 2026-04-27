<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, User } from '@element-plus/icons-vue'
import { loginByCid, loginByEmail } from '@/api/auth'
import { setSession } from '@/utils/session'
import { applyTheme, isDarkTheme } from '@/utils/theme'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const isDark = ref(isDarkTheme())

const form = reactive({
  account: '',
  password: '',
})

const syncDarkMode = () => {
  isDark.value = applyTheme(isDark.value ? 'dark' : 'light')
}

const persistLogin = (payload) => {
  setSession({
    cid: payload?.cid ?? payload?.user?.cid ?? null,
    token: payload?.token ?? '',
    user: payload?.user ?? null,
    rbac: payload?.rbac ?? { roles: [], permissions: [] },
  })
  localStorage.setItem('cid', String(payload?.cid ?? payload?.user?.cid ?? ''))
}

const handleLogin = async () => {
  if (loading.value) return
  loading.value = true
  try {
    const value = form.account.trim()
    const payload =
      /^\d+$/.test(value)
        ? await loginByCid({ cid: Number(value) })
        : await loginByEmail({ email: value, password: form.password })
    persistLogin(payload.data)
    ElMessage.success('登录成功')
    await router.push(route.query.redirect || '/')
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <section class="auth-hero">
      <div class="auth-hero__overlay"></div>
      <div class="auth-hero__content">
        <p class="eyebrow">Open Aerion Integrated Management Platform</p>
        <h1>进入你的飞行控制台</h1>
        <p>
          用同一个入口管理账号、权限、飞行记录与管制记录，前后端数据已经打通。
        </p>
        <div class="auth-hero__stats">
          <div>
            <strong>账号</strong>
            <span>支持邮箱与 CID</span>
          </div>
          <div>
            <strong>记录</strong>
            <span>飞行 / 管制双轨归档</span>
          </div>
          <div>
            <strong>权限</strong>
            <span>RBAC 实时可见</span>
          </div>
        </div>
      </div>
    </section>

    <section class="auth-panel">
      <div class="theme-toggle">
        <el-switch v-model="isDark" inline-prompt active-text="夜" inactive-text="昼" @change="syncDarkMode" />
      </div>

      <el-card class="auth-card" shadow="never">
        <template #header>
          <div class="auth-card__header">
            <div>
              <h2>欢迎回来</h2>
              <p>支持邮箱登录，也支持直接输入 CID 登录。</p>
            </div>
          </div>
        </template>

        <el-form label-position="top" @submit.prevent="handleLogin">
          <el-form-item label="邮箱或 CID">
            <el-input v-model="form.account" placeholder="name@example.com 或 100001" :prefix-icon="User" />
          </el-form-item>

          <el-form-item label="密码">
            <el-input
              v-model="form.password"
              type="password"
              show-password
              placeholder="邮箱登录时必填"
              :prefix-icon="Lock"
            />
            <div class="field-tip">
              直接输入纯数字 CID 时，会使用后端提供的快速登录链路。
            </div>
          </el-form-item>

          <el-button type="primary" class="submit-button" :loading="loading" @click="handleLogin">
            登录
          </el-button>
        </el-form>

        <div class="auth-links">
          <router-link to="/forgot-password">忘记密码</router-link>
          <router-link to="/register">注册新账号</router-link>
        </div>
      </el-card>
    </section>
  </div>
</template>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1.15fr 0.85fr;
  background: #f3f6fb;
  overflow: auto;
}

.auth-hero {
  position: relative;
  overflow: hidden;
  background:
    linear-gradient(135deg, rgba(14, 116, 144, 0.72), rgba(15, 23, 42, 0.94)),
    url('https://images.unsplash.com/photo-1436491865332-7a61a109cc05?q=80&w=2074&auto=format&fit=crop') center/cover;
}

.auth-hero__overlay {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(125, 211, 252, 0.24), transparent 35%);
}

.auth-hero__content {
  position: relative;
  z-index: 1;
  max-width: 560px;
  padding: 72px 64px;
  color: white;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.28em;
  font-size: 0.78rem;
  color: #bae6fd;
}

.auth-hero h1 {
  font-size: 3.4rem;
  line-height: 1.1;
  margin: 18px 0 20px;
}

.auth-hero p {
  font-size: 1.08rem;
  line-height: 1.8;
  color: #e2e8f0;
}

.auth-hero__stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-top: 36px;
}

.auth-hero__stats div {
  padding: 18px;
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(12px);
}

.auth-hero__stats strong {
  display: block;
  font-size: 1rem;
}

.auth-hero__stats span {
  display: block;
  margin-top: 8px;
  font-size: 0.88rem;
  color: #cbd5e1;
}

.auth-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 24px;
  position: relative;
}

.theme-toggle {
  position: absolute;
  top: 20px;
  right: 24px;
}

.auth-card {
  width: min(100%, 460px);
  border-radius: 28px;
  border: 1px solid #dbe4f0;
  box-shadow: 0 28px 60px rgba(15, 23, 42, 0.08);
}

.auth-card__header h2 {
  margin: 0;
  font-size: 1.8rem;
  color: #111827;
}

.auth-card__header p {
  margin: 10px 0 0;
  color: #6b7280;
}

.submit-button {
  width: 100%;
  height: 44px;
  margin-top: 10px;
}

.field-tip {
  margin-top: 8px;
  font-size: 0.78rem;
  color: #64748b;
}

.auth-links {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-top: 20px;
  font-size: 0.9rem;
}

.auth-links a {
  color: #2563eb;
  text-decoration: none;
}

@media (max-width: 1024px) {
  .auth-page {
    grid-template-columns: 1fr;
  }

  .auth-hero {
    min-height: 280px;
  }

  .auth-hero__content {
    padding: 40px 28px;
  }

  .auth-hero h1 {
    font-size: 2.5rem;
  }

  .auth-panel {
    padding-top: 28px;
  }
}

@media (max-width: 680px) {
  .auth-hero__stats {
    grid-template-columns: 1fr;
  }
}

:global(.dark) .auth-page {
  background: #020617;
}

:global(.dark) .auth-card {
  background: #0f172a;
  border-color: #1e293b;
}

:global(.dark) .auth-card__header h2 {
  color: #f8fafc;
}

:global(.dark) .auth-card__header p,
:global(.dark) .field-tip {
  color: #94a3b8;
}
</style>
