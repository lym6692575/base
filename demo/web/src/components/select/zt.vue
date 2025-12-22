<template>
  <lee-select
    :options="processedZt"
    :key="generatedKey"
    v-bind="$attrs"
    v-on="$listeners"
    placeholder="请选择状态"
  />
</template>
<script>
import leeSelect from "@/components/elementUI/lee-select";
import {mapGetters} from "vuex";

export default {
  components: {
    leeSelect,
  },
  props: {
    subOptions: {
      type: Boolean,
      default: true
    }
  },
  methods: {},
  // 添加生命周期函数
  mounted() {
    this.$store.dispatch("common/setFjlx");
  },
  beforeDestroy() {
  },
  computed: {
    ...mapGetters(["zt"]),
    processedZt() {
      const zt = this.zt.data;
      const arr = [];
      if (zt) {
        zt.forEach((element) => {
          arr.push({value: element.key, label: element.value});
        });
        if (this.subOptions) {
          arr.push({value: [1, 2], label: "新增+修订"})
        }
      }
      return arr;
    },
    generatedKey() {
      const timestamp = new Date().getTime();
      const random = Math.floor(Math.random() * 1000); // 生成一个随机数，范围为0到999
      return timestamp + "_" + random;
    },
  },
};
</script>
