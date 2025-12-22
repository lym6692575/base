import { staticRouter, dynamicRouter } from "../router/routerData";
import router from "../router/index";
import store from "../store/index";
const routes = staticRouter;

export default function loadRoute() {
  try {
    if (store.state.base.is) {
      store.dispatch("base/setRouter", staticRouter);
      setTimeout(() => {
        console.log("data", data);
        let data = routesData(dynamicRouter);
        store.dispatch("base/setRouter", data);
        router.addRoutes(data);
      }, 2000);
    } else {
      store.dispatch("base/setRouter", staticRouter);
    }
  } catch (error) {
    console.log(error);
  }
}

function routesData(result) {
  result.forEach(element => {
    routes.push({
      path: element.path,
      // 太他妈傻逼了webpack不支持import
      // component: () => import(`@/views/${element.component}`)
      hidden: element.hidden,
      component: resolve => require([`@/views/${element.component}`], resolve)
    });
  });
  return routes;
}
