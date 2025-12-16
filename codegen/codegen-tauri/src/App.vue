<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'
// 导入Element Plus图标
import { House, InfoFilled, Cpu, ArrowRight, ArrowLeft, User, CaretBottom } from '@element-plus/icons-vue'

const route = useRoute()
const isCollapse = ref(false)

// 导航菜单数据
const menuItems = [
  {
    index: '/',
    title: '首页',
    icon: House
  },
  {
    index: '/config-manager',
    title: '配置管理',
    icon: InfoFilled
  },
  {
    index: '/about',
    title: '关于',
    icon: InfoFilled
  }
]
</script>

<template>
  <div class="app-container">
    <!-- 侧边栏导航 -->
    <el-aside :width="isCollapse ? '64px' : '200px'" style="background-color: #001529; color: white;">
      <div class="logo-container">
        <h1 class="logo-text" v-if="!isCollapse">Codegen</h1>
        <el-icon v-else><Cpu /></el-icon>
      </div>
      <el-menu
        :default-active="route.path"
        class="el-menu-vertical-demo"
        :collapse="isCollapse"
        background-color="#001529"
        text-color="#fff"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item
          v-for="item in menuItems"
          :key="item.index"
          :index="item.index"
        >
          <template #icon>
            <el-icon><component :is="item.icon" /></el-icon>
          </template>
          <template #title>{{ item.title }}</template>
        </el-menu-item>
      </el-menu>
      <el-button
        class="collapse-btn"
        type="text"
        @click="isCollapse = !isCollapse"
      >
        <el-icon>{{ isCollapse ? 'ArrowRight' : 'ArrowLeft' }}</el-icon>
      </el-button>
    </el-aside>

    <!-- 主内容区域 -->
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header style="display: flex; align-items: center; justify-content: space-between; padding: 0 20px; background-color: #fff; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.09);">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="route.path !== '/'">
              {{ menuItems.find(item => item.index === route.path)?.title || '页面' }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-icon><User /></el-icon>
              管理员
              <el-icon class="el-icon--right"><CaretBottom /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人中心</el-dropdown-item>
                <el-dropdown-item>设置</el-dropdown-item>
                <el-dropdown-item divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区域 -->
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </div>
</template>

<style scoped>
.app-container {
  width: 100%;
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.logo-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60px;
  border-bottom: 1px solid #233445;
  margin-bottom: 20px;
}

.logo-text {
  font-size: 18px;
  font-weight: bold;
  color: white;
  margin: 0;
}

.collapse-btn {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  color: white;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.user-info .el-icon {
  margin-right: 5px;
}

.el-menu-vertical-demo {
  border-right: none;
}
</style>
