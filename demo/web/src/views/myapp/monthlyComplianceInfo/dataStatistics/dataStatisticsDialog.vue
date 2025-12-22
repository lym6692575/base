<template>
  <div>
    <el-dialog
      width="80%"
      v-bind="$attrs"
      v-on="$listeners"
      @open="onOpen"
      @close="onClose"
      title="违规事件详情"
    >
      <el-row :gutter="20">
        <el-form
          ref="elForm"
          :model="formData"
          size="medium"
          label-position="left"
          class="disabled"
        >
          <el-col :span="12">
            <el-form-item label="填报月份：" prop="ssjd">
              <lee-quarter-picker v-model="formData.ssjd" :readonly="true"></lee-quarter-picker>
            </el-form-item>
            <!--            <el-form-item label="填报月份：" prop="ssyf">-->
            <!--              <el-date-picker-->
            <!--                type="month"-->
            <!--                v-model="formData.ssyf"-->
            <!--                format="yyyy-MM"-->
            <!--                value-format="yyyy-MM"-->
            <!--                :style="{ width: '100%' }"-->
            <!--                placeholder="请选择日期"-->
            <!--                clearable-->
            <!--                readonly-->
            <!--              ></el-date-picker>-->
            <!--            </el-form-item>-->
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属部门 / 分公司：" prop="ssbmid">
              <sjid
                v-model="formData.ssbmid"
                :checkStrictly="false"
                :onlyCurrentLevel="true"
                disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item
              label="本单位及受托管理控股、参股企业，本月是否发生以下重大合规风险事件："
              prop="tdydhgxxWgsjlx"
            >
              <div
                class="wgitem"
                v-for="(item, index) in wgsjlxList"
                :key="item.id"
              >
                <span class="left-align">{{
                    index + 1 + "、" + item.sjlxmc
                  }}</span>
                <div class="flex-grow"></div>
                <el-radio-group
                  v-model="formData.tdydhgxxWgsjlx[index]['isMarked']"
                  size="medium"
                >
                  <el-radio
                    v-for="(item, index) in [
                      { label: '是', value: true },
                      { label: '否', value: false },
                    ]"
                    :key="index"
                    :label="item.value"
                    disabled
                  >{{ item.label }}
                  </el-radio
                  >
                </el-radio-group>
              </div>
              <span class="mt10">注：1. 违规处罚类型包括：海关失信、纳入异常经营名单、政府采购不良记录、重大税收违反、行政处罚、统计上严重失信、欠税公告、银保监及证监会处罚等</span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="事件情况描述：" prop="sjqkms">
              <el-input
                v-model="formData.sjqkms"
                type="textarea"
                placeholder="请输入事件情况描述"
                :autosize="{ minRows: 4, maxRows: 4 }"
                :style="{ width: '100%' }"
                readonly
              ></el-input>
            </el-form-item>
          </el-col>
          <!-- <el-col :span="24">
            <el-form-item
              label="本月合规管理重点工作进展情况和工作亮点："
              prop="byzdgz"
            >
              <el-input
                v-model="formData.byzdgz"
                type="textarea"
                placeholder="请输入本月合规管理重点工作进展情况和工作亮点"
                :autosize="{ minRows: 4, maxRows: 4 }"
                :style="{ width: '100%' }"
                readonly
              ></el-input>
            </el-form-item>
          </el-col> -->
          <el-col :span="24">
            <el-form-item label="填报人：" prop="tbr">
              <el-input
                v-model="formData.tbr"
                placeholder="请输入填报人："
                clearable
                :style="{ width: '100%' }"
                readonly
              >
              </el-input>
            </el-form-item
            >
          </el-col>
        </el-form>
      </el-row>
    </el-dialog>
  </div>
</template>
<script>
import {getWgsjlxList} from "@/api/app/monthlyComplianceInfo/dataStatistics";
import {DeepCopyUtils} from "@/utils/LeeUtils";
import sjid from "@/views/components/cascader/sjid.vue";
import leeQuarterPicker from "@/components/elementUI/lee-quarter-picker/index.vue";

export default {
  inheritAttrs: false,
  components: {leeQuarterPicker, sjid},
  props: [],
  data() {
    return {
      isLoading: false,
      // 违规数据列表
      wgsjlxList: [],
      formData: {
        tdydhgxxWgsjlx: [],
        ssyf: null,
        ssbmid: [],
        sjqkms: null,
        byzdgz: null,
        tbr: null,
      },
    };
  },
  computed: {},
  watch: {},
  created() {
  },
  mounted() {
    this.handleWgsjlxList();
  },
  methods: {
    // 初始化对话框数据
    onInit(_, row) {
      let _row = DeepCopyUtils.deepCopy(row);
      this.formData = _row;
    },

    // 获取本月重大违规事件列表
    handleWgsjlxList() {
      this.isLoading = true;
      getWgsjlxList().then((res) => {
        console.log(res.data);
        this.wgsjlxList = res.data;
        this.isLoading = false;
      });
    },

    onOpen() {
    },
    onClose() {
      this.$refs["elForm"].resetFields();
    },
    close() {
      this.$emit("update:visible", false);
    },
    handelConfirm() {
      this.$refs["elForm"].validate((valid) => {
        if (!valid) return;
        this.close();
      });
    },
  },
};
</script>
<style lang="sass" scoped>
.wgitem
  display: flex
  align-items: center
  width: 100%

.left-align
  margin-right: 10px

.flex-grow
  flex-grow: 1

// disable样式穿透
.disabled /deep/ .el-input__inner
  color: #606266
  background: #fff
// .disabled >>>.el-radio__input
//     color: #606266
</style>
