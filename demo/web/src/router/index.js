import Vue from 'vue'
import Router from 'vue-router'
import { staticRouter } from './routerData'
import store from '@/store' // 引入Vuex的store，此处路径需要根据项目实际情况
import { Notification, MessageBox, Message } from 'element-ui'
import loadRoute from '@/utils/loadingRouter'

Vue.use(Router)

const routes = staticRouter // 静态路由保存

const router = new Router({
  mode: 'hash',
  scrollBehavior: () => ({ y: 0 }),
  routes
})

router.beforeEach(async (to, from, next) => {
  const routerArr = store.getters.routerArr

  // 第一次进来，先加载动态路由
  if (!routerArr.length) {
    await loadRoute()          // 等它加载完
    return next({              // 跳回用户想去的地方
      path: to.fullPath,
      replace: true
    })
  }

  // 需要登录的页面
  if (to.matched.some(r => r.meta.requiresAuth)) {
    if (!store.getters.is) {
      Message('请使用管理员账号登录！')
      return next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    }
  }

  // 普通页面，直接放行
  next()
})

export default router
