<template>
  <div class="scheme-manager-container">
    <el-row :gutter="20">
      <!-- Left: Scheme List -->
      <el-col :span="6">
        <el-card class="box-card" style="height: calc(100vh - 100px); overflow-y: auto;">
          <template #header>
            <div class="card-header">
              <span>方案列表</span>
              <div>
                  <el-button type="primary" size="small" @click="handleCreateScheme">新建</el-button>
                  <el-button type="text" @click="refreshList">刷新</el-button>
              </div>
            </div>
          </template>
          
          <el-menu
            :default-active="activeSchemeId"
            class="scheme-menu"
            @select="handleSelectScheme"
          >
            <!-- 分组展示 -->
            <template v-for="(schemes, group) in groupedSchemes" :key="group">
                <el-sub-menu :index="group">
                    <template #title>
                        <el-icon><Folder /></el-icon>
                        <span>{{ group }}</span>
                    </template>
                    <el-menu-item
                        v-for="scheme in schemes"
                        :key="scheme.id"
                        :index="String(scheme.id)"
                    >
                        <el-icon><Cpu /></el-icon>
                        <span>{{ scheme.name }}</span>
                    </el-menu-item>
                </el-sub-menu>
            </template>
          </el-menu>
        </el-card>
      </el-col>
      
      <!-- Right: Scheme Details -->
      <el-col :span="18">
        <el-card class="box-card" style="height: calc(100vh - 100px); overflow-y: auto;">
          <template #header>
            <div class="card-header">
              <span>{{ currentScheme ? currentScheme.name : '请选择方案' }}</span>
              <div v-if="currentScheme">
                 <el-button @click="handleEditInfo">编辑信息</el-button>
                 <el-button type="primary" plain @click="handleEditVariables">全局变量配置</el-button>
                 <el-button 
                  type="primary" 
                  @click="handleAddStep"
                >添加步骤</el-button>
                 <el-button 
                  type="danger" 
                  @click="handleDeleteScheme"
                >删除方案</el-button>
              </div>
            </div>
          </template>
          
          <div v-if="currentScheme">
              <div class="scheme-info">
                  <el-descriptions :column="2" border>
                      <el-descriptions-item label="名称">{{ currentScheme.name }}</el-descriptions-item>
                      <el-descriptions-item label="分组">{{ currentScheme.group_name }}</el-descriptions-item>
                      <el-descriptions-item label="描述" :span="2">{{ currentScheme.description }}</el-descriptions-item>
                  </el-descriptions>
                  
                  <div style="margin-top: 10px;">
                      <el-tag 
                        v-for="(val, key) in parseVariables(currentScheme.variables)" 
                        :key="key" 
                        style="margin-right: 5px; margin-bottom: 5px;"
                        type="info"
                      >
                        {{ key }}: {{ val }}
                      </el-tag>
                  </div>
              </div>
              
              <el-divider content-position="left">生成步骤</el-divider>
              
              <el-table :data="schemeItems" style="width: 100%" v-loading="loading">
                  <el-table-column prop="template_name" label="使用模板" width="180" />
                  <el-table-column prop="output_filename_pattern" label="输出文件名" />
                  <el-table-column prop="output_sub_package" label="输出子包" />
                  <el-table-column label="状态" width="100">
                      <template #default="scope">
                          <el-switch 
                              v-model="scope.row.is_enabled" 
                              :active-value="1" 
                              :inactive-value="0"
                              @change="(val) => handleToggleStatus(scope.row, val)"
                          />
                      </template>
                  </el-table-column>
                  <el-table-column label="操作" width="150">
                      <template #default="scope">
                          <el-button size="small" @click="handleEditItem(scope.row)">编辑</el-button>
                          <el-button size="small" type="danger" @click="handleDeleteItem(scope.row)">删除</el-button>
                      </template>
                  </el-table-column>
              </el-table>
          </div>
          <div v-else class="empty-state">
            <el-empty description="请从左侧选择一个方案进行配置" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Create/Edit Scheme Info Dialog -->
    <el-dialog v-model="createSchemeVisible" :title="schemeForm.id ? '编辑方案信息' : '新建方案'" width="500px">
        <el-form :model="schemeForm" label-width="80px">
            <el-form-item label="名称">
                <el-input v-model="schemeForm.name" placeholder="请输入方案名称" />
            </el-form-item>
            <el-form-item label="分组">
                <el-input v-model="schemeForm.group_name" placeholder="请输入分组名称 (默认: Default)" />
            </el-form-item>
            <el-form-item label="描述">
                <el-input v-model="schemeForm.description" type="textarea" placeholder="请输入描述" />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="createSchemeVisible = false">取消</el-button>
                <el-button type="primary" @click="submitScheme">确定</el-button>
            </span>
        </template>
    </el-dialog>

    <!-- Global Variables Dialog -->
    <el-dialog v-model="variablesDialogVisible" title="全局变量配置" width="700px">
        <div class="help-text" style="margin-bottom: 10px;">
            定义方案级别的全局变量（如基类路径、公共常量等），可在具体步骤中通过 ${VarName} 引用。
        </div>
        
        <el-table :data="schemeVariablesList" style="width: 100%" border size="small">
            <el-table-column label="变量名 (Key)" width="200">
                <template #default="scope">
                    <el-input v-model="scope.row.key" placeholder="如: DefaultBase" />
                </template>
            </el-table-column>
            <el-table-column label="值 (Value)">
                <template #default="scope">
                    <el-input v-model="scope.row.value" placeholder="如: com.example.BaseEntity" />
                </template>
            </el-table-column>
            <el-table-column label="操作" width="60">
                <template #default="scope">
                    <el-button type="danger" link @click="removeSchemeVariable(scope.$index)">
                        <el-icon><Close /></el-icon>
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-button type="primary" link @click="addSchemeVariable" style="margin-top: 5px;">
            <el-icon><Plus /></el-icon> 添加全局变量
        </el-button>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="variablesDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submitVariables">保存变量</el-button>
            </span>
        </template>
    </el-dialog>

    <!-- Item Dialog -->
    <el-dialog v-model="itemDialogVisible" :title="itemForm.id ? '编辑步骤' : '添加步骤'" width="700px">
        <el-form :model="itemForm" label-width="120px">
            <el-form-item label="选择模板">
                <el-select v-model="itemForm.template_id" placeholder="请选择模板" style="width: 100%" :disabled="!!itemForm.id" filterable>
                    <el-option-group
                      v-for="(templates, type) in groupedTemplates"
                      :key="type"
                      :label="type || '未分类'"
                    >
                      <el-option 
                          v-for="tpl in templates" 
                          :key="tpl.id" 
                          :label="tpl.name" 
                          :value="tpl.id" 
                      />
                    </el-option-group>
                </el-select>
            </el-form-item>
            <el-form-item label="输出文件名">
                <el-input v-model="itemForm.output_filename_pattern" placeholder="例如: {EntityName}Service.java" />
                <div class="help-text">支持变量: {EntityName}</div>
            </el-form-item>
            <el-form-item label="输出子包">
                <el-input v-model="itemForm.output_sub_package" placeholder="例如: service.impl" />
            </el-form-item>
            
            <el-divider>变量映射配置</el-divider>
            <div class="help-text" style="margin-bottom: 10px;">
                配置模板中需要的特殊变量。值可以是字面量，也可以引用全局变量（格式：${GlobalKey}）。
            </div>
            
            <el-table :data="itemVariablesList" style="width: 100%" border size="small">
                <el-table-column label="模板变量名 (Key)" width="200">
                    <template #default="scope">
                        <el-input v-model="scope.row.key" placeholder="如: baseClass" />
                    </template>
                </el-table-column>
                <el-table-column label="值 (Value)">
                    <template #default="scope">
                         <!-- Select from global variables -->
                        <el-select 
                            v-model="scope.row.value" 
                            filterable 
                            allow-create 
                            default-first-option
                            placeholder="选择或输入值"
                            style="width: 100%"
                        >
                             <el-option
                                v-for="(val, key) in parseVariables(currentScheme.variables)"
                                :key="key"
                                :label="`${key} (${val})`"
                                :value="`\${${key}}`"
                             >
                                <span style="float: left">{{ key }}</span>
                                <span style="float: right; color: #8492a6; font-size: 13px">{{ val }}</span>
                             </el-option>
                        </el-select>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="60">
                    <template #default="scope">
                        <el-button type="danger" link @click="removeItemVariable(scope.$index)">
                            <el-icon><Close /></el-icon>
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
            <el-button type="primary" link @click="addItemVariable" style="margin-top: 5px;">
                <el-icon><Plus /></el-icon> 添加变量映射
            </el-button>
            
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="itemDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submitItem">确定</el-button>
            </span>
        </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useSchemeStore } from '../store/scheme'
