import request from "@/utils/request";
import { apiUtils } from "@/utils/LeeUtils.js";

// 查询管理员列表
export function getAdminList(params) {
  const Obj = apiUtils.filterParams(params);
  return request({
    url: "/manager/admin",
    method: "get",
    params: Obj
  });
}

// 创建管理员
export function saveAdmin(data) {
  return request({
    url: "/manager/admin/saveadmin",
    method: "post",
    data
  });
}

// 删除管理员
export function deleteAdmin(id) {
  return request({
    url: `/manager/admin/${id}`,
    method: "delete"
  });
}

// 注销
export function updateCancelState(id) {
  return request({
    url: `/manager/admin/cancel/${id}`,
    method: "post"
  });
}
