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
            ref="popover"
            placement="bottom"
            trigger="click"
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
      <el-table-column property="sbdw" label="发布单位" min-width="10%"/>
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
      <el-table-column label="操作" width="100">
        <template slot-scope="scope">
<!--          {{scope.row.fksj}}-->
<!--          {{ $moment().isAfter($moment(scope.row.fksj).format('YYYY-MM-DD'), 'day') }}-->
          <el-tooltip
            class="item"
            effect="dark"
            content="超出截止日期"
            placement="top-start"
            :disabled="!($moment().isAfter($moment(scope.row.fksj).format('YYYY-MM-DD'), 'day'))"
          >
            <el-button
              size="mini"
              type="primary"
              @click="handleUpdate(scope.$index, scope.row)"
              :disabled="$moment().isAfter($moment(scope.row.fksj).format('YYYY-MM-DD'), 'day')"
            >上传
            </el-button>
          </el-tooltip>
        </template>
      </el-table-column>
    </lee-table>
    <sbql-dialog ref="sbqkRef"></sbql-dialog>

    <notice-dialog
      ref="editRef"
      :postType="'update'"
      :switchDisabled="false"
      :titleName="'上传材料'"
      @data-saved="handelSearch(value)"
    ></notice-dialog>
  </div>
</template>

<script>
import {getSbtzList, deleteSbtz} from "@/api/app/materialSubmittal/submit";
import fileListDownload from "@/views/components/fileListDownload";
import flowBox from "@/views/components/flowBox";
import {mapGetters} from "vuex";
import noticeDialog from "./noticeDialog";
import sbqlDialog from "./sbqlDialog";
import leeTable from '@/components/elementUI/lee-table/index.vue'

export default {
  props: {
    value: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      nowTime: this.$moment().format("YYYY-MM-DD HH:mm:ss"),
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
    }
    ,

    handleOpenSbqkDialog(index, row) {
      this.$refs.sbqkRef.openDialog(index, row);
    }
    ,

    // 点击弹出编辑对话框
    handleUpdate(index, row) {
      this.$refs.editRef.openDialog(index, row);
    }
    ,

    // 获取数据 && 数据初始化
    handleGetTableData(params) {
      this.isLoading = true;
      getSbtzList(params).then((res) => {
        this.rawData = res.data;
        this.isLoading = false;
      });
    }
    ,

    // 删除合规文件并刷新表格数据
    handleDelete(_, row) {
      deleteSbtz(row.id).then((res) => {
        if (res.result) {
          this.$message({
            message: res.msg,
            type: "success",
          });
          // 刷新数据
          this.handleGetTableData(this.value);
        }
      });
    }
    ,

    // 分页切换
    handleCurrentChange(val) {
      let params = this.value;
      if (params) {
        params.page = val;
        this.page = val;
      }
      this.handleGetTableData(params);
    }
    ,
  },
  mounted() {
    // 初始数据
    this.handleGetTableData(this.value);
    console.log("moment", this.nowTime)
  },
  computed: {
    ...mapGetters(["znbm"]),
    // 处理生数据
    tableData() {
      if (this.rawData && this.rawData.content) {
        const content = this.rawData.content;
        const transformedData = content.map((item) => {
          return {
            ...item,
            count: item.tdclbs.length,
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
    // 生成key值
    generatedKey() {
      const timestamp = new Date().getTime();
      const random = Math.floor(Math.random() * 1000); // 生成一个随机数，范围为0到999
      return timestamp + "_" + random;
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
