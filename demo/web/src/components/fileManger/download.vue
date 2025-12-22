<template>
  <div>
    <button @click="downloadFile">下载文件</button>
  </div>
</template>

<script>
import { download } from "@/api/app/common";

export default {
  methods: {
    downloadFile() {
      const filename = "1a4c2b39-6d7f-4efd-b661-12ab59618704.txt"; // 替换为实际的文件名

      download(filename)
        .then((res) => {
          var name = "result.pdf";
          var blob = new Blob([res]);
          var url = window.URL.createObjectURL(blob);
          var aLink = document.createElement("a");
          aLink.style.display = "none";
          aLink.href = url;
          aLink.setAttribute("download", name);
          document.body.appendChild(aLink);
          aLink.click();
          document.body.removeChild(aLink); // 下载完成移除元素
          window.URL.revokeObjectURL(url); // 释放掉blob对象
        })
        .catch((error) => {
          // 处理上传失败的回调
          console.error("文件下载失败", error);
        });
    },
  },
  computed: {},
};
</script>