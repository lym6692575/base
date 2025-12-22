<!-- 季度组件 -->

<!--
  TODO
  有一个初始值没能让样式正确的active的bug
-->
<template>
  <div>
    <el-popover
      placement="bottom"
      width="270"
      v-model="visible"
      :disabled="readonly"
    >
      <div class="el-date-picker__header">
        <button
          type="button"
          aria-label="前一年"
          class="el-picker-panel__icon-btn el-date-picker__prev-btn el-icon-d-arrow-left"
          @click="prev"
        />
        <span role="button" class="el-date-picker__header-label">{{ year }}年</span>
        <button
          type="button"
          aria-label="后一年"
          class="el-picker-panel__icon-btn el-date-picker__next-btn el-icon-d-arrow-right"
          @click="next"
        />
      </div>
      <div style="margin:10px; height: 65px;" class="flex-two-row">
        <template v-for="(item,index) in quarterList">
          <el-col :span="12">
            <el-button
              class="m10 btn"
              style="width: 100%"
              :class="[`${year} 年 ${item.name}` === showValue ? 'selectBtn': '']"
              :key="item.name+year"
              type="text"
              size="medium"
              @click="selectSeason(index, item)"
            >{{ item.name }}
            </el-button>
          </el-col>
        </template>
      </div>
      <!-- input框 -->
      <el-input slot="reference" v-model="showValue" placeholder="选择季度" readonly>
        <i slot="prefix" class="el-input__icon el-icon-date"/>
      </el-input>
    </el-popover>
  </div>
</template>

<script>
// import * as moment from 'moment'

export default {
  name: 'QuarterDate',
  props: {
    value: [String],
    readonly: {
      type: Boolean,
      default: false
    },
  },
  data() {
    return {
      // 按钮数组定义
      quarterList: [
        {month: '01', name: '第一季度', monthRange: [1, 2, 3]},
        {month: '04', name: '第二季度', monthRange: [4, 5, 6]},
        {month: '07', name: '第三季度', monthRange: [7, 8, 9]},
        {month: '10', name: '第四季度', monthRange: [10, 11, 12]}
      ],
      visible: false,
      year: new Date().getFullYear(), // input显示时间，会随着用户操作改变
      currentYear: new Date().getFullYear(), // 当前年份，不变
      month: new Date().getMonth() + 1, // 当前月份，不变
      season: '', // 获取当前季度
      showValue: '' // input框上绑定的数据
    }
  },
  watch: {},
  created() {
  },
  mounted() {
    // 获取当前季度
    if (this.value) {
      const parts = this.value.split('-');
      if (parts.length === 2) {
        const year = parseInt(parts[0], 10);
        const season = parseInt(parts[1], 10);

        // 确保解析的年份和季度是有效的
        if (!isNaN(year) && !isNaN(season)) {
          this.year = year;

          // 更新显示值
          const quarterName = this.quarterList.find(q => q.monthRange.includes((season - 1) * 3 + 1)).name;
          this.showValue = `${year}年 ${quarterName}`;
        }
      }
    }
  },
  methods: {
    /** 上一年 */
    prev() {
      this.year = this.year * 1 - 1
    },
    /** 下一年 */
    next() {
      this.year = this.year * 1 + 1
    },

    /** 触发当前的季度 */
    selectSeason(i, item) {
      this.visible = false
      const that = this
      that.season = i + 1
      this.showValue = `${this.year} 年 ${item.name}`
      // const obj = {
      //   year: this.year,
      //   season: that.season
      // }
      this.$emit("input", `${this.year}-${that.season}`);
      this.$emit('change', `${this.year}-${that.season}`) // 每次选择时间都将当前选择时间发送到父组件
    },
  }
}
</script>

<style lang="scss" scoped>
.btn.el-button--text {
  font-family: Helvetica Neue, Helvetica, PingFang SC, Hiragino Sans GB, Microsoft YaHei, Arial, sans-serif;
  color: #606266;
  display: inline-block;
  font-size: 14px;
  margin-top: 10px;
  text-align: center;

  &:hover {
    color: #409EFF;
  }

  &.is-disabled {
    color: #C0C4CC;
    cursor: not-allowed;
  }

  &.selectBtn {
    color: #409EFF;
  }

  &.selectBtn:hover {
    color: #409EFF !important;
  }

  &.currentBtn {
    color: #409EFF;
    font-weight: bold;

    &:hover {
      color: #409EFF;
      font-weight: bold;
    }
  }
}
</style>

