<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cartApi, orderApi } from '../api'

const router = useRouter()
const items = ref([])
const address = ref('湖南省长沙市岳麓区某路 1 号')

const total = computed(() =>
  items.value.reduce((sum, i) => sum + Number(i.price) * i.quantity, 0)
)

async function load() {
  items.value = (await cartApi.list()).data
}

async function change(item, value) {
  await cartApi.update(item.id, value)
  await load()
}

async function remove(item) {
  await cartApi.remove(item.id)
  await load()
}

async function checkout() {
  if (!items.value.length) return
  await ElMessageBox.confirm('确认用当前购物车下单？', '提示')
  await orderApi.create({ address: address.value })
  ElMessage.success('下单成功')
  router.push('/orders')
}

onMounted(load)
</script>

<template>
  <div class="page">
    <div class="title-lg">购物车</div>
    <el-card shadow="never">
      <el-table :data="items" v-if="items.length">
        <el-table-column label="商品">
          <template #default="{ row }">
            <div class="cell">
              <div class="thumb">{{ row.productName.slice(0, 2) }}</div>
              <div>
                <div>{{ row.productName }}</div>
                <div class="muted">库存 {{ row.stock }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="120">
          <template #default="{ row }"><span class="price">¥{{ row.price }}</span></template>
        </el-table-column>
        <el-table-column label="数量" width="160">
          <template #default="{ row }">
            <el-input-number v-model="row.quantity" :min="1" :max="row.stock" size="small" @change="(v) => change(row, v)" />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="120">
          <template #default="{ row }">
            <span class="price">¥{{ (row.price * row.quantity).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column width="90">
          <template #default="{ row }">
            <el-button link type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="购物车是空的" />
    </el-card>

    <div class="footer" v-if="items.length">
      <div class="total">合计：<span class="price">¥{{ total.toFixed(2) }}</span></div>
      <el-button type="primary" @click="checkout">结算下单</el-button>
    </div>
  </div>
</template>

<style scoped>
.cell { display: flex; align-items: center; gap: 12px; }
.thumb {
  width: 44px; height: 44px; border-radius: 4px; color: #fff; font-size: 12px;
  display: flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, #1f3864, #4a6fa5);
}
.footer { display: flex; justify-content: flex-end; align-items: center; gap: 20px; margin-top: 16px; }
.total { font-size: 16px; }
</style>
