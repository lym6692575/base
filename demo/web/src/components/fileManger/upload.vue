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
        <template v-if="filesTagsList && filesTagsList.length != 0">
          <el-tag
            v-for="(file, index) in filesTagsList"
            :key="index"
            closable
            type="tag.info"
            @close="removeFile(index, file.savedName)"
          >
            {{ file.fjmc }}
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
      <button v-if="false" @click="apiUpload" :disabled="files.length === 0">
        上传文件
      </button>
    </div>
  </div>
</template>

<script>
import { mapGetters, mapActions } from "vuex";
import { v4 as uuidv4 } from "uuid";
export default {
  props: {
    index: {
      type: Number,
      default: null,
    },
    value: {
      type: Array,
      default: () => {},
    },
  },
  data() {
    return {};
  },
  model: {
    // 需要双向绑定的 props 变量名称，也就是父组件通过 v-model 与子组件双向绑定的变量
    prop: "value",
    // 定义由 $emit 传递变量的名称
    event: "newValue",
  },
  watch: {
    // 监听 sonValue 临时变量，如果临时变量发生变化，那么通过 $emit 将新的值传递给父组件
    filesTagsList(value) {
      // 【注意】newValue x需要和 model.event 定义的值一致
      this.$emit("newValue", this.filesTagsList);
    },
  },
  methods: {
    ...mapActions("flow", ["deleteFilesAndName", "saveFilesAndName"]),

    openFileInput() {
      this.$refs.fileInput.value = ""; // 清空之前选择的文件
      this.$refs.fileInput.click();
    },

    handleFileChange(event) {
      const fileList = event.target.files; // 上传文件数据
      const newFiles = Array.from(fileList); // 数据类型转化为数组
      const savedName = uuidv4(); // 生成文件保存名字同时作为唯一标识,防止重复名字
      // vuex保存
      this.saveFilesAndName({ newFiles, savedName });

      if (fileList.length > 0) {
        const latestFile = fileList[fileList.length - 1]; // 获取最新选中的文件
        const latestFileName = latestFile.name; // 获取最新选中的文件名
        const fileExtension =
          "." + latestFileName.substring(latestFileName.lastIndexOf(".") + 1); //文件名后缀

        // t_d_ywlc_hjfj实体
        this.filesTagsList.push({
          ywlchjid: this.flowFormParams.tdywlcHjList[this.index].id,
          savedName: savedName + fileExtension,
          fjmc: latestFileName,
        });
      }
    },

    // 删除
    removeFile(index, savedName) {
      this.filesTagsList.splice(index, 1);
      this.deleteFilesAndName(savedName);
    },
  },
  mounted() {},
  computed: {
    ...mapGetters(["flowFormParams", "filesData"]),
    filesTagsList() {
      return this.flowFormParams.tdywlcHjList[this.index].tdywlcHjfjList || "";
    },
    // tags循环用的文件名称
    getFileDisplayName() {
      return this.fileName;
    },
  },
  beforeDestroy() {
    this.files = [];
  },
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