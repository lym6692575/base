<template>
  <div>
    <el-cascader
      class="block"
      v-bind="$attrs"
      v-on="$listeners"
      v-model="selectedValue"
      :options="computedDepartmentTree"
      :props="{ label: 'name', checkStrictly: checkStrictly, value: 'id' }"
      @change="change"
      placeholder="请选择岗位"
    ></el-cascader>
  </div>
</template>

<script>
import { getTree } from "@/api/app/manager/workPositionJoiner";
export default {
  components: {},
  props: {
    value: {
      type: [Array, String],
      default: () => [],
    },
    // 是否全选
    checkStrictly: {
      type: Boolean,
      default: true,
    },
    markedSsbmids: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      selectedValue: [],
      departmentTree: [],
    };
  },
  methods: {
    change(val) {
      this.$emit("input", val);
    },
    findDefaultNode(tree) {
      // 遍历树，找到第一个type为'gwgl'且没有子节点的部门，并返回它的路径
      for (const node of tree) {
        if (
          node.type === "gwgl" &&
          (!node.children || node.children.length === 0)
        ) {
          return [node.id];
        } else if (node.children && node.children.length > 0) {
          const path = this.findDefaultNode(node.children);
          if (path) {
            return [node.id, ...path];
          }
        }
      }
      return null;
    },
  },
  watch: {
    value(newValue) {
      this.selectedValue = newValue || [];
    },
  },

  // 添加生命周期函数
  mounted() {
    this.selectedValue = this.value;
    if (this.value && this.value == null) {
      this.selectedValue = [];
    }
    getTree().then((res) => {
      // console.log("gwpicker", res);
      this.departmentTree = res.data;
      // this.selectedValue = this.findDefaultNode(this.departmentTree);
      // 通过调用 change 方法发送 input 事件,更新父组件v-model
      this.change(this.selectedValue);
      // 调用父组件搜索
      this.$emit("change");
    });
  },
  beforeDestroy() {},
  computed: {
    computedDepartmentTree() {
      let res = JSON.parse(JSON.stringify(this.departmentTree));
      function dfs(node) {
        if (node.type === "znbm" && node.children === null) {
          node.disabled = true;
        }
        if (node.children) {
          for (let i = 0; i < node.children.length; i++) {
            dfs(node.children[i]);
          }
        }
      }
      for (let i = 0; i < res.length; i++) {
        dfs(res[i]);
      }
      return res;
    },
  },
};
</script>
<style lang="sass" scoped>
.block
  width: 100%
</style>
