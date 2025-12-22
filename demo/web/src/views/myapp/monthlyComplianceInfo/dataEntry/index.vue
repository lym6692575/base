<template>
  <div>
    <el-form
      ref="queryForm"
      :model="queryParams"
      size="medium"
      :rules="queryRules"
      label-width="100px"
      label-position="left"
    >
      <el-row :gutter="20">
        <el-col :span="7">
<!--          <el-form-item label="选择季度：" prop="ssjd">-->
<!--            <lee-quarter-picker v-model="queryParams.ssjd" @change="handleChangeSsjd"></lee-quarter-picker>-->
<!--          </el-form-item>-->
          <el-form-item label="选择月份：" prop="ssyf">
            <el-date-picker
              type="month"
              v-model="queryParams.ssyf"
              format="yyyy-MM"
              value-format="yyyy-MM"
              :style="{ width: '100%' }"
              placeholder="请选择月份"
              clearable
            ></el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="11">
          <el-form-item
            label="所属部门 / 分公司："
            prop="ssbmid"
            label-width="150px"
          >
            <sjid
              v-model="ssbmidArr"
              :checkStrictly="false"
              :onlyCurrentLevel="true"
              @change="handleChangeSjid"
              :clearable="false"
              type="ydhg"
            />
            <!-- <znbm v-model="queryParams.ssbmid" :showAllOptions="false" /> -->
          </el-form-item>
        </el-col>
        <el-col :span="6" class="right-align">
          <el-form-item>
            <el-button
              @click="onSearch()"
              type="primary"
              icon="el-icon-search"
              size="medium"
            >
              查询
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <el-empty
      v-if="formData.tdydhgxxWgsjlx.length === 0"
      description="请选择条件并查询数据"
    ></el-empty>
    <el-form
      v-else
      ref="elForm"
      :model="formData"
      :rules="rules"
      size="medium"
      label-position="left"
    >
      <el-row>
        <el-form-item
          label="本单位及受托管理控股、参股企业，本月是否发生以下重大合规风险事件："
          prop="tdydhgxxWgsjlx"
        >
          <div
            class="wgitem"
            v-for="(item, index) in wgsjlxList"
            :key="item.id"
          >
            <span class="left-align">{{ index + 1 + "、" + item.sjlxmc }}</span>
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
                :disabled="item.disabled"
              >{{ item.label }}
              </el-radio
              >
            </el-radio-group>
          </div>
          <span>注：1. 违规处罚类型包括：海关失信、纳入异常经营名单、政府采购不良记录、重大税收违反、行政处罚、统计上严重失信、欠税公告、银保监及证监会处罚等</span>
        </el-form-item>
        <el-form-item label="事件情况描述：" prop="sjqkms">
          <el-input
            v-model="formData.sjqkms"
            type="textarea"
            placeholder="请输入事件情况描述"
            :autosize="{ minRows: 4, maxRows: 4 }"
            :style="{ width: '100%' }"
          ></el-input>
        </el-form-item>
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
          ></el-input>
        </el-form-item>
        <el-form-item label="填报人：" prop="tbr">
          <el-input
            v-model="formData.tbr"
            placeholder="请输入填报人："
            clearable
            :style="{ width: '100%' }"
          >
          </el-input>
        </el-form-item>
      </el-row>
      <el-form-item size="large">
        <el-button style="float: right" type="primary" @click="submitForm">提交</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
import {znbm} from "@/components/select";
import sjid from "@/views/components/cascader/sjid.vue";
import {
  getWgsjlxList,
  getYdhgxxList,
  saveYdhgxx,
} from "@/api/app/monthlyComplianceInfo/dataEntry";
import leeQuarterPicker from "@/components/elementUI/lee-quarter-picker/index.vue";

export default {
  components: {leeQuarterPicker, znbm, sjid},
  props: [],
  data() {
    return {
      hasQuery: false,
      isLoading: false,
      // 所属部门数组
      ssbmidArr: [],

      queryParams: {
        ssyf: null,
        ssbmid: undefined,
      },
      queryRules: {
        ssyf: [
          {
            required: true,
            message: "请选择月份",
            trigger: "change",
          },
        ],
        ssbmid: [
          {
            required: true,
            message: "请选择部门",
            trigger: "change",
          },
        ],
      },
      formData: {
        tdydhgxxWgsjlx: [],
        ssjd: null,
        ssyf: null,
        ssbmid: null,
        sjqkms: null,
        byzdgz: null,
        tbr: null,
      },
      rules: {
        sjqkms: [
          {
            validator: (rule, value, callback) => {
              if (this.isAnyMarkedTrue && !value) {
                callback(new Error("请输入事件情况描述"));
              } else {
                callback();
              }
            },
            trigger: "blur",
          },
        ],
        tbr: [
          {
            required: true,
            message: "请输入填报人",
            trigger: "blur",
          },
          {
            validator: function (rule, value, callback) {
              var reg = /^[\u4e00-\u9fa5]+$/;
              if (!reg.test(value)) {
                callback(new Error("填报人姓名必须为汉字"));
              } else {
                callback();
              }
            },
            trigger: "blur",
          },
        ],
      },
      // 违规数据列表
      wgsjlxList: [],
    };
  },
  computed: {
    isAnyMarkedTrue() {
      return this.formData.tdydhgxxWgsjlx.some(
        (item) => item.isMarked === true
      );
    },
  },
  watch: {},
  created() {
  },
  mounted() {
    this.handleGetTableData();
  },
  methods: {
    // 修改所属季度更新表单数据
    handleChangeSsjd(val){
      this.formData.ssjd = val
    },

    // 修改部门时更新查询参数
    handleChangeSjid(val) {
      console.log(val)
      if (val.length > 1) {
        this.queryParams.ssbmid = val[val.length - 1]
        this.formData.ssbmid = val[val.length - 1]
      } else {
        this.queryParams.ssbmid = val[0]
        this.formData.ssbmid = val[0]
      }
    },

    // 获取本月重大违规事件列表
    handleGetTableData() {
      this.isLoading = true;
      getWgsjlxList().then((res) => {
        console.log(res.data);
        this.wgsjlxList = res.data;
        this.isLoading = false;
      });
    },

    onSearch() {
      this.$refs["queryForm"].validate((valid) => {
        if (!valid) return;
        let queryParams = JSON.parse(JSON.stringify(this.queryParams));
        getYdhgxxList(queryParams)
          .then((response) => {
            this.$message({
              message: response.msg,
              type: "success",
            });
            console.log(response.data);
            this.formData = response.data;
          })
          .catch((error) => {
            this.$message({
              message: error,
              type: "warning",
            });
          });
      });
    },

    submitForm() {
      this.$refs["elForm"].validate((valid) => {
        if (!valid) return;
        // TODO 提交表单
        saveYdhgxx(this.formData)
          .then((response) => {
            this.$message({
              message: response.msg,
              type: "success",
            });
            console.log(response.data);
            this.formData = response.data;
          })
          .catch((error) => {
            this.$message({
              message: error,
              type: "warning",
            });
          });
      });
    },

    resetForm() {
      this.$refs["elForm"].resetFields();
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
</style>
