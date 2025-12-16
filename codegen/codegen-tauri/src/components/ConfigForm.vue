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
      <!-- <el-col :span="12">
        <el-form-item label="模板目录">
          <el-input v-model="formData.templatesDir" placeholder="请输入模板目录"></el-input>
        </el-form-item>
      </el-col> -->
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
      <el-input
        v-model="formData.fields"
        type="textarea"
        :rows="6"
        placeholder="请输入字段配置，格式：name:type:column:id:label;name2:type2:column2:id2:label2"
      ></el-input>
      <div class="form-help-text">示例：id:Long:ID:true:id;name:String:NAME:false:name</div>
    </el-form-item>
    
    <el-form-item>
      <el-button type="primary" @click="handleSave">保存</el-button>
      <el-button @click="handleCancel">取消</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'

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
      fields: ''
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

// 监听modelValue变化，更新表单数据
watch(() => props.modelValue, (newValue) => {
  formData.value = { ...newValue }
}, { deep: true })

// 保存表单
const handleSave = async () => {
  try {
    if (formRef.value) {
      await formRef.value.validate()
    }
    console.log(formData.value)
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
</style>
