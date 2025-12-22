import request from "@/utils/request";

// 登录
export function login(data) {
  return request({
    url: "/login",
    method: "post",
    whitelist: true, // 添加 whitelist: true
    data
  });
}

// 查询文件类别
export function getWjlb(params) {
  return request({
    url: "/common/select/wjlb?isdelete=0",
    method: "get",
    params
  });
}

// 查询业务类别
export function getYwlb(params) {
  return request({
    url: "/common/select/ywlb?isdelete=0",
    method: "get",
    params
  });
}

// 查询效力等级
export function getXldj() {
  return request({
    url: "/common/select/xldj?isdelete=0",
    method: "get"
  });
}

// 查询职能部门
export function getZnbm() {
  return request({
    url: "/common/select/znbm?isdelete=0",
    method: "get"
  });
}

// 查询附件类型
export function getFjlx(params) {
  return request({
    url: "/common/select/fjlx?isdelete=0",
    method: "get",
    params
  });
}

// 查询附件类型
export function getGwSelect() {
  return request({
    url: "/common/select/getGwSelect",
    method: "get",
  });
}

// 文件上传
export function upload(formData) {
  return request({
    // url: "/common/uploadTest",
    url: "test",
    method: "post",
    headers: {
      "Content-Type": "multipart/form-data" // 设置请求头
    },
    data: formData
  });
}

// 文件下载
export function download() {
  return request({
    url: "/common/download",
    method: "GET",
    params: { filename: "1a4c2b39-6d7f-4efd-b661-12ab59618704.txt" }
  });
}
