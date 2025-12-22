<template>
  <div>
    <input type="file" ref="fileInput" style="display: none" />
    <div :span="24" class="header">
      <span>附件下载：</span>
    </div>
    <el-table class="custom-table mt10" :data="tableData" style="width: 100%">
      <el-table-column type="index" min-width="10%" label="序号" />
      <el-table-column label="附件类型：" prop="lxmc" :min-width="`20%`">
      </el-table-column>
      <el-table-column label="附件名称" prop="fjmc" :min-width="`30%`">
      </el-table-column>
      <el-table-column label="附件名称" prop="tgbm" :min-width="`30%`">
      </el-table-column>
      <el-table-column label="附件名称" prop="tgr" :min-width="`30%`">
      </el-table-column>
      <el-table-column label="功能键" :min-width="`20%`">
        <template slot-scope="{ row }">
          <el-button @click="downloadFlowFile(row)" type="primary" plain
            >下载</el-button
          >
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { fjlx } from "@/components/select";
import { downloadFlowFile } from "@/api/app/manager/file";
import { downloadFlowGeFile } from "@/api/app/knowledge/businessFlow";
import { mapGetters } from "vuex";
export default {
  props: {
    tdywlcHjfjList: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      currentRow: null, // 当前点击的行
    };
  },
  components: {
    fjlx,
  },
  methods: {
    // 下载逻辑
    downloadFlowFile(row) {
      // 判断是否为通用组件
      if (row.isGeneral) {
        downloadFlowGeFile(row.id).then((res) => {
          console.log(res);
          var name = row.fjmc;
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
        });
      } else {
        downloadFlowFile(row.id).then((res) => {
          console.log(res);
          var name = row.fjmc;
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
        });
      }
    },

    // 寻找附件类型名称
    findFjlxmc(id) {
      if (this.fjlx) {
        const found = this.fjlx.data.find((item) => {
          return item.id == id;
        });
        if (found) {
          return found.lxmc;
        } else {
          return "无";
        }
      }
    },
  },
  computed: {
    ...mapGetters(["fjlx"]),
    // 处理数据
    tableData() {
      const tableData = [];
      const tdhgwjFj = this.tdywlcHjfjList;
      console.log("tdhgwjFj", this.tdywlcHjfjList);
      tdhgwjFj.forEach((item) => {
        tableData.push({
          ...item,
          lxmc: this.findFjlxmc(item.fjlxid),
        });
      });

      return tableData;
    },
    // 生成 key 值
    generatedKey() {
      const timestamp = new Date().getTime();
      const random = Math.floor(Math.random() * 1000); // 生成一个随机数，范围为0到999
      return timestamp + "_" + random;
    },
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
</style>