<template>
  <div>
<!--    <h1>queryParams</h1>-->
<!--    {{ queryParams }}-->
<!--    <h1>multipleSelection</h1>-->
<!--    {{ multipleSelection }}-->
    <el-row :gutter="15">
      <el-form
        label-position="left"
        ref="elForm"
        :model="queryParams"
        size="medium"
        label-width="90px"
      >
        <el-col :span="10">
          <el-form-item label="流程名称：" prop="lcmc">
            <lee-input v-model="queryParams.lcmc"></lee-input>
          </el-form-item>
        </el-col>
        <el-col :span="10">
          <el-form-item label="业务类别：" prop="ywlbmc">
            <ywlb v-model="queryParams.ywlbid"/>
          </el-form-item>
        </el-col>
        <el-col :span="4">
          <el-form-item label-width="0" prop="field103">
            <el-button
              type="primary"
              icon="el-icon-search"
              size="medium"
              @click="onSearch"
            >
              查询
            </el-button>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-row>
            <!-- 表格 -->
            <el-table
              class="custom-table"
              ref="multipleTable"
              :data="tableData.content"
              tooltip-effect="dark"
              style="width: 100%"
              @selection-change="handleSelectionChange"
              :row-key="bindRowKeys"
              v-loading="isLoading"
            >
              <el-table-column type="selection" :reserve-selection="true" min-width="10%">
              </el-table-column>
              <el-table-column
                type="index"
                min-width="10%"
                label="序号"
              ></el-table-column>
              <el-table-column
                property="ywlbmc"
                label="业务类别"
                min-width="30%"
              >
              </el-table-column>
              <el-table-column property="lcmc" label="流程名称" min-width="40%">
              </el-table-column>
            </el-table>
            <!-- 分页器 -->
            <el-pagination
              class="custom-pagination"
              v-if="tableData['totalElements'] > queryParams.rows"
              background
              layout="prev, pager, next"
              :total="tableData['totalElements']"
              :page-sizes="[5, 10, 15, 20]"
              @current-change="handleCurrentChange"
            >
            </el-pagination>
          </el-row>
        </el-col>
<!--        <el-col :span="24">-->
<!--          <el-form-item class="custom-button-box mt20" size="large" v-if="postType!=='save'">-->
<!--            <el-popconfirm-->
<!--              title="确定保存？该操作关闭对话框并刷新表格数据"-->
<!--              @confirm="submitForm"-->
<!--            >-->
<!--              <el-button slot="reference" type="primary">保存</el-button>-->
<!--            </el-popconfirm>-->
<!--          </el-form-item>-->
<!--        </el-col>-->
      </el-form>
    </el-row>
  </div>
</template>
<script>
import leeInput from "@/components/elementUI/el-input";
import {ywlb, xldj, znbm} from "@/components/select";
import {mapGetters} from "vuex";
import {getTableData} from "@/api/app/manager/flow";
import {updataFlowList} from "@/api/app/manager/file";

export default {
  components: {
    leeInput,
    ywlb,
  },
  props: {
    fileFormData: {
      type: Object,
      default: () => {
      },
    },
    postType: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      queryParams: {
        page: 1,
        rows: 10,
        lcmc: "",
        ywlbid: 'SELECTALL',
      },
      rawData: [],
      isLoading: false,
      multipleSelection: [],
    };
  },
  computed: {
    ...mapGetters(["ywlb"]),
    tableData() {
      if (this.rawData && this.rawData.content) {
        const content = this.rawData.content;
        const transformedData = content.map((item) => {
          return {
            ...item,
            ywlbmc: this.findYwlbmc(item.ywlbid),
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
  watch: {},
  created() {
  },
  mounted() {
    console.log("流程mounted")
    // this.openDialogCallback()
  },
  methods: {
    // 保存选中的数据id,row-key就是要指定一个key标识这一行的数据
    bindRowKeys(row) {
      return row.id
    },

    // 清除选择
    clearSelect() {
      this.multipleSelection = []
      this.$refs.multipleTable.clearSelection()
    },

    // 打开对话框的回调函数
    async openDialog() {
      // 初始化参数
      this.queryParams = {page: 1, rows: 10, lcmc: "", ywlbid: "SELECTALL"}
      // 查询
      try {
        await this.onSearch();
        this.$nextTick(() => {
          this.executeToggleSelection();
        });
      } catch (e) {
      }
    },

    // 初始化选择根据props传过来的数据标记数据
    executeToggleSelection() {
      const selectedTdywlcList = this.fileFormData.tdywlc;
      const tdywlcList = this.tableData.content;
      const filteredList = tdywlcList.filter((item) =>
        selectedTdywlcList.some((selectedItem) => selectedItem.id === item.id)
      );
      this.toggleSelection(filteredList);
    },

    // 查询
    onSearch() {
      return this.handleGetTableData(this.queryParams);
    },

    // 获取数据 && 数据初始化
    handleGetTableData(params) {
      this.isLoading = true;
      const selectedTdywlc = this.fileFormData.tdywlc;
      let selectedId = selectedTdywlc ? selectedTdywlc.map(item => ({id: item.id})) : [];
      params.isdelete = 0;

      // 返回 Promise
      return getTableData(params, selectedId).then(res => {
        this.rawData = res.data;
        this.isLoading = false;
      });
    },

    // 分页切换
    handleCurrentChange(val) {
      let queryParams = this.queryParams;
      if (queryParams) {
        queryParams.page = val;
      }
      this.handleGetTableData(queryParams);
    },

    // 选择的方法
    toggleSelection(rows) {
      if (rows) {
        rows.forEach((row) => {
          this.$refs.multipleTable.toggleRowSelection(row);
        });
      } else {
        this.$refs.multipleTable.clearSelection();
      }
    },

    handleSelectionChange(val) {
      this.multipleSelection = val;
    },

    // 提交
    submitForm() {
      const id = this.fileFormData.id;
      const tDywlcList = this.multipleSelection;
      updataFlowList(id, tDywlcList).then((res) => {
        if (res.result) {
          this.$message({
            message: res.msg,
            type: "success",
          });
          // 关闭对话框保证数据的刷新
          this.$emit("close");
        }
      });
    },

    // 寻找业务类别名称
    findYwlbmc(id) {
      const found = this.ywlb.data.find((item) => {
        return item.id == id;
      });
      if (found) {
        return found.lbmc;
      } else {
        return "无";
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
.custom-button-box
  float: right

// 居中设置
.custom-table > > > th
  text-align: center

.custom-table > > > td
  text-align: center
</style>
