import request from "@/utils/request";
import { apiUtils } from "@/utils/LeeUtils.js";

// 查询TDlyb列表
export function getTDlybList(params) {
  const Obj = apiUtils.filterParams(params);

  return request({
    url: "/messageBoard?isdelete=0",
    method: "get",
    params: Obj
  });
}

// 根据ID获取TDlyb
export function getTDlybById(id) {
  return request({
    url: `/messageBoard/${id}`,
    method: "get"
  });
}

// 创建TDlyb
export function saveTDlyb(data) {
  return request({
    url: "/messageBoard/save",
    method: "post",
    data
  });
}

// 删除TDlyb
export function deleteTDpxzl(id) {
  return request({
    url: `/messageBoard/${id}`,
    method: "delete"
  });
}
