<template>
  <div class="field-config-container">
    <!-- 模式切换 -->
    <div class="mode-switcher" style="margin-bottom: 15px;">
      <el-radio-group v-model="fieldMode" size="small">
        <el-radio-button label="json">JSON编辑器</el-radio-button>
        <el-radio-button label="table">表格编辑</el-radio-button>
      </el-radio-group>

      <!-- JSON操作按钮 -->
      <div v-if="fieldMode === 'json'" class="json-actions">
        <el-button size="small" @click="exportJson">
          <el-icon>
            <Download />
          </el-icon> 导出JSON
        </el-button>
        <el-button size="small" @click="copyJson">
          <el-icon>
            <DocumentCopy />
          </el-icon> 复制JSON
        </el-button>
      </div>
      <div v-if="fieldMode === 'table'" class="json-actions">
        <el-button size="small" @click="addField">
          <el-icon>
            <Plus />
          </el-icon> 添加字段
        </el-button>
      </div>
      <!-- JSON校验错误提示 -->
      <el-alert v-if="jsonError" type="error" :title="jsonError" :closable="false" style="margin-top: 10px;"></el-alert>
    </div>

    <!-- 表格模式 -->
    <div v-if="fieldMode === 'table'" class="fields-container">
      <el-table :data="fields" border style="width: 100%" stripe>
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
            <el-button size="small" type="danger" @click="removeField(scope.$index)">
              <el-icon>
                <Delete />
              </el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="fields.length === 0" class="empty-fields">
        暂无字段，请点击"添加字段"按钮添加字段
      </div>
    </div>

    <!-- JSON模式 -->
    <div v-else class="json-container">
      <el-input 
        v-model="jsonFields"
        type="textarea"
        :rows="25" 
        placeholder="请输入JSON格式的字段配置"
        style="font-family: monospace; font-size: 14px;" 
        @input="validateJson"
        >
      </el-input>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Delete, Download, DocumentCopy } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  width: {
    type: String,
    default: '800px'
  }
})

const emit = defineEmits(['update:modelValue', 'update:width'])

// 字段编辑模式：json 或 table
const fieldMode = ref('json')
// JSON编辑器内容
const jsonFields = ref('')
// JSON校验错误
const jsonError = ref('')

// 初始化宽度
if (fieldMode.value === 'table') {
  // 初始表格模式下宽度改为60%
  emit('update:width', '60%')
} else {
  // 初始JSON模式下宽度改为30%
  emit('update:width', '30%')
}
// 字段数据 - 使用computed避免循环更新
const fields = computed({
  get: () => [...props.modelValue],
  set: (newValue) => {
    emit('update:modelValue', [...newValue])
    // 更新JSON内容
    jsonFields.value = JSON.stringify(newValue, null, 2)
  }
})

// 初始化JSON内容
jsonFields.value = JSON.stringify(fields.value, null, 2)

// 监听modelValue变化，更新JSON内容
watch(() => props.modelValue, (newValue) => {
  jsonFields.value = JSON.stringify(newValue, null, 2)
}, { deep: true })

// 监听fieldMode变化，同步数据和调整宽度
watch(fieldMode, (newMode, oldMode) => {
  console.log('fieldMode变化:', newMode, oldMode)
  // 根据fieldMode动态调整宽度
  if (newMode === 'table') {
    // 表格模式下宽度改为60%
    console.log('切换到表格模式，宽度改为60%')
    emit('update:width', '60%')
  } else if (newMode === 'json') {
    // JSON模式下宽度改为30%
    emit('update:width', '30%')
  }
  
  // 同步数据
  if (oldMode === 'json' && newMode === 'table') {
    // 从JSON切换到表格，需要验证JSON并同步到fields
    validateJson()
    if (!jsonError.value) {
      try {
        const parsedFields = JSON.parse(jsonFields.value)
        emit('update:modelValue', parsedFields)
      } catch (e) {
        emit('update:modelValue', [])
      }
    }
  } else if (oldMode === 'table' && newMode === 'json') {
    // 从表格切换到JSON，同步表格数据到JSON
    jsonFields.value = JSON.stringify(props.modelValue, null, 2)
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
    const parsedFields = JSON.parse(jsonFields.value)
    if (!Array.isArray(parsedFields)) {
      jsonError.value = 'JSON必须是数组格式'
      return false
    }

    // 验证每个字段的必填项
    const invalidField = parsedFields.find(field => !field.name || !field.type)
    if (invalidField) {
      jsonError.value = '每个字段必须包含name和type属性'
      return false
    }

    // 只有在JSON模式切换到表格模式时才更新fields
    // 避免input事件触发时的循环更新
    jsonError.value = ''
    return true
  } catch (e) {
    jsonError.value = `JSON格式错误: ${e.message}`
    return false
  }
}

// 添加字段
const addField = () => {
  const newFields = [...props.modelValue]
  newFields.push({
    name: '',
    type: 'String',
    column: '',
    id: false,
    label: ''
  })
  emit('update:modelValue', newFields)
}

// 删除字段
const removeField = (index) => {
  const newFields = [...props.modelValue]
  newFields.splice(index, 1)
  emit('update:modelValue', newFields)
}

// 导出JSON
const exportJson = () => {
  try {
    const jsonStr = JSON.stringify(fields.value, null, 2)
    const blob = new Blob([jsonStr], { type: 'application/json' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'fields-config.json'
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
    ElMessage.success('JSON导出成功')
  } catch (error) {
    ElMessage.error('JSON导出失败')
  }
}

// 复制JSON
const copyJson = () => {
  try {
    const jsonStr = JSON.stringify(fields.value, null, 2)
    navigator.clipboard.writeText(jsonStr)
    ElMessage.success('JSON复制成功')
  } catch (error) {
    ElMessage.error('JSON复制失败')
  }
}
</script>

<style scoped>
.field-config-container {
  margin-top: 10px;
}

.mode-switcher {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  flex-wrap: wrap;
  gap: 10px;
}

.json-actions {
  display: flex;
  gap: 8px;
  margin-left: auto;
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

.el-table {
  margin-top: 10px;
}

.el-table th {
  background-color: #f5f7fa;
  font-weight: 600;
}

.fields-container .el-button {
  margin-bottom: 15px;
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
</style>
