<template>
  <div v-loading="loading">
    <el-empty v-if="!currentMatch" description="请从赛程管理中选择一场比赛进入">
      <el-button type="primary" @click="$router.push('/menu/matches')">去选择比赛</el-button>
    </el-empty>

    <div v-else>
      <el-card shadow="hover" class="match-header">
        <div class="match-meta">
          <div class="meta-left">
            <el-tag effect="dark" size="large">{{ currentMatch.level }}</el-tag>
            <span class="date">{{ new Date(currentMatch.matchDate).toLocaleString() }}</span>
          </div>
          <div class="meta-center" v-if="currentMatch.status === 'ONGOING'">
            <el-tag type="warning" effect="plain" round class="round-tag">
              ⚡ 第 {{ currentRoundNumber }} 回合
            </el-tag>
          </div>
          <div class="meta-right">
            <el-tag type="success" v-if="currentMatch.status==='ONGOING'">🟢 比赛进行中</el-tag>
            <el-tag type="danger" v-else-if="currentMatch.status==='FINISHED'">🔴 比赛已结束</el-tag>
          </div>
        </div>
      </el-card>

      <el-row :gutter="20" style="margin-top: 20px;">

        <el-col :span="10">
          <el-card class="player-card player-a" shadow="always" :class="{ 'serving-card': isPlayerAServing && currentMatch.status === 'ONGOING' }">
            <div v-if="currentMatch.status==='FINISHED' && currentMatch.finalScoreA > currentMatch.finalScoreB" class="winner-trophy">🏆 胜者</div>
            <div v-if="isPlayerAServing && currentMatch.status === 'ONGOING'" class="server-badge">
              <span class="pingpong-icon">🏓</span> 当前发球
            </div>
            <div class="player-name">{{ currentMatch.playerAName }}</div>
            <div class="score">{{ currentMatch.finalScoreA }}</div>
            <el-button type="primary" size="large" class="score-btn" @click="handleAddScore(currentMatch.playerAId)" :disabled="currentMatch.status === 'FINISHED'">+1 得分</el-button>
          </el-card>
        </el-col>

        <el-col :span="4" style="display: flex; flex-direction: column; align-items: center; justify-content: center;">
          <div class="vs-text">VS</div>
          <div class="camera-box">
            <video ref="liveVideo" autoplay muted playsinline style="width: 100%; height: 100%; object-fit: cover;"></video>
            <div class="rec-dot" v-if="isRecording"></div>
            <div class="camera-tip">🎥 鹰眼系统</div>
          </div>
        </el-col>

        <el-col :span="10">
          <el-card class="player-card player-b" shadow="always" :class="{ 'serving-card': !isPlayerAServing && currentMatch.status === 'ONGOING' }">
            <div v-if="currentMatch.status==='FINISHED' && currentMatch.finalScoreB > currentMatch.finalScoreA" class="winner-trophy">🏆 胜者</div>
            <div v-if="!isPlayerAServing && currentMatch.status === 'ONGOING'" class="server-badge">
              <span class="pingpong-icon">🏓</span> 当前发球
            </div>
            <div class="player-name">{{ currentMatch.playerBName }}</div>
            <div class="score">{{ currentMatch.finalScoreB }}</div>
            <el-button type="success" size="large" class="score-btn" @click="handleAddScore(currentMatch.playerBId)" :disabled="currentMatch.status === 'FINISHED'">+1 得分</el-button>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">

        <el-col :span="14">
          <el-card shadow="never" style="margin-bottom: 20px;">
            <template #header><div style="font-weight: bold;">📈 比分走势</div></template>
            <div id="chartContainer" style="width: 100%; height: 250px;"></div>
          </el-card>

          <el-card shadow="never" class="history-card">
            <template #header><div style="font-weight: bold;">🎬 精彩回放 (点击播放)</div></template>
            <el-table :data="roundsHistory" height="250" style="width: 100%" stripe size="small">
              <el-table-column prop="roundNumber" label="回合" width="60" align="center" />
              <el-table-column label="比分" width="80" align="center">
                <template #default="scope">{{ scope.row.scoreA }} : {{ scope.row.scoreB }}</template>
              </el-table-column>
              <el-table-column label="得分者" align="center">
                <template #default="scope">
                  <el-tag size="small" :type="scope.row.winnerId === currentMatch.playerAId ? 'primary' : 'success'">
                    {{ scope.row.winnerId === currentMatch.playerAId ? currentMatch.playerAName : currentMatch.playerBName }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="回放" align="center">
                <template #default="scope">
                  <el-button v-if="scope.row.videoFilePath" type="danger" link size="small" @click="playVideo(scope.row.videoFilePath)">
                    ▶️ 播放
                  </el-button>
                  <span v-else style="color: #ccc; font-size: 12px;">处理中/无</span>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>

        <el-col :span="10">
          <el-card shadow="never" class="stats-card">
            <template #header><div style="font-weight: bold;">📊 战术分析面板</div></template>

            <div class="stat-box">
              <div class="stat-title">🔥 最大连胜纪录</div>
              <div id="streakChart" style="width: 100%; height: 120px;"></div>
            </div>

            <div class="stat-box">
              <div class="stat-title">🎯 发球得分率</div>
              <div class="progress-item">
                <span>{{ currentMatch.playerAName }}</span>
                <el-progress :percentage="stats.serveWinRateA" :color="'#409EFF'" />
              </div>
              <div class="progress-item" style="margin-top: 10px;">
                <span>{{ currentMatch.playerBName }}</span>
                <el-progress :percentage="stats.serveWinRateB" :color="'#67C23A'" />
              </div>
            </div>

            <div class="stat-box">
              <div class="stat-title">⏱️ 比赛节奏</div>
              <div style="text-align: center; font-size: 24px; font-weight: bold; color: #606266;">
                {{ stats.avgDurationSeconds }} <span style="font-size: 14px; font-weight: normal;">秒 / 回合</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-dialog v-model="videoDialogVisible" title="关键帧回放" width="600px" destroy-on-close>
        <video :src="currentVideoUrl" controls autoplay style="width: 100%;"></video>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, reactive } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

