<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '../api'

const router = useRouter()
const form = ref({ username: '', password: '', nickname: '' })

async function submit() {
  if (!form.value.username || form.value.password.length < 6) {
    return ElMessage.warning('用户名不能为空，密码至少 6 位')
  }
  await authApi.register(form.value)
  ElMessage.success('注册成功，请登录')
  router.push('/login')
}
</script>

<template>
  <div class="page login-wrap">
    <el-card class="login-card">
      <div class="title-lg" style="text-align:center">注册账号</div>
      <el-form label-width="0">
        <el-form-item><el-input v-model="form.username" placeholder="用户名" size="large" /></el-form-item>
        <el-form-item><el-input v-model="form.nickname" placeholder="昵称（可选）" size="large" /></el-form-item>
        <el-form-item><el-input v-model="form.password" type="password" placeholder="密码（至少 6 位）" size="large" show-password /></el-form-item>
        <el-button type="primary" size="large" style="width:100%" @click="submit">注册</el-button>
      </el-form>
      <div class="tip muted">
        已有账号？<el-link type="primary" @click="router.push('/login')">去登录</el-link>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.login-wrap { display: flex; justify-content: center; padding-top: 80px; }
.login-card { width: 380px; }
.tip { text-align: center; margin-top: 12px; }
</style>
