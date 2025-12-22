<!-- 风险提示 -->
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
      height="500px"
    >
      <el-table-column property="wjmc" label="文件名称" width="400">
        <template slot-scope="scope">
          <el-link
            type="primary"
            @click="showComplianceDetail(scope.$index, scope.row)"
          >
            {{ scope.row.wjmc }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column property="wjbh" label="文件编号" width="200" :show-overflow-tooltip="true"/>
      <el-table-column property="fwsj" label="发文时间" width="180"/>
      <el-table-column property="bmmc" label="职能部门" width="200" :show-overflow-tooltip="true">
      </el-table-column>
      <el-table-column property="xldjmc" label="效力等级"/>
      <el-table-column property="ztmc" label="状态">
        <template slot-scope="scope">
          {{ scope.row.zt == 1 ? '新增' : (scope.row.zt == 2 ? '修订' : '废止')}}
        </template>
      </el-table-column>
      <el-table-column
        property="lct"
        label="流程图"
        width="180"
      >
        <template slot-scope="scope">
          <el-link
            type="primary"
            @click="handleOpenFlowBoxDialog(scope.$index, scope.row)"
          >
            {{ scope.row.tdywlc && scope.row.tdywlc.length > 0 ? '流程图' : '' }}
          </el-link>
        </template>
      </el-table-column>
    </lee-table>
    <!--对话框-->
    <flow-box ref="flowboxRef"></flow-box>
    <compliance-detail
      ref="detailRef"
      :titleName="'合规文件详情'"
      @data-saved="handelSearch(realQueryParams)"
    ></compliance-detail>
  </div>
</template>

<script>
import flowBox from '@/views/components/flowBox'
import complianceDetail from './complianceDetail'
import {getTableData} from '@/api/app/knowledge/compliance'
import {mapGetters} from 'vuex'
import leeTable from '@/components/elementUI/lee-table'
import {apiUtils, dateUtils, DeepCopyUtils} from '@/utils/LeeUtils'

export default {
  components: {
    leeTable,
    flowBox,
    complianceDetail
  },
  props: {
    value: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      realQueryParams: {}
    }
  },
  methods: {
    // 查询配适器
    async getTableDataAdapter(params) {
      console.log("table", params)
      try {
        params = DeepCopyUtils.deepCopy(params)
        if (params.wjlbid === '' || params.wjlbid === undefined) {
          params.wjlbid = ['297e70548985569f018985bd3e490005', '4', '297e70548985569f018985bd3e490006', '297e70548985569f018985bd3e490008']
        }
        console.log("params", params)
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

    // 打开流程图浏览对话框
    handleOpenFlowBoxDialog(index, row) {
      this.$refs.flowboxRef.openDialog(index, row)
    },

    // 查看文件细节
    showComplianceDetail(index, row) {
      this.$refs.detailRef.openDialog(index, row)
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
    }
  },
  mounted() {
  },
  computed: {
    ...mapGetters(['znbm']),
    tableData() {
      if (this.rawData && this.rawData.content) {
        const content = this.rawData.content
        const transformedData = content.map((item) => {
          return {
            ...item,
            ztmc: item.zt == 1 ? '有效' : '废止',
            znbmmc: this.findZnbmmc(item.znbmid),
            lct:
              item.tdywlc && item.tdywlc.length > 0 ? '流程图' : ''
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
  }
}
</script>
