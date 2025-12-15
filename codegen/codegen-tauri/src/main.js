import { createApp } from 'vue'
import './style.css'
import App from './App.vue'

// 导入Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// 导入Vue Router
import router from './router'

// 导入Pinia
import { pinia } from './store'

const app = createApp(App)

// 使用插件
app.use(ElementPlus)
app.use(router)
app.use(pinia)

app.mount('#app')
