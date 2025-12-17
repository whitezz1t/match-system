<template>
  <el-card shadow="never">
    <template #header>
      <div class="card-header">
        <span>👥 选手列表</span>
        <el-button type="primary" @click="handleAddPlayer">
          <el-icon style="margin-right: 5px"><Plus /></el-icon>添加新选手
        </el-button>
      </div>
    </template>

    <el-table :data="players" style="width: 100%" v-loading="loading" border stripe>
      <el-table-column prop="playerId" label="ID" width="80" align="center" />
      <el-table-column prop="name" label="姓名" width="180" align="center">
        <template #default="scope">
          <el-tag effect="plain" round>{{ scope.row.name }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="头像" align="center">
        <template #default>
          <el-avatar :size="30" icon="UserFilled" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" align="center">
        <template #default>
          <el-button link type="primary" size="small">编辑</el-button>
          <el-button link type="danger" size="small">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, UserFilled } from '@element-plus/icons-vue'

const players = ref([])
const loading = ref(false)

// 加载选手数据
const loadPlayers = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/players')
    players.value = res.data
  } catch (error) {
    ElMessage.error('获取选手列表失败')
  } finally {
    loading.value = false
  }
}

// 添加选手
const handleAddPlayer = () => {
  ElMessageBox.prompt('请输入新选手的名字', '添加选手', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /\S/,
    inputErrorMessage: '名字不能为空'
  }).then(async ({ value }) => {
    try {
      // 后端接口: POST /api/players?name=xxx
      await axios.post('/api/players', null, { params: { name: value } })
      ElMessage.success(`选手 ${value} 添加成功`)
      loadPlayers() // 刷新列表
    } catch (e) {
      ElMessage.error('添加失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  loadPlayers()
})
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>