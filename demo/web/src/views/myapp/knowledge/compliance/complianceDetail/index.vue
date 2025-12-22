<template>
  <el-dialog
    :title="titleName"
    :visible.sync="dialogVisible"
    width="50%"
    class="container"
    append-to-body
  >
    <el-row :gutter="15">
      <el-form ref="elForm" :model="formData" size="medium" class="disabled">
        <el-col :span="24">
          <el-form-item label="文件名称：" prop="wjmc">
            <elInput :disabled="true" v-model="formData.wjmc"></elInput>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="编号(文号)：" prop="wjbh">
            <elInput :disabled="true" v-model="formData.wjbh"></elInput>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="职能部门：" prop="znbmid">
            <znbm
              v-if="formData.znbmid!==null"
              :disabled="true"
              :showAllOptions="false"
              v-model="formData.znbmid"
            />
            <elInput v-else :disabled="true" v-model="formData.znbmmc"></elInput>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="发文时间：" prop="fwsj">
            <el-date-picker
              :disabled="true"
              v-model="formData.fwsj"
              format="yyyy-MM-dd"
              value-format="yyyy-MM-dd"
              :style="{ width: '100%' }"
              placeholder="请选择日期"
              clearable
            ></el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="效力等级：" prop="xldjid">
            <xldj
              :disabled="true"
              :showAllOptions="false"
              v-model="formData.xldjid"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="业务类别：" prop="ywlbid">
            <ywlb
              :showAllOptions="false"
              :multiple="true"
              collapse-tags
              v-model="formData.ywlb"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="文件类别：" prop="wjlbid">
            <wjlb
              :disabled="true"
              :showAllOptions="false"
              v-model="formData.wjlbid"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item>
            <!--
              附件列表
              传递附件数据 formData.tdhgwjFj
              文件类型 : 合规文件
            -->
            <file-list-download
              :fileType="'hgwj'"
              ref="upload"
              :fileList="formData.tdhgwjFj"
            ></file-list-download>
          </el-form-item>
        </el-col>
      </el-form>
    </el-row>
  </el-dialog>
</template>

<script>
import elInput from "@/components/elementUI/el-input";
import fileListDownload from "@/views/components/fileListDownload";
import {wjlb, ywlb, xldj, znbm, fjlx} from "@/components/select";
import {saveTDhgwj} from "@/api/app/manager/file";

export default {
  components: {
    elInput,
    wjlb,
    ywlb,
    xldj,
    znbm,
    fjlx,
    fileListDownload,
  },
  props: {
    titleName: {
      type: String,
      default: "新增合规文件",
    },
  },
  data() {
    return {
      dialogVisible: false,
      flowVisible: false,
      formData: {
        wjmc: "", // 文件名称
        wjbh: "", // 文件编号
        znbmmc: "", // 职能部门名称
        znbmid: "", // 职能部门
        fwsj: "", // 发文时间
        xldjid: "", // 效力等级
        ywlb: [], // 业务类别
        wjlbid: "", // 文件类别
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
// disable样式穿透
.disabled /deep/ .el-input__inner
  color: #606266
</style>
