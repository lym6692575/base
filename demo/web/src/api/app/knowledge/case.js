import request from "@/utils/request";
import { apiUtils } from "../../../utils/LeeUtils";

// 查询合规文件表
export function getTableData(params) {
  const Obj = apiUtils.filterParams(params);
  return request({
    url: "/knowledge/case",
    method: "get",
    params: Obj
  });
}

// 合规文件附件下载
export function downloadFile(id) {
  return request({
    url: `/knowledge/case/downloadfile/${id}`,
    method: "get"
  });
}