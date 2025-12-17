import { createApp } from 'vue'
import App from './App.vue'
import router from './router' // 引入路由配置
import ElementPlus from 'element-plus' // 引入 UI 组件库
import 'element-plus/dist/index.css' // 引入组件样式
import * as ElementPlusIconsVue from '@element-plus/icons-vue' // 引入图标

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.use(router) // 告诉 Vue 使用路由
app.use(ElementPlus) // 告诉 Vue 使用 Element Plus
app.mount('#app')