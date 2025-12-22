<!-- @author zhengjie -->
<template>
  <div>
    <el-input v-model="num" @change="ceshi" style="width:200px"></el-input>
    <!-- <p >{{num|NumFormat}}</p> -->
  </div>
</template>

<script>
import icons from "./index";
var that;
export default {
  name: "threenum",
  data() {
    return {
      num: 0
    };
  },
  beforeCreate: function() {
    that = this;
  },

  methods: {
    ceshi(value) {
      if (!value) return "0.00";

      var intPart = Number(value).toFixed(0); //获取整数部分
      var intPartFormat = intPart
        .toString()
        .replace(/(\d)(?=(?:\d{3})+$)/g, "$1,"); //将整数部分逢三一断
      var floatPart = ".00"; //预定义小数部分
      var value2Array = value.split(".");
      //=2表示数据有小数位
      if (value2Array.length == 2) {
        floatPart = value2Array[1].toString(); //拿到小数部分
        if (floatPart.length == 1) {
          //补0,实际上用不着

          this.num = intPartFormat + "." + floatPart + "0";
        } else {
          this.num = intPartFormat + "." + floatPart;
        }
      } else {
        this.num = intPartFormat + floatPart;
      }
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss" scoped></style>
