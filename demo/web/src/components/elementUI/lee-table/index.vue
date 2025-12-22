<template>
  <div>
    <!--    <h1>oldMultipleSelection</h1>-->
    <!--    {{ oldMultipleSelection }}-->
    <!--    <h1>internalData</h1>-->
    <!--    {{internalData}}-->
    <el-table
      ref="elTable"
      :data="internalData"
      v-bind="$attrs"
      v-on="$listeners"
      v-loading="loading"
      :style="combinedTableStyle"
      row-key="id"
      @selection-change="handleSelectionChange"
      border
      :height="height!==null ? height : (windowHeight-390) + 'px'"
      :header-cell-style="{
        'text-align': 'center',
      }"
      :row-style="{
        height: '0',
        'text-align': 'center',
      }"
      :cell-style="{
        padding: '6px',
        'text-align': 'center',
      }"
      class="lee-table"
      :class="{ 'hide-select-all': singleSelect }"
    >
      <el-table-column
        v-if="select"
        :reserve-selection="true"
        type="selection"
        width="55"
      >
      </el-table-column>
      <el-table-column
        v-if="index"
        fixed
        type="index"
        width="60"
        label="序号"
        :index="indexMethods"
        class="lee-flex-center"
      />
      <!-- 插槽用于自定义列 -->
      <slot></slot>
    </el-table>

    <!-- Pagination -->
    <el-pagination
      class="lee-right-align lee-mt20"
      :current-page="Number(currentPage)"
      :page-sizes=pageSizes
      :page-size="initialSize"
      :total="totalElements"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    >
    </el-pagination>
    <!--    <pagination-->
    <!--      :current-page="Number(currentPage)"-->
    <!--      :page-sizes=pageSizes-->
    <!--      :total="totalElements">-->
    <!--      :getData="getData()"-->
    <!--    </pagination>-->
  </div>
</template>

<script>
import pagination from './pagination/index.vue'

export default {
  components: {
    pagination
  },
  computed: {
    combinedTableStyle() {
      return {
        ...this.tableStyle
      }
    }
  },
  props: {
    // 查询方法
    fetchData: {
      type: Function,
      required: true
    },

    // 表格样式
    tableStyle: {
      type: Object,
      default: () => ({})
    },

    // 高度(如果给出定高自适应高度无效)
    height: {
      type: String,
      default: null
    },

    // 查询参数
    queryParams: {
      type: Object,
      default: () => ({})
    },

    // 分页器页数
    initialPage: {
      type: Number,
      default: 1
    },

    // 分页器单页数量
    initialSize: {
      type: Number,
      default: 10
    },

    // 序号
    index: {
      type: Boolean,
      default: true
    },

    // 是否打开选择器
    select: {
      type: Boolean,
      default: false
    },

    // 是否开启单选
    singleSelect: {
      type: Boolean,
      default: false
    },

    // 选择器标识
    selectionCriteria: {
      type: String,
      default: undefined
    },

    // 分页器sizes
    pageSizes: {
      type: Array,
      default: () => [5, 10, 15, 20]
    },

    // 是否自动获取数据
    autoLoading: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      // 用来保存窗口高度
      windowHeight: 0,
      // 加载状态
      loading: false,
      // 页面数据
      internalData: [],
      // 总数居数量需要后端接口返回
      totalElements: 0,
      // 分页
      currentPage: this.initialPage,
      // 页显示
      currentSize: this.initialSize,
      // 备份的查询参数
      oldQueryParams: {},
      // 选中数据
      multipleSelection: [],
      // 选中数据的id
      multipleSelectionId: [],
      // 备份的选中数据
      oldMultipleSelection: []
    }
  },
  watch: {
    // queryParams: {
    //   deep: true,
    //   handler() {
    //     this.getData(); // 当 queryParams 变化时重新获取数据
    //   },
    // },
  },
  methods: {
    // 获取表格数据
    async getData() {
      this.loading = true

      // if (this.multipleSelection.length > 0) {
      //   this.multipleSelection.forEach(item => {
      //     if (this.oldMultipleSelection.includes(item.id)) {
      //       console.log(item + ' is in both arrays');
      //     } else {
      //       console.log(item + ' is only in array1');
      //       this.oldMultipleSelection.push(item.id)
      //     }
      //   });
      // }
      console.log('get',this.queryParams)
      const newQueryParams = JSON.stringify(this.queryParams)

      // 判断查询条件是否需要将页数归一
      // 如果oldQueryParams不是空对象，并且与newQueryParams不同
      if (this.oldQueryParams && this.oldQueryParams !== newQueryParams) {
        this.currentPage = 1 // 页数回到1
        this.$refs.elTable && this.$refs.elTable.clearSelection() // 如果修改了查询条件把以选择的数据清除
      }

      // 保存旧的查询参数
      this.oldQueryParams = newQueryParams

      const {data, total} = await this.fetchData({
        ...this.queryParams,
        page: this.currentPage,
        rows: this.currentSize
      })
      this.internalData = data

      this.$nextTick(() => {
        // 选择器
        this.initAutoSelecter()
        // 避免加载数据时造成的错行样式bug
        this.$refs.elTable.doLayout()
      })

      this.totalElements = total
      this.loading = false
    },

    // 用于勾选数据库标记勾选的数据
    initAutoSelecter() {
      // selectionCriteria存在执行自动标记
      if (this.selectionCriteria !== undefined) {
        // console.log(this.selectionCriteria)
        // 首先，根据数据库的标记来设置选中的行
        this.internalData.forEach(row => {
          // console.log(this.selectionsByPage[this.currentPage])
          if (row[this.selectionCriteria] == '1') {
            this.$refs.elTable.toggleRowSelection(row, true)
          }
        })
      }
    },

    // 分页器改变页
    handleCurrentChange(page) {
      // console.log(page)
      this.currentPage = page
      this.getData()
    },

    // 分页器改变数量
    handleSizeChange(size) {
      // console.log(size)
      this.currentSize = size
      this.getData()
    },

    // 索引方法
    indexMethods(index) {
      return (this.currentPage - 1) * this.currentSize + index + 1
    },

    handleSelectionChange(val) {
      // 如果是单选模式，并且选择了多于一个的项
      if (this.singleSelect) {
        if (val.length > 1) {
          // 清除所有选择
          this.$refs.elTable.clearSelection()
          // 重新选中最后一个被选择的行
          this.$refs.elTable.toggleRowSelection(val[val.length - 1])
          // 更新 multipleSelection 数组
          val = [val[val.length - 1]]
        }
      }
      this.multipleSelection = val
      this.multipleSelectionId = val.map(item => item.id)
      this.$emit('update:selection', this.multipleSelection)
    },

    // 调用el-table的清除选择方法
    clearSelection() {
      this.$refs.elTable && this.$refs.elTable.clearSelection()
    },

    updateWindowHeight() {
      this.windowHeight = window.innerHeight
    }
  },
  mounted() {
    // console.log(window.innerHeight)
    this.updateWindowHeight()
    if (this.autoLoading) {
      this.getData()
    }
  }
}
</script>
<style lang="sass" src="./lee-table.sass"></style>
