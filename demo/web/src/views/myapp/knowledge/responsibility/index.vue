<template>
  <el-row style="width: 100%">
    <el-container class="el-container">
      <el-aside width="auto" class="aside">
        <work-position-joiner-aside class="pr20" />
      </el-aside>
      <!--空状态-->
      <el-col v-if="!selectedNode">
        <el-empty class="mt40" :image-size="200" description="请选择组织节点"></el-empty>
      </el-col>
      <el-container class="pl20">
        <!-- 岗位职责 -->
        <work-position-joiner-main
          v-show="selectedNode && selectedNode.type !== 'znbm'"
          style="flex: 1"
        />
        <departmentMain
          v-show="selectedNode && selectedNode.type === 'znbm'"
          style="flex: 1"
        />
      </el-container>
    </el-container>
  </el-row>
</template>

<script>
import Vue from "vue";
import workPositionJoinerAside from "./workPositionJoinerAside";
import workPositionJoinerMain from "./workPositionJoinerMain";
import departmentMain from "./departmentMain";

export default {
  inject: ["eventBus"],
  data() {
    return {
      // 创建一个 Event Bus
      eventBus: new Vue(),
      // 用来切换所选的是部门还是岗位
      selectedNode: null,
    };
  },
  components: {
    workPositionJoinerAside,
    workPositionJoinerMain,
    departmentMain,
  },

  // !!!
  // provide / inject 本身不是响应式
  provide() {
    // 提供 Event Bus 给所有子组件
    return {
      eventBus: this.eventBus,
    };
  },
  created() {},
  methods: {
    selectNode(node) {
      this.selectedNode = node;
    },
  },
  mounted() {
    // 给事件总线添加切换方法
    this.$nextTick(() => {
      this.eventBus.$on("selectNode", (node) => this.selectNode(node));
    });
  },

  beforeDestroy() {},
  computed: {},
};
</script>
<style lang="sass" scoped>
.el-container
  height: 100%

.aside
  overflow: auto
  height: calc(100vh - 72px - 122px)
  border-right: solid 1px #e6e6e6
  background: #fff
  margin-top: 12px
  margin-right: 12px
</style>
