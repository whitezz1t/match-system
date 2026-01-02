<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="aside-menu">
      <div class="logo">
        ⚖️ 比赛管理系统
      </div>
      <el-menu
          active-text-color="#409EFF"
          background-color="#304156"
          text-color="#bfcbd9"
          :default-active="$route.path"
          router
          class="el-menu-vertical"
      >
        <el-menu-item index="/menu/scoreboard">
          <el-icon><Trophy /></el-icon>
          <span>实时计分板</span>
        </el-menu-item>

        <el-menu-item index="/menu/matches">
          <el-icon><Calendar /></el-icon>
          <span>赛程管理</span>
        </el-menu-item>

        <el-menu-item index="/menu/players">
          <el-icon><UserFilled /></el-icon>
          <span>选手管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header-bar">
        <div class="breadcrumb">
          当前位置：{{ $route.meta.title }}
        </div>
        <div class="user-info">
          <span>欢迎，{{ user }}</span>
          <el-button type="danger" link size="small" @click="handleLogout" style="margin-left: 15px;">
            <el-icon style="margin-right: 5px"><SwitchButton /></el-icon> 退出
          </el-button>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
// 引入需要的图标
import { Trophy, UserFilled, Calendar, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()
// route 用于获取当前 URL 信息，比如高亮哪个菜单
const route = useRoute()
const user = ref(sessionStorage.getItem('match_user'))

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗?', '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
  }).then(() => {
    sessionStorage.removeItem('match_user')
    router.push('/login')
    ElMessage.success('已退出')
  }).catch(() => {})
}
</script>

<style scoped>
/* 布局容器占满全屏 */
.layout-container { height: 100vh; }

/* 侧边栏样式 */
.aside-menu { background-color: #304156; overflow-x: hidden; box-shadow: 2px 0 6px rgba(0,21,41,.35); }
.logo { height: 60px; line-height: 60px; text-align: center; color: white; font-weight: bold; font-size: 18px; background-color: #2b2f3a; }
.el-menu-vertical { border-right: none; }

/* 顶栏样式 */
.header-bar { background-color: #fff; border-bottom: 1px solid #e6e6e6; display: flex; justify-content: space-between; align-items: center; padding: 0 20px; box-shadow: 0 1px 4px rgba(0,21,41,.08); }
.user-info { font-size: 14px; color: #606266; display: flex; align-items: center; }
.breadcrumb { font-weight: bold; color: #303133; }

/* 主体内容区样式 */
.main-content { background-color: #f0f2f5; padding: 20px; }
</style>