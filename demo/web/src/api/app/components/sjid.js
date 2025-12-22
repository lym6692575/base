import request from "@/utils/request";
// 查询效力等级
export function getDepartmentTree() {
  return request({
    url: "/manager/department/tree",
    method: "get"
  });
}