import { useTemplateStore } from '../store/template' 
import { Cpu, Folder, Plus, Close } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const schemeStore = useSchemeStore()
const templateStore = useTemplateStore()

const schemeList = computed(() => schemeStore.schemeList)
const schemeItems = computed(() => schemeStore.currentSchemeItems)
const templateList = computed(() => templateStore.templateList)
const loading = computed(() => schemeStore.loading)

const activeSchemeId = ref('')
const currentScheme = ref(null)

// Group schemes by group_name
const groupedSchemes = computed(() => {
    const groups = {}
    schemeList.value.forEach(scheme => {
        const group = scheme.group_name || 'Default'
        if (!groups[group]) groups[group] = []
        groups[group].push(scheme)
    })
    return groups
})

// Group templates for selection
const groupedTemplates = computed(() => {
    const groups = {}
    templateList.value.forEach(tpl => {
        const type = tpl.type || '未分类'
        if (!groups[type]) groups[type] = []
        groups[type].push(tpl)
    })
    return groups
})

// Dialogs
const createSchemeVisible = ref(false)
const schemeForm = ref({ 
    id: null,
    name: '', 
    description: '',
    group_name: 'Default',
    variables: {} // JSON object
})
const schemeVariablesList = ref([]) // Array of { key: '', value: '' }

