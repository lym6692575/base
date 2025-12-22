<!--文件类别-->
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
      <el-table-column property="lbmc" label="类别名称"></el-table-column>
      <el-table-column property="ord" label="显示排序"></el-table-column>
      <el-table-column property="enable" label="是否通用">
        <template slot-scope="scope">
          {{ scope.row.sfty === 1 ? '是' : '否' }}
        </template>
      </el-table-column>
      <el-table-column property="cjsj" label="创建时间"></el-table-column>
      <el-table-column property="cjr" label="创建人"></el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleEdit(scope.$index, scope.row)"
          >编辑
          </el-button
          >
          <el-popconfirm
            title="确定删除该文件类别？"
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
    <categoryEditDialog ref="editRef" @data-saved="handelSearch()"/>
  </div>
</template>

<script>
import categoryEditDialog from './categoryEditDialog.vue'
import { getTDywlb, deleteTDywlb } from '@/api/app/manager/category'
import { mapGetters } from 'vuex'
import leeTable from '@/components/elementUI/lee-table'

export default {
  components: {
    leeTable,
    categoryEditDialog
  },
  props: {
    value: {
      type: Object,
      required: true
    }
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
  computed: {
  },
  methods: {
    // 查询配适器
    async getTableDataAdapter(params) {
      try {
        const response = await getTDywlb(params)
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
      return (this.page - 1) * this.rows + index + 1
    },

    // 获取数据
    handleGetTableData(params) {
      this.isLoading = true
      getTDywlb(params).then((res) => {
        this.rawData = res.data
        this.isLoading = false
      })
    },

    // 本地模糊查询
    onfuzzySearch(val) {
      console.log(val)
      this.lbmc = val
    },

    // 编辑
    handleEdit(index, row) {
      this.$refs.editRef.initEditdata(row)
    },

    // 删除
    handleDelete(index, row) {
      deleteTDywlb(row.id).then((res) => {
        if (res.result) {
          this.$message({
            message: res.msg,
            type: 'success'
          })
          // 刷新vuex数据
          this.handelSearch()
        }
      })
    },

    // 分页切换
    handleCurrentChange(val) {
      let params = this.value
      if (params) {
        params.page = val
        this.page = val
      }
      this.handleGetTableData(params)
    },

    // 查询到所属上级名称
    findSjmc(sjid) {
      const found = this.ywlb.data.find((item) => {
        return item.id == sjid
      })
      if (found) {
        return found.bmmc
      } else {
        return '无'
      }
    }
  },
  mounted() {
    // 初始数据
    this.handleGetTableData(this.value)
  }
}
</script>