const route = useRoute()
const currentMatch = ref(null)
const roundsHistory = ref([])
const loading = ref(false)
const videoDialogVisible = ref(false)
const currentVideoUrl = ref('')

// === 状态锁：防止连点导致录制逻辑错乱 ===
const isProcessing = ref(false)

const stats = reactive({
  serveWinRateA: 0,
  serveWinRateB: 0,
  maxStreakA: 0,
  maxStreakB: 0,
  avgDurationSeconds: 0
})

// === 📷 录制核心变量 ===
const liveVideo = ref(null)
const isRecording = ref(false) // 界面红点控制
let mediaRecorder = null
let recordedChunks = [] // 临时存储当前这一回合的数据
let stream = null
let currentRoundForSave = 0 // 暂存当前要保存的回合数

// 图表实例
let myChart = null
let streakChart = null
let pollingTimer = null

// 计算属性
const currentRoundNumber = computed(() => {
  if (!currentMatch.value) return 0
  return currentMatch.value.finalScoreA + currentMatch.value.finalScoreB + 1
})

const isPlayerAServing = computed(() => {
  if (!currentMatch.value) return false
  const scoreA = currentMatch.value.finalScoreA
  const scoreB = currentMatch.value.finalScoreB
  const total = scoreA + scoreB
  if (scoreA >= 10 && scoreB >= 10) return total % 2 === 0
  return Math.floor(total / 2) % 2 === 0
})

// === 1. 初始化逻辑 (修改了Recorder配置) ===
const initCamera = async () => {
  try {
    // 获取流
    stream = await navigator.mediaDevices.getUserMedia({
      video: { width: 1280, height: 720 },
      audio: true // 建议开启音频，否则有些浏览器录制会报错
    })

    // 预览画面 (必须静音，否则会啸叫)
    if (liveVideo.value) {
      liveVideo.value.srcObject = stream
      liveVideo.value.muted = true
    }

    startNewRecording() // 页面加载完，立刻开始录制第一球

  } catch (e) {
    console.warn("摄像头启动失败或用户拒绝", e)
    ElMessage.warning("无法启动摄像头，视频录制功能不可用")
  }
}

// === 核心：开启一段新的录制 ===
const startNewRecording = () => {
  if (!stream) return

  // 清空旧数据
  recordedChunks = []

  // 创建新实例 (每次新建是为了保证头文件完整)
  // 优先使用 vp8 编码，兼容性最好
  const mimeType = MediaRecorder.isTypeSupported('video/webm;codecs=vp8')
      ? 'video/webm;codecs=vp8'
      : 'video/webm'

  mediaRecorder = new MediaRecorder(stream, { mimeType })

  // 1. 收集数据
  mediaRecorder.ondataavailable = (event) => {
    if (event.data && event.data.size > 0) {
      recordedChunks.push(event.data)
    }
  }

  // 2. 停止时的回调 (保存视频 + 重启录制)
  mediaRecorder.onstop = () => {
    saveVideoToServer() // 上传刚才那一段

    // 只要比赛没结束，就立刻开启下一段
    if (currentMatch.value && currentMatch.value.status === 'ONGOING') {
      // 稍微延迟 100ms 确保资源释放，避免浏览器卡死
      setTimeout(() => {
        startNewRecording()
      }, 100)
    } else {
      isRecording.value = false
    }
  }

  // 3. 启动
  mediaRecorder.start()
  isRecording.value = true
}

