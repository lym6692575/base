<template>
  <div :class="{ show: show }" class="menu-tree-search" title="搜索菜单">
    <span class="fa fa-search search-icon" @click.stop="click" />
    <el-input
      ref="menuTreeSearch"
      v-model="modelValueThis"
      clear
      size="mini"
      placeholder="请输入菜单名称"
      class="menu-tree-search-input"
    >
    </el-input>
  </div>
</template>

<script>
export default {
  name: "menuTreeSearch",
  model: {
    prop: "modelValue",
    event: "valueChange"
  },
  data() {
    return {
      modelValueThis: undefined,
      search: "",
      show: true
    };
  },
  computed: {
    routes() {
      return this.$store.getters.permission_routes; //.concat(this.$store.getters.manager_routes)
    }
  },
  watch: {
    show(value) {
      // if (value) {
      //   document.body.addEventListener("click", this.closed);
      // } else {
      //   document.body.removeEventListener("click", this.closed);
      // }
    },
    modelValueThis(val) {
      this.$emit("valueChange", val);
    },
    modelValue(val) {
      this.modelValueThis = val;
    }
  },
  created() {
    this.modelValueThis = this.modelValue;
  },
  mounted() {},
  methods: {
    click() {
      this.show = !this.show;
      if (this.show) {
        this.$refs.menuTreeSearch && this.$refs.menuTreeSearch.focus();
      }
    },
    closed() {
      this.$refs.menuTreeSearch && this.$refs.menuTreeSearch.blur();

      this.show = false;
    }
  }
};
</script>

<style lang="scss" scoped>
.menu-tree-search {
  margin: 0 3px;
  font-size: 0 !important;
  display: inline-block;
  max-width: 75%;
  .search-icon {
    cursor: pointer;
    font-size: 14px;
    padding: 4px;
    vertical-align: middle;
    &:hover {
      border-radius: 3px;
      background: rgba(0, 0, 0, 0.1);
    }
  }

  .menu-tree-search-input {
    font-size: 14px;
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
      background: none;
    }
  }

  &.show {
    .menu-tree-search-input {
      width: calc(100% - 35px);
      margin-left: 10px;
    }
  }
}
</style>
