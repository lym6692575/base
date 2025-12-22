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
          <el-link type="primary" @click="handleReply(scope.$index, scope.row)">
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
      <el-table-column label="是否发布" width="100">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.fbzt"
            active-value="1"
            inactive-value="0"
            @change="handlePublishChange(scope.row)"
          >
          </el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template slot-scope="scope">
          <el-popconfirm
            title="确定删除该文件？"
            @confirm="handleDelete(scope.$index, scope.row)"
          >
            <el-button slot="reference" size="mini" type="danger"
            >删除</el-button
            >
          </el-popconfirm>
        </template>
      </el-table-column>
    </lee-table>
    <message-board-reply-dialog
      ref="dialogRef"
      :visible.sync="dialogVisible"
      @data-refresh="handelSearch()"
    ></message-board-reply-dialog>
  </div>
</template>

<script>
import * as XLSX from "xlsx";
import messageBoardReplyDialog from "./messageBoardReplyDialog";
import {
  getTDlybList,
  deleteTDlyb,
  updateTDlybFbzt,
} from "@/api/app/manager/messageBoard";
import { mapGetters } from "vuex";
import leeTable from "@/components/elementUI/lee-table/index.vue";
export default {
  components: {
    leeTable,
    messageBoardReplyDialog,
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
      page: 1,
      rows: 10,
      isLoading: false,
      dialogVisible: false,
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

    // 回复逻辑
    handleReply(index, row) {
      this.dialogVisible = true;
      this.$refs.dialogRef.onInit(index, row);
    },

    // 删除合规文件并刷新表格数据
    handleDelete(_, row) {
      deleteTDlyb(row.id).then((res) => {
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

    // 发布切换
    handlePublishChange(row) {
      console.log(row.id);
      updateTDlybFbzt(row.id).then((res) => {
        if (res.result) {
          this.$message({
            message: res.msg,
            type: "success",
          });
          // 刷新数据
          this.handleGetTableData(this.value);
        }
      });
    },

    // 转换数据为二维数组给导出excel用
    convertDataToAoa(data) {
      // 定义想要保留的列和它们的中文标题
      const columns = [
        { key: "id", title: "序号" },
        { key: "lyzt", title: "留言主题" },
        { key: "lynr", title: "留言内容" },
        { key: "tdlydf", title: "留言答复" },
        { key: "lxr", title: "联系人" },
        { key: "lxdh", title: "联系方式" },
        { key: "cjsj", title: "发布时间" },
      ];

      // 提取列标题
      const headers = columns.map((column) => column.title);

      // 根据列的顺序从每个对象中提取数据
      const rows = data.map((obj, index) =>
        columns.map((column) => {
          // 特殊处理 '序号' 和 '留言答复'
          if (column.key === "id") {
            return index + 1; // 序号
          } else if (column.key === "tdlydf") {
            return obj[column.key] && obj[column.key].length > 0
              ? obj[column.key][0].dfjlynr
              : ""; // 取第一条留言答复
          } else {
            return obj[column.key]; // 其他列
          }
        })
      );

      // 在行数组的开头添加标题
      rows.unshift(headers);

      return rows;
    },

    // 导出表格
    getExcel() {
      const aoa = this.convertDataToAoa(this.tableData.content);
      console.log(aoa);
      const worksheet = XLSX.utils.aoa_to_sheet(aoa);
      const workbook = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(workbook, worksheet, "Sheet1");
      XLSX.writeFile(workbook, "留言板.xlsx");
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
