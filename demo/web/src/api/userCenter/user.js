import request from "@/utils/request";
import { praseStrEmpty } from "@/utils/commonFn";

// 查询用户详细
export function getUser(userId) {
  return request({
    url: "flame-ura/users/" + praseStrEmpty(userId),
    method: "get"
  });
}

// 更新用户
export function updateUser(data) {
  return request({
    url: "flame-ura/users/" + data.id,
    method: "put",
    data: data
  });
}

// 修改用户密码
export function changeMyPwd(userId, params) {
  let url = "flame-ura/users/" + userId + "/password";
  return request({
    url: url,
    method: "patch",
    data: params
  });
}
//个人中心修改用户密码
export function personalChangePassword(userId, params) {
  let url = "flame-ura/users/" + userId + "/action/changePersonalPassword";
  return request({
    url: url,
    method: "patch",
    data: params
  });
}
