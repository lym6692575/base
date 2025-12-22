import request from "@/utils/request";
import { apiUtils } from "@/utils/LeeUtils.js";

// 查询TDlyb列表
export function getTDlybList(params) {
  const Obj = apiUtils.filterParams(params);

  return request({
    url: "/manager/messageBoard?isdelete=0",
    method: "get",
    params: Obj
  });
}


// 创建TDlyb
export function saveTDlyb(data) {
  return request({
    url: "/manager/messageBoard/save",
    method: "post",
    data
  });
}

// 删除TDlyb
export function deleteTDlyb(id) {
  return request({
    url: `/manager/messageBoard/${id}`,
    method: "delete"
  });
}

// TDlyb发布状态切换
export function updateTDlybFbzt(id) {
  return request({
    url: `/manager/messageBoard/fbzt/${id}`,
    method: "post"
  });
}