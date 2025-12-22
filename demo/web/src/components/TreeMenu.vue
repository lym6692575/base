<template>
  <ul>
    <li v-for="node in treeData" :key="node.id">
      <span @click="handleNodeClick(node)">{{ node.name }}</span>
      <tree-menu
        :treeData="node.children"
        v-if="node.children"
        @node-click.native="handleNodeClick"
      />
    </li>
  </ul>
</template>

<script>
export default {
  name: "TreeMenu",
  props: {
    treeData: {
      type: Array,
      required: true,
    },
  },
  methods: {
    handleNodeClick(node) {
      if (node.path) {
        this.$store.dispatch("base/setSelectedNode", node);
      }
    },
  },
};
</script>

<style scoped>
ul {
  list-style-type: none;
  padding-left: 0;
}

li {
  cursor: pointer;
}
</style>