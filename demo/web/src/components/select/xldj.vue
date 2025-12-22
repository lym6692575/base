<template>
  <lee-select
    :options="this.processedXldj"
    :key="generatedKey"
    v-bind="$attrs"
    v-on="$listeners"
    placeholder="请选择效力等级"
  />
</template>

<script>
import leeSelect from "@/components/elementUI/lee-select";
import { mapGetters } from "vuex";

export default {
  props: {},
  components: {
    leeSelect,
  },
  methods: {},
  // 添加生命周期函数
  mounted() {},
  beforeDestroy() {},
  computed: {
    ...mapGetters(["xldj"]),
    processedXldj() {
      const xldj = this.xldj.data;
      const arr = [];
      if (xldj) {
        xldj.forEach((element) => {
          arr.push({ value: String(element.id), label: element.djmc });
        });
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