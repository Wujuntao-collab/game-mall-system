<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '../api'
import { setLogin, isAdmin } from '../store/user'

const router = useRouter()
const form = ref({ username: '', password: '' })
const loading = ref(false)

async function submit() {
  if (!form.value.username || !form.value.password) return ElMessage.warning('请输入账号和密码')
  loading.value = true
  try {
    const res = await authApi.login(form.value)
    setLogin(res.data)
    ElMessage.success('登录成功')
    router.push(isAdmin() ? '/admin/dashboard' : '/')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page login-wrap">
    <el-card class="login-card">
      <div class="title-lg" style="text-align:center">登录游戏商城</div>
      <el-form label-width="0">
        <el-form-item><el-input v-model="form.username" placeholder="用户名" size="large" /></el-form-item>
        <el-form-item><el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password @keyup.enter="submit" /></el-form-item>
        <el-button type="primary" size="large" style="width:100%" :loading="loading" @click="submit">登录</el-button>
      </el-form>
      <div class="tip muted">
        还没有账号？<el-link type="primary" @click="router.push('/register')">立即注册</el-link>
      </div>
      <div class="tip muted">演示管理员：admin / admin123</div>
    </el-card>
  </div>
</template>

<style scoped>
.login-wrap { display: flex; justify-content: center; padding-top: 80px; }
.login-card { width: 380px; }
.tip { text-align: center; margin-top: 12px; }
</style>
