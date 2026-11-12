<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { shopApi, cartApi, orderApi } from '../api'
import { isLogin } from '../store/user'

const route = useRoute()
const router = useRouter()
const product = ref(null)
const quantity = ref(1)
const address = ref('湖南省长沙市岳麓区某路 1 号')
const buyDialog = ref(false)

async function load() {
  product.value = (await shopApi.product(route.params.id)).data
}

async function addCart() {
  if (!isLogin()) return router.push('/login')
  await cartApi.add({ productId: product.value.id, quantity: quantity.value })
  ElMessage.success('已加入购物车')
}

function openBuy() {
  if (!isLogin()) return router.push('/login')
  buyDialog.value = true
}

async function buyNow() {
  await orderApi.create({
    address: address.value,
    items: [{ productId: product.value.id, quantity: quantity.value }]
  })
  buyDialog.value = false
  ElMessage.success('下单成功，去订单页支付')
  router.push('/orders')
}

onMounted(load)
</script>

<template>
  <div class="page" v-if="product">
    <el-breadcrumb separator="/" class="crumb">
      <el-breadcrumb-item :to="{ path: '/products' }">商城</el-breadcrumb-item>
      <el-breadcrumb-item>{{ product.name }}</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card shadow="never">
      <div class="detail">
        <div class="ph big">{{ product.name.slice(0, 2) }}</div>
        <div class="info">
          <h2>{{ product.name }}</h2>
          <div class="muted">{{ product.categoryName }} · {{ product.type }} · 库存 {{ product.stock }}</div>
          <div class="price big-price">¥{{ product.price }}</div>
          <p class="desc">{{ product.description }}</p>
          <div class="buy">
            <span class="muted">数量</span>
            <el-input-number v-model="quantity" :min="1" :max="product.stock" />
            <el-button type="primary" @click="addCart">加入购物车</el-button>
            <el-button type="warning" @click="openBuy">立即购买</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <el-dialog v-model="buyDialog" title="确认下单" width="420px">
      <el-form label-width="70px">
        <el-form-item label="收货地址">
          <el-input v-model="address" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="合计">
          <span class="price">¥{{ (product.price * quantity).toFixed(2) }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="buyDialog = false">取消</el-button>
        <el-button type="primary" @click="buyNow">提交订单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.crumb { margin-bottom: 16px; }
.detail { display: flex; gap: 28px; }
.ph.big { width: 380px; height: 300px; font-size: 36px; border-radius: 6px; flex: 0 0 380px; }
.info h2 { margin: 0 0 8px; }
.big-price { font-size: 28px; margin: 12px 0; }
.desc { color: #606266; line-height: 1.7; }
.buy { display: flex; align-items: center; gap: 12px; margin-top: 20px; }
</style>
