<template>
  <div>
    <input
      type="file"
      ref="fileInput"
      @change="handleFileChange"
      style="display: none"
    />
    <div :span="24" class="header">
      <span>附件列表：</span>
      <!-- <el-button
        type="primary"
        @click="addRow"
        :disabled="isAnyRowEditing"
        plain
        class="header-addButton"
      >
        新增
      </el-button> -->
      <el-button
        type="primary"
        @click="addRow"
        plain
        class="header-addButton"
        size="small"
        round
      >
        新增附件
      </el-button>
    </div>
    <el-table
      class="custom-table mt20"
      :data="tableData"
      style="width: 100%"
      @selection-change="$emit('input', $event)"
    >
      <el-table-column type="index" min-width="10%" label="序号" />
      <!-- <el-table-column label="附件类型：" :min-width="`15%`">
        <template slot-scope="{ row }">
          <fjlx
            class="fjlx"
            :showAllOptions="false"
            v-model="row.fjlxid"
            :disabled="!row.isEditing"
            :key="generatedKey + 'fjlx'"
            :ssyw="'1'"
          ></fjlx>
        </template>
      </el-table-column> -->
      <el-table-column label="文件名" :min-width="`25%`">
        <template slot-scope="{ row }">
          <div class="full-width-button">
            <el-tooltip
              class="item"
              effect="light"
              content="点击选择或替换文件"
              placement="top"
            >
              <el-button
                size="small"
                class="button"
                :disabled="!row.isEditing"
                :class="{ 'disabled-button': !row.isEditing }"
                @click="openFileInput(row)"
                plain
                icon="el-icon-upload"
              >
                {{
                  row.fileInfo
                    ? row.fileInfo.name
                    : row.isEditing
                    ? "点击添加文件"
                    : ""
                }}
              </el-button>
            </el-tooltip>
          </div>
        </template>
      </el-table-column>
      <!-- <el-table-column label="提供部门" :min-width="`20%`">
        <template slot-scope="{ row }">
          <el-input
            v-model="row.tgbm"
            placeholder="请输入提供部门"
            :disabled="!row.isEditing"
          ></el-input>
        </template>
      </el-table-column>
      <el-table-column label="提供人" :min-width="`20%`">
        <template slot-scope="{ row }">
          <el-input
            v-model="row.tgr"
            placeholder="请输入提供人"
            :disabled="!row.isEditing"
          ></el-input>
        </template>
      </el-table-column> -->
      <el-table-column label="功能键" :min-width="`20%`">
        <template slot-scope="{ row }">
          <el-button
            v-if="!row.isEditing"
            @click="editRow(row)"
            size="small"
            type="primary"
            plain
            >编辑</el-button
          >
          <el-button
            v-else
            @click="saveRow(row)"
            type="primary"
            plain
            size="small"
            >确认</el-button
          >
          <el-button type="danger" plain @click="deleteRow(row)" size="small"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { fjlx } from "@/components/select";
export default {
  props: {
    value: {
      type: Array,
      default: () => [],
    },
    fjList: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      currentRow: null, // 当前点击的行
    };
  },
  components: {
    fjlx,
  },
  methods: {
    openFileInput(row) {
      this.currentRow = row; // 记录当前点击的行
      row.fileSelected = false; // 重置文件选择状态
      row.fileChanged = true; // 编辑此行文件被修改过给后端使用
      this.$refs.fileInput.value = ""; // 清空之前选择的文件
      this.$refs.fileInput.click();
    },
    handleFileChange(event) {
      const fileList = event.target.files; // 上传文件数据
      const newFiles = Array.from(fileList); // 数据类型转化为数组

      if (
        this.currentRow &&
        this.currentRow.fileSelected &&
        this.currentRow.isEditing
      ) {
        // 如果文件已选择且当前行处于编辑状态，则替换文件
        this.$set(this.currentRow, "fileInfo", newFiles[0]);
      } else {
        // 否则，新增文件
        this.$set(this.currentRow, "fileSelected", true);
        this.$set(this.currentRow, "fileInfo", newFiles[0]);
      }
    },
    // 移除文件
    removeFile(index) {
      this.tableData[index].fileSelected = false;
      this.tableData[index].fileInfo = null;
    },
    // 表格相关
    // 添加行
    addRow() {
      this.tableData.push({
        fjlxid: "",
        inputValue: "",
        // 操作列状态
        isEditing: true,
        fileSelected: false,
        fileInfo: null,
      });
    },
    // 编辑当前行
    editRow(row) {
      row.isEditing = true;
    },
    // 保存当前行
    saveRow(row) {
      // 保存行的逻辑
      // if (!row.fjlxid) {
      //   this.$message({
      //     message: "请选择附件类型",
      //     type: "warning",
      //   });
      //   return;
      // }
      if (!row.fileInfo) {
        this.$message({
          message: "请上传文件文件",
          type: "warning",
        });
        return;
      }
      row.isEditing = false;
    },
    // 删除当前行
    deleteRow(row) {
      const index = this.tableData.indexOf(row);
      this.tableData.splice(index, 1);
    },
  },
  computed: {
    // 处理数据
    tableData() {
      const tableData = [];
      const fjList = this.fjList;
      // console.log("fjList", this.fjList);
      fjList.forEach((item) => {
        tableData.push({
          ...item,
          fjlxid: item.fjlxid,
          isEditing: false,
          fileSelected: false,
          fileInfo: { name: item.fjmc }, // 仅仅为了遍历时显示名称，懒得再多加一个属性了
        });
      });

      return tableData;
    },
    // 判断是否存在编辑状态
    isAnyRowEditing() {
      return this.tableData.some((row) => row.isEditing);
    },
    // 生成 key 值
    generatedKey() {
      const timestamp = new Date().getTime();
      const random = Math.floor(Math.random() * 1000); // 生成一个随机数，范围为0到999
      return timestamp + "_" + random;
    },
  },
};
</script>

<style lang="sass" scoped>
.header
  line-height: 36px
  span
    font-weight: 700
  &-addButton
    float: right
.full-width-button
  display: flex
  justify-content: center
  align-items: center
  height: 36px
  .button
    width: 100%
    height: 100%

// 替换禁用样式
.disabled-button
  color: gray
.fjlx >>> .el-input__inner
  color: #606266 !important

// 居中设置
.custom-table>>>th
  text-align: center
.custom-table>>>td
  text-align: center
</style>
</style>
