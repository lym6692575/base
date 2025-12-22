import request from "@/utils/request";
import { apiUtils } from "@/utils/LeeUtils.js";

// 获取znbm
export function getTableData(params) {
  const Obj = apiUtils.filterParams(params);
  return request({
    url: "/manager/department?isdelete=0",
    method: "get",
    params: Obj
  });
}

// 创建znbm
export function saveTDznbm(data) {
  return request({
    url: "/manager/department",
    method: "post",
    data
  });
}

// 删除znbm
export function deleteTDznbm(id) {
  return request({
    url: `/manager/department/${id}`,
    method: "delete"
  });
}


// 获取znbm树
export function getDepartmentTree() {
  return request({
    url: `/manager/department/tree`,
    method: "get",
  });
}
