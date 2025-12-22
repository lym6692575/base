<template>
  <div>
    <!--    {{ value }}-->
    <!-- 表格 -->
    <!-- {{ tableData.content }} -->
    <lee-table
        ref="leeTable"
        :fetch-data="getTableDataAdapter"
        :query-params="value"
        v-bind="$attrs"
        v-on="$listeners"
        :initialSize="10"
        :pageSizes="[20, 50, 100, 200]"
    >
      <el-table-column property="lbmc" label="业务类别名称">
        <template v-slot="scope">
          <el-button type="text" @click="navigateToCompliance(scope.row.ywlbid)">
            {{ scope.row.lbmc }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column property="total" label="总计"></el-table-column>
      <el-table-column property="ztOneCount" label="新增">
        <template v-slot="scope">
          <el-button type="text" @click="navigateToCompliance(scope.row.ywlbid,[value['startDate'],value['endDate']])">
            {{ scope.row.ztOneCount }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column property="ztTwoCount" label="修订"></el-table-column>
      <el-table-column property="ztZeroCount" label="废止"></el-table-column>
    </lee-table>
  </div>
</template>

<script>
import * as XLSX from 'xlsx'
import {getcount} from '@/api/app/releaseQuery'
import leeTable from '@/components/elementUI/lee-table/index.vue'
import {mapGetters} from "vuex";
import {RecursiveUtils} from '@/utils/LeeUtils'
import {RouterUtils} from '@/utils/ProjectUtils.js'

export default {
  components: {leeTable},
  data() {
    return {
      lastQueryParams: {}
    }
  },
  props: {
    value: {
      type: Object,
      required: true
    }
  },
  computed: {
    ...mapGetters(['routerArr']),
  },
  methods: {
    // 查询配适器
    async getTableDataAdapter(params) {
      this.lastQueryParams = params
      try {
        const response = await getcount(params)
        console.log(response)
        return {
          data: response.data, // 适应到你的数据结构
          total: null // 适应到你的数据结构
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

    // 跳转逻辑
    navigateToCompliance(ywlbid, date) {
      const compliance = RouterUtils.findRouterByName("compliance");
      this.$store.dispatch('base/setSelectedNode',
          {
            node: compliance,
            query: {
              path: '/knowledge/compliance',
              query: {wjlbid: '1', ywlbid, date}
            }
          }
      )
      // this.$router.push({
      //   path: '/knowledge/compliance',
      //   query: { wjlbid: '1', ywlbid, date }
      // })
    },

    // 导出表格
    getExcel() {
      const aoa = this.convertDataToAoa(this.$refs.leeTable.internalData)
      const worksheet = XLSX.utils.aoa_to_sheet(aoa)
      const workbook = XLSX.utils.book_new()
      XLSX.utils.book_append_sheet(workbook, worksheet, 'Sheet1')
      XLSX.writeFile(workbook, '制度发布查询.xlsx')
    },

    // 导出处理数据
    convertDataToAoa(data) {
      // 定义想要保留的列和它们的中文标题
      const columns = [
        {key: 'id', title: '序号'},
        {key: 'lbmc', title: '类别'},
        {key: 'total', title: '总计'},
        {key: 'ztZeroCount', title: '新增'},
        {key: 'ztOneCount', title: '修订'},
        {key: 'ztTwoCount', title: '废止'}
      ]

      // 提取列标题
      const headers = columns.map((column) => column.title)

      // 根据列的顺序从每个对象中提取数据
      const rows = data.map((obj, index) =>
          columns.map((column) => {
            // 特殊处理 '序号' 和 '留言答复'
            if (column.key === 'id') {
              return index + 1 // 序号
            } else {
              return obj[column.key] // 其他列
            }
          })
      )

      // 在行数组的开头添加标题
      rows.unshift(headers)

      return rows
    }
  },

  mounted() {
  }
}
</script>
