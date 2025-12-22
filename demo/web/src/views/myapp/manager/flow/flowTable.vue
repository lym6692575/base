<!-- 接口获取数据的table和分页器 -->
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
            @click="handleEdit(scope.$index, scope.row)"
          >
            {{ scope.row.tdywlcHjList && scope.row.tdywlcHjList.length > 0 ? '流程图' : '流程图' }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template slot-scope="scope">
          <el-popconfirm
            title="确定删除该流程？"
            @confirm="handleDelete(scope.$index, scope.row)"
          >
            <el-button slot="reference" size="mini" type="danger"
            >删除
            </el-button
            >
          </el-popconfirm>
        </template>
      </el-table-column>
    </lee-table>

    <!--
      编辑对话框
      传递查询方法
    -->
    <flow-edit ref="editRef" @data-saved="handelSearch()"/>
  </div>
</template>

<script>
import flowEdit from "./flowEdit";
import {getTableData, deleteTDywlc} from "@/api/app/manager/flow";
import {mapGetters} from "vuex";
import leeTable from "@/components/elementUI/lee-table/index.vue";

export default {
  props: {
    // 查询参数
    value: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      rawData: [],
      currentRow: null,
      // 初始搜索条件
      page: 1,
      rows: 10,
      isLoading: false,
    };
  },
  components: {
    leeTable,
    flowEdit,
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
    // index方法
    indexMethods(index) {
      return (this.page - 1) * this.rows + index + 1;
    },

    // 点击流程图显示编辑对话框
    handleEdit(index, row) {
      this.$store.dispatch("flow/setflowFormParams", row);

      let tdywlcGefj = row.tdywlcGefj;
      tdywlcGefj.forEach((item, i) => {
        item.temporaryId = i;
      });

      this.$store.dispatch("flow/setGeneralFileList", tdywlcGefj);
      this.$refs.editRef.openDialog(index);
    },

    // 获取数据 && 数据初始化
    handleGetTableData(params) {
      this.isLoading = true;
      getTableData(params).then((res) => {
        this.rawData = res.data;
        this.isLoading = false;
      });
    },

    // 删除业务流程并刷新表格数据
    handleDelete(_, row) {
      deleteTDywlc(row.id).then((res) => {
        if (res.result) {
          this.$message({
            message: res.msg,
            type: "success",
          });
          // 刷新数据
          this.handelSearch();
        }
      });
    },

    // 分页切换
    handleCurrentChange(val) {
      let params = this.value;
      if (params) {
        params.page = val;
        this.page = val;
      }
      this.handleGetTableData(params);
    },
  },
  mounted() {
    // 初始数据
    const params = {page: this.page, rows: this.rows};
    this.handleGetTableData(params);
  },
  computed: {
    // 处理生数据
    tableData() {
      if (this.rawData && this.rawData.content) {
        const content = this.rawData.content;
        const transformedData = content.map((item) => {
          return {
            ...item,
            ztmc: item.lczt == 1 ? "有效" : "无效",
            lct: "流程图",
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

<style lang="sass" scoped>
.custom-table
  width: 100%
  // 调整表格宽度，可根据需要进行调整
  .el-table__body-wrapper
    overflow: hidden !important
// 隐藏表格内容溢出

.custom-pagination
  margin-top: 20px
  // 调整分页器的上边距，可根据需要进行调整
  display: flex
  justify-content: flex-end
// 将分页器放置在右侧

.link
  color: #409eff
  text-decoration: none

  &:hover
    text-decoration: underline
</style>
