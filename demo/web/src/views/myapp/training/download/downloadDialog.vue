<template>
  <el-dialog
    :title="titleName"
    :visible.sync="dialogVisible"
    width="50%"
  >
    <!--
      内嵌对话框
      传递表格数据 formData
    -->
    <el-dialog width="45%" title="流程选择" append-to-body> </el-dialog>
    <el-row :gutter="15">
      <el-form ref="elForm" label-width="100px" :model="formData" size="medium">
        <el-col :span="24">
          <el-form-item
            label="资料名称："
            prop="wjmc"
            :rules="[{ required: true, message: '文件名称不能为空' }]"
          >
            <elInput v-model="formData.wjmc"></elInput>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item>
            <!--
              附件列表
              传递附件数据 formData.tdhgwjFj
            -->
            <!-- <file-upload-list
              ref="upload"
              :tdhgwjFj="formData.tdhgwjFj"
            ></file-upload-list> -->
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item style="float: right">
            <el-button type="primary" @click="handleEditFiles">保存</el-button>
            <el-button type="primary" @click="handlePickFlow"
              >流程选择</el-button
            >
          </el-form-item>
        </el-col>
      </el-form>
    </el-row>
  </el-dialog>
</template>

<script>
// import fileUploadList from "./fileUploadList.vue";

import elInput from "@/components/elementUI/el-input";
import { ywlb, xldj, znbm, fjlx } from "@/components/select";
import { saveTDhgwj } from "@/api/app/manager/file";
export default {
  components: {
    // fileUploadList,
    elInput,
    ywlb,
    xldj,
    znbm,
    fjlx,
  },
  props: {
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
      formData: {
        wjmc: "", // 文件名称
        wjbh: "", // 文件编号
        znbmmc: "", // 职能部门名称
        znbmid: "", // 职能部门
        fwsj: "", // 发文时间
        xldjid: "", // 效力等级
        ywlbid: "", // 业务类别
        zt: "1", // 有效性
        fjlxid: "", // 附件类型
      },
    };
  },
  methods: {
    // 打开流程选择对话框的回调函数
    initOpenYwlcDialog() {
      this.$nextTick(() => {
        this.$refs.picker.openDialogCallback();
      });
    },

    // 关闭对话框给子组件用的
    handleCloseYwlcDialog() {
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
          console.log("tableData", tableData);

          // 处理文件
          tableData.forEach((item) => {
            formDataWithFiles.append("files", item.fileInfo);
          });

          // 处理实体数据
          form.tdhgwjFj = tableData;
          formDataWithFiles.append("data", JSON.stringify(form));
          formDataWithFiles.append("files", "files");

          saveTDhgwj(formDataWithFiles).then((res) => {
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
