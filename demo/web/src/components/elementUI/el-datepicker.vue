<template>
  <div class="date-picker-container">
    <div class="date-picker-item">
      <span>{{ startLabel + "：" }}</span>
      <el-date-picker
        v-model="date.start"
        type="date"
        placeholder="选择日期"
      ></el-date-picker>
    </div>
    <div class="date-picker-item">
      <span>至：</span>
      <el-date-picker
        v-model="date.end"
        type="date"
        placeholder="选择日期"
      ></el-date-picker>
    </div>
  </div>
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