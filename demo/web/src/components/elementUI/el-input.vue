<template>
  <div class="container">
    <span v-if="name" :style="{ minWidth: (name.length + 1) * 14 + 'px' }">
      {{ name + "：" }}
    </span>
    <el-input
      class="input"
      v-bind="$attrs"
      v-on="$listeners"
      placeholder="请输入内容"
      clearable
    ></el-input>
  </div>
</template>
  
<script>
export default {
  props: {
    name: {
      type: String,
      default: undefined,
    },
  },
  data() {
    return {};
  },
};
</script>
  
<style lang="sass" scoped>
.container
  display: inline-flex
  align-items: center
  font-size: 14px
  width: 100%
  & span
    margin-right: 10px
  & span
    max-width: 160px
</style>