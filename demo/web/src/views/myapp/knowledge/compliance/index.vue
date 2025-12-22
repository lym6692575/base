<!-- 合规文件 -->
<template>
  <div>
    <el-form
      ref="elForm"
      label-position="left"
      :model="queryParams"
      size="medium"
      label-width="80px"
      class="lee-form"
    >
      <el-row :gutter="20">
        <el-col :span="7">
          <el-form-item label="文件名称:" prop="wjmc">
            <elInput v-model="queryParams.wjmc"/>
          </el-form-item>
        </el-col>
        <div v-if="type!=='template'">
          <el-col :span="7" >
            <el-form-item label="职能部门:" prop="znbmid">
              <znbm :level=level :bmName="bmName" v-model="queryParams.znbmid"/>
            </el-form-item>
          </el-col>
          <el-col :span="10" v-show="type!=='zdxg'">
            <el-form-item label="发文时间:" prop="date">
              <el-date-picker
                type="daterange"
                v-model="date"
                format="yyyy-MM-dd"
                value-format="yyyy-MM-dd"
                :style="{ width: '100%' }"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                range-separator="至"
                unlink-panels
                :clearable="true"
                @change="handleDataChange"
              ></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="7" v-show="type!=='zdxg'">
            <el-form-item label="文件类别:" prop="wjlbid">
              <wjlb v-model="queryParams.wjlbid"/>
            </el-form-item>
          </el-col>
          <el-col :span="7">
            <el-form-item label="业务类别:" prop="ywlbid">
              <ywlb
                v-model="queryParams.ywlb"
                :multiple="true"
                collapse-tags
              />
            </el-form-item>
          </el-col>
        </div>
        <el-col :span="8" v-show="type!=='zdxg'">
          <el-form-item label="状态:" prop="zt">
            <zt v-model="queryParams.zt"/>
          </el-form-item>
        </el-col>

        <el-col :span="type!=='template' ? 2 :  9" class="right-align">
          <el-button @click="onSearch" type="primary" icon="el-icon-search"
          >查询
          </el-button
          >
        </el-col>
      </el-row>
    </el-form>
    <compTable ref="tableRef" v-model="queryParams" :type="type"></compTable>
  </div>
</template>

<script>
import elInput from '@/components/elementUI/el-input'
import {wjlb, ywlb, xldj, znbm, zt} from '@/components/select'
import compTable from './compTable.vue'
import {apiUtils, dateUtils, DeepCopyUtils} from '@/utils/LeeUtils'

export default {
  name: 'compliance',
  components: {
    elInput,
    wjlb,
    ywlb,
    xldj,
    znbm,
    zt,

    compTable
  },
  props: {
    type: {
      type: String,
    },
  },
  data() {
    return {
      queryParams: {
        page: '1',
        rows: '10',
        wjmc: undefined,
        wjlbid: 'SELECTALL',
        ywlb: ['SELECTALL'],
        xldjid: 'SELECTALL',
        znbmid: 'SELECTALL',
        zt: 'SELECTALL',
        startDate: undefined,
        endDate: undefined
      },
      date: []
    }
  },
  computed: {
    // 控制显示
    bmName() {
      if (this.queryParams.wjlbid === '8b6adfc4898216610189827d0b8e0000') {
        return []
      } else {
        return ['省机关', '直属机构']
      }
    },
    // 控制显示
    level() {
      if (this.queryParams.wjlbid === '8b6adfc4898216610189827d0b8e0000') {
        return 'all'
      } else {
        return '2'
      }
    }
  },
  methods: {
    // 查询
    onSearch() {
      this.$refs.tableRef.handelSearch()
    },

    handleDataChange(val) {
      if (val !== null) {
        this.queryParams.startDate = val[0]
        this.queryParams.endDate = val[1]
      } else {
        // 如果清空了日期选择, 将查询参数赋值为空
        this.queryParams.startDate = ""
        this.queryParams.endDate = ""
      }
    },

    // 获取已选数据
    getSelectedData(){
      return this.$refs.tableRef.getSelectedData()
    },

    // 清除选择
    clearSelection(){
      this.$refs.tableRef.clearSelection()
    }
  },

  mounted() {
    // 检查是否有查询参数，并且查询参数是否有效（不为空或undefined）
    if (this.$route.query && Object.keys(this.$route.query).length > 0) {

      // 遍历所有查询参数并将它们赋值给queryParams
      for (let key in this.$route.query) {
        console.log("key", key)
        if (
          this.$route.query[key] !== undefined &&
          this.$route.query[key] !== null
        ) {
          // 特殊处理日期
          if (key === 'date') {
            this.date = this.$route.query[key]
            this.queryParams.startDate = this.$route.query[key][0]
            this.queryParams.endDate = this.$route.query[key][1]
          } else {
            console.log("2")
            this.queryParams[key] = this.$route.query[key]
          }
        }
      }
    }
    // 特殊模式下添加查询条件
    if (this.type === 'template') {
      this.queryParams.zt = "0"
    }
    this.onSearch()
  },
  beforeDestroy() {
  }
}
</script>

<style scoped>
.parent-container {
  display: flex;
  flex-direction: row;
}

.lee-form >>> .el-form-item {
  margin-bottom: 14px;
}
</style>
