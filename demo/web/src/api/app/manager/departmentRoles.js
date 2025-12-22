import request from "@/utils/request";
import {apiUtils} from "@/utils/LeeUtils.js";

// 获取TDZnbm
export function getTableData(params) {
    const Obj = apiUtils.filterParams(params);
    return request({
        url: "/manager/departmentroles",
        method: "get",
        params: Obj
    });
}

// 获取全部的职能部门列表
export function getZnbmList() {
    return request({
        url: `/manager/departmentroles/getZnbmList`,
        method: "get"
    });
}

// 获取树结构
export function getTree() {
    return request({
        url: "/manager/departmentroles/getTree",
        method: "get"
    });
}

// 获得合规文件及关联数据
export function getHgwjList(params) {
    const Obj = apiUtils.filterParams(params);
    return request({
        url: `/manager/departmentroles/getHgwjList`,
        method: "get",
        params: Obj
    });
}

// 设置合规文件及关联数据
export function setBmzzgx(params) {
    console.log("api", params)
    return request({
        url: `/manager/departmentroles/setBmzzgx/${params.znbmid}`,
        method: "post",
        data: params.hgwj
    });
}

//  设置关联根据合规文件id
export function setBmzzgxByHgwjid(params) {
    return request({
        url: `/manager/departmentroles/setBmzzgxByHgwjid/${params.hgwjid}`,
        method: "post",
        data: params.znbm
    });
}

// 设置部门职责
export function setZnbmBmzz(params) {
    return request({
        url: `/manager/departmentroles/setZnbmBmzz/${params.bmid}`,
        method: "post",
        data: params.bmzz
    });
}
