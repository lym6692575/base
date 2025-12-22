<template>
  <lee-select
    :options="this.processedYwlb"
    :key="generatedKey"
    v-bind="$attrs"
    v-on="$listeners"
  />
</template>

<script>
import leeSelect from "@/components/elementUI/lee-select";
import { mapGetters } from "vuex";

export default {
  props: {
    ssyw: {
      type: String,
      default: "2",
    },
  },
  components: {
    leeSelect,
  },
  mounted() {
    let ssyw = { ssyw: "2" };
    if (this.ssyw == "1") {
      ssyw = { ssyw: "1" };
    }
    this.$store.dispatch("common/setFjlx", ssyw);
  },
  beforeDestroy() {},
  computed: {
    ...mapGetters(["fjlx"]),
    processedYwlb() {
      const fjlx = this.fjlx.data;
      const arr = [];
      if (fjlx) {
        fjlx.forEach((element) => {
          arr.push({ value: String(element.id), label: element.lxmc });
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