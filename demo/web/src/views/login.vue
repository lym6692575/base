<template>
  <div>
    <div class="login">
      <el-form :model="form" @submit.native.prevent>
        <h2 class="lable">登录</h2>
        <div class="input-box">
          <el-form-item label="账号：">
            <el-input
              v-model="form.username"
              placeholder="请输入账号"
              class="input"
            ></el-input>
          </el-form-item>
          <el-form-item label="密码：">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              class="input"
            ></el-input>
          </el-form-item>
        </div>
        <el-form-item>
          <el-checkbox class="right-align" v-model="form.rememberMe"
            >记住密码</el-checkbox
          >
        </el-form-item>
        <el-form-item>
          <el-button class="button" type="primary" @click="submitForm"
            >登录</el-button
          >
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { login } from "@/api/app/common";
import { sm2 } from 'sm-crypto';
import {rsaPublicData} from "@/utils/sm2";
export default {
  data() {
    return {
      form: {
        username: "",
        password: "",
        rememberMe: false,
      },
    };
  },
  created() {
    const savedUsername = localStorage.getItem("username");
    const savedPassword = localStorage.getItem("password");
    if (savedUsername && savedPassword) {
      this.form.username = savedUsername;
      this.form.password = savedPassword;
      this.form.rememberMe = true;
    }
  },
  methods: {
    submitForm() {
      if (!this.form.username || !this.form.password) {
        this.$message.error("请输入用户名和密码！");
        return;
      }

      // 登录逻辑
      login({ username: this.form.username, password: rsaPublicData(this.form.password) })
        .then((response) => {
          console.log(response);
          this.$store.dispatch("base/setToken", response.data["jwt"]);
          this.$store.dispatch("base/setUserInfo", response.data);
          this.$store.dispatch("base/changeStore", true);
          this.$message({
            message: response.msg,
            type: "success",
          });
          this.$router.push({ name: "loading" });
        })
        .catch((error) => {
          // this.$message({
          //   message: error,
          //   type: "warning",
          // });
        });

      if (this.form.rememberMe) {
        localStorage.setItem("username", this.form.username);
        localStorage.setItem("password", this.form.password);
      } else {
        localStorage.removeItem("username");
        localStorage.removeItem("password");
      }
    },
  },
};
</script>

<style>
body {
  background: url("~@/assets/login.jpg") no-repeat center center fixed;
  -webkit-background-size: cover;
  -moz-background-size: cover;
  -o-background-size: cover;
  background-size: cover;
  /* background: black; */
}
</style>

<style lang=sass scoped>
.login
  width: 540px
  height: 540px
  padding: 40px 80px
  background: #fff
  opacity: 0.8
  position: absolute
  right: 6%
  top: 20%
  border-radius: 20px
  border: 1px solid #eaeefb
.lable
  text-align: center
  letter-spacing: 2px
.input-box
  margin-top: 40px
.input >>> .el-input__inner
  height: 48px
.button
  width: 100%
  height: 48px
  font-size: 20px
  font-family: Helvetica Neue, Helvetica, PingFang SC, Hiragino Sans GB, Microsoft YaHei, Arial, sans-serif
  font-weight: bold
  letter-spacing: 4px
</style>
