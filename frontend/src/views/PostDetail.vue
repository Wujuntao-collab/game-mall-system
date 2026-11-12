<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { postApi } from '../api'
import { isLogin } from '../store/user'

const route = useRoute()
const router = useRouter()
const post = ref(null)
const content = ref('')

async function load() {
  post.value = (await postApi.detail(route.params.id)).data
}

async function submit() {
  if (!isLogin()) return router.push('/login')
  if (!content.value.trim()) return ElMessage.warning('评论内容不能为空')
  await postApi.comment(post.value.id, content.value)
  content.value = ''
  ElMessage.success('评论成功')
  await load()
}

onMounted(load)
</script>

<template>
  <div class="page" v-if="post">
    <el-breadcrumb separator="/" class="crumb">
      <el-breadcrumb-item :to="{ path: '/posts' }">社区</el-breadcrumb-item>
      <el-breadcrumb-item>{{ post.title }}</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card shadow="never">
      <h2 class="title">{{ post.title }}</h2>
      <div class="muted">{{ post.nickname || post.username }} · {{ post.views }} 浏览</div>
      <div class="content">{{ post.content }}</div>
      <el-alert v-if="post.productName" type="warning" :closable="false"
        :title="'帖子关联商品：' + post.productName + '（可在商城搜索购买）'" />
    </el-card>

    <el-card shadow="never" class="mt">
      <div class="title-lg" style="font-size:16px">评论 {{ post.comments.length }}</div>
      <div v-for="c in post.comments" :key="c.id" class="comment">
        <span class="who">{{ c.nickname || c.username }}</span>
        <span>{{ c.content }}</span>
      </div>
      <el-empty v-if="!post.comments.length" description="还没有评论" :image-size="60" />
      <div class="editor">
        <el-input v-model="content" type="textarea" :rows="3" placeholder="说点什么…" />
        <el-button type="primary" class="mt8" @click="submit">发表评论</el-button>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.crumb { margin-bottom: 16px; }
.title { margin: 0 0 6px; }
.content { margin: 16px 0; line-height: 1.9; white-space: pre-wrap; }
.mt { margin-top: 16px; }
.comment { padding: 10px 0; border-bottom: 1px dashed #ebeef5; display: flex; gap: 10px; }
.who { color: #1f3864; font-weight: 600; min-width: 80px; }
.editor { margin-top: 16px; }
.mt8 { margin-top: 8px; }
</style>
