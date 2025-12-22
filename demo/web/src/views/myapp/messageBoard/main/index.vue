<template>
  <div>
    <el-form
      ref="elForm"
      label-position="left"
      :model="formParams"
      size="medium"
      label-width="80px"
    >
      <el-row :gutter="20">
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
              :clearable="false"
              unlink-panels
              @change="handleDataChange"
            ></el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="16" class="right-align">
          <el-button
            class="search"
            @click="onSearch"
            type="primary"
            icon="el-icon-search"
          >查询
          </el-button
          >
          <el-button
            @click="onAdd"
            type="primary"
            icon="el-icon-circle-plus-outline"
          >发布留言
          </el-button
          >
        </el-col>
      </el-row>
    </el-form>
    <el-row :gutter="20">
      <el-col :span="24">
        <message-board-table
          class="table"
          ref="tableRef"
          v-model="formParams"
        ></message-board-table>
      </el-col>
    </el-row>
    <message-board-add-dialog
      :visible.sync="dialogVisible"
    ></message-board-add-dialog>
  </div>
</template>

<script>
import leeInput from "@/components/elementUI/el-input";
import messageBoardTable from "./messageBoardTable.vue";
import messageBoardAddDialog from "./messageBoardAddDialog";
import {dateUtils, DeepCopyUtils} from "@/utils/LeeUtils";

export default {
  name: "file",
  components: {
    leeInput,

    messageBoardTable,
    messageBoardAddDialog,
  },
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
          value: 1,
        },
        {
          label: "已答复",
          value: 2,
        },
      ],
      dialogVisible: false,
    };
  },
  methods: {
    // 查询
    onSearch() {
      this.$refs.tableRef.handelSearch();
    },

    // 添加
    onAdd() {
      this.dialogVisible = true;
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
.search {
  margin-left: 10px;
}

.table {
  margin-top: 10px;
}
</style>
