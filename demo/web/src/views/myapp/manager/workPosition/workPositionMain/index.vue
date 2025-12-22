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
          <el-form-item label="岗位名称：" prop="gwmc">
            <el-input
              v-model="queryParams.gwmc"
              placeholder="请输入岗位名称"
              clearable
              :style="{ width: '100%' }"
            >
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="10">
          <el-form-item label="所属部门：" prop="sjid">
            <sjid v-model="ssidArr" :checkStrictly="false" @change="handleSjidChange"/>
          </el-form-item>
        </el-col>
        <el-col class="right-align" :span="4">
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
          <workPositionMainTable
            class="table"
            ref="tableRef"
            v-model="queryParams"
          />
        </el-col>
      </el-row>
    </el-form>
    <!-- 新增对话框 -->
    <work-position-main-dialog
      :visible.sync="dialogVisible"
      actionType="add"
      @data-refresh="onSearch()"
    />
  </div>
</template>
<script>
import sjid from "@/views/components/cascader/sjid.vue";
import workPositionMainTable from "./workPositionMainTable.vue";
import workPositionMainDialog from "./workPositionMainDialog.vue";

export default {
  data() {
    return {
      dialogVisible: false,
      ssidArr: [],
      queryParams: {
        page: "1",
        rows: "10",
        // 岗位名称
        gwmc: "",
        // 所属部门
        ssid: "",
      },
    };
  },
  components: {
    sjid,
    workPositionMainTable,
    workPositionMainDialog,
  },
  methods: {
    // 查询
    onSearch() {
      this.$refs.tableRef.handelSearch();
    },

    handleSjidChange(val){
      if (val.length > 0) {
        this.queryParams.ssid = val[val.length - 1];
      } else {
        this.queryParams.ssid = val[0];
      }
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
.parent-container {
  display: flex;
  flex-direction: row;
}
</style>
