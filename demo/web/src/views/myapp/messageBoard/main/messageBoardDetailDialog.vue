<template>
  <div>
    <el-dialog
      v-bind="$attrs"
      v-on="$listeners"
      @open="onOpen"
      @close="onClose"
      title="留言详情"
    >
      <el-form
        ref="elForm"
        :model="formData"
        size="medium"
        label-width="100px"
        label-position="left"
        class="disabled"
      >
        <el-row>
          <el-form-item label="留言主题：" prop="lyzt">
            <el-input
              v-model="formData.lyzt"
              placeholder="请输入留言主题："
              clearable
              :style="{ width: '100%' }"
              readonly
            >
            </el-input>
          </el-form-item>
          <el-form-item label="留言内容：" prop="lynr">
            <el-input
              class="disabled"
              v-model="formData.lynr"
              type="textarea"
              placeholder="请输入留言内容："
              :autosize="{ minRows: 4, maxRows: 4 }"
              :style="{ width: '100%' }"
              readonly
            ></el-input>
          </el-form-item>
          <el-form-item label="答复内容：" :prop="'tdlydf[0].dfjlynr'">
            <el-input
              v-model="formData.tdlydf[0].dfjlynr"
              type="textarea"
              placeholder="请输入留言内容："
              :autosize="{ minRows: 4, maxRows: 4 }"
              :style="{ width: '100%' }"
              readonly
            ></el-input>
          </el-form-item>
        </el-row>
      </el-form>
    </el-dialog>
  </div>
</template>
<script>
export default {
  inheritAttrs: false,
  components: {},
  props: [],
  data() {
    return {
      formData: {
        lyzt: undefined,
        lynr: undefined,
        lxr: undefined,
        lxdh: undefined,
        tdlydf: [{ dfjlynr: "" }],
      },
    };
  },
  computed: {},
  watch: {},
  created() {},
  mounted() {},
  methods: {
    onInit(index, row) {
      if (row && !row.tdlydf[0]) {
        row.tdlydf.push({
          lyztid: row.id,
          dfjlynr: "",
        });
      }
      this.formData = row;
    },
    onOpen() {},
    onClose() {
      this.$refs["elForm"].resetFields();
    },
    close() {
      this.$emit("update:visible", false);
    },
  },
};
</script>
<style lang="sass" scoped>
// disable样式穿透
.disabled >>> .el-input__inner
  color: #606266
.disabled >>> .el-textarea__inner
  color: #606266
</style>
