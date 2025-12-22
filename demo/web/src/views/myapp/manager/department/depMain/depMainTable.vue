<!--部门管理-->
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
      <el-table-column property="sjmc" label="所属上级">
        <template slot-scope="scope">
          {{ findZnbmmc(scope.row.sjid) }}
        </template>
      </el-table-column>
      <el-table-column property="bmmc" label="部门名称"></el-table-column>
      <el-table-column property="px" label="排序"></el-table-column>
      <el-table-column property="enable" label="是否启用" width="100">
        <template slot-scope="scope">
          {{ scope.row.sign === "1" ? '是' : '否' }}
        </template>
      </el-table-column>
      <el-table-column property="cjsj" label="创建时间" width="120"></el-table-column>
      <el-table-column property="cjr" label="创建人" width="100"></el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleEdit(scope.$index, scope.row)"
          >编辑
          </el-button
          >
          <el-popconfirm
            title="删除该部门会删除所关联的所有岗位，是否确认删除？"
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
    <!-- 编辑对话框 -->
    <dep-edit-dialog
      :visible.sync="dialogVisible"
      ref="editRef"
      actionType="edit"
      @data-refresh="handelSearch()"
    />
  </div>
</template>

<script>
import depEditDialog from './depEditDialog.vue'
import { getTableData, deleteTDznbm } from '@/api/app/manager/department'
import { DeepCopyUtils, dateUtils, searchUtils } from '@/utils/LeeUtils'
import { mapGetters } from 'vuex'
import leeTable from '@/components/elementUI/lee-table'

export default {
  inject: ['eventBus'],
  components: {
    leeTable,
    depEditDialog
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
  computed: {
    ...mapGetters(['znbm']),
  },
  methods: {
    // 查询配适器
    async getTableDataAdapter(params) {
      try {
        const response = await getTableData(params)
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
      this.dialogVisible = true
      this.$refs.editRef.onInit(index, row)
    },

    // 删除
    handleDelete(index, row) {
      deleteTDznbm(row.id).then((res) => {
        if (res['result']) {
          this.$message({
            message: res.msg,
            type: 'success'
          })
          // 刷新数据
          this.handelSearch()
          this.eventBus.$emit('refresh')
        }
      })
    },

    // 寻找职能部门名称
    findZnbmmc(id) {
      if (this.znbm) {
        const found = this.znbm.data.find((item) => {
          return item.id === id
        })
        if (found) {
          return found.bmmc
        } else {
          return '无'
        }
      }
    },

    // 寻找所属上级名称
    findSjmc(sjid) {
      const found = this.znbm.data.find((item) => {
        return item.id == sjid
      })
      if (found) {
        return found.bmmc
      } else {
        return '无'
      }
    },

    // 寻找职能部门名称
    findHierarchy(id, topLevel = true) {
      let hierarchy = []
      const data = DeepCopyUtils.deepCopy(this.znbm.data)
      for (let i = 0; i < data.length; i++) {
        if (data[i].id === id) {
          hierarchy.push(data[i].bmmc)
          if (data[i].sjid !== null) {
            // Combine the arrays
            hierarchy = this.findHierarchy(data[i].sjid, false).concat(
              hierarchy
            )
          }
          break
        }
      }

      return topLevel ? hierarchy.join(' / ') : hierarchy
    }
  },

  mounted() {
  }
}
</script>
