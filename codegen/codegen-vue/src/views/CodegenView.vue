<template>
  <div class="codegen-container">
    <h1>代码生成管理</h1>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card title="配置参数">
          <el-form ref="formRef" :model="formData" label-width="100px">
            <el-form-item label="模板名称">
              <el-input v-model="formData.templateName" placeholder="请输入模板名称"></el-input>
            </el-form-item>
            <el-form-item label="输出路径">
              <el-input v-model="formData.outputPath" placeholder="请输入输出路径"></el-input>
            </el-form-item>
            <el-form-item label="数据源">
              <el-select v-model="formData.dataSource" placeholder="请选择数据源">
                <el-option label="MySQL" value="mysql"></el-option>
                <el-option label="PostgreSQL" value="postgresql"></el-option>
                <el-option label="H2" value="h2"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="生成类型">
              <el-checkbox-group v-model="formData.generateTypes">
                <el-checkbox label="实体类"></el-checkbox>
                <el-checkbox label="Mapper接口"></el-checkbox>
                <el-checkbox label="Service层"></el-checkbox>
                <el-checkbox label="Controller层"></el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleGenerate">生成代码</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card title="生成日志">
          <el-scrollbar height="400px">
            <pre class="log-content">{{ generateLog }}</pre>
          </el-scrollbar>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card title="生成历史">
          <el-table :data="generateHistory" style="width: 100%">
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column prop="templateName" label="模板名称"></el-table-column>
            <el-table-column prop="generateTime" label="生成时间"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 'success' ? 'success' : 'danger'">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="outputFiles" label="输出文件数" width="120"></el-table-column>
            <el-table-column label="操作" width="180">
              <template #default="scope">
                <el-button size="small" @click="handleView(scope.row)">查看详情</el-button>
                <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useCodegenStore } from '../store/codegen'

const codegenStore = useCodegenStore()
const formRef = ref(null)
const formData = reactive({
  templateName: '',
  outputPath: '',
  dataSource: 'h2',
  generateTypes: ['实体类', 'Mapper接口']
})
const generateLog = ref('')
const generateHistory = ref([])

// 生成代码
const handleGenerate = async () => {
  try {
    await formRef.value.validate()
    generateLog.value += `开始生成代码...\n`
    
    // 调用store中的生成代码方法
    const result = await codegenStore.generateCode(formData)
    
    generateLog.value += `代码生成成功！\n生成文件：${result.outputFiles}\n`
    ElMessage.success('代码生成成功')
    
    // 更新历史记录
    fetchHistory()
  } catch (error) {
    generateLog.value += `代码生成失败：${error.message}\n`
    ElMessage.error('代码生成失败')
  }
}

// 重置表单
const handleReset = () => {
  formRef.value.resetFields()
}

// 查看详情
const handleView = (row) => {
  ElMessage.info('查看详情功能开发中...')
}

// 删除记录
const handleDelete = (row) => {
  ElMessage.info('删除功能开发中...')
}

// 获取生成历史
const fetchHistory = async () => {
  // 模拟从store获取历史记录
  generateHistory.value = codegenStore.generateHistory
}

// 组件初始化
onMounted(() => {
  // 初始化store数据
  codegenStore.init();
});
</script>

<style scoped>
.codegen-container {
  max-width: 1200px;
  margin: 50px auto;
  padding: 0 20px;
}

h1 {
  font-size: 2rem;
  margin-bottom: 30px;
  color: #333;
  text-align: center;
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
}
</style>