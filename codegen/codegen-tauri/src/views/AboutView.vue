<template>
  <div class="about">
    <h1>关于 Codegen Tool</h1>
    <p>Codegen Tool 是一个代码生成工具，用于简化开发流程，提高工作效率。</p>
    <p>该可视化管理平台提供了直观的界面，方便用户配置和使用代码生成功能。</p>
    <el-divider></el-divider>
    <h3>功能特性</h3>
    <el-list>
      <el-list-item>• 可视化配置代码生成参数</el-list-item>
      <el-list-item>• 支持多种代码模板</el-list-item>
      <el-list-item>• 一键生成代码文件</el-list-item>
      <el-list-item>• 代码生成历史记录</el-list-item>
    </el-list>
  </div>
</template>

<script setup>
</script>

<style scoped>
.about {
  max-width: 800px;
  margin: 50px auto;
  padding: 0 20px;
}

h1 {
  font-size: 2rem;
  margin-bottom: 20px;
  color: #333;
}

h3 {
  font-size: 1.5rem;
  margin: 30px 0 15px 0;
  color: #333;
}

p {
  line-height: 1.6;
  margin-bottom: 15px;
  color: #666;
}

.el-list {
  margin-top: 10px;
}
</style>