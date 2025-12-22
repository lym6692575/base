import request from "@/utils/request";

/**
 * 文件上传
 * @param {文件对象} fileobj 
 * @param {文件上传处理地址} url 
 * @returns 
 */
export function fileUpload(fileobj, url) {
    let param = new FormData();
    // 上传文件对象 名称file与后台控制器参数要一致
    param.append('file', fileobj.file);
    return request({
        method: 'post',
        // 上传地址
        url: url,
        // 定义上传头
        headers: {
            'Content-Type': 'multipart/form-data',
            //"token": window.sessionStorage.getItem('token')
        },
        data: param
    });
}
/**
 * 文件下载
 * @param {文件id} fileId 
 * @param {文件下载处理地址} url 
 * @returns 
 */
export function fileDownload(fileId, url) {
    return request({
        method: 'get',
        url: url,
        headers: {
            'Content-Type': 'application/json',
            //"token": window.sessionStorage.getItem('token')
        },
        params: { fileId: fileId, },
        responseType: 'blob'
    });
}

/**
 * 文件删除
 * @param {文件id} fileId 
 * @param {文件删除处理方法地址} url 
 * @returns 
 */
export function fileDelete(fileId, url) {
    return request({
        method: 'get',
        url: url,
        headers: {
            'Content-Type': 'application/json',
            //"token": window.sessionStorage.getItem('token')
        },
        params: { fileId: fileId, }
    });
}
