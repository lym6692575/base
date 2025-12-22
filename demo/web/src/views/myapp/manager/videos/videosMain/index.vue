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
          <!-- <el-button @click="onSearch" type="primary" icon="el-icon-search"
            >查询</el-button
          > -->
          <el-tooltip :disabled="gwglid.length!==0" content="请选择组织节点" placement="top" effect="light">
            <el-button
                @click="handleNewGwsp"
                type="primary"
                icon="el-icon-circle-plus-outline"
                :disabled="queryParams.gwglid.length === 0">
              新增
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
          <videos-main-table
              v-show="gwglid.length!==0"
              class="table"
              ref="tableRef"
              v-model="queryParams"
              :gwglid="gwglid"
          />
        </el-col>
      </el-row>
    </el-form>
    <!-- 新增对话框 -->
    <videos-main-dialog
        :visible.sync="dialogVisible"
        ref="dialogRef"
        postType="add"
        :gwglid="gwglid"
        @data-refresh="onSearch()"
    />
  </div>
</template>
<script>
import gwPicker from "./gwPicker.vue";
import videosMainTable from "./videosMainTable.vue";
import videosMainDialog from "./videosMainDialog.vue";

export default {
  // 调用事件总线
  inject: ["eventBus"],
  data() {
    return {
      gwglid: [],
      dialogVisible: false,
      selectedNode: null,
      queryParams: {
        page: "1",
        rows: "10",
        // 岗位id
        gwglid: "",
      },
    };
  },
  components: {
    gwPicker,
    videosMainTable,
    videosMainDialog,
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

    // 调用新增对话框
    handleNewGwsp() {
      this.dialogVisible = true;
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
