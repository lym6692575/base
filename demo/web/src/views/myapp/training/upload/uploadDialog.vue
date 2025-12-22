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
      <el-form ref="elForm" :model="formData" size="medium">
        <el-col :span="24">
          <el-form-item
            label="资料名称："
            prop="zlmc"
            label-width="100px"
            :rules="[{ required: true, message: '文件名称不能为空' }]"
          >
            <elInput v-model="formData.zlmc"></elInput>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item>
            <file-upload-list
              ref="upload"
              :fileList="formData.tdpxzlFj"
              @is-editing="isChildEditing = $event"
            ></file-upload-list>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item style="float: right">
            <el-button type="primary" @click="handleEditFiles">保存</el-button>
          </el-form-item>
        </el-col>
      </el-form>
    </el-row>
  </el-dialog>
</template>

<script>
import fileUploadList from "@/views/components/fileListUpload";
import elInput from "@/components/elementUI/el-input";
import { ywlb, xldj, znbm, fjlx } from "@/components/select";
import { saveTDpxzl, updateTDpxzl } from "@/api/app/training";
import {DeepCopyUtils} from "@/utils/LeeUtils";
import {mapGetters} from 'vuex'
export default {
  components: {
    fileUploadList,
    elInput,
    ywlb,
    xldj,
    znbm,
    fjlx,
  },
  props: {
    postType: {
      type: String,
      require: true,
    },
    titleName: {
      type: String,
      default: "新增交流资料",
    },
  },

  data() {
    return {
      dialogVisible: false,
      formData: {
        zlmc: undefined, // 文件名称
        tDpxzlFj: [], // 附件列表
      },
      isChildEditing: false,
    };
  },
  methods: {
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
      // 校验是否有子组件正在编辑
      if (this.isChildEditing) {
        this.$message({
          message: "附件列表正在被编辑状态，无法保存。请检查确认所上传的附件。",
          type: "warning",
        });
        return;
      }
      this.$refs.elForm.validate((valid) => {
        // 校验通过，执行提交逻辑
        if (valid) {
          const formDataWithFiles = new FormData(); // 表单
          const form = DeepCopyUtils.deepCopy(this.formData);
          // 如果是保存行为,添加创建人字段
          if(this.postType ==="save"){
            form.cjr=this.userInfo.realname
          }
          // 附件列表
          const tableData = this.$refs.upload.tableData; // 附件列表
          form.tdpxzlFj = tableData;

          // 处理文件
          tableData.forEach((item) => {
            formDataWithFiles.append("files", item.fileInfo);
          });

          // 处理实体数据
          formDataWithFiles.append("data", JSON.stringify(form));
          formDataWithFiles.append("files", "files");
          if (this.postType == "save") {
            saveTDpxzl(formDataWithFiles).then((res) => {
              if (res.result) {
                this.$message({
                  message: res.msg,
                  type: "success",
                });
                // 调用父组件刷新
                this.$emit("data-saved");
                this.dialogVisible = false;
              }
            });
          } else {
            updateTDpxzl(formDataWithFiles).then((res) => {
              if (res.result) {
                this.$message({
                  message: res.msg,
                  type: "success",
                });
                // 调用父组件刷新
                this.$emit("data-saved");
                this.dialogVisible = false;
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
    ...mapGetters(['userInfo']),
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
