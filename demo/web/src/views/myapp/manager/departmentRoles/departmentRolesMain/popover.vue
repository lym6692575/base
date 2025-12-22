<template>
  <el-popover
    placement="bottom"
    width="600"
    trigger="click"
    ref="popover"
    @show="onShow"
    :popper-options="{ boundariesElement: 'viewport', removeOnDestroy: true }"
  >
    <el-button type="text" slot="reference">
      {{
        actionType == 'edit' ?
          setLabelForCell != '' ? setLabelForCell : '点击添加关联'
          :
          setLabelForCell != '' ? setLabelForCell : null
      }}
    </el-button>
    <el-form
      ref="elForm"
      :model="localParams"
      size="medium"
      label-width="90px"
      label-position="left"
      class="lee-form"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="部门名称：" prop="bmmc">
            <el-input
              v-model="localParams.bmmc"
              placeholder="请输入部门名称"
              clearable
              :style="{ width: '100%' }"
              @input="onSearch"
            >
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <el-table
      ref="singleTable"
      border
      style="width: 100%"
      max-height="500"
      :data="tableData"
      class="mb10 lee-table"
      v-loading="isLoading"
      @select="handleSelectionChange"
      @select-all="handleSelectionChange"
      :header-cell-style="{ 'text-align': 'center' }"
      :cell-style="{ 'text-align': 'center' }"
    >
      <el-table-column
        v-if="actionType != 'view'"
        label="选择"
        type="selection"
        min-width="10%"
      >
      </el-table-column>
      <el-table-column
        label="序号"
        type="index"
        min-width="5%"
      ></el-table-column>
      <el-table-column property="sjbmmc" label="所属上级" min-width="15%">
      </el-table-column>
      <el-table-column
        property="bmmc"
        label="部门名称"
        min-width="15%"
      ></el-table-column>
    </el-table>
    <!-- <el-pagination
      class="right-align mt10"
      v-if="paginationtotal > 0"
      background
      layout="prev, pager, next"
      :total="paginationtotal"
      :page-sizes="[5, 10, 15, 20]"
      @current-change="handleCurrentChange"
    >
    </el-pagination> -->
    <!-- <el-button @click="close">取消</el-button> -->
    <el-col v-if="actionType != 'view'" class="mt10 right-align" :span="24">
      <!-- <el-button type="primary" @click="handleSelectionAllChange"
        >全选</el-button
      > -->
      <el-button type="primary" @click="handelConfirm">保存</el-button>
    </el-col>
  </el-popover>
</template>

<script>
import { mapGetters } from 'vuex'
import { ywlb } from '@/components/select'
import { DeepCopyUtils, searchUtils } from '@/utils/LeeUtils'
import {
  getZnbmList,
  setBmzzgxByHgwjid
} from '@/api/app/manager/departmentRoles'

