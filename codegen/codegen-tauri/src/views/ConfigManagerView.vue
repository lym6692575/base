<template>
  <div class="config-manager-container">
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>配置列表</span>
              <div class="card-header-actions">
                <el-button type="primary" @click="handleAdd">新增配置</el-button>
                <el-button type="danger" @click="handleDelete" :disabled="!selectedConfig">删除选中</el-button>
                <el-button type="success" @click="handleGenerate" :disabled="!selectedConfig || isGenerating">
                  <el-icon v-if="isGenerating"><Loading /></el-icon>
                  生成代码
                </el-button>
              </div>
            </div>
          </template>
          
          <el-table
            :data="configList"
            v-loading="loading"
            style="width: 100%"
            @row-click="handleRowClick"
            :row-class-name="getRowClassName"
            highlight-current-row
          >
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column prop="entityName" label="实体名称"></el-table-column>
            <el-table-column prop="module" label="模块"></el-table-column>
            <el-table-column prop="createdAt" label="创建时间"></el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
                <el-button size="small" type="danger" @click="handleDeleteSingle(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 编辑表单 -->
    <el-drawer
      v-model="drawerVisible"
      title="配置详情"
      direction="rtl"
      :size="'60%'"
    >
      <ConfigForm
        v-model="editingConfig"
        @save="handleSave"
        @cancel="handleCancel"
      />
    </el-drawer>
    
    <!-- 生成日志 -->
    <el-drawer
      v-model="logDrawerVisible"
      title="生成日志"
      direction="rtl"
      :size="'50%'"
    >
      <el-scrollbar height="600px">
        <pre class="log-content">{{ generateLog }}</pre>
      </el-scrollbar>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { useConfigStore } from '../store/config'
import ConfigForm from '../components/ConfigForm.vue'

const configStore = useConfigStore()
const drawerVisible = ref(false)
const logDrawerVisible = ref(false)
const generateLog = ref('')

// 从store获取状态
const configList = computed(() => configStore.configList)
const selectedConfig = computed(() => configStore.selectedConfig)
const editingConfig = computed({
  get: () => configStore.editingConfig,
  set: (value) => { configStore.editingConfig = value }
})
const loading = computed(() => configStore.loading)
const isGenerating = computed(() => configStore.isGenerating)

// 初始化
onMounted(async () => {
  await configStore.init()
})

// 行点击事件，用于单选
const handleRowClick = (row) => {
  configStore.selectConfig(row)
}

// 获取行样式，用于高亮选中行
const getRowClassName = ({ row }) => {
  return row.id === selectedConfig.value?.id ? 'selected-row' : ''
}

// 新增配置
const handleAdd = () => {
  configStore.startEdit()
  drawerVisible.value = true
}

// 编辑配置
const handleEdit = async (row) => {
  try {
    // 调用API获取详细配置
    const config = await configStore.loadConfigById(row.id)
    if (config) {
      configStore.startEdit(config)
      drawerVisible.value = true
    } else {
      ElMessage.error('获取配置详情失败')
    }
  } catch (error) {
    ElMessage.error('获取配置详情失败')
    console.error('获取配置详情失败:', error)
  }
}

// 删除选中配置
const handleDelete = () => {
  if (selectedConfig.value) {
    handleDeleteSingle(selectedConfig.value)
  }
}

// 删除单个配置
const handleDeleteSingle = async (row) => {
  try {
    await configStore.deleteConfig(row.id)
    ElMessage.success(`配置 ${row.entityName} 已删除`)
  } catch (error) {
    ElMessage.error('删除配置失败')
  }
}

// 保存配置
const handleSave = async (formData) => {
  try {
    // 更新store中的editingConfig
    configStore.editingConfig = formData
    const success = await configStore.saveConfig()
    if (success) {
      ElMessage.success('配置保存成功')
      drawerVisible.value = false
    } else {
      ElMessage.error('配置保存失败')
    }
  } catch (error) {
    ElMessage.error('配置保存失败')
  }
}

// 取消编辑
const handleCancel = () => {
  configStore.cancelEdit()
  drawerVisible.value = false
}

// 生成代码
const handleGenerate = async () => {
  if (!selectedConfig.value) return
  
  try {
    generateLog.value = `开始生成代码...\n`
    logDrawerVisible.value = true
    
    const result = await configStore.generateCodeById(selectedConfig.value.id)
    
    if (result.status === 'success') {
      generateLog.value += `代码生成成功！\n`
      generateLog.value += `输出信息：${result.output}\n`
      ElMessage.success('代码生成成功')
    } else {
      generateLog.value += `代码生成失败：${result.message}\n`
      ElMessage.error('代码生成失败')
    }
  } catch (error) {
    generateLog.value += `代码生成失败：${error.message}\n`
    ElMessage.error('代码生成失败')
  }
}
</script>

<style scoped>
.config-manager-container {
  padding: 20px;
}

h1 {
  font-size: 2rem;
  margin-bottom: 30px;
  color: #333;
  text-align: center;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header-actions {
  display: flex;
  gap: 10px;
}

.selected-row {
  background-color: #f0f9ff !important;
}

.log-content {
  margin: 0;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  font-family: 'Courier New', Courier, monospace;
  font-size: 14px;
  line-height: 1.5;
  color: #333;
  white-space: pre-wrap;
  word-break: break-all;
}

.form-help-text {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style>
