<template>
  <div>
    <el-row>
      <el-col :span="24">
        <czsp ref="czspRef" v-model="queryParams"></czsp>
        <gwzz-table ref="gwzzTableRef" v-model="queryParams"/>
        <gzzd-table ref="gzzdTableRef" v-model="queryParams"/>
        <ywlc-table ref="ywlcTableRef" v-model="queryParams"/>
        <!--        <hgyywj-table ref="hgyywjTableRef" v-model="queryParams" />-->
        <!--
        20231123改该表为合规文件应用类别查询
        嫌麻烦未修改vue文件名称
        -->
        <othersHgwjTable ref="othersHgwjTableRef" v-model="queryParams"/>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import czsp from "./czsp";
import gwzzTable from "./gwzzTable.vue";
import gzzdTable from "./gzzdTable.vue";
import ywlcTable from "./ywlcTable.vue";
import hgyywjTable from "./hgyywjTable.vue";
import othersHgwjTable from "./othersHgwjTable.vue";

export default {
  // 调用事件总线
  inject: ["eventBus"],
  data() {
    return {
      dialogVisible: false,
      queryParams: {
        page: "1",
        rows: "5",
        // 岗位id
        gwglid: [],
      },
    };
  },
  components: {
    czsp,
    gwzzTable,
    gzzdTable,
    ywlcTable,
    hgyywjTable,
    othersHgwjTable,
  },
  methods: {
    // 查询
    onSearch(gwglid) {
      this.queryParams.page = 1;
      if (gwglid) {
        this.queryParams.gwglid = gwglid;
      }
      this.$refs.czspRef.handleGetData(this.queryParams);
      this.$refs.gwzzTableRef.onSearch(this.queryParams);
      this.$refs.gzzdTableRef.onSearch(this.queryParams);
      this.$refs.ywlcTableRef.onSearch(this.queryParams);
      // this.$refs.hgyywjTableRef.handleGetTableData(this.queryParams);
      this.$refs.othersHgwjTableRef.onSearch(this.queryParams);
    },
  },
  // 添加生命周期函数
  mounted() {
    // 给事件总线添加搜索方法
    this.$nextTick(() => {
      this.eventBus.$on("onSearch", (gwglid) => this.onSearch(gwglid));
    });
  },
  beforeDestroy() {
  },
  computed: {},
};
</script>
