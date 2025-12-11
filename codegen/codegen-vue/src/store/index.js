import { createPinia } from 'pinia'

// 导入所有store模块
import { useCodegenStore } from './codegen'

// 创建pinia实例
const pinia = createPinia()

export {
  pinia,
  useCodegenStore
}