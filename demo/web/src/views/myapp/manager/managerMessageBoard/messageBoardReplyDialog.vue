<template>
  <div>
    <el-dialog
      v-bind="$attrs"
      v-on="$listeners"
      @open="onOpen"
      @close="onClose"
      title="发布留言"
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
          <el-form-item label="留言主题：" prop="lyzt">
            <el-input
              v-model="formData.lyzt"
              placeholder="请输入留言主题："
              clearable
              :style="{ width: '100%' }"
            >
            </el-input>
          </el-form-item>
          <el-form-item label="留言内容：" prop="lynr">
            <el-input
              v-model="formData.lynr"
              type="textarea"
              placeholder="请输入留言内容："
              :autosize="{ minRows: 4, maxRows: 4 }"
              :style="{ width: '100%' }"
            ></el-input>
          </el-form-item>
          <el-form-item label="答复内容：" :prop="'tdlydf[0].dfjlynr'">
            <el-input
              v-model="formData.tdlydf[0].dfjlynr"
              type="textarea"
              placeholder="请输入留言内容："
              :autosize="{ minRows: 4, maxRows: 4 }"
              :style="{ width: '100%' }"
            ></el-input>
          </el-form-item>
          <el-col :span="12" class="pr10">
            <el-form-item label="联系人：" prop="lxr">
              <el-input
                v-model="formData.lxr"
                placeholder="请输入联系人"
                clearable
                :style="{ width: '100%' }"
              >
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12" class="pl10">
            <el-form-item label="联系电话：" prop="lxdh">
              <el-input
                v-model="formData.lxdh"
                placeholder="请输入联系电话"
                clearable
                :style="{ width: '100%' }"
              >
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="handelConfirm">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import {saveTDlyb} from "@/api/app/manager/messageBoard";
import {DeepCopyUtils} from "@/utils/LeeUtils";

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
        tdlydf: [{dfjlynr: ""}],
      },
      rules: {
        lyzt: [
          {
            required: true,
            message: "请输入留言主题",
            trigger: "blur",
          },
        ],
        lynr: [
          {
            required: true,
            message: "请输入留言内容",
            trigger: "blur",
          },
        ],
        "tdlydf[0].dfjlynr": [
          {
            required: true,
            message: "请输入回复内容",
            trigger: "blur",
          },
        ],
        lxr: [
          {
            required: true,
            message: "请输入联系人",
            trigger: "blur",
          },
        ],
        lxdh: [
          {
            required: true,
            message: "请输入联系电话",
            trigger: "blur",
          },
          {
            validator: (rule, value, callback) => {
              const pattern = /^\d+$/; // 正则表达式：只包含数字
              if (value && !pattern.test(value)) {
                callback(new Error("联系电话必须是纯数字"));
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
  computed: {},
  watch: {},
  created() {
  },
  mounted() {
  },
  methods: {
    onInit(index, row) {
      let _row = DeepCopyUtils.deepCopy(row)
      // 如果没有回复实体创建一个新的回复实体
      if (_row && !_row.tdlydf[0]) {
        _row.tdlydf.push({
          lyztid: _row.id,
          dfjlynr: "",
        });
      }
      this.formData = _row;
    },
    onOpen() {
      console.log("open");
    },
    onClose() {
      this.$refs["elForm"].resetFields();
    },
    close() {
      this.$emit("update:visible", false);
    },
    handelConfirm() {
      this.$refs["elForm"].validate((valid) => {
        if (!valid) return;
        saveTDlyb(this.formData)
          .then((response) => {
            this.close();
            this.$message({
              message: response.msg,
              type: "success",
            });
            this.$emit("data-refresh");
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
