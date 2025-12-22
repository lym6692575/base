<template>
  <div>
    <el-dialog
        width="80%"
        v-bind="$attrs"
        v-on="$listeners"
        @open="onOpen"
        @close="onClose"
        title="文件关联"
        :header-cell-style="{ 'text-align': 'center' }"
        :cell-style="{ 'text-align': 'center' }"
    >
      <el-form
          ref="elForm"
          :model="queryParams"
          size="medium"
          label-width="100px"
          label-position="left"
      >
        <el-row :gutter="20">
          <el-col :span="10">
            <el-form-item label="文件名称：" prop="wjmc">
              <el-input
                  v-model="queryParams.wjmc"
                  placeholder="请输入文件名称"
                  clearable
                  :style="{ width: '100%' }"
              >
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="10">
            <el-form-item label="业务类别：" prop="sjid">
              <ywlb v-model="queryParams.ywlbid"/>
            </el-form-item>
          </el-col>
          <el-col class="right-align" :span="4">
            <el-button @click="handelSearch" type="primary" icon="el-icon-search"
            >查询
            </el-button
            >
          </el-col>
        </el-row>
      </el-form>
      <lee-table
          ref="leeTable"
          height="550"
          :query-params="computedQueryParams"
          v-bind="$attrs"
          v-on="$listeners"
          :select="true"
          selectionCriteria="is"
          :fetch-data="getTableDataAdapter"
          @update:selection="handleSelectionUpdate"
      >
        <el-table-column property="lbmc" label="所属类别">
        </el-table-column>
        <el-table-column property="wjmc" label="文件名称">
        </el-table-column>
        <el-table-column
            property="bmmc"
            label="职能部门"
        >
          <template slot-scope="scope">
            {{ scope.row.znbmid === null ? scope.row.znbmmc : scope.row.bmmc   }}
          </template>
        </el-table-column>
        <el-table-column
            property="partialTDznbm"
            label="关联部门"
            width="340px"
        >
          <template slot-scope="scope">
            <popover
                :znbmList="scope.row['partialTDznbm']"
                :hgwjid="scope.row.id"
                @data-refresh="handelSearch()"
                actionType="edit"
            ></popover>
          </template>
        </el-table-column>
        <el-table-column
            property="fwsj"
            label="发布时间"
        ></el-table-column>
      </lee-table>
      <div slot="footer">
        <el-button @click="close">取消</el-button>
        <el-button type="primary" @click="handelConfirm">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {DeepCopyUtils, searchUtils} from "@/utils/LeeUtils";
import {getHgwjList, setBmzzgx} from "@/api/app/manager/departmentRoles";
import {ywlb} from "@/components/select";
import popover from "./popover.vue";
import leeTable from "@/components/elementUI/lee-table/index.vue";

export default {
  inject: ["eventBus"],
  inheritAttrs: false,
  components: {leeTable, ywlb, popover},
  props: {
    znbmid: {
      type: String,
      required: true
    },
  },
  data() {
    return {
      isLoading: false,
      rawData: [],
      // 所选数据
      selection: [],
      // 查询参数
      queryParams: {
        wjmc: undefined,
        ywlbid: "",
      },
    };
  },
  computed: {
    computedQueryParams() {
      return {znbmid: this.znbmid, ...this.queryParams}
    }
  },
  watch: {},
  created() {
  },
  mounted() {
  },
  methods: {
    // 查询配适器
    async getTableDataAdapter(params) {
      try {
        const response = await getHgwjList(params)
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

    onInit() {
      this.$nextTick(() => {
        this.handelSearch()
      })
    },

    onOpen() {
    },

    onClose() {
      this.$refs["leeTable"].clearSelection()
      this.$refs["elForm"].resetFields();
    },

    close() {
      this.$emit("update:visible", false);
    },


    handelConfirm() {
      this.$refs["elForm"].validate((valid) => {
        if (!valid) return;
        let formData = DeepCopyUtils.deepCopy({znbmid: this.znbmid});
        formData.hgwj = this.selection;
        console.log("formData",formData)
        setBmzzgx(formData)
            .then((response) => {
              // this.close();
              this.$message({
                message: response.msg,
                type: "success",
              });
              this.$emit("data-refresh");
            })
            .catch((error) => {
              this.$message({
                message: error,
                type: "warning",
              });
            });
      });
    },

    // 多选的数组
    handleSelectionUpdate(selection) {
      this.selection = selection
    },
  },
};
</script>
