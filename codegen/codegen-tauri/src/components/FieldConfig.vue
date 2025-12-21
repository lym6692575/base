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
        <el-button size="small" @click="openSqlImport">
          <el-icon><Upload /></el-icon> 智能导入(SQL)
        </el-button>
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
    
    <!-- SQL Import Dialog -->
    <el-dialog v-model="sqlImportVisible" title="智能导入 (SQL字段定义)" width="600px" append-to-body>
        <div class="help-text" style="margin-bottom: 10px; color: #666; font-size: 12px;">
            请粘贴 Oracle/MySQL 等风格的字段定义文本（每行一个字段），例如：<br>
            <code>id varchar2(255) not null, /*&lt;主键id&gt;*/</code>
        </div>
        <el-input
            v-model="sqlContent"
            type="textarea"
            :rows="15"
            placeholder="粘贴 SQL 字段定义内容..."
            style="font-family: monospace;"
        />
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="sqlImportVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSqlImport">识别并导入</el-button>
            </span>
        </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Delete, Download, DocumentCopy, Upload } from '@element-plus/icons-vue'

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
const sqlImportVisible = ref(false)
const sqlContent = ref('')

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
        localFields.value = parsedFields // Update localFields instead of emitting directly
      } catch (e) {
        localFields.value = []
      }
    }
  } else if (oldMode === 'table' && newMode === 'json') {
    // 从表格切换到JSON，同步表格数据到JSON
    jsonFields.value = JSON.stringify(localFields.value, null, 2)
    jsonError.value = ''
  }
})

// 初始化宽度
if (fieldMode.value === 'table') {
  emit('update:width', '60%')
} else {
  emit('update:width', '30%')
}

// 字段数据 - 使用computed避免循环更新
// 直接使用 props.modelValue 会导致无法修改，因为 props 是只读的。
// 在表格中 v-model="scope.row.name" 修改的是 fields 数组中的对象属性。
// 如果 fields 是通过 get() => [...props.modelValue] 返回的新数组，那么表格修改的是这个新数组的元素。
// 但 set() 并没有被触发，因为修改的是数组内部对象的属性，而不是替换整个数组。
// 即使触发了 set，它也是把新数组发给父组件。
// 问题在于表格绑定的是 computed 返回的新数组，用户在输入框修改时，修改的是新数组的引用。
// 但Vue的响应式系统需要正确地通知父组件更新。

// 解决方案：使用本地 ref 副本，并监听 props 变化同步。
const localFields = ref([])

// 初始化
watch(() => props.modelValue, (newVal) => {
    // 只有当传入值和本地值不一样时才更新，避免循环
    if (JSON.stringify(newVal) !== JSON.stringify(localFields.value)) {
        localFields.value = JSON.parse(JSON.stringify(newVal))
        jsonFields.value = JSON.stringify(newVal, null, 2)
    }
}, { immediate: true, deep: true })

// 监听本地变化，同步给父组件
watch(localFields, (newVal) => {
    emit('update:modelValue', newVal)
    
    // 如果是由 JSON 编辑引起的更新，不要反向覆盖 jsonFields，否则会打断输入
    if (isUpdatingFromJson.value) {
        isUpdatingFromJson.value = false
        return
    }
    
    // 如果不在 JSON 模式，或者数据来自外部/表格编辑，同步更新 JSON 内容
    // 为了防止不必要的格式化（比如光标跳动），可以加一层判断：
    // 如果当前是 JSON 模式，且 newVal 与 jsonFields 解析后一致，则不更新字符串
    if (fieldMode.value === 'json') {
        try {
             // 只有当内容真的不一致时才覆盖（比如表格模式切换过来，或者外部数据变化）
             // 但如果是用户输入触发的 localFields 变更，上面 isUpdatingFromJson 应该已经拦截了
             // 这里主要处理非用户输入的情况
             const currentJsonObj = JSON.parse(jsonFields.value || '[]')
             if (JSON.stringify(currentJsonObj) !== JSON.stringify(newVal)) {
                 jsonFields.value = JSON.stringify(newVal, null, 2)
             }
        } catch (e) {
             // 当前 JSON 可能无效，但 localFields 更新了（可能来自 props），强制覆盖？
             // 如果来自 props，应该强制覆盖。
             // 这里的 newVal 是 localFields。
             jsonFields.value = JSON.stringify(newVal, null, 2)
        }
    } else {
        // 表格模式，总是更新 JSON 字符串以便切换时就绪
        jsonFields.value = JSON.stringify(newVal, null, 2)
    }
}, { deep: true })

// fields 用于模板绑定，直接指向 localFields
const fields = computed({
    get: () => localFields.value,
    set: (val) => {
        localFields.value = val
    }
})

// 初始化JSON内容
// jsonFields.value = JSON.stringify(fields.value, null, 2) // Removed, handled by watch

// 监听modelValue变化，更新JSON内容
// watch(() => props.modelValue, ... ) // Removed, handled by watch above

const isUpdatingFromJson = ref(false)

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

    jsonError.value = ''
    
    // 如果是JSON模式，且JSON有效，则更新localFields
    // 关键点：使用标志位避免watch循环
    if (fieldMode.value === 'json') {
        // 深度比较，如果真的有变化才更新
        if (JSON.stringify(parsedFields) !== JSON.stringify(localFields.value)) {
            isUpdatingFromJson.value = true
            localFields.value = parsedFields
            // 更新后重置标志位将在 watch 回调中处理，或者使用 nextTick
            // 但 watch 是同步还是异步？Vue 3 watch 默认是 pre-flush (在 DOM 更新前)，但也可能在本次 tick 中。
            // 最安全的方式是：在 watch 中检查这个标志位。
        }
    }
    
    return true
  } catch (e) {
    jsonError.value = `JSON格式错误: ${e.message}`
    return false
  }
}

