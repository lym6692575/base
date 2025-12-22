import store from '@/store'

/*
    项目工具类
*/
export class RouterUtils {
  static get routerArr() {
    return store.getters.routerArr;  // 获取最新的路由数组
  }

  static findRouterByName(name, routes = this.routerArr) {
    for (const route of routes) {
      // 检查当前路由对象的 name 是否匹配
      if (route.name === name) {
        return route;
      }

      // 如果有 children 属性，递归地在 children 中搜索
      if (route.children && route.children.length > 0) {
        const found = this.findRouterByName(name, route.children);
        if (found) {
          return found;
        }
      }
    }

    // 如果没有找到，返回undefined
    return undefined;
  }
}


export class evn {
  static getBasePathConfig() {
    return process.env.VUE_APP_ENV === "prod"
      ? require("../../config/prod.config.js")
      : process.env.VUE_APP_ENV === "dev"
        ? require("../../config/dev.config.js")
        : require("../../config/test.config.js");
  }
}

