<template>
  <div>
    <el-cascader
      class="block"
      v-bind="$attrs"
      v-on="$listeners"
      v-model="selectedValue"
      :options="computedDepartmentTree"
      :props="{ label: 'bmmc', checkStrictly: checkStrictly, value: 'id' }"
      @change="change"
      placeholder="请选择部门"
    ></el-cascader>
  </div>
</template>

<script>
import {mapGetters} from "vuex";
import {getTree} from "@/api/app/manager/departmentRoles";

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
    addProperty(obj, ids, propertyName, propertyValue) {
      let allChildrenDisabled = true; // 初始假设所有子节点都被禁用

      if (obj instanceof Array) {
        for (let i in obj) {
          // 如果有任何一个子节点没有被禁用，那么allChildrenDisabled就应该为false
          allChildrenDisabled &= this.addProperty(
            obj[i],
            ids,
            propertyName,
            propertyValue
          );
        }
        // 对数组进行排序
        obj.sort((a, b) => {
          return (a[propertyName] === true) - (b[propertyName] === true);
        });
      } else {
        if ("children" in obj && obj["children"].length > 0) {
          allChildrenDisabled = this.addProperty(
            obj["children"],
            ids,
            propertyName,
            propertyValue
          );
        } else {
          allChildrenDisabled = !ids.includes(obj["id"]);
        }

        // 如果该节点的所有子节点都被禁用，那么该节点被禁用
        if (allChildrenDisabled) {
          obj[propertyName] = propertyValue;
        } else {
          obj[propertyName] = !propertyValue;
        }
      }

      // 返回这个节点是否被禁用
      return allChildrenDisabled;
    },

    traverseTree(node) {
      if (node === null) return;  // 当前节点为空，则直接返回
      if (node.sign === "2") {
        console.log(node.bmmc);  // 遍历当前节点
      }
      if (node.children && node.children.length) {
        for (let i = 0; i < node.children.length; i++) {
          this.traverseTree(node.children[i]);  // 递归遍历子节点
        }
      }
    }
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
      this.departmentTree = res.data;
      this.traverseTree(res.data[0])
    });
  },
  beforeDestroy() {
  },
  computed: {
    // ...mapGetters(["znbmTree"]),
    computedDepartmentTree() {
      let baned = this.markedSsbmids;
      let res = JSON.parse(JSON.stringify(this.departmentTree));
      if (baned.length > 0) {
        this.addProperty(res, baned, "disabled", true);
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
