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
      <el-input v-model="jsonFields" type="textarea" :rows="15" placeholder="请输入JSON格式的字段配置"
        style="font-family: monospace; font-size: 14px;" @input="validateJson"></el-input>
      <div class="json-help-text">
        示例：[{"name": "id", "type": "Long", "column": "ID", "id": true, "label": "id"}, {"name": "name", "type":
        "String",
        "column": "NAME", "id": false, "label": "名称"}]
      </div>
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
  }
})

const emit = defineEmits(['update:modelValue'])

// 字段编辑模式：json 或 table
const fieldMode = ref('json')
// JSON编辑器内容
const jsonFields = ref('')
// JSON校验错误
const jsonError = ref('')
// 字段数据
const fields = ref([...props.modelValue])

// 初始化JSON内容
jsonFields.value = JSON.stringify(fields.value, null, 2)

// 监听modelValue变化，更新表单数据
watch(() => props.modelValue, (newValue) => {
  fields.value = [...newValue]
  // 更新JSON内容
  jsonFields.value = JSON.stringify(fields.value, null, 2)
}, { deep: true })

// 监听fields变化，同步到父组件
watch(fields, (newFields) => {
  emit('update:modelValue', [...newFields])
  // 更新JSON内容
  jsonFields.value = JSON.stringify(newFields, null, 2)
}, { deep: true })

// 监听fieldMode变化，同步数据
watch(fieldMode, (newMode, oldMode) => {
  if (oldMode === 'json' && newMode === 'table') {
    // 从JSON切换到表格，需要验证JSON并同步到fields
    validateJson()
    if (!jsonError.value) {
      try {
        fields.value = JSON.parse(jsonFields.value)
      } catch (e) {
        fields.value = []
      }
    }
  } else if (oldMode === 'table' && newMode === 'json') {
    // 从表格切换到JSON，同步表格数据到JSON
    jsonFields.value = JSON.stringify(fields.value, null, 2)
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

    // 同步到fields
    emit('update:modelValue', fields)
    jsonError.value = ''
    return true
  } catch (e) {
    jsonError.value = `JSON格式错误: ${e.message}`
    return false
  }
}

// 添加字段
const addField = () => {
  fields.value.push({
    name: '',
    type: 'String',
    column: '',
    id: false,
    label: ''
  })
}

// 删除字段
const removeField = (index) => {
  fields.value.splice(index, 1)
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
</style>
