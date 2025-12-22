import request from "@/utils/request";

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
// 获取当前登录用户信息
export function getCurrentUser() {
  return request({
    url: "flame-ura/loginUsers/current",
    method: "get"
  });
}

// 退出方法
export function logout() {
  return request({
    url: "flame-uac/loginUsers/action/logout",
    method: "post"
  });
}
