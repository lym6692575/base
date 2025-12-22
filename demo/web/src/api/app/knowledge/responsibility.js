import request from "@/utils/request";
import { apiUtils } from "@/utils/LeeUtils.js";

// 查询合规文件表
export function getHgwjTableData(params) {
  const Obj = apiUtils.filterParams(params);
  return request({
    url: "/knowledge/responsibility/getHgwj",
    method: "get",
    params: Obj
  });
}

// 根据业务类别查询合规文件表除了规章制度和岗位职责
export function getHgwjWithExceptIdsTableData(params) {
  const Obj = apiUtils.filterParams(params);
  return request({
    url: "/knowledge/responsibility/getHgwjWithExceptIds",
    method: "get",
    params: Obj
  });
}

// 根据文件名称查询合规文件表除了岗位职责
export function getHgwjWithExceptIdsAndWjmcTableData(params) {
  const Obj = apiUtils.filterParams(params);
  return request({
    url: "/knowledge/responsibility/getHgwjWithExceptIdsAndWjmc",
    method: "get",
    params: Obj
  });
}

// 查询业务流程
export function getYwlcTableData(params) {
  const Obj = apiUtils.filterParams(params);
  return request({
    url: "/knowledge/responsibility/getYwlc",
    method: "get",
    params: Obj
  });
}

// 查询业务类别
export function getYwlbOptions() {
  return request({
    url: "/knowledge/responsibility/getWjlbOptions",
    method: "get",
  });
}

// 查询部门职责
export function getBmzz(bmid) {
  return request({
    url: `/knowledge/responsibility/getBmzz/${bmid}`,
    method: "get"
  });
}

// 查询部门职责关联的合规文件
export function getHgwjListByBmid(bmid) {
  return request({
    url: `/knowledge/responsibility/getHgwjListByBmid/${bmid}`,
    method: "get"
  });
}

// 查询岗位关联的岗位视频
export function getGwspListByGwid(gwid) {
  return request({
    url: `/knowledge/responsibility/getGwspListByGwid/${gwid}`,
    method: "get"
  });
}