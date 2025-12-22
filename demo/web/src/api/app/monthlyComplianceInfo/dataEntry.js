import request from "@/utils/request";
import { apiUtils } from "@/utils/LeeUtils.js";

// 查询Wgsjlx列表
export function getWgsjlxList() {
  return request({
    url: "/monthlyComplianceInfo/dataEntry/getWgsjlx",
    method: "get"
  });
}

// 查询Ydhgxx列表
export function getYdhgxxList(params) {
  const Obj = apiUtils.filterParams(params);
  return request({
    url: "/monthlyComplianceInfo/dataEntry/getYdhgxx",
    method: "get",
    params: Obj
  });
}

// saveYdhgxx
export function saveYdhgxx(data) {
  return request({
    url: "/monthlyComplianceInfo/dataEntry/saveYdhgxx",
    method: "post",
    data
  });
}
