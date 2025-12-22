<template>
  <div>
    <lee-select
      :options="processedYwlb"
      v-bind="$attrs"
      :key="generatedKey"
      v-on="$listeners"
      placeholder="请选择文件类别"
    />
  </div>
</template>

<script>
import leeSelect from "@/components/elementUI/lee-select";
import {mapGetters} from "vuex";

export default {
  components: {
    leeSelect,
  },
  props: {
    sfty: {
      type: String,
      default: "1",
    },
    wjlbName: {
      type: Array,
      default: () => []
    }
  },
  computed: {
    ...mapGetters(["wjlb"]),
    processedYwlb() {
      const wjlb = this.wjlb.data;
      const arr = [];
      if (wjlb) {
        wjlb.forEach((element) => {
          if (this.wjlbName.length > 0) {
            if (this.wjlbName.includes(element.lbmc)) {
              arr.push({value: String(element.id), label: element.lbmc});
            }
          } else {
            arr.push({value: String(element.id), label: element.lbmc});
          }

        });
      }
      return arr;
    },
    generatedKey() {
      const timestamp = new Date().getTime();
      const random = Math.floor(Math.random() * 1000); // 生成一个随机数，范围为0到999
      return timestamp + "_" + random + "wjlb";
    },
  },
  mounted() {
    let sfty = {sfty: "1"};
    if (this.ssyw === "2") {
      sfty = {sfty: "2"};
    } else if (this.sfty === "0") {
      sfty = "";
    }
    this.$store.dispatch("common/setWjlb", sfty);
  },
};
</script>
