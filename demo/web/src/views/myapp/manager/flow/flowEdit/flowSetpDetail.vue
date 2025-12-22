<template>
  <div>
    <el-empty
      v-if="flowFormParams.tdywlcHjList && flowFormParams.tdywlcHjList.length === 0"
      description="尚未配置环节设置，请添加环节"
    ></el-empty>
    <div v-else>
      <ul class="tab-list">
        <li
          v-for="(item, index) in tabList"
          :key="index"
          :class="{ active: activeTab === index }"
          @click="
            index !== 0 && index !== tabList.length - 1
              ? changeTab(index)
              : null
          "
          :style="{
            width: liWidth,
            color: getTextColor(item.hjmc),
          }"
        >
          {{ item.hjmc }}
          <span v-if="index !== tabList.length - 1" class="array"></span>
        </li>
      </ul>
<!--       {{ tabList[activeTab] }}-->
      <el-card class="tab">
        <div
          class="tab-content"
          v-for="(content, index) in tabList"
          :key="index"
          v-show="activeTab === index"
        >
          <b class="tab-content-title">{{ content.hjmc + '：' }}</b>
          <el-input
            class="input-note"
            type="textarea"
            :rows="2"
            placeholder="请输入内容"
            v-model="content.note"
            :autosize="{ minRows: '4' }"
          >
          </el-input>
          <!-- <el-form
            ref="elForm"
            label-position="left"
            size="medium"
            label-width="80px"
          > -->
          <el-row class="mt20" :gutter="20">
            <el-col :span="12">
              <!-- <el-select
                v-model="value"
                filterable
                clearable
                placeholder="请选择"
                class="p5_input"
                allow-create
                default-first-option
              >
                <el-option
                  v-for="(item, key) in options"
                  :key="key"
                  :label="item"
                  :value="String(item)"
                />
              </el-select> -->
              <znbm
                name="执行部门"
                :showAllOptions="false"
                v-model="content.zxbm"
                multiple
                filterable
                clearable
                placeholder="请选择执行部门"
                class="p5_input"
                allow-create
              >
              </znbm>
            </el-col>
            <el-col :span="12">
              <gw
                name="执行岗位"
                ref="refGw"
                :showAllOptions="false"
                filterable
                allow-create
                v-model="content.zxgwmc?content.zxgwmc:content.zxgw"
                @change="handleChangeGw"
              >
              </gw>
<!--              <Input class="zxgw" name="执行岗位" v-model="content.zxgw"/>-->
            </el-col>
          </el-row>
          <!-- </el-form> -->
          <div class="fileUpload-container">
            <flow-file-upload
              v-if="index !== 0 && index !== tabList.length - 1"
              :key="content.id"
              :index="index - 1"
              @is-editing="(event) => handleChildEditingStatus(event, index)"
            />
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>


<script>
import Input from '@/components/elementUI/el-input'
import { fjlx, znbm, gw } from '@/components/select'
import flowFileUpload from './flowFileUpload.vue'
import { mapGetters, mapActions } from 'vuex'

export default {
  data() {
    return {
      activeTab: 1, // 默认激活的Tab索引
      initList: [{ hjmc: '开始' }, { hjmc: '形成正文发布' }],
      childEditingStatuses: []
    }
  },
  components: {
    Input,
    fjlx,
    znbm,
    gw,
    flowFileUpload
  },
  created() {
    this.activeTab = 1 // 设置初始值为 1
  },
  methods: {
    ...mapActions('flow', [
      'changeNewButtondisabled',
      'resetNewButtondisabled'
    ]),
    handleChangeGw(val){
      this.$nextTick(()=>{
        const createdLabel = this.$refs.refGw[this.activeTab].createdLabel
        if(createdLabel.flag === false){
          this.tabList[this.activeTab].zxgw = val
          this.tabList[this.activeTab].zxgwmc = null
        }else{
          this.tabList[this.activeTab].zxgwmc = createdLabel.label
          this.tabList[this.activeTab].zxgw = null
        }
      })
    },
    // 设置特殊环节颜色
    getTextColor(hjmc) {
      if (hjmc === '开始') {
        return 'green'
      } else if (hjmc === '形成正文发布') {
        return 'red'
      } else {
        return '' // 默认颜色
      }
    },

    changeTab(index) {
      this.activeTab = index // 切换Tab
    },
    // 校验是否处于编辑状态
    handleChildEditingStatus(newVal, index) {
      this.$set(this.childEditingStatuses, index, newVal)
    },

    // 格式化多选框的职能部门
    formatStringArray(str) {
      // 如果输入不是字符串，直接返回原值
      if (typeof str !== 'string') {
        return str
      }

      let parsedArray
      try {
        // 尝试解析输入字符串为数组
        parsedArray = JSON.parse(str)
      } catch (e) {
        console.error('JSON parsing error:', e)
        return null
      }

      // 如果解析成功，遍历数组，去除字符串元素中的转义字符
      const cleanedArray = parsedArray.map((item) => {
        if (typeof item === 'string') {
          try {
            return JSON.parse(item)
          } catch (e) {
            console.error('JSON parsing error for item:', e, item)
            return item
          }
        } else {
          // 如果元素不是字符串，直接返回原值
          return item
        }
      })

      return cleanedArray
    }
  },
  computed: {
    ...mapGetters(['flowFormParams', 'newButtondisabled', 'generalFileList']),

    //tab-list
    tabList() {
      const initList = [{ hjmc: '开始' }, { hjmc: '形成正文发布' }]
      const tdywlcHjList = this.flowFormParams.tdywlcHjList || []
      if (tdywlcHjList) {
        tdywlcHjList.forEach((item) => {
          if (item.zxbm && item.zxbm.length > 0) {
            item.zxbm = this.formatStringArray(item.zxbm)
          }
        })
      }
      const tabList = [initList[0], ...tdywlcHjList, initList[1]]
      return tabList
    },
    // 计算 tab-list <li> 宽度
    liWidth() {
      const liWidth = 100 / this.tabList.length
      return liWidth + '%'
    }
  }
}
</script>

<style lang="sass" scoped>
.tab-list
  list-style-type: none
  padding: 0
  margin: 0
  padding: 13px 2%
  border-radius: 4px
  background: #f5f7fa
  position: relative
  font-size: 16px
  font-weight: 700
  color: #303133

  & li
    display: inline-block
    padding: 10px
    cursor: pointer
    text-align: center
    position: relative

  .array
    position: absolute
    left: 95%

  .array:after
    transform: rotate(45deg) translateY(4px)
    transform-origin: 100% 100%

  .array:before
    transform: rotate(-45deg) translateY(-4px)
    transform-origin: 0 0

  .array:after,
  .array:before
    content: ""
    display: inline-block
    height: 15px
    width: 1px
    background: #c0c4cc

  & li.active
    color: #409eff

.input-note,
.input-container,
.fileUpload-container
  margin-top: 20px

.tab
  margin-top: 20px
  border: 1px solid #ebeef5
  border-radius: 4px
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1)
  background: #fff

.fileUpload-container
  .fjlx
    margin-right: 10px
</style>
