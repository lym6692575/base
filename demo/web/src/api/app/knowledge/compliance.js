import request from "@/utils/request";
import { apiUtils } from "../../../utils/LeeUtils";

// 查询合规文件表
export function getTableData(params) {
  params.isdelete = 0;  //
  const Obj = apiUtils.filterParams(params);
  return request({
    url: "/knowledge/compliance/getall",
    method: "post",
    data: Obj
  });
}

export function donwloadZip(id) {
  return request({
    url: "/knowledge/download-zip/" + id,
    method: "get",
    responseType: 'blob'
  }).then((response) => {
    // 使用从服务器响应获取的文件名（如果有）
    const contentDisposition = response.headers['content-disposition'];
    let filename = 'download.zip'; // 默认文件名
    if (contentDisposition) {
      const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
      const matches = filenameRegex.exec(contentDisposition);
      if (matches != null && matches[1]) {
        filename = matches[1].replace(/['"]/g, '');
      }
    }

    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', filename);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  }).catch((error) => {
    console.error("下载失败：", error);
  });
}
