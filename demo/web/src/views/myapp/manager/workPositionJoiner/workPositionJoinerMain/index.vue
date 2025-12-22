<template>
  <div>
    <el-form
      ref="elForm"
      :model="queryParams"
      size="medium"
      label-width="100px"
      label-position="left"
    >
      <el-row :gutter="20">
        <el-col :span="14">
          <el-form-item label="所选岗位：" prop="sjid">
            <gw-picker
              v-model="gwglid"
              :checkStrictly="false"
              @change="handleGwChange"
            />
          </el-form-item>
        </el-col>
        <el-col class="right-align" :span="10">
          <el-tooltip :disabled="gwglid.length!==0" content="请选择组织节点" placement="top" effect="light">
            <el-button
              @click="handleNewZnbm"
              type="primary"
              icon="el-icon-connection"
              :disabled="queryParams.gwglid.length === 0"
            >
              关联流程
            </el-button>
          </el-tooltip>
        </el-col>
        <el-col :span="24">
          <el-empty
            v-show="gwglid.length===0"
            style="width: 100%"
            class="mt40"
            :image-size="200"
            description="请选择组织节点">
          </el-empty>
          <work-Position-Joiner-Table
            v-show="gwglid.length!==0"
            class="table"
            ref="tableRef"
            v-model="queryParams"
          />
        </el-col>
      </el-row>
    </el-form>
    <!-- 关联对话框 -->
    <work-position-joiner-dialog
      :visible.sync="dialogVisible"
      ref="dialogRef"
      actionType="add"
      @data-refresh="onSearch()"
      :gwglid="queryParams.gwglid"
    />
  </div>
</template>
<script>
import gwPicker from "./gwPicker.vue";
import workPositionJoinerTable from "./workPositionJoinerTable.vue";
import workPositionJoinerDialog from "./workPositionJoinerDialog.vue";
import videosMainTable from '@/views/myapp/manager/videos/videosMain/videosMainTable.vue'

export default {
  // 调用事件总线
  inject: ["eventBus"],
  data() {
    return {
      gwglid: [],
      dialogVisible: false,
      queryParams: {
        page: "1",
        rows: "10",
        // 岗位id
        gwglid: "",
      },
    };
  },
  components: {
    videosMainTable,
    gwPicker,
    workPositionJoinerTable,
    workPositionJoinerDialog,
  },
  methods: {
    // 处理gw选择后的逻辑
    handleGwChange(gwglidArr) {
      if (gwglidArr && gwglidArr.length > 0) {
        this.gwglid = gwglidArr;
        this.queryParams.gwglid = gwglidArr[gwglidArr.length - 1]
        this.eventBus.$emit("setSelectedNode", gwglidArr);
      } else {
        this.queryParams.gwglid = gwglidArr[0]
      }
      this.onSearch()
    },

    // 查询
    onSearch() {
      this.$refs.tableRef.handelSearch();
    },

    // 调用关联对话框
    handleNewZnbm() {
      this.dialogVisible = true;
      this.$refs.dialogRef.onInit()
    },
  },
  // 添加生命周期函数
  mounted() {
    // 给事件总线添加搜索方法
    this.$nextTick(() => {
      this.eventBus.$on("onSearch", (gwglid) => this.handleGwChange(gwglid));
    });
  },
  beforeDestroy() {
  },
  computed: {},
};
</script>
