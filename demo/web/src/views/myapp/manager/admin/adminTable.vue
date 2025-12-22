<template>
  <div>
    <lee-table
      ref="leeTable"
      :fetch-data="getTableDataAdapter"
      :query-params="value"
      v-bind="$attrs"
      v-on="$listeners"
    >
      <el-table-column property="realname" label="姓名"></el-table-column>
      <el-table-column property="username" label="账号"></el-table-column>
      <el-table-column property="createtime" label="创建时间">
        <template v-slot="scope">
          {{ scope.row.createtime.substring(0,10) }}
        </template>
      </el-table-column>
      <el-table-column label="是否注销">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.cancel"
            active-value="1"
            inactive-value="0"
            @change="handleChangeCancelState(scope.row)"
          >
          </el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleEdit(scope.$index, scope.row)"
          >编辑
          </el-button
          >
          <el-popconfirm
            title="确定删除该管理员？"
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
    <admin-dialog
      :visible.sync="dialogVisible"
      ref="editRef"
      :postType="'edit'"
      @data-save="handelSearch(value)"
    ></admin-dialog>
  </div>
</template>

<script>
import adminDialog from './adminDialog'
import {
  getAdminList,
  deleteAdmin,
  updateCancelState
} from '@/api/app/manager/admin'
import leeTable from '@/components/elementUI/lee-table'

export default {
  components: {
    leeTable,
    adminDialog
  },
  data() {
    return {
      rawData: [],
      currentRow: null,
      page: 1,
      rows: 10,
      isLoading: false,
      dialogVisible: false
    }
  },
  props: {
    value: {
      type: Object,
      required: true
    }
  },
  computed: {},
  methods: {
    // 查询配适器
    async getTableDataAdapter(params) {
      try {
        const response = await getAdminList(params)
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
    handleDelete(_, row) {
      deleteAdmin(row.id)
        .then((res) => {
          if (res.result) {
            this.$message({
              message: res.msg,
              type: 'success'
            })
            // 刷新数据
            this.handelSearch();
            // this.eventBus.$emit("refresh");
          }
        })
        .catch((error) => {
          this.$message({
            message: error,
            type: 'warning'
          })
        })
    },
    // 处理注销
    handleChangeCancelState(row) {
      updateCancelState(row.id)
        .then((res) => {
          if (res.result) {
            this.$message({
              message: res.msg,
              type: "success",
            });
            // 刷新数据
            this.handelSearch();
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
    this.$nextTick(() => {
      this.eventBus.$on("onSearch", () => this.handelSearch());
    });
  }
}
</script>
