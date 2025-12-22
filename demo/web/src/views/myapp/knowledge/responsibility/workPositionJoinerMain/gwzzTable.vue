<template>
  <div>
    <!-- 表格 -->
    <h5 class="title">
      <i class="el-icon-user-solid"></i>
      <span class="mr20">岗位职责：</span>
      <div class="title-border"></div>
    </h5>
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
        <template slot-scope="scope">
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
import { getHgwjTableData } from "@/api/app/knowledge/responsibility";
import { DeepCopyUtils, dateUtils } from "@/utils/LeeUtils";
import { mapGetters } from "vuex";
import hgwjDialog from "./Dialog/hwwjDialog.vue";
import leeTable from '@/components/elementUI/lee-table/index.vue'
export default {
  inject: ["eventBus"],
  components: { leeTable, hgwjDialog },
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
    async getTableDataAdapter(params) {
      let _params = DeepCopyUtils.deepCopy(params);
      if (_params.gwglid.length > 0) {
        _params.gwglid = _params.gwglid[_params.gwglid.length - 1];
      } else {
        _params.gwglid = _params.gwglid[0];
      }
      _params.wjlbid = "8b6adfc4898216610189827d0b8e0000";
      try {
        const response = await getHgwjTableData(_params)
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


    // 打开hgwj对话框
    handleShowDialog(index, row) {
      this.dialogVisible = true;
      this.$refs.dialogRef.onInit(index, row);
    },
  },

  mounted() {},
};
</script>

<style lang="sass" scoped>
.title
  color: #515A6e
  &-border
    border-bottom: 1px dashed #909399
    margin-top: 20px
    opacity: 0.4
</style>
