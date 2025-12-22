<template>
  <div>
    <!-- 表格 -->
    <!--    {{ tableData.content }}-->
    <el-table
      ref="singleTable"
      border
      style="width: 100%"
      :data="tableData.content"
      class="mb10"
      v-loading="isLoading"
      :span-method="spanMethod"
      :header-cell-style="{ 'text-align': 'center' }"
      :cell-style="{ 'text-align': 'center' }"
    >
      <el-table-column
        label="序号"
        type="index"
        width="60"
        :index="indexMethods"
      ></el-table-column>
      <el-table-column property="bmmc" label="部门名称" min-width="15%">
      </el-table-column>
      <el-table-column property="displayBmzz" label="部门职责" min-width="15%">
        <template slot-scope="scope">
          <el-link
            type="primary"
            @click="handleOpenBmzzDialog(scope.$index, scope.row)"
          >
            {{ scope.row.displayBmzz != null ? scope.row.displayBmzz : '详情' }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column
        property="tdhgwjItem.wjmc"
        label="相关文件"
        min-width="15%"
      >
      </el-table-column>
      <el-table-column
        property="tdhgwjItem.ywlb.lbmc"
        label="文件类型"
        min-width="15%"
      >
      </el-table-column>
      <el-table-column
        property="tdhgwjItem.ywlb.sfty"
        label="是否通用"
        width="100"
      >
        <template slot-scope="scope">
          {{
            scope.row.tdhgwjItem.ywlb.sfty === 1
              ? '是'
              : scope.row.tdhgwjItem.ywlb.sfty === ''
                ? ''
                : '否'
          }}
        </template>
      </el-table-column>
      <el-table-column
        property="tdhgwjItem.partialTDznbm"
        label="关联部门"
        width="340px"
      >
        <template slot-scope="scope">
          <popover
            :znbmList="scope.row.tdhgwjItem['partialTDznbm']"
            actionType="view"
          ></popover>
        </template>
      </el-table-column>
      <el-table-column
        property="tdhgwjItem.fwsj"
        label="发布时间"
        min-width="15%"
      >
      </el-table-column>
      <!-- <el-table-column label="操作" min-width="10%">
        <template slot-scope="scope">
          <el-popconfirm
            title="确定删除该部门？"
            @confirm="handleDelete(scope.$index, scope.row)"
          >
            <el-button slot="reference" size="mini" type="danger"
              >取消关联</el-button
            >
          </el-popconfirm>
        </template>
      </el-table-column> -->
    </el-table>
    <!-- 分页器 -->
    <el-pagination
      class="right-align"
      v-if="tableData['totalElements'] > rows"
      background
      layout="prev, pager, next"
      :total="tableData['totalElements']"
      :page-sizes="[5, 10, 15, 20]"
      @current-change="handleCurrentChange"
    >
    </el-pagination>
    <department-roles-bmzz-dialog
      :visible.sync="dialogVisible"
      ref="bmzzRef"
      @data-refresh="handleGetTableData(value)"
    ></department-roles-bmzz-dialog>
  </div>
</template>

<script>
import { getTableData } from '@/api/app/manager/departmentRoles'
import { DeepCopyUtils, dateUtils, searchUtils } from '@/utils/LeeUtils'
import { mapGetters } from 'vuex'
import popover from './popover.vue'
import departmentRolesBmzzDialog from './departmentRolesBmzzDialog'

export default {
  inject: ['eventBus'],
  components: { popover, departmentRolesBmzzDialog },
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
    tableData() {
      if (this.rawData && this.rawData.content) {
        const content = this.rawData.content
        const transformedData = []

        content.forEach((item) => {
          // 如果tdhgwj为空或者不是数组，将当前项作为一行添加到结果中
          if (!Array.isArray(item['tdhgwj']) || item['tdhgwj'].length === 0) {
            transformedData.push({
              ...item,
              tdhgwjItem: { ywlb: { lbmc: '', sfty: '' } } // 添加默认的tdhgwjItem对象
            })
          } else {
            // 如果tdhgwj是数组，将每一项和当前的其他信息组合成一行
            item['tdhgwj'].forEach((tdhgwjItem, index) => {
              transformedData.push({
                ...item,
                displayBmzz: index === 0 ? '详情' : '', // 仅在第一行显示详情
                tdhgwjItem: tdhgwjItem || {} // 确保tdhgwjItem不是空值
              })
            })
          }
        })

        return {
          ...this.rawData,
          content: transformedData
        }
      } else {
        return [] // 或其他默认值
      }
    }
  },
  methods: {
    // 合并单元格方法
    spanMethod({ row, column, rowIndex, columnIndex }) {
      if (columnIndex === 1 || columnIndex === 2) {
        // 对第二列和第三列进行操作
        if (
          rowIndex > 0 &&
          this.tableData.content[rowIndex].bmmc ===
          this.tableData.content[rowIndex - 1].bmmc
        ) {
          return {
            // 如果当前行的 bmmc 与上一行的相同，则跳过渲染
            rowspan: 0,
            colspan: 0
          }
        }
        let rowspan = 1 // 默认行跨度为 1
        // 查找下面有多少连续的行与当前行的 bmmc 相同，并设置相应的行跨度
        while (
          rowIndex + rowspan < this.tableData.content.length &&
          this.tableData.content[rowIndex].bmmc ===
          this.tableData.content[rowIndex + rowspan].bmmc
          ) {
          rowspan++
        }
        return {
          rowspan,
          colspan: 1 // 列跨度为 1，所以不会合并列
        }
      }
    },
    // 编辑部门职责对话框
    handleOpenBmzzDialog(index, row) {
      this.dialogVisible = true
      this.$refs.bmzzRef.onInit(index, row)
    },

    // index方法
    indexMethods(index) {
      return (this.page - 1) * this.rows + index + 1
    },

    // 获取数据
    handleGetTableData(params) {
      this.isLoading = true

      let _params = DeepCopyUtils.deepCopy(params)
      // if (_params.znbmid.length > 0) {
      //   _params.znbmid = _params.znbmid[_params.znbmid.length - 1]
      // } else {
      //   _params.znbmid = _params.znbmid[0]
      // }

      getTableData(_params).then((res) => {
        this.rawData = res.data
        this.isLoading = false
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

    // 编辑
    handleEdit(index, row) {
      this.dialogVisible = true
      this.$refs.editRef.onInit(index, row)
    },

    // 删除
    handleDelete(index, row) {
      deleteTDgwgl(row.id).then((res) => {
        if (res.result) {
          this.$message({
            message: res.msg,
            type: 'success'
          })
          // 刷新数据
          this.handleGetTableData(this.value)
        }
      })
    }
  },

  mounted() {
  }
}
</script>
