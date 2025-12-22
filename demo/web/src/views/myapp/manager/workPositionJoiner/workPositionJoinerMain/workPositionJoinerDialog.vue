<template>
  <div>
    <el-dialog
      width="80%"
      v-bind="$attrs"
      v-on="$listeners"
      @open="onOpen"
      @close="onClose"
    >
      <template v-slot:title>
        <el-row>
          <span style="
          line-height: 24px;
          font-size: 18px;
          color: #303133;"
          >流程选择</span>
          <span style="font-size: 14px">{{ title }}</span>
        </el-row>
      </template>
      <el-form
        ref="elForm"
        :model="queryParams"
        size="medium"
        label-width="100px"
        label-position="left"
      >
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item
              label-width="120px"
              :label="queryParams.source==='t_d_hgwj'?'合规文件名称：':'流程名称：'"
              prop="name"
            >
              <el-input
                v-model="queryParams.name"
                :placeholder="queryParams.source==='t_d_hgwj'?'请输入合规文件名称':'请输入流程名称'"
                clearable
                :style="{ width: '100%' }"
              >
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item
              label-width="80px"
              label="类型："
              prop="name"
            >
              <el-select v-model="queryParams.source" placeholder="请选择类别">
                <el-option
                  v-for="item in [{ value: 't_d_hgwj', label: '合规文件' }, { value: 't_d_ywlc', label: '流程图' }]"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="业务类别：" prop="sjid">
              <ywlb v-model="queryParams.ywlbid"/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="文件类别：" prop="wjlb">
              <wjlb v-model="queryParams.wjlbid"/>
            </el-form-item>
          </el-col>
          <el-col class="right-align" :span="2">
            <el-button @click="onSearch" type="primary" icon="el-icon-search"
            >查询
            </el-button
            >
          </el-col>
        </el-row>
      </el-form>
      <lee-table
        ref="leeTable"
        height="450"
        :query-params="computedQueryParams"
        v-bind="$attrs"
        v-on="$listeners"
        :select="true"
        selectionCriteria="specialSort"
        :fetch-data="getTableDataAdapter"
        @update:selection="handleSelectionUpdate"
        @select="test"
      >
        <el-table-column property="tdywlb.lbmc" label="业务类别">
        </el-table-column>
        <el-table-column property="tdwjlb.lbmc" label="文件类别">
        </el-table-column>
        <el-table-column property="name" label="文件 / 流程名称">
        </el-table-column>
        <el-table-column
          property="tdznbm.bmmc"
          label="职能部门"
        >
          <template slot-scope="scope">
            {{
              scope.row.source === 't_d_hgwj' ?
                (scope.row.znbmid ? scope.row.tdznbm.bmmc : scope.row.znbmmc)
                :
                ''
            }}
          </template>
        </el-table-column>
        <el-table-column
          property="displayDate"
          label="发文时间 / 发布时间"
          width="140"
        >
          <template v-slot="scope">
            {{ scope.row.source === 't_d_hgwj' ?scope.row.fwsj : scope.row.cjsj.substring(0, 10) }}
          </template>
        </el-table-column>
      </lee-table>
      <!--      <div slot="footer">-->
      <!--        <el-button @click="close">取消</el-button>-->
      <!--        <el-button type="primary" @click="handelConfirm">确定</el-button>-->
      <!--      </div>-->
    </el-dialog>
  </div>
</template>

<script>
import { ywlb, wjlb } from '@/components/select'
import { DeepCopyUtils } from '@/utils/LeeUtils'
import { getYwlcHgwj, saveGwlcgx, updateGwlcgx } from '@/api/app/manager/workPositionJoiner'
import leeTable from '@/components/elementUI/lee-table/index.vue'
import popover from '@/views/myapp/manager/departmentRoles/departmentRolesMain/popover.vue'
import { mapGetters } from 'vuex'

export default {
  inject: ['eventBus'],
  inheritAttrs: false,
  components: { popover, leeTable, ywlb, wjlb },
  props: {
    gwglid: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      isLoading: false,
      rawData: [],
      // 所选数据
      selection: [],
      queryParams: {
        page: 1,
        rows: 10,
        source: 't_d_hgwj',
        name: undefined,
        ywlbid:'',
        wjlbid:'',
      },
      formData: {
        ViewYwlcHgwj: []
      }
    }
  },
  computed: {
    ...mapGetters(['znbm']),
    computedQueryParams() {
      return { gwglid: this.gwglid, ...this.queryParams }
    },
    title() {
      return '（当前所选岗位：' + (this.eventBus.gwnameArr.join('--') || '') + '）' || []
    }
  },
  watch: {},
  created() {
  },
  mounted() {
  },
  methods: {
    test(_, data) {
      let formData = DeepCopyUtils.deepCopy({ ViewYwlcHgwj: data })
      formData.gwglid = this.gwglid

      updateGwlcgx(formData)
        .then((response) => {
          // this.close();
          this.$message({
            type: 'success',
            message: response['msg']
          })
          this.$emit('data-refresh')
        })
        .catch((error) => {
          this.$message({
            message: error,
            type: 'warning'
          })
        })
    },
    // 查询配适器
    async getTableDataAdapter(params) {
      try {
        const response = await getYwlcHgwj(params)
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

    // 查
    handelSearch() {
      this.$refs.leeTable.getData()
    },

    // 勾选数组
    handleSelectionUpdate(selection) {
      this.selection = selection
    },

    onInit() {
      this.$nextTick(() => {
        this.handelSearch()
      })
    },

    onOpen() {
      console.log('bus', this.eventBus.gwnameArr)
    },

    onClose() {
      this.$refs['leeTable'].clearSelection()
      this.$refs['elForm'].resetFields()
    },

    close() {
      this.$emit('update:visible', false)
      this.formData = {
        ViewYwlcHgwj: []
      }
    },

    onSearch() {
      this.handelSearch()
    },

    handelConfirm() {
      this.$refs['elForm'].validate((valid) => {
        if (!valid) return
        let formData = DeepCopyUtils.deepCopy({ ViewYwlcHgwj: this.selection })
        formData.gwglid = this.gwglid

        saveGwlcgx(formData)
          .then((response) => {
            // this.close();
            this.$message({
              message: response.msg,
              type: 'success'
            })
            this.$emit('data-refresh')
            // console.log(this.eventBus);
            // this.eventBus.$emit("refresh");
            this.close()
          })
          .catch((error) => {
            this.$message({
              message: error,
              type: 'warning'
            })
          })
      })
    }
  }
}
</script>
