<template>
  <div>
    <lee-table
      ref="leeTable"
      :fetch-data="getTableDataAdapter"
      :query-params="value"
      v-bind="$attrs"
      v-on="$listeners"
    >

      <el-table-column label="所属月份" property="ssyf">
<!--        <template slot-scope="scope">-->
<!--          {{ scope.row.ssjd.substring(0,4) +'年 第'+scope.row.ssjd.substring(5)+'季度'}}-->
<!--        </template>-->
      </el-table-column>
      <el-table-column property="test" label="所属部门">
        <template slot-scope="scope">
          {{ findHierarchy(scope.row.ssbmid) }}
        </template>
      </el-table-column>
      <el-table-column property="tbr" label="填报人" width="120"></el-table-column>
      <el-table-column label="查看详情" width="180">
        <template slot-scope="scope">
          <el-link type="primary" @click="handleEdit(scope.$index, scope.row)">
            查看
          </el-link>
        </template>
      </el-table-column>
    </lee-table>
    <data-statistics-dialog
      :visible.sync="dialogVisible"
      ref="editRef"
    ></data-statistics-dialog>
  </div>
</template>

<script>
import dataStatisticsDialog from "./dataStatisticsDialog";
import {getYdhgxxList} from "@/api/app/monthlyComplianceInfo/dataStatistics";
import {DeepCopyUtils, dateUtils, searchUtils} from "@/utils/LeeUtils";
import {mapGetters} from "vuex";
import leeTable from "@/components/elementUI/lee-table/index.vue";
import {getAdminList} from "@/api/app/manager/admin";

export default {
  components: {
    leeTable,
    dataStatisticsDialog,
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
  props: {
    value: {
      type: Object,
      required: true,
    },
  },
  computed: {
    ...mapGetters(["znbm"]),
    tableData() {
      if (this.rawData && this.rawData.content) {
        const content = this.rawData.content;
        const transformedData = content.map((item) => {
          return {
            ...item,
            enable: item.sign === 1 ? "是" : "否",
            // 上级部门名称
            test: this.findHierarchy(item.ssbmid),
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
    // 查询配适器
    async getTableDataAdapter(params) {
      try {
        const response = await getYdhgxxList(params)
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

    // 获取数据
    handleGetTableData(params) {
      this.isLoading = true;

      getYdhgxxList(params)
        .then((response) => {
          this.rawData = response.data;
          this.isLoading = false;
        })
        .catch((error) => {
          this.$message({
            message: error,
            type: "warning",
          });
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

    // 编辑
    handleEdit(index, row) {
      this.dialogVisible = true;
      this.$refs.editRef.onInit(index, row);
    },

    // 删除
    handleDelete(index, row) {
      deleteTDgwgl(row.id).then((res) => {
        if (res.result) {
          this.$message({
            message: res.msg,
            type: "success",
          });
          // 刷新数据
          this.handleGetTableData(this.value);
          // this.eventBus.$emit("refresh");
        }
      });
    },

    // 寻找职能部门名称
    findHierarchy(id, topLevel = true) {
      let hierarchy = [];
      const data = DeepCopyUtils.deepCopy(this.znbm.data);
      for (let i = 0; i < data.length; i++) {
        if (data[i].id === id) {
          hierarchy.push(data[i].bmmc);
          if (data[i].sjid !== null) {
            // Combine the arrays
            hierarchy = this.findHierarchy(data[i].sjid, false).concat(
              hierarchy
            );
          }
          break;
        }
      }

      return topLevel ? hierarchy.join(" / ") : hierarchy;
    },
  },

  mounted() {
  },
};
</script>
