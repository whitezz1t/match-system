<template>
  <div>
    <el-card shadow="never" style="margin-bottom: 20px;">
      <div class="header-row">
        <span style="font-weight: bold; font-size: 16px;">📅 比赛历史记录</span>
        <el-button type="success" @click="showStartDialog = true">
          <el-icon style="margin-right: 5px"><VideoPlay /></el-icon>开始新比赛
        </el-button>
      </div>

      <div class="filter-row" style="margin-top: 20px; display: flex; gap: 15px; flex-wrap: wrap; align-items: center;">

        <el-date-picker
            v-model="filters.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 300px;"
        />

        <el-select v-model="filters.level" placeholder="比赛级别" clearable style="width: 150px;">
          <el-option label="决赛" value="决赛" />
          <el-option label="半决赛" value="半决赛" />
          <el-option label="小组赛" value="小组赛" />
        </el-select>

        <el-select v-model="filters.playerId" placeholder="包含选手" clearable filterable style="width: 150px;">
          <el-option v-for="p in players" :key="p.playerId" :label="p.name" :value="p.playerId" />
        </el-select>

        <el-button type="primary" @click="loadData">
          <el-icon style="margin-right: 5px"><Search /></el-icon> 搜索
        </el-button>
        <el-button @click="resetFilters">重置</el-button>

      </div>
    </el-card>

    <el-table :data="matches" v-loading="loading" border stripe style="width: 100%">

      <el-table-column prop="matchId" label="ID" width="80" align="center" />

      <el-table-column prop="matchDate" label="比赛时间" width="180" align="center">
        <template #default="scope">
          {{ scope.row.matchDate ? new Date(scope.row.matchDate).toLocaleString() : '无时间' }}
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

      <el-table-column label="操作" width="280" align="center">
        <template #default="scope">
          <el-button type="primary" size="small" @click="goToScoreboard(scope.row.matchId)">
            详情
          </el-button>

          <el-button type="success" size="small" @click="handleExport(scope.row.matchId)">
            <el-icon><Download /></el-icon>
          </el-button>

          <el-button type="danger" size="small" @click="handleDelete(scope.row.matchId)">
            <el-icon><Delete /></el-icon> 删除
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
import { ElMessageBox, ElMessage } from 'element-plus'
import { VideoPlay, Download, Delete, Search } from '@element-plus/icons-vue' // ✨ 引入 Search 图标

const router = useRouter()
const matches = ref([])
const players = ref([])
const loading = ref(false)
const showStartDialog = ref(false)

// ✨ 新增：筛选条件状态
const filters = reactive({
  dateRange: null, // [startDate, endDate]
  level: '',
  playerId: null
})

const form = reactive({ level: '小组赛', playerAId: null, playerBId: null })

// ✨ 修改：loadData 支持发送筛选参数
const loadData = async () => {
  loading.value = true
  try {
    // 1. 构造查询参数
    const params = {}
    if (filters.level) params.level = filters.level
    if (filters.playerId) params.playerId = filters.playerId
    if (filters.dateRange && filters.dateRange.length === 2) {
      params.startDate = filters.dateRange[0]
      params.endDate = filters.dateRange[1]
    }

    // 2. 发起请求
    const [matchRes, playerRes] = await Promise.all([
      // ✨ 关键点：把 params 传给后端
      axios.get('/api/matches', { params }),
      axios.get('/api/players')
    ])

    // 注意：后端 Repository 已经写了 DESC 排序，这里不需要再 .reverse() 了
    matches.value = matchRes.data || []
    players.value = playerRes.data || []

  } catch (e) {
    ElMessage.error('数据加载失败')
    matches.value = []
  }
  finally { loading.value = false }
}

// ✨ 新增：重置按钮逻辑
const resetFilters = () => {
  filters.dateRange = null
  filters.level = ''
  filters.playerId = null
  loadData() // 重置后立即重新加载所有数据
}

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
    loadData()
    goToScoreboard(res.data.matchId)
  } catch (e) {
    ElMessage.error('创建失败')
  }
}

const goToScoreboard = (matchId) => {
  router.push({ path: '/menu/scoreboard', query: { id: matchId } })
}

const handleExport = async (matchId) => {
  try {
    ElMessage.info('正在生成 Excel，请稍候...')
    const res = await axios.get(`/api/matches/${matchId}/export`, { responseType: 'blob' })
    const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const link = document.createElement('a')
    link.href = window.URL.createObjectURL(blob)
    link.download = `比赛记录_${matchId}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(link.href)
    ElMessage.success('导出成功！')
  } catch (e) {
    ElMessage.error('导出失败')
  }
}

const handleDelete = (matchId) => {
  ElMessageBox.confirm(
      '删除后无法恢复，确定要删除这场比赛及其所有回合记录吗？',
      '警告',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
  ).then(async () => {
    try {
      await axios.delete(`/api/matches/${matchId}`)
      ElMessage.success('删除成功')
      loadData()
    } catch (e) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 点击取消不做任何事
  })
}

onMounted(() => { loadData() })
</script>

<style scoped>
.header-row { display: flex; justify-content: space-between; align-items: center; }
/* 简单的 flex 布局让筛选条件对齐 */
.filter-row { /* 这里已经在 template 里写了 style，可以留空 */ }
</style>