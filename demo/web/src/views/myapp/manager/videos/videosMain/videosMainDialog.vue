<template>
  <div>
    <el-dialog
      v-bind="$attrs"
      v-on="$listeners"
      @open="onOpen"
      @close="onClose"
      :title="postType === 'add' ? '新增视频链接' : '编辑视频链接'"
    >
      <!-- <h1>formData</h1>
      {{ formData }} -->
      <el-form
        ref="elForm"
        :model="formData"
        :rules="rules"
        size="medium"
        label-position="left"
      >
        <el-form-item label="视频名称" prop="spmc">
          <el-input
            v-model="formData.spmc"
            placeholder="请输入视频名称"
            show-word-limit
            clearable
            :style="{ width: '100%' }"
          ></el-input>
        </el-form-item>
        <el-form-item label="视频链接地址" prop="spljdz">
          <el-input
            v-model="formData.spljdz"
            placeholder="请输入视频链接地址"
            show-word-limit
            clearable
            :style="{ width: '100%' }"
          ></el-input>
        </el-form-item>
        <el-form-item label="排序" prop="px">
          <el-input
            v-model="formData.px"
            placeholder="请输入视频链接地址"
            show-word-limit
            clearable
            :style="{ width: '100%' }"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="close">取消</el-button>
        <el-button type="primary" @click="handelConfirm">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { saveGwsp } from "@/api/app/manager/videos";
export default {
  inheritAttrs: false,
  components: {},
  props: {
    postType: {
      type: String,
      require: true,
    },
    gwglid: {
      type: Array,
      default: () => {
        [];
      },
    },
  },
  data() {
    return {
      formData: {
        spmc: undefined,
        spljdz: undefined,
        px: undefined,
        gwid: undefined,
      },
      rules: {
        spmc: [
          {
            required: true,
            message: "请输入视频名称",
            trigger: "blur",
          },
        ],
        spljdz: [
          {
            required: true,
            message: "请输入视频链接地址",
            trigger: "blur",
          },
        ],
        px: [
          {
            required: true,
            message: "请输入排序",
            trigger: "blur",
          },
          {
            pattern: /^[0-9]+$/,
            message: "排序只能是整数数字",
            trigger: "blur",
          },
        ],
      },
    };
  },
  computed: {},
  watch: {
    gwglid: function (newVal) {
      this.formData.gwid = newVal[newVal.length - 1];
    },
  },
  created() {},
  mounted() {},
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
    handelConfirm() {
      this.$refs["elForm"].validate((valid) => {
        if (!valid) return;
        let dataToSend = JSON.parse(JSON.stringify(this.formData));
        saveGwsp(dataToSend)
          .then((response) => {
            this.$message({
              message: response.msg,
              type: "success",
            });
            this.$emit("data-refresh");
            this.close();
          })
          .catch((error) => {
            this.$message({
              message: error,
              type: "warning",
            });
          });
      });
    },
  },
};
</script>
<style>
</style>
