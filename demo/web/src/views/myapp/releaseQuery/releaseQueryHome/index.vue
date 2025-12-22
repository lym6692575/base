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
          <el-form-item label="业务类别" prop="ywlbid">
            <ywlb v-model="queryParams.ywlbid" />
          </el-form-item>
        </el-col>
        <el-col :span="7">
          <el-form-item label="发文时间:" prop="date">
            <el-date-picker
              type="daterange"
              v-model="date"
              format="yyyy-MM-dd"
              value-format="yyyy-MM-dd"
              :style="{ width: '100%' }"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              range-separator="至"
              unlink-panels
              :clearable="false"
              @change="handleDataChange"
            ></el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="10" class="right-align">
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
              @click="getExcel"
              type="primary"
              icon="el-icon-folder-opened"
              >导出</el-button
            >
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col>
          <release-query-home-table
            class="table"
            ref="tableRef"
            v-model="queryParams"
          ></release-query-home-table>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>
<script>
import releaseQueryHomeTable from "./releaseQueryHomeTable";
import { ywlb } from "@/components/select";
import { dateUtils, DeepCopyUtils } from "@/utils/LeeUtils";
import { getMarkedSsbmids } from "@/api/app/monthlyComplianceInfo/dataStatistics";

export default {
  components: { ywlb, releaseQueryHomeTable },
  props: [],
  data() {
    let defaultDates = this.defaultValue();
    return {
      dialogVisible: false,
      isLoading: false,
      // 查询参数
      queryParams: {
        ywlbid: "",
        startDate: defaultDates[0],
        endDate: defaultDates[1]
      },
      date: this.defaultValue(),
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
    // 查询
    onSearch() {
      this.$refs["queryForm"].validate((valid) => {
        this.$refs.tableRef.handelSearch();
      });
    },

    handleDataChange(val) {
      this.date = [...val];
      this.queryParams.startDate = val[0];
      this.queryParams.endDate = val[1];
    },

    // 获取默认查询日期
    defaultValue() {
      const currentDate = new Date();
      const threeMonthsAgo = new Date();
      threeMonthsAgo.setMonth(currentDate.getMonth() - 3);
      return [
        dateUtils.format(threeMonthsAgo, "YYYY-MM-DD"),
        dateUtils.format(currentDate, "YYYY-MM-DD"),
      ];
    },

    // 导出表格
    getExcel() {
      this.$refs.tableRef.getExcel();
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
