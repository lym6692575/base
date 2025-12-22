<template>
  <div>
    <lee-table
      ref="leeTable"
      :fetch-data="getTableDataAdapter"
      :query-params="value"
      v-bind="$attrs"
      v-on="$listeners"
      :initialSize="20"
      :pageSizes="[20, 50, 100, 200]"
    >
      <el-table-column property="lcmc" label="流程名字">
      </el-table-column>
      <el-table-column property="cjsj" label="发布时间">
        <template slot-scope="scope">
          {{ scope.row.cjsj.substring(0, 10) }}
        </template>
      </el-table-column>
      <el-table-column property="tdywlb.lbmc" label="业务类别"/>
      <el-table-column
        property="lct"
        label="流程图"
      >
        <template slot-scope="scope">
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
import flowBox from "@/views/components/flowBox";
import complianceDetail from "./complianceDetail";
import {getTableData} from "@/api/app/knowledge/businessFlow";
import {mapGetters} from "vuex";
import leeTable from "@/components/elementUI/lee-table";
import {DeepCopyUtils} from "@/utils/LeeUtils";

export default {
  components: {
    leeTable,
    flowBox,
    complianceDetail,
  },
  props: {
    value: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {};
  },
  methods: {
    // 查询配适器
    async getTableDataAdapter(params) {
      try {
        const response = await getTableData(params)
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

    // 查
    handelSearch() {
      this.$refs.leeTable.getData()
    },

    // 打开流程图浏览对话框
    handleOpenFlowBoxDialog(index, row) {
      // 懒得改父组件接受数据了先这么对付用
      let formData = {tdywlc: [JSON.parse(JSON.stringify(row))]};
      this.$refs.flowboxRef.openDialog(index, formData);
    },


    // 寻找业务类别名称
    findYwlbmc(id) {
      if (this.ywlb) {
        const found = this.ywlb.data.find((item) => {
          return item.id == id;
        });
        if (found) {
          return found.lbmc;
        } else {
          return "无";
        }
      }
    },
  },

  mounted() {
  },

  computed: {
    ...mapGetters(["znbm"]),
  },
};
</script>
