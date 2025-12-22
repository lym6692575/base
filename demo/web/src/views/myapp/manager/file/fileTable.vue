<template>
  <div>
    <lee-table
      ref="leeTable"
      :fetch-data="getTableDataAdapter"
      :query-params="value"
      v-bind="$attrs"
      v-on="$listeners"
      :initialSize="20"
      :pageSizes="[20, 50, 100, 200]"
      style="width: 100%"
      height="470px"
    >
      <el-table-column property="wjmc" label="文件名称" width="400">
        <template slot-scope="scope">
          <el-link type="primary" @click="handleEdit(scope.$index, scope.row)">
            {{ scope.row.wjmc }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column property="wjbh" label="文件编号" width="200"/>
      <el-table-column property="wjlbmc" label="文件类别" width="180"/>
      <el-table-column property="fwsj" label="发文时间" width="180"/>
      <el-table-column property="bmmc" label="职能部门" width="320">
      </el-table-column>
      <el-table-column property="xldjmc" label="效力等级"/>
      <el-table-column property="ztmc" label="状态">
        <template slot-scope="scope">
          {{ scope.row.zt == 1 ? '新增' : (scope.row.zt == 2 ? '修订' : '废止') }}
        </template>
      </el-table-column>
      <el-table-column property="lct" label="流程图" width="180">
        <template slot-scope="scope">
          <el-link
            type="primary"
            @click="handleOpenFlowBoxDialog(scope.$index, scope.row)"
            :disabled="scope.row.tdywlc && scope.row.tdywlc.length === 0"
          >
            {{ scope.row.tdywlc && scope.row.tdywlc.length > 0 ? '流程图' : '未配置流程图' }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        width="240"
        fixed="right"
      >
        <template v-slot="scope">
          <el-tooltip
            content="该合规文件没有上传附件，无法批量下载"
            placement="top"
            :disabled="scope.row.tdhgwjFj.length!==0"
          >
            <el-button
              class="mr20"
              size="mini"
              icon="el-icon-folder"
              :disabled="scope.row.tdhgwjFj.length===0"
              @click="handleDownloadZip(scope.$index, scope.row)"
            >
              批量下载
            </el-button>
          </el-tooltip>
          <el-popconfirm
            title="确定删除该文件？"
            @confirm="handleDelete(scope.$index, scope.row)"
          >
            <el-button slot="reference" size="mini" icon="el-icon-delete"
            >删除
            </el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </lee-table>
    <flow-box ref="flowboxRef"></flow-box>
    <file-edit
      ref="editRef"
      :postType="'update'"
      :switchDisabled="false"
      :titleName="'编辑合规文件'"
      @data-saved="handelSearch(value)"
    ></file-edit>
  </div>
</template>

<script>
import {getTableData, deleteTDhgwj} from '@/api/app/manager/file'
import {donwloadZip} from "@/api/app/knowledge/compliance";
import flowBox from '@/views/components/flowBox'
import {mapGetters} from 'vuex'
import fileEdit from './fileEdit'
import leeTable from '@/components/elementUI/lee-table'
import {evn} from "@/utils/ProjectUtils";

export default {
  props: {
    value: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      rawData: [],
      currentRow: null,
      // 初始搜索条件
      page: 1,
      rows: 10,
      isLoading: false
    }
  },
  components: {
    leeTable,
    fileEdit,
    flowBox
  },
  methods: {
    // 查询配适器
    async getTableDataAdapter(params) {
      try {
        const response = await getTableData(params)
        console.log(response)
        return {
          data: response.data.content, // 适应到你的数据结构
          total: response.data.totalElements // 适应到你的数据结构
        }
      } catch (error) {
        this.$message({
          message: error,
          type: 'warning'
        })
        throw error
      }
    },

    renderHeader(h) {
      return(
        <div>
          操作
          <el-button style='float:right' size='mini' type='text'>1</el-button>
        </div>
      )
    },
    // 查
    handelSearch() {
      this.$refs.leeTable.getData()
    },

    // index方法
    indexMethods(index) {
      return (this.page - 1) * this.rows + index + 1
    },

    mergeMethods(data) {
      // 检查数据是否存在和符合预期的结构
      if (!data || !data.tdywlc || !Array.isArray(data.tdywlc)) {
        console.error('Invalid data format')
        return
      }

      // 遍历每一个tdywlc对象
      data.tdywlc.forEach((tdywlcItem) => {
        // 遍历tdywlcItem的tdywlcHjList数组
        tdywlcItem.tdywlcHjList.forEach((hjList) => {
          // 如果tdywlcHjfjList字段不存在，则初始化为一个空数组
          if (!Array.isArray(hjList.tdywlcHjfjList)) {
            hjList.tdywlcHjfjList = []
          }
          // 合并tdywlcGefj数组到tdywlcHjfjList
          if (Array.isArray(tdywlcItem.tdywlcGefj)) {
            hjList.tdywlcHjfjList.push(...tdywlcItem.tdywlcGefj)
          }
        })
      })
    },

    handleOpenFlowBoxDialog(index, row) {
      let formData = JSON.parse(JSON.stringify(row))
      this.$refs.flowboxRef.openDialog(index, formData)
    },

    // 点击文件名弹出编辑对话框
    handleEdit(index, row) {
      this.$refs.editRef.openDialog(index, row)
    },

    // 删除合规文件并刷新表格数据
    handleDelete(_, row) {
      deleteTDhgwj(row.id).then((res) => {
        if (res.result) {
          this.$message({
            message: res.msg,
            type: 'success'
          })
          // 刷新数据
          this.handelSearch(this.value)
        }
      })
    },

    // 批量下载
    handleDownloadZip(_, row) {
      let path = process.env.VUE_APP_BASE_API
      window.location.href = `${path}/knowledge/download-zip/${row.id}`;
    },
  },
  mounted() {
  },
  computed: {
    ...mapGetters(['znbm']),
    // 生成key值
    generatedKey() {
      const timestamp = new Date().getTime()
      const random = Math.floor(Math.random() * 1000) // 生成一个随机数，范围为0到999
      return timestamp + '_' + random
    }
  }
}
</script>

<style lang="sass" scoped>
.custom-table
  width: 100%
  // 调整表格宽度，可根据需要进行调整
  .el-table__body-wrapper
    overflow: hidden !important
// 隐藏表格内容溢出

.custom-pagination
  margin-top: 20px
  // 调整分页器的上边距，可根据需要进行调整
  display: flex
  justify-content: flex-end
// 将分页器放置在右侧

.link
  color: #409eff
  text-decoration: none

  &:hover
    text-decoration: underline
</style>
