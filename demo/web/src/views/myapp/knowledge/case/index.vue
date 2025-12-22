<!-- 风险识别 -->
<template>
  <div>
    <el-form
      ref="elForm"
      label-position="left"
      :model="queryParams"
      size="medium"
      label-width="80px"
      class="lee-form"
    >
      <el-row :gutter="20">
        <el-col :span="7">
          <el-form-item label="文件名称:" prop="wjmc">
            <elInput v-model="queryParams.wjmc"/>
          </el-form-item>
        </el-col>
        <el-col :span="7">
          <el-form-item label="职能部门:" prop="znbmid">
            <znbm level=2 :bmName="['省机关', '直属机构']" v-model="queryParams.znbmid"/>
          </el-form-item>
        </el-col>
        <el-col :span="10">
          <el-form-item label="发文时间:" prop="date">
            <el-date-picker
              type="daterange"
              v-model="date"
              format="yyyy-MM-dd"
              value-format="yyyy-MM-dd"
              :style="{ width: '100%' }"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              range-separator="至"
              unlink-panels
              :clearable="true"
              @change="handleDataChange"
            ></el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="7">
          <el-form-item label="文件类别:" prop="wjlbid">
            <wjlb ref="wjlbRef" :wjlbName="['风险预警', '法律法规要求','风险控制文档','合规文件']"
                  v-model="queryParams.wjlbid"/>
          </el-form-item>
        </el-col>
        <el-col :span="7">
          <el-form-item label="业务类别:" prop="ywlbid">
            <ywlb
              v-model="queryParams.ywlb"
              :multiple="true"
              collapse-tags
            />
          </el-form-item>
        </el-col>

        <el-col :span="8">
          <el-form-item label="状态:" prop="zt">
            <zt v-model="queryParams.zt"/>
          </el-form-item>
        </el-col>

        <el-col :span="2" class="right-align">
          <el-button @click="onSearch" type="primary" icon="el-icon-search"
          >查询
          </el-button
          >
        </el-col>
      </el-row>
    </el-form>
    <caseTable ref="tableRef" v-model="queryParams"></caseTable>
  </div>
</template>

<script>
import elInput from "@/components/elementUI/el-input";
import {wjlb, ywlb, xldj, znbm, zt} from "@/components/select";
import caseTable from "./caseTable.vue";
import {apiUtils, dateUtils, DeepCopyUtils} from "@/utils/LeeUtils";

export default {
  name: "compliance",
  components: {
    elInput,
    wjlb,
    ywlb,
    xldj,
    znbm,
    zt,

    caseTable,
  },
  data() {
    return {
      queryParams: {
        wjmc: undefined,
        wjlbid: 'SELECTALL',
        ywlb: ['SELECTALL'],
        xldjid: 'SELECTALL',
        znbmid: 'SELECTALL',
        zt: 'SELECTALL',
        startDate: undefined,
        endDate: undefined,
      },
      date: [],
    };
  },

  methods: {
    onSearch() {
      this.$refs.tableRef.handelSearch()
    },

    handleDataChange(val) {
      this.queryParams.startDate = val[0]
      this.queryParams.endDate = val[1]
    },
  },

  mounted() {
    // 检查是否有查询参数，并且查询参数是否有效（不为空或undefined）
    if (this.$route.query && Object.keys(this.$route.query).length > 0) {
      // 遍历所有查询参数并将它们赋值给queryParams
      for (let key in this.$route.query) {
        if (
          this.$route.query[key] !== undefined &&
          this.$route.query[key] !== null
        ) {
          this.queryParams[key] = this.$route.query[key];
        }
      }
      this.onSearch();
    }
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

.lee-form >>> .el-form-item {
  margin-bottom: 14px;
}
</style>
