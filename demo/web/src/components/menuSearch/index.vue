<template>
  <div v-if="layout == 'horizontal' && settings.showSearchText">
    <el-input
      size="mini"
      style="font-size:14px"
      v-model="search"
      clearable
      prefix-icon="el-icon-search"
      placeholder="请输入要搜索的菜单名称"
    />
  </div>
</template>

<script>
import { mapGetters } from "vuex";
export default {
  name: "menuSearch",
  data() {
    return {
      search: ""
    };
  },
  computed: {
    ...mapGetters(["settings"]),
    layout() {
      return this.$store.state.settings.layout;
    }
  },
  watch: {
    search(val) {
      this.$store.dispatch("setMenuSearchVal", val);
    }
  },
  mounted() {},
  methods: {}
};
</script>

<style lang="scss" scoped>
.header-search {
  font-size: 0 !important;

  .search-icon {
    cursor: pointer;
    font-size: 18px;
    vertical-align: middle;
  }

  .header-search-select {
    font-size: 18px;
    transition: width 0.2s;
    width: 0;
    overflow: hidden;
    background: transparent;
    border-radius: 0;
    display: inline-block;
    vertical-align: middle;

    /deep/ .el-input__inner {
      border-radius: 0;
      border: 0;
      padding-left: 0;
      padding-right: 0;
      box-shadow: none !important;
      border-bottom: 1px solid #d9d9d9;
      vertical-align: middle;
      color: #fff;
      background: none;
    }
  }

  &.show {
    .header-search-select {
      width: 210px;
      margin-left: 10px;
    }
  }
}
</style>
