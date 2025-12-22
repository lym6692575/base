<template>
  <div>
    <!--    {{queryParams}}-->
    <el-form
      ref="elForm"
      label-position="left"
      :model="queryParams"
      size="medium"
      label-width="80px"
    >
      <el-row :gutter="20">
        <el-col :span="6">
          <el-form-item label="文件名称:" prop="wjmc">
            <el-input
              v-model="queryParams.wjmc"
              placeholder="请输入文件名称"
              clearable
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="业务类别:" prop="ywlbid">
            <ywlb
              v-model="queryParams.ywlb"
              :multiple="true"
              collapse-tags
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="文件类别:" prop="wjlbid">
            <wjlb v-model="queryParams.wjlbid"/>
          </el-form-item>
        </el-col>
        <!-- <el-col :span="3">
          <el-form-item label="效力等级:" prop="xldjid">
            <xldj v-model="queryParams.xldjid" />
          </el-form-item>
        </el-col> -->
        <el-col :span="6">
          <el-form-item label="职能部门:" prop="znbmid">
            <znbm v-model="queryParams.znbmid"/>
            <!-- <sjid v-model="queryParams.znbmid" :checkStrictly="true"></sjid> -->
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="状态:" prop="zt">
            <zt v-model="queryParams.zt"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
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
        <el-col :span="6" class="right-align">
          <el-button @click="onSearch" type="primary" icon="el-icon-search"
          >查询
          </el-button
          >
          <el-button @click="onAdd" type="primary" icon="el-icon-circle-plus-outline"
          >新增
          </el-button
          >
        </el-col>
      </el-row>
    </el-form>
    <fileTable
      class="table"
      ref="tableRef"
      v-model="queryParams"
    ></fileTable>
    <file-edit
      ref="addRef"
      :postType="'save'"
      @data-saved="onSearch()"
    ></file-edit>
  </div>
</template>

<script>
import {ywlb, wjlb, xldj, znbm, zt} from '@/components/select'
import sjid from '@/views/components/cascader/sjid.vue'
import {dateUtils, DeepCopyUtils} from '@/utils/LeeUtils'
import fileTable from './fileTable.vue'
import fileEdit from './fileEdit'

export default {
  name: 'file',
  data() {
    return {
      message: '文件管理',
      queryParams: {
        page: '1',
        rows: '10',
        wjmc: undefined,
        ywlb: ['SELECTALL'],
        wjlbid: 'SELECTALL',
        xldjid: 'SELECTALL',
        znbmid: 'SELECTALL',
        zt: 'SELECTALL',
        startDate: undefined,
        endDate: undefined
      },
      date: []
    }
  },
  components: {
    ywlb,
    wjlb,
    xldj,
    znbm,
    sjid,
    zt,

    fileTable,
    fileEdit
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

    // 添加
    onAdd() {
      this.$refs.addRef.dialogVisible = true
    }
  },
  // 添加生命周期函数
  mounted() {
  },
  beforeDestroy() {
  },
  computed: {}
}
</script>

<style scoped>
.container {
  width: 100%;
}

.parent-container {
  display: flex;
  flex-direction: row;
}

.zt,
.search {
  margin-left: 10px;
}

.table {
  margin-top: 10px;
}
</style>
