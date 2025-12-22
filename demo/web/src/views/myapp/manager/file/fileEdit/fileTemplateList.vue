<template>
  <div>
    <el-dialog
      v-bind="$attrs"
      v-on="$listeners"
      :append-to-body="true"
      title="选择文件"
      width="60%"
      @close="onClose"
      @open="onOpen"
    >
      <compliance ref="refComp" type="template"></compliance>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="handleConfirm">选 择</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import compliance from '@/views/myapp/knowledge/compliance/index.vue'

export default {
  props: {
    value: {
      type: Array,
      default: () => [],
    },
    tdhgwjFj: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      currentRow: null, // 当前点击的行
    };
  },
  components: {
    compliance,
  },
  methods: {
    // 获取已选数据
    handleConfirm() {
      this.$emit("update-template-date", this.$refs.refComp.getSelectedData())
    },
    onOpen(){
      this.$refs.refComp.onSearch()
    },
    onClose(){
      this.$refs.refComp.clearSelection()
    }
  }
};
</script>

<style lang="sass" scoped>
</style>
