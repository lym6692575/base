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
      placeholder="请选择所属部门"
      clearable
    ></el-cascader>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import { getDepartmentTree } from "@/api/app/components/sjid";
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
    // 如果type==='ydhg'隐藏后端isHiddenForPageYdhgxx字段为true的内容
    type:{
      type:String,
      default:""
    }
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

    // 递归筛选树结构
    filterActiveNodes(nodes) {
      // 定义一个内部递归函数，以避免this上下文问题
      const filterNodes = (node) => {
        // 检查当前节点是否active
        if (node["isHiddenForPageYdhgxx"] !== true) {
          // 创建一个新节点，包含当前节点的所有属性，但只包含active为true的子节点
          const newNode = { ...node };
          if (node.children && node.children.length) {
            newNode.children = node.children
              .map(childNode => filterNodes(childNode)) // 这里使用局部函数进行递归调用
              .filter(child => child !== null); // 过滤掉null值
          }
          return newNode;
        } else {
          // 如果当前节点不active，返回null
          return null;
        }
      };

      // 如果传入的是数组，对数组中的每个节点进行处理
      return nodes.map(node => filterNodes(node)).filter(node => node !== null);
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
    getDepartmentTree().then((res) => {
      console.log(res);
      this.departmentTree = res.data;
    });
  },
  beforeDestroy() {},
  computed: {
    // ...mapGetters(["znbmTree"]),
    computedDepartmentTree() {
      let baned = this.markedSsbmids;
      let res = JSON.parse(JSON.stringify(this.departmentTree));
      console.log("res", res);
      if (baned.length > 0) {
        this.addProperty(res, baned, "disabled", true);
      }
      if(this.type==='ydhg'){
        res = this.filterActiveNodes(res)
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
