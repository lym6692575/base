import request from "@/utils/request";
import { apiUtils } from "@/utils/LeeUtils.js";

// 获取岗位数据
export function getTDgwgl(params) {
  const Obj = apiUtils.filterParams(params);
  return request({
    url: "/manager/workPosition",
    method: "get",
    params: Obj
  });
}

// 获取岗位树
export function getTDgwglTree() {
  return request({
    url: "/manager/workPosition/tree",
    method: "get",
  });
}


// 创建TDywlb
export function saveTDgwgl(data) {
  return request({
    url: "/manager/workPosition",
    method: "post",
    data
  });
}

// 删除岗位
export function deleteTDgwgl(id) {
  return request({
    url: `/manager/workPosition/${id}`,
    method: "delete"
  });
}