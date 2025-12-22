import request from "@/utils/request";
import { apiUtils } from "@/utils/LeeUtils.js";

// 获取TDhgwj
export function getTableData(params) {
  const Obj = apiUtils.filterParams(params);
  return request({
    url: "/manager/file",
    method: "post",
    data: Obj
  });
}

// 创建TDhgwj
export function saveTDhgwj(data) {
  return request({
    url: "/manager/file/update",
    method: "post",
    headers: {
      "Content-Type": "multipart/form-data" // 设置请求头
    },
    data
  });
}

// 更新TDhgwj
export function updateTDhgwj(data) {
  return request({
    url: "/manager/file/update",
    method: "post",
    headers: {
      "Content-Type": "multipart/form-data" // 设置请求头
    },
    data
  });
}

// 删除TDhgwj
export function deleteTDhgwj(id) {
  return request({
    url: `/manager/file/${id}`,
    method: "delete"
  });
}

// 关联流程
export function updataFlowList(id, tDywlcList) {
  return request({
    url: `/manager/file/updataflowlist/${id}`,
    method: "put",
    data: JSON.stringify(tDywlcList)
  });
}

// 合规文件附件下载
export function downloadFile(id) {
  return request({
    url: `/manager/file/downloadfile/${id}`,
    method: "get",
  });
}

// 关联流程文件下载
export function downloadFlowFile(id) {
  return request({
    url: `/manager/file/downloadflowfile/${id}`,
    method: "get",
    responseType: "blob"
  });
}
