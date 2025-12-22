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

      <el-table-column property="test" label="所属部门">
        <template slot-scope="scope"> {{ findHierarchy(scope.row.ssid)}} </template>
      </el-table-column>
      <el-table-column property="gwmc" label="岗位名称"></el-table-column>
      <el-table-column property="px" label="排序" width="100"></el-table-column>
      <el-table-column property="sign" label="是否启用" width="100">
        <template slot-scope="scope"> {{ scope.row.sign === "1" ? "是" : "否" }} </template>
      </el-table-column>
      <el-table-column property="cjsj" label="发布时间" width="120"></el-table-column>
      <el-table-column property="cjr" label="发布人" width="100"></el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleEdit(scope.$index, scope.row)"
          >编辑</el-button
          >
          <el-popconfirm
            title="确定删除该岗位？"
            @confirm="handleDelete(scope.$index, scope.row)"
          >
            <el-button slot="reference" size="mini" type="danger"
            >删除</el-button
            >
          </el-popconfirm>
        </template>
      </el-table-column>
    </lee-table>
    <!-- 编辑对话框 -->
    <work-position-main-dialog
      :visible.sync="dialogVisible"
      ref="editRef"
      actionType="edit"
      @data-refresh="handelSearch"
    />
  </div>
</template>

<script>
import workPositionMainDialog from "./workPositionMainDialog.vue";
import { getTDgwgl, deleteTDgwgl } from "@/api/app/manager/workPosition";
import { DeepCopyUtils, dateUtils, searchUtils } from "@/utils/LeeUtils";
import { mapGetters } from "vuex";
import leeTable from "@/components/elementUI/lee-table/index.vue";
import {getTableData} from "@/api/app/manager/department";
export default {
  inject: ["eventBus"],
  components: {
    leeTable,
    workPositionMainDialog,
  },
  data() {
    return {
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
  },
  methods: {
    // 查询配适器
    async getTableDataAdapter(params) {
      try {
        const response = await getTDgwgl(params)
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
          this.handelSearch();
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

  mounted() { },
};
</script>
