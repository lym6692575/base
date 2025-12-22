<template>
  <div>
    <!-- 表格 -->
    <h5 class="title">
      <i class="el-icon-s-help"></i>
      <span class="mr20">业务流程：</span>
      <div class="title-border"></div>
    </h5>
    <lee-table
      ref="leeTable"
      :fetch-data="getTableDataAdapter"
      :query-params="value"
      v-bind="$attrs"
      v-on="$listeners"
      :auto-loading="false"
      height="230px"
      :initialSize="5"
    >
      <el-table-column property="lcmc" label="流程名称">
      </el-table-column>
      <el-table-column
        property="cjsj"
        label="发布时间"
        width="120"
      >
        <template v-slot="scope">
          {{scope.row.cjsj.substring(0,10)}}
        </template>
      </el-table-column>
      <el-table-column
        property="tdywlb.lbmc"
        label="业务类别"
      ></el-table-column>
      <el-table-column
        property="enable"
        label="状态"
        width="120"
      >
        <template v-slot="scope">
          {{scope.row.lczt == 1 ? "有效" : "废止"}}
        </template>
      </el-table-column>
      <el-table-column
        property="lct"
        label="流程图"
        width="120"
      >
        <template v-slot="scope">
          <el-link
            type="primary"
            @click="handleOpenFlowBoxDialog(scope.$index, scope.row)"
          >
            {{ scope.row.tdywlcHjList && scope.row.tdywlcHjList.length > 0 ? '流程图' : '' }}
          </el-link>
        </template>
      </el-table-column>
    </lee-table>

    <flow-box ref="flowboxRef"></flow-box>
  </div>
</template>

<script>
import { getYwlcTableData } from '@/api/app/knowledge/responsibility'
import { DeepCopyUtils, dateUtils } from "@/utils/LeeUtils";
import { mapGetters } from "vuex";
import flowBox from "@/views/components/flowBox";
import leeTable from '@/components/elementUI/lee-table/index.vue'
export default {
  inject: ["eventBus"],
  components: { leeTable, flowBox },
  data() {
    return {
      dialogVisible: false,
    };
  },
  props: {
    value: {
      type: Object,
      required: true,
    },
  },
  computed: {
    ...mapGetters(["znbm"]),
    tableData() {
      if (this.rawData) {
        const content = this.rawData;
        const transformedData = content.map((item) => {
          return {
            ...item,
            displayDate: item.cjsj.substring(0, 10),
            enable: item.lczt == 1 ? "有效" : "废止",
            lct:
              item.tdywlcHjList && item.tdywlcHjList.length > 0
                ? "流程图"
                : "流程图未创建",
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
  methods: {
    // 查询配适器
    async getTableDataAdapter(params) {
      let _params = DeepCopyUtils.deepCopy(params);
      if (_params.gwglid.length > 0) {
        _params.gwglid = _params.gwglid[_params.gwglid.length - 1];
      } else {
        _params.gwglid = _params.gwglid[0];
      }
      console.log("_params",_params)
      try {
        const response = await getYwlcTableData(_params)
        console.log(response)
        return {
          data: response.data.content, // 适应到你的数据结构
          total: response.data.totalElements // 适应到你的数据结构
        }
      } catch (error) {
        this.$message({
          message: error,
          type: 'warning'
        })
        throw error
      }
    },

    onSearch() {
      this.$refs.leeTable.getData()
    },

    // 获取数据
    handleGetTableData() {
      this.isLoading = true;
      let params = DeepCopyUtils.deepCopy(this.value);
      if (params.gwglid.length > 0) {
        params.gwglid = params.gwglid[params.gwglid.length - 1];
      } else {
        params.gwglid = params.gwglid[0];
      }
      getYwlcTableData(params).then((res) => {
        console.log(res);
        this.rawData = res.data;
        this.isLoading = false;
      });
    },

    // 打开hgwj对话框
    handleShowDialog(index, row) {
      this.dialogVisible = true;
      this.$refs.dialogRef.onInit(index, row);
    },

    // 打开流程图浏览对话框
    handleOpenFlowBoxDialog(index, row) {
      // 懒得改父组件接受数据了先这么对付用
      let formData = { tdywlc: [JSON.parse(JSON.stringify(row))] };
      this.$refs.flowboxRef.openDialog(index, formData);
    },
  },

  mounted() {},
};
</script>

<style lang="sass" scoped>
.title
  color: #515A6e
  &-border
    border-bottom: 1px dashed #909399
    margin-top: 20px
    opacity: 0.4
</style>
