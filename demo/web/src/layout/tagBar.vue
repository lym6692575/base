<template>
  <div class="container">
    <el-tabs
      class="tag-container"
      :value="selectedNode"
      type="card"
      closable
      @tab-click="onClickTab"
      @tab-remove="removeTab">
      <el-tab-pane
        v-for="(item, index) in tagBarArr"
        :key="item.name"
        :label="item.meta.label"
        :name="item.name"
      >
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>

import {mapGetters} from 'vuex'

export default {
  data() {
    return {};
  },
  methods: {
    // 点击节点
    onClickTab(node) {
      this.$store.dispatch('base/setSelectedNode', {node})
    },

    // 删除节点
    removeTab(nodeName) {
      this.$store.dispatch('base/deleteTagBar', nodeName)
    }
  },
  mounted() {
  },
  computed: {
    ...mapGetters(['tagBarArr', 'selectedNode']),
  },
};
</script>

<style lang="scss" scoped>
.container {
  padding: 10px 0 20px 0;
}

.tag-container {
  padding: 0 20px
}

.container /deep/ .el-tabs__header,
.container /deep/ .el-tabs__item,
.container /deep/ .el-tabs__nav {
  border: none;
  color: #606266;
  font-weight: 100
}

.container /deep/ .el-tabs__item {
  margin-right: 12px;
}

.container /deep/ .is-active {
  color: #0270c1;
}

.container /deep/ .is-active::before {
  content: "";
  position: absolute;
  left: 15%;
  top: 100%;
  transform: translateY(-50%);
  width: 60%;
  height: 1px;
  border-radius: 10px;
  background-color: #003ea9;
}
</style>
