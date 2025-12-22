import request from "@/utils/request";
import { apiUtils } from "@/utils/LeeUtils.js";

// 查询管理员列表
export function getGwspList(params) {
  const Obj = apiUtils.filterParams(params);
  return request({
    url: "/manager/gwsp",
    method: "get",
    params: Obj
  });
}

// 创建管理员
export function saveGwsp(data) {
  return request({
    url: "/manager/gwsp",
    method: "post",
    data
  });
}

// 删除管理员
export function deleteGwsp(id) {
  return request({
    url: `/manager/gwsp/${id}`,
    method: "delete"
  });
}