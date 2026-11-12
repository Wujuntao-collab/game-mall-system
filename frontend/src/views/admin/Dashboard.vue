<script setup>
import { onMounted, ref } from 'vue'
import { adminApi } from '../../api'

const stats = ref({})
const cards = ref([])

onMounted(async () => {
  stats.value = (await adminApi.stats()).data
  cards.value = [
    { label: '注册用户', value: stats.value.userCount, color: '#409eff' },
    { label: '在售商品', value: stats.value.productCount, color: '#67c23a' },
    { label: '累计订单', value: stats.value.orderCount, color: '#e6a23c' },
    { label: '成交金额（元）', value: stats.value.salesAmount, color: '#f56c6c' }
  ]
})
</script>

<template>
  <div>
    <div class="title-lg">数据概览</div>
    <div class="cards">
      <el-card v-for="c in cards" :key="c.label" shadow="hover" class="stat">
        <div class="label">{{ c.label }}</div>
        <div class="value" :style="{ color: c.color }">{{ c.value }}</div>
      </el-card>
    </div>
    <el-card shadow="never" class="tips">
      <div class="title-lg" style="font-size:15px">演示路径建议</div>
      <ol class="list">
        <li>商品管理 → 新增一个商品，再回前台搜索验证</li>
        <li>帖子审核 → 通过/驳回前台用户发的帖子</li>
        <li>订单管理 → 对已支付订单点「发货」</li>
        <li>前台确认收货 → 回到概览看成交金额变化</li>
      </ol>
    </el-card>
  </div>
</template>

<style scoped>
.cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.stat .label { color: #909399; font-size: 13px; }
.stat .value { font-size: 30px; font-weight: 700; margin-top: 8px; }
.tips { margin-top: 20px; }
.list { line-height: 2; color: #606266; }
</style>
