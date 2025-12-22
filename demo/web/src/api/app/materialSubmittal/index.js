import request from "@/utils/request";
import { apiUtils } from "@/utils/LeeUtils.js";

// 查询
export function getSbtzList(params) {
  const Obj = apiUtils.filterParams(params);

  return request({
    url: "/materialSubmittal",
    method: "get",
    params: Obj
  });
}

// 创建
export function saveSbtz(data) {
  return request({
    url: "/materialSubmittal/save",
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
    url: "/materialSubmittal/update",
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
    url: "/materialSubmittal/getfjList",
    method: "get",
    params: Obj
  });
}

// 根据ID获取TDpxzl
export function getTDpxzlById(id) {
  return request({
    url: `/materialSubmittal/${id}`,
    method: "get"
  });
}

// 删除TDpxzl
export function deleteSbtz(id) {
  return request({
    url: `/materialSubmittal/${id}`,
    method: "delete"
  });
}

// 删除TDclbs，软删除材料报送
export function deleteClbs(id) {
  return request({
    url: `/materialSubmittal/clbs/${id}`,
    method: "delete"
  });
}

// 下载TDsbtzFj附件
export function downloadSbtzFjfile(id) {
  return request({
    url: `/materialSubmittal/downloadSbtzFjfile/${id}`,
    method: "get",
    responseType: "blob"
  });
}
