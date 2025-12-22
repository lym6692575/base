import request from "@/utils/request";
import { apiUtils } from "../../../utils/LeeUtils";

// 查询TDywlc
export function getTableData(params, _specialIds) {
  const Obj = apiUtils.filterParams(params);

  let url = "manager/flow";

  if (_specialIds) {
    const specialIds = JSON.stringify(_specialIds);
    url += `?specialIds=${specialIds}`;
  }

  return request({
    url: url,
    method: "get",
    params: Obj
  });
}

// 创建或更新TDywlc
export function saveTDywlc(data) {
  return request({
    url: "/manager/flow",
    method: "post",
    headers: {
      "Content-Type": "multipart/form-data" // 设置请求头
    },
    data
  });
}

// 删除TDywlc
export function deleteTDywlc(id) {
  return request({
    url: `/manager/flow/${id}`,
    method: "delete"
  });
}
