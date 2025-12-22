<template>
  <div class="tree-container">
    <el-tree
      v-loading="isLoading"
      ref="treeRef"
      class="tree"
      :data="filteredTree"
      :props="defaultProps"
      :highlight-current="true"
      @node-click="handleNodeClick"
      node-key="id"
      :default-expanded-keys="['297e70548906150c0189061566a90000']"
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
      isLoading: false,
      rawData: [],
      filteredTree:[],
      defaultProps: {
        children: "children",
        label: "name",
      },
      // 默认查询id
      defaultNodeKey: "297e70548a0e07da018a0e09f2230000",
    };
  },
  methods: {
    // 获取数据 && 数据初始化
    handleGetTDgwgl() {
      this.isLoading = true;
      getTDgwglTree().then((res) => {
        this.rawData = res.data;
        this.isLoading = false;
        this.$nextTick(() => {
          this.filteredTree = (this.filterActiveNodes(this.rawData))
          this.handleNodeClick(this.$refs.treeRef.getCurrentNode());
        });
      });
    },

    // 筛选树结构
    filterActiveNodes(nodes) {
      // 定义一个内部递归函数，以避免this上下文问题
      const filterNodes = (node) => {
        // 检查当前节点是否active
        if (node["isHiddenForPageGwzz"] !== true) {
          // 创建一个新节点，包含当前节点的所有属性，但只包含active为true的子节点
          const newNode = { ...node };
          if (node.children && node.children.length) {
            newNode.children = node.children
              .map(childNode => filterNodes(childNode)) // 这里使用局部函数进行递归调用
              .filter(child => child !== null); // 过滤掉null值
          }
          return newNode;
        } else {
          // 如果当前节点不active，返回null
          return null;
        }
      };

      // 如果传入的是数组，对数组中的每个节点进行处理
      return nodes.map(node => filterNodes(node)).filter(node => node !== null);
    },

    handleNodeClick(node) {
      this.eventBus.$emit("selectNode", node);
      if (node.type === "gwgl") {
        let gwglid = this.getPathToRoot(this.rawData, node.id);
        this.eventBus.$emit("onSearch", gwglid);
      } else if (node.type === "znbm" && node["isInternal"] === false) {
        let znbmid = this.getPathToRoot(this.rawData, node.id);
        console.log("znbmid", znbmid);
        this.eventBus.$emit("onSearchBmzz", znbmid);
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
