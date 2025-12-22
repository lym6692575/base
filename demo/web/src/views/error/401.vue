<template>
  <div class="errPage-container">
    <el-row>
      <el-col :span="24">
        <div style="width:100%;text-align:center">
          <img :src="unauth" width="134" height="145" alt="未授权" />
        </div>
        <h4>对不起，您暂时没有访问权限！</h4>

        <!-- <ul class="list-unstyled">
          <li class="link-type">
            <router-link to="/">
              回首页
            </router-link>
          </li>
        </ul> -->
      </el-col>
    </el-row>
  </div>
</template>

<script>
import errGif from "@/assets/401_images/401.gif";
import unauth from "@/assets/401_images/unauth.jpg";
export default {
  name: "Page401",
  data() {
    return {
      errGif: errGif + "?" + +new Date(),
      unauth: unauth + "?" + +new Date()
    };
  },
  methods: {
    back() {
      if (this.$route.query.noGoBack) {
        this.$router.push({ path: "/" });
      } else {
        this.$router.go(-1);
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.app-main {
  background: #fff;
}
.errPage-container {
  background: #fff;
  width: 100%;
  max-width: 100%;
  align-items: center;
  height: 100%;
  display: flex;
  justify-content: center;
  .pan-back-btn {
    background: #008489;
    color: #fff;
    border: none !important;
  }
  .pan-gif {
    margin: 0 auto;
    display: block;
  }
  .pan-img {
    display: block;
    margin: 0 auto;
    width: 100%;
  }
  .text-jumbo {
    display: block;
    font-size: 20px;
    font-weight: 700;
    color: #484848;
  }
  .list-unstyled {
    font-size: 14px;
    li {
      padding-bottom: 5px;
    }
    a {
      color: #008489;
      text-decoration: none;
      &:hover {
        text-decoration: underline;
      }
    }
  }
}
</style>
