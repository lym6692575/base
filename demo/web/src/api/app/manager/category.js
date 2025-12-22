import request from "@/utils/request";
import { apiUtils } from "@/utils/LeeUtils.js";

// 获取岗位数据
export function getTDywlb(params) {
  const Obj = apiUtils.filterParams(params);
  return request({
    url: "/manager/category",
    method: "get",
    params: Obj
  });
}

// 创建TDywlb
export function saveTDywlb(data) {
  return request({
    url: "/manager/category",
    method: "post",
    data
  });
}

// 删除TDywlb
export function deleteTDywlb(id) {
  return request({
    url: `/manager/category/${id}`,
    method: "delete"
  });
}
