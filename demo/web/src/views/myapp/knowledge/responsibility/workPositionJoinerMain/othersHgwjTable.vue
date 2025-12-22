<!--
20231123改该表为合规文件应用类别查询
未修改vue文件名称
-->
<template>
  <div>
    <!-- 表格 -->
    <h5 class="title">
      <i class="el-icon-menu"></i>
      <span class="mr20">合规应用文件：</span>
      <div class="title-border"></div>
    </h5>
    <el-form
      ref="elForm"
      :model="queryParams"
      size="medium"
      label-width="140px"
      label-position="left"
    >
      <!--      {{queryParams}}-->
      <el-col :span="8">
        <el-form-item label="合规应用文件类别:" prop="wjlbid">
          <el-select
            v-model="queryParams.wjlbid"
            placeholder="请选择文件类别"
            clearable
            :style="{ width: '100%' }"
          >
            <el-option
              v-for="(item, index) in wjlbOptions"
              :key="index"
              :label="item.lbmc"
              :value="item.id"
              :disabled="item.disabled"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="16" class="right-align">
        <el-button @click="onSearch(queryParams)" type="primary" icon="el-icon-search"
        >查询
        </el-button
        >
      </el-col>
    </el-form>
    <lee-table
      ref="leeTable"
      :fetch-data="getTableDataAdapter"
      :query-params="value"
      v-bind="$attrs"
      v-on="$listeners"
      :auto-loading="false"
      height="230px"
      :initialSize="5"
    >
      <el-table-column property="wjmc" label="文件名称">
        <template slot-scope="scope">
          <el-link
            type="primary"
            @click="handleShowDialog(scope.$index, scope.row)"
          >
            {{ scope.row.wjmc }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column
        property="fwsj"
        label="发文时间"
        width="120"
      >
      </el-table-column>
      <el-table-column
        property="wjlb.lbmc"
        label="文件类别"
      ></el-table-column>
      <el-table-column
        property="znbm.bmmc"
        label="职能部门"
      >
        <template v-slot="scope">
          {{ scope.row.znbmid === null ? scope.row.znbmmc : scope.row.znbm.bmmc }}
        </template>
      </el-table-column>
      <el-table-column
        property="xldj.djmc"
        label="效力等级"
        width="120"
      ></el-table-column>
      <el-table-column
        property="enable"
        label="状态"
        width="120"
      >
        <template v-slot="scope">
          {{ scope.row.zt == 1 ? '新增' : (scope.row.zt == 2 ? '修订' : '废止')}}
        </template>
      </el-table-column>
    </lee-table>
    <hgwj-dialog :visible.sync="dialogVisible" ref="dialogRef"></hgwj-dialog>
  </div>
</template>

<script>
import {
  getHgwjWithExceptIdsTableData,
  getYwlbOptions
} from '@/api/app/knowledge/responsibility'
import { mapGetters } from 'vuex'
import hgwjDialog from './Dialog/hwwjDialog.vue'
import flowBox from '@/views/components/flowBox'
import leeTable from '@/components/elementUI/lee-table/index.vue'
import { DeepCopyUtils } from '@/utils/LeeUtils'

export default {
  inject: ['eventBus'],
  components: { leeTable, hgwjDialog, flowBox },
  data() {
    return {
      queryParams: {
        wjlbid: '297e70548985569f018985bd3e490008'
      },
      rawData: [],
      currentRow: null,
      dialogVisible: false,
      wjlbOptions: []
    }
  },
  props: {
    value: {
      type: Object,
      required: true
    }
  },
  computed: {
    ...mapGetters(['znbm'])

  },
  methods: {
    // 查询配适器
    async getTableDataAdapter(params) {

      let _params = DeepCopyUtils.deepCopy(params);
      _params.wjlbid = this.queryParams.wjlbid;
      if (_params.gwglid.length > 0) {
        _params.gwglid = _params.gwglid[_params.gwglid.length - 1];
      } else {
        _params.gwglid = _params.gwglid[0];
      }
      try {
        const response = await getHgwjWithExceptIdsTableData(_params)
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

    onSearch() {
      this.$refs.leeTable.getData()
    },

    // 获取业务类别选项
    handleGetYwlbOptions() {
      getYwlbOptions().then((res) => {
        this.wjlbOptions = [{ id: '', lbmc: '全部' }, ...res.data]
      })
    },

    // 打开hgwj对话框
    handleShowDialog(index, row) {
      this.dialogVisible = true
      this.$refs.dialogRef.onInit(index, row)
    },

    // 打开流程图浏览对话框
    handleOpenFlowBoxDialog(index, row) {
      this.$refs.flowboxRef.openDialog(index, row)
    }
  },

  mounted() {
    this.handleGetYwlbOptions()
  }
}
</script>

<style lang="sass" scoped>
.title
  color: #515A6e

  &-border
    border-bottom: 1px dashed #909399
    margin-top: 20px
    opacity: 0.4
</style>