const variablesDialogVisible = ref(false)
const itemDialogVisible = ref(false)
const itemForm = ref({
    id: null,
    template_id: null,
    output_filename_pattern: '',
    output_sub_package: ''
})
const itemVariablesList = ref([]) // Array of { key: '', value: '' }

onMounted(async () => {
  await schemeStore.loadAllSchemes()
  await templateStore.loadAllTemplates()
})

const refreshList = async () => {
  await schemeStore.loadAllSchemes()
}

// ... (keep existing methods: handleSelectScheme, handleCreateScheme, handleEditScheme, submitScheme, handleDeleteScheme) ...

const handleSelectScheme = async (index) => {
  activeSchemeId.value = index
  const scheme = schemeList.value.find(s => String(s.id) === index)
  if (scheme) {
      currentScheme.value = scheme
      await schemeStore.loadSchemeItems(scheme.id)
  }
}

const handleCreateScheme = () => {
    schemeForm.value = { 
        id: null,
        name: '', 
        description: '', 
        group_name: 'Default',
        variables: {}
    }
    schemeVariablesList.value = []
    createSchemeVisible.value = true
}

const handleEditInfo = () => {
    if (!currentScheme.value) return
    schemeForm.value = {
        id: currentScheme.value.id,
        name: currentScheme.value.name,
        description: currentScheme.value.description,
        group_name: currentScheme.value.group_name || 'Default',
        variables: currentScheme.value.variables // Keep existing variables
    }
    createSchemeVisible.value = true
}

const handleEditVariables = () => {
    if (!currentScheme.value) return
    const vars = parseVariables(currentScheme.value.variables)
    schemeVariablesList.value = Object.entries(vars).map(([k, v]) => ({ key: k, value: v }))
    variablesDialogVisible.value = true
}

const addSchemeVariable = () => {
    schemeVariablesList.value.push({ key: '', value: '' })
}

const removeSchemeVariable = (index) => {
    schemeVariablesList.value.splice(index, 1)
}

const submitVariables = async () => {
    // Serialize variables
    const vars = {}
    schemeVariablesList.value.forEach(item => {
        if (item.key) vars[item.key] = item.value
    })
    
    // We need to update only variables, but our API is generic update
    // So we fetch current info and merge
    const payload = {
        variables: JSON.stringify(vars)
    }
    
    const success = await schemeStore.updateScheme(currentScheme.value.id, payload)
    if (success) {
        ElMessage.success('变量配置已更新')
        variablesDialogVisible.value = false
        // Refresh
        const updated = schemeList.value.find(s => s.id === currentScheme.value.id)
        if (updated) currentScheme.value = updated
    } else {
        ElMessage.error('更新失败')
    }
}

