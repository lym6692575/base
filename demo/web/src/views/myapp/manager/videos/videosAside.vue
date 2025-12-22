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
        :default-expanded-keys="expandedKeys"
    ></el-tree>

  </div>
</template>

<script>
import {mapGetters} from "vuex";
import {getTDgwglTree} from "@/api/app/manager/workPosition";

export default {
  // 调用事件总线
  inject: ["eventBus"],
  data() {
    return {
      // 加载状态
      isLoading: false,
      // 树结构展开节点
      expandedKeys: ['297e70548906150c0189061566a90000'],
      rawData: [],
      defaultProps: {
        children: "children",
        label: "name",
      },
    };
  },
  methods: {
    setSelectedNode(gwglidArr) {
      // 修改树结构展开状态
      this.expandedKeys = gwglidArr;
      // 设置当前选中节点
      this.$refs.treeRef.setCurrentKey(gwglidArr[gwglidArr.length - 1])
    },

    // 获取数据 && 数据初始化
    handleGetTDgwgl() {
      this.isLoading = true;
      getTDgwglTree().then((res) => {
        this.rawData = res.data;
        this.isLoading = false;
      });
    },

    // 树节点点击事件
    handleNodeClick(data) {
      console.log(data)
      if (data.type === "gwgl") {
        let gwglid = this.getPathToRoot(this.rawData, data.id);
        this.eventBus.$emit("onSearch", gwglid);
      }
    },

    // 获取树的id
    getPathToRoot(tree, leafId) {
      let path = [];

      function dfs(node) {
        // 1. 如果找到叶子节点，则添加到路径中
        if (node.id === leafId) {
          path.unshift(node.id);
          return true;
        }

        // 2. 如果没有子节点，直接返回 false
        if (!node.children) {
          return false;
        }

        // 3. 对每个子节点运行 DFS
        for (let i = 0; i < node.children.length; i++) {
          if (dfs(node.children[i])) {
            // 4. 如果找到了叶子节点，将当前节点添加到路径中
            path.unshift(node.id);
            return true;
          }
        }

        // 5. 如果没有在子节点中找到叶子节点，返回 false
        return false;
      }

      // 启动 DFS
      for (let i = 0; i < tree.length; i++) {
        if (dfs(tree[i])) {
          break;
        }
      }

      return path;
    },
  },
  computed: {
    ...mapGetters(["znbmTree"]),
  },
  mounted() {
    this.handleGetTDgwgl();
    this.$nextTick(() => {
      this.eventBus.$on("setSelectedNode", (gwglid) => this.setSelectedNode(gwglid));
    });
  },
  beforeDestroy() {
  },
};
</script>

<style lang="sass" scoped>
.tree-container
  height: 100%

  .tree
    min-width: 200px
</style>
