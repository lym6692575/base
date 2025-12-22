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
        class="disabled"
      >
        <el-row :gutter="20">
          <el-col :span="24">
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
              <znbm
                v-if="formData.znbmid!==null"
                :disabled="true"
                :showAllOptions="false"
                v-model="formData.znbm.bmmc"
              />
              <elInput v-else :disabled="true" v-model="formData.znbmmc"></elInput>
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
                v-model="formData.ywlb.lbmc"
                readonly
                :style="{ width: '100%' }"
              >
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="文件类别：">
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
import {znbm} from "@/components/select";
import elInput from "@/components/elementUI/el-input.vue";

export default {
  inheritAttrs: false,
  components: {elInput, znbm, fileListDownload},
  data() {
    return {
      formData: {
        // 没几把用单纯为了不报错
        znbm: {bmmc: ""},
        xldj: {djmc: ""},
        ywlb: {ywlb: ""},
        wjlb: {ywlb: ""},
      },
    };
  },
  methods: {
    onInit(_, row) {
      let _row = JSON.parse(JSON.stringify(row));
      this.formData = _row;
    },
    onOpen() {
    },
    onClose() {
      this.$refs["elForm"].resetFields();
    },
    close() {
      this.$emit("update:visible", false);
    },
  },
};
</script>
<style lang="sass" scoped>
// disable样式穿透
.disabled /deep/ .el-input__inner
  color: #606266
  background: #FFFFFF
</style>
