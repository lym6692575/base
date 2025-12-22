<template>
  <div>
<!--    {{ createdLabel }}-->
    <lee-select
      ref="select"
      :options="this.processedGw"
      :key="generatedKey"
      v-bind="$attrs"
      v-on="$listeners"
      placeholder="请选择岗位"
      @update-created-select="handleUpdate"
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
  data() {
    return {
      createdLabel: {}
    }
  },
  computed: {
    ...mapGetters(["gw"]),
    processedGw() {
      let gw = this.sign === '1' ?
        this.gw.data.filter(item => item.sign === '1')
        : this.gw.data;

      const arr = [];
      gw.forEach((item) => {
        arr.push({value: String(item.id), label: (item.ssbmmc + "---" + item.name)});
      })
      return arr;
    },

    generatedKey() {
      const timestamp = new Date().getTime();
      const random = Math.floor(Math.random() * 1000); // 生成一个随机数，范围为0到999
      return timestamp + "_" + random;
    },
  },
  methods: {
    handleUpdate(val) {
      this.createdLabel = val
    }
  },
};
</script>
