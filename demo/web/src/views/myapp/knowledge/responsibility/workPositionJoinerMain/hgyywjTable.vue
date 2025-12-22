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
      <el-col :span="8">
        <el-form-item label="合规应用文件类别：" prop="wjlbid">
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
        <el-button @click="onSearch" type="primary" icon="el-icon-search"
          >查询</el-button
        >
      </el-col>
    </el-form>
    <el-table
      ref="singleTable"
      border
      style="width: 100%"
      stripe
      :data="tableData.content"
      class="mb10"
      v-loading="isLoading"
    >
      <el-table-column
        label="序号"
        type="index"
        min-width="5%"
      ></el-table-column>
      <el-table-column property="wjmc" label="名称" min-width="10%">
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
        property="displayDate"
        label="发文时间"
        min-width="10%"
      ></el-table-column>
      <el-table-column
        property="wjlb.lbmc"
        label="文件类别"
        min-width="10%"
      ></el-table-column>
      <el-table-column property="znbm.bmmc" label="职能部门" min-width="10%">
        <template slot-scope="scope">
          {{ scope.row.znbmid === null ? scope.row.znbmmc : scope.row.znbm.bmmc   }}
        </template>
      </el-table-column>
      <el-table-column
        property="xldj.djmc"
        label="效力等级"
        min-width="10%"
      ></el-table-column>
      <el-table-column
        property="enable"
        label="状态"
        min-width="10%"
      ></el-table-column>
<!--      <el-table-column property="lct" min-width="10%" label="流程图">-->
<!--        <template slot-scope="scope">-->
<!--          <el-link-->
<!--            :disabled="scope.row.lct != '流程图'"-->
<!--            type="primary"-->
<!--            @click="handleOpenFlowBoxDialog(scope.$index, scope.row)"-->
<!--          >-->
<!--            {{ scope.row.lct }}-->
<!--          </el-link>-->
<!--          <flow-box ref="flowboxRef"></flow-box>-->
<!--        </template>-->
<!--      </el-table-column>-->
    </el-table>
    <hgwj-dialog :visible.sync="dialogVisible" ref="dialogRef"></hgwj-dialog>
  </div>
</template>

<script>
import {
  getHgwjWithExceptIdsTableData,
  getYwlbOptions,
} from "@/api/app/knowledge/responsibility";
import { DeepCopyUtils, dateUtils } from "@/utils/LeeUtils";
import { mapGetters } from "vuex";
import hgwjDialog from "./Dialog/hwwjDialog.vue";
import flowBox from "@/views/components/flowBox";
export default {
  inject: ["eventBus"],
  components: { hgwjDialog, flowBox },
  data() {
    return {
      queryParams: {
        wjlbid: "",
      },
      rawData: [],
      currentRow: null,
      page: 1,
      rows: 200,
      isLoading: false,
      dialogVisible: false,
      wjlbOptions: [],
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
    tableData() {
      if (this.rawData) {
        const content = this.rawData;
        const transformedData = content.map((item) => {
          return {
            ...item,
            displayDate: item.cjsj.substring(0, 10),
            enable: item.zt == 1 ? "有效" : "废止",
            lct:
              item.tdywlc && item.tdywlc.length > 0 ? "流程图" : "",
          };
        });

        return {
          ...this.rawData,
          content: transformedData,
        };
      } else {
        return []; // 或其他默认值
      }
    },
  },
  methods: {
    onSearch() {
      this.handleGetTableData();
    },
    // 获取数据
    handleGetTableData() {
      this.isLoading = true;
      let params = DeepCopyUtils.deepCopy(this.value);
      if (params.gwglid.length > 0) {
        params.gwglid = params.gwglid[params.gwglid.length - 1];
      } else {
        params.gwglid = params.gwglid[0];
      }
      params.wjlbid = this.queryParams.wjlbid;
      getHgwjWithExceptIdsTableData(params).then((res) => {
        console.log(res);
        this.rawData = res.data;
        this.isLoading = false;
      });
    },

    // 获取业务类别选项
    handleGetYwlbOptions() {
      getYwlbOptions().then((res) => {
        this.wjlbOptions = [{ id: "", lbmc: "全部" }, ...res.data];
      });
    },

    // 打开hgwj对话框
    handleShowDialog(index, row) {
      this.dialogVisible = true;
      this.$refs.dialogRef.onInit(index, row);
    },

    // 打开流程图浏览对话框
    handleOpenFlowBoxDialog(index, row) {
      this.$refs.flowboxRef.openDialog(index, row);
    },
  },

  mounted() {
    this.handleGetYwlbOptions();
  },
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
