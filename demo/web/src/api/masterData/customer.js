import request from "@/utils/request";

//计量单位主数据获取
export function getCustomersByExample(objson) {
  return request({
    url:
      "/md/customers/page/search/example?page=" +
      objson.page +
      "&size=" +
      objson.size +
      "&createTime=" +
      objson.createTime +
      "&creator=" +
      objson.creator +
      "&creatorName=" +
      objson.creatorName +
      "&modifier=" +
      objson.modifier +
      "&modifierName=" +
      objson.modifierName +
      "&modifyTime=" +
      objson.modifyTime +
      "&code=" +
      objson.code +
      "&country=" +
      objson.country +
      "&name=" +
      objson.name +
      "&salesTo=" +
      objson.salesTo +
      "&text=" +
      objson.text +
      "&status=" +
      objson.status,
    method: "get"
  });
}
//删除计量单位
export function deleteCustomer(data) {
  return request({
    url: "/md/customers",
    method: "delete",
    data: data
  });
}
//新增计量单位
export function addCustomer(data) {
  return request({
    url: "/md/customers",
    method: "post",
    data: data
  });
}
//修改计量单位
export function editCustomer(data) {
  return request({
    url: "/md/customers",
    method: "put",
    data: data
  });
}
//批量删除计量单位
export function deleteCustomerBatch(data) {
  return request({
    url: "/md/customers/batch",
    method: "delete",
    data: data
  });
}
