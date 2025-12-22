<template>
  <div>
    <el-row :gutter="20">
      <el-form
        ref="elForm"
        label-position="left"
        :model="formParams"
        :rules="rules"
        size="medium"
        label-width="100px"
      >
        <el-col :span="12">
          <el-form-item label="流程名称：" prop="lcmc">
            <Input v-model="formParams.lcmc"></Input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="业务类别：" prop="ywlbid">
            <ywlb
              v-model="formParams.ywlbid"
              :showAllOptions="false"
              :ssyw="'1'"
              :key="computedKey_ywlb"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="文件类别：" prop="wjlbid">
            <wjlb
              v-model="formParams.wjlbid"
              :showAllOptions="false"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态：" prop="lczt">
            <zt
              v-model="formParams.lczt"
              :showAllOptions="false"
              :key="computedKey_lczt"
              :sub-options="false"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <general ref="general"></general>
        </el-col>
      </el-form>
    </el-row>
    <div>
      <!-- 环节 header -->
      <el-row class="common title">
        <div class="title-content">
          <el-col :span="2">
            <div class="center">排序</div>
          </el-col>
          <el-col :span="1">
            <el-divider direction="vertical"></el-divider>
          </el-col>
          <el-col :span="14">
            <div class="center">环节名称</div>
          </el-col>
          <el-col :span="1">
            <el-divider direction="vertical"></el-divider>
          </el-col>
          <el-col :span="6">
            <div class="center">功能键</div>
          </el-col>
        </div>
      </el-row>
      <!-- 环节循环主体 -->
      <el-row
        class="common step"
        v-for="(item, i) in formParams.tdywlcHjList"
        :key="item.id"
      >
        <div class="step-content">
          <el-col :span="2">{{ item.hjpx }}</el-col>
          <el-col :span="1"
          >
            <el-divider class="step-divider" direction="vertical"></el-divider
            >
          </el-col>
          <el-col :span="14">
            <el-input v-model="item.hjmc" placeholder="请输入内容"></el-input>
          </el-col>
          <el-col :span="1"
          >
            <el-divider class="step-divider" direction="vertical"></el-divider
            >
          </el-col>
          <el-col :span="6">
            <div class="center">
              <el-button
                size="small"
                type="primary"
                icon="el-icon-caret-top"
                @click="handleMove(i, 'up')"
              ></el-button>
              <el-button
                size="small"
                type="primary"
                icon="el-icon-caret-bottom"
                @click="handleMove(i, 'down')"
              ></el-button>
              <el-button
                size="small"
                type="danger"
                icon="el-icon-delete"
                @click="handleDelete(i)"
              ></el-button>
            </div>
          </el-col>
        </div>
      </el-row>
      <!-- 增加环节 -->
      <el-collapse-transition>
        <div v-show="newButtondisabled">
          <el-row class="common step">
            <div class="step-content">
              <el-col :span="2" v-if="formParams && formParams.tdywlcHjList"
              >{{ formParams.tdywlcHjList.length + 1 }}
              </el-col>
              <el-col :span="1"
              >
                <el-divider
                  class="step-divider"
                  direction="vertical"
                ></el-divider
                >
              </el-col>
              <el-col :span="14">
                <el-input
                  v-model="newLchj.hjmc"
                  placeholder="请输入内容"
                ></el-input>
              </el-col>
              <el-col :span="1"
              >
                <el-divider
                  class="step-divider"
                  direction="vertical"
                ></el-divider
                >
              </el-col>
              <el-col :span="6">
                <div class="center">
                  <el-button
                    size="small"
                    type="primary"
                    icon="el-icon-circle-check"
                    @click="handleNewLchjCache"
                  >确认
                  </el-button
                  >
                  <el-button
                    size="small"
                    type="danger"
                    icon="el-icon-circle-close"
                    @click="handleNewOrCancelNewLchj"
                  >取消
                  </el-button
                  >
                </div>
              </el-col>
            </div>
          </el-row>
        </div>
      </el-collapse-transition>
      <!-- 增加环节按钮 -->
      <el-button
        class="add-button"
        size="small"
        type="primary"
        icon="el-icon-circle-plus-outline"
        :disabled="newButtondisabled"
        @click="handleNewOrCancelNewLchj"
        plain
      >
        增加环节
      </el-button>
    </div>
  </div>
</template>

<script>
import Input from '@/components/elementUI/el-input'
import { ywlb, wjlb, zt } from '@/components/select'
import general from './general'
import { mapGetters, mapActions } from 'vuex'

