<template>
  <el-dialog
    :title="titleName"
    :visible.sync="dialogVisible"
    v-bind="$attrs"
    v-on="$listeners"
    @open="onOpen"
    @close="onClose"
    width="60%"
  >
    <!--
      内嵌对话框
      传递表格数据 formData
    -->
    <!-- {{ formData }} -->
    <el-row :gutter="15">
      <el-form ref="elForm" :model="formData" size="medium">
        <el-col :span="12">
          <el-form-item
            label="材料名称："
            prop="clmc"
            :rules="[{ required: true, message: '材料名称不能为空' }]"
          >
            <elInput
              placeholder="请输入材料名称"
              v-model="formData.clmc"
            ></elInput>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            label="发布单位："
            prop="fbdwid"
            :rules="[{ required: true, message: '发布单位未选择' }]"
          >
            <znbm :showAllOptions="false" v-model="formData.fbdwid" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item
            label="反馈（截至）时间："
            prop="fksj"
          >
            <el-date-picker
              v-model="formData.fksj"
              format="yyyy-MM-dd"
              value-format="yyyy-MM-dd"
              :style="{ width: '100%' }"
              unlink-panels
              :clearable="false"
            ></el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item
            label="上报要求："
            prop="xgyq"
            :rules="[{ required: true, message: '上报要求不能为空' }]"
          >
            <elInput
              v-model="formData.xgyq"
              type="textarea"
              :autosize="{ minRows: 4, maxRows: 8 }"
              placeholder="请输入上报要求"
            ></elInput>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item>
            <!--
              附件列表
              传递附件数据 formData.fjList
            -->
            <file-upload-list
              ref="upload"
              :fjList="formData.fjList"
            ></file-upload-list>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item style="float: right">
            <el-button type="primary" @click="handleEditFiles">保存</el-button>
            <!-- <el-button
              v-if="postType == 'update'"
              type="primary"
              @click="handlePickFlow"
              >流程选择</el-button
            > -->
          </el-form-item>
        </el-col>
      </el-form>
    </el-row>
  </el-dialog>
</template>

<script>
import fileUploadList from "./fileUploadList.vue";
import { wjlb, ywlb, xldj, znbm, fjlx } from "@/components/select";
import { saveSbtz, updateSbtz } from "@/api/app/materialSubmittal";
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
      formData: {
        fzsj: undefined,
        fzyy: undefined,
        clmc: undefined, // 文件名称
        xgyq: undefined, // 文件编号
        znbmmc: undefined, // 职能部门名称
        fbdwid: undefined, // 职能部门
        fksj:undefined,
        fwsj: undefined, // 发文时间
        xldjid: undefined, // 效力等级
        wjlbid: undefined, // 业务类别
        ywlbid: undefined, // 业务类别
        zt: "1", // 有效性
        fjlxid: undefined, // 附件类型
        fjList: [],
      },
    };
  },
  components: {
    fileUploadList,
    wjlb,
    ywlb,
    xldj,
    znbm,
    fjlx,
  },
  methods: {
    onOpen() {},
    onClose() {
      this.$refs["elForm"].resetFields();
      this.formData.fjList = [];
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
      this.formData = JSON.parse(JSON.stringify(row));
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

          // 判断更新还是保存
          if (this.postType == "save") {
            saveSbtz(formDataWithFiles).then((res) => {
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
            updateSbtz(formDataWithFiles).then((res) => {
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
          }
        } else {
          // 校验不通过，处理失败情况
          this.$message({
            message: "请填写必填信息",
            type: "warning",
          });
        }
      });
    },
  },
  computed: {
    // 编号(文号)动态校验
    xgyqRules() {
      return this.formData.wjlbid === "1"
        ? [{ required: true, message: "规章制度的编号(文号)不能为空" }]
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
        ? [{ required: true, message: "废止时间不能为空" }]
        : [];
    },
    fzyyRules() {
      return this.formData.zt == "0"
        ? [{ required: true, message: "废止原因不能为空" }]
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
