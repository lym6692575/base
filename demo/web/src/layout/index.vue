<template>
  <div class="wapper">
    <el-header class="header">
      <common-header></common-header>
    </el-header>
    <el-container class="content">
      <el-aside class="aside" width="12%">
        <commonAside />
      </el-aside>
      <el-container>
        <el-header class="tag-bar">
          <tagBar></tagBar>
        </el-header>
        <el-main class="main">
          <el-header class="main-header" height="none">
            <common-main-header></common-main-header>
          </el-header>
          <router-view class="main-body" />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import commonAside from "./commonAside.vue";
import commonHeader from "./commonHeader.vue";
import commonMainHeader from "./commonMainHeader.vue";
import tagBar from "./tagBar.vue"
import { mapGetters } from "vuex";
export default {
  name: "layout",
  data() {
    return { screenHeight: 0 };
  },
  components: {
    commonAside,
    commonHeader,
    commonMainHeader,
    tagBar
  },
  computed: {
    ...mapGetters(["selectedNode"]),
    processedRouterArr() {
      return this.routerArr.filter((item) => item.hidden !== true);
    },
  },
};
</script>
<style lang="sass" scoped>
.wapper
  height: calc(100vh)
  background: #f0f4f7
.header
  height: 60px
  background-image: linear-gradient(120deg,rgb(0, 62, 169) 0%,rgb(72, 131, 229) 100%)

.content
  height: calc(100vh - 74px)
.aside
  padding: 8px
  overflow: auto
  height: 100%
  min-width: 240px
  border-right: solid 1px #e6e6e6
  background: #fff
  margin-top: 12px
  margin-right: 12px
  border-top-right-radius: 10px
  border-bottom-right-radius: 10px
.main
  margin-top: 12px
  height: 100%
  background: #fff
  border-top-left-radius: 10px
  border-bottom-left-radius: 10px
  border-left: solid 1px #e6e6e6
  width: 100%

.tag-bar
  margin-top: 12px
  background: #fff
  border-top-left-radius: 10px
  border-bottom-left-radius: 10px
  padding: 0

.main-header
  padding: 0
  border-bottom: solid 1px #e6e6e6
  margin-bottom: 20px
</style>
