<template>
  <div>
    <el-dialog
      v-bind="$attrs"
      v-on="$listeners"
      @open="onOpen"
      @close="onClose"
      title="编辑部门"
    >
      <el-form
        ref="elForm"
        :model="formData"
        :rules="rules"
        size="medium"
        label-width="100px"
      >
        <el-row>
          <el-form-item label="是否启用：" prop="sign" required>
            <el-switch
              v-model="formData.sign"
              :active-value="'1'"
              :inactive-value="'2'"
            ></el-switch>
          </el-form-item>
          <el-form-item label="部门名称：" prop="bmmc">
            <el-input
              v-model="formData.bmmc"
              placeholder="请输入部门名称："
              clearable
              :style="{ width: '100%' }"
            >
            </el-input>
          </el-form-item>
          <el-form-item label="所属上级：" prop="sjid">
            <sjid
              v-model="formData.sjid"
              :disabled="formData.sjid.length === 0"
            />
          </el-form-item>
          <el-form-item label="排序：" prop="px">
            <el-input
              v-model="formData.px"
              placeholder="请输入排序："
              clearable
              :style="{ width: '100%' }"
            >
            </el-input>
          </el-form-item>
        </el-row>
      </el-form>
      <div slot="footer">
        <el-button @click="close">取消</el-button>
        <el-button type="primary" @click="handelConfirm">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import sjid from "@/views/components/cascader/sjid.vue";
import { DeepCopyUtils } from "@/utils/LeeUtils";
import { saveTDznbm } from "@/api/app/manager/department";
export default {
  inject: ["eventBus"],
  inheritAttrs: false,
  components: { sjid },
  props: [],
  data() {
    return {
      formData: {
        sign: undefined,
        bmmc: undefined,
        sjid: [],
        px: undefined,
      },
      rules: {
        bmmc: [
          {
            required: true,
            message: "请输入部门名称：",
            trigger: "blur",
          },
        ],
        sjid: [
          {
            required: true,
            message: "请选择所属上级：",
            trigger: "change",
          },
        ],
        px: [
          {
            required: true,
            message: "请输入排序：",
            trigger: "blur",
          },
          {
            validator: (rule, value, callback) => {
              const intValue = parseInt(value);
              if (Number.isNaN(intValue) || intValue !== parseInt(value)) {
                callback(new Error("排序必须是整数"));
              } else {
                callback();
              }
            },
            trigger: "blur",
          },
        ],
      },
    };
  },
  computed: {
    ...mapGetters(["znbm"]),
  },
  watch: {},
  created() {},
  mounted() {},
  methods: {
    onInit(_, row) {
      const znbm = DeepCopyUtils.deepCopy(this.znbm.data);
      let _row = DeepCopyUtils.deepCopy(row);

      // 递归出联机选择器需要的数组
      const findHierarchy = function (id, data) {
        let hierarchy = [id];

        for (let i = 0; i < data.length; i++) {
          if (data[i].id === id) {
            if (data[i].sjid !== null) {
              // Combine the arrays
              hierarchy = findHierarchy(data[i].sjid, data).concat(hierarchy);
            }
            break;
          }
        }

        return hierarchy;
      };

      let sjid = findHierarchy(_row.id, znbm);
      sjid.splice(sjid.length - 1, 1);

      _row.sjid = sjid;
      this.formData = _row;
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

        if (formData.sjid.length > 0) {
          formData.sjid = formData.sjid[formData.sjid.length - 1];
        } else {
          formData.sjid = formData.sjid[0];
        }

        saveTDznbm(formData)
          .then((response) => {
            this.close();
            this.$message({
              message: response['msg'],
              type: "success",
            });

            this.$emit("data-refresh");
            this.eventBus.$emit("refresh");
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
<style>
</style>