// === 2. 记分逻辑 (修改：加入状态锁和停止录制指令) ===
const handleAddScore = async (winnerId) => {
  // 如果正在处理上一球的视频保存，禁止点击
  if (isProcessing.value) {
    ElMessage.warning('视频处理中，请稍候...')
    return
  }

  try {
    const matchId = currentMatch.value.matchId
    // 暂存当前回合数，供 onstop 里的 saveVideoToServer 使用
    currentRoundForSave = currentRoundNumber.value

    // A. 提交分数
    const res = await axios.post(`/api/matches/${matchId}/score`, null, { params: { winnerId } })
    currentMatch.value = res.data
    ElMessage.success('得分已记录')

    // B. 触发视频截断逻辑
    if (mediaRecorder && mediaRecorder.state === 'recording') {
      isProcessing.value = true // 锁定按钮

      // 延迟 2 秒后停止录制 (捕捉庆祝画面)
      setTimeout(() => {
        if (mediaRecorder.state === 'recording') {
          mediaRecorder.stop() // 这会触发 onstop -> saveVideoToServer -> startNewRecording
        }

        // 3秒后解锁按钮 (给重启录制留点时间)
        setTimeout(() => {
          isProcessing.value = false
        }, 1000)
      }, 2000)
    }

    // C. 刷新数据
    refreshAllData(matchId)

  } catch (e) {
    ElMessage.error(e.response?.data?.message || '记分失败')
    isProcessing.value = false
  }
}

// === 3. 上传视频 (被 onstop 自动调用) ===
const saveVideoToServer = () => {
  const matchId = currentMatch.value.matchId
  const roundNum = currentRoundForSave

  if (recordedChunks.length === 0) return

  const blob = new Blob(recordedChunks, { type: 'video/webm' })

  // 可以在这里打印一下大小，确保录到了东西
  console.log(`回合 ${roundNum} 视频生成，大小: ${blob.size}`)

  const formData = new FormData()
  formData.append('file', blob, `match_${matchId}_round_${roundNum}.webm`)

  axios.post(`/api/matches/${matchId}/rounds/${roundNum}/video`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }).then(() => {
    // 上传成功后，刷新列表显示播放按钮
    loadRounds(matchId)
  }).catch(e => console.error("视频上传失败", e))
}

// === 下面的数据刷新、图表渲染逻辑保持不变 ===
const refreshAllData = (matchId) => {
  loadRounds(matchId)
  loadStats(matchId)
}

const loadMatch = async () => {
  const matchId = route.query.id
  if (!matchId) return
  try {
    const res = await axios.get('/api/matches')
    const match = res.data.find(m => m.matchId == matchId)
    if (match) {
      currentMatch.value = match
      refreshAllData(matchId)
    }
  } catch(e) { console.error(e) }
}

const loadRounds = async (matchId) => {
  const res = await axios.get(`/api/matches/${matchId}/rounds`)
  roundsHistory.value = [...res.data].reverse()
  updateChart(res.data)
}

const loadStats = async (matchId) => {
  try {
    const res = await axios.get(`/api/matches/${matchId}/stats`)
    const data = res.data
    stats.serveWinRateA = parseFloat((data.serveWinRateA * 100).toFixed(1))
    stats.serveWinRateB = parseFloat((data.serveWinRateB * 100).toFixed(1))
    stats.maxStreakA = data.maxStreakA
    stats.maxStreakB = data.maxStreakB
    stats.avgDurationSeconds = data.avgDurationSeconds ? data.avgDurationSeconds.toFixed(1) : '0.0'
    renderStreakChart()
  } catch (e) { console.error("统计加载失败", e) }
}

const playVideo = (path) => {
  currentVideoUrl.value = `http://localhost:8080${path}`
  videoDialogVisible.value = true
}

