<template>
  <div>
    <p>{{ message }}</p>
  </div>
</template>

<script>
import loadRoute from '@/utils/loadingRouter.js'
import { RouterUtils } from '@/utils/ProjectUtils'

export default {
  name: 'business',
  data() {
    return {
      message: '加载中...'
    }
  },
  methods: {},
  // 添加生命周期函数
  created() {
    loadRoute()
    const home = RouterUtils.findRouterByName('home')
    this.$store.dispatch('base/setSelectedNode', { node: home })
  },
  mounted() {
    this.$store.dispatch('common/setXldj')
    this.$store.dispatch('common/setZnbm')
    this.$store.dispatch('common/setYwlb')
    this.$store.dispatch('common/setFjlx')
    this.$store.dispatch('common/setGw')
  },
  beforeDestroy() {
  }
}
</script>

<style scoped>
/* 可选：添加组件的样式 */
</style>
