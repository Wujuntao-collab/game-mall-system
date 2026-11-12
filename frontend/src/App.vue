<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { userState, logout, isLogin, isAdmin } from './store/user'

const router = useRouter()
const cartCount = computed(() => window.__cartCount || 0)

function go(path) {
  router.push(path)
}

function doLogout() {
  logout()
  router.push('/')
}
</script>

<template>
  <el-header class="header">
    <div class="header-inner">
      <div class="brand" @click="go('/')">游戏商城系统</div>
      <el-menu mode="horizontal" :ellipsis="false" router :default-active="$route.path" class="menu">
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/products">商城</el-menu-item>
        <el-menu-item index="/posts">社区</el-menu-item>
        <el-menu-item v-if="isLogin()" index="/cart">购物车</el-menu-item>
        <el-menu-item v-if="isLogin()" index="/orders">我的订单</el-menu-item>
        <el-menu-item v-if="isAdmin()" index="/admin/dashboard">管理后台</el-menu-item>
      </el-menu>
      <div class="right">
        <template v-if="isLogin()">
          <el-dropdown>
            <span class="user">{{ userState.user?.nickname || userState.user?.username }}</span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-if="isAdmin()" @click="go('/admin/dashboard')">管理后台</el-dropdown-item>
                <el-dropdown-item @click="doLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button link @click="go('/login')">登录</el-button>
          <el-button type="primary" size="small" @click="go('/register')">注册</el-button>
        </template>
      </div>
    </div>
  </el-header>
  <router-view />
</template>

<style scoped>
.header {
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  height: 60px;
  padding: 0;
}
.header-inner {
  max-width: 1180px;
  margin: 0 auto;
  height: 60px;
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 0 16px;
}
.brand {
  font-size: 18px;
  font-weight: 700;
  color: #1f3864;
  cursor: pointer;
  white-space: nowrap;
}
.menu { flex: 1; border-bottom: none; }
.right { display: flex; align-items: center; gap: 8px; }
.user { cursor: pointer; color: #1f3864; font-weight: 600; }
</style>
