<template>
  <div class="template-manager-container">
    <el-row :gutter="20">
      <!-- Left: Template List -->
      <el-col :span="6">
        <el-card class="box-card" style="height: calc(100vh - 100px); overflow-y: auto;">
          <template #header>
            <div class="card-header">
              <span>模板列表</span>
              <div>
                  <el-button type="primary" size="small" @click="handleCreate">新建</el-button>
                  <el-button type="text" @click="refreshList">刷新</el-button>
              </div>
            </div>
          </template>
          <el-menu
            :default-active="activeTemplateId"
            class="template-menu"
            @select="handleSelectTemplate"
          >
            <!-- 按 Type 分组 -->
            <template v-for="(templates, type) in groupedTemplates" :key="type">
                <el-sub-menu :index="type || 'Uncategorized'">
                    <template #title>
                        <el-icon><Folder /></el-icon>
                        <span>{{ type || '未分类' }}</span>
                    </template>
                    <el-menu-item
                      v-for="tpl in templates"
                      :key="tpl.id"
                      :index="String(tpl.id)"
                    >
                      <el-icon><Document /></el-icon>
                      <span>{{ tpl.name }}</span>
                    </el-menu-item>
                </el-sub-menu>
            </template>
          </el-menu>
        </el-card>
      </el-col>
      
      <!-- Right: Editor -->
      <el-col :span="18">
        <el-card class="box-card" style="height: calc(100vh - 100px);">
          <template #header>
            <div class="card-header">
              <span>{{ currentTemplate ? currentTemplate.name : '请选择模板' }}</span>
              <div>
                 <el-button 
                  v-if="currentTemplate"
                  type="primary"
                  plain
                  @click="editMetaVisible = true"
                  style="margin-right: 10px;"
                >编辑属性</el-button>
                 <el-button 
                  v-if="currentTemplate"
                  type="danger" 
                  @click="handleDelete" 
                  :loading="deleting"
                  style="margin-right: 10px;"
                >删除</el-button>
                 <el-button 
                  type="primary" 
                  @click="handleSave" 
                  :disabled="!currentTemplate || !hasChanges"
                  :loading="saving"
                >保存修改</el-button>
              </div>
            </div>
          </template>
          
          <div v-if="currentTemplate" class="editor-container">
            <el-input
              v-model="editorContent"
              type="textarea"
              :rows="25"
              placeholder="Template content"
              class="code-editor"
              spellcheck="false"
            />
             <div class="editor-footer">
                <p>类型: {{ currentTemplate.type || '无' }} | 描述: {{ currentTemplate.description }}</p>
                <p>最后更新: {{ currentTemplate.updated_at }}</p>
             </div>
          </div>
          <div v-else class="empty-state">
            <el-empty description="请从左侧选择一个模板进行编辑" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Create Template Dialog -->
    <el-dialog v-model="createDialogVisible" title="新建模板" width="500px">
        <el-form :model="createForm" label-width="80px">
            <el-form-item label="名称">
                <el-input v-model="createForm.name" placeholder="请输入模板名称 (如 java_entity)" />
            </el-form-item>
            <el-form-item label="类型">
                <el-select v-model="createForm.type" placeholder="请选择或输入类型" allow-create filterable>
                    <el-option label="Entity" value="entity" />
                    <el-option label="DTO" value="dto" />
                    <el-option label="Service" value="service" />
                    <el-option label="Controller" value="controller" />
                    <el-option label="Mapper" value="mapper" />
                </el-select>
            </el-form-item>
            <el-form-item label="描述">
                <el-input v-model="createForm.description" placeholder="请输入描述" />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="createDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submitCreate" :loading="creating">确定</el-button>
            </span>
        </template>
    </el-dialog>

    <!-- Edit Meta Dialog -->
    <el-dialog v-model="editMetaVisible" title="编辑模板属性" width="500px">
        <el-form v-if="currentTemplate" label-width="80px">
             <el-form-item label="名称">
                <el-input v-model="currentTemplate.name" disabled />
            </el-form-item>
            <el-form-item label="类型">
                <el-select v-model="currentTemplate.type" placeholder="请选择或输入类型" allow-create filterable>
                    <el-option label="Entity" value="entity" />
                    <el-option label="DTO" value="dto" />
                    <el-option label="Service" value="service" />
                    <el-option label="Controller" value="controller" />
                    <el-option label="Mapper" value="mapper" />
                </el-select>
            </el-form-item>
            <el-form-item label="描述">
                <el-input v-model="currentTemplate.description" placeholder="请输入描述" />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="editMetaVisible = false">取消</el-button>
                <el-button type="primary" @click="submitEditMeta">确定</el-button>
            </span>
        </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useTemplateStore } from '../store/template'
import { Document, Folder } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const templateStore = useTemplateStore()
const templateList = computed(() => templateStore.templateList)
const activeTemplateId = ref('')
const currentTemplate = ref(null)
const editorContent = ref('')
const saving = ref(false)
const deleting = ref(false)
const creating = ref(false)
const editMetaVisible = ref(false)

