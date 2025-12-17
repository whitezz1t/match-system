<template>
  <div class="page-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>🚀 注册新裁判账号</span>
        </div>
      </template>

      <el-form label-position="top" size="large">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" />
        </el-form-item>

        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password />
        </el-form-item>

        <el-form-item label="确认密码">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" :prefix-icon="Lock" show-password />
        </el-form-item>

        <el-button type="success" style="width: 100%; margin-top: 10px;" @click="handleRegister" :loading="loading" round>
          立即注册
        </el-button>

        <div style="text-align: center; margin-top: 15px;">
          <el-link type="primary" @click="$router.push('/login')">已有账号？返回登录</el-link>
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
const form = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const handleRegister = async () => {
  if(!form.username || !form.password) {
    ElMessage.warning('请填写完整信息')
    return
  }
  if(form.password !== form.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }

  loading.value = true
  try {
    // 发送请求给后端
    const res = await axios.post('/api/register', null, {
      params: {
        username: form.username,
        password: form.password
      }
    })

    if (res.data === 'success') {
      ElMessage.success('注册成功！正在跳转登录页...')
      setTimeout(() => router.push('/login'), 1500)
    } else if (res.data === 'exist') {
      ElMessage.warning('该用户名已被注册')
    } else {
      ElMessage.error('注册失败')
    }
  } catch (error) {
    ElMessage.error('无法连接到服务器')
    console.error(error)
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
.box-card { width: 400px; border-radius: 12px; }
.card-header { text-align: center; font-weight: bold; font-size: 20px; }
</style>