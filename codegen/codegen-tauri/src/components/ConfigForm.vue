<template>
  <el-drawer v-model="drawerVisible" title="配置详情" direction="rtl" :size="drawerSize">
    <div>
      <el-col>
        <!-- 表单主体区域 -->
        <div>
          <el-divider>包配置</el-divider>
          <el-form ref="formRef" :model="formData" label-width="120px" style="margin-top: 20px;">
            <el-form-item label="包基础路径">
              <el-input v-model="formData.packageBase" placeholder="请输入包基础路径"></el-input>
            </el-form-item>
            <el-form-item label="模块">
              <el-input v-model="formData.module" placeholder="请输入模块名"></el-input>
            </el-form-item>
            <el-form-item label="实体名称">
              <el-input v-model="formData.entityName" placeholder="请输入实体名称"></el-input>
            </el-form-item>
            <el-form-item label="ID类型">
              <el-select v-model="formData.idType" placeholder="请选择ID类型">
                <el-option label="String" value="String"></el-option>
                <el-option label="Long" value="Long"></el-option>
                <el-option label="Integer" value="Integer"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="映射类型">
              <el-select v-model="formData.mapping" placeholder="请选择映射类型">
                <el-option label="TABLE" value="TABLE"></el-option>
                <el-option label="SUBSELECT" value="SUBSELECT"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="表名">
              <el-input v-model="formData.tableName" placeholder="请输入表名"></el-input>
            </el-form-item>
            <el-form-item v-if="formData.mapping === 'SUBSELECT'" label="子查询">
              <el-input v-model="formData.subselect" type="textarea" :rows="3" placeholder="请输入子查询语句"></el-input>
            </el-form-item>
            <el-form-item label="输出目录">
              <el-input v-model="formData.outputDir" placeholder="请输入输出目录"></el-input>
            </el-form-item>
            <el-divider>基础类配置</el-divider>
            <el-form-item label="实体基类">
              <el-input v-model="formData.entityBaseClass" placeholder="请输入实体基类"></el-input>
            </el-form-item>
            <el-form-item label="DTO基类">
              <el-input v-model="formData.dtoBaseClass" placeholder="请输入DTO基类"></el-input>
            </el-form-item>
            <el-form-item label="Mapper基类">
              <el-input v-model="formData.mapperBaseClass" placeholder="请输入Mapper基类"></el-input>
            </el-form-item>
            <el-form-item label="Service基类">
              <el-input v-model="formData.serviceBaseClass" placeholder="请输入Service基类"></el-input>
            </el-form-item>
            <el-form-item label="ServiceImpl基类">
              <el-input v-model="formData.serviceImplBaseClass" placeholder="请输入ServiceImpl基类"></el-input>
            </el-form-item>
          </el-form>
        </div>
      </el-col>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { EditPen, ArrowLeft, ArrowRight, Close } from '@element-plus/icons-vue'
import FieldConfig from './FieldConfig.vue'

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

const emit = defineEmits(['update:modelValue', 'save', 'cancel', 'update:drawerSize'])

const formRef = ref(null)
const formData = ref({ ...props.modelValue })
const isExpanded = ref(false)
const drawerVisible = ref(false)
const drawerSize = ref('30%')

// 确保fields是数组
if (!Array.isArray(formData.value.fields)) {
  formData.value.fields = []
}

// 监听modelValue变化，更新表单数据
watch(() => props.modelValue, (newValue) => {
  formData.value = { ...newValue }
  // 确保fields是数组
  if (!Array.isArray(formData.value.fields)) {
    formData.value.fields = []
  }
}, { deep: true })

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

function open() {
  drawerVisible.value = true
}

function close() {
  drawerVisible.value = false
}

defineExpose({
  open,
  close
})
</script>