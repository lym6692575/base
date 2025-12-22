import request from "@/utils/request";
import { apiUtils } from "@/utils/LeeUtils.js";

// 查询
export function getSbtzList(params) {
  const Obj = apiUtils.filterParams(params);

  return request({
    url: "/materialSubmittal/submit",
    method: "get",
    params: Obj
  });
}

// 创建
export function saveClbs(data) {
  return request({
    url: "/materialSubmittal/submit/save",
    method: "post",
    headers: {
      "Content-Type": "multipart/form-data" // 设置请求头
    },
    data
  });
}

// 更新
export function updateSbtz(data) {
  return request({
    url: "/materialSubmittal/submit/update",
    method: "post",
    headers: {
      "Content-Type": "multipart/form-data" // 设置请求头
    },
    data
  });
}

// 查询TDpxzl列表
export function getTDpxzlFjList(params) {
  const Obj = apiUtils.filterParams(params);

  return request({
    url: "/materialSubmittal/submit/getfjList",
    method: "get",
    params: Obj
  });
}

// 根据ID获取TDpxzl
export function getTDpxzlById(id) {
  return request({
    url: `/materialSubmittal/submit/${id}`,
    method: "get"
  });
}

// 删除TDpxzl
export function deleteSbtz(id) {
  return request({
    url: `/materialSubmittal/submit/${id}`,
    method: "delete"
  });
}

// 校验是否存在
export function checkSbqk(params) {
  const Obj = apiUtils.filterParams(params);
  return request({
    url: `/materialSubmittal/submit/checkSbqk`,
    method: "get",
    params: Obj
  });
}

// 下载TDsbtzFj附件
export function downloadSbtzFjfile(id) {
  return request({
    url: `/materialSubmittal/submit/downloadSbtzFjfile/${id}`,
    method: "get",
    responseType: "blob"
  });
}
