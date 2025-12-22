<template>
  <div class="tree-menu-page">
    123
  </div>
</template>

<script>
import TreeMenu from "@/components/TreeMenu.vue";
import { mapGetters } from "vuex";
import gw from "@/components/select/gw.vue"
export default {
  name: "TreeMenuPage",
  components: {
    TreeMenu,
    gw
  },
  data() {
    return {
      treeData: [],
    };
  },
  mounted() {
    // 从后端获取权限数据，并根据权限动态生成路由和菜单
    // 假设权限数据包含以下结构
    const permissionData = {
      user: {
        id: 1,
        name: "User",
        children: [
          {
            id: 2,
            name: "def",
            path: "/def",
          },
        ],
      },
      product: {
        id: 3,
        name: "Product",        children: [
          {
            id: 4,
            name: "Product List",
            path: "/product-list",
          },
          {
            id: 5,
            name: "Product Details",
            path: "/product-details",
          },
        ],
      },
    };

    // 根据权限数据生成路由和菜单
    this.treeData = this.generateTreeData(permissionData);
    this.generateRoutes(permissionData);
  },
  methods: {
    generateTreeData(permissionData) {
      // 根据权限数据生成树形菜单数据
      // 只包含具有路径的节点
      const treeData = [];

      for (const key in permissionData) {
        const node = permissionData[key];

        if (node.path) {
          treeData.push({
            id: node.id,
            name: node.name,
            path: node.path,
          });
        } else if (node.children) {
          const children = this.generateTreeData(node.children);

          if (children.length > 0) {
            treeData.push({
              id: node.id,
              name: node.name,
              children,
            });
          }
        }
      }

      return treeData;
    },
    generateRoutes(permissionData) {
      // 根据权限数据生成动态路由
      const routes = [];

      for (const key in permissionData) {
        const node = permissionData[key];

        if (node.path) {
          console.log(`@/views/${node.name}.vue`);
          const route = {
            path: node.path,
            name: node.name,
            component: () => import(`@/views/${node.name}.vue`),
          };

          routes.push(route);
        } else if (node.children) {
          const childrenRoutes = this.generateRoutes(node.children);

          if (childrenRoutes.length > 0) {
            const route = {
              path: `/${key}`,
              name: node.name,
              component: () => import("@/views/Default.vue"),
              children: childrenRoutes,
            };

            routes.push(route);
          }
        }
      }
      console.log("routes", routes);
      // 将生成的路由添加到 Vue Router 实例中
      this.$router.addRoute(routes);
      this.$store.dispatch("getRoutes", routes);
      // 打印参数
      // 打印以注册路由
      console.log(this.$router.options.routes);
    },
  },

  computed: {
    ...mapGetters(["selectedNode"]),
  },
};
</script>

<style lang="scss" scoped>
.tree-menu-page {
  display: flex;
}

.content {
  flex: 1;
  padding: 20px;
}
</style>
