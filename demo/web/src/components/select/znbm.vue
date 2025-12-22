<template>
  <div>
    <lee-select
      :options="this.processedZnbm"
      :key="generatedKey"
      v-bind="$attrs"
      v-on="$listeners"
      placeholder="请选择职能部门"
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
    sign: {
      type: String,
      default: "1",
    },
    level: {
      type: String,
      default: 'all'
    },
    bmName: {
      type: Array,
      default: () => []
    }
  },
  computed: {
    ...mapGetters(["znbm"]),
    processedZnbm() {
      let znbm = this.sign === '1' ?
        this.znbm.data.filter(item => item.sign === '1')
        : this.znbm.data;

      // console.log("sign", this.sign)
      // console.log("znbm", znbm)
      const arr = [];

      // 整理数据
      const findParent = (node, bmmc, level) => {
        const parent = znbm.find((item) => item.id === node.sjid);
        if (parent) {
          const result = findParent(parent, parent.bmmc + "--" + bmmc, level + 1);
          return {
            ...result,
            parentName: parent.bmmc
          };
        } else {
          return {label: bmmc, level: level, parentName: null};
        }
      };
      if (znbm) {
        znbm.forEach((element) => {
          let {label, level, parentName} = findParent(element, element.bmmc, 0);
          if (this.level === "all" || (this.level === "2" && level === 2)) {
            if (this.bmName.length > 0) {
              if (this.bmName.includes(parentName)) {
                arr.push({value: String(element.id), label: label, level: level, parent: parentName});
              }
            } else {
              arr.push({value: String(element.id), label: label, level: level, parent: parentName});
            }
          }
        });
      }

      // if (znbm) {
      //   znbm.forEach((element) => {
      //     let {label, level} = findParent(element, element.bmmc, 0);
      //     if (this.level === "all") {
      //       arr.push({value: String(element.id), label: label, level: level});
      //     } else if (this.level === "2") {
      //       if (level === 2)
      //         arr.push({value: String(element.id), label: label, level: level});
      //     }
      //   });
      // }

      return arr;
    },

    generatedKey() {
      const timestamp = new Date().getTime();
      const random = Math.floor(Math.random() * 1000); // 生成一个随机数，范围为0到999
      return timestamp + "_" + random;
    },
    mounted() {
      this.$store.dispatch("common/setZnbm");
    },
  },
};
</script>
