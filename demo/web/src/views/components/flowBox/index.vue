<template>
  <el-dialog
    title="流程图详情"
    :visible.sync="dialogVisible"
    append-to-body
    width="90%"  >
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane
        v-for="ywlc in tdywlc"
        :key="ywlc.id"
        :name="ywlc.id"
        :label="ywlc.lcmc"
      >
        <div>
          <ul class="tab-list">
            <li
              v-for="(item, index) in ywlc.tabList"
              :key="index"
              :class="{ active: activeTab === index }"
              @click="
                index !== 0 && index !== ywlc.tabList.length - 1
                  ? changeTab(index)
                  : null
              "
              :style="{
                color: getTextColor(item.hjmc),
              }"
            >
              {{ item.hjmc }}
              <span
                v-if="index !== ywlc.tabList.length - 1"
                class="array"
              ></span>
            </li>
          </ul>

          <el-card class="tab">
            <div
              class="tab-content"
              v-for="(content, index) in ywlc.tabList"
              :key="index"
              v-show="activeTab === index"
            >
              <b class="tab-content-title">{{ content.hjmc + "：" }}</b>
              <el-input
                class="input-note"
                type="textarea"
                :rows="2"
                placeholder="请输入内容"
                v-model="content.note"
                :disabled="true"
                :autosize="{ minRows: '4' }"
              >
              </el-input>
              <el-row class="mt20" :gutter="20">
                <el-col :span="12">
                  <znbm
                    name="执行部门"
                    :showAllOptions="false"
                    v-model="content.zxbm"
                    multiple
                    filterable
                    clearable
                    class="p5_input"
                    allow-create
                    :disabled="true"
                  >
                  </znbm>
                </el-col>
                <el-col :span="12">
                  <Input
                    :disabled="true"
                    class="zxgw"
                    name="执行岗位"
                    v-model="content.zxgw"
                /></el-col>
              </el-row>
              <el-row class="mt20" :gutter="20">
                <el-col :span="24">
                  <file-list
                    :tdywlcHjfjList="content.tdywlcHjfjList"
                  ></file-list>
                </el-col>
              </el-row>
            </div>
          </el-card>
        </div>
      </el-tab-pane>
    </el-tabs>
  </el-dialog>
</template>

<script>
import Input from "@/components/elementUI/el-input";
import fileList from "./fileList.vue";
import { fjlx, znbm } from "@/components/select";
import upload from "@/components/fileManger/upload";
import { mapGetters, mapActions } from "vuex";
export default {
  components: {
    Input,
    fileList,
    fjlx,
    znbm,
    upload,
  },
  data() {
    return {
      dialogVisible: false,
      activeName: "",
      activeTab: 1, // 默认激活的Tab索引
      formData: {},
    };
  },
  computed: {
    tdywlc() {
      if (this.formData && this.formData.tdywlc) {
        const initList = [{ hjmc: "开始" }, { hjmc: "形成正文发布" }];
        const tdywlc = this.formData.tdywlc || [];
        console.log("tdywlc",tdywlc)
        tdywlc.forEach((item) => {
          const tdywlcHjList =
            item.tdywlcHjList.sort(
              (a, b) => parseInt(a.hjpx) - parseInt(b.hjpx)
            ) || [];

          item.tdywlcHjList.forEach((hjitem) => {
            console.log("hjitem",hjitem)
            if (hjitem.zxbm && hjitem.zxbm.length > 0)
              hjitem.zxbm = this.formatStringArray(hjitem.zxbm);
          });

          console.log("123", tdywlcHjList);
          item.tabList = [initList[0], ...tdywlcHjList, initList[1]];
        });
        this.activeName = tdywlc[0].id; // 设置activeName为第一个tab的name值
        return tdywlc;
      }
      return [];
    },
  },
  methods: {
    // 格式化多选框的职能部门
    formatStringArray(str) {
      // 如果输入不是字符串，直接返回原值
      if (typeof str !== "string") {
        return str;
      }

      let parsedArray;
      try {
        // 尝试解析输入字符串为数组
        parsedArray = JSON.parse(str);
      } catch (e) {
        console.error("JSON parsing error:", e);
        return null;
      }

      // 如果解析成功，遍历数组，去除字符串元素中的转义字符
      const cleanedArray = parsedArray.map((item) => {
        if (typeof item === "string") {
          try {
            return JSON.parse(item);
          } catch (e) {
            console.error("JSON parsing error for item:", e, item);
            return item;
          }
        } else {
          // 如果元素不是字符串，直接返回原值
          return item;
        }
      });

      return cleanedArray;
    },
    // 设置特殊环节颜色
    getTextColor(hjmc) {
      if (hjmc === "开始") {
        return "green";
      } else if (hjmc === "形成正文发布") {
        return "red";
      } else {
        return ""; // 默认颜色
      }
    },

    // 合并通用附件到各个环节的方法
    mergeMethods(data) {
      // 检查数据是否存在和符合预期的结构
      if (!data || !data.tdywlc || !Array.isArray(data.tdywlc)) {
        console.error("Invalid data format");
        return;
      }
      console.log("data",data)

      // 遍历每一个tdywlc对象
      data.tdywlc.forEach((tdywlcItem) => {
        // 遍历tdywlcItem的tdywlcHjList数组
        tdywlcItem.tdywlcHjList.forEach((hjList) => {
          // 如果tdywlcHjfjList字段不存在，则初始化为一个空数组
          if (!Array.isArray(hjList.tdywlcHjfjList)) {
            hjList.tdywlcHjfjList = [];
          }
          // 合并tdywlcGefj数组到tdywlcHjfjList
          if (Array.isArray(tdywlcItem.tdywlcGefj)) {
            hjList.tdywlcHjfjList.push(...tdywlcItem.tdywlcGefj);
          }
        });
      });
    },

    // 打开对话框逻辑
    openDialog(index, row) {
      this.formData = JSON.parse(JSON.stringify(row));
      this.mergeMethods(this.formData);
      this.dialogVisible = true; // 打开对话框
    },


    handleClick(tab, event) {
      console.log(tab, event);
    },

    changeTab(index) {
      this.activeTab = index; // 切换Tab
    },
  },
};
</script>

<style lang="sass" scoped>
.tab-list
  list-style-type: none
  padding: 0
  margin: 0
  padding: 13px 2%
  border-radius: 4px
  background: #f5f7fa
  position: relative
  font-size: 16px
  font-weight: 700
  color: #303133
  display: flex
  width: 100%
  & li
    flex: 1
    display: inline-block
    padding: 10px
    cursor: pointer
    text-align: center
    position: relative
  .array
    position: absolute
    left: 95%
  .array:after
    transform: rotate(45deg) translateY(4px)
    transform-origin: 100% 100%
  .array:before
    transform: rotate(-45deg) translateY(-4px)
    transform-origin: 0 0
  .array:after,
  .array:before
    content: ""
    display: inline-block
    height: 15px
    width: 1px
    background: #c0c4cc

  & li.active
    color: #409eff

.input-note,
.input-container,
.fileUpload-container,
  margin-top: 20px

.tab
  margin-top: 20px
  border: 1px solid #ebeef5
  border-radius: 4px
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1)
  background: #fff
.fileUpload-container
  .fjlx
    margin-right: 10px

// 禁用状态文字颜色修改

.el-textarea.is-disabled >>> .el-textarea__inner,
.tab  >>> .el-input__inner,
.tab  >>>.el-tag.el-tag--info
  color: #303133
</style>
