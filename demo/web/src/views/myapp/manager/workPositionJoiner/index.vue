<template>
  <el-row style="width: 100%">
    <el-container class="el-container" ref="containerElement">
      <el-aside width="auto" class="aside" ref="asideElement">
        <work-position-joiner-aside class="pr20"/>
      </el-aside>
      <el-container class="pl20">
        <work-position-joiner-main ref="mainElement"/>
      </el-container>
    </el-container>
  </el-row>
</template>

<script>
import ResizeObserver from 'resize-observer-polyfill'
import Vue from 'vue'
import workPositionJoinerAside from './workPositionJoinerAside'
import workPositionJoinerMain from './workPositionJoinerMain'

export default {
  data() {
    return {
      // 创建一个 Event Bus
      eventBus: new Vue({
        data: {
          gwnameArr: [] // 初始值
        }
      }),
      observer: null
    }
  },
  components: {
    workPositionJoinerAside,
    workPositionJoinerMain
  },

  // !!!
  // provide / inject 本身不是响应式
  provide() {
    // 提供 Event Bus 给所有子组件
    return {
      eventBus: this.eventBus
    }
  },
  created() {},
  methods: {},
  mounted() {
    this.observer = new ResizeObserver(entries => {
      for (const entry of entries) {
        // 获取新的宽度
        const newWidth = entry.contentRect.width

        // 计算并设置 mainElement 的新宽度
        if (this.$refs.containerElement.$el instanceof Element && this.$refs.mainElement.$el instanceof Element) {
          const containerWidth = this.$refs.containerElement.$el.offsetWidth
          const newMainWidth = containerWidth - newWidth - 40

          // 直接通过 ref 设置新的宽度
          this.$refs.mainElement.$el.style.width = `${newMainWidth}px`
        }
      }
    })
    this.observer.observe(this.$refs.asideElement.$el)
  },
  beforeDestroy() {
    this.observer.disconnect()
  },
  computed: {}
}
</script>
<style lang="sass" scoped>
.el-container
  height: 100%

.aside
  overflow: auto
  height: calc(100vh - 72px - 122px)
  border-right: solid 1px #e6e6e6
  background: #fff
  margin-top: 12px
  margin-right: 12px
</style>
