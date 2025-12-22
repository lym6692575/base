import request from "@/utils/request";
import { apiUtils } from "../../../utils/LeeUtils";

// 查询合规文件表
export function getTableData(params) {
  const Obj = apiUtils.filterParams(params);
  return request({
    url: "/knowledge/flow",
    method: "get",
    params: Obj
  });
}

// 流程通用附件下载
export function downloadFlowGeFile(id) {
  return request({
    url: `/manager/flow/downloadFlowGeFile/${id}`,
    method: "get"
  });
}
