import { defineStore } from 'pinia'
// 导入API服务层
import { 
  generateCode as apiGenerateCode,
  getGenerateHistory as apiGetGenerateHistory,
  deleteGenerateHistory as apiDeleteGenerateHistory,
  clearGenerateHistory as apiClearGenerateHistory,
  getTemplates as apiGetTemplates
} from '../services/codegenApi'

export const useCodegenStore = defineStore('codegen', {
  state: () => ({
    // 生成历史记录
    generateHistory: [],
    // 代码模板列表
    templates: [],
    // 当前正在生成的任务
    currentTask: null,
    // 生成状态
    generateStatus: 'idle' // idle, generating, success, error
  }),
  
  getters: {
    // 获取成功的生成记录数量
    successCount: (state) => {
      return state.generateHistory.filter(item => item.status === 'success').length
    },
    // 获取失败的生成记录数量
    errorCount: (state) => {
      return state.generateHistory.filter(item => item.status === 'danger').length
    },
    // 获取启用的模板列表
    enabledTemplates: (state) => {
      return state.templates.filter(item => item.enabled)
    }
  },
  
  actions: {
    // 初始化数据
    init() {
      this.loadTemplates()
      this.loadGenerateHistory()
    },
    
    // 加载模板列表
    loadTemplates() {
      this.templates = apiGetTemplates()
    },
    
    // 加载生成历史
    loadGenerateHistory() {
      this.generateHistory = apiGetGenerateHistory()
    },
    
    // 生成代码
    async generateCode(config) {
      try {
        this.generateStatus = 'generating'
        this.currentTask = {
          id: Date.now(),
          ...config
        }
        
        // 调用API服务生成代码
        const result = await apiGenerateCode(config)
        
        // 生成成功
        this.generateStatus = 'success'
        
        // 更新历史记录
        this.loadGenerateHistory()
        
        // 重置当前任务
        this.currentTask = null
        
        return result
      } catch (error) {
        this.generateStatus = 'error'
        this.currentTask = null
        
        // 更新历史记录
        this.loadGenerateHistory()
        
        throw error
      } finally {
        // 延迟重置状态
        setTimeout(() => {
          this.generateStatus = 'idle'
        }, 3000)
      }
    },
    
    // 添加新模板
    addTemplate(template) {
      const newTemplate = {
        id: this.templates.length + 1,
        enabled: true,
        ...template
      }
      this.templates.push(newTemplate)
    },
    
    // 更新模板状态
    updateTemplateStatus(id, enabled) {
      const template = this.templates.find(item => item.id === id)
      if (template) {
        template.enabled = enabled
      }
    },
    
    // 删除生成历史
    deleteHistory(id) {
      if (apiDeleteGenerateHistory(id)) {
        this.loadGenerateHistory()
      }
    },
    
    // 清空生成历史
    clearHistory() {
      if (apiClearGenerateHistory()) {
        this.loadGenerateHistory()
      }
    }
  }
})