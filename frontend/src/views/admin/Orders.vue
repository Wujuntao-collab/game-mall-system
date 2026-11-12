<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { adminApi } from '../../api'

const list = ref([])
const total = ref(0)
const page = ref(1)
const STATUS = { 0: '待支付', 1: '已支付', 2: '已发货', 3: '已完成', 4: '已取消' }
const TYPE = { 0: 'info', 1: 'warning', 2: 'primary', 3: 'success', 4: 'danger' }

async function load() {
  const res = await adminApi.orders({ page: page.value, size: 10 })
  list.value = res.data.list
  total.value = res.data.total
}

async function ship(row) {
  await adminApi.ship(row.id)
  ElMessage.success('已发货')
  await load()
}

onMounted(load)
</script>

<template>
  <div>
    <div class="title-lg">订单管理</div>
    <el-card shadow="never">
      <el-table :data="list">
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
        <el-table-column prop="username" label="下单用户" width="120" />
        <el-table-column label="金额" width="110">
          <template #default="{ row }"><span class="price">¥{{ row.totalAmount }}</span></template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }"><el-tag :type="TYPE[row.status]">{{ STATUS[row.status] }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="address" label="收货地址" />
        <el-table-column label="操作" width="110">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" size="small" type="primary" @click="ship(row)">发货</el-button>
            <span v-else class="muted">—</span>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-if="total > 10" class="pager" background layout="prev, pager, next"
        :total="total" :page-size="10" v-model:current-page="page" @current-change="load" />
    </el-card>
  </div>
</template>

<style scoped>
.items { padding: 8px 24px; }
.item { display: flex; gap: 24px; padding: 6px 0; border-bottom: 1px dashed #ebeef5; }
.pager { margin-top: 16px; justify-content: center; }
</style>
