<!--最新资料-->
<template>
  <div>
    <el-table
      ref="singleTable"
      :data="tableData.content"
      border
      style="width: 100%"
      stripe
      class="custom-table"
      v-loading="isLoading"
      :header-cell-style="{
        'text-align': 'center',
      }"
      :row-style="{
        height: '0',
        'text-align': 'center',
      }"
      :cell-style="{
        padding: '6px',
        'text-align': 'center',
      }"
    >
      <el-table-column
        type="index"
        min-width="10%"
        label="序号"
        :index="indexMethods"
      />
      <el-table-column property="wjmc" label="文件名称" width="400">
        <template slot-scope="scope">
          <el-link
            type="primary"
            @click="showComplianceDetail(scope.$index, scope.row)"
          >
            {{ scope.row.wjmc }}
          </el-link>
          <table-detail
            ref="detailRef"
            :titleName="'合规文件详情'"
          ></table-detail>
        </template>
      </el-table-column>
      <el-table-column property="wjbh" label="文件编号" width="200" :show-overflow-tooltip="true"/>
      <el-table-column property="fwsj" label="发文时间" width="180"/>
      <el-table-column property="bmmc" label="职能部门" width="200" :show-overflow-tooltip="true">
      </el-table-column>
      <el-table-column property="xldjmc" label="效力等级"/>
      <el-table-column property="ztmc" label="状态"/>
      <el-table-column property="lct" label="流程图 / 链接地址" width="180">
        <template slot-scope="scope">
          <div v-if="scope.row.ljdz === null && scope.row.ljdz === ''">
            <el-link
              :disabled="scope.row.lct !== '流程图'"
              type="primary"
              @click="handleOpenFlowBoxDialog(scope.$index, scope.row)"
            >
              {{ scope.row.lct }}
            </el-link>
            <flow-box ref="flowboxRef"></flow-box>
          </div>
          <div v-else>
<!--            {{ scope.row.ljdz }}-->
            <el-link
              target="_blank"
              type="primary"
              :href="scope.row.ljdz"
            >
              点击跳转
            </el-link>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import flowBox from "@/views/components/flowBox";
import tableDetail from "./tableDetail";
import {getTableData} from "@/api/app/knowledge/compliance";
import {apiUtils, dateUtils, DeepCopyUtils} from "@/utils/LeeUtils";
import {wjlb, ywlb, xldj, znbm, zt} from "@/components/select";
import {mapGetters} from "vuex";

export default {
  components: {
    flowBox,
    tableDetail,
    wjlb,
    xldj,
    znbm,
    zt,
    ywlb,
  },
  props: {},
  data() {
    return {
      rawData: [],
      currentRow: null,
      // 初始搜索条件
      isLoading: false,

      queryParams: {
        page: "1",
        rows: "5",
        wjmc: undefined,
        wjlbid: "",
        ywlbid: "",
        xldjid: "",
        znbmid: "",
        zt: "",
      },

      date: [],
    };
  },
  methods: {
    onSearch() {
      // 防止分页器报错所以赋值为1
      this.queryParams.page = 1;
      // 深拷贝表单防止污染
      let queryParams = DeepCopyUtils.deepCopy(this.queryParams);

      queryParams.startDate = this.date[0];
      queryParams.endDate = this.date[1];
      queryParams.date = [];

      this.handleGetTableData(queryParams);
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
    // 获取数据
    handleGetTableData(params) {
      this.isLoading = true;
      getTableData(params).then((res) => {
        this.rawData = res.data;
        this.isLoading = false;
      });
    },

    // index方法
    indexMethods(index) {
      return (this.queryParams.page - 1) * this.queryParams.rows + index + 1;
    },

    // 打开流程图浏览对话框
    handleOpenFlowBoxDialog(index, row) {
      this.$refs.flowboxRef.openDialog(index, row);
    },

    // 查看文件细节
    showComplianceDetail(index, row) {
      this.$refs.detailRef.openDialog(index, row);
    },

    // 分页切换
    handleCurrentChange(val) {
      let queryParams = DeepCopyUtils.deepCopy(this.queryParams);
      if (queryParams) {
        queryParams.page = val;
        this.queryParams.page = val;
      }
      this.handleGetTableData(queryParams);
    },

    // 寻找职能部门名称
    findZnbmmc(id) {
      if (this.znbm && this.znbm.length > 0) {
        const found = this.znbm.data.find((item) => {
          return item.id === id;
        });
        if (found) {
          return found.bmmc;
        } else {
          return "无";
        }
      }
    },
  },
  mounted() {
    // 初始数据
    this.handleGetTableData(this.queryParams);
  },
  computed: {
    ...mapGetters(["znbm"]),
    tableData() {
      if (this.rawData && this.rawData.content) {
        const content = this.rawData.content;
        const transformedData = content.map((item) => {
          return {
            ...item,
            ztmc: item.zt !== 1 ? "有效" : "废止",
            lct:
              item.tdywlc && item.tdywlc.length > 0 ? "流程图" : "",
          };
        });

        return {
          ...this.rawData,
          content: transformedData,
        };
      } else {
        return []; // 或其他默认值
      }
    },
  },
};
</script>

<style scoped>
.lee-form >>> .el-form-item {
  margin-bottom: 14px;
}
</style>
