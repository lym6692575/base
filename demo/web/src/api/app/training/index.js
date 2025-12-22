import request from "@/utils/request";
import { apiUtils } from "@/utils/LeeUtils.js";

// 查询TDpxzl列表
export function getTDpxzlList(params) {
  const Obj = apiUtils.filterParams(params);

  return request({
    url: "/training",
    method: "get",
    params: Obj
  });
}

// 查询TDpxzl列表
export function getTDpxzlFjList(params) {
  const Obj = apiUtils.filterParams(params);

  return request({
    url: "/training/getfjList",
    method: "get",
    params: Obj
  });
}

// 根据ID获取TDpxzl
export function getTDpxzlById(id) {
  return request({
    url: `/training/${id}`,
    method: "get"
  });
}

// 创建TDpxzl
export function saveTDpxzl(data) {
  return request({
    url: "/training/save",
    method: "post",
    headers: {
      "Content-Type": "multipart/form-data" // 设置请求头
    },
    data
  });
}

// 更新TDpxzl
export function updateTDpxzl(data) {
  return request({
    url: "/training/update",
    method: "post",
    headers: {
      "Content-Type": "multipart/form-data" // 设置请求头
    },
    data
  });
}

// 删除TDpxzl
export function deleteTDpxzl(id) {
  return request({
    url: `/training/${id}`,
    method: "delete"
  });
}

// 培训附件下载
export function downloadTrainingFile(id) {
  return request({
    url: `/training/downloadfile/${id}`,
    method: "get",
    responseType: "blob"
  });
}
