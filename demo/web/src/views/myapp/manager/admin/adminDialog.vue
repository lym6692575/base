<template>
  <div>
    <el-dialog
      v-bind="$attrs"
      v-on="$listeners"
      @open="onOpen"
      @close="onClose"
      :title="postType == 'add' ? '新增管理员' : '编辑管理员'"
    >
      <el-form
        ref="elForm"
        :model="formData"
        :rules="rules"
        size="medium"
        label-width="100px"
        label-position="left"
      >
        <el-form-item label="账号：" prop="username">
          <el-input
            v-model="formData.username"
            placeholder="请输入单行文本账号："
            clearable
            :style="{ width: '100%' }"
            :disabled="postType != 'add'"
          >
          </el-input>
        </el-form-item>
        <el-form-item
          :label="postType == 'add' ? '密码' : '新密码'"
          prop="password"
        >
          <el-input
            v-model="formData.password"
            placeholder="请输入单行文本密码"
            clearable
            show-password
            :style="{ width: '100%' }"
          >
          </el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="formData.confirmPassword"
            placeholder="请输入单行文本确认密码"
            clearable
            show-password
            :style="{ width: '100%' }"
          >
          </el-input>
        </el-form-item>
        <el-form-item label="真实姓名" prop="realname">
          <el-input
            v-model="formData.realname"
            placeholder="请输入单行文本真实姓名"
            clearable
            :style="{ width: '100%' }"
          >
          </el-input>
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
import { saveAdmin } from "@/api/app/manager/admin";
import {rsaPublicData} from "@/utils/sm2";
export default {
  inheritAttrs: false,
  components: {},
  props: {
    postType: {
      type: String,
      require: true,
    },
  },
  data() {
    return {
      formData: {
        username: undefined,
        password: undefined,
        confirmPassword: undefined,
        realname: undefined,
      },
      rules: {
        username: [
          {
            required: true,
            message: "请输入账号：",
            trigger: "blur",
          },
          {
            pattern: /^[a-zA-Z0-9]{3,20}$/,
            message: "用户名必须是3-20位的英文或数字",
            trigger: "blur",
          },
        ],
        password: [
          {
            required: true,
            message: "请输入密码",
            trigger: "blur",
          },
          {
            pattern: /^(?=.*\d)(?=.*[a-zA-Z])(?=.*\W).{12,}$/,
            message: "密码必须是大于等于12位至少有一个数字、字母和特殊字符串",
            trigger: "blur",
          },
        ],
        confirmPassword: [
          {
            required: true,
            message: "请输入确认密码",
            trigger: "blur",
          },
          {
            validator: (rule, value, callback) => {
              if (value !== this.formData.password) {
                callback(new Error("两次输入密码不一致!"));
              } else {
                callback();
              }
            },
            trigger: "blur",
          },
        ],
        realname: [
          {
            required: true,
            message: "请输入真实姓名",
            trigger: "blur",
          },
        ],
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
    handelConfirm() {
      this.$refs["elForm"].validate((valid) => {
        if (!valid) return;
        let dataToSend = JSON.parse(JSON.stringify(this.formData));
        dataToSend.password = rsaPublicData(dataToSend.password);
        delete dataToSend.confirmPassword;
        delete dataToSend.createtime;
        saveAdmin(dataToSend)
          .then((response) => {
            // this.close();
            this.$message({
              message: response.msg,
              type: "success",
            });
            this.$emit("data-save");
            // console.log(this.eventBus);
            // this.eventBus.$emit("refresh");
          })
          .catch((error) => {
            this.$message({
              message: error,
              type: "warning",
            });
          });
        // this.close();
      });
    },
  },
};
</script>

<style>
</style>
