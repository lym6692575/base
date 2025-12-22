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

      <el-table-column property="clmc" label="材料名称" min-width="12%">
      </el-table-column>
      <el-table-column property="xgyq" label="上报要求" min-width="20%"/>
      <el-table-column property="fwsj" label="附件" width="100">
        <template slot-scope="scope">
          <el-popover
            ref="elPopover"
            placement="bottom"
            trigger="click"
            :teleported="false"
            @after-enter="handleFixPosition"
          >
            <div style="width:600px; max-height: 400px; overflow: auto">
              <file-list-download
                :fileType="'sbtz'"
                ref="upload"
                :fileList="scope.row.fjList"
                :showSubList="false"
              ></file-list-download>
            </div>
            <el-button slot="reference" type="text">附件列表</el-button>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column property="znbm.bmmc" label="发布单位" min-width="10%"/>
      <el-table-column property="fbsj" label="发布时间" width="120"/>
      <el-table-column property="fksj" label="截至日期" width="120"/>
      <el-table-column property="count" label="上报情况" width="90">
        <template slot-scope="scope">
          <el-link
            type="primary"
            @click="handleOpenSbqkDialog(scope.$index, scope.row)"
          >
            {{ scope.row.tdclbs.length }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="scope">
          <el-button
            slot="reference"
            size="mini"
            @click="handleEdit(scope.$index, scope.row)"
          >编辑
          </el-button
          >
          <el-popconfirm
            title="确定删除该通知？"
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
    <sbql-dialog
      ref="sbqkRef"
      @data-saved="handelSearch()"
    >
    </sbql-dialog>
    <notice-dialog
      ref="editRef"
      :postType="'update'"
      :switchDisabled="false"
      :titleName="'编辑上报通知'"
      @data-saved="handelSearch()"
    ></notice-dialog>
  </div>
</template>

<script>
import {getSbtzList, deleteSbtz} from "@/api/app/materialSubmittal";
import fileListDownload from "@/views/components/fileListDownload";
import flowBox from "@/views/components/flowBox";
import {mapGetters} from "vuex";
import noticeDialog from "./noticeDialog";
import sbqlDialog from "./sbqlDialog";
import leeTable from "@/components/elementUI/lee-table/index.vue";
import {getTableData} from "@/api/app/knowledge/businessFlow";

export default {
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
    };
  },
  components: {
    leeTable,
    fileListDownload,
    noticeDialog,
    sbqlDialog,
    flowBox,
  },
  methods: {
    // 修复位置问题
    handleFixPosition() {
      this.$refs.elPopover.updatePopper() // 注意主要是这里
    },
    // 查询配适器
    async getTableDataAdapter(params) {
      try {
        const response = await getSbtzList(params)
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

    handleOpenSbqkDialog(index, row) {
      this.$refs.sbqkRef.openDialog(index, row);
    },

    // 点击弹出编辑对话框
    handleEdit(index, row) {
      this.$refs.editRef.openDialog(index, row);
    },

    // 删除合规文件并刷新表格数据
    handleDelete(_, row) {
      deleteSbtz(row.id).then((res) => {
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
  },
  computed: {
    ...mapGetters(["znbm"]),
  },
};
</script>
