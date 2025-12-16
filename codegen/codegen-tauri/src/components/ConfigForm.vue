<template>
  <el-form
    ref="formRef"
    :model="formData"
    label-width="120px"
    style="margin-top: 20px;"
  >
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="包基础路径">
          <el-input v-model="formData.packageBase" placeholder="请输入包基础路径"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="模块">
          <el-input v-model="formData.module" placeholder="请输入模块名"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
    
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="实体名称">
          <el-input v-model="formData.entityName" placeholder="请输入实体名称"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="ID类型">
          <el-select v-model="formData.idType" placeholder="请选择ID类型">
            <el-option label="String" value="String"></el-option>
            <el-option label="Long" value="Long"></el-option>
            <el-option label="Integer" value="Integer"></el-option>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="映射类型">
          <el-select v-model="formData.mapping" placeholder="请选择映射类型">
            <el-option label="TABLE" value="TABLE"></el-option>
            <el-option label="SUBSELECT" value="SUBSELECT"></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="表名">
          <el-input v-model="formData.tableName" placeholder="请输入表名"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
    
    <el-form-item v-if="formData.mapping === 'SUBSELECT'" label="子查询">
      <el-input
        v-model="formData.subselect"
        type="textarea"
        :rows="3"
        placeholder="请输入子查询语句"
      ></el-input>
    </el-form-item>
    
    <el-row :gutter="20">
      <el-col :span="24">
        <el-form-item label="输出目录">
          <el-input v-model="formData.outputDir" placeholder="请输入输出目录"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
    
    <el-divider>基础类配置</el-divider>
    
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="实体基类">
          <el-input v-model="formData.entityBaseClass" placeholder="请输入实体基类"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="DTO基类">
          <el-input v-model="formData.dtoBaseClass" placeholder="请输入DTO基类"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
    
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="Mapper基类">
          <el-input v-model="formData.mapperBaseClass" placeholder="请输入Mapper基类"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="Service基类">
          <el-input v-model="formData.serviceBaseClass" placeholder="请输入Service基类"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
    
    <el-form-item label="ServiceImpl基类">
      <el-input v-model="formData.serviceImplBaseClass" placeholder="请输入ServiceImpl基类"></el-input>
    </el-form-item>
    
    <el-divider>字段配置</el-divider>
    
    <el-form-item label="字段配置">
      <!-- 模式切换 -->
      <div class="mode-switcher" style="margin-bottom: 15px;">
        <el-radio-group v-model="fieldMode" size="small">
          <el-radio-button label="json">JSON编辑器</el-radio-button>
          <el-radio-button label="table">表格编辑</el-radio-button>
        </el-radio-group>
        
        <!-- JSON校验错误提示 -->
        <el-alert
          v-if="jsonError"
          type="error"
          :title="jsonError"
          :closable="false"
          style="margin-top: 10px;"
        ></el-alert>
      </div>
      
      <!-- 表格模式 -->
      <div v-if="fieldMode === 'table'" class="fields-container">
        <el-button type="primary" size="small" @click="addField" style="margin-bottom: 15px;">
          <el-icon><Plus /></el-icon> 添加字段
        </el-button>
        
        <el-table
          :data="formData.fields"
          border
          style="width: 100%"
          stripe
        >
          <el-table-column prop="id" label="主键" min-width="80" align="center">
            <template #default="scope">
              <el-switch v-model="scope.row.id"></el-switch>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="字段名" min-width="120">
            <template #default="scope">
              <el-input v-model="scope.row.name" placeholder="请输入字段名"></el-input>
            </template>
          </el-table-column>
          <el-table-column prop="type" label="类型" min-width="100">
            <template #default="scope">
              <el-select v-model="scope.row.type" placeholder="请选择类型">
                <el-option label="String" value="String"></el-option>
                <el-option label="Long" value="Long"></el-option>
                <el-option label="Integer" value="Integer"></el-option>
                <el-option label="Boolean" value="Boolean"></el-option>
                <el-option label="Date" value="Date"></el-option>
                <el-option label="BigDecimal" value="BigDecimal"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="column" label="数据库列名" min-width="120">
            <template #default="scope">
              <el-input v-model="scope.row.column" placeholder="请输入数据库列名"></el-input>
            </template>
          </el-table-column>
          <el-table-column prop="label" label="显示名称" min-width="120">
            <template #default="scope">
              <el-input v-model="scope.row.label" placeholder="请输入显示名称"></el-input>
            </template>
          </el-table-column>
          <el-table-column label="操作" min-width="200" align="center">
            <template #default="scope">
              <el-button
                size="small"
                type="danger"
                @click="removeField(scope.$index)"
              >
                <el-icon><Delete /></el-icon> 删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div v-if="formData.fields.length === 0" class="empty-fields">
          暂无字段，请点击"添加字段"按钮添加字段
        </div>
      </div>
      
      <!-- JSON模式 -->
      <div v-else class="json-container">
        <el-input
          v-model="jsonFields"
          type="textarea"
          :rows="15"
          placeholder="请输入JSON格式的字段配置"
          style="font-family: monospace; font-size: 14px;"
          @input="validateJson"
        ></el-input>
        <div class="json-help-text">
          示例：[{"name": "id", "type": "Long", "column": "ID", "id": true, "label": "id"}, {"name": "name", "type": "String", "column": "NAME", "id": false, "label": "名称"}]
        </div>
      </div>
    </el-form-item>
    
    <!-- 右侧按钮布局 -->
    <el-form-item style="text-align: right;">
      <el-button type="primary" @click="handleSave">保存</el-button>
      <el-button @click="handleCancel">取消</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({
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
      fields: []
    })
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'save', 'cancel'])

