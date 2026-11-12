<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { postApi } from '../api'
import { isLogin } from '../store/user'

const router = useRouter()
const list = ref([])
const total = ref(0)
const page = ref(1)
const keyword = ref('')
const dialog = ref(false)
const form = ref({ title: '', content: '' })

async function load() {
  const res = await postApi.list({ keyword: keyword.value || null, page: page.value, size: 10 })
  list.value = res.data.list
  total.value = res.data.total
}

function openDialog() {
  if (!isLogin()) return router.push('/login')
  form.value = { title: '', content: '' }
  dialog.value = true
}

async function submit() {
  if (!form.value.title || !form.value.content) {
    return ElMessage.warning('标题和内容都要填')
  }
  await postApi.create(form.value)
  dialog.value = false
  ElMessage.success('已提交，等待管理员审核')
  await load()
}

onMounted(load)
</script>

<template>
  <div class="page">
    <div class="toolbar">
      <div class="title-lg" style="margin:0">游戏社区</div>
      <div style="flex:1"></div>
      <el-input v-model="keyword" placeholder="搜索帖子" style="width: 220px" clearable @keyup.enter="load" />
      <el-button type="primary" @click="openDialog">发帖</el-button>
    </div>

    <el-card shadow="never">
      <div v-for="p in list" :key="p.id" class="post" @click="router.push('/posts/' + p.id)">
        <div class="post-title">{{ p.title }}</div>
        <div class="muted">
          {{ p.nickname || p.username }} · {{ p.views }} 浏览 · {{ p.commentCount }} 评论
          <el-tag v-if="p.productName" size="small" type="warning" style="margin-left: 8px">
            关联商品：{{ p.productName }}
          </el-tag>
        </div>
      </div>
      <el-empty v-if="!list.length" description="还没有帖子" />
    </el-card>

    <el-pagination v-if="total > 10" class="pager" background layout="prev, pager, next"
      :total="total" :page-size="10" v-model:current-page="page" @current-change="load" />

    <el-dialog v-model="dialog" title="发布帖子" width="560px">
      <el-form label-width="60px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="6" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="submit">提交审核</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.post { padding: 14px 4px; border-bottom: 1px solid #f0f2f5; cursor: pointer; }
.post:hover { background: #fafbfc; }
.post-title { font-weight: 600; margin-bottom: 6px; font-size: 15px; }
.pager { margin-top: 16px; justify-content: center; }
</style>
