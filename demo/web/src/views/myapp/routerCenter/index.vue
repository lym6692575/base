<template>
  <keep-alive :include="cachedViews">
    <router-view />
  </keep-alive>
</template>

<script>
export default {
  name: "routerCenter",
  computed: {
    cachedViews() {
      return this.$store.state.tagsView.cachedViews;
    }
  }
};
</script>
