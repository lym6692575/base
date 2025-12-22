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
            <el-button @click="onSearch" type="primary" icon="el-icon-search"
              >查询</el-button
            >
            <el-button @click="onAdd" type="primary" icon="el-icon-circle-plus-outline"
              >新增</el-button
            >
          </el-col>
          <upload-dialog
            :postType="'save'"
            ref="editRef"
            @data-saved="onSearch()"
          ></upload-dialog>
        </el-row>
      </el-form>
      <upload-table ref="tableRef" v-model="formData"></upload-table>
    </div>
  </div>
</template>

<script>
import elInput from "@/components/elementUI/el-input";
import uploadTable from "./uploadTable.vue";
import uploadDialog from "./uploadDialog.vue";
export default {
  name: "upload",
  data() {
    return {
      message: "资料上传",
      formData: {
        page: "1",
        rows: "10",
        wjmc: undefined,
      },
    };
  },
  components: {
    elInput,
    uploadTable,
    uploadDialog,
  },
  methods: {
    onSearch() {
      this.$refs.tableRef.handelSearch();
    },

    onAdd() {
      this.$refs.editRef.openDialog("", {});
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
