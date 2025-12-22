import { getWjlb, getYwlb, getXldj, getZnbm, getFjlx,getGwSelect } from '@/api/app/common'
import { treeUtils } from '@/utils/LeeUtils.js'

const state = {
  // 下拉框
  wjlb: [], // 文件类别
  ywlb: [], // 业务类别
  xldj: [], // 效力等级
  znbm: [], // 职能部门
  fjlx: [], // 附件类型
  gw: [], // 岗位
  zt: {
    name: '状态',
    data: [{ key: '1', value: '新增' }, { key: '2', value: '修订' },{ key: '0', value: '废止' }]
  }, // 状态

  // 树
  znbmTree: [] // 职能部门树结构
}

const mutations = {
  // 下拉框
  // 存文件类别
  SET_WJLB(state, data) {
    state.wjlb = data
  },
  // 存业务类别
  SET_YWLB(state, data) {
    state.ywlb = data
  },
  // 存效力等级
  SET_XLDJ(state, data) {
    state.xldj = data
  },
  // 存职能部门
  SET_ZNBM(state, node) {
    state.znbm = node
  },

  // 存附件类型
  SET_FJLX(state, node) {
    state.fjlx = node
  },

  // 存附件类型
  SET_GW(state, node) {
    state.gw = node
  },

  // 树结构
  SET_ZNBMTREE(state, node) {
    state.znbmTree = node
  }
}

const actions = {
  // 存文件类别
  setWjlb(context, params) {
    getWjlb(params).then(res => {
      context.commit('SET_WJLB', res)
    })
  },
  // 存业务类别
  setYwlb(context, params) {
    getYwlb(params).then(res => {
      context.commit('SET_YWLB', res)
    })
  },
  // 存效力等级
  setXldj(context, params) {
    getXldj(params).then(res => {
      context.commit('SET_XLDJ', res)
    })
  },

  // 存附件类型
  setFjlx(context, params) {
    getFjlx(params).then(res => {
      context.commit('SET_FJLX', res)
    })
  },

  setGw(context, params) {
    getGwSelect().then(res => {
      context.commit('SET_GW', res)
    })
  },

  // 存职能部门下拉框和树结构
  setZnbm(context) {
    getZnbm().then(res => {
      context.commit('SET_ZNBM', res)
      const tree = res.data.map(item => {
        return {
          id: item.id,
          label: item.bmmc,
          sjid: item.sjid
        }
      })
      console.log('tree', tree)
      context.commit('SET_ZNBMTREE', treeUtils.generateTree(tree, 'sjid'))
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
