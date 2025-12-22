<!-- 
  vuex本地数据table和分页 
-->
<template>
  <div>
    <!-- 表格 -->
    <el-table
      ref="singleTable"
      border
      style="width: 100%"
      stripe
      :data="tableData.content"
      class="mb10"
      v-loading="isLoading"
    >
      <el-table-column
        label="序号"
        type="index"
        min-width="5%"
        :index="indexMethods"
      ></el-table-column>
      <el-table-column property="lbmc" label="类别名称" min-width="15%">
      </el-table-column>
      <el-table-column property="ord" label="显示排序" min-width="15%">
      </el-table-column>
      <el-table-column property="enable" label="是否通用" width="80">
      </el-table-column>
      <el-table-column
        property="cjsj"
        label="创建时间"
        width="120"
      ></el-table-column>
      <el-table-column property="cjr" label="创建人" width="80">
      </el-table-column>
      <el-table-column label="操作" width="138">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleEdit(scope.$index, scope.row)"
            >编辑</el-button
          >
          <el-popconfirm
            title="确定删除该业务类别？"
            @confirm="handleDelete(scope.$index, scope.row)"
          >
            <el-button slot="reference" size="mini" type="danger"
              >删除</el-button
            >
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页器 -->
    <el-pagination
      v-if="tableData.totalElements > rows"
      background
      layout="prev, pager, next"
      :total="tableData.totalElements"
      :page-sizes="[5, 10, 15, 20]"
      @current-change="handleCurrentChange"
    >
    </el-pagination>
    <!-- 编辑对话框 -->
    <fileCategoryDialog ref="editRef" @data-saved="handleGetTableData(value)" />
  </div>
</template>
    
  <script>
import fileCategoryDialog from "./fileCategoryDialog.vue";
import { getTDywlb, deleteTDywlb } from "@/api/app/manager/category";
import { mapGetters } from "vuex";
export default {
  components: {
    fileCategoryDialog,
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
  computed: {
    tableData() {
      if (this.rawData && this.rawData.content) {
        const content = this.rawData.content;
        const transformedData = content.map((item) => {
          return {
            ...item,
            enable: item.sfty == 1 ? "是" : "否",
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
    // index方法
    indexMethods(index) {
      return (this.page - 1) * this.rows + index + 1;
    },

    // 获取数据
    handleGetTableData(params) {
      this.isLoading = true;
      getTDywlb(params).then((res) => {
        this.rawData = res.data;
        this.isLoading = false;
      });
    },

    // 本地模糊查询
    onfuzzySearch(val) {
      console.log(val);
      this.lbmc = val;
    },

    // 编辑
    handleEdit(index, row) {
      this.$refs.editRef.initEditdata(row);
    },

    // 删除
    handleDelete(index, row) {
      deleteTDywlb(row.id).then((res) => {
        if (res.result) {
          this.$message({
            message: res.msg,
            type: "success",
          });
          // 刷新vuex数据
          this.handleGetTableData(this.value);
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

    // 查询到所属上级名称
    findSjmc(sjid) {
      const found = this.ywlb.data.find((item) => {
        return item.id == sjid;
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
};
</script>