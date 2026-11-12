<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { shopApi, cartApi } from '../api'
import { isLogin } from '../store/user'

const router = useRouter()
const banners = ref([])
const notices = ref([])
const products = ref([])

async function load() {
  const [b, n, p] = await Promise.all([
    shopApi.banners(),
    shopApi.notices(),
    shopApi.products({ page: 1, size: 8 })
  ])
  banners.value = b.data
  notices.value = n.data
  products.value = p.data.list
}

async function addCart(product) {
  if (!isLogin()) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  await cartApi.add({ productId: product.id, quantity: 1 })
  ElMessage.success('已加入购物车')
}

onMounted(load)
</script>

<template>
  <div class="page">
    <el-carousel height="260px" v-if="banners.length">
      <el-carousel-item v-for="b in banners" :key="b.id">
        <img :src="b.imageUrl" class="banner" alt="" />
      </el-carousel-item>
    </el-carousel>

    <el-alert
      v-if="notices.length"
      class="notice"
      :title="notices[0].title + '：' + notices[0].content"
      type="info"
      :closable="false"
      show-icon
    />

    <div class="title-lg">热门推荐</div>
    <div class="card-grid">
      <el-card v-for="p in products" :key="p.id" shadow="hover" :body-style="{ padding: '0' }">
        <div class="ph" @click="router.push('/products/' + p.id)">{{ p.name.slice(0, 2) }}</div>
        <div class="card-body">
          <div class="name" @click="router.push('/products/' + p.id)">{{ p.name }}</div>
          <div class="muted">{{ p.categoryName }} · 库存 {{ p.stock }}</div>
          <div class="row">
            <span class="price">¥{{ p.price }}</span>
            <el-button size="small" type="primary" @click="addCart(p)">加入购物车</el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.banner { width: 100%; height: 260px; object-fit: cover; border-radius: 6px; }
.notice { margin: 16px 0; }
.card-body { padding: 12px; }
.name { font-weight: 600; margin-bottom: 6px; cursor: pointer; }
.row { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; }
</style>
