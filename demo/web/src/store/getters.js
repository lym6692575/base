const getters = {
  userInfo: state => state.base.userInfo,
  token: state => state.base.token,
  selectedNode: state => state.base.selectedNode,
  is: state => state.base.is,
  routerArr: state => state.base.routerArr,
  tagBarArr: state => state.base.tagBarArr,
  // 下拉框
  // 文件类别
  wjlb: state => state.common.wjlb,
  // 业务类别
  ywlb: state => state.common.ywlb,
  // 效力等级
  xldj: state => state.common.xldj,
  // 职能部门
  znbm: state => state.common.znbm,
  // 文件类型
  fjlx: state => state.common.fjlx,
  // 文件类型
  gw: state => state.common.gw,
  // 状态类型
  zt: state => state.common.zt,

  //树结构
  znbmTree: state => state.common.znbmTree,

  // 流程组件用
  // 流程表单参数
  flowFormParams: state => state.flow.flowFormParams,
  // 开关是否可用
  newButtondisabled: state => state.flow.newButtondisabled,
  // 流程通用列表
  generalFileList: state => state.flow.generalFileList
};
export default getters;
