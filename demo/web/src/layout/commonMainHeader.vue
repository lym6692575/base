<template>
  <div class="container">
    <div class="text-container box">
      <span>{{ this.$route.meta.label }}</span>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {};
  },
  methods: {},
  mounted() {},
  computed: {},
};
</script>

<style lang="scss" scoped>
.container {
  padding: 10px 0 20px 0;
}
.text-container {
  font-weight: bold;
  color: #303133;
}
.box {
  position: relative;
  padding-left: 20px; /* 为小方块预留空间 */
}

.box::before {
  content: "";
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 5px;
  height: 18px;
  border-radius: 10px;
  background-color: rgb(0, 62, 169);
}
</style>
