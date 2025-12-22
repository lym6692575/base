<template>
  <el-dialog
    title="上报情况"
    :visible.sync="dialogVisible"
    v-bind="$attrs"
    v-on="$listeners"
    @open="onOpen"
    @close="onClose"
    width="70%"
  >
    <!-- <h1>formData</h1>
    {{ formData }}
    <h1>tableData</h1>
    {{ tableData }} -->
    <el-table
      ref="singleTable"
      :data="tableData"
      border
      style="width: 100%"
      stripe
      class="custom-table"
      :header-cell-style="{
        'text-align': 'center',
      }"
      :row-style="{
        height: '0',
        'text-align': 'center',
      }"
      :cell-style="{
        padding: '6px',
        'text-align': 'center',
      }"
    >
      <el-table-column type="index" width="60" label="序号"/>
      <el-table-column property="clmc" label="材料名称" min-width="12%">
      </el-table-column>
      <el-table-column show-overflow-tooltip property="xgyq" label="上报要求" min-width="20%"/>
      <el-table-column property="fwsj" label="附件" width=100>
        <template slot-scope="scope">
          <el-popover ref="elPopover"
                      placement="bottom"
                      width="700"
                      trigger="click"
                      :teleported="false"
                      @after-enter="handleFixPosition">
            <file-list-download
              :fileType="'clbs'"
              ref="upload"
              :fileList="scope.row.fjList"
              :showSubList ="false"
            ></file-list-download>
            <el-button slot="reference" type="text">附件列表</el-button>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column property="sbdw" label="上报单位" min-width="10%"/>
      <el-table-column property="cjsj" label="上报时间" width=120 />
      <!-- <el-table-column label="操作" min-width="10%">
        <template slot-scope="scope">
          <el-button
            slot="reference"
            size="mini"
            type="primary"
            @click="handleEdit(scope.$index, scope.row)"
            >上传</el-button
          >
        </template>
      </el-table-column> -->
    </el-table>
  </el-dialog>
</template>

<script>
import {wjlb, ywlb, xldj, znbm, fjlx} from "@/components/select";
import {saveClbs, updateSbtz} from "@/api/app/materialSubmittal/submit";
import fileListDownload from "@/views/components/fileListDownload";

export default {
  inheritAttrs: false,
  props: {
    postType: {
      type: String,
      require: true,
    },
    switchDisabled: {
      type: Boolean,
      default: true,
    },
    titleName: {
      type: String,
      default: "新增合规文件",
    },
  },
  data() {
    return {
      dialogVisible: false,
      activeNames: ["1"],
      rawData: [],
      formData: {
        fzsj: undefined,
        fzyy: undefined,
        sbdwid: undefined, // 职能部门
        fjList: [],
      },
      page: 1,
      row: 10,
      Loading: false,
    };
  },
  components: {
    fileListDownload,
    wjlb,
    ywlb,
    xldj,
    znbm,
    fjlx,
  },
  methods: {
    // 修复位置问题
    handleFixPosition() {
      this.$refs.elPopover.updatePopper() // 注意主要是这里
    },
    onOpen() {
    },
    onClose() {
      // this.$refs["elForm"].resetFields();
      // this.formData.fjList = [];
    },
    close() {
      this.$emit("update:visible", false);
    },

    // 打开流程选择对话框的回调函数
    initOpenYwlcDialog() {
      this.$nextTick(() => {
        this.$refs.picker.openDialogCallback();
      });
    },

    // 关闭对话框给选择流程对话框子组件用的
    handleCloseYwlcDialog() {
      this.$emit("data-saved");
      this.dialogVisible = false;
      this.flowVisible = false;
    },

    // 打开对话框逻辑
    openDialog(index, row) {
      console.log(index, row);
      this.rawData = JSON.parse(JSON.stringify(row));
      this.dialogVisible = true; // 打开对话框
    },

    // 打开内嵌选择流程对话框
    handlePickFlow() {
      this.flowVisible = true;
    },

    // 保存或更新
    handleEditFiles() {
      this.$refs.elForm.validate((valid) => {
        // 校验通过，执行提交逻辑
        if (valid) {
          const formDataWithFiles = new FormData(); // 表单
          const form = this.formData;
          // 附件列表
          const tableData = this.$refs.upload.tableData; // 附件列表

          // 处理文件
          tableData.forEach((item) => {
            formDataWithFiles.append("files", item.fileInfo);
          });

          // 处理实体数据
          form.fjList = tableData;
          formDataWithFiles.append("data", JSON.stringify(form));
          formDataWithFiles.append("files", "files");

          saveClbs(formDataWithFiles).then((res) => {
            if (res.result) {
              this.$message({
                message: res.msg,
                type: "success",
              });
              this.dialogVisible = false;
              // 调用父组件刷新
              this.$emit("data-saved");
            }
          });
        } else {
          // 校验不通过，处理失败情况
          this.$message({
            message: "请填写必填信息",
            type: "warning",
          });
        }
      });
    },
    // index方法
    indexMethods(index) {
      return (this.page - 1) * this.rows + index + 1;
    },
  },
  computed: {
    tableData() {
      if (this.rawData && this.rawData.tdclbs) {
        return this.rawData.tdclbs.map((item) => {
          return {...item, clmc: this.rawData.clmc, xgyq: this.rawData.xgyq};
        });
      }
      return [];
    },
    // 编号(文号)动态校验
    xgyqRules() {
      return this.formData.wjlbid === "1"
        ? [{required: true, message: "规章制度的编号(文号)不能为空"}]
        : [];
    },
    generatedKey() {
      const timestamp = new Date().getTime();
      const random = Math.floor(Math.random() * 1000); // 生成一个随机数，范围为0到999
      return timestamp + "_" + random;
    },
    // 动态校验
    fzsjRules() {
      return this.formData.zt == "0"
        ? [{required: true, message: "废止时间不能为空"}]
        : [];
    },
    fzyyRules() {
      return this.formData.zt == "0"
        ? [{required: true, message: "废止原因不能为空"}]
        : [];
    },
  },
};
</script>
<style lang="sass" scoped>
.container
  > div:not(:first-child)
    margin-top: 10px

.fwsj
  display: inline-flex
  align-items: center
  white-space: nowrap
  margin-right: 10px
</style>
