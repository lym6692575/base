import Layout from "@/layout";
// 静态路由
export const staticRouter = [
  {
    path: "/login",
    name: "",
    component: () => import("@/views/login.vue")
  },
  {
    path: "/404",
    component: () => import("@/views/error/404")
  },
  {
    path: "/401",
    component: () => import("@/views/error/401")
  },
  {
    path: "/Default",
    component: () => import("@/views/Default.vue")
  },
  {
    path: "/",
    component: Layout
  }
];

// 动态路由
export const dynamicRouter = [
  {
    path: "/def",
    component: "def.vue"
  }
];