const formRef = ref(null)
const formData = ref({ ...props.modelValue })

// 字段编辑模式：json 或 table
const fieldMode = ref('json')
// JSON编辑器内容
const jsonFields = ref('')
// JSON校验错误
const jsonError = ref('')

// 确保fields是数组
if (!Array.isArray(formData.value.fields)) {
  formData.value.fields = []
}

// 初始化JSON内容
jsonFields.value = JSON.stringify(formData.value.fields, null, 2)

// 监听modelValue变化，更新表单数据
watch(() => props.modelValue, (newValue) => {
  formData.value = { ...newValue }
  // 确保fields是数组
  if (!Array.isArray(formData.value.fields)) {
    formData.value.fields = []
  }
  // 更新JSON内容
  jsonFields.value = JSON.stringify(formData.value.fields, null, 2)
}, { deep: true })

// 监听fieldMode变化，同步数据
watch(fieldMode, (newMode, oldMode) => {
  if (oldMode === 'json' && newMode === 'table') {
    // 从JSON切换到表格，需要验证JSON并同步到fields
    validateJson()
    if (!jsonError.value) {
      try {
        formData.value.fields = JSON.parse(jsonFields.value)
      } catch (e) {
        formData.value.fields = []
      }
    }
  } else if (oldMode === 'table' && newMode === 'json') {
    // 从表格切换到JSON，同步表格数据到JSON
    jsonFields.value = JSON.stringify(formData.value.fields, null, 2)
    jsonError.value = ''
  }
})

// 验证JSON
const validateJson = () => {
  if (!jsonFields.value.trim()) {
    jsonError.value = ''
    return true
  }
  
  try {
    const fields = JSON.parse(jsonFields.value)
    if (!Array.isArray(fields)) {
      jsonError.value = 'JSON必须是数组格式'
      return false
    }
    
    // 验证每个字段的必填项
    const invalidField = fields.find(field => !field.name || !field.type)
    if (invalidField) {
      jsonError.value = '每个字段必须包含name和type属性'
      return false
    }
    
    jsonError.value = ''
    return true
  } catch (e) {
    jsonError.value = `JSON格式错误: ${e.message}`
    return false
  }
}

// 添加字段
const addField = () => {
  formData.value.fields.push({
    name: '',
    type: 'String',
    column: '',
    id: false,
    label: ''
  })
}

// 删除字段
const removeField = (index) => {
  formData.value.fields.splice(index, 1)
}

// 保存表单
const handleSave = async () => {
  try {
    // 验证必填字段
    if (!formData.value.packageBase) {
      ElMessage.error('请输入包基础路径')
      return
    }
    if (!formData.value.entityName) {
      ElMessage.error('请输入实体名称')
      return
    }
    
    // 验证字段
    if (fieldMode.value === 'json') {
      // JSON模式下验证JSON
      if (!validateJson()) {
        ElMessage.error('JSON格式错误，请检查')
        return
      }
      // 解析JSON到fields
      formData.value.fields = JSON.parse(jsonFields.value)
    }
    
    // 验证字段数量
    if (formData.value.fields.length === 0) {
      ElMessage.error('请至少添加一个字段')
      return
    }
    
    // 验证每个字段的必填项
    const invalidField = formData.value.fields.find(field => !field.name || !field.type)
    if (invalidField) {
      ElMessage.error('请完善所有字段的名称和类型')
      return
    }
    
    emit('save', formData.value)
  } catch (error) {
    ElMessage.error('表单验证失败')
  }
}

// 取消编辑
const handleCancel = () => {
  emit('cancel')
}
</script>

<style scoped>
.form-help-text {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.fields-container {
  margin: 10px 0;
}

.empty-fields {
  text-align: center;
  padding: 40px 0;
  color: #909399;
  background-color: #f5f7fa;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
}

.el-form-item {
  margin-bottom: 20px;
}

/* 表格样式优化 */
.el-table {
  margin-top: 10px;
}

.el-table th {
  background-color: #f5f7fa;
  font-weight: 600;
}

/* 按钮样式优化 */
.fields-container .el-button {
  margin-bottom: 15px;
}

/* JSON模式样式 */
.json-container {
  margin-top: 10px;
}

.json-help-text {
  font-size: 12px;
  color: #909399;
  margin-top: 10px;
  line-height: 1.5;
  font-family: monospace;
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

/* 模式切换器样式 */
.mode-switcher {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  flex-wrap: wrap;
  gap: 10px;
}

/* 右侧按钮布局 */
.el-form-item:last-of-type {
  text-align: right;
  padding-right: 10px;
}
</style>
