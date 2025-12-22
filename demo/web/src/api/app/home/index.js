import request from "@/utils/request";
import { apiUtils } from "@/utils/LeeUtils.js";

// 查询合规文件各类别三个月的数量统计
export function getHgwjCount() {
  return request({
    url: "/home/getcount",
    method: "get"
  });
}
