<template>
  <div class="page-container">
    <el-card class="box-card" shadow="always">
      <div class="login-title">⚖️ 比赛系统登录</div>

      <el-form size="large">
        <el-form-item>
          <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" />
        </el-form-item>

        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
        </el-form-item>

        <el-button type="primary" style="width: 100%; font-weight: bold;" @click="handleLogin" :loading="loading" round>
          登 录
        </el-button>

        <div style="margin-top: 20px; text-align: center; border-top: 1px solid #eee; padding-top: 15px;">
          <span style="color: #909399; font-size: 14px;">还没有账号？</span>
          <el-button type="primary" link @click="$router.push('/register')">点击注册</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

const handleLogin = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }

  loading.value = true
  try {
    const res = await axios.post('/api/login', null, {
      params: { username: form.username, password: form.password }
    })

    if (res.data === 'success') {
      // 🟢 核心：发通行证
      sessionStorage.setItem('match_user', form.username)
      ElMessage.success('登录成功')
      router.push('/menu') // 跳去菜单页
    } else {
      ElMessage.error('账号或密码错误')
    }
  } catch (error) {
    ElMessage.error('无法连接到服务器')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.box-card { width: 380px; border-radius: 12px; }
.login-title { text-align: center; margin-bottom: 25px; color: #409EFF; font-size: 26px; font-weight: bold; }
</style>