<script setup>
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const menus = [
  { path: '/admin/dashboard', label: '数据概览' },
  { path: '/admin/products', label: '商品管理' },
  { path: '/admin/orders', label: '订单管理' },
  { path: '/admin/users', label: '用户管理' },
  { path: '/admin/posts', label: '帖子审核' },
  { path: '/admin/content', label: '公告与轮播' }
]
</script>

<template>
  <div class="layout">
    <el-aside width="200px" class="aside">
      <div class="logo">管理后台</div>
      <el-menu :default-active="route.path" router class="menu">
        <el-menu-item v-for="m in menus" :key="m.path" :index="m.path">{{ m.label }}</el-menu-item>
      </el-menu>
    </el-aside>
    <div class="main">
      <router-view />
    </div>
  </div>
</template>

<style scoped>
.layout { display: flex; min-height: calc(100vh - 60px); }
.aside { background: #fff; border-right: 1px solid #e4e7ed; }
.logo { font-weight: 700; color: #1f3864; padding: 18px 20px; font-size: 16px; border-bottom: 1px solid #f0f2f5; }
.menu { border-right: none; }
.main { flex: 1; padding: 20px; }
</style>
