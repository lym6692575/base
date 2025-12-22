import request from "@/utils/request";
import { apiUtils } from "@/utils/LeeUtils.js";

// 获取中间表数据
export function getGwlcgx(params) {
  const Obj = apiUtils.filterParams(params);
  return request({
    url: "/manager/workPositionFlow/getGwlcgx",
    method: "get",
    params: Obj
  });
}

// 修改关联数据
export function saveGwlcgx(data) {
  return request({
    url: `/manager/workPositionFlow/save?gwglid=${data.gwglid}`,
    method: "post",
    data: data.ViewYwlcHgwj
  });
}

export function updateGwlcgx(data) {
  return request({
    url: `/manager/workPositionFlow/update?gwglid=${data.gwglid}`,
    method: "post",
    data: data.ViewYwlcHgwj
  });
}

// 获取关联选择数据
export function getYwlcHgwj(params) {
  const Obj = apiUtils.filterParams(params);
  return request({
    url: "/manager/workPositionFlow/getYwlcHgwj",
    method: "get",
    params: Obj
  });
}

// 获取岗位树
export function getTree() {
  return request({
    url: "/manager/workPositionFlow/getTree",
    method: "get"
  });
}
