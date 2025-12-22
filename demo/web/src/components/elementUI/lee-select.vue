<template>
  <div class="select-container">
    <div v-if="name" :style="{ minWidth: (name.length + 1) * 14 + 'px' }">
      {{ name + "：" }}
    </div>
    <el-tooltip
      :disabled="!tooltip || !(this.selectData && this.selectData.length>0)"
      class="item"
      effect="dark"
      :content="tooltipContent"
      placement="right"
    >
      <el-select
        ref="ref"
        class="select-option"
        v-bind="$attrs"
        v-on="$listeners"
        :value="selectData"
        @change="change"
        :placeholder="placeholder"
        :multiple="multiple"
        :allowCreate="allowCreate"
      >
        <el-option
          v-if="this.showAllOptions && this.options.length > 0"
          label='全选'
          value='SELECTALL'
          key="SELECTALL"
          @click.native='selectAll'
        >
        </el-option>
        <el-option
          v-for="item in computedOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
    </el-tooltip>
  </div>
</template>

<script>
export default {
  name: "leeSelect",
  props: {
    value: [Number, String, Array],
    name: {
      type: String,
      default: undefined,
    },
    options: {
      type: Array,
      default: () => [],
    },
    placeholder: {
      type: String,
      default: "",
    },
    showAllOptions: {
      type: Boolean,
      default: true,
    },
    multiple: {
      type: Boolean,
      default: false
    },
    tooltip: {
      type: Boolean,
      default: false
    },
    allowCreate: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      selectData: this.value,
    };
  },
  computed: {
    computedOptions() {
      return this.options;
    },
    // 提示的计算属性
    tooltipContent() {
      let str;
      if ((this.selectData && this.selectData.length > 0) && (this.options && this.options.length > 0)) {
        let option = this.options.find((option) => {
          return this.selectData === option.value
        })

        if (option !== undefined)
          str = option.label
      }
      return str
    }
  },
  watch: {
    value(newValue) {
      this.selectData = newValue
    }
  },
  methods: {
    // 是否创建过自定义下拉框选项
    createdSelectLabel() {
      return new Promise((resolve) => {
        this.$nextTick(() => {
          if (this.$refs.ref.createdLabel) {
            const label = this.$refs.ref.createdLabel;
            const selectData = this.selectData;
            let flag = label === selectData;
            this.$emit('update-created-select', {flag, label});
          }
        });
      });
    },
    selectAll() {
      if (this.multiple === true) {
        if (this.selectData[0] === "SELECTALL" && this.selectData.length < this.options.length) {
          this.selectData = []
          this.options.map((item) => {
            this.selectData.push(item.value)
          })
          this.selectData.unshift('SELECTALL')
        } else {
          // console.log("触发清空")
          this.selectData = []
        }
      }
      this.$emit("input", this.selectData);
    },
    change(val) {
      if (this.multiple === true) {
        // console.log("change triggered");
        let updatedVal = [...val];
        // console.log(updatedVal.length)
        // console.log(this.selectData.length)
        if (this.multiple === true) {
          if (updatedVal[0] !== "SELECTALL" && updatedVal.length === this.options.length) {
            // console.log("添加全选")
            updatedVal.unshift("SELECTALL")
          }
          if (updatedVal.length < this.selectData.length) {
            // console.log("去掉全选")
            updatedVal = updatedVal.filter((item) => {
              return item !== 'SELECTALL'
            })
          }
        }
        this.selectData = updatedVal;
      } else {
        this.selectData = val
      }

      this.$emit("input", this.selectData);
      if (this.allowCreate) {
        this.createdSelectLabel();
      }
    },
  },
  mounted() {
    if (this.multiple === true && this.value) {
      if (this.value[0] === "SELECTALL") {
        this.selectAll()
      }
    }
  }
};
</script>

<style lang=sass scoped>
.select-container
  display: inline-flex
  align-items: center
  font-size: 14px
  width: 100%

  & span
    min-width: 80px
    margin-right: 10px

.select-container /deep/ .el-select__tags
  white-space: nowrap
  overflow: hidden
</style>
