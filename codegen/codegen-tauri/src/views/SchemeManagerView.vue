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
                  type="success" 
                  @click="handleImportSteps"
                >导入步骤</el-button>
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
                  <el-table-column label="使用模板" width="180">
                      <template #default="scope">
                          <el-link type="primary" @click="handlePreviewTemplate(scope.row.template_id)">{{ scope.row.template_name }}</el-link>
                      </template>
                  </el-table-column>
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
    <el-dialog v-model="variablesDialogVisible" title="全局变量配置" width="800px">
        <div class="help-text" style="margin-bottom: 10px; display: flex; justify-content: space-between; align-items: center;">
            <span>定义方案级别的全局变量（如基类路径、公共常量等），可在具体步骤中通过 ${VarName} 引用。</span>
            <el-button type="primary" link @click="handleEditVariablesJson">
                JSON 编辑
            </el-button>
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
        <el-form :model="itemForm" label-width="120px" :rules="itemRules" ref="itemFormRef">
            <el-form-item label="选择模板" prop="template_id">
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
            <el-form-item label="输出文件名" prop="output_filename_pattern">
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
    <!-- Template Preview Dialog -->
    <el-dialog v-model="previewDialogVisible" title="模板预览" width="800px">
        <div v-loading="previewLoading">
            <el-descriptions border :column="1" size="small" style="margin-bottom: 15px">
                <el-descriptions-item label="模板名称">{{ previewTemplate.name }}</el-descriptions-item>
                <el-descriptions-item label="模板类型">{{ previewTemplate.type || '未分类' }}</el-descriptions-item>
                <el-descriptions-item label="描述">{{ previewTemplate.description || '无' }}</el-descriptions-item>
            </el-descriptions>
            <el-input
                v-model="previewTemplate.content"
                type="textarea"
                :rows="20"
                readonly
                class="preview-content"
            />
        </div>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="previewDialogVisible = false">关闭</el-button>
            </span>
        </template>
    </el-dialog>

    <!-- JSON Edit Dialog -->
    <el-dialog v-model="jsonEditVisible" title="JSON 编辑" width="600px">
        <el-input
            v-model="jsonContent"
            type="textarea"
            :rows="15"
            placeholder="请输入 JSON 内容"
        />
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="jsonEditVisible = false">取消</el-button>
                <el-button type="primary" @click="submitJsonEdit">确定</el-button>
            </span>
        </template>
    </el-dialog>

    <!-- Import Steps Dialog -->
    <el-dialog v-model="importStepsVisible" title="导入步骤" width="800px">
        <el-tabs v-model="importActiveTab">
            <!-- Tab 1: From Scheme -->
            <el-tab-pane label="从其他方案导入" name="scheme">
                <el-form label-width="100px">
                    <el-form-item label="选择来源方案">
                        <el-select v-model="sourceSchemeId" placeholder="请选择来源方案" style="width: 100%" @change="handleSourceSchemeChange">
                            <el-option
                                v-for="scheme in schemeList.filter(s => s.id !== currentScheme?.id)"
                                :key="scheme.id"
                                :label="scheme.name"
                                :value="scheme.id"
                            />
                        </el-select>
                    </el-form-item>
                </el-form>
                
                <el-table 
                    ref="sourceItemsTableRef"
                    :data="sourceSchemeItems" 
                    style="width: 100%; margin-top: 10px;" 
                    height="400"
                    @selection-change="handleSelectionChange"
                    v-loading="importLoading"
                >
                    <el-table-column type="selection" width="55" />
                    <el-table-column prop="template_name" label="模板" width="150" />
                    <el-table-column prop="output_filename_pattern" label="输出文件名" />
                    <el-table-column prop="output_sub_package" label="输出子包" />
                </el-table>
                
                <div style="margin-top: 15px; text-align: right;">
                    <el-button @click="importStepsVisible = false">取消</el-button>
                    <el-button type="primary" @click="submitImportFromScheme" :disabled="selectedSourceItems.length === 0">导入选中步骤</el-button>
                </div>
            </el-tab-pane>
            
            <!-- Tab 2: JSON Import/Export -->
            <el-tab-pane label="JSON 导入/导出" name="json">
                 <div class="help-text" style="margin-bottom: 10px;">
                    您可以复制下方的 JSON 分享给他人，或粘贴 JSON 导入步骤。
                </div>
                <el-input
                    v-model="importJsonContent"
                    type="textarea"
                    :rows="15"
                    placeholder="在此粘贴步骤 JSON..."
                />
                 <div style="margin-top: 15px; display: flex; justify-content: space-between;">
                    <el-button @click="copyCurrentStepsJson">复制当前步骤 JSON</el-button>
                    <div>
                        <el-button @click="importStepsVisible = false">取消</el-button>
                        <el-button type="primary" @click="submitImportFromJson">导入 JSON</el-button>
                    </div>
                </div>
            </el-tab-pane>
        </el-tabs>
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
const itemFormRef = ref(null)
const itemRules = {
    template_id: [
        { required: true, message: '请选择模板', trigger: 'change' }
    ],
    output_filename_pattern: [
        { required: true, message: '请输入输出文件名', trigger: 'blur' }
    ]
}
const itemVariablesList = ref([]) // Array of { key: '', value: '' }

