<template>
  <div class="tree-container">
    <el-tree
        v-loading="isLoading"
        ref="treeRef"
        class="tree"
        :data="rawData"
        :props="defaultProps"
        :highlight-current="true"
        @node-click="handleNodeClick"
        node-key="id"
        :default-expanded-keys="['297e70548906150c0189061566a90000']"
    ></el-tree>
  </div>
</template>

<script>
import {getTDgwglTree} from '@/api/app/manager/workPosition'

export default {
  // 调用事件总线
  inject: ['eventBus'],
  data() {
    return {
      // 加载状态
      isLoading: false,
      rawData: [],
      defaultProps: {
        children: 'children',
        label: 'name'
      }
    }
  },
  methods: {
    // 获取数据 && 数据初始化
    handleGetTDgwgl() {
      this.isLoading = true
      getTDgwglTree().then((res) => {
        this.rawData = res.data
        this.isLoading = false
      })
    },
    handleNodeClick(node) {
      if (node.type === "gwgl") {
        console.log("gwgl")
      } else if (node.type === "znbm" && node["isInternal"] === false) {
        console.log("znbm")
      }
    }
  },
  beforeDestroy() {
  },
  mounted() {
    this.handleGetTDgwgl()
    this.eventBus.$on('refresh', this.handleGetTDgwgl)
  },
}
</script>

<style lang="sass" scoped>
.tree-container
  .tree
    min-width: 200px
</style>
