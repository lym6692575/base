<template>
  <el-pagination
    @size-change="handleSizeChange"
    @current-change="handleCurrentChange"
    :current-page="currentPage"
    :page-sizes="[10, 15, 20, 25]"
    :page-size="pagesize"
    layout="total, sizes, prev, pager, next, jumper"
    :total="userList.length"
  >
  </el-pagination>
</template>
  
  <script>
export default {
  props: {
    startLabel: {
      type: String,
      default: "默认名称",
    },
    value: {
      type: Object,
      default: "",
    },
  },
  model: {
    // 需要双向绑定的 props 变量名称，也就是父组件通过 v-model 与子组件双向绑定的变量
    prop: "value",
    // 定义由 $emit 传递变量的名称
    event: "newValue",
  },
  data() {
    return {
      date: {
        start: "",
        end: "",
      },
    };
  },
  methods: {
    console() {
      console.log(this.date);
    },
  },
  watch: {
    date: {
      deep: true,
      handler() {
        this.$emit("newValue", this.date);
      },
    },
  },
};
</script>
  
  <style lang="sass" scoped>
.date-picker-container
    display: inline-flex

.date-picker-item
    margin-right: 10px
    & span
        min-width: 80px
        margin-right: 10px
</style>