<template>
  <div class="empty-state-page">
    <el-card class="empty-state-card" shadow="never">
      <template #header>
        <div class="empty-state-card__header">
          <el-tag type="warning" effect="dark">开发中</el-tag>
          <h2>忘记密码功能暂未接通</h2>
        </div>
      </template>

      <p>
        当前后端还没有提供忘记密码接口，所以我先把这个入口改成了明确提示，避免前端继续请求不存在的 API。
      </p>
      <p>
        等后端补上验证码发送和密码重置接口后，这个页面可以直接继续接成完整流程。
      </p>

      <div class="empty-state-actions">
        <router-link to="/login">
          <el-button type="primary">返回登录</el-button>
        </router-link>
        <router-link to="/register">
          <el-button plain>去注册新账号</el-button>
        </router-link>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.empty-state-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background:
    linear-gradient(135deg, rgba(251, 146, 60, 0.1), rgba(15, 23, 42, 0.12)),
    #f3f6fb;
  overflow: auto;
}

.empty-state-card {
  width: min(100%, 680px);
  border-radius: 28px;
  border: 1px solid #e2e8f0;
}

.empty-state-card__header {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.empty-state-card__header h2 {
  margin: 0;
  font-size: 1.8rem;
  color: #111827;
}

.empty-state-card p {
  color: #475569;
  line-height: 1.9;
}

.empty-state-actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}

:global(.dark) .empty-state-page {
  background: #020617;
}

:global(.dark) .empty-state-card {
  background: #0f172a;
  border-color: #1e293b;
}

:global(.dark) .empty-state-card__header h2 {
  color: #f8fafc;
}

:global(.dark) .empty-state-card p {
  color: #94a3b8;
}
</style>
