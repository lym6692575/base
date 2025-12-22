import store from "@/store";
import { setToken, setRefreshToken } from "@/utils/auth";
import { refreshToken } from "@/api/userCenter/loginUser";
import user from "@/model/user";
import { MessageBox } from "element-ui";
import route from "@/router";

let refreshInterval;
let monitorInterval;

export function refreshIntervalFun() {
  refreshInterval = setInterval(() => {
    //登录时间秒数
    let loginTime = store.getters.loginTime;
    if (!loginTime) return;

    //有效时间秒数
    let expireSeconds = store.getters.expireSeconds;
    //当前时间秒数
    let currTimeSeconds = Date.parse(new Date()) / 1000;
    //当前时间 - 登录时间 <= 5分钟
    if (currTimeSeconds - loginTime > expireSeconds - 30) {
      console.info("refreshTokenAuto方法执行 == 开始刷新令牌 ==");
      refreshToken().then((res) => {
        if (res.status != "error") {
          setToken(res.access_token);
          setRefreshToken(res.refresh_token);

          let expiresIn = res.expires_in;
          let loginTime = Date.parse(new Date()) / 1000;

          //token有效期秒数
          store.dispatch("setLoginTime", loginTime);
          //登录时间
          store.dispatch("setExpireSeconds", expiresIn);
        }
      });
    }
  }, 1000 * 10);
}

export function monitorIntervalFun() {
  monitorInterval = setInterval(() => {
    // token有效时间秒数
    let expireSeconds = store.getters.expireSeconds;
    if (!expireSeconds) {
      return;
    }

    // 当前时间秒数
    let currTimeSeconds = Date.parse(new Date()) / 1000;
    // 最后一次请求后端时间
    let lastRequestTimeSeconds = store.getters.lastRequestTime;

    if (currTimeSeconds - lastRequestTimeSeconds >= expireSeconds) {
      clearInterval(refreshInterval);
      clearInterval(monitorInterval);
      //登录时间
      store.dispatch("setLoginTime", 0);
      //token有效期秒数
      store.dispatch("setExpireSeconds", 0);
      MessageBox.confirm("会话超时，请重新登录", "系统提示", {
        distinguishCancelAndClose: true,
        cancelButtonText: "关闭系统",
        confirmButtonText: "重新登录",
        closeOnClickModal: false,
        type: "warning",
        dangerouslyUseHTMLString: true,
        beforeClose: (action, instance, done) => {
          if (action === "confirm") {
            user.loginOut();
            route.replace("/login");
          } else if (action === "cancel") {
            user.loginOut();
            window.open("about:blank", "_top").close();
          } else {
            user.loginOut();
            done();
          }
        },
      });
    }
  }, 1000 * 10);
}
