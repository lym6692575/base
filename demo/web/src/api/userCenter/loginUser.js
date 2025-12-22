import request from "@/utils/request";
import { getRefreshToken } from "@/utils/auth";

// 登录
export function login(name, password) {
  return request({
    url: "flame-ura/loginUsers/action/login",
    method: "post",
    data: {
      name: name,
      password: password
    }
  });
}

// 退出
export function logout() {
  return request({
    url: "flame-uac/loginUsers/action/logout",
    method: "post"
  });
}

// 获取当前登录用户信息
export function getInfo() {
  return request({
    url: "flame-ura/loginUsers/current",
    method: "get"
  });
}

export function refreshToken() {
  let clientId = process.env.VUE_APP_CLIENTID;
  let refreshToken = getRefreshToken();
  return request({
    url:
      "/oauth/refresh?refreshToken=" + refreshToken + "&clientId=" + clientId,
    method: "get"
  });
}