export default {
  data() {
    return {
      newLchj: {
        hjmc: '',
        show: true
      },
      rules: {
        lcmc: [
          {
            required: true,
            message: '请输入流程名称',
            trigger: 'blur'
          }
        ],
        ywlbid: [
          {
            required: true,
            message: '请选择业务类别',
            trigger: 'blur'
          }
        ],
        wjlbid: [
          {
            required: true,
            message: '请选择文件类别',
            trigger: 'blur'
          }
        ],
        lczt: [
          {
            required: true,
            message: '请选择流程状态',
            trigger: 'blur'
          }
        ]
      }
    }
  },
  components: {
    Input,
    ywlb,
    wjlb,
    zt,
    general
  },
  methods: {
    ...mapActions('flow', [
      'changeNewButtondisabled',
      'resetNewButtondisabled'
    ]),

    // 初始化数据
    initEditdata() {
      this.newLchj = {
        hjmc: '',
        hjpx: 0
      }
      this.dialogVisible = true
    },

    // 循环中的环节挪动
    handleMove(index, direction) {
      if (direction === 'up' && index > 0) {
        this.swapItems(index, index - 1)
      } else if (
        direction === 'down' &&
        index < this.formParams.tdywlcHjList.length - 1
      ) {
        this.swapItems(index, index + 1)
      }
    },

    swapItems(index1, index2) {
      const currentItem = this.formParams.tdywlcHjList[index1]
      const targetItem = this.formParams.tdywlcHjList[index2]

      // 交换位置
      this.formParams.tdywlcHjList.splice(index1, 1, targetItem)
      this.formParams.tdywlcHjList.splice(index2, 1, currentItem)

      // 交换 hjpx 数据
      const tempHjpx = currentItem.hjpx
      currentItem.hjpx = targetItem.hjpx
      targetItem.hjpx = tempHjpx
    },

    // 删除环节
    handleDelete(index) {
      const tdywlcHjList = this.formParams.tdywlcHjList
      tdywlcHjList.splice(index, 1)
      // 重新排序 hjpx 字段
      tdywlcHjList.forEach((item, i) => {
        item.hjpx = i + 1
      })
    },

    // 将新增环节缓存至tdywlcHjList数组中
    handleNewLchjCache() {
      console.log('new')
      const newLchj = {
        hjmc: this.newLchj.hjmc,
        hjpx: this.formParams.tdywlcHjList.length + 1,
        sign: 1,
        cjr: '',
        tdywlcHjfjList: this.generalFileList.length === 0 ? [] : [...this.generalFileList]
      }
      if (!newLchj.hjmc || newLchj.hjmc.length === 0) {
        console.log(newLchj)
        this.$message({
          message: '添加环节名称不能为空！',
          type: 'warning'
        })
      } else {
        console.log('校验后')
        this.formParams.tdywlcHjList.push(newLchj)
        this.handleNewOrCancelNewLchj()
      }
    },

    // 打开或取消增加环节
    handleNewOrCancelNewLchj() {
      this.newLchj.hjmc = ''
      this.changeNewButtondisabled()
    },

    // 表单校验的回调
    validate(callback) {
      this.$refs.elForm.validate((valid) => {
        if (valid) {
          callback(null)
        } else {
          callback(new Error('Validation failed'))
        }
      })
    }
  },
  computed: {
    ...mapGetters(['flowFormParams', 'newButtondisabled', 'generalFileList']),
    formParams() {
      const flowFormParams = this.flowFormParams
      if (flowFormParams.length >= 1) {
        flowFormParams.tdywlcHjList.sort(
          (a, b) => parseInt(a.hjpx) - parseInt(b.hjpx)
        )
      }
      return flowFormParams
    },
    // 生成唯一key
    computedKey_ywlb() {
      return 'ywlb_' + this.formParams.ywlbid || 'defaultKey' // 使用默认的键值作为 key，当 ywlbid 为空时
    },
    computedKey_lczt() {
      return 'lczt_' + this.formParams.lczt || 'defaultKey' //
    }
  }
}
</script>

<style lang="sass" scoped>
.input-container
  display: flex
  align-items: center

  > *
    margin-right: 10px

  > *:last-child
    margin-right: 0

.el-button--small
  font-size: 14px

.center
  display: inline-block

.el-divider
  background-color: #606266

.common
  border: 1px solid #DCDFE6
  border-radius: 3px
  transition: .2s
  text-align: center

.title
  padding: 14px 20px
  background: #f4f4f4
  margin-top: 20px

.step
  padding: 10px 20px
  line-height: 33px
  margin-top: 10px

  &-divider
    background: #DCDFE6

  &-button-group
    margin-right: 16px

.add-button
  margin-top: 10px
  display: inline-block
  position: relative
  left: 50%
  transform: translateX(-50%)
</style>