// Create Dialog
const createDialogVisible = ref(false)
const createForm = ref({
    name: '',
    description: '',
    type: ''
})

// Group templates
const groupedTemplates = computed(() => {
    const groups = {}
    templateList.value.forEach(tpl => {
        const type = tpl.type || '未分类'
        if (!groups[type]) groups[type] = []
        groups[type].push(tpl)
    })
    return groups
})

// Check if content has changed
const hasChanges = computed(() => {
  if (!currentTemplate.value) return false
  return editorContent.value !== currentTemplate.value.content
})

onMounted(async () => {
  await templateStore.loadAllTemplates()
})

const refreshList = async () => {
  await templateStore.loadAllTemplates()
}

const handleSelectTemplate = async (index) => {
  // Confirm if there are unsaved changes
  if (hasChanges.value) {
      // Simple confirm (in a real app, use ElMessageBox)
      if (!confirm('您有未保存的修改，确定要切换吗？')) {
          return
      }
  }

  activeTemplateId.value = index
  const tpl = await templateStore.loadTemplateById(index)
  if (tpl) {
    // Clone to avoid direct mutation issues
    currentTemplate.value = { ...tpl }
    editorContent.value = tpl.content
  }
}

const handleSave = async () => {
  if (!currentTemplate.value) return
  
  saving.value = true
  try {
    const success = await templateStore.updateTemplate(currentTemplate.value.id, editorContent.value)
    if (success) {
      ElMessage.success('保存成功')
      // Update local state
      currentTemplate.value.content = editorContent.value
      // Reload list to update timestamps
      await templateStore.loadAllTemplates()
    } else {
      ElMessage.error('保存失败')
    }
  } catch (e) {
    ElMessage.error('保存出错: ' + e.message)
  } finally {
    saving.value = false
  }
}

const submitEditMeta = async () => {
    if (!currentTemplate.value) return
    
    try {
        // We need an API that supports updating metadata without content, or update all
        // Currently reusing updateTemplate but we might need to extend it or create updateMeta
        // Since updateTemplate only takes content in previous implementation, we need to check store/api
        
        // Actually, looking at main.py update_template, it accepts type and description.
        // But store/api wrapper might need adjustment. 
        // Let's assume store.updateTemplate needs to be called with object or we create a new method.
        // For now, let's just use raw fetch or update store method.
        
        // Wait, I see I didn't update store.js to pass metadata. Let's fix that later or now.
        // I will update api call here directly for now or update store.
        
        // Let's call API directly for simplicity in this turn or rely on store update
        // I will update the store logic in next step, assuming it will be there.
        
        const response = await fetch(`http://127.0.0.1:8000/templates/${currentTemplate.value.id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                type: currentTemplate.value.type,
                description: currentTemplate.value.description
            })
        })
        
        if (response.ok) {
            ElMessage.success('属性更新成功')
            editMetaVisible.value = false
            await templateStore.loadAllTemplates()
        } else {
            ElMessage.error('更新失败')
        }
        
    } catch (e) {
        ElMessage.error('更新出错: ' + e.message)
    }
}

const handleCreate = () => {
    createForm.value = { name: '', description: '', type: '' }
    createDialogVisible.value = true
}

const submitCreate = async () => {
    if (!createForm.value.name) {
        ElMessage.warning('请输入模板名称')
        return
    }
    
    creating.value = true
    try {
        const id = await templateStore.createTemplate({
            name: createForm.value.name,
            description: createForm.value.description,
            type: createForm.value.type,
            content: '' // Start with empty content
        })
        
        if (id) {
            ElMessage.success('创建成功')
            createDialogVisible.value = false
            // Select the new template
            activeTemplateId.value = String(id)
            await handleSelectTemplate(String(id))
        } else {
            ElMessage.error('创建失败，可能是名称重复')
        }
    } catch (e) {
        ElMessage.error('创建出错: ' + e.message)
    } finally {
        creating.value = false
    }
}

const handleDelete = async () => {
    if (!currentTemplate.value) return
    
    try {
        await ElMessageBox.confirm(
            `确定要删除模板 "${currentTemplate.value.name}" 吗？`,
            '警告',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
            }
        )
        
        deleting.value = true
        const success = await templateStore.deleteTemplate(currentTemplate.value.id)
        if (success) {
            ElMessage.success('删除成功')
            currentTemplate.value = null
            editorContent.value = ''
            activeTemplateId.value = ''
        } else {
            ElMessage.error('删除失败')
        }
    } catch (e) {
        if (e !== 'cancel') {
            ElMessage.error('删除出错: ' + e.message)
        }
    } finally {
        deleting.value = false
    }
}
</script>

<style scoped>
.template-manager-container {
  padding: 20px;
  height: 100vh;
  box-sizing: border-box;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.editor-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.code-editor :deep(.el-textarea__inner) {
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 14px;
  line-height: 1.5;
  background-color: #f8f9fa;
  color: #333;
}

.editor-footer {
    margin-top: 10px;
    font-size: 12px;
    color: #999;
    text-align: right;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}
</style>