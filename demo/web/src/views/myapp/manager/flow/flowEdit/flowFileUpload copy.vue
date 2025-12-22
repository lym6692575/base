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
    {{ filesTagsList }}
    <el-row>
      <el-col :span="24">
        <el-table
          class="custom-table mt20"
          :data="filesTagsList"
          style="width: 100%"
          @selection-change="$emit('input', $event)"
        >
          <el-table-column type="index" min-width="10%" label="序号" />
          <el-table-column label="附件类型：" :min-width="`20%`">
            <template slot-scope="{ row }">
              <fjlx
                class="fjlx"
                :showAllOptions="false"
                v-model="row.fjlxid"
                :disabled="!row.isEditing"
                :key="generatedKey + 'fjlx'"
              ></fjlx>
            </template>
          </el-table-column>
          <el-table-column label="附件名称" :min-width="`30%`">
            <template slot-scope="{ row }">
              <div class="full-width-button">
                <el-tooltip
                  class="item"
                  effect="light"
                  content="点击选择或替换文件"
                  placement="top"
                >
                  <el-button
                    size="small"
                    class="button"
                    :disabled="!row.isEditing"
                    :class="{ 'disabled-button': !row.isEditing }"
                    @click="openFileInput(row)"
                    plain
                    icon="el-icon-upload"
                  >
                    {{
                      row.fileInfo
                        ? row.fileInfo.name
                        : row.isEditing
                        ? "点击添加文件"
                        : ""
                    }}
                  </el-button>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="功能键" :min-width="`20%`">
            <template slot-scope="{ row }">
              <el-button
                v-if="!row.isEditing"
                @click="editRow(row)"
                type="primary"
                plain
                >编辑</el-button
              >
              <el-button v-else @click="saveRow(row)" type="primary" plain
                >确认</el-button
              >
              <el-button type="danger" plain @click="deleteRow(row)"
                >删除</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { fjlx } from "@/components/select";
import { mapGetters, mapActions } from "vuex";
import { v4 as uuidv4 } from "uuid";
export default {
  components: {
    fjlx,
  },
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
.header
  line-height: 36px
  span
    font-weight: 700
  &-addButton
    float: right
.full-width-button
  display: flex
  justify-content: center
  align-items: center
  height: 36px
  .button
    width: 100%
    height: 100%

// 替换禁用样式
.disabled-button
  color: gray
.fjlx >>> .el-input__inner
  color: #606266 !important

// 居中设置
.custom-table>>>th
  text-align: center
.custom-table>>>td
  text-align: center
</style>