<template>
  <div>
    <el-card shadow="never" style="margin-bottom: 20px;">
      <div class="filter-header">
        <span style="font-weight: bold; font-size: 16px;">📅 比赛日程管理</span>
        <el-button type="success" @click="showStartDialog = true">
          <el-icon style="margin-right: 5px"><VideoPlay /></el-icon>开始新比赛
        </el-button>
      </div>
    </el-card>

    <el-table :data="matches" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="matchId" label="ID" width="80" align="center" />
      <el-table-column prop="matchDate" label="比赛时间" width="180" align="center">
        <template #default="scope">
          {{ new Date(scope.row.matchDate).toLocaleString() }}
        </template>
      </el-table-column>
      <el-table-column prop="level" label="级别" width="100" align="center">
        <template #default="scope">
          <el-tag type="warning" size="small">{{ scope.row.level || '普通赛' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="对阵双方" align="center">
        <template #default="scope">
          <span style="font-weight: bold">{{ scope.row.playerAName }}</span>
          <span style="margin: 0 10px; color: #909399">VS</span>
          <span style="font-weight: bold">{{ scope.row.playerBName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="当前比分" width="120" align="center">
        <template #default="scope">
          <span style="font-size: 16px; font-weight: bold; color: #409EFF">
            {{ scope.row.finalScoreA }} : {{ scope.row.finalScoreB }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'ONGOING' ? 'success' : 'info'">
            {{ scope.row.status === 'ONGOING' ? '进行中' : '已结束' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" align="center">
        <template #default="scope">
          <el-button type="primary" size="small" @click="goToScoreboard(scope.row.matchId)">
            进入计分
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showStartDialog" title="发起新比赛" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="比赛级别">
          <el-select v-model="form.level" placeholder="请选择级别">
            <el-option label="决赛" value="决赛" />
            <el-option label="半决赛" value="半决赛" />
            <el-option label="小组赛" value="小组赛" />
          </el-select>
        </el-form-item>
        <el-form-item label="选手A">
          <el-select v-model="form.playerAId" placeholder="选择选手A">
            <el-option v-for="p in players" :key="p.playerId" :label="p.name" :value="p.playerId" />
          </el-select>
        </el-form-item>
        <el-form-item label="选手B">
          <el-select v-model="form.playerBId" placeholder="选择选手B">
            <el-option v-for="p in players" :key="p.playerId" :label="p.name" :value="p.playerId" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showStartDialog = false">取消</el-button>
        <el-button type="primary" @click="handleStartMatch">确定开始</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { VideoPlay } from '@element-plus/icons-vue'

const router = useRouter()
const matches = ref([])
const players = ref([])
const loading = ref(false)
const showStartDialog = ref(false)

const form = reactive({ level: '小组赛', playerAId: null, playerBId: null })

// 加载比赛和选手数据
const loadData = async () => {
  loading.value = true
  try {
    const [matchRes, playerRes] = await Promise.all([
      axios.get('/api/matches'),
      axios.get('/api/players')
    ])
    matches.value = matchRes.data.reverse() // 最新的在前面
    players.value = playerRes.data
  } catch (e) { ElMessage.error('数据加载失败') }
  finally { loading.value = false }
}

// 开始比赛
const handleStartMatch = async () => {
  if (!form.playerAId || !form.playerBId) {
    ElMessage.warning('请选择两名选手')
    return
  }
  if (form.playerAId === form.playerBId) {
    ElMessage.warning('不能选择同一名选手')
    return
  }

  try {
    const res = await axios.post('/api/matches/start', null, {
      params: {
        playerAId: form.playerAId,
        playerBId: form.playerBId,
        level: form.level
      }
    })
    ElMessage.success('比赛创建成功！')
    showStartDialog.value = false
    loadData() // 刷新列表
    // 直接跳转去计分
    goToScoreboard(res.data.matchId)
  } catch (e) {
    ElMessage.error('创建失败')
  }
}

// 跳转到计分板 (带参数)
const goToScoreboard = (matchId) => {
  router.push({ path: '/menu/scoreboard', query: { id: matchId } })
}

onMounted(() => { loadData() })
</script>

<style scoped>
.filter-header { display: flex; justify-content: space-between; align-items: center; }
</style>