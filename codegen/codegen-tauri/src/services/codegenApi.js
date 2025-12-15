// 代码生成工具的API服务层
// 目前实现基本的数据处理和本地存储功能，后续可以扩展为与后端API交互

// 模拟生成代码的API调用
export const generateCode = async (config) => {
  try {
    // 模拟API请求延迟
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // 模拟生成结果
    const result = {
      success: true,
      message: '代码生成成功',
      outputFiles: Math.floor(Math.random() * 10) + 5, // 随机生成5-15个文件
      details: {
        packageBase: config.packageBase,
        module: config.module,
        entityName: config.entityName,
        outputDir: config.outputDir,
        generateTime: new Date().toLocaleString()
      }
    }
    
    // 保存到本地存储
    saveGenerateHistory(result)
    
    return result
  } catch (error) {
    console.error('代码生成失败:', error)
    throw new Error('代码生成失败: ' + error.message)
  }
}

// 保存生成历史
export const saveGenerateHistory = (record) => {
  try {
    const history = getGenerateHistory()
    const newRecord = {
      id: Date.now(),
      ...record
    }
    history.unshift(newRecord)
    localStorage.setItem('codegenHistory', JSON.stringify(history))
    return true
  } catch (error) {
    console.error('保存历史记录失败:', error)
    return false
  }
}

// 获取生成历史
export const getGenerateHistory = () => {
  try {
    const history = localStorage.getItem('codegenHistory')
    return history ? JSON.parse(history) : []
  } catch (error) {
    console.error('获取历史记录失败:', error)
    return []
  }
}

// 删除生成历史
export const deleteGenerateHistory = (id) => {
  try {
    const history = getGenerateHistory()
    const newHistory = history.filter(item => item.id !== id)
    localStorage.setItem('codegenHistory', JSON.stringify(newHistory))
    return true
  } catch (error) {
    console.error('删除历史记录失败:', error)
    return false
  }
}

// 清空生成历史
export const clearGenerateHistory = () => {
  try {
    localStorage.removeItem('codegenHistory')
    return true
  } catch (error) {
    console.error('清空历史记录失败:', error)
    return false
  }
}

// 获取代码模板列表
export const getTemplates = () => {
  return [
    {
      id: 1,
      name: 'Spring Boot + MyBatis',
      description: '生成Spring Boot + MyBatis的代码结构',
      enabled: true,
      defaultConfig: {
        packageBase: 'com.dqjq.myapp',
        module: 'demo',
        entityName: 'Demo',
        idType: 'Long',
        mapping: 'SUBSELECT',
        tableName: '',
        subselect: 'SELECT 1 AS ID, \'DEMO\' AS NAME FROM DUAL',
        outputDir: 'src/main/java',
        entityBaseClass: 'com.dqjq.base.lee.entity.BaseEntity',
        dtoBaseClass: 'com.dqjq.base.lee.dto.BaseDto',
        serviceBaseClass: 'com.example.demo.common.lee.Basic.BasicPlusService',
        serviceImplBaseClass: 'com.example.demo.common.lee.Basic.impl.BasicPlusServiceImpl'
      }
    },
    {
      id: 2,
      name: 'Spring Boot + JPA',
      description: '生成Spring Boot + JPA的代码结构',
      enabled: true,
      defaultConfig: {
        packageBase: 'com.dqjq.myapp',
        module: 'demo',
        entityName: 'Demo',
        idType: 'Long',
        mapping: 'TABLE',
        tableName: 'demo_table',
        subselect: '',
        outputDir: 'src/main/java',
        entityBaseClass: 'com.dqjq.base.lee.entity.BaseEntity',
        dtoBaseClass: 'com.dqjq.base.lee.dto.BaseDto',
        serviceBaseClass: 'com.example.demo.common.lee.Basic.BasicPlusService',
        serviceImplBaseClass: 'com.example.demo.common.lee.Basic.impl.BasicPlusServiceImpl'
      }
    },
    {
      id: 3,
      name: 'Vue 3 + Element Plus',
      description: '生成Vue 3 + Element Plus的前端代码',
      enabled: true,
      defaultConfig: {
        packageBase: 'com.dqjq.myapp',
        module: 'frontend',
        entityName: 'Demo',
        idType: 'Number',
        mapping: 'JSON',
        tableName: '',
        subselect: '',
        outputDir: 'src/components',
        entityBaseClass: '',
        dtoBaseClass: '',
        serviceBaseClass: '',
        serviceImplBaseClass: ''
      }
    }
  ]
}

// 导出配置为JSON格式
export const exportConfigToJson = (config) => {
  try {
    const jsonConfig = JSON.stringify(config, null, 2)
    const blob = new Blob([jsonConfig], { type: 'application/json' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `codegen-${config.entityName}-${Date.now()}.json`
    a.click()
    URL.revokeObjectURL(url)
    return true
  } catch (error) {
    console.error('导出配置失败:', error)
    return false
  }
}

// 导入JSON配置
export const importConfigFromJson = (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = (e) => {
      try {
        const config = JSON.parse(e.target.result)
        resolve(config)
      } catch (error) {
        reject(new Error('JSON格式错误'))
      }
    }
    reader.onerror = () => {
      reject(new Error('文件读取失败'))
    }
    reader.readAsText(file)
  })
}