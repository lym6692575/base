<template>
  <div>
    <!-- 表格 -->
    <lee-table
        ref="leeTable"
        :fetch-data="getTableDataAdapter"
        :query-params="value"
        v-bind="$attrs"
        v-on="$listeners"
        :initialSize="20"
        :pageSizes="[20, 50, 100, 200]"
    >
      <el-table-column property="spmc" label="视频名称" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column property="spljdz" label="视频链接地址" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column property="cjsj" label="上传日期" width="120"></el-table-column>
      <el-table-column property="px" label="排序" width="100"></el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="scope">
          <el-button
              size="mini"
              @click="handleEdit(scope.$index, scope.row)"
          >编辑
          </el-button
          >
          <el-popconfirm
              title="确定删除该视频链接？"
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
    <videos-main-dialog
        :visible.sync="dialogVisible"
        ref="dialogRef"
        postType="edit"
        :gwglid="gwglid"
        @data-refresh="handelSearch(value)"
    />
  </div>
</template>

<script>
import {getGwspList, deleteGwsp} from "@/api/app/manager/videos";
import {DeepCopyUtils, dateUtils, searchUtils} from "@/utils/LeeUtils";
import {mapGetters} from "vuex";
import videosMainDialog from "./videosMainDialog.vue";
import leeTable from "@/components/elementUI/lee-table/index.vue";
import {getTableData} from "@/api/app/knowledge/businessFlow";

export default {
  inject: ["eventBus"],
  components: {leeTable, videosMainDialog},
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
    gwglid: {
      type: Array,
      default: () => []
    }
  },
  computed: {
    ...mapGetters(["znbm"]),
  },
  methods: {
    async getTableDataAdapter(params) {
      try {
        const response = await getGwspList(params)
        console.log("table", response)
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


    // 编辑
    handleEdit(index, row) {
      this.dialogVisible = true;
      this.$refs.dialogRef.onInit(index, row);
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

    // 删除
    handleDelete(_, row) {
      deleteGwsp(row.id)
          .then((res) => {
            if (res.result) {
              this.$message({
                message: res.msg,
                type: "success",
              });
              // 刷新数据
              this.handelSearch(this.value);
              // this.eventBus.$emit("refresh");
            }
          })
          .catch((error) => {
            this.$message({
              message: error,
              type: "warning",
            });
          });
    },
  },

  mounted() {
  },
};
</script>
