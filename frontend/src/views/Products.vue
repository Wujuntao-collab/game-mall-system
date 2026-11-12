<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { shopApi, cartApi } from '../api'
import { isLogin } from '../store/user'

const router = useRouter()
const route = useRoute()
const keyword = ref('')
const categoryId = ref(null)
const categories = ref([])
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = 12

async function load() {
  const res = await shopApi.products({
    keyword: keyword.value || null,
    categoryId: categoryId.value,
    page: page.value,
    size
  })
  list.value = res.data.list
  total.value = res.data.total
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

function search() {
  page.value = 1
  load()
}

watch([categoryId, page], load)
onMounted(async () => {
  categories.value = (await shopApi.categories()).data
  if (route.query.categoryId) categoryId.value = Number(route.query.categoryId)
  await load()
})
</script>

<template>
  <div class="page">
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索游戏 / 道具 / 周边" style="width: 260px" clearable @keyup.enter="search" />
      <el-button type="primary" @click="search">搜索</el-button>
      <el-select v-model="categoryId" placeholder="全部分类" clearable style="width: 160px">
        <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <span class="muted">共 {{ total }} 件商品</span>
    </div>

    <div class="card-grid">
      <el-card v-for="p in list" :key="p.id" shadow="hover" :body-style="{ padding: '0' }">
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

    <el-empty v-if="!list.length" description="没有找到相关商品" />
    <el-pagination
      v-if="total > size"
      class="pager"
      background
      layout="prev, pager, next"
      :total="total"
      :page-size="size"
      v-model:current-page="page"
    />
  </div>
</template>

<style scoped>
.card-body { padding: 12px; }
.name { font-weight: 600; margin-bottom: 6px; cursor: pointer; }
.row { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; }
.pager { margin-top: 20px; justify-content: center; }
</style>
