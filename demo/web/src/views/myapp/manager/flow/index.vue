<!-- 接口获取数据的table和分页器 -->
<template>
  <div>
    <el-form
      ref="elForm"
      label-position="left"
      :model="queryParams"
      size="medium"
      label-width="80px"
    >
      <el-row :gutter="20">
        <el-col :span="6">
          <el-form-item label="业务流程:" prop="wjmc">
            <el-input v-model="queryParams.lcmc" clearable placeholder="请输入业务流程"/>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="状态:" prop="zt">
            <zt v-model="queryParams.lczt"/>
          </el-form-item>
        </el-col>
        <el-col :span="12" class="right-align">
          <el-button
            class="search"
            @click="onSearch"
            type="primary"
            icon="el-icon-search"
          >查询
          </el-button>
          <el-button
            @click="onAdd"
            type="primary"
            icon="el-icon-circle-plus-outline"
          >新增
          </el-button>
        </el-col
        >
        <el-col :span="6">
          <div class="grid-content bg-purple"></div>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <flowTable class="table" ref="tableRef" v-model="queryParams"/>
          <flow-edit activeType="save" ref="editRef" @data-saved="onSearch()"/>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
import {ywlb, zt} from "@/components/select";
import flowTable from "./flowTable.vue";
import flowEdit from "./flowEdit";
import {apiUtils} from "@/utils/LeeUtils";

export default {
  name: "flow",
  data() {
    return {
      message: "流程管理",
      queryParams: {
        page: "1",
        rows: "10",
        lcmc: '', // 流程名称
        lczt: 'SELECTALL', // 状态
      },
    };
  },
  components: {
    ywlb,
    zt,

    flowTable,
    flowEdit,
  },
  methods: {
    // 查询
    onSearch() {
      this.$refs.tableRef.handelSearch();
    },
    // 添加
    onAdd() {
      // 新业务流程数据结构
      const newYwLc = {tdywlcHjList: []};
      this.$store.dispatch("flow/setflowFormParams", newYwLc);
      this.$refs.editRef.dialogVisible = true;
    },
  },
  // 添加生命周期函数
  mounted() {
  },
  beforeDestroy() {
  },
  computed: {},
};
</script>

<style lang="sass" scoped>
.parent-container
  display: flex
  flex-direction: row

.zt,
.search
  margin-left: 10px

.table
  margin-top: 10px
</style>