// 添加字段
const addField = () => {
  const newFields = [...localFields.value]
  newFields.push({
    name: '',
    type: 'String',
    column: '',
    id: false,
    label: ''
  })
  localFields.value = newFields
}

// 删除字段
const removeField = (index) => {
  const newFields = [...localFields.value]
  newFields.splice(index, 1)
  localFields.value = newFields
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

// SQL Import Logic
const openSqlImport = () => {
    sqlContent.value = ''
    sqlImportVisible.value = true
}

const handleSqlImport = () => {
    if (!sqlContent.value.trim()) return
    
    const lines = sqlContent.value.split('\n')
    const newFields = []
    
    // Helper: camelCase converter
    const toCamelCase = (str) => {
        // Handle all caps with underscores (TRGT_NAME -> trgtName)
        // or lowercase with underscores (trgt_name -> trgtName)
        // or already camelCase (trgtName -> trgtName)
        // or simple word (ejdwid -> ejdwid)
        
        // If string contains underscore
        if (str.includes('_')) {
            return str.toLowerCase().replace(/_([a-z])/g, (g) => g[1].toUpperCase())
        }
        // If all caps without underscore (ID -> id)
        if (str === str.toUpperCase()) {
            return str.toLowerCase()
        }
        // Default: return as is (assuming it's already camel or simple lower)
        return str
    }
    
    // Type mapping
    const mapType = (dbType) => {
        const t = dbType.toLowerCase()
        if (t.includes('char') || t.includes('text') || t.includes('clob')) return 'String'
        if (t.includes('int') || t.includes('number') || t.includes('decimal') || t.includes('numeric')) return 'Integer' // Defaulting to Integer for simplicity
        if (t.includes('date') || t.includes('time')) return 'Date'
        if (t.includes('bool') || t.includes('bit')) return 'Boolean'
        return 'String'
    }

    // Regex explanation:
    // ^\s*                  : Start of line, optional whitespace
    // ([a-zA-Z0-9_]+)       : Group 1 - Column Name
    // \s+                   : Whitespace
    // ([a-zA-Z0-9_]+)       : Group 2 - Data Type
    // .*?                   : Any chars (lazy)
    // (?:                   : Start non-capturing group for comments
    //   \/\*                : Match /*
    //   (?:<([^>]+)>)?      : Optional <Content> (Group 3)
    //   ([^*]+)?            : Optional other content inside comment (Group 4)
    //   .*?                 : Any other chars
    //   \*\/                : Match */
    // )?                    : End comment group (optional)
    
    // We need a more robust regex to handle various comment styles:
    // 1. /*<二级单位名称>*/  -> Group 3: 二级单位名称
    // 2. /*<车间id>组织机构有权组织id*/ -> Group 3: 车间id
    // 3. /*车间名称*/ -> No Group 3, but match text inside
    
    // Let's refine the logic. Instead of one complex regex, we can match column and type first, then look for comment.
    
    for (const line of lines) {
        if (!line.trim()) continue
        // Skip comment lines starting with -- or /*
        if (line.trim().startsWith('--') || line.trim().startsWith('/*')) continue
        
        // Match Column and Type
        const basicMatch = line.match(/^\s*([a-zA-Z0-9_]+)\s+([a-zA-Z0-9_]+)/)
        if (basicMatch) {
            const colName = basicMatch[1]
            const dbType = basicMatch[2]
            
            // Extract comment
            let comment = colName // Default
            
            // Try to find /* ... */
            // Fix: support malformed end like */ or /
            const commentMatch = line.match(/\/\*(.*?)(?:\*\/|\/$|$)/)
            if (commentMatch) {
                const rawComment = commentMatch[1].trim()
                // Check if it has <...> format
                const bracketMatch = rawComment.match(/<([^>]+)>/)
                if (bracketMatch) {
                    comment = bracketMatch[1]
                } else {
                    comment = rawComment
                }
            } else {
                // Try to find -- comment
                const doubleDashMatch = line.match(/--(.*)/)
                if (doubleDashMatch) {
                    comment = doubleDashMatch[1].trim()
                }
            }
            
            // Cleanup comment: remove leading/trailing special chars if needed, but usually regex groups handle it.
            // If comment contains special chars like `*` inside (e.g. `*同一组织`), we might want to keep it or not.
            // User's requirement: "I hope label is the Chinese inside /* */".
            // So if we have `/*<Name>*/`, we want `Name`.
            // If we have `/*Name*/`, we want `Name`.
            
            newFields.push({
                name: toCamelCase(colName),
                type: mapType(dbType),
                column: colName.toUpperCase(),
                label: comment,
                id: false // Default not ID, user can check it
            })
        }
    }
    
    if (newFields.length > 0) {
        // Append to existing fields or replace? 
        // Let's append
        const currentFields = [...localFields.value]
        localFields.value = [...currentFields, ...newFields]
        ElMessage.success(`成功识别并导入 ${newFields.length} 个字段`)
        sqlImportVisible.value = false
    } else {
        ElMessage.warning('未能识别有效字段定义，请检查格式')
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
