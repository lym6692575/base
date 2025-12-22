<template>
  <div>
    <el-form
      ref="elForm"
      :model="queryParams"
      size="medium"
      label-width="100px"
      label-position="left"
    >
      <el-row :gutter="20">
        <el-col :span="10">
          <el-form-item label="类别名称：" prop="lbmc">
            <el-input
              v-model="queryParams.lbmc"
              placeholder="请输入类别名称"
              clearable
              :style="{ width: '100%' }"
            >
            </el-input></el-form-item
        ></el-col>
        <el-col :span="14" class="right-align">
          <el-button @click="onSearch" type="primary" icon="el-icon-search"
            >查询</el-button
          >
          <el-button
            @click="onAdd"
            type="primary"
            icon="el-icon-circle-plus-outline"
            >新增</el-button
          >
        </el-col>
      </el-row>
    </el-form>
    <el-row :gutter="20">
      <el-col>
        <file-Category-Table ref="tableRef" v-model="queryParams" />
      </el-col>
    </el-row>
    <!-- 新增对话框 -->
    <fileCategoryDialog ref="addRef" @data-saved="onSearch()" />
  </div>
</template>

<script>
import fileCategoryTable from "./fileCategoryTable.vue";
import fileCategoryDialog from "./fileCategoryDialog.vue";
import { dateUtils, DeepCopyUtils } from "@/utils/LeeUtils";

export default {
  name: "category",
  data() {
    return {
      queryParams: {
        page: "1",
        rows: "10",
        // 类别名称
        lbmc: "",
      },
    };
  },
  components: {
    fileCategoryTable,
    fileCategoryDialog,
  },
  methods: {
    // 查询
    onSearch() {
      let _queryParams = DeepCopyUtils.deepCopy(this.queryParams); // 深拷贝表单防止污染
      _queryParams.page = 1; // 防止分页器报错所以赋值为1
      this.$refs.tableRef.handleGetTableData(_queryParams);
    },
    // 调用新增对话框
    onAdd() {
      this.$refs.addRef.dialogVisible = true;
    },
  },
  // 添加生命周期函数
  mounted() {},
  beforeDestroy() {},
  computed: {},
};
</script>

<style scoped>
.parent-container {
  display: flex;
  flex-direction: row;
}
</style>
