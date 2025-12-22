<template>
  <div>
    <lee-select
      :options="processedYwlb"
      v-bind="$attrs"
      :key="generatedKey"
      v-on="$listeners"
      placeholder="请选择业务类别"
    />
  </div>
</template>

<script>
import leeSelect from "@/components/elementUI/lee-select";
import { mapGetters } from "vuex";

export default {
  components: {
    leeSelect,
  },
  props: {
    sfty: {
      type: String,
      default: "1",
    },
  },
  computed: {
    ...mapGetters(["ywlb"]),
    processedYwlb() {
      const ywlb = this.ywlb.data;
      const arr = [];
      if (ywlb) {
        ywlb.forEach((element) => {
          arr.push({ value: String(element.id), label: element.lbmc });
        });
      }
      return arr;
    },
    generatedKey() {
      const timestamp = new Date().getTime();
      const random = Math.floor(Math.random() * 1000); // 生成一个随机数，范围为0到999
      return timestamp + "_" + random + "ywlb";
    },
  },
  mounted() {
    // let sfty = { sfty: "1" };
    // console.log(this.sfty);
    // if (this.ssyw == "2") {
    //   sfty = { sfty: "2" };
    // } else if (this.sfty == "0") {
    //   sfty = "";
    // }
    // console.log("sfty", sfty);
    // this.$store.dispatch("common/setYwlb", sfty);
    this.$store.dispatch("common/setYwlb");
  },
};
</script>
