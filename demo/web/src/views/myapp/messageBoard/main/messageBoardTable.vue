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
      <el-table-column property="lyzt" label="留言主题">
      </el-table-column>
      <el-table-column property="lynr" label="留言内容">
        <template slot-scope="scope">
          <el-link type="primary" @click="handleShowDetail(scope.$index, scope.row)">
            {{ scope.row.lynr }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column
        property="cjsj"
        label="发布时间"
        min-width="20%"
      >
        <template slot-scope="scope">
          {{ scope.row.cjsj.substring(0, 10)}}
        </template>
      </el-table-column>
    </lee-table>
    <message-board-detail-dialog
      ref="dialogRef"
      :visible.sync="dialogVisible"
    ></message-board-detail-dialog>
  </div>
</template>

<script>
import { getTDlybList } from "@/api/app/messageBoard";
import messageBoardDetailDialog from "./messageBoardDetailDialog";
import { mapGetters } from "vuex";
import leeTable from "@/components/elementUI/lee-table/index.vue";
export default {
  components: {leeTable, messageBoardDetailDialog },
  props: {
    value: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      dialogVisible: false,
      rawData: [],
      currentRow: null,
      // 初始搜索条件
      page: 1,
      rows: 10,
      isLoading: false,
      value1: true,
    };
  },
  methods: {
    // 查询配适器
    async getTableDataAdapter(params) {
      try {
        const response = await getTDlybList(params)
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

    // 获取数据 && 数据初始化
    handleGetTableData(params) {
      this.isLoading = true;
      getTDlybList(params).then((res) => {
        this.rawData = res.data;
        this.isLoading = false;
      });
    },

    // 详情对话框
    handleShowDetail(index, row) {
      this.dialogVisible = true;
      this.$refs.dialogRef.onInit(index, row);
      console.log(row);
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

    // 寻找职能部门名称
    findZnbmmc(id) {
      const found = this.znbm.data.find((item) => {
        return item.id == id;
      });
      if (found) {
        return found.bmmc;
      } else {
        return "无";
      }
    },
  },
  mounted() {
    // 初始数据
    this.handleGetTableData(this.value);
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
            cjsjDisplay: item.cjsj.substring(0, 10),
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
  width: 100% // 调整表格宽度，可根据需要进行调整
  .el-table__body-wrapper
    overflow: hidden !important // 隐藏表格内容溢出

.custom-pagination
  margin-top: 20px // 调整分页器的上边距，可根据需要进行调整
  display: flex
  justify-content: flex-end // 将分页器放置在右侧

.link
  color: #409eff
  text-decoration: none
  &:hover
    text-decoration: underline
</style>
