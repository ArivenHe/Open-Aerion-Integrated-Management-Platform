<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Key, Lock, Message, Picture, User } from '@element-plus/icons-vue'
import { fetchCaptcha, register, sendRegisterCode } from '@/api/auth'

const router = useRouter()
const loading = ref(false)
const sending = ref(false)
const captchaImage = ref('')
const captchaKey = ref('')

const form = reactive({
  firstName: '',
  lastName: '',
  email: '',
  password: '',
  passwordConfirm: '',
  captchaCode: '',
  emailCode: '',
})

const loadCaptcha = async () => {
  try {
    const payload = await fetchCaptcha()
    captchaKey.value = payload.data.captchaKey
    captchaImage.value = payload.data.imageBase64
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const handleSendCode = async () => {
  if (!form.email) {
    ElMessage.warning('请先填写邮箱')
    return
  }
  if (!captchaKey.value || !form.captchaCode) {
    ElMessage.warning('请先填写图形验证码')
    return
  }
  sending.value = true
  try {
    await sendRegisterCode({
      email: form.email,
      captchaKey: captchaKey.value,
      imageCode: form.captchaCode,
    })
    ElMessage.success('验证码已发送，请查收邮箱')
    form.captchaCode = ''
    await loadCaptcha()
  } catch (error) {
    ElMessage.error(error.message)
    await loadCaptcha()
  } finally {
    sending.value = false
  }
}

const handleRegister = async () => {
  if (form.password !== form.passwordConfirm) {
    ElMessage.error('两次密码输入不一致')
    return
  }
  loading.value = true
  try {
    const payload = await register({
      firstName: form.firstName,
      lastName: form.lastName,
      email: form.email,
      password: form.password,
      emailCode: form.emailCode,
    })
    await ElMessageBox.alert(
      payload?.data?.cid
        ? `注册成功，您的平台 / FSD CID 为 ${payload.data.cid}。`
        : '注册成功，请前往登录。',
      '注册完成',
      { confirmButtonText: '去登录' }
    )
    await router.push('/login')
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    loading.value = false
  }
}

onMounted(loadCaptcha)
</script>

<template>
  <div class="register-page">
    <section class="register-panel">
      <el-card class="register-card" shadow="never">
        <template #header>
          <div class="register-card__header">
            <h2>创建账号</h2>
            <p>注册后会自动同步平台账号与 FSD CID。</p>
          </div>
        </template>

        <el-form label-position="top" @submit.prevent="handleRegister">
          <div class="grid-two">
            <el-form-item label="名">
              <el-input v-model="form.firstName" :prefix-icon="User" placeholder="Ariven" />
            </el-form-item>
            <el-form-item label="姓">
              <el-input v-model="form.lastName" :prefix-icon="User" placeholder="Wang" />
            </el-form-item>
          </div>

          <el-form-item label="邮箱">
            <el-input v-model="form.email" :prefix-icon="Message" placeholder="name@example.com" />
          </el-form-item>

          <div class="grid-two">
            <el-form-item label="密码">
              <el-input v-model="form.password" type="password" show-password :prefix-icon="Lock" />
            </el-form-item>
            <el-form-item label="确认密码">
              <el-input v-model="form.passwordConfirm" type="password" show-password :prefix-icon="Lock" />
            </el-form-item>
          </div>

          <div class="grid-captcha">
            <el-form-item label="图形验证码">
              <el-input v-model="form.captchaCode" :prefix-icon="Picture" placeholder="输入图形验证码" />
            </el-form-item>
            <div class="captcha-box" @click="loadCaptcha">
              <img v-if="captchaImage" :src="captchaImage" alt="captcha" />
              <span v-else>加载中...</span>
            </div>
          </div>

          <div class="grid-email-code">
            <el-form-item label="邮箱验证码">
              <el-input v-model="form.emailCode" :prefix-icon="Key" placeholder="输入邮箱验证码" />
            </el-form-item>
            <el-button class="email-button" plain :loading="sending" @click="handleSendCode">
              发送验证码
            </el-button>
          </div>

          <el-button type="primary" class="submit-button" :loading="loading" @click="handleRegister">
            完成注册
          </el-button>
        </el-form>

        <div class="register-links">
          <router-link to="/login">已有账号，去登录</router-link>
        </div>
      </el-card>
    </section>
  </div>
</template>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 28px;
  background:
    linear-gradient(135deg, rgba(37, 99, 235, 0.08), rgba(14, 165, 233, 0.1)),
    #f3f6fb;
  overflow: auto;
}

.register-panel {
  width: min(100%, 720px);
}

.register-card {
  border-radius: 28px;
  border: 1px solid #dbe4f0;
}

.register-card__header h2 {
  margin: 0;
  font-size: 1.9rem;
  color: #111827;
}

.register-card__header p {
  margin: 10px 0 0;
  color: #6b7280;
}

.grid-two,
.grid-captcha,
.grid-email-code {
  display: grid;
  gap: 16px;
}

.grid-two {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.grid-captcha,
.grid-email-code {
  grid-template-columns: 1fr 180px;
  align-items: start;
}

.captcha-box {
  height: 78px;
  border: 1px solid #dbe4f0;
  border-radius: 14px;
  overflow: hidden;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
}

.captcha-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.email-button {
  margin-top: 30px;
  height: 42px;
}

.submit-button {
  width: 100%;
  height: 44px;
  margin-top: 6px;
}

.register-links {
  margin-top: 18px;
  text-align: center;
}

.register-links a {
  color: #2563eb;
  text-decoration: none;
}

@media (max-width: 760px) {
  .grid-two,
  .grid-captcha,
  .grid-email-code {
    grid-template-columns: 1fr;
  }

  .email-button {
    margin-top: 0;
  }
}

:global(.dark) .register-page {
  background: #020617;
}

:global(.dark) .register-card {
  background: #0f172a;
  border-color: #1e293b;
}

:global(.dark) .register-card__header h2 {
  color: #f8fafc;
}

:global(.dark) .register-card__header p {
  color: #94a3b8;
}

:global(.dark) .captcha-box {
  background: #0b1220;
  border-color: #1e293b;
}
</style>
