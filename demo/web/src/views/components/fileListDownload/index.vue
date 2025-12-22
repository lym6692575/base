<template>
  <div>
    <input type="file" ref="fileInput" style="display: none"/>
    <div :span="24" class="header">
      <span>附件下载：</span>
    </div>
    <el-table class="custom-table mt10" :data="tableData" style="width: 100%">
      <el-table-column type="index" min-width="10%" label="序号"/>
      <el-table-column label="附件类型：" prop="lxmc" min-width="20%">
      </el-table-column>
      <el-table-column label="附件名称" prop="fjmc" min-width="30%">
        <template v-slot="scope">
          {{ scope.row.fjmc }}
<!--          <el-link type="primary" @click="handleLinkToViewer(scope.row)">-->
<!--            {{ scope.row.fjmc }}-->
<!--          </el-link>-->
        </template>
      </el-table-column>
      <el-table-column v-if="showSubList" label="提供部门" prop="tgbm" :min-width="`20%`">
      </el-table-column>
      <el-table-column v-if="showSubList" label="提供人" prop="tgr" :min-width="`20%`">
      </el-table-column>
      <el-table-column label="功能键" min-width="20%">
        <template slot-scope="{ row }">
          <el-button
            @click="downloadFlowFile(row)"
            type="primary"
            plain
            size="small"
          >下载
          </el-button
          >
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import {fjlx} from "@/components/select";
import {mapGetters} from "vuex";
import {evn} from "@/utils/ProjectUtils"

export default {
  props: {
    fileType: {
      type: String,
      require: true,
    },
    fileList: {
      type: Array,
      default: () => [],
    },
    showSubList:{
      type:Boolean,
      default:true,
    }
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
      const urls = {
        'hgwj': `manager/file/downloadfile/${row.id}`,
        'ywlc': `manager/file/downloadflowfile/${row.id}`,
        'pxzl': `training/downloadfile/${row.id}`,
        'sbtz': `materialSubmittal/downloadSbtzFjfile/${row.id}`,
        'clbs': `materialSubmittal/submit/downloadSbtzFjfile/${row.id}`
      };

      let path = evn.getBasePathConfig().file.toString()
      window.location.href = path + urls[this.fileType] || '';
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
    handleLinkToViewer(row){
      console.log("row",row)
      let routeUrl = this.$router.resolve({
        path: "/fileViewer",
        query: { ...row }
      });
      window.open(routeUrl.href, "_blank");
    }
  },
  computed: {
    ...mapGetters(["fjlx"]),
    // 处理数据
    tableData() {
      const tableData = [];
      const fileList = this.fileList;
      fileList.forEach((item) => {
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

.fjlx /deep/ .el-input__inner
  color: #606266 !important

// 居中设置
.custom-table /deep/ th
  text-align: center

.custom-table /deep/ td
  text-align: center
</style>
