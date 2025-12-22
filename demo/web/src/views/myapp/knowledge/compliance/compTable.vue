<!-- 合规文件 -->
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
      :autoLoading="false"
      height="500px"
      :select="type==='template'"
      :singleSelect="type==='template'"
      @update:selection="handleSelectionUpdate"
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
      <el-table-column v-if="type!=='template'" property="wjbh" label="文件编号" width="200" :show-overflow-tooltip="true"/>
      <el-table-column v-if="type!=='template' && type!=='zdxg'" property="fwsj" label="发文时间" width="180"/>
      <el-table-column v-if="type!=='template'" property="bmmc" label="职能部门" width="200" :show-overflow-tooltip="true">
      </el-table-column>
      <el-table-column v-if="type!=='template'" property="xldjmc" label="效力等级"/>
      <el-table-column v-if="type!=='zdxg'" property="ztmc" label="状态">
        <template slot-scope="scope">
          {{ scope.row.zt == 1 ? '新增' : (scope.row.zt == 2 ? '修订' : '废止') }}
        </template>
      </el-table-column>
      <el-table-column
        v-if="this.lastQueryParams.wjlbid!=='8b6adfc4898216610189827d0b8e0000' && this.lastQueryParams.wjlbid!=='297e70548d340064018d3506e6c60003' && this.lastQueryParams.wjlbid!=='297e70548b6c20b8018b6c3fd63d0000' && type!=='zdxg'"
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
      <el-table-column
        v-if="this.lastQueryParams.wjlbid==='297e70548b6c20b8018b6c3fd63d0000' ||this.lastQueryParams.wjlbid==='297e70548d340064018d3506e6c60003'|| type==='zdxg'"
        property="ljdz"
        label="链接地址"
      >
        <template slot-scope="scope">
          <el-link
            v-if="scope.row.ljdz !== null && scope.row.ljdz !== ''"
            target="_blank"
            type="primary"
            :href="scope.row.ljdz"
          >
            点击跳转
          </el-link>
          <el-link
            v-else
            disabled
            type="primary"
          >
            未添加链接
          </el-link>
        </template>
      </el-table-column>
    </lee-table>
    <!--对话框-->
    <flow-box ref="flowboxRef"></flow-box>
    <compliance-detail
      ref="detailRef"
      :titleName="'合规文件详情'"
      @data-saved="handelSearch(value)"
    ></compliance-detail>
  </div>
</template>

<script>
import flowBox from '@/views/components/flowBox'
import complianceDetail from './complianceDetail'
import {getTableData} from '@/api/app/knowledge/compliance'
import {mapGetters} from 'vuex'
import leeTable from '@/components/elementUI/lee-table'
import {apiUtils, dateUtils, DeepCopyUtils} from "@/utils/LeeUtils";

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
    },
    type: {
      type: String,
    },
  },
  data() {
    return {
      rawData: [],
      selection: [], // 所选数据
      currentRow: null,
      // 初始搜索条件
      page: 1,
      rows: 10,
      isLoading: false,
      // 上一个搜索记录，用来判断隐藏流程图
      lastQueryParams: {
        wjlbid: ""
      }
    }
  },
  methods: {
    // 查询配适器
    async getTableDataAdapter(params) {
      console.log("table", params)
      // 如果是制度官宣添加查询参数
      if (this.type === "zdxg") {
        params.wjlbid = "297e70548d340064018d3506e6c60003"
      }
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
      this.lastQueryParams = DeepCopyUtils.deepCopy(this.value)
      this.$refs.leeTable.getData()
    },

    // 打开流程图浏览对话框
    handleOpenFlowBoxDialog(index, row) {
      this.$refs.flowboxRef.openDialog(index, row)
    },

    // 查看文件细节
    showComplianceDetail(index, row) {
      console.log('open', index, row)
      console.log(this.$refs.detailRef)
      this.$refs.detailRef.openDialog(index, row)
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

    // 更新已选
    handleSelectionUpdate(selection) {
      this.selection = selection;
    },

    // 获取选择数据
    getSelectedData() {
      return this.selection
    },

    // 清除选择
    clearSelection() {
      this.$refs.leeTable.clearSelection()
    }
  },
  mounted() {
  },
  computed: {
    ...mapGetters(['znbm']),
  }
}
</script>
