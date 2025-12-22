<template>
  <div>
    <el-form
      ref="queryForm"
      :model="queryParams"
      size="medium"
      label-width="100px"
      label-position="left"
    >
      <el-row :gutter="20">
        <el-col :span="7">
          <el-form-item label="真实姓名：" prop="ssyf">
            <el-input
              v-model="queryParams.realname"
              placeholder="请输入真实姓名"
              clearable
              :style="{ width: '100%' }"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="24 - 7" class="right-align">
          <el-form-item>
            <el-button
              @click="onSearch()"
              type="primary"
              icon="el-icon-search"
              size="medium"
            >
              查询
            </el-button>
            <el-button
              @click="handleNewAdmin()"
              type="primary"
              icon="el-icon-circle-plus-outline"
              size="medium"
            >
              新增
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col>
          <admin-table
            class="table"
            ref="tableRef"
            v-model="queryParams"
          ></admin-table>
          <admin-dialog
            :visible.sync="dialogVisible"
            :postType="'add'"
            ref="editRef"
            @data-save="onSearch"
          ></admin-dialog>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>
<script>
import adminDialog from "./adminDialog";
import adminTable from "./adminTable";
import sjid from "@/views/components/cascader/sjid.vue";
import { getMarkedSsbmids } from "@/api/app/monthlyComplianceInfo/dataStatistics";

export default {
  inject: ["eventBus"], // 调用事件总线
  components: { adminDialog, adminTable, sjid },
  props: [],
  data() {
    return {
      dialogVisible: false,
      isLoading: false,
      // 查询参数
      queryParams: {
        page: 1,
        rows: 10,
        realname: undefined,
      },
      queryRules: {
        ssyf: [
          {
            required: true,
            message: "请选择月份",
            trigger: "change",
          },
        ],
        ssbmid: [
          {
            required: true,
            message: "请选择部门",
            trigger: "change",
          },
        ],
      },
    };
  },
  computed: {},
  watch: {},
  created() {},
  mounted() {},
  methods: {
    // 获取查询有重大违规事件的组织id
    handleGetMarkedSsbmids(ssyf) {
      getMarkedSsbmids(ssyf).then((res) => {
        this.markedSsbmids = res.data;
      });
    },

    // 新增
    handleNewAdmin() {
      this.dialogVisible = true;
    },

    onSearch() {
      this.$refs["queryForm"].validate((valid) => {
        if (!valid) return;
        this.$refs.tableRef.handelSearch();
      });
    },

    submitForm() {
      this.$refs["elForm"].validate((valid) => {
        if (!valid) return;
        // TODO 提交表单
        saveYdhgxx(this.formData)
          .then((response) => {
            this.$message({
              message: response.msg,
              type: "success",
            });
            console.log(response.data);
            this.formData = response.data;
          })
          .catch((error) => {
            this.$message({
              message: error,
              type: "warning",
            });
          });
      });
    },

    resetForm() {
      this.$refs["elForm"].resetFields();
    },
  },
};
</script>
<style lang="sass" scoped>
.wgitem
  display: flex
  align-items: center
  width: 100%

.left-align
  margin-right: 10px

.flex-grow
  flex-grow: 1
</style>
