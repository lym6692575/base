import request from "@/utils/request";

// 获取用户在某应用下有权的菜单树
export function getAuthMenuTreesInApp(userId, appId) {
  return request({
    url:
      "flame-ura/authMenus/tree/search/appId?appId=" +
      appId +
      "&userId=" +
      userId,
    method: "get"
  });
}

//或有用户在某应用下有权的菜单列表
export function getMenusInApp(userId, appId) {
  return request({
    url:
      "flame-ura/authMenus/search/appId?appId=" + appId + "&userId=" + userId,
    method: "get"
  });
}

//获取用户在某应用下所有菜单的树形结构
export function getMenusTreeInApp(userId, appId, depth) {
  return request({
    url:
      "flame-ura/authMenus/tree/search/appId?appId=" +
      appId +
      "&userId=" +
      userId +
      "&depth=" +
      depth,
    method: "get"
  });
}
