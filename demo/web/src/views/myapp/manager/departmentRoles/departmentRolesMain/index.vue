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
          <el-form-item label="岗位名称：" prop="znbmid">
            <znbm
                v-model="znbmidArr"
                @change="handleGwChange"
            ></znbm>
          </el-form-item>
        </el-col>
        <el-col class="right-align" :span="10">
          <!-- <el-button
            @click="handleEditBmzz"
            type="primary"
            icon="el-icon-connection"
            >部门职责</el-button
          > -->
          <el-tooltip :disabled="znbmidArr.length!==0" content="请选择组织节点" placement="top" effect="light">
            <el-button
                :disabled="znbmidArr.length===0"
                @click="handleNewZnbm"
                type="primary"
                icon="el-icon-connection"
            >关联文件
            </el-button>
          </el-tooltip>
        </el-col>
        <el-col :span="24">
          <el-empty
              v-show="znbmidArr.length===0"
              style="width: 100%"
              class="mt40"
              :image-size="200"
              description="请选择组织节点">
          </el-empty>
          <main-table
              v-show="znbmidArr.length!==0" class="table" ref="tableRef" v-model="queryParams"/>
        </el-col>
      </el-row>
    </el-form>
    <!-- 对话框 -->
    <department-roles-main-dialog
        :visible.sync="dialogVisible"
        ref="dialogRef"
        actionType="add"
        :znbmid=this.queryParams.znbmid
        @data-refresh="onSearch()"
    />
  </div>
</template>
<script>
import znbm from "@/views/components/cascader/znbm.vue";
import mainTable from "./mainTable.vue";
import departmentRolesMainDialog from "./departmentRolesMainDialog.vue";
import videosMainTable from "@/views/myapp/manager/videos/videosMain/videosMainTable.vue";

export default {
  inject: ["eventBus"],
  data() {
    return {
      znbmidArr: [],
      dialogVisible: false,
      queryParams: {
        page: "1",
        rows: "10",
        // 所属部门
        znbmid: "",
      },
    };
  },
  components: {
    videosMainTable,
    znbm,
    mainTable,
    departmentRolesMainDialog,
  },
  methods: {
    // 处理znbm选择后的逻辑
    handleGwChange(znbmidArr) {
      if (znbmidArr && znbmidArr.length > 0) {
        this.znbmidArr = znbmidArr;
        this.queryParams.znbmid = znbmidArr[znbmidArr.length - 1]
        this.eventBus.$emit("setSelectedNode", znbmidArr);
      } else {
        this.queryParams.znbmid = znbmidArr[0]
      }
      this.onSearch()
    },

    // 查询
    onSearch() {
      this.queryParams.page = 1;
      this.$refs.tableRef.handleGetTableData(this.queryParams);
    },

    // 调用关联对话框
    handleNewZnbm() {
      this.$refs.dialogRef.onInit(this.queryParams.znbmid);
      this.dialogVisible = true;
    },
  },
  // 添加生命周期函数
  mounted() {
    this.$nextTick(() => {
      this.eventBus.$on("onSearch", (znbmid) => this.handleGwChange(znbmid));
    });
  },
  beforeDestroy() {
  },
  computed: {},
};
</script>

<style scoped>
.parent-container {
  display: flex;
  flex-direction: row;
}
</style>
