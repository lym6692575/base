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
<!--    {{ formData }}-->
    <el-row :gutter="15">
      <el-form ref="elForm" :model="formData" size="medium">
        <el-col :span="24">
          <el-form-item
            label="上报单位（所属上级\部门名称   例：省机关\企管法规部）："
            prop="sbdw"
            :rules="[{ required: true, message: '上报单位未填写' }]"
          >
            <!--            <znbm :showAllOptions="false" v-model="formData.sbdwid" />-->
            <el-input v-model="formData.sbdw" placeholder="请输入上报单位名称"></el-input>
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
import {wjlb, ywlb, xldj, znbm, fjlx} from "@/components/select";
import {saveClbs, checkSbqk} from "@/api/app/materialSubmittal/submit";
import fileListDownload from "@/views/components/fileListDownload/index.vue";

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
        // sbdwid: undefined, // 职能部门id
        sbdw: undefined, // 职能部门
        sbtzid: undefined,
        fjList: [],
      },
    };
  },
  components: {
    fileListDownload,
    fileUploadList,
    wjlb,
    ywlb,
    xldj,
    znbm,
    fjlx,
  },
  methods: {
    onOpen() {
    },
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
      this.formData.sbtzid = JSON.parse(JSON.stringify(row.id));
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
          checkSbqk({sbtzid: this.formData.sbtzid, sbdw: this.formData.sbdw}).then((res) => {
            if (res['result'] === true) {
              this.$confirm('该上报单位已存在上报数据, 继续保存将替换原数据, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
              }).then(() => {
                save()
              }).catch(() => {
              });
            } else {
              save()
            }
          })
          const save = () => {
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
              if (res["result"]) {
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
