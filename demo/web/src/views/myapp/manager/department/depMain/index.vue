<template>
  <div>
    <el-form
      ref="elForm"
      :model="queryParam"
      size="medium"
      label-width="100px"
      label-position="left"
    >
      <el-row :gutter="20">
        <el-col :span="10">
          <el-form-item label="部门名称：" prop="bmmc">
            <el-input
              v-model="queryParam.bmmc"
              placeholder="请输入部门名称"
              clearable
              :style="{ width: '100%' }"
            >
            </el-input>
          </el-form-item>
        </el-col>
        <el-col class="right-align" :span="14">
          <el-button @click="onSearch" type="primary" icon="el-icon-search"
          >查询
          </el-button
          >
          <el-button
            @click="handleNewZnbm"
            type="primary"
            icon="el-icon-circle-plus-outline"
          >新增
          </el-button
          >
        </el-col>
        <el-col :span="24">
          <dep-main-table
            class="table"
            ref="tableRef"
            v-model="queryParam"
          />
        </el-col>
      </el-row>
    </el-form>
    <!-- 新增对话框 -->
    <dep-add-dialog
      :visible.sync="dialogVisible"
      actionType="add"
      @data-refresh="onSearch()"
    />
  </div>
</template>
<script>
import depMainTable from "./depMainTable.vue";
import depAddDialog from "./depAddDialog.vue";

export default {
  data() {
    return {
      dialogVisible: false,
      queryParam: {
        page: "1",
        rows: "10",
        // 部门名称
        bmmc: null,
        // 所属部门
        ssid: [],
      },
    };
  },
  components: {
    depMainTable,
    depAddDialog
  },
  methods: {
    // 查询
    onSearch() {
      this.$refs.tableRef.handelSearch();
    },
    // 调用新增对话框
    handleNewZnbm() {
      this.dialogVisible = true;
    },
  },
  // 添加生命周期函数
  mounted() {
  },
  beforeDestroy() {
  },
  computed: {},
};
</script>

<style scoped>
</style>
