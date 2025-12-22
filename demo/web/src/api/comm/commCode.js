import request from "@/utils/request";
import sysSettings from "../../../settings/sys.settings";
import store from "../../store";
import app from "../../store/modules/app";

//获取应用ID
export function getAppId() {
  return sysSettings.baseAppId;
}

//获取当前用户ID
export function getCurrentUserId() {
  var userInfo = store.state.user.info;
  return userInfo.id;
}

//获取当前用户名
export function getCurrentUserName() {
  var userInfo = store.state.user.info;
  return userInfo.account;
}

//获取当前用户姓名
export function getCurrentUserRealName() {
  var userInfo = store.state.user.info;
  return userInfo.realName;
}

//获取当前用户组织ID
export function getCurrentUserOrgId() {
  var userInfo = store.state.user.info;
  return userInfo.orgId;
}

//获取当前日期时间
export function getCurrentDateTime() {
  var dateObj = new Date();
  return formatDatetime(dateObj);
}

//格式化日期时间
export function formatDatetime(timestamp) {
  if (timestamp != null) {
    let date = new Date(timestamp);
    let Y = date.getFullYear() + "-";
    let M =
      (date.getMonth() + 1 < 10
        ? "0" + (date.getMonth() + 1)
        : date.getMonth() + 1) + "-";
    let D = (date.getDate() < 10 ? "0" + date.getDate() : date.getDate()) + " ";
    let h =
      (date.getHours() < 10 ? "0" + date.getHours() : date.getHours()) + ":";
    let m =
      (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes()) +
      ":";
    let s =
      date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();
    return Y + M + D + h + m + s;
  }
}

//格式化日期
export function formatDate(timestamp) {
  if (timestamp != null) {
    let str = new Date(timestamp);
    let year = str.getFullYear();
    let month = str.getMonth() + 1;
    if (month < 10) {
      month = "0" + (str.getMonth() + 1);
    }
    let day = str.getDate();
    if (day < 10) {
      day = "0" + str.getDate();
    }
    return year + "-" + month + "-" + day;
  }
}

//获取用户所属组织及其下级组织树
export function selfAndSubOrgs(userid) {
  return request({
    url:
      "/flame-ura/orgs/selfAndSubOrgs/search/userId?depth=-1&userId=" + userid,
    method: "get"
  });
}
