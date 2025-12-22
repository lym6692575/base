<template>
  <div class="icon-tool-box">
    <span
      :title="menuLinkage ? '取消菜单关联' : '与菜单关联'"
      @click="menuLinkage = !menuLinkage"
      :class="{
        'icon-tool': true,
        fa: true,
        'fa-link': !menuLinkage,
        'fa-unlink': menuLinkage
      }"
    ></span>
  </div>
</template>

<script>
export default {
  name: "menuTool",
  data() {
    return {};
  },
  computed: {
    menuLinkage: {
      get() {
        return this.$store.state.settings.menuLinkage;
      },
      set(val) {
        this.$store.dispatch("settings/changeSetting", {
          key: "menuLinkage",
          value: val
        });
      }
    }
  },
  methods: {}
};
</script>

<style lang="scss">
.icon-tool-box {
  display: inline-block;
  text-align: right;
  .icon-tool {
    margin: 0 3px;
    transition: all 0.5s ease;
    padding: 4px;
    cursor: pointer;
    font-size: 14px;
    &:hover {
      border-radius: 3px;
      background: rgba(0, 0, 0, 0.1);
    }
  }
}
</style>
