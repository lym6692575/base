<template>
  <div class="menu-title-container">
    <div class="menu-title-content">
      <div class="logo-and-text">
        <img :src="require('@/assets/logo/logo.png')" width="30px"/>
        <div class="text-container">
          <span class="chinese">中国石油江西销售分公司</span>
          <span>China Petroleum Jiangxi Sales</span>
        </div>
      </div>
      <div class="log">
        <el-row>
          <el-col :span="12">
            <a :href="`${path}/common/downloadmanual`"><span><i class="el-icon-reading pr10"></i>操作手册下载</span></a>
          </el-col>
          <el-col class="pl20" :span="12">
            <router-link v-if="is===false" to="/login">
              <span><i class="el-icon-user pr10"></i>管理员登录</span>
            </router-link>
            <el-button v-else type="text" @click="logout()">
              <span><i class="el-icon-user pr10"></i>退出当前帐号</span>
            </el-button>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { downloadmanual } from '@/api/app/common'
import { getHgwjCount } from '@/api/app/home'

export default {
  data() {
    return {
      path: ''
    }
  },
  methods: {
    login() {
      this.$store.dispatch('base/changeStore', true)
    },
    logout() {
      this.$store.dispatch('base/changeStore', false)
      this.$store.dispatch('base/setToken', null)
      // this.$store.dispatch('base/setSelectedNode', null)
      this.$message({
        message: '登出成功',
        type: 'success'
      })
      this.$nextTick(() => {
        this.$router.push({ path: '/' })
      })
    }
  },
  mounted() {
    this.path = process.env.VUE_APP_BASE_API
  },
  computed: {
    ...mapGetters(['is', 'selectedNode'])
  }
}
</script>

<style lang="scss" scoped>
.menu-title-container {
  display: flex;
  align-items: center;
  height: 100%;
  color: #fff;
}

.menu-title-container img {
  margin-right: 16px;
}

.menu-title-container span {
  display: flex;
  align-items: center;
  font-weight: 100;
  font-family: "Microsoft YaHei", sans-serif;
  font-size: 14px;
}

.menu-title-container span.chinese {
  letter-spacing: 1px;
  font-size: 16px;
  border-bottom: solid 1px #e6e6e6;
  font-family: "Microsoft YaHei", serif;
}

.menu-title-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex: 1;
}

.logo-and-text {
  display: flex;
  align-items: center;
}

.text-container {
  display: flex;
  flex-direction: column;
}

.log {
  width: 290px;
}

.log a{
  height: 38px;
  line-height: 38px;
}

.log span {
  color: #FFFFFF;
  font-size: 14px;
  font-family: "Microsoft YaHei", system-ui;
  letter-spacing: 1px;
}
</style>



