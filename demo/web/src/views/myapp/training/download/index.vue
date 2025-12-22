<template>
  <div>
    <div calss="parent-container">
      <el-form
        ref="elForm"
        label-position="left"
        label-width="90px"
        :model="formData"
        size="medium"
      >
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item
              label-width="110px"
              label="交流资料名称："
              prop="zlmc"
            >
              <el-input v-model="formData.zlmc" clearable> </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="16" class="right-align">
            <el-button @click="onSearch()" type="primary" icon="el-icon-search"
              >查询</el-button
            >
          </el-col>
        </el-row>
      </el-form>
      <download-table ref="tableRef" v-model="formData"></download-table>
    </div>
  </div>
</template>

<script>
import elInput from "@/components/elementUI/el-input";
import { ywlb, xldj, znbm } from "@/components/select";
import downloadTable from "./downloadTable.vue";
export default {
  name: "upload",
  data() {
    return {
      message: "资料上传",
      formData: {
        page: "1",
        rows: "10",
        fjmc: undefined,
      },
    };
  },
  components: {
    elInput,
    downloadTable,
  },
  methods: {
    onSearch() {
      this.$refs.tableRef.handelSearch();
    },
  },
  // 添加生命周期函数
  mounted() {},
  beforeDestroy() {},
  computed: {},
};
</script>

<style scoped>
.parent-container {
  display: flex;
  flex-direction: row;
}
</style>
