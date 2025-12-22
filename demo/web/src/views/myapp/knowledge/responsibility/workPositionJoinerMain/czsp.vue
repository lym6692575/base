<template>
  <div>
    <!-- 表格 -->
    <h5 class="title">
      <i class="el-icon-user-solid"></i>
      <span class="mr20">操作视频：</span>
      <div class="title-border"></div>
    </h5>
    <!-- {{ rawData }} -->
    <el-row :gutter="60">
      <el-col
        v-for="item in rawData"
        :key="item.label"
        :span="6"
        :class="{ mb20: rawData.length > 24 / 12 }"
      >
        <el-link
          class="link"
          type="primary"
          :href="item.spljdz"
          target="_blank"
        >
          {{ item.spmc }}
        </el-link>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getGwspListByGwid } from "@/api/app/knowledge/responsibility";
import { DeepCopyUtils, dateUtils } from "@/utils/LeeUtils";
import { mapGetters } from "vuex";
import hgwjDialog from "./Dialog/hwwjDialog.vue";
export default {
  inject: ["eventBus"],
  components: { hgwjDialog },
  data() {
    return {
      rawData: [],
      currentRow: null,
      page: 1,
      rows: 10,
      isLoading: false,
      dialogVisible: false,
    };
  },
  props: {
    value: {
      type: Object,
      required: true,
    },
  },
  computed: {},
  methods: {
    // 获取数据
    handleGetData() {
      this.isLoading = true;
      let params = DeepCopyUtils.deepCopy(this.value);
      if (params.gwglid.length > 0) {
        params.gwglid = params.gwglid[params.gwglid.length - 1];
      } else {
        params.gwglid = params.gwglid[0];
      }
      getGwspListByGwid(params.gwglid).then((res) => {
        console.log(res);
        this.rawData = res.data;
        this.isLoading = false;
      });
    },

    // 打开hgwj对话框
    handleShowDialog(index, row) {
      this.dialogVisible = true;
      this.$refs.dialogRef.onInit(index, row);
    },
  },

  mounted() {},
};
</script>

<style lang="sass" scoped>
.title
  color: #515A6e
  &-border
    border-bottom: 1px dashed #909399
    margin-top: 20px
    opacity: 0.4
.link
  color: #409eff
  text-decoration: underline
</style>
