import Vue from "vue";

import Cookies from "js-cookie";
import "normalize.css/normalize.css"; // a modern alternative to CSS resets
import "@/utils/dialogDrag";
import Element from "element-ui";
import Base64 from "js-base64";
import "@babel/polyfill";
import Es6Promise from "es6-promise";

require("es6-promise").polyfill();
Es6Promise.polyfill();
import "element-ui/lib/theme-chalk/index.css";
import Echarts from "echarts"; //引入echarts

import "./assets/styles/element-variables.scss";
import "./assets/styles/animate.min.css";
import "font-awesome/css/font-awesome.min.css";
import "@/assets/styles/index.scss"; // global css
import "@/assets/styles/common.scss"; // common css

import App from "./App";
import store from "./store";
import router from "./router";
import "./assets/icons"; // icon
import "./permission"; // permission control


import sysSettings from "../settings/sys.settings";

import * as filters from "@/utils/filter";
Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key]);
});

import * as commonFn from "@/utils/commonFn";
Object.keys(commonFn).forEach(key => {
  Vue.prototype[key] = commonFn[key];
});

// 全局方法挂载
Vue.prototype.sysSettings = sysSettings;

window.localStorage.setItem("userInfo", Date.now().toString());
window.addEventListener("storage", function(event) {
  if (!event.newValue) {
    return;
  }
  if (event.key === "userInfo") {
    localStorage.setItem(
      "storeSessionData",
      sessionStorage.getItem("userInfo")
    );
    localStorage.removeItem("storeSessionData");
  } else if (event.key === "storeSessionData") {
    if (event.newValue != "null") {
      localStorage.setItem("userInfo_L", event.newValue);
    }
  }
});

/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online! ! !
 */
Vue.use(Element, {
  size: Cookies.get("size") || "medium" // set element-ui default size
});
Vue.config.productionTip = false;
Vue.prototype.$echarts = Echarts; //引入组件

import moment from "moment"
Vue.prototype.$moment = moment; //引入组件

new Vue({
  el: "#app",
  router,
  store,
  render: h => h(App)
});
