<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { orderApi } from '../api'

const list = ref([])
const total = ref(0)
const page = ref(1)

const STATUS = { 0: '待支付', 1: '已支付', 2: '已发货', 3: '已完成', 4: '已取消' }
const TYPE = { 0: 'info', 1: 'warning', 2: 'primary', 3: 'success', 4: 'danger' }

async function load() {
  const res = await orderApi.list({ page: page.value, size: 10 })
  list.value = res.data.list
  total.value = res.data.total
}

async function act(fn, success) {
  await fn()
  ElMessage.success(success)
  await load()
}

onMounted(load)
</script>

<template>
  <div class="page">
    <div class="title-lg">我的订单</div>
    <el-card shadow="never">
      <el-table :data="list" v-if="list.length">
        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="items">
              <div v-for="i in row.items" :key="i.id" class="item">
                <span>{{ i.productName }}</span>
                <span class="muted">×{{ i.quantity }}</span>
                <span class="price">¥{{ i.price }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column label="金额" width="120">
          <template #default="{ row }"><span class="price">¥{{ row.totalAmount }}</span></template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="TYPE[row.status]">{{ STATUS[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="address" label="收货地址" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" size="small" type="primary" @click="act(() => orderApi.pay(row.id), '支付成功')">支付</el-button>
            <el-button v-if="row.status === 0" size="small" @click="act(() => orderApi.cancel(row.id), '已取消')">取消</el-button>
            <el-button v-if="row.status === 2" size="small" type="success" @click="act(() => orderApi.confirm(row.id), '已确认收货')">确认收货</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="还没有订单" />
    </el-card>
    <el-pagination v-if="total > 10" class="pager" background layout="prev, pager, next"
      :total="total" :page-size="10" v-model:current-page="page" @current-change="load" />
  </div>
</template>

<style scoped>
.items { padding: 8px 24px; }
.item { display: flex; gap: 24px; padding: 6px 0; border-bottom: 1px dashed #ebeef5; }
.pager { margin-top: 16px; justify-content: center; }
</style>
