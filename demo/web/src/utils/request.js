import axios from 'axios'
import { Notification, MessageBox, Message } from 'element-ui'
import store from '@/store'
import route from '@/router'

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
// 创建axios实例
const service = axios.create({
  // axios中请求配置有baseURL选项，表示请求URL公共部分
  baseURL: process.env.VUE_APP_BASE_API,
  // baseURL: "http://10.114.12.92:11330/",
  // 超时
  timeout: 100000
})

// request拦截器
service.interceptors.request.use(
  config => {
    //如果该请求在白名单里，那么不进行操作
    if (config.whitelist) {
      return config
    }
    config.url = encodeURI(config.url) //解决在IE环境下URL传递中文参数变成乱码导至页面功能出错
    const token = store.getters.token
    config.headers['Authorization'] = token
    return config
  },
  error => {
    Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  res => {
    const contentType = res.headers['content-type']
    const code = res.status
    if (contentType === 'application/octet-stream') {
      // 如果是下载文件的接口特殊处理他们
      return res.data
    } else {
      if (code !== 200) {
        Message.error('后端错误,请联系管理员?！')
        return Promise.reject('后端错误！')
      } else {
        if (res.data.httpCode != 200) {
          Message.error(res.data.msg)
        }
        return res.data
      }
    }
  },
  error => {
    const errorData = JSON.parse(JSON.stringify(error))
    let code = errorData.response.status
    let message = errorData.response.data.message
    if (code === 401) {
      MessageBox.confirm('会话超时，请重新登录', '系统提示', {
        distinguishCancelAndClose: true,
        cancelButtonText: '取消',
        confirmButtonText: '重新登录',
        closeOnClickModal: false,
        type: 'warning',
        dangerouslyUseHTMLString: true,
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            // user.loginOut();
            route.push({ name: 'login' })
          } else if (action === 'cancel') {
            // user.loginOut();
            window.close()
          } else {
            // user.loginOut();
            done()
          }
        }
      })
    } else if (code === 403) {
      Message.error('token过期或错误，请重新登录！')
      route.push({ name: 'login' })
    } else if (code !== 200) {
      if (errorData.response.data.message) {
        Message.error(errorData.response.data.message)
      } else {
        Message.error('后端错误,请联系管理员！')
      }
      return {
        status: 'error',
        code: errorData.response.data.status,
        details: errorData.response
      }
    } else {
      return Promise.reject(JSON.stringify(errorData))
    }
  }
)

export default service
