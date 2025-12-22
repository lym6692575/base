import Layout from "@/layout";
// 静态路由
export const staticRouter = [
  {
    path: "/",
    name: "login",
    hidden: true,
    component: () => import("@/views/login")
  },
  {
    path: "/loading",
    name: "loading",
    hidden: true,
    component: () => import("@/views/loading")
  },
  {
    path: "/building",
    name: "building",
    hidden: true,
    component: () => import("@/views/building")
  },
  {
    path: "/fileViewer",
    name: "fileViewer",
    hidden: true,
    component: () => import("@/views/fileViewer")
  },
  {
    path: "/home",
    name: "home",
    component: Layout,
    order: "0010",
    meta: {
      label: "首页面",
      icon: null
    },
    children: [
      {
        path: "", // 注意这里的路径是空字符串，这意味着它会匹配 "/home" 路径
        name: "home",
        hidden: true,
        component: () => import("@/views/myapp/home"),
        meta: {
          label: "首页面",
          icon: null
        }
      }
    ]
  },
  {
    path: "/knowledge",
    name: "knowledge",
    component: Layout,
    meta: {
      label: "合规知识库",
      icon: null
    },
    children: [
      {
        path: "compliance",
        name: "compliance",
        order: "0010",
        component: () => import("@/views/myapp/knowledge/compliance"),
        meta: {
          label: "合规文件",
          icon: null
        }
      },
      {
        path: "case",
        name: "case",
        order: "0020",
        component: () => import("@/views/myapp/knowledge/case"),
        meta: {
          label: "风险提示",
          icon: null
        }
      },
      {
        path: "businessFlow",
        name: "businessFlow",
        order: "0030",
        component: () => import("@/views/myapp/knowledge/businessFlow"),
        meta: {
          label: "业务流程",
          icon: null
        }
      },
      {
        path: "responsibility",
        name: "responsibility",
        order: "0040",
        component: () => import("@/views/myapp/knowledge/responsibility"),
        meta: {
          label: "岗位职责",
          icon: null
        }
      },
      {
        path: "zdxg",
        name: "zdxg",
        order: "0050",
        component: () => import("../views/myapp/knowledge/zdxgCompliance"),
        meta: {
          label: "制度宣贯",
          icon: null
        }
      }
    ]
  },
  {
    path: "/training",
    name: "training",
    component: Layout,
    meta: {
      label: "交流资料管理",
      icon: null
    },
    children: [
      {
        path: "download",
        name: "download",
        order: "0020",
        component: () => import("@/views/myapp/training/download"),
        meta: {
          label: "资料下载",
          icon: null
        }
      }
    ]
  },
  {
    path: "/monthlycomplianceinfo",
    name: "monthlycomplianceinfo",
    component: Layout,
    meta: {
      label: "月度合规信息",
      icon: null
    },
    children: [
      {
        path: "dataEntry",
        name: "dataEntry",
        order: "0010",
        component: () =>
          import("@/views/myapp/monthlyComplianceInfo/dataEntry"),
        meta: {
          label: "合规信息录入",
          icon: null
        }
      },
      {
        path: "dataStatistics",
        name: "dataStatistics",
        order: "0020",
        component: () =>
          import("@/views/myapp/monthlyComplianceInfo/dataStatistics"),
        meta: {
          label: "合规信息统计",
          icon: null
        }
      },

    ]
  },
  {
    path: "/materialSubmittal",
    name: "materialSubmittal",
    component: Layout,
    meta: {
      label: "材料上报",
      icon: null
    },
    children: [
      {
        path: "submit",
        name: "submit",
        order: "0010",
        component: () => import("@/views/myapp/materialSubmittal/submit"),
        meta: {
          label: "上报材料",
          icon: null
        }
      }
    ]
  },
  {
    path: "/messageBoard",
    name: "messageBoard",
    component: Layout,
    meta: {
      label: "留言板",
      icon: null
    },
    children: [
      {
        path: "",
        name: "messageBoardHome",
        order: "0010",
        component: () => import("@/views/myapp/messageBoard/main"),
        hidden: true,
        meta: {
          label: "留言板",
          icon: null
        }
      }
    ]
  },
  {
    path: "/releaseQuery",
    name: "releaseQuery",
    component: Layout,
    meta: {
      label: "综合统计查询",
      icon: null
    },
    children: [
      {
        path: "releaseQueryHome",
        name: "releaseQueryHome",
        order: "0010",
        component: () => import("@/views/myapp/releaseQuery/releaseQueryHome"),
        meta: {
          label: "制度发布查询",
          icon: null
        }
      }
    ]
  },
  {
    path: "/manager",
    name: "manager",
    component: Layout,
    isLogin: true,
    meta: {
      requiresAuth: true,
      label: "系统维护",
      icon: null
    },
    children: [
      {
        path: "manager-messageBoard",
        name: "manager-messageBoard",
        order: "0010",
        component: () => import("@/views/myapp/manager/managerMessageBoard"),
        meta: {
          label: "留言管理",
          icon: null
        }
      },
      {
        path: "flow",
        name: "flow",
        order: "0020",
        component: () => import("@/views/myapp/manager/flow"),
        meta: {
          label: "流程图管理",
          icon: null
        }
      },
      {
        path: "admin",
        name: "admin",
        order: "0030",
        component: () => import("@/views/myapp/manager/admin"),
        meta: {
          label: "管理员维护",
          icon: null
        }
      },
      {
        path: "file",
        name: "file",
        order: "0040",
        component: () => import("@/views/myapp/manager/file"),
        meta: {
          label: "文件管理",
          icon: null
        }
      },
      {
        path: "category",
        name: "category",
        order: "0050",
        component: () => import("@/views/myapp/manager/category"),
        meta: {
          label: "文件类别管理",
          icon: null
        }
      },

      {
        path: "industryCategory ",
        name: "industryCategory",
        order: "0055",
        component: () => import("@/views/myapp/manager/industryCategory"),
        meta: {
          label: "业务类别管理",
          icon: null
        }
      },
      {
        path: "department",
        name: "department",
        order: "0060",
        component: () => import("@/views/myapp/manager/department"),
        meta: {
          label: "部门管理",
          icon: null
        }
      },
      {
        path: "workposition",
        name: "workposition",
        order: "0070",
        component: () => import("@/views/myapp/manager/workPosition"),
        meta: {
          label: "岗位管理",
          icon: null
        }
      },
      {
        path: "workpositionjoiner",
        name: "workpositionjoiner",
        order: "0080",
        component: () => import("@/views/myapp/manager/workPositionJoiner"),
        meta: {
          label: "岗位流程管理",
          icon: null
        }
      },
      {
        path: "departmentroles",
        name: "departmentroles",
        order: "0090",
        component: () => import("@/views/myapp/manager/departmentRoles"),
        meta: {
          label: "部门职责管理",
          icon: null
        }
      },
      {
        path: "videos",
        name: "videos",
        order: "0100",
        component: () => import("@/views/myapp/manager/videos"),
        meta: {
          label: "视频管理",
          icon: null
        }
      },
      {
        path: "upload",
        name: "upload",
        order: "0010",
        component: () => import("@/views/myapp/training/upload"),
        meta: {
          label: "交流资料上传",
          icon: null
        }
      },
      {
        path: "notice",
        name: "notice",
        order: "0010",
        component: () => import("@/views/myapp/materialSubmittal/notice"),
        meta: {
          label: "材料上报通知",
          icon: null
        }
      },
      {
        path: "highlightStatistics",
        name: "highlightStatistics",
        order: "0030",
        component: () =>
          import("@/views/myapp/monthlyComplianceInfo/highlightStatistics"),
        meta: {
          label: "月份亮点统计",
          icon: null
        }
      },
      // {
      //   path: "fileCategory",
      //   name: "fileCategory",
      //   order: "0110",
      //   component: () => import("@/views/myapp/manager/fileCategory"),
      //   meta: {
      //     label: "文件类别管理",
      //     icon: null
      //   }
      // }
      {
        path: "test",
        name: "test",
        hidden: process.env.NODE_ENV !== 'development',
        order: "0020",
        component: () => import("@/views/myapp/manager/test"),
        meta: {
          label: "测试",
          icon: null
        }
      }
    ]
  },
  {
    path: "/manager",
    name: "manager",
    component: Layout,
    isLogin: true,
    meta: {
      requiresAuth: true,
      label: "系统维护",
      icon: null
    },
    children: [
      {
        path: "manager-messageBoard",
        name: "manager-messageBoard",
        order: "0010",
        component: () => import("@/views/myapp/manager/managerMessageBoard"),
        meta: {
          label: "留言管理",
          icon: null
        }
      },
      {
        path: "flow",
        name: "flow",
        order: "0020",
        component: () => import("@/views/myapp/manager/flow"),
        meta: {
          label: "流程图管理",
          icon: null
        }
      },
      {
        path: "admin",
        name: "admin",
        order: "0030",
        component: () => import("@/views/myapp/manager/admin"),
        meta: {
          label: "管理员维护",
          icon: null
        }
      },
      {
        path: "file",
        name: "file",
        order: "0040",
        component: () => import("@/views/myapp/manager/file"),
        meta: {
          label: "文件管理",
          icon: null
        }
      },
      {
        path: "category",
        name: "category",
        order: "0050",
        component: () => import("@/views/myapp/manager/category"),
        meta: {
          label: "文件类别管理",
          icon: null
        }
      },

      {
        path: "industryCategory ",
        name: "industryCategory",
        order: "0055",
        component: () => import("@/views/myapp/manager/industryCategory"),
        meta: {
          label: "业务类别管理",
          icon: null
        }
      },
      {
        path: "department",
        name: "department",
        order: "0060",
        component: () => import("@/views/myapp/manager/department"),
        meta: {
          label: "部门管理",
          icon: null
        }
      },
      {
        path: "workposition",
        name: "workposition",
        order: "0070",
        component: () => import("@/views/myapp/manager/workPosition"),
        meta: {
          label: "岗位管理",
          icon: null
        }
      },
      {
        path: "workpositionjoiner",
        name: "workpositionjoiner",
        order: "0080",
        component: () => import("@/views/myapp/manager/workPositionJoiner"),
        meta: {
          label: "岗位流程管理",
          icon: null
        }
      },
      {
        path: "departmentroles",
        name: "departmentroles",
        order: "0090",
        component: () => import("@/views/myapp/manager/departmentRoles"),
        meta: {
          label: "部门职责管理",
          icon: null
        }
      },
      {
        path: "videos",
        name: "videos",
        order: "0100",
        component: () => import("@/views/myapp/manager/videos"),
        meta: {
          label: "视频管理",
          icon: null
        }
      },
      {
        path: "upload",
        name: "upload",
        order: "0010",
        component: () => import("@/views/myapp/training/upload"),
        meta: {
          label: "交流资料上传",
          icon: null
        }
      },
      {
        path: "notice",
        name: "notice",
        order: "0010",
        component: () => import("@/views/myapp/materialSubmittal/notice"),
        meta: {
          label: "材料上报通知",
          icon: null
        }
      },
      {
        path: "highlightStatistics",
        name: "highlightStatistics",
        order: "0030",
        component: () =>
          import("@/views/myapp/monthlyComplianceInfo/highlightStatistics"),
        meta: {
          label: "月份亮点统计",
          icon: null
        }
      },
      // {
      //   path: "fileCategory",
      //   name: "fileCategory",
      //   order: "0110",
      //   component: () => import("@/views/myapp/manager/fileCategory"),
      //   meta: {
      //     label: "文件类别管理",
      //     icon: null
      //   }
      // }
      {
        path: "test",
        name: "test",
        hidden: process.env.NODE_ENV !== 'development',
        order: "0020",
        component: () => import("@/views/myapp/manager/test"),
        meta: {
          label: "测试",
          icon: null
        }
      }
    ]
  },
  {
    path: "/znwd",
    name: "znwd",
    component: Layout,
    meta: {
      label: "智能问答",
      icon: null
    },
    children: [
      {
        path: "",
        name: "znwd",
        order: "0010",
        component: () => import("@/views/myapp/znwd/index.vue"),
        hidden: true,
        meta: {
          label: "智能问答",
          icon: null
        }
      }
    ]
  },
];

// 动态路由
export const dynamicRouter = [];
