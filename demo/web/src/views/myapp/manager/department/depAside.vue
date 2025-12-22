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
import {getDepartmentTree} from '@/api/app/manager/department'

export default {
  // 调用事件总线
  inject: ['eventBus'],
  data() {
    return {
      isLoading: false,
      rawData: [],
      defaultProps: {
        children: 'children',
        label: 'bmmc'
      }
    }
  },
  methods: {
    handleNodeClick(data) {
      console.log(data)
    },
    handleGetTreeData() {
      this.isLoading = true;
      console.log("getTree")
      getDepartmentTree().then((res) => {
        console.log(res.data)
        this.rawData = res.data
        this.isLoading = false;
      })
    }
  },
  // 添加生命周期函数
  mounted() {
    this.handleGetTreeData()
    this.eventBus.$on('refresh', this.handleGetTreeData)
  },
  beforeDestroy() {
  },
}
</script>
<style lang="sass" scoped>
.tree-container
  height: 100%

  .tree
    min-width: 200px
</style>
