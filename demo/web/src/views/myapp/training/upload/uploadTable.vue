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
      <el-table-column property="zlmc" label="资料名称">
      </el-table-column>
      <el-table-column property="fjwj" label="附件文件">
        <template slot-scope="scope">
          <el-link
            :disabled="(scope.row.tdpxzlFj && scope.row.tdpxzlFj.length > 0)===false"
            type="primary"
            @click="handleOpenDetailDialog(scope.$index, scope.row)"
          >
            {{
              scope.row.tdpxzlFj && scope.row.tdpxzlFj.length > 0
                ? "附件详情"
                : "未上传附件"
            }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column
        property="cjr"
        label="上传人"
      ></el-table-column>
      <el-table-column
        property="cjsj"
        label="上传时间"
      >
        <template slot-scope="scope">
          {{ scope.row.cjsj.substring(0, 10)}}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="scope">
          <el-button
            size="mini"
            @click="handleEdit(scope.$index, scope.row)"
          >编辑</el-button
          >
          <el-popconfirm
            title="确定删除该资料？"
            @confirm="handleDelete(scope.$index, scope.row)"
          >
            <el-button slot="reference" size="mini" type="danger"
            >删除</el-button
            >
          </el-popconfirm>
        </template>
      </el-table-column>
    </lee-table>
    <upload-dialog
      ref="editRef"
      postType="'update'"
      :titleName="'编辑交流资料'"
      @data-saved="handelSearch()"
    ></upload-dialog>
    <el-dialog
      :title="'附件详情'"
      :visible.sync="detailDialogVisible"
      width="50%"
    >
      <file-list-download
        :fileType="'pxzl'"
        :fileList="currentRow ? currentRow.tdpxzlFj : []"
        :show-sub-list="false"
      ></file-list-download>
    </el-dialog>
  </div>
</template>

<script>
import fileListDownload from "@/views/components/fileListDownload";
import { getTDpxzlList, deleteTDpxzl } from "@/api/app/training/index";
import uploadDialog from "./uploadDialog.vue";
import { mapGetters } from "vuex";
import leeTable from "@/components/elementUI/lee-table/index.vue";
export default {
  components: {
    leeTable,
    uploadDialog,
    fileListDownload,
  },
  props: {
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
      detailDialogVisible: false,
    };
  },
  methods: {
    async getTableDataAdapter(params) {
      try {
        const response = await getTDpxzlList(params)
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

    handleOpenDetailDialog(index, row) {
      this.currentRow = row;
      this.detailDialogVisible = true;
    },
    // index方法
    indexMethods(index) {
      console.log(index);
      console.log("page", this.page);
      return (this.page - 1) * this.rows + index + 1;
    },
    // 获取数据
    handleGetTableData(params) {
      this.isLoading = true;
      getTDpxzlList(params).then((res) => {
        console.log(res);
        this.rawData = res.data;
        this.isLoading = false;
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

    // 寻找附件类型名称
    findFjlxmc(id) {
      if (this.fjlx) {
        const found = this.fjlx.data.find((item) => {
          return item.id == id;
        });
        if (found) {
          return found.lxmc;
        } else {
          return "无";
        }
      }
    },

    // 打开对话框
    handleEdit(index, row) {
      this.$refs.editRef.openDialog(index, row);
    },

    // 删除合规文件并刷新表格数据
    handleDelete(_, row) {
      deleteTDpxzl(row.id).then((res) => {
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
  },
  mounted() {

    // 初始数据
    const params = { page: this.page, rows: this.rows };
    this.handleGetTableData(params);
  },
  computed: {
    ...mapGetters(["fjlx", "znbm", "ywlb", "xldj"]),
    tableData() {
      let transformedData = [];
      if (this.rawData && this.rawData.content) {
        const content = this.rawData.content;
        transformedData = content.map((item) => {
          return {
            ...item,
            fjxq:
              item.tdpxzlFj && item.tdpxzlFj.length > 0
                ? "附件详情"
                : "未上传附件",
            cjsj: item.cjsj.substring(0, 10),
          };
        });
      }

      return {
        ...(this.rawData || {}), // 处理原始数据为空的情况
        content: transformedData,
      };
    },
  },
};
</script>
<style lang="sass" scoped>
.fjmc
  padding: 10px 0
  border-bottom: 1px solid #dfe6ec

  &:last-child
    border-bottom: none
</style>
