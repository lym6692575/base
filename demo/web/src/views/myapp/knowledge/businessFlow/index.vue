<template>
  <div>
    <div class="parent-container"></div>
    <el-form
      ref="elForm"
      label-position="left"
      :model="queryParams"
      size="medium"
      label-width="80px"
    >
      <!-- {{ queryParams }} -->
      <el-row :gutter="20">
        <el-col :span="6">
          <el-form-item label="业务流程:" prop="wjmc">
            <el-input v-model="queryParams.lcmc" clearable placeholder="请输入业务流程"/>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="业务类别:" prop="ywlbid">
            <ywlb
              v-model="queryParams.ywlbid"
            />
          </el-form-item>
        </el-col>
        <!-- <el-col :span="5">
          <el-form-item label="效力等级:" prop="xldjid">
            <xldj v-model="queryParams.xldjid" />
          </el-form-item>
        </el-col>
        <el-col :span="5">
          <el-form-item label="职能部门:" prop="znbmid">
            <znbm v-model="queryParams.znbmid" />
          </el-form-item>
        </el-col> -->
        <el-col :span="12" class="right-align">
          <el-button @click="onSearch" type="primary" icon="el-icon-search"
          >查询
          </el-button
          >
        </el-col>
      </el-row>
    </el-form>
    <compTable ref="tableRef" v-model="queryParams"></compTable>
  </div>
</template>

<script>
import {ywlb, xldj, znbm, zt} from "@/components/select";
import elDatepicker from "@/components/elementUI/el-datepicker";
import compTable from "./compTable.vue";
import {apiUtils, dateUtils, DeepCopyUtils} from "@/utils/LeeUtils";
import leeSelect from "@/components/elementUI/lee-select.vue";

export default {
  name: "compliance",
  components: {
    leeSelect,
    ywlb,
    xldj,
    znbm,
    zt,
    elDatepicker,

    compTable,
  },
  data() {
    return {
      queryParams: {
        page: "1",
        rows: "10",
        lcmc: undefined,
        ywlbid: 'SELECTALL',
        xldjid: 'SELECTALL',
        znbmid: 'SELECTALL',
        zt: "1",
      },
    };
  },

  methods: {
    onSearch() {
      this.$refs.tableRef.handelSearch()
    },
  },
  // 添加生命周期函数
  mounted() {
  },
  beforeDestroy() {
  },
  computed: {
    generatedKey() {
      const timestamp = new Date().getTime();
      const random = Math.floor(Math.random() * 1000); // 生成一个随机数，范围为0到999
      return timestamp + "_" + random;
    },
  },
};
</script>

<style scoped>
.parent-container {
  display: flex;
  flex-direction: row;
}
</style>
