<template>
  <div>
    <input
      type="file"
      ref="fileInput"
      @change="handleFileChange"
      style="display: none"
    />
    <!--    <h1>generalFileList</h1>-->
    <!--    {{ generalFileList }}-->
    <!--    <h1>flowFormParams</h1>-->
    <!--    {{ flowFormParams }}-->
    <div :span="24" class="header">
      <span>流程通用附件列表：</span>
      <el-tooltip
        :disabled="(flowFormParams.tdywlcHjList && flowFormParams.tdywlcHjList.length>0)"
        class="item"
        effect="dark"
        content="请先添加流程环节再添加通用附件"
        placement="right"
      >
        <el-button
          type="primary"
          @click="addRow"
          plain
          class="header-addButton"
          size="small"
          round
          :disabled="!(flowFormParams.tdywlcHjList && flowFormParams.tdywlcHjList.length>0)"
        >
          新增通用附件
        </el-button>
      </el-tooltip>
    </div>
    <el-table
      class="custom-table mt20"
      :data="tableData"
      style="width: 100%"
      @selection-change="$emit('input', $event)"
    >
      <el-table-column type="index" min-width="10%" label="序号"/>
      <el-table-column label="附件类型：" :min-width="`20%`">
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
      <el-table-column label="提供部门" :min-width="`20%`">
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
      </el-table-column>
      <el-table-column label="功能键" :min-width="`20%`">
        <template slot-scope="{ row }">
          <el-button
            v-if="!row.isEditing"
            @click="editRow(row)"
            type="primary"
            plain
          >编辑
          </el-button
          >
          <el-button v-else @click="saveRow(row)" type="primary" plain
          >确认
          </el-button
          >
          <el-button type="danger" plain @click="deleteRow(row)"
          >删除
          </el-button
          >
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import {fjlx} from "@/components/select";
import {mapGetters, mapActions} from "vuex";

export default {
  props: {
    index: {
      type: Number,
      default: null,
    },
  },
  data() {
    return {
      tableData: [],
      currentRow: null, // 当前点击的行
    };
  },
  model: {
    // 需要双向绑定的 props 变量名称，也就是父组件通过 v-model 与子组件双向绑定的变量
    prop: "value",
    // 定义由 $emit 传递变量的名称
    event: "newValue",
  },
  watch: {
    // 监听 sonValue 临时变量，如果临时变量发生变化，那么通过 $emit 将新的值传递给父组件
    filesTagsList(value) {
      // 【注意】newValue x需要和 model.event 定义的值一致
      this.$emit("newValue", this.filesTagsList);
    },
    generalFileList: {
      handler() {
        this.initializeTableData();
      },
      deep: true,
    },
    isAnyRowEditing(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.$emit("is-editing", newVal);
      }
    },
  },
  components: {
    fjlx,
  },
  methods: {
    ...mapActions("flow", [
      "deleteFilesAndName",
      "setflowFormParams",
      "setGeneralFileList",
      "removeGeneralFileList",
    ]),

    // 初始化tableData
    initializeTableData() {
      const tableData = [];
      if (this.generalFileList && this.generalFileList.length > 0)
        this.generalFileList.forEach((item, i) => {
          tableData.push({
            ...item,
            fjlxid: item.fjlxid,
            isEditing: false,
            fileSelected: false,
            isGeneral: true, // 标记为通用附件
            fileInfo: item.fileChanged ? item.fileInfo : {name: item.fjmc},
          });
        });

      this.tableData = tableData;
    },

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
        temporaryId: this.generalFileList.length, // 临时id,用于删除
        fjlxid: "",
        inputValue: "",
        // 操作列状态
        isEditing: true,
        isGeneral: true, // 是否是通用附件
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
      if (!row.fjlxid) {
        this.$message({
          message: "请选择附件类型",
          type: "warning",
        });
        return;
      }
      if (!row.fileInfo) {
        this.$message({
          message: "请选择上传附件",
          type: "warning",
        });
        return;
      }
      row.isEditing = false;

      // 保存vuex状态
      this.setGeneralFileList(this.tableData);
    },
    // 删除当前行
    deleteRow(row) {
      const index = this.tableData.indexOf(row);
      this.tableData.splice(index, 1);

      // 修改vuex状态
      this.removeGeneralFileList({
        generalFileList: this.tableData,
        deletedId: row.temporaryId,
      });
    },
  },
  computed: {
    ...mapGetters(["flowFormParams", "filesData", "generalFileList"]),
    // 监听编辑状态
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
  created() {
    this.initializeTableData();
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

.fjlx /deep/ .el-input__inner
  color: #606266 !important

// 居中设置
.custom-table /deep/ th
  text-align: center

.custom-table /deep/ td
  text-align: center
</style>
</style>
