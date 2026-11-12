<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { adminApi } from '../../api'

const list = ref([])
const total = ref(0)
const page = ref(1)
const status = ref(null)
const STATUS = { 0: '待审核', 1: '已通过', 2: '已驳回' }
const TYPE = { 0: 'warning', 1: 'success', 2: 'danger' }

async function load() {
  const res = await adminApi.posts({ status: status.value, page: page.value, size: 10 })
  list.value = res.data.list
  total.value = res.data.total
}

async function audit(row, next) {
  await adminApi.audit(row.id, next)
  ElMessage.success(next === 1 ? '已通过' : '已驳回')
  await load()
}

onMounted(load)
</script>

<template>
  <div>
    <div class="toolbar">
      <div class="title-lg" style="margin:0">帖子审核</div>
      <div style="flex:1"></div>
      <el-select v-model="status" placeholder="全部状态" clearable style="width: 140px" @change="load">
        <el-option label="待审核" :value="0" />
        <el-option label="已通过" :value="1" />
        <el-option label="已驳回" :value="2" />
      </el-select>
    </div>

    <el-card shadow="never">
      <el-table :data="list">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="nickname" label="作者" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }"><el-tag :type="TYPE[row.status]">{{ STATUS[row.status] }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="views" label="浏览" width="80" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" type="success" @click="audit(row, 1)">通过</el-button>
            <el-button size="small" type="danger" @click="audit(row, 2)">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-if="total > 10" class="pager" background layout="prev, pager, next"
        :total="total" :page-size="10" v-model:current-page="page" @current-change="load" />
    </el-card>
  </div>
</template>

<style scoped>
.pager { margin-top: 16px; justify-content: center; }
</style>
