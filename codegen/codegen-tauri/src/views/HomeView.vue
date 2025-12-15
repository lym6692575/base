<template>
  <div class="home">
    <h1>Codegen Tool 可视化管理平台</h1>
    <p>欢迎使用代码生成工具的可视化管理界面</p>
    <el-button type="primary" @click="$router.push('/codegen')">进入管理页面</el-button>
  </div>
</template>

<script setup>
</script>

<style scoped>
.home {
  text-align: center;
  margin-top: 100px;
}

h1 {
  font-size: 2.5rem;
  margin-bottom: 20px;
  color: #333;
}

p {
  font-size: 1.2rem;
  margin-bottom: 30px;
  color: #666;
}
</style>