const previewDialogVisible = ref(false)
const previewLoading = ref(false)
const previewTemplate = ref({})

const jsonEditVisible = ref(false)
const jsonContent = ref('')

// Import Steps Logic
const importStepsVisible = ref(false)
const importActiveTab = ref('scheme')
const sourceSchemeId = ref(null)
const sourceSchemeItems = ref([])
const selectedSourceItems = ref([])
const importJsonContent = ref('')
const importLoading = ref(false)
const sourceItemsTableRef = ref(null)

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
    
    const normalizeVariablesForApi = (val) => {
        if (val == null) return '{}'
        if (typeof val === 'string') return val
        try {
            return JSON.stringify(val)
        } catch (e) {
            return '{}'
        }
    }
    
    const payload = {
        name: schemeForm.value.name,
        description: schemeForm.value.description,
        group_name: schemeForm.value.group_name,
        variables: normalizeVariablesForApi(schemeForm.value.variables)
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
    if (!itemFormRef.value) return
    
    await itemFormRef.value.validate(async (valid) => {
        if (valid) {
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
    })
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

const handlePreviewTemplate = async (templateId) => {
    previewDialogVisible.value = true
    previewLoading.value = true
    try {
        // Find in store first
        let tpl = templateList.value.find(t => t.id === templateId)
        
        // Use loadTemplateById from store to ensure we get full content
        const result = await templateStore.loadTemplateById(templateId)
        if (result) {
            previewTemplate.value = result
        } else if (tpl) {
             previewTemplate.value = tpl
        }
    } catch (e) {
        ElMessage.error('加载模板失败')
    } finally {
        previewLoading.value = false
    }
}

// Suggestion logic for autocomplete
// Removed as we use Select now

const handleEditVariablesJson = () => {
    const vars = {}
    schemeVariablesList.value.forEach(item => {
        if (item.key) vars[item.key] = item.value
    })
    jsonContent.value = JSON.stringify(vars, null, 4)
    jsonEditVisible.value = true
}

const submitJsonEdit = () => {
    try {
        const vars = JSON.parse(jsonContent.value)
        if (typeof vars !== 'object' || vars === null) {
            ElMessage.error('JSON 格式错误：必须是对象')
            return
        }
        
        schemeVariablesList.value = Object.entries(vars).map(([k, v]) => ({ 
            key: k, 
            value: typeof v === 'string' ? v : JSON.stringify(v) 
        }))
        jsonEditVisible.value = false
        ElMessage.success('JSON 解析成功，请点击“保存变量”以生效')
    } catch (e) {
        ElMessage.error('JSON 解析失败：' + e.message)
    }
}

// --- Import Steps Implementation ---

const handleImportSteps = () => {
    importStepsVisible.value = true
    importActiveTab.value = 'scheme'
    sourceSchemeId.value = null
    sourceSchemeItems.value = []
    selectedSourceItems.value = []
    importJsonContent.value = ''
}

const handleSourceSchemeChange = async (schemeId) => {
    if (!schemeId) {
        sourceSchemeItems.value = []
        return
    }
    
    importLoading.value = true
    try {
        // Use api directly or store method to get items without affecting current view
        // store.loadSchemeItems updates currentSchemeItems, which we don't want.
        // So we need a new action or reuse api directly. 
        // Let's use the API directly for safety, imported at top? 
        // No, let's add a method to store to fetch items without setting state, or use a separate state.
        // Actually, looking at store/scheme.js, loadSchemeItems updates state.
        // We can just call the API service directly here if we import it, or add a store action "fetchSchemeItems".
        // Let's assume we can import api here.
        // But importing api in view is mixing layers.
        // Better: add `getSchemeItems` action to store that returns data instead of setting state.
        
        // Let's quickly check store/scheme.js content again via tool? 
        // I recall it has loadSchemeItems.
        // I will add a temporary fetch in store or just use the existing load but save/restore state? No that's hacky.
        // I'll add a simple fetch action to scheme store later if needed.
        // For now, I'll use the fact that I can't easily modify store file without another tool call.
        // Wait, I can modify store file.
        // But for now, let's use a workaround: The store has `apiGetSchemeItems` imported. 
        // I can just import it here too? No, not good practice.
        // I'll use a new action in the store.
        
        // Actually, let's just add the action to store first.
        // But I need to write this file first. 
        // Let's implement the logic assuming the store action `fetchSchemeItems` exists.
        
        const items = await schemeStore.fetchSchemeItems(schemeId)
        sourceSchemeItems.value = items || []
    } catch (e) {
        ElMessage.error('加载步骤失败')
    } finally {
        importLoading.value = false
    }
}

const handleSelectionChange = (selection) => {
    selectedSourceItems.value = selection
}

const submitImportFromScheme = async () => {
    if (selectedSourceItems.value.length === 0) return
    
    try {
        let successCount = 0
        for (const item of selectedSourceItems.value) {
            const payload = {
                scheme_id: currentScheme.value.id,
                template_id: item.template_id,
                output_filename_pattern: item.output_filename_pattern,
                output_sub_package: item.output_sub_package,
                variables: item.variables // Copy variables as well
            }
            const success = await schemeStore.createSchemeItem(payload)
            if (success) successCount++
        }
        
        ElMessage.success(`成功导入 ${successCount} 个步骤`)
        importStepsVisible.value = false
        // Refresh current list
        await schemeStore.loadSchemeItems(currentScheme.value.id)
    } catch (e) {
        ElMessage.error('导入失败')
    }
}

const copyCurrentStepsJson = () => {
    const data = schemeItems.value.map(item => ({
        template_id: item.template_id,
        template_name: item.template_name, // Optional, for reference
        output_filename_pattern: item.output_filename_pattern,
        output_sub_package: item.output_sub_package,
        variables: item.variables
    }))
    importJsonContent.value = JSON.stringify(data, null, 4)
    ElMessage.success('已生成 JSON，请全选复制')
}

const submitImportFromJson = async () => {
    try {
        const items = JSON.parse(importJsonContent.value)
        if (!Array.isArray(items)) {
            ElMessage.error('JSON 格式错误：必须是数组')
            return
        }
        
        let successCount = 0
        for (const item of items) {
            if (!item.template_id || !item.output_filename_pattern) continue
            
            const payload = {
                scheme_id: currentScheme.value.id,
                template_id: item.template_id,
                output_filename_pattern: item.output_filename_pattern,
                output_sub_package: item.output_sub_package || '',
                variables: item.variables || '{}'
            }
            const success = await schemeStore.createSchemeItem(payload)
            if (success) successCount++
        }
        
        ElMessage.success(`成功导入 ${successCount} 个步骤`)
        importStepsVisible.value = false
        await schemeStore.loadSchemeItems(currentScheme.value.id)
    } catch (e) {
        ElMessage.error('JSON 解析或导入失败：' + e.message)
    }
}

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
