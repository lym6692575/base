import request from "@/utils/request";
import { apiUtils } from "@/utils/LeeUtils.js";

// 查询TDpxzl列表
export function getcount(params) {
  const Obj = apiUtils.filterParams(params);

  return request({
    url: "/releaseQuery/getcount",
    method: "get",
    params: Obj
  });
}
