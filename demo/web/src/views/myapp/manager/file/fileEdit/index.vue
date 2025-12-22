<template>
  <el-dialog
    :title="titleName"
    :visible.sync="dialogVisible"
    v-bind="$attrs"
    v-on="$listeners"
    @open="onOpen"
    @close="onClose"
    width="60%"
  >
    <!--
      内嵌对话框
      传递表格数据 formData
    -->
<!--    <h1>formData</h1>-->
<!--    {{ formData }}-->

    <el-dialog
      width="45%"
      title="流程选择"
      :visible.sync="flowVisible"
      append-to-body
      @open="initOpenYwlcDialog()"
      @closed="flowClosed"
    >
      <flow-picker
        ref="picker"
        :fileFormData="formData"
        @close="handleCloseYwlcDialog()"
        :postType="postType">
      </flow-picker>
    </el-dialog>
    <file-template-list
      :visible.sync="fileTemplateVisible"
      @update-template-date="updateTemplateData"
    >
    </file-template-list>
    <el-row :gutter="15">
      <el-form ref="elForm" :model="formData" size="medium" label-position="right">
        <el-col :span="24">
          <el-form-item label="有效性：" prop="zt">
            <el-radio :disabled="switchDisabled" v-model="formData.zt" label="1"
            >有效
            </el-radio
            >
            <el-radio :disabled="switchDisabled" v-model="formData.zt" label="0"
            >废止
            </el-radio
            >
          </el-form-item>
        </el-col>
        <!-- 废止折叠面板 -->
        <el-collapse-transition>
          <div v-if="formData.zt == '0'">
            <el-col :span="12">
              <el-form-item label="废止时间：" prop="fzsj" :rules="fzsjRules">
                <el-date-picker
                  v-model="formData.fzsj"
                  format="yyyy-MM-dd"
                  value-format="yyyy-MM-dd"
                  :style="{ width: '100%' }"
                  placeholder="请选择日期"
                  clearable
                ></el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="废止原因：" prop="fzyy" :rules="fzyyRules">
                <elInput v-model="formData.fzyy" placeholder="请输入废止原因"></elInput>
              </el-form-item>
            </el-col>
          </div>
        </el-collapse-transition>
        <el-col :span="24">
          <el-form-item
            label="文件名称："
            prop="wjmc"
            :rules="[{ required: true, message: '文件名称不能为空' }]"
          >
            <elInput v-model="formData.wjmc" placeholder="请输入文件名称"></elInput>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="编号(文号)：" prop="wjbh" :rules="wjbhRules">
            <elInput v-model="formData.wjbh" placeholder="请输入编号(文号)"></elInput>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            label="职能部门："
            :prop="isInput?'znbmmc':'znbmid'"
            :rules="[{ required: true, message: '请输入或选择职能部门' }]"
            class="znbm"
          >
            <template v-slot:label>
              <span>职能部门：</span>
              <el-button style="position: absolute; right: 8px;" type="text" @click="handleZnbmSwitch">
                {{ isInput ? '切换下拉框' : '切换录入框' }}
              </el-button>
            </template>
            <znbm
              v-show="!isInput"
              filterable
              allow-create
              :showAllOptions="false"
              v-model="formData.znbmid"
            />
            <el-input v-show="isInput" v-model="formData.znbmmc" placeholder="请输入职能部门名称"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            label="发文时间："
            prop="fwsj"
            :rules="[{ required: true, message: '发文时间未选择' }]"
          >
            <el-date-picker
              v-model="formData.fwsj"
              format="yyyy-MM-dd"
              value-format="yyyy-MM-dd"
              :style="{ width: '100%' }"
              placeholder="请选择日期"
              clearable
            ></el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            label="效力等级："
            prop="xldjid"
            :rules="[{ required: true, message: '效力等级未选择' }]"
          >
            <xldj :showAllOptions="false" v-model="formData.xldjid"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            label="业务类别："
            prop="ywlb"
            :rules="[{ required: true, message: '业务类别未选择' }]"
          >
            <ywlb
              :showAllOptions="false"
              :multiple="true"
              collapse-tags
              v-model="formData.ywlb"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            label="文件类别："
            prop="wjlbid"
            :rules="[{ required: true, message: '文件类别未选择' }]"
          >
            <wjlb :showAllOptions="false" v-model="formData.wjlbid"/>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item
            label="关联岗位："
            prop="glgw"
          >
            <gw v-model="formData.glgw"></gw>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item
            label="链接地址："
            prop="ljdz"
          >
            <el-input v-model="formData.ljdz" placeholder="请输入链接地址"/>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item>
            <!--
              附件列表
              传递附件数据 formData.tdhgwjFj
            -->
            <file-upload-list
              ref="upload"
              :tdhgwjFj="formData.tdhgwjFj"
            ></file-upload-list>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item style="float: right">
            <el-button
              v-if="postType === 'save'"
              type="primary"
              @click="handlePickFile"
            >
              文件模板选择
            </el-button>
            <el-button
              type="primary"
              @click="handlePickFlow"
            >
              {{ buttonLabel }}
            </el-button>
            <el-button type="primary" @click="handleEditFiles">保存</el-button>
          </el-form-item>
        </el-col>
      </el-form>
    </el-row>
  </el-dialog>
