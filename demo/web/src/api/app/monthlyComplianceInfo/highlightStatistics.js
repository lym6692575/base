import request from "@/utils/request";
import { apiUtils } from "@/utils/LeeUtils.js";

// 查询Wgsjlx列表
export function getYdhgxxList(params) {
  const Obj = apiUtils.filterParams(params);
  return request({
    url: "/monthlyComplianceInfo/highlightStatistics/getYdhgxx",
    method: "get",
    params: Obj
  });
}

