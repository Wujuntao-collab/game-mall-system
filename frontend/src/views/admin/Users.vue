<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { adminApi } from '../../api'

const list = ref([])
const total = ref(0)
const page = ref(1)

async function load() {
  const res = await adminApi.users({ page: page.value, size: 10 })
  list.value = res.data.list
  total.value = res.data.total
}

async function toggle(row) {
  const next = row.status === 1 ? 0 : 1
  await adminApi.userStatus(row.id, next)
  ElMessage.success(next === 1 ? '已启用' : '已禁用')
  await load()
}

onMounted(load)
</script>

<template>
  <div>
    <div class="title-lg">用户管理</div>
    <el-card shadow="never">
      <el-table :data="list">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column label="角色" width="110">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'">{{ row.role }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button size="small" @click="toggle(row)" :disabled="row.role === 'ADMIN'">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
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
