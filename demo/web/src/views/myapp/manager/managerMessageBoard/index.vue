<template>
  <div>
    <el-row :gutter="20">
      <el-form
        ref="elForm"
        :model="formParams"
        size="medium"
        label-width="100px"
        label-position="left"
      >
        <el-col :span="6">
          <el-form-item label="答复状态：" prop="lydf">
            <el-select
              v-model="formParams.lydf"
              placeholder="请选择下拉选择"
              clearable
              :style="{ width: '100%' }"
            >
              <el-option
                v-for="(item, index) in lydfOptions"
                :key="index"
                :label="item.label"
                :value="item.value"
                :disabled="item.disabled"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="发布日期：" prop="date">
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
        <el-col :span="10" class="right-align">
          <el-button @click="onSearch" type="primary" icon="el-icon-search"
          >查询
          </el-button
          >
          <el-button
            @click="getExcel"
            type="primary"
            icon="el-icon-folder-opened"
          >导出
          </el-button
          >
        </el-col>
      </el-form>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="24">
        <message-board-table
          class="table"
          ref="tableRef"
          v-model="formParams"
        ></message-board-table>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import messageBoardTable from "./messageBoardTable.vue";
import {dateUtils, DeepCopyUtils} from "@/utils/LeeUtils";

export default {
  name: "file",
  data() {
    let defaultDates = this.defaultValue();
    return {
      message: "留言板",
      date: [...defaultDates],
      formParams: {
        lydf: "",
        startDate: defaultDates[0],
        endDate: defaultDates[1]
      },
      lydfOptions: [
        {
          label: "全部",
          value: "",
        },
        {
          label: "未答复",
          value: 0,
        },
        {
          label: "已答复",
          value: 1,
        },
      ],
    };
  },
  components: {
    messageBoardTable,
  },
  methods: {
    // 查询
    onSearch() {
      this.$refs.tableRef.handelSearch();
    },

    // 导出表格
    getExcel() {
      this.$refs.tableRef.getExcel();
    },

    // 添加
    onAdd() {
      this.$refs.addRef.dialogVisible = true;
    },

    handleDataChange(val) {
      this.date = [...val];
      this.formParams.startDate = val[0];
      this.formParams.endDate = val[1];
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
  mounted() {
  },
  beforeDestroy() {
  },
  computed: {},
};
</script>

<style scoped>

.table {
  margin-top: 10px;
}
</style>
