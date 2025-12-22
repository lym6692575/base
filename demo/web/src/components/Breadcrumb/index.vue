<template>
  <el-breadcrumb class="app-breadcrumb" separator="/">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item v-for="item in levelList" :key="item.id">
        <!-- <el-breadcrumb-item v-for="item in levelList" :key="item.path"> -->
        <!-- <a v-if="item.path" @click.prevent="handleLink(item)">{{
          item.meta.title
        }}</a> -->
        <span class="no-redirect">{{ item.name }}</span>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script>
import pathToRegexp from "path-to-regexp";

export default {
  data() {
    return {
      levelList: null,
      getVal: false
    };
  },
  watch: {
    $route(route) {
      // if you go to the redirect page, do not update the breadcrumbs
      if (route.path.startsWith("/redirect/")) {
        return;
      }
      let that = this;
      setTimeout(function() {
        that.getBreadcrumb();
      }, 10);
    }
  },
  created() {
    //this.getBreadcrumb();
    this.getValFn();
  },
  methods: {
    getValFn() {
      let that = this;
      if (
        this.$store.getters.visitedViews.length > 0 &&
        this.$store.state.permission.allMenus &&
        this.$store.state.permission.allMenus.length > 0
      ) {
        that.getBreadcrumb();
        this.getVal = null;
      } else {
        this.getVal = setTimeout(() => {
          that.getValFn();
        }, 10);
      }
    },
    getBreadcrumb() {
      // let matched = this.$route.matched.filter(
      //   item => item.meta && item.meta.title
      // );
      // const first = matched[0];
      // this.levelList = matched.filter(
      //   item => item.meta && item.meta.title && item.meta.breadcrumb !== false
      // );
      let tabId = this.$route.query.tabId;
      let tabView = this.$store.getters.visitedViews.filter(
        item => item.tabId == tabId
      );
      let menu = this.$store.state.permission.allMenus.filter(
        item => item.id == tabId
      );
      if (menu.length > 0) {
        this.levelList = this.getParent(this.$route.query.tabId).reverse();
      } else if (tabView.length > 0) {
        this.levelList = tabView;
      } else {
        this.levelList = [{ name: "未命名" }];
      }
    },
    getParent(tabId) {
      let result = [];
      let menu = this.$store.state.permission.allMenus.filter(
        item => item.id == tabId
      );
      result = result.concat(menu);
      if (menu[0].parentMenuId != 0) {
        result = result.concat(
          this.getParent(
            this.$store.state.permission.allMenus.filter(
              item => item.id == menu[0].parentMenuId
            )[0].id
          )
        );
      }
      return result;
    },
    isDashboard(route) {
      const name = route && route.name;
      if (!name) {
        return false;
      }
      return name.trim() === "首页";
    },
    pathCompile(path) {
      const { params } = this.$route;
      var toPath = pathToRegexp.compile(path);
      return toPath(params);
    },
    handleLink(item) {
      const { redirect, path } = item;
      if (redirect) {
        this.$router.push(redirect);
        return;
      }
      this.$router.push(this.pathCompile(path));
    }
  }
};
</script>

<style lang="scss" scoped>
.app-breadcrumb.el-breadcrumb {
  display: inline-block;
  font-size: 14px;
  line-height: 50px;
  margin-left: 8px;

  .no-redirect {
    color: #97a8be;
    cursor: text;
  }
}
</style>
