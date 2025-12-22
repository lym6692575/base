<template>
  <div>
    <el-row>
      <el-col :span="24">
        <div v-show="!isBmzzHidden">
          <h5 class="title">
            <i class="el-icon-s-order"></i>
            <span class="mr20">部门职责：</span>
            <div class="title-border"></div>
          </h5>
          <el-input
            type="textarea"
            :autosize="{ minRows: 4, maxRows: 40 }"
            placeholder="请输入内容"
            v-model="bmzz"
            readonly
          >
          </el-input>
        </div>
        <h5 class="title">
          <i class="el-icon-s-order"></i>
          <span class="mr20">法律法规：</span>
          <div class="title-border"></div>
        </h5>
        <bmzz-hgwj-table ref="bmzzHgwjTableRef"></bmzz-hgwj-table>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import { getBmzz, getHgwjListByBmid } from '@/api/app/knowledge/responsibility'
import bmzzHgwjTable from './bmzzHgwjTable.vue'

export default {
  // 调用事件总线
  inject: ['eventBus'],
  data() {
    return {
      dialogVisible: false,
      bmzz: '',
      isBmzzHidden:false,
      hgwjList: []
    }
  },
  components: { bmzzHgwjTable },
  methods: {
    // 查询
    onSearchBmzz(znbmid) {
      if (znbmid.length > 0) {
        znbmid = znbmid[znbmid.length - 1]
      } else {
        znbmid = znbmid[0]
      }

      getBmzz(znbmid).then((res) => {
        console.log(res)
        this.bmzz = res.data.bmzz
        this.isBmzzHidden = res.data.isBmzzHidden
      })

      this.$nextTick(() => {
        this.$refs.bmzzHgwjTableRef.handleGetTableData(znbmid)
      })
    }
  },
  // 添加生命周期函数
  mounted() {
    // 给事件总线添加搜索方法
    this.$nextTick(() => {
      this.eventBus.$on('onSearchBmzz', (znbmid) => this.onSearchBmzz(znbmid))
    })
  },
  beforeDestroy() {
  },
  computed: {}
}
</script>

<style lang="sass" scoped>
.title
  color: #515A6e

  &-border
    border-bottom: 1px dashed #909399
    margin-top: 20px
    opacity: 0.4
</style>