export default {
  inject: ['eventBus'],
  inheritAttrs: false,
  components: { ywlb },
  props: {
    // 默认选中的职能部门
    znbmList: {
      type: Array,
      default: () => []
    },
    // 合规文件id
    hgwjid: {
      type: String,
      default: ''
    },
    // 操作类别
    actionType: {
      type: String,
      default: 'view' // 外部表格浏览
    }
  },
  data() {
    return {
      allZnbmList: [], // 全部职能部门
      selectedZnbmList: [], // 选中的部门
      isLoading: false,
      rawData: this.znbmList,

      // 本地备份状态
      localParams: {
        bmmc: undefined
      },

      // 模糊查询参数
      queryParams: {
        bmmc: undefined
      },

      bmmc: undefined,

      // 表单
      formData: {
        hgwjid: this.hgwjid, // 合规文件id
        znbm: [] // 职能部门
      },

      page: 1,
      rows: 10,
      // 分页器的总数
      paginationtotal: null
    }
  },
  computed: {
    ...mapGetters(['znbm']),
    // 这个没写好，应该在外面拿到znbm的全部数据的，但是懒得改了
    setLabelForCell() {
      const arr = this.znbmList
      const znbmLength = this.znbm.data.length
      let result = ''

      if (arr && arr.length > 0) {
        if (arr.length === znbmLength) {
          return '全部'
        }

        for (let i = 0; i < Math.min(arr.length, 3); i++) {
          result += arr[i].bmmc
          if (i < arr.length - 1) result += '、'
        }

        if (arr.length > 3) {
          result += '...'
        }
      }

      return result
    },
    // 表数据处理
    tableData() {
      if (this.rawData) {
        let tableData = []

        // 判断打开类型
        if (this.actionType == 'view') {
          tableData = DeepCopyUtils.deepCopy(this.selectedZnbmList)
        } else {
          tableData = DeepCopyUtils.deepCopy(this.allZnbmList)
        }

        // 如果需要处理数据可以在这里处理
        tableData = tableData.map((item, index) => {
          item.is = false
          item.index = index
          return {
            ...item
          }
        })

        const queryParams = this.queryParams

        // 判断是否模糊查询
        // 文件名称
        if (queryParams.bmmc != null) {
          tableData = searchUtils.fuzzySearch(
            tableData,
            queryParams.bmmc,
            'bmmc'
          )
        }

        // 控制分页
        // const page = this.page;
        // const rows = this.rows;

        // const switcher = {
        //   pageStart: (page - 1) * rows,
        //   pageEnd: page * rows,
        // };

        // this.isLoading = false;
        // tableData = tableData.slice(switcher.pageStart, switcher.pageEnd);
        return tableData
      } else {
        return [] // 或其他默认值
      }
    }
  },
  watch: {},
  created() {
  },
  mounted() {
  },
  methods: {
    onShow() {
      this.selectedZnbmList = DeepCopyUtils.deepCopy(this.znbmList)
      this.handleGetTableData()
      // if (this.actionType != "view") {
      //   this.handleGetTableData();
      // }
    },

    onSearch() {
      // 触发计算属性实现伪查询
      this.queryParams = DeepCopyUtils.deepCopy(this.localParams)
      // 重新选中
      this.$nextTick(() => {
        this.executeToggleSelection()
      })
    },

    handelConfirm() {
      this.$refs['elForm'].validate((valid) => {
        if (!valid) return
        let formData = DeepCopyUtils.deepCopy(this.formData)
        formData.znbm = this.selectedZnbmList
        setBmzzgxByHgwjid(formData)
          .then((response) => {
            // this.close();
            this.$message({
              message: response.msg,
              type: 'success'
            })
            this.$emit('data-refresh')
            // console.log(this.eventBus);
            // this.eventBus.$emit("refresh");
          })
          .catch((error) => {
            this.$message({
              message: error,
              type: 'warning'
            })
          })
      })
    },

    // 单选逻辑
    handleSelectionChange(val, row) {
      this.selectedZnbmList = val
    },

    // 全选逻辑
    handleSelectionAllChange() {
      this.selectedZnbmList = []
      this.selectedZnbmList = this.allZnbmList
    },

    // 勾选标记数据
    executeToggleSelection() {
      const rows = this.tableData
      const selectedZnbmList = this.selectedZnbmList
      const selectedZnbmListIds = selectedZnbmList.map((item) => item.id)
      rows.forEach((item) => {
        if (selectedZnbmListIds.includes(item.id)) {
          // 判断item.id是否存在于selectedZnbmListIds中
          this.$refs.singleTable.toggleRowSelection(item)
        }
      })
    },

    // 获取数据 && 数据初始化
    handleGetTableData() {
      this.isLoading = true
      // const selectedTdywlc = this.fileFormData.tdywlc;
      // console.log("selectedTdywlc", selectedTdywlc);
      // const selectedId = selectedTdywlc.map((item) => {
      //   return { id: item.id };
      // });

      getZnbmList().then((res) => {
        this.allZnbmList = res.data
        this.paginationtotal = res.data.length
        this.isLoading = false

        this.$nextTick(() => {
          if (this.$refs.popover) {
            this.$refs.popover.updatePopper() // 修复偶尔出现的定位bug
            this.executeToggleSelection() // 标记逻辑
          }
        })
      })
    },

    // 分页切换
    handleCurrentChange(val) {
      this.page = val
      // 标记
      this.$nextTick(() => {
        this.executeToggleSelection()
      })
    }
  }
}
</script>
<style lang="sass" scoped>
.lee-form >>> .el-form-item
  margin-bottom: 10px
</style>
