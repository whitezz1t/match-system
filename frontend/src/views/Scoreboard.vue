<template>
  <div v-loading="loading">
    <el-empty v-if="!currentMatch" description="请从赛程管理中选择一场比赛进入">
      <el-button type="primary" @click="$router.push('/menu/matches')">去选择比赛</el-button>
    </el-empty>

    <div v-else>
      <el-card shadow="hover" class="match-header">
        <div class="match-meta">
          <el-tag effect="dark">{{ currentMatch.level }}</el-tag>
          <span class="date">{{ new Date(currentMatch.matchDate).toLocaleString() }}</span>
          <el-tag type="success" v-if="currentMatch.status==='ONGOING'">🟢 比赛进行中</el-tag>
        </div>
      </el-card>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="10">
          <el-card class="player-card player-a" shadow="always">
            <div class="player-name">{{ currentMatch.playerAName }}</div>
            <div class="score">{{ currentMatch.finalScoreA }}</div>
            <el-button type="primary" size="large" class="score-btn" @click="addScore(currentMatch.playerAId)">
              +1 得分
            </el-button>
          </el-card>
        </el-col>

        <el-col :span="4" style="display: flex; align-items: center; justify-content: center;">
          <div class="vs-text">VS</div>
        </el-col>

        <el-col :span="10">
          <el-card class="player-card player-b" shadow="always">
            <div class="player-name">{{ currentMatch.playerBName }}</div>
            <div class="score">{{ currentMatch.finalScoreB }}</div>
            <el-button type="success" size="large" class="score-btn" @click="addScore(currentMatch.playerBId)">
              +1 得分
            </el-button>
          </el-card>
        </el-col>
      </el-row>

      <el-card style="margin-top: 20px;" shadow="never">
        <template #header>
          <div style="font-weight: bold;">📈 得分走势分析</div>
        </template>
        <div id="chartContainer" style="width: 100%; height: 350px;"></div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

const route = useRoute()
const currentMatch = ref(null)
const loading = ref(false)
let myChart = null
let pollingTimer = null

// 加载比赛详情
const loadMatch = async () => {
  const matchId = route.query.id
  if (!matchId) return

  try {
    const res = await axios.get('/api/matches')
    // 简单查找，实际应该写一个 getById 接口
    const match = res.data.find(m => m.matchId == matchId)
    if (match) {
      currentMatch.value = match
      updateChart(matchId)
    }
  } catch (e) {
    console.error(e)
  }
}

// 记分
const addScore = async (winnerId) => {
  try {
    const res = await axios.post(`/api/matches/${currentMatch.value.matchId}/score`, null, {
      params: { winnerId }
    })
    currentMatch.value = res.data // 更新本地数据
    ElMessage.success('记分成功')
    updateChart(currentMatch.value.matchId) // 刷新图表
  } catch (e) {
    ElMessage.error('记分失败')
  }
}

// 渲染/更新图表
const updateChart = async (matchId) => {
  try {
    const res = await axios.get(`/api/matches/${matchId}/rounds`)
    const rounds = res.data

    // 如果没有 DOM 或者没有数据，就不画
    if (!document.getElementById('chartContainer')) return

    if (!myChart) {
      myChart = echarts.init(document.getElementById('chartContainer'))
    }

    const xData = rounds.map(r => `R${r.roundNumber}`)
    const yDataA = rounds.map(r => r.scoreA)
    const yDataB = rounds.map(r => r.scoreB)

    const option = {
      tooltip: { trigger: 'axis' },
      legend: { data: [currentMatch.value.playerAName, currentMatch.value.playerBName] },
      xAxis: { type: 'category', data: xData },
      yAxis: { type: 'value', minInterval: 1 },
      series: [
        { name: currentMatch.value.playerAName, type: 'line', data: yDataA, smooth: true, color: '#409EFF' },
        { name: currentMatch.value.playerBName, type: 'line', data: yDataB, smooth: true, color: '#67C23A' }
      ]
    }
    myChart.setOption(option)
  } catch (e) { console.error('图表加载失败', e) }
}

onMounted(() => {
  loading.value = true
  loadMatch().finally(() => {
    loading.value = false
    // 轮询：每3秒刷新一次比分，防止多端不同步
    pollingTimer = setInterval(loadMatch, 3000)
  })
})

onUnmounted(() => {
  if (pollingTimer) clearInterval(pollingTimer)
  if (myChart) myChart.dispose()
})
</script>

<style scoped>
.match-meta { text-align: center; font-size: 14px; color: #666; display: flex; justify-content: center; gap: 15px; align-items: center; }
.player-card { text-align: center; height: 250px; display: flex; flex-direction: column; justify-content: center; }
.player-a { border-top: 5px solid #409EFF; }
.player-b { border-top: 5px solid #67C23A; }
.player-name { font-size: 24px; font-weight: bold; margin-bottom: 20px; }
.score { font-size: 80px; font-weight: bold; line-height: 1; margin-bottom: 20px; color: #303133; }
.vs-text { font-size: 50px; font-weight: bold; color: #E6E8EB; font-style: italic; }
.score-btn { width: 80%; margin: 0 auto; }
</style>