import request from "@/utils/request";

// 查询路由
export function getRoutes(appId) {
  return request({
    url: "flame-ura/routes/tree/search/appId?appId=" + appId,
    method: "get"
  });
}
