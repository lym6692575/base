import { defineStore } from 'pinia'
// 导入配置管理的API服务
import {
  getAllConfigs as apiGetAllConfigs,
  getConfigById as apiGetConfigById,
  createConfig as apiCreateConfig,
  updateConfig as apiUpdateConfig,
  deleteConfig as apiDeleteConfig,
  generateCode as apiGenerateCode
} from '../services/configApi'

export const useConfigStore = defineStore('config', {
  state: () => ({
    // 配置列表
    configList: [],
    // 当前选中的配置
    selectedConfig: null,
    // 当前编辑的配置
    editingConfig: null,
    // 加载状态
    loading: false,
    // 生成状态
    generateStatus: 'idle' // idle, generating, success, error
  }),
  
  getters: {
    // 获取选中的配置ID
    selectedConfigId: (state) => state.selectedConfig?.id || null,
    // 判断是否正在加载
    isLoading: (state) => state.loading,
    // 判断是否正在生成代码
    isGenerating: (state) => state.generateStatus === 'generating'
  },
  
  actions: {
    // 初始化数据
    async init() {
      await this.loadAllConfigs()
    },
    
    // 加载所有配置
    async loadAllConfigs() {
      try {
        this.loading = true
        const result = await apiGetAllConfigs()
        if (result.status === 'success') {
          this.configList = result.data || []
        }
      } catch (error) {
        console.error('加载配置列表失败:', error)
      } finally {
        this.loading = false
      }
    },
    
    // 加载特定配置
    async loadConfigById(id) {
      try {
        this.loading = true
        const result = await apiGetConfigById(id)
        if (result.status === 'success') {
          return result.data
        }
        return null
      } catch (error) {
        console.error(`加载配置ID ${id} 失败:`, error)
        return null
      } finally {
        this.loading = false
      }
    },
    
    // 选择配置
    selectConfig(config) {
      this.selectedConfig = config
    },
    
    // 取消选择
    clearSelection() {
      this.selectedConfig = null
    },
    
    // 开始编辑配置
    startEdit(config = null) {
      if (config) {
        console.log(config)
        this.editingConfig = JSON.parse(JSON.stringify(config))
      } else {
        // 创建新配置的默认值
        this.editingConfig = {
          packageBase: '',
          module: '',
          entityName: '',
          idType: 'String',
          mapping: 'TABLE',
          tableName: '',
          subselect: '',
          outputDir: '',
          templatesDir: '',
          entityBaseClass: '',
          dtoBaseClass: '',
          mapperBaseClass: '',
          serviceBaseClass: '',
          serviceImplBaseClass: '',
          fields: ''
        }
      }
    },
    
    // 取消编辑
    cancelEdit() {
      this.editingConfig = null
    },
    
    // 保存配置
    async saveConfig() {
      try {
        this.loading = true
        let result
        
        if (this.editingConfig.id) {
          // 更新配置
          console.log(this.editingConfig)
          result = await apiUpdateConfig(this.editingConfig.id, this.editingConfig)
        } else {
          // 创建新配置
          result = await apiCreateConfig(this.editingConfig)
        }
        
        if (result.status === 'success') {
          // 重新加载配置列表
          await this.loadAllConfigs()
          // 取消编辑
          this.cancelEdit()
          return true
        }
        return false
      } catch (error) {
        console.error('保存配置失败:', error)
        return false
      } finally {
        this.loading = false
      }
    },
    
    // 删除配置
    async deleteConfig(id) {
      try {
        this.loading = true
        const result = await apiDeleteConfig(id)
        
        if (result.status === 'success') {
          // 重新加载配置列表
          await this.loadAllConfigs()
          // 如果删除的是选中的配置，清空选择
          if (this.selectedConfigId === id) {
            this.clearSelection()
          }
          return true
        }
        return false
      } catch (error) {
        console.error(`删除配置ID ${id} 失败:`, error)
        return false
      } finally {
        this.loading = false
      }
    },
    
    // 生成代码
    async generateCodeById(id) {
      try {
        this.generateStatus = 'generating'
        const result = await apiGenerateCode(id)
        
        if (result.status === 'success') {
          this.generateStatus = 'success'
          return result
        } else {
          this.generateStatus = 'error'
          throw new Error(result.message || '生成代码失败')
        }
      } catch (error) {
        console.error('生成代码失败:', error)
        this.generateStatus = 'error'
        throw error
      } finally {
        // 延迟重置生成状态
        setTimeout(() => {
          this.generateStatus = 'idle'
        }, 3000)
      }
    }
  }
})
