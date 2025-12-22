<template>
  <el-dialog
    title="新增文件类别"
    :visible.sync="dialogVisible"
    width="30%"
  >
    <div>
      <!-- 类别名称 -->
      <elInput name="类别名称" width="260px" v-model="params.lbmc"></elInput>
      <!-- 显示排序 -->
      <elInput
        class="mt10"
        name="显示排序"
        width="260px"
        v-model="params.ord"
      ></elInput>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="handleSaveTDywlb">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import elInput from "@/components/elementUI/el-input";
import { saveTDywlb } from "@/api/app/manager/category";
import { mapGetters } from 'vuex'
export default {
  data() {
    return {
      params: {
        lbmc: "", // 类别名称
        ord: "", // 显示排序
        sfty: "1", // 是否通用
      },

      dialogVisible: false,
    };
  },
  components: {
    elInput,
  },
  computed: {...mapGetters(["userInfo"])},
  methods: {

    // 保存逻辑
    handleSaveTDywlb() {
      const params = this.params;
      params.cjr = this.userInfo.realname
      saveTDywlb(params).then((res) => {
        if (res.result) {
          this.$message({
            message: res.msg,
            type: "success",
          });
          this.$emit("data-saved");
          this.dialogVisible = false;
        }
      });
    },
  },
};
</script>
