<template>
  <el-row>
    <el-col class="row" :span="24" style="margin-top: -40px">
      <el-row class="mt10">
        <el-col :span="24">
          <h5 class="title">
            <i class="el-icon-s-management"></i>
            <span class="mr20">最新资料:</span>
            <el-link href="#/knowledge/compliance" type="primary" class="title-more mr10">更多</el-link>
            <div class="title-border"></div>
          </h5>
        </el-col>
        <el-col>
          <latestTable></latestTable>
        </el-col>
      </el-row>
      <el-row class="mt10">
        <el-col :span="24">
          <h5 class="title">
            <i class="el-icon-menu"></i>
            <span class="mr20">常用资料:</span>
            <div class="title-border"></div>
          </h5>
        </el-col>
        <el-col :span="6" v-for="(item, index) in linkMenu" :key="index">
          <router-link
            v-if="!item.link.path.startsWith('http')"
            :to="item.link"
          >
            <el-card class="box-card pt10" shadow="hover" :style="item.bgcolor">
              <div style="display: flex; align-items: center">
                <div class="circle-icon">
                  <i :class="item.icon"></i>
                </div>
                <div class="card-right ml20">
                  <div class="name">{{ item.name }}</div>
                </div>
              </div>
            </el-card>
          </router-link>
          <a v-else :href="item.link.path" target="_blank">
            <el-card class="box-card pt10" shadow="hover" :style="item.bgcolor">
              <div style="display: flex; align-items: center">
                <div class="circle-icon">
                  <i :class="item.icon"></i>
                </div>
                <div class="card-right ml20">
                  <div class="name">{{ item.name }}</div>
                </div>
              </div>
            </el-card>
          </a>
        </el-col>
      </el-row>
      <el-row :gutter="40" class="mt10">
        <el-col :span="24">
          <h5 class="title">
            <i class="el-icon-search"></i>
            <span class="mr20">资料查询:</span>
            <div class="title-border"></div>
          </h5>
        </el-col>
        <el-col>
          <home-table></home-table>
        </el-col>
      </el-row>
    </el-col>
  </el-row>
</template>

<script>
import {getHgwjCount} from "@/api/app/home";
import homeTable from "./homeTable";
import latestTable from "@/views/myapp/home/latestTable.vue";

export default {
  components: {homeTable, latestTable},
  data() {
    return {
      hgwjCount: [],
      hgwjMenu: [
        {
          name: "规章制度",
          icon: "el-icon-s-order",
          bgcolor:
            "background:linear-gradient(120deg, #65BAF1 0%, #4BA6F3 100%);",
          link: {
            path: "/knowledge/compliance",
            query: {wjlbid: "1"},
          },
        },
        {
          name: "风险预警",
          icon: "el-icon-warning",
          bgcolor:
            "background:linear-gradient(120deg, #61ECB8 0%, #38DBDF 100%);",
          link: {
            path: "/knowledge/compliance",
            query: {wjlbid: "4"},
          },
        },
      ],
      linkMenu: [
        {
          name: "岗位职责",
          icon: "el-icon-s-custom",
          bgcolor:
            "background:linear-gradient(120deg, #65BAF1 0%, #4BA6F3 100%);",
          link: {
            path: "/knowledge/compliance",
            query: {wjlbid: "8b6adfc4898216610189827d0b8e0000"},
          },
        },
        {
          name: "业务流程",
          icon: "el-icon-s-help",
          bgcolor:
            "background:linear-gradient(120deg, #61ECB8 0%, #38DBDF 100%);",
          link: {
            path: "/knowledge/businessFlow",
          },
        },
        {
          name: "上级管理制度",
          icon: "el-icon-s-order",
          bgcolor:
            "background:linear-gradient(120deg, #65BAF1 0%, #4BA6F3 100%);",
          link: {
            path: "https://ims.petrochina/home",
          },
        },

        {
          name: "基层规程",
          icon: "el-icon-s-management",
          bgcolor:
            "background:linear-gradient(120deg, #46DDDE 0%, #6CE4E5 100%);",
          link: {
            path: "/building",
            query: {wjlbid: "297e70548985569f018985bd3e490001"},
          },
        },
      ],
    };
  },
  computed: {
    hgwjMenuComputed() {
      return this.hgwjMenu.map((item) => {
        // 在 hgwjCount 中寻找对应的计数
        const countItem = this.hgwjCount.find(
          (count) => count.lbmc === item.name
        );

        // 如果找到了对应的计数，就把它添加到 item 中
        if (countItem) {
          item.count = countItem.count;
        }

        return item;
      });
    },
  },
  methods: {
    // 获取合规文件计数
    handleGetHgwjCount() {
      getHgwjCount()
        .then((response) => {
          // this.$message({
          //   message: response.msg,
          //   type: "success",
          // });
          this.hgwjCount = response.data;
        })
        .catch((error) => {
          this.$message({
            message: error,
            type: "warning",
          });
        });
    },
  },

  mounted() {
    // 初始数据
    this.handleGetHgwjCount();
  },
};
</script>

<style lang="sass" scoped>
.title
  color: #515A6e
  &-border
    border-bottom: 1px dashed #909399
    margin-top: 20px
    opacity: 0.4

.icon
  font-size: 20px

.row
  padding: 10px 0 20px 0

  .label
    vertical-align: middle
    font-size: 14px
    font-weight: bold
    color: #606266
    line-height: 40px

  .box-card
    color: #fff
    min-width: 250px
    min-height: 110px
    margin-right: 80px
  .card-right
    padding: 10px 12% 0
    text-align: center

  .circle-icon
    display: inline-flex
    justify-content: center
    align-items: center
    border-radius: 50%
    width: 50px
    height: 50px
    padding: 5px
    font-size: 36px

  .name
    font-size: 18px
    font-weight: bold
    letter-spacing: 4px
    padding-right: 4px
    min-width: 92px
    padding-bottom: 8px

  .count
    font-size: 30px
    padding-top: 10px
    font-weight: bold
  .title-more
    float: right
</style>
