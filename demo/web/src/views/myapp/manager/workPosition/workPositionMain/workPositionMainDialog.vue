<template>
  <div>
    <el-dialog
      v-bind="$attrs"
      v-on="$listeners"
      @open="onOpen"
      @close="onClose"
      :title="actionType == 'edit' ? '编辑岗位' : '新增岗位'"
    >
      <el-form
        ref="elForm"
        :model="formData"
        :rules="rules"
        size="medium"
        label-width="100px"
        label-position="left"
      >
        <el-row>
          <el-form-item label="是否启用：" prop="sign" required>
            <el-switch
              v-model="formData.sign"
              :active-value="'1'"
              :inactive-value="'2'"
            ></el-switch>
          </el-form-item>
          <el-form-item label="岗位名称：" prop="gwmc">
            <el-input
              v-model="formData.gwmc"
              placeholder="请输入岗位名称"
              clearable
              :style="{ width: '100%' }"
            >
            </el-input>
          </el-form-item>
          <el-form-item label="所属部门：" prop="ssid">
            <sjid v-model="formData.ssid" :checkStrictly="false" />
          </el-form-item>
          <el-form-item label="排序：" prop="px">
            <el-input
              v-model="formData.px"
              placeholder="请输入排序"
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
import { saveTDgwgl } from "@/api/app/manager/workPosition";
export default {
  inject: ["eventBus"],
  inheritAttrs: false,
  components: { sjid },
  props: {
    actionType: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      formData: {
        sign: undefined,
        gwmc: undefined,
        ssid: [],
        px: undefined,
      },
      rules: {
        gwmc: [
          {
            required: true,
            message: "请输入部门名称",
            trigger: "blur",
          },
        ],
        ssid: [
          {
            required: true,
            message: "请选择部门",
            trigger: "change",
          },
        ],
        px: [
          {
            required: true,
            message: "请输入排序",
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
    ...mapGetters(["znbm","userInfo"]),
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

      let ssid = findHierarchy(_row.ssid, znbm);

      _row.ssid = ssid;
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
        if(this.actionType!=="edit"){
          formData.cjr = this.userInfo.realname
        }
        if (formData.ssid.length > 0) {
          formData.ssid = formData.ssid[formData.ssid.length - 1];
        } else {
          formData.ssid = formData.ssid[0];
        }

        saveTDgwgl(formData)
          .then((response) => {
            this.close();
            this.$message({
              message: response.msg,
              type: "success",
            });
            this.$emit("data-refresh");
            console.log(this.eventBus);
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