const submitScheme = async () => {
    if (!schemeForm.value.name) return ElMessage.warning('请输入名称')
    
    const payload = {
        name: schemeForm.value.name,
        description: schemeForm.value.description,
        group_name: schemeForm.value.group_name,
        variables: schemeForm.value.variables // Pass through
    }

    if (schemeForm.value.id) {
        const success = await schemeStore.updateScheme(schemeForm.value.id, payload)
        if (success) {
            ElMessage.success('更新成功')
            createSchemeVisible.value = false
            const updated = schemeList.value.find(s => s.id === schemeForm.value.id)
            if (updated) currentScheme.value = updated
        } else {
            ElMessage.error('更新失败')
        }
    } else {
        const id = await schemeStore.createScheme(payload)
        if (id) {
            ElMessage.success('创建成功')
            createSchemeVisible.value = false
            activeSchemeId.value = String(id)
            const scheme = schemeList.value.find(s => s.id === id)
            if (scheme) {
                currentScheme.value = scheme
                await schemeStore.loadSchemeItems(id)
            }
        } else {
            ElMessage.error('创建失败')
        }
    }
}

const handleDeleteScheme = async () => {
    if (!currentScheme.value) return
    try {
        await ElMessageBox.confirm('确定删除该方案吗？这将同时删除方案下的所有步骤配置。', '警告', { type: 'warning' })
        const success = await schemeStore.deleteScheme(currentScheme.value.id)
        if (success) {
            ElMessage.success('删除成功')
            currentScheme.value = null
            activeSchemeId.value = ''
        }
    } catch (e) {}
}


const handleAddStep = () => {
    itemForm.value = {
        id: null,
        template_id: null,
        output_filename_pattern: '{EntityName}.java',
        output_sub_package: ''
    }
    itemVariablesList.value = []
    itemDialogVisible.value = true
}

const handleEditItem = (row) => {
    itemForm.value = { ...row }
    // Parse variables
    const vars = parseVariables(row.variables)
    itemVariablesList.value = Object.entries(vars).map(([k, v]) => ({ key: k, value: v }))
    itemDialogVisible.value = true
}

const addItemVariable = () => {
    itemVariablesList.value.push({ key: '', value: '' })
}

const removeItemVariable = (index) => {
    itemVariablesList.value.splice(index, 1)
}

const submitItem = async () => {
    if (!itemForm.value.template_id) return ElMessage.warning('请选择模板')
    if (!itemForm.value.output_filename_pattern) return ElMessage.warning('请输入输出文件名')
    
    // Serialize variables
    const vars = {}
    itemVariablesList.value.forEach(item => {
        if (item.key) vars[item.key] = item.value
    })
    
    const payload = {
        template_id: itemForm.value.template_id,
        output_filename_pattern: itemForm.value.output_filename_pattern,
        output_sub_package: itemForm.value.output_sub_package,
        variables: JSON.stringify(vars)
    }
    
    if (itemForm.value.id) {
        // Update
        const success = await schemeStore.updateSchemeItem(itemForm.value.id, payload)
        if (success) {
            ElMessage.success('更新成功')
            itemDialogVisible.value = false
            await schemeStore.loadSchemeItems(currentScheme.value.id)
        }
    } else {
        // Create
        const createPayload = {
            scheme_id: currentScheme.value.id,
            ...payload
        }
        const success = await schemeStore.createSchemeItem(createPayload)
        if (success) {
            ElMessage.success('添加成功')
            itemDialogVisible.value = false
        }
    }
}

const handleDeleteItem = async (row) => {
    try {
        await ElMessageBox.confirm('确定删除该步骤吗？', '提示', { type: 'warning' })
        const success = await schemeStore.deleteSchemeItem(row.id, currentScheme.value.id)
        if (success) ElMessage.success('删除成功')
    } catch (e) {}
}

const handleToggleStatus = async (row, val) => {
    await schemeStore.updateSchemeItem(row.id, { is_enabled: val })
    ElMessage.success(val ? '已启用' : '已禁用')
}

// Suggestion logic for autocomplete
// Removed as we use Select now

// Helper to parse JSON variables safely
const parseVariables = (vars) => {
    if (!vars) return {}
    if (typeof vars === 'object') return vars
    try {
        return JSON.parse(vars)
    } catch (e) {
        return {}
    }
}
</script>

<style scoped>
.scheme-manager-container {
  padding: 20px;
  height: 100vh;
  box-sizing: border-box;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.scheme-info {
    margin-bottom: 20px;
    color: #666;
    font-size: 14px;
}

.help-text {
    font-size: 12px;
    color: #999;
    margin-top: 4px;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}
</style>