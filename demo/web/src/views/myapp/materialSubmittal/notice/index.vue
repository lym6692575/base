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
        <el-col :span="5">
          <el-form-item label="材料名称:" prop="clmc">
            <elInput v-model="queryParams.clmc" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="发布时间:" prop="date">
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
              :clearable="false"
              @change="handleDataChange"
            ></el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="11" class="right-align">
          <el-button @click="onSearch" type="primary" icon="el-icon-search"
            >查询</el-button
          >
          <el-button
            @click="onAdd"
            type="primary"
            icon="el-icon-circle-plus-outline"
            >新增</el-button
          >
        </el-col>
      </el-row>
    </el-form>
    <el-row :gutter="20">
      <el-col :span="24">
        <notice-table
          class="table"
          ref="tableRef"
          v-model="queryParams"
        ></notice-table>
        <notice-dialog
          ref="addRef"
          :postType="'save'"
          :titleName="'新增上报通知'"
          @data-saved="onSearch()"
        ></notice-dialog>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import elInput from "@/components/elementUI/el-input";
import { ywlb, xldj, znbm, zt } from "@/components/select";
import sjid from "@/views/components/cascader/sjid.vue";
import { dateUtils, DeepCopyUtils } from "@/utils/LeeUtils";
import noticeTable from "./noticeTable.vue";
import noticeDialog from "./noticeDialog";

export default {
  name: "file",
  data() {
    let defaultDates = this.defaultValue();
    return {
      message: "文件管理",
      queryParams: {
        page: "1",
        rows: "10",
        clmc: undefined,
        startDate: defaultDates[0],
        endDate: defaultDates[1]
      },
      date: this.defaultValue(),
    };
  },
  components: {
    elInput,
    ywlb,
    xldj,
    znbm,
    sjid,
    zt,

    noticeTable,
    noticeDialog,
  },
  methods: {
    // 查询
    onSearch() {
      this.$refs.tableRef.handelSearch()
    },

    // 添加
    onAdd() {
      this.$refs.addRef.dialogVisible = true;
    },

    handleDataChange(val) {
      this.date = [...val];
      this.queryParams.startDate = val[0];
      this.queryParams.endDate = val[1];
    },

    // 获取默认查询日期
    defaultValue() {
      const currentDate = new Date();
      const threeMonthsAgo = new Date();
      threeMonthsAgo.setMonth(currentDate.getMonth() - 3);
      return [
        dateUtils.format(threeMonthsAgo, "YYYY-MM-DD"),
        dateUtils.format(currentDate, "YYYY-MM-DD"),
      ];
    },


  },
  // 添加生命周期函数
  mounted() {},
  beforeDestroy() {},
  computed: {},
};
</script>

<style scoped>
.container {
  width: 100%;
}
.parent-container {
  display: flex;
  flex-direction: row;
}
.zt,
.search {
  margin-left: 10px;
}
.table {
  margin-top: 10px;
}
</style>
