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
          {{ scope.row.cjsj.substring(0, 10) }}
        </template>
      </el-table-column>
    </lee-table>
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
import {getTDpxzlList} from "@/api/app/training/index";
// import downloadDialog from "./downloadDialog.vue";
import {mapGetters} from "vuex";
import leeTable from "@/components/elementUI/lee-table/index.vue";

export default {
  components: {
    leeTable,
    // downloadDialog,
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
    // 查询配适器
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

    // 打开对话框
    handleOpenDetailDialog(index, row) {
      this.currentRow = row;
      this.detailDialogVisible = true;
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


    handleEdit(index, row) {
      this.$refs.editRef.openDialog(index, row);
    },
  },
  mounted() {
  },
  computed: {
    ...mapGetters(["fjlx", "znbm", "ywlb", "xldj"]),
  },
};
</script>
