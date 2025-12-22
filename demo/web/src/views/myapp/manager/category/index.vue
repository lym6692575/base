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
        <el-col :span="6">
          <el-form-item label="类别名称：" prop="lbmc">
            <el-input
              v-model="queryParams.lbmc"
              placeholder="请输入文件类别名称"
              clearable
              :style="{ width: '100%' }"
            >
            </el-input></el-form-item
        ></el-col>
        <el-col :span="18" class="right-align">
          <el-button @click="onSearch" type="primary" icon="el-icon-search"
            >查询</el-button
          >
          <el-button @click="onAdd" type="primary" icon="el-icon-circle-plus-outline"
            >新增</el-button
          >
        </el-col>
      </el-row>
    </el-form>
    <el-row :gutter="20">
      <el-col>
        <categoryTable ref="tableRef" v-model="queryParams" />
      </el-col>
    </el-row>
    <!-- 新增对话框 -->
    <categoryAddDialog ref="addRef" @data-saved="onSearch()" />
  </div>
</template>

<script>
import categoryTable from "./categoryTable.vue";
import categoryAddDialog from "./categoryAddDialog.vue";
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
    categoryTable,
    categoryAddDialog,
  },
  methods: {
    // 查询
    onSearch() {
      this.$refs.tableRef.handelSearch();
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