const updateChart = (rounds) => {
  if (!document.getElementById('chartContainer')) return
  if (!myChart) myChart = echarts.init(document.getElementById('chartContainer'))
  const data = rounds
  myChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { top: 30, bottom: 30, left: 40, right: 40 },
    xAxis: { type: 'category', data: data.map(r => `R${r.roundNumber}`) },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      { name: currentMatch.value.playerAName, type: 'line', data: data.map(r => r.scoreA), color: '#409EFF', showSymbol: false },
      { name: currentMatch.value.playerBName, type: 'line', data: data.map(r => r.scoreB), color: '#67C23A', showSymbol: false }
    ]
  })
}

const renderStreakChart = () => {
  if (!document.getElementById('streakChart')) return
  if (!streakChart) streakChart = echarts.init(document.getElementById('streakChart'))
  streakChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { top: 10, bottom: 10, left: 10, right: 30, containLabel: true },
    xAxis: { type: 'value', show: false },
    yAxis: { type: 'category', data: [currentMatch.value.playerAName, currentMatch.value.playerBName] },
    series: [
      {
        type: 'bar',
        data: [
          { value: stats.maxStreakA, itemStyle: { color: '#409EFF' } },
          { value: stats.maxStreakB, itemStyle: { color: '#67C23A' } }
        ],
        label: { show: true, position: 'right' },
        barWidth: '60%'
      }
    ]
  })
}

onMounted(() => {
  loading.value = true
  loadMatch().finally(() => {
    loading.value = false
    initCamera() // 开启摄像头并自动开始录制第一段
    pollingTimer = setInterval(() => loadMatch(), 3000)
  })
  window.addEventListener('resize', () => {
    myChart && myChart.resize()
    streakChart && streakChart.resize()
  })
})

onUnmounted(() => {
  if (stream) stream.getTracks().forEach(t => t.stop())
  if (pollingTimer) clearInterval(pollingTimer)
  if (myChart) myChart.dispose()
  if (streakChart) streakChart.dispose()
})
</script>

<style scoped>
/* 样式整合 */
.match-header { margin-bottom: 20px; }
.match-meta { display: flex; justify-content: space-between; align-items: center; }
.meta-center { flex: 1; text-align: center; }
.round-tag { font-size: 16px; padding: 18px 25px; font-weight: bold; border: 2px solid #E6A23C; }

.player-card { text-align: center; height: 280px; display: flex; flex-direction: column; justify-content: center; position: relative; transition: all 0.3s; }
.player-a { border-top: 5px solid #409EFF; }
.player-b { border-top: 5px solid #67C23A; }
.serving-card { box-shadow: 0 0 15px rgba(0, 0, 0, 0.2); transform: translateY(-2px); }
.player-a.serving-card { background-color: #ecf5ff; }
.player-b.serving-card { background-color: #f0f9eb; }

.player-name { font-size: 24px; font-weight: bold; margin-bottom: 10px; margin-top: 20px; }
.score { font-size: 80px; font-weight: bold; line-height: 1; margin-bottom: 20px; color: #303133; }
.vs-text { font-size: 40px; font-weight: bold; color: #E6E8EB; font-style: italic; margin-bottom: 10px; }
.score-btn { width: 80%; margin: 0 auto; }
.server-badge { position: absolute; top: 10px; right: 10px; background-color: #303133; color: #fff; padding: 4px 10px; border-radius: 12px; font-size: 12px; }
.winner-trophy { position: absolute; top: 10px; left: 0; right: 0; font-size: 22px; color: #E6A23C; font-weight: bold; animation: bounce 1s infinite; }

/* 摄像头小窗口 */
.camera-box { width: 120px; height: 90px; background: #000; border-radius: 8px; overflow: hidden; position: relative; box-shadow: 0 4px 12px rgba(0,0,0,0.3); }
.camera-tip { position: absolute; bottom: 0; left: 0; width: 100%; background: rgba(0,0,0,0.6); color: #fff; font-size: 10px; text-align: center; padding: 2px 0; }
.rec-dot { position: absolute; top: 5px; right: 5px; width: 8px; height: 8px; background: red; border-radius: 50%; animation: blink 1s infinite; }

/* 统计卡片样式 */
.stats-card { height: 100%; display: flex; flex-direction: column; }
.stat-box { margin-bottom: 25px; }
.stat-title { font-size: 14px; color: #909399; margin-bottom: 10px; font-weight: bold; }
.progress-item { display: flex; justify-content: space-between; align-items: center; font-size: 14px; }
.progress-item .el-progress { width: 70%; }

@keyframes bounce { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-5px); } }
@keyframes blink { 50% { opacity: 0; } }
</style>