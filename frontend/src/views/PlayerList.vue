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
          <el-tag effect="plain" round size="large">{{ scope.row.name }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="头像" align="center">
        <template #default>
          <el-avatar :size="30" icon="UserFilled" />
        </template>
      </el-table-column>

      <el-table-column label="操作" width="200" align="center">
        <template #default="scope">
          <el-button link type="primary" size="small" @click="handleEdit(scope.row)">
            <el-icon><Edit /></el-icon> 编辑
          </el-button>

          <el-button link type="danger" size="small" @click="handleDelete(scope.row)">
            <el-icon><Delete /></el-icon> 删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
// 记得引入 Edit 和 Delete 图标
import { Plus, UserFilled, Edit, Delete } from '@element-plus/icons-vue'

const players = ref([])
const loading = ref(false)

// 1. 加载选手数据
const loadPlayers = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/players')
    players.value = res.data.reverse() // 新添加的在前面
  } catch (error) {
    ElMessage.error('获取选手列表失败')
  } finally {
    loading.value = false
  }
}

// 2. 添加选手
const handleAddPlayer = () => {
  ElMessageBox.prompt('请输入新选手的名字', '添加选手', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /\S/, // 非空正则
    inputErrorMessage: '名字不能为空'
  }).then(async ({value}) => {
    try {
      await axios.post('/api/players', null, {params: {name: value}})
      ElMessage.success(`选手 ${value} 添加成功`)
      loadPlayers()
    } catch (e) {
      ElMessage.error('添加失败')
    }
  }).catch(() => {
  })
}

// 3. ✨ 编辑选手
const handleEdit = (player) => {
  ElMessageBox.prompt('修改选手姓名', '编辑', {
    confirmButtonText: '保存',
    cancelButtonText: '取消',
    inputValue: player.name, // 默认显示旧名字
    inputPattern: /\S/,
    inputErrorMessage: '名字不能为空'
  }).then(async ({value}) => {
    // 如果名字没变，就不发请求
    if (value === player.name) return;

    try {
      // 发送 PUT 请求: /api/players/{id}?name=新名字
      await axios.put(`/api/players/${player.playerId}`, null, {params: {name: value}})
      ElMessage.success('修改成功')
      loadPlayers() // 刷新
    } catch (e) {
      ElMessage.error('修改失败')
    }
  }).catch(() => {
  })
}

// 4. ✨ 删除选手
const handleDelete = (player) => {
  ElMessageBox.confirm(
      `确定要删除选手 "${player.name}" 吗？此操作无法恢复。`,
      '警告',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
  ).then(async () => {
    try {
      // 发送 DELETE 请求
      await axios.delete(`/api/players/${player.playerId}`)
      ElMessage.success('删除成功')
      loadPlayers() // 刷新
    } catch (e) {
      // 捕获外键约束错误（如果选手参加过比赛，后端会报错）
      if (e.response && e.response.status === 500) {
        ElMessage.error('删除失败：该选手已有比赛记录，无法删除！')
      } else {
        ElMessage.error('删除失败')
      }
    }
  }).catch(() => {
  })
}

onMounted(() => {
  loadPlayers()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>