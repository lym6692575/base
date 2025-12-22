<template>
  <div class="container">
    <input
      type="file"
      ref="fileInput"
      @change="handleFileChange"
      style="display: none"
    />
    <div class="tags">
      <span>附件列表：</span>
      <div class="tags-content">
        <template v-if="files.length != 0">
          <el-tag
            v-for="(file, index) in files"
            :key="index"
            closable
            type="tag.info"
            @close="removeFile(index)"
          >
            {{ file.name }}
          </el-tag>
        </template>
        <template v-else>
          <span class="tags-placeholder">请选择文件</span>
        </template>
      </div>
    </div>
    <div class="button-container">
      <el-button size="small" type="primary" @click="openFileInput" plain>
        选择文件
      </el-button>
      <button
        v-if="showUpLoadButton"
        @click="uploadFiles"
        :disabled="files.length === 0"
      >
        上传文件
      </button>
    </div>
  </div>
</template>

<script>
import { upload } from "@/api/app/common";
export default {
  props: {
    showUpLoadButton: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      files: [],
    };
  },
  methods: {
    openFileInput() {
      this.$refs.fileInput.value = ""; // 清空之前选择的文件
      this.$refs.fileInput.click();
    },
    handleFileChange(event) {
      const fileList = event.target.files;
      const newFiles = Array.from(fileList);
      this.files = this.files.concat(newFiles);
    },
    removeFile(index) {
      this.files.splice(index, 1);
    },
    uploadFiles() {
      if (this.files.length === 0) {
        return; // 没有选择文件，直接返回
      }

      // 创建一个 FormData 对象，用于存储所有文件
      let formData = new FormData();

      this.files.forEach((file) => {
        console.log("file", file);
        formData.append("files", file);
      });

      formData.append("typeName", "pxzl");
      formData.append("zlmc", "测试资料名称切换接口"); // 将额外的参数添加到 FormData 对象中
      formData.append("fjlxid", 1); // 将额外的参数添加到 FormData 对象中
      // 在这里执行上传文件的操作，可以使用您喜欢的上传方法
      upload(formData)
        .then((response) => {
          // 处理上传成功的回调
          console.log(response);
        })
        .catch((error) => {
          // 处理上传失败的回调
          console.error("文件上传失败", error);
        });
    },
  },
  computed: {},
};
</script>

<style lang="sass" scoped>
.el-button--small
  padding: 11px 15px

.container
  display: inline-flex
.tags
  display: inline-flex
  align-items: center
  border-radius: 4px
  white-space: nowrap
  overflow: auto
  margin-right: 10px
  span
    margin-right: 10px
  &-placeholder
    color: #C0C4CC
    margin-left: 10px
    font-family: PingFang SC
  &-content
    display: inline-flex
    border: 1px solid #eaeefb
    border-radius: 4px
    min-width: 180px
    padding: 2px 4px
    min-height: 36px
    align-items: center
    white-space: nowrap
    overflow: auto
  &-item
    margin-right: 6px
.tags-container,
.button-container
  display: inline-block
  vertical-align: top

.tags-container
  margin-right: 10px

.button-container
</style>