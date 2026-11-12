<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi, shopApi } from '../../api'

const notices = ref([])
const banners = ref([])
const noticeDialog = ref(false)
const bannerDialog = ref(false)
const noticeForm = ref({})
const bannerForm = ref({})

async function load() {
  notices.value = (await shopApi.notices()).data
  banners.value = (await shopApi.banners()).data
}

function editNotice(row) {
  noticeForm.value = row ? { ...row } : { title: '', content: '' }
  noticeDialog.value = true
}

async function saveNotice() {
  if (!noticeForm.value.title) return ElMessage.warning('标题不能为空')
  if (noticeForm.value.id) {
    await adminApi.noticeUpdate(noticeForm.value.id, noticeForm.value)
  } else {
    await adminApi.noticeCreate(noticeForm.value)
  }
  noticeDialog.value = false
  ElMessage.success('已保存')
  await load()
}

async function removeNotice(row) {
  await ElMessageBox.confirm('确认删除该公告？', '提示', { type: 'warning' })
  await adminApi.noticeDelete(row.id)
  ElMessage.success('已删除')
  await load()
}

function editBanner(row) {
  bannerForm.value = row ? { ...row } : { imageUrl: '', linkUrl: '', sort: 0 }
  bannerDialog.value = true
}

async function saveBanner() {
  if (!bannerForm.value.imageUrl) return ElMessage.warning('图片地址不能为空')
  if (bannerForm.value.id) {
    await adminApi.bannerUpdate(bannerForm.value.id, bannerForm.value)
  } else {
    await adminApi.bannerCreate(bannerForm.value)
  }
  bannerDialog.value = false
  ElMessage.success('已保存')
  await load()
}

async function removeBanner(row) {
  await ElMessageBox.confirm('确认删除该轮播图？', '提示', { type: 'warning' })
  await adminApi.bannerDelete(row.id)
  ElMessage.success('已删除')
  await load()
}

onMounted(load)
</script>

<template>
  <div>
    <div class="title-lg">公告与轮播</div>
    <el-card shadow="never" class="mb">
      <div class="toolbar">
        <span class="sub">公告</span>
        <div style="flex:1"></div>
        <el-button type="primary" size="small" @click="editNotice(null)">新增公告</el-button>
      </div>
      <el-table :data="notices">
        <el-table-column prop="title" label="标题" width="220" />
        <el-table-column prop="content" label="内容" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="editNotice(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="removeNotice(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card shadow="never">
      <div class="toolbar">
        <span class="sub">轮播图</span>
        <div style="flex:1"></div>
        <el-button type="primary" size="small" @click="editBanner(null)">新增轮播</el-button>
      </div>
      <el-table :data="banners">
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="imageUrl" label="图片地址" />
        <el-table-column prop="linkUrl" label="跳转链接" width="160" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="editBanner(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="removeBanner(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="noticeDialog" :title="noticeForm.id ? '编辑公告' : '新增公告'" width="520px">
      <el-form label-width="60px">
        <el-form-item label="标题"><el-input v-model="noticeForm.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="noticeForm.content" type="textarea" :rows="4" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="noticeDialog = false">取消</el-button>
        <el-button type="primary" @click="saveNotice">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="bannerDialog" :title="bannerForm.id ? '编辑轮播' : '新增轮播'" width="520px">
      <el-form label-width="80px">
        <el-form-item label="图片地址"><el-input v-model="bannerForm.imageUrl" placeholder="https://…" /></el-form-item>
        <el-form-item label="跳转链接"><el-input v-model="bannerForm.linkUrl" placeholder="/products" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="bannerForm.sort" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bannerDialog = false">取消</el-button>
        <el-button type="primary" @click="saveBanner">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.mb { margin-bottom: 16px; }
.sub { font-weight: 600; color: #1f3864; }
</style>
