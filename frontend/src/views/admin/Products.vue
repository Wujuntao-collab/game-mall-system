<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi, shopApi } from '../../api'

const list = ref([])
const total = ref(0)
const page = ref(1)
const keyword = ref('')
const categories = ref([])
const dialog = ref(false)
const form = ref({})

async function load() {
  const res = await adminApi.products({ keyword: keyword.value || null, page: page.value, size: 10 })
  list.value = res.data.list
  total.value = res.data.total
}

function openCreate() {
  form.value = { name: '', categoryId: null, type: 'GAME', price: 0, stock: 0, description: '', status: 1 }
  dialog.value = true
}

function openEdit(row) {
  form.value = { ...row }
  dialog.value = true
}

async function save() {
  if (!form.value.name) return ElMessage.warning('商品名称不能为空')
  if (form.value.id) {
    await adminApi.updateProduct(form.value.id, form.value)
  } else {
    await adminApi.createProduct(form.value)
  }
  dialog.value = false
  ElMessage.success('保存成功')
  await load()
}

async function toggle(row) {
  const next = row.status === 1 ? 0 : 1
  await adminApi.productStatus(row.id, next)
  ElMessage.success(next === 1 ? '已上架' : '已下架')
  await load()
}

async function remove(row) {
  await ElMessageBox.confirm('确认删除商品「' + row.name + '」？', '提示', { type: 'warning' })
  await adminApi.deleteProduct(row.id)
  ElMessage.success('已删除')
  await load()
}

onMounted(async () => {
  categories.value = (await shopApi.categories()).data
  await load()
})
</script>

<template>
  <div>
    <div class="toolbar">
      <div class="title-lg" style="margin:0">商品管理</div>
      <div style="flex:1"></div>
      <el-input v-model="keyword" placeholder="搜索商品" style="width: 200px" clearable @keyup.enter="load" />
      <el-button type="primary" @click="openCreate">新增商品</el-button>
    </div>

    <el-card shadow="never">
      <el-table :data="list">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="type" label="类型" width="90" />
        <el-table-column label="价格" width="100">
          <template #default="{ row }"><span class="price">¥{{ row.price }}</span></template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '在售' : '已下架' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="toggle(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-if="total > 10" class="pager" background layout="prev, pager, next"
        :total="total" :page-size="10" v-model:current-page="page" @current-change="load" />
    </el-card>

    <el-dialog v-model="dialog" :title="form.id ? '编辑商品' : '新增商品'" width="560px">
      <el-form label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="选择分类" style="width:100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type" style="width:100%">
            <el-option label="数字版游戏 GAME" value="GAME" />
            <el-option label="虚拟道具 ITEM" value="ITEM" />
            <el-option label="实体周边 GOODS" value="GOODS" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="form.stock" :min="0" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="上架" inactive-text="下架" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.pager { margin-top: 16px; justify-content: center; }
</style>
