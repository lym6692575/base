<template>
  <div>
    <el-menu
      :default-active="selectedNode"
      class="el-menu"
      @open="handleOpen"
      @close="handleClose"
      :collapse="isCollapse"
      unique-opened
    >
      <el-submenu
        v-for="(item, i) in processedRouterArr"
        :key="item.name"
        :index=item.name
      >
        <template slot="title">
          <div @click="handleClick(item,i)">
            <i :class="item.icon"></i>
            <span slot="title">{{ item.meta.label }}</span>
          </div>
        </template>
        <div>
          <div v-for="(childItem,i) in item.children" :key="childItem.label">
            <el-menu-item-group v-if="childItem.hidden !== true">
              <el-menu-item
                @click="handleChangeContent(childItem,i)"
                :index=childItem.name
              >
                {{ childItem.meta.label }}
              </el-menu-item>
            </el-menu-item-group>
          </div>
        </div>
      </el-submenu>
    </el-menu>
  </div>
</template>

<script>
import {mapGetters} from 'vuex'

export default {
  data() {
    return {
      isCollapse: false
    }
  },
  methods: {
    handleOpen(key, keyPath) {
      // console.log(key, keyPath)
    },
    handleClose(key, keyPath) {
      // console.log(key, keyPath)
    },
    handleClick(item, i) {
      // 如果有子项
      if (item.children && item.children.length) {
        // 查找子项中路径为空的子项
        const childWithEmptyPath = item.children.find(
          (child) => child.path === ''
        )
        // 如果找到了路径为空的子项
        if (childWithEmptyPath) {
          // 跳转到该子项
          const currentRouteName = this.$route.name
          // 判断当前路由的名称和传入的名称是否相同
          if (currentRouteName === childWithEmptyPath.name) {
            return // 如果相同则不执行后续逻辑
          }
          console.log("handleClick", childWithEmptyPath)
          this.$store.dispatch('base/setSelectedNode', {node: childWithEmptyPath})
        }
      } else {
        // 如果没有子项，直接跳转
        return
      }
    },
    handleChangeContent(node, i) {
      this.$store.dispatch('base/setSelectedNode', {node})
    }
  },
  mounted() {
    // console.log(this.processedRouterArr);
  },
  computed: {
    ...mapGetters(['userInfo','routerArr', 'is', 'selectedNode']),
    processedRouterArr() {
      let routers = this.routerArr.filter(
        (item) => item && item.hidden !== true
      )
      if (this.userInfo.right == 0) {
        routers = routers.filter((item) => item.name !== 'manager')
      }
      return routers
    }
  }
}
</script>
<style lang="sass" scoped>
.el-menu
  border: none
</style>
