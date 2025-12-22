<!-- todo -->
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
      <el-table-column property="tbr" label="填报人" width="120">
        <template slot-scope="scope">
          {{ scope.row.tbr == null ? "未填报" : scope.row.tbr }}
        </template>
      </el-table-column>
      <el-table-column property="sftb" label="是否填报" width="120">
        <template slot-scope="scope">
          <el-link
            :disabled="scope.row.byzdgz === null"
            type="primary"
            @click="handleEdit(scope.$index, scope.row)"
          >
            {{ scope.row.byzdgz === null ? "未填报" : "查看" }}
          </el-link>
        </template>
      </el-table-column>
    </lee-table>
    <highlight-statistics-dialog
      :visible.sync="dialogVisible"
      ref="editRef"
    ></highlight-statistics-dialog>
  </div>
</template>

<script>
import * as XLSX from "xlsx";
import XLSXST from "xlsx-js-style";
import highlightStatisticsDialog from "./highlightStatisticsDialog";
import {getYdhgxxList} from "@/api/app/monthlyComplianceInfo/highlightStatistics";
import {DeepCopyUtils, dateUtils, searchUtils} from "@/utils/LeeUtils";
import {mapGetters} from "vuex";
import leeTable from "@/components/elementUI/lee-table/index.vue";

export default {
  components: {
    leeTable,
    highlightStatisticsDialog,
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
      if (this.$refs.leeTable && this.$refs.leeTable.internalData) {
        const content = this.$refs.leeTable.internalData;
        console.log("content", content)
        const transformedData = content.map((item) => {
          return {
            ...item,
            enable: item.sign === 1 ? "是" : "否",
            // 上级部门名称
            sjbmmc: this.findHierarchy(item.ssbmid),
            tbr: item.tbr == null ? "未填报" : item.tbr,
            // 是否填报
            sftb: item.byzdgz == null ? "未填报" : "查看",
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
          // this.$message({
          //   message: response.msg,
          //   type: "success",
          // });
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

    // 转换数据为二维数组给导出excel用
    convertDataToAoa(data) {
      console.log(data);
      // 定义要保留的列和它们的中文标题
      const columns = ["序号", "ssyf", "sjbmmc", "byzdgz", "tbr", "sftb"];
      const columnLabels = [
        "序号",
        "月份",
        "所属部门",
        "工作进展及亮点",
        "填报人",
        "是否填报",
      ];

      // 根据列的顺序从每个对象中提取数据
      const rows = data.map((obj, index) =>
        columns.map((column) => {
          // 特殊处理 "序号"，"tbr" 和 "sftb"
          if (column === "序号") {
            return index + 1; // 序号
          } else if (column === "byzdgz") {
            return obj[column] || "未填报";
          } else if (column === "tbr") {
            return obj[column] || "未填报"; // 如果 'tbr' 为空，则返回 '未填报'
          } else if (column === "sftb") {
            return obj[column] === "查看" ? "已填报" : obj[column]; // 如果 'sftb' 为 '查看'，则返回 '已填报'，否则返回原值
          } else {
            return obj[column]; // 其他列
          }
        })
      );

      // 在行数组的开头添加标题
      rows.unshift(columnLabels);

      return rows;
    },

    // 导出表格
    getExcel() {
      // https://blog.csdn.net/qq_40837982/article/details/131601478?utm_medium=distribute.pc_relevant.none-task-blog-2~default~baidujs_baidulandingword~default-4-131601478-blog-132669100.235^v40^pc_relevant_anti_vip_base&spm=1001.2101.3001.4242.3&utm_relevant_index=7
      let content = this.$refs.leeTable.internalData

      const transformedData = content.map((item) => {
        return {
          ...item,
          enable: item.sign === 1 ? "是" : "否",
          // 上级部门名称
          sjbmmc: this.findHierarchy(item.ssbmid),
          tbr: item.tbr == null ? "未填报" : item.tbr,
          // 是否填报
          sftb: item.byzdgz == null ? "未填报" : "查看",
        };
      });

      const aoa = this.convertDataToAoa(transformedData);
      console.log(aoa);
      // const workbook = XLSX.utils.book_new();
      // const worksheet = XLSX.utils.aoa_to_sheet(aoa);
      const worksheet = XLSXST.utils.aoa_to_sheet(aoa);
      for (const key in worksheet) {
        if (worksheet[key] instanceof Object) {
          worksheet[key].s = {
            alignment: {
              vertical: 'center',
              horizontal: 'center',
              indent: 0,
              wrapText: true
            },
            font: {
              name: '宋体',
              sz: 12,
              color: { rgb: '#FF000000' },
              bold: false,
              italic: false,
              underline: false
            },
            border: {
              top: { style: 'thin' },
              bottom: { style: 'thin' },
              left: { style: 'thin' },
              right: { style: 'thin' }
            }
          }
        }
      }
      worksheet['!cols'] = [
        {
          wpx: 60
        },
        {
          wpx: 300
        },
        {
          wpx: 300
        },
        {
          wpx: 100
        },
        {
          wpx: 60
        },
        {
          wpx: 60
        }
      ]
      const workbook = XLSXST.utils.book_new();
      XLSXST.utils.book_append_sheet(workbook, worksheet, "Sheet1");
      XLSXST.writeFile(workbook, "月度亮点统计.xlsx");
    },
    mounted() {
    },
  }
};
</script>
