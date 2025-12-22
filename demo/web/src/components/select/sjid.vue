<template>
  <lee-select
    :options="this.processedZnbm"
    :key="generatedKey"
    v-bind="$attrs"
    v-on="$listeners"
    placeholder="请选择所属上级"
    :showAllOptions="false"
  />
</template>

<script>
import leeSelect from "@/components/elementUI/lee-select";
import { mapGetters } from "vuex";

export default {
  props: {
    value: {
      type: String,
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
      selectedValue: this.value,
    };
  },
  methods: {},
  watch: {
    // 监听 sonValue 临时变量，如果临时变量发生变化，那么通过 $emit 将新的值传递给父组件
    selectedValue(value) {
      // 【注意】newValue x需要和 model.event 定义的值一致
      this.$emit("newValue", this.selectedValue);
    },
  },
  components: {
    leeSelect,
  },
  // 添加生命周期函数
  mounted() {},
  beforeDestroy() {},
  computed: {
    ...mapGetters(["znbm"]),
    processedZnbm() {
      const znbm = this.znbm.data;
      const arr = [];
      if (znbm) {
        znbm.forEach((element) => {
          arr.push({
            value: String(element.id),
            label: element.bmmc,
            id: element.id,
          });
        });
      }
      return arr;
    },
  },
};
</script>