</template>

<script>
import fileUploadList from './fileUploadList.vue'
import fileTemplateList from "@/views/myapp/manager/file/fileEdit/fileTemplateList.vue";
import flowPicker from './flowPicker.vue'
import {wjlb, ywlb, xldj, znbm, fjlx} from '@/components/select'
import gw from "@/views/components/cascader/gw.vue"
import {saveTDhgwj, updateTDhgwj} from '@/api/app/manager/file'
import {mapGetters} from 'vuex'
import adminDialog from "@/views/myapp/manager/admin/adminDialog.vue";
import {DeepCopyUtils} from "@/utils/LeeUtils";

export default {
  inheritAttrs: false,
  props: {
    postType: {
      type: String,
      require: true
    },
    switchDisabled: {
      type: Boolean,
      default: true
    },
    titleName: {
      type: String,
      default: '新增合规文件'
    }
  },
  data() {
    return {
      dialogVisible: false, // 主对话框
      flowVisible: false, // 流程选择对话框
      fileTemplateVisible: false, // 模板选择对话框
      templateData: {}, // 所选模板数据
      activeNames: ['1'],
      isInput: false,
      opened: false, // 是否打开过流程选择
      formData: {
        fzsj: undefined,
        fzyy: undefined,
        wjmc: undefined, // 文件名称
        wjbh: undefined, // 文件编号
        znbmmc: undefined, // 职能部门名称
        znbmid: undefined, // 职能部门
        fwsj: undefined, // 发文时间
        xldjid: undefined, // 效力等级
        wjlbid: undefined, // 业务类别
        ywlb: [], // 业务类别
        zt: '1', // 有效性
        fjlxid: undefined, // 附件类型
        ljdz: undefined, // 链接地址
        tdhgwjFj: [],
        tdywlc: [], // 业务流程列表
        glgw: []
      }
    }
  },
  components: {
    adminDialog,
    fileUploadList,
    fileTemplateList,
    flowPicker,
    wjlb,
    ywlb,
    xldj,
    znbm,
    fjlx,
    gw
  },
  methods: {
    // 更新模板数据
    updateTemplateData(val) {
      // 我也不知道为什么要多深拷贝这一次,万一以后有用呢,懒得删了
      this.templateData = DeepCopyUtils.deepCopy(val[0])
      this.templateData.zt = "1"
      delete this.templateData.id
      if (this.templateData.tdhgwjFj && this.templateData.tdhgwjFj.length > 0) {
        // 遍历附件列表把id和hgwjid去掉为了不让后端保存时候失去关联,同时加上标记让后端特殊处理
        this.templateData.tdhgwjFj.forEach((item) => {
          delete item.id
          delete item.hgwjid
          item.isTemplate = true
        })
      }
      this.formData = DeepCopyUtils.deepCopy(this.templateData)
      this.fileTemplateVisible = false
    },

    onOpen() {
    },
    // 文件对话框关闭时
    onClose() {
      this.$refs['elForm'].resetFields()
      console.log("对话框关闭")
      this.$refs.upload.tdhgwjFj=[]
      this.formData =  {
          fzsj: undefined,
          fzyy: undefined,
          wjmc: undefined, // 文件名称
          wjbh: undefined, // 文件编号
          znbmmc: undefined, // 职能部门名称
          znbmid: undefined, // 职能部门
          fwsj: undefined, // 发文时间
          xldjid: undefined, // 效力等级
          wjlbid: undefined, // 业务类别
          ywlb: [], // 业务类别
          zt: '1', // 有效性
          fjlxid: undefined, // 附件类型
          ljdz: undefined, // 链接地址
          tdhgwjFj: [],
          tdywlc: [], // 业务流程列表
          glgw: []
      }
      this.isInput = false
      if (this.$refs.picker) {
        this.$refs.picker.clearSelect()
      }
      // 将是否打开过的标记恢复
      this.opened = false
    },

    close() {
      this.$emit('update:visible', false)
    },

    // 选择流程对话框关闭时
    flowClosed() {
      console.log("关闭触发")
      // 将所选流程更新到文件对话框
      console.log(this.$refs.picker.multipleSelection)
      this.formData.tdywlc = [...this.$refs.picker.multipleSelection]
    },

    // 打开流程选择对话框的回调函数
    initOpenYwlcDialog() {
      // 如果打开过了就不触发初始化
      if (this.opened === false) {
        this.$nextTick(() => {
          this.$refs.picker.openDialog()
          this.opened = true
        })
      }
    },

    // 关闭对话框给选择流程对话框子组件用的
    handleCloseYwlcDialog() {
      console.log("1")
      this.$emit('data-saved')
      this.dialogVisible = false
      this.flowVisible = false
    },

    // 打开对话框逻辑
    openDialog(index, row) {
      console.log(index, row)
      this.formData = JSON.parse(JSON.stringify(row))
      if (this.formData.znbmid === null) {
        this.isInput = true
      }
      this.dialogVisible = true // 打开对话框
    },

    // 打开内嵌选择流程对话框
    handlePickFlow() {
      this.flowVisible = true
    },

    // 打开文件模板列表对话框
    handlePickFile() {
      this.fileTemplateVisible = true
    },

    // 保存或更新
    handleEditFiles() {
      this.$refs.elForm.validate((valid) => {
        // 校验通过，执行提交逻辑
        if (valid) {
          const formDataWithFiles = new FormData() // 表单
          let form = this.formData
          if(this.postType==="save"){
            form.cjr=this.userInfo.realname
          }
          // 附件列表
          const tableData = this.$refs.upload.tableData // 附件列表

          // 处理文件
          tableData.forEach((item) => {
            formDataWithFiles.append('files', item.fileInfo)
          })

          // 处理实体数据
          form.tdhgwjFj = tableData
          formDataWithFiles.append('data', JSON.stringify(form))
          formDataWithFiles.append('files', 'files')

          // 判断更新还是保存
          if (this.postType == 'save') {
            saveTDhgwj(formDataWithFiles).then((res) => {
              if (res.result) {
                this.$message({
                  message: res.msg,
                  type: 'success'
                })
                this.dialogVisible = false
                // 调用父组件刷新
                this.$emit('data-saved')
              }
            })
          } else {
            updateTDhgwj(formDataWithFiles).then((res) => {
              if (res.result) {
                this.$message({
                  message: res.msg,
                  type: 'success'
                })
                this.dialogVisible = false
                // 调用父组件刷新
                this.$emit('data-saved')
              }
            })
          }
        } else {
          // 校验不通过，处理失败情况
          this.$message({
            message: '请填写必填信息',
            type: 'warning'
          })
        }
      })
    },

    handleZnbmSwitch() {
      this.isInput = !this.isInput
      console.log(this.isInput)
      if (this.isInput) {
        this.formData.znbmid = null;
      } else {
        this.formData.znbmmc = null;
      }
    },
  },
  computed: {
    ...mapGetters(['userInfo']),
    // 编号(文号)动态校验
    wjbhRules() {
      return this.formData.wjlbid === '1'
        ? [{required: true, message: '规章制度的编号(文号)不能为空'}]
        : []
    },
    generatedKey() {
      const timestamp = new Date().getTime()
      const random = Math.floor(Math.random() * 1000) // 生成一个随机数，范围为0到999
      return timestamp + '_' + random
    },
    // 动态校验
    fzsjRules() {
      return this.formData.zt == '0'
        ? [{required: true, message: '废止时间不能为空'}]
        : []
    },
    // 动态校验
    fzyyRules() {
      return this.formData.zt == '0'
        ? [{required: true, message: '废止原因不能为空'}]
        : []
    },
    buttonLabel() {
      let str = '流程选择'
      if (this.formData.tdywlc && this.formData.tdywlc.length > 0) {
        str = str + '（' + this.formData.tdywlc.length + '）'
      }

      return str
    },
    // 流程选择button提示
    buttonTip() {
      let res = []
      if (this.formData.tdywlc && this.formData.tdywlc.length > 0) {
        res = this.formData.tdywlc.map(item => item.lcmc)
      }
      return res
    }
  }
}
</script>
<style lang="sass" scoped>
.container
  > div:not(:first-child)
    margin-top: 10px

.fwsj
  display: inline-flex
  align-items: center
  white-space: nowrap
  margin-right: 10px

.znbm /deep/ .el-form-item__content
  margin-top: 36px
//.el-fade-in
//  transition: all 0.5s ease
//  opacity: 0
//.el-fade-in-active
//  opacity: 1
//.el-fade-out
//  transition: all 0.5s ease
//  opacity: 1
//.el-fade-out-active
//  opacity: 0
</style>
