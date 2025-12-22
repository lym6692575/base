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
    <!-- {{ tableData }}
    isAnyRowEditing:{{ isAnyRowEditing }} -->
    <el-table
      class="custom-table mt20"
      :data="tableData"
      style="width: 100%"
      @selection-change="$emit('input', $event)"
    >
      <el-table-column type="index" min-width="10%" label="序号" />
      <el-table-column label="附件类型：" :min-width="`20%`">
        <template slot-scope="{ row }">
          <fjlx
            class="fjlx"
            :showAllOptions="false"
            v-model="row.fjlxid"
            :disabled="!row.isEditing"
            :key="generatedKey + 'fjlx'"
            :ssyw="ssyw"
          ></fjlx>
        </template>
      </el-table-column>
      <el-table-column label="文件名" :min-width="`30%`">
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
      <el-table-column label="功能键" :min-width="`20%`">
        <template slot-scope="{ row }">
          <el-button
            v-if="!row.isEditing"
            @click="editRow(row)"
            type="primary"
            plain
            size="small"
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
    fileList: {
      type: Array,
      default: () => [],
    },
    ssyw: {
      type: String,
      default: "2",
    },
  },
  data() {
    return {
      tableData: [],
      currentRow: null,
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
      console.log(this.tableData);
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
      let index = this.tableData.indexOf(row);
      this.tableData.splice(index, 1, { ...row, isEditing: true });
    },
    // 保存当前行
    saveRow(row) {
      // 保存行的逻辑
      if (!row.fjlxid) {
        this.$message({
          message: "请选择附件类型",
          type: "warning",
        });
        return;
      }
      if (!row.fileInfo) {
        this.$message({
          message: "请选择合规文件",
          type: "warning",
        });
        return;
      }
      let index = this.tableData.indexOf(row);
      this.tableData.splice(index, 1, { ...row, isEditing: false });
    },
    // 删除当前行
    deleteRow(row) {
      const index = this.tableData.indexOf(row);
      this.tableData.splice(index, 1);
      console.log(this.tableData);
    },

    // 处理文件列表数据
    fileListToTableData(fileList) {
      return fileList.map((file) => ({
        ...file,
        isEditing: false,
        fileSelected: false,
        fileInfo: { name: file.fjmc },
      }));
    },
  },
  computed: {
    // 处理数据
    isAnyRowEditing() {
      return this.tableData.some((item) => item.isEditing);
    },
    generatedKey() {
      const timestamp = new Date().getTime();
      const random = Math.floor(Math.random() * 1000);
      return timestamp + "_" + random;
    },
  },

  watch: {
    fileList: {
      handler(newFileList) {
        this.tableData = this.fileListToTableData(newFileList);
      },
      deep: true,
      immediate: true, // 立即触发处理程序
    },
    isAnyRowEditing(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.$emit("is-editing", newVal);
      }
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