<template>
  <div>
    <el-dialog
      class="dialog"
      v-bind="$attrs"
      v-on="$listeners"
      @open="onOpen"
      @close="onClose"
      title="编辑部门职责"
    >
      <el-form ref="elForm" :model="formData" size="medium">
        <el-form-item prop="bmzz">
          <el-input
            v-model="formData.bmzz"
            type="textarea"
            placeholder="请输入部门职责"
            :autosize="{ minRows: 6, maxRows: 30 }"
            :style="{ width: '100%' }"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="handelConfirm">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { DeepCopyUtils } from "@/utils/LeeUtils";
import { setZnbmBmzz } from "@/api/app/manager/departmentRoles";
export default {
  inheritAttrs: false,
  components: {},
  props: [],
  data() {
    return {
      formData: {
        bmid: undefined,
        bmzz: undefined,
      },
    };
  },
  computed: {},
  watch: {},
  created() {},
  mounted() {},
  methods: {
    onInit(_, row) {
      let _row = DeepCopyUtils.deepCopy(row);
      console.log("row", _row);
      this.formData.bmid = _row.id;
      this.formData.bmzz = _row.bmzz;
    },
    onOpen() {},
    onClose() {
      this.$refs["elForm"].resetFields();
    },
    close() {
      this.$emit("update:visible", false);
    },
    handelConfirm() {
      this.$refs["elForm"].validate((valid) => {
        if (!valid) return;
        let formData = DeepCopyUtils.deepCopy(this.formData);
        setZnbmBmzz(formData)
          .then((response) => {
            // this.close();
            this.$message({
              message: response.msg,
              type: "success",
            });
            this.$emit("data-refresh");
            this.close();
            // console.log(this.eventBus);
            // this.eventBus.$emit("refresh");
          })
          .catch((error) => {
            this.$message({
              message: error,
              type: "warning",
            });
          });
      });
    },
  },
};
</script>
<style lang="sass" scoped>
.dialog >>> .el-dialog__body
  padding: 30px 20px 0 20px
</style>
