import request from "@/utils/request";
import { apiUtils } from "@/utils/LeeUtils.js";

// 查询Wgsjlx列表
export function getWgsjlxList() {
  return request({
    url: "/monthlyComplianceInfo/dataStatistics/getWgsjlx",
    method: "get"
  });
}

// 查询Ydhgxx列表
export function getYdhgxxList(params) {
  const Obj = apiUtils.filterParams(params);
  return request({
    url: "/monthlyComplianceInfo/dataStatistics/getYdhgxx",
    method: "get",
    params: Obj
  });
}

// 查询有重大事件的组织id
export function getMarkedSsbmids(ssyf) {
  return request({
    url: "/monthlyComplianceInfo/dataStatistics/getWgsjlx/getMarkedSsbmids",
    method: "get",
    params: {
      ssyf
    }
  });
}
