<template>
  <div>
    <!--    currentSize:{{currentSize}}-->
    <!--    initialSize:{{initialSize}}-->
    <!-- Pagination -->
    <el-pagination
      class="lee-right-align lee-mt10"
      :current-page="Number(currentPage)"
      :page-sizes=pageSizes
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    >
    </el-pagination>
  </div>
</template>

<script>
export default {
  computed: {
    combinedTableStyle() {
      return {
        ...this.tableStyle,
      };
    }
  },
  props: {
    total: {
      type: Number,
      default: 0
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

    // 分页器sizes
    pageSizes: {
      type: Array,
      default: () => [5, 10, 15, 20]
    }
  },
  data() {
    return {
      windowHeight: 0,
      loading: false, // 加载开关
      internalData: [], // 页面数据
      totalElements: 0, // 总数居数量需要后端接口返回
      currentPage: this.initialPage, // 分页
      currentSize: this.initialSize, // 页显示
      oldQueryParams: {}, // 备份的查询参数
      multipleSelection: [] // 选中数据
    }
  },
  methods: {

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
  },
  mounted() {
  }
}
</script>
