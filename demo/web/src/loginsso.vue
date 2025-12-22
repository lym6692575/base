<template>
  <div class="login" v-loading="true" element-loading-background="rgba(113, 113, 198,1)"
    element-loading-text="自动登录中，请稍后..."></div>
</template>
<script>
import axios from "axios";
import { setToken, setRefreshToken } from "@/utils/auth";

const service = axios.create({
  // 超时
  timeout: 10000
});
export default {

  created() {
    this.handleLogin();
  },
  methods: {
    async handleLogin() {
      let url = window.location.href;
      let code = getUrlKey("code", url);
      let grant_type = getUrlKey("grant_type", url);
      let state = getUrlKey("state", url);
      let param = {
        client_id: process.env.VUE_APP_CLIENTID,
        code: code,
        state: state
      };
      let headers = {
        "Content-Type": "application/x-www-form-urlencoded",
        Authorization: "Basic xxxxx"
      };
      axios({
        url: process.env.VUE_APP_BASE_API + "/flame-ura/oauth/callback",
        method: "get",
        params: param
      })
        .then(result => {
          let expiresIn = result.data.data.tokenInfo.expires_in;
          let loginTime = Date.parse(new Date()) / 1000;

          //token有效期秒数
          this.$store.dispatch("setLoginTime", loginTime);
          //登录时间
          this.$store.dispatch("setExpireSeconds", expiresIn);

          setToken(result.data.data.tokenInfo.access_token);
          setRefreshToken(result.data.data.tokenInfo.refresh_token);
          if (result.data.data.userInfo != "null") {
            localStorage.setItem(
              "userInfo_L",
              JSON.stringify(result.data.data.userInfo)
            );
          }
          this.$store.dispatch("setUserInfo", result.data.data.userInfo);

          if (this.$store.getters.allMenus.length == 0) {
            this.$store
              .dispatch("getRoutes", this.$store.getters.info.id)
              .then(accessRoutes => {
                let basePath = [
                  {
                    path: "/",
                    redirect:
                      this.$store.state.permission.basePath ||
                      JSON.parse(sessionStorage.getItem("basePath"))
                  },
                  {
                    path: "*",
                    name: "",
                    redirect: "/error/404"
                  }
                ];
                this.$router.addRoutes(accessRoutes.concat(basePath)); // 动态添加可访问路由表

                this.$store
                  .dispatch("setUserMenus", {
                    userid: this.$store.getters.info.id,
                    appId: this.sysSettings.baseAppId
                  })
                  .then(menus => {
                    if (menus.length == 0) {
                      this.$store.dispatch("setBasePath", {
                        path: "/error/401"
                      });
                      localStorage.setItem(
                        "basePath",
                        JSON.stringify({ path: "/error/401" })
                      );
                      sessionStorage.setItem("isAuth", false);
                      this.$router.push({ path: "/error/401" });
                    } else {
                      // if (!this.$store.state.settings.fistLevelMenuId) {
                      //   this.$store.dispatch(
                      //     "settings/setFistLevelMenuId",
                      //     menus[0].id
                      //   );
                      // }
                      let menu;
                      let path = { path: "/error/401" };
                      if (menus.length > 0) {
                        path = this.getLeafMenuPath(menus[0]);
                        if (!path.path) {
                          path = { path: "/error/404" };
                          this.messager.showError("首页菜单请配置跳转地址！");
                          this.$router.push(path);
                          return;
                        }
                        menu = this.getLeafMenu(menus[0]);
                        localStorage.setItem("basePath", JSON.stringify(path));
                        this.$store.dispatch("setBasePath", path);
                        this.tagsView.addView({
                          tabId: menu.id,
                          code: menu.code,
                          name: menu.name,
                          path: menu.path,
                          query: path.query,
                          icon: menu.icon,
                          meta: {
                            //   topMenuId: this.$store.state.settings
                            //     .fistLevelMenuId,
                            //   appId: this.$store.state.settings.appId
                          }
                        });
                      }

                      if (this.inIframe()) {
                        this.$router.push(
                          JSON.parse(sessionStorage.getItem("inInframeHref"))
                        );
                      } else {
                        this.$router.push(path);
                      }
                    }
                  });
              });
          } else {
            let menu = this.getLeafMenu(this.$store.getters.allMenusTree[0]);
            let path = this.getLeafMenuPath(
              this.$store.getters.allMenusTree[0]
            );
            this.tagsView.addView({
              tabId: menu.id,
              code: menu.code,
              name: menu.name,
              path: menu.path,
              query: path.query,
              icon: menu.icon,
              meta: {}
            });
            this.$router.push(path);
          }
        })
        .catch(function (error) {
          console.log(error);
        });
    }
  }
};
export function getUrlKey(name, url) {
  return (
    decodeURIComponent(
      (new RegExp("[?|&]" + name + "=" + "([^&;]+?)(&|#|;|$)").exec(url) || [
        ,
        ""
      ])[1].replace(/\+/g, "%20")
    ) || null
  );
}
// 响应拦截器
service.interceptors.response.use(
  res => {
    const code = res.data.code;
    if (code === 401) {
      MessageBox.confirm(
        "登录状态已过期，您可以继续留在该页面，或者重新登录",
        "系统提示",
        {
          confirmButtonText: "重新登录",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
        store.dispatch("LogOut").then(() => {
          location.reload(); // 为了重新实例化vue-router对象 避免bug
        });
      });
    } else if (code !== 200) {
      Notification.error({
        title: res.data.msg
      });
      return Promise.reject("error");
    } else {
      return res.data;
    }
  },
  error => {
    console.log("err" + error);
    Message({
      message: error.message,
      type: "error",
      duration: 5 * 1000
    });
    return Promise.reject(error);
  }
);
</script>
<style lang="scss">
.login {
  .el-loading-text {
    color: #fff;
    font-size: 18px;
    margin: 20px;
  }

  .el-loading-spinner .path {
    stroke: #fff;
  }
}
</style>
<style lang="scss" scoped>
.login {
  position: absolute;
  height: 100%;
  width: 100%;
}
</style>
