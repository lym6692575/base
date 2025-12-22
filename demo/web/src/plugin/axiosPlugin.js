import axios from 'axios';

const AxiosPlugin = {
  install(Vue) {
    Vue.prototype.$axios = axios;
  }
};

export default AxiosPlugin;