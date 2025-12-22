// import router from "./router";
import store from "./store";
import { Message } from "element-ui";
import NProgress from "nprogress";
import "nprogress/nprogress.css";
import { getToken } from "@/utils/auth";
import sysSettings from "../settings/sys.settings";
import { handleKeepAlive, inIframe } from "@/utils/commonFn";
NProgress.configure({ showSpinner: false });

// const whiteList = ["/login", "/loginsso", "/auth-redirect", "/bind"];
const whiteList = ["*"];

// router.beforeEach((to, from, next) => {
//   if (inIframe() && to.path != "/loginsso" && to.path != "/login") {
//     sessionStorage.setItem(
//       "inInframeHref",
//       JSON.stringify({ path: to.path, query: to.query, params: to.params })
//     );
//   }
//   NProgress.start();
//   setTimeout(function() {
//     handleKeepAlive(to);
//     if (getToken() && localStorage.getItem("userInfo_L")) {
//       if (!store.getters.info) {
//         store.dispatch(
//           "setUserInfo",
//           JSON.parse(localStorage.getItem("userInfo_L"))
//         );
//       }
//       if (to.path === "/login") {
//         next({
//           path: "/"
//         });
//         NProgress.done();
//       } else {
//         if (
//           sessionStorage.getItem("isAuth") != "false" &&
//           store.getters.allMenus.length == 0
//         ) {
//           store
//             .dispatch("getRoutes", store.getters.info.id)
//             .then(accessRoutes => {
//               // 测试 默认静态页面
//               // store.dispatch('permission/generateRoutes', { roles }).then(accessRoutes => {
//               // 根据roles权限生成可访问的路由表
//               //router.matcher = new Router().matcher

//               let basePath = [
//                 {
//                   path: "/",
//                   redirect:
//                     store.state.permission.basePath ||
//                     JSON.parse(localStorage.getItem("basePath"))
//                 },
//                 {
//                   path: "*",
//                   name: "",
//                   redirect: "/error/404"
//                 }
//               ];
//               router.addRoutes(accessRoutes.concat(basePath)); // 动态添加可访问路由表
//               store
//                 .dispatch("setUserMenus", {
//                   userid: store.getters.info.id,
//                   appId: sysSettings.baseAppId
//                 })
//                 .then(menus => {
//                   if (menus.length == 0) {
//                     Message.error("无权访问！");
//                     sessionStorage.setItem("isAuth", false);
//                     store.dispatch("setBasePath", {
//                       path: "/error/401"
//                     });
//                     localStorage.setItem(
//                       "basePath",
//                       JSON.stringify({ path: "/error/401" })
//                     );
//                     next({ path: "/error/401" });
//                     NProgress.done();
//                   } else {
//                     next({
//                       ...to,
//                       replace: true
//                     }); // hack方法 确保addRoutes已完成
//                   }
//                 });
//             });
//           // }).catch(err => {
//           //     store.dispatch('FedLogOut').then(() => {
//           //       Message.error(err)
//           //       next({ path: '/' })
//           //     })
//           //   })
//         } else {
//           next();
//           // 没有动态改变权限的需求可直接next() 删除下方权限判断 ↓
//           // if (hasPermission(store.getters.roles, to.meta.roles)) {
//           //   next()
//           // } else {
//           //   next({ path: '/401', replace: true, query: { noGoBack: true }})
//           // }
//           // 可删 ↑
//         }
//       }
//     } else {
//       if (whiteList.indexOf(to.path) !== -1) {
//         // 在免登录白名单，直接进入
//         next();
//       } else {
//         next(`/login?redirect=${to.path}`); // 否则全部重定向到登录页
//         NProgress.done();
//       }
//     }
//   }, 50);
// });
// router.afterEach(() => {
//   NProgress.done();
// });
