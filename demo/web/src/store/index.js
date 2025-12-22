import Vue from "vue";
import Vuex from "vuex";
import base from "./modules/base";
import common from "./modules/common";
import flow from "./modules/flow";
import getters from "./getters";
import createPersistedState from "vuex-persistedstate"; // vueX持久存储

Vue.use(Vuex);

const store = new Vuex.Store({
  modules: {
    base,
    common,
    flow
  },
  getters,
  plugins: [
    createPersistedState({
      storage: window.sessionStorage,
      // paths: ["userInfo"],
      paths: ["base", "common"]
    })
  ]
});

export default store;
