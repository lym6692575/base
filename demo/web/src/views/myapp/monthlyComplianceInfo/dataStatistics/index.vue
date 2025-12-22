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
          <!--          <el-form-item label="选择季度：" prop="ssjd">-->
          <!--            <lee-quarter-picker v-model="queryParams.ssjd"></lee-quarter-picker>-->
          <!--          </el-form-item>-->
          <el-form-item label="选择月份：" prop="ssyf">
            <el-date-picker
              type="month"
              v-model="queryParams.ssyf"
              format="yyyy-MM"
              value-format="yyyy-MM"
              :style="{ width: '100%' }"
              placeholder="请选择月份"
              clearable
              @change="dateChange"
            ></el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="11">
          <el-form-item
            label="所属部门 / 分公司："
            prop="znbmid"
            label-width="150px"
          >
            <sjid
              v-model="queryParams.ssbmid"
              :onlyCurrentLevel="true"
              :markedSsbmids="markedSsbmids"
              @change="handleChangeSjid"
              type="ydhg"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6" class="right-align">
          <el-form-item>
            <el-button
              @click="onSearch()"
              type="primary"
              icon="el-icon-search"
              size="medium"
            >
              查询
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col>
          <data-statistics-table
            class="table"
            ref="tableRef"
            v-model="queryParams"
          ></data-statistics-table>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>
<script>
import dataStatisticsTable from "./dataStatisticsTable";
import sjid from "@/views/components/cascader/sjid.vue";
import {getMarkedSsbmids} from "@/api/app/monthlyComplianceInfo/dataStatistics";
import leeQuarterPicker from "@/components/elementUI/lee-quarter-picker/index.vue";

export default {
  components: {leeQuarterPicker, dataStatisticsTable, sjid},
  props: [],
  data() {
    return {
      isLoading: false,
      // 所属部门数组
      ssbmidArr: [],
      // 季度选择器对象
      ssjdObj: {},
      // 查询参数
      queryParams: {
        page: 1,
        rows: 10,
        ssyf: this.defaultValue(),
        // ssjd: this.defaultValue(),
        ssbmid: null,
      },

      queryRules: {
        ssjd: [
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
      // 有违规事件的组织ID
      markedSsbmids: [],
    };
  },
  computed: {
    isAnyMarkedTrue() {
      return this.formData.tdydhgxxWgsjlx.some(
        (item) => item.isMarked === true
      );
    },
  },
  watch: {},
  created() {
  },
  mounted() {
  },
  methods: {
    // 修改部门时更新查询参数
    handleChangeSjid(val) {
      console.log(val)
      if (val.length > 1) {
        this.queryParams.ssbmid = val[val.length - 1]
      } else {
        this.queryParams.ssbmid = val[0]
      }
    },

    // 获取默认查询日期
    defaultValue() {
      const date = new Date();
      const year = date.getFullYear(); // 获取当前年份
      let month = date.getMonth() + 1; // 获取当前月份（1-12），并加1因为getMonth()返回0-11
      month = month < 10 ? '0' + month : month; // 如果月份小于10，前面加一个'0'
      return `${year}-${month}`; // 返回格式化的字符串
    },

    dateChange() {
      this.onSearch();
    },

    onSearch() {
      this.$refs["queryForm"].validate((valid) => {
        this.$refs.tableRef.handelSearch()
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
