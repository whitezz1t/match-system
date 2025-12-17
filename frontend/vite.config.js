import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': { // 意思是：凡是 /api 开头的请求
        target: 'http://localhost:8080', // 都转发给后端 8080 端口
        changeOrigin: true,
        // 下面这行千万不要随便加，除非你确定后端接口里不带 /api
        // rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})