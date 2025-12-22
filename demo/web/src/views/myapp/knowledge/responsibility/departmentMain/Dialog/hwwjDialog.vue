<template>
  <div>
    <el-dialog
      v-bind="$attrs"
      v-on="$listeners"
      @open="onOpen"
      @close="onClose"
      title="合规文件详情"
    >
      <!-- {{ formData }} -->
      <el-form
        ref="elForm"
        :model="formData"
        size="medium"
        label-position="left"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="文件名称：">
              <el-input
                v-model="formData.wjmc"
                readonly
                :style="{ width: '100%' }"
              >
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="编号(文号)：">
              <el-input
                v-model="formData.wjbh"
                readonly
                :style="{ width: '100%' }"
              >
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职能部门：">
              <el-input
                v-model="formData.znbm.bmmc"
                readonly
                :style="{ width: '100%' }"
              >
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发文时间：">
              <el-input
                v-model="formData.fwsj"
                readonly
                :style="{ width: '100%' }"
              >
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="效力等级：">
              <el-input
                v-model="formData.xldj.djmc"
                readonly
                :style="{ width: '100%' }"
              >
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业务类别：">
              <el-input
                v-model="formData.wjlb.lbmc"
                readonly
                :style="{ width: '100%' }"
              >
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label-width="0px">
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
        </el-row>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import fileListDownload from "@/views/components/fileListDownload";
export default {
  inheritAttrs: false,
  components: { fileListDownload },
  data() {
    return {
      formData: {
        // 没几把用单纯为了不报错
        znbm: { bmmc: "" },
        xldj: { djmc: "" },
        wjlb: { wjlb: "" },
      },
    };
  },
  methods: {
    onInit(_, row) {
      let _row = JSON.parse(JSON.stringify(row));
      this.formData = _row;
    },
    onOpen() {},
    onClose() {
      this.$refs["elForm"].resetFields();
    },
    close() {
      this.$emit("update:visible", false);
    },
  },
};
</script>
