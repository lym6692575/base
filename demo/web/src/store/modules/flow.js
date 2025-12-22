import Vue from "vue";

const state = {
  // 业务流程生数据
  flowFormParams: {
    // general: "", // 流程名称
    // ywlbid: "", // 业务类别id
    // lczt: "", // 流程状态
    // tdywlcHjList: []
  },
  generalFileList: [], // 流程通用列表
  // 开关是否可用
  newButtondisabled: false
};

const mutations = {
  // 添加通用附件到
  ADD_GENERAL_TO_TDYWLCHJLIST(state) {
    state.flowFormParams.tdywlcHjList.forEach(obj => {
      state.generalFileList.forEach(item => {
        console.log("1obj", obj);
        // 判断是新增还是更新
        const index = obj.tdywlcHjfjList.findIndex(
          fj => fj.temporaryId === item.temporaryId
        );
        if (index == -1) {
          obj.tdywlcHjfjList.push(item);
        } else {
          Vue.set(obj.tdywlcHjfjList, index, item);
        }
        // Vue.set(obj, item, "");
      });
    });
  },

  // 删除通用附件到
  REMOVE_GENERAL_FROM_TDYWLCHJLIST_BY_ID(state, deletedId) {
    console.log("deletedId", deletedId);
    state.flowFormParams.tdywlcHjList.forEach(obj => {
      const index = obj.tdywlcHjfjList.findIndex(
        item => item.temporaryId === deletedId
      );
      console.log("index", index);
      if (index !== -1) {
        obj.tdywlcHjfjList.splice(index, 1);
      }
    });
  },

  // 设置流程通用列表
  SET_GENERALFILELIST(state, data) {
    state.generalFileList = data;
  },

  // set业务流程
  SET_FORMPARAMS(state, flowFormParams) {
    state.flowFormParams = JSON.parse(JSON.stringify(flowFormParams));
  },

  // 改变开关是否可用
  CHANGE_NEWBUTTONDISABLED(state) {
    state.newButtondisabled = !state.newButtondisabled;
  },

  // 重置开关状态
  RESET_NEWBUTTONDISABLED(state) {
    state.newButtondisabled = false;
  }
};

const actions = {
  addGeneralToTdywlcHjList(context) {
    context.commit("ADD_GENERAL_TO_TDYWLCHJLIST");
  },

  // 存流程通用附件列表
  setGeneralFileList(context, generalFileList) {
    context.commit("SET_GENERALFILELIST", generalFileList);
    context.commit("ADD_GENERAL_TO_TDYWLCHJLIST");
  },

  // 删除流程通用附件列表
  removeGeneralFileList(context, { generalFileList, deletedId }) {
    context.commit("SET_GENERALFILELIST", generalFileList);
    context.commit("REMOVE_GENERAL_FROM_TDYWLCHJLIST_BY_ID", deletedId);
  },

  // 存表单数据
  setflowFormParams(context, flowFormParams) {
    console.log("actionsflowFormParams", flowFormParams);
    context.commit("SET_FORMPARAMS", flowFormParams);
  },

  // 重置表单数据
  resetflowFormParams(context) {
    context.commit("SET_FORMPARAMS", {});
  },

  // 改变开关是否可用
  changeNewButtondisabled(context) {
    context.commit("CHANGE_NEWBUTTONDISABLED");
  },

  // 重置开关状态
  resetNewButtondisabled(context) {
    context.commit("RESET_NEWBUTTONDISABLED");
  },

  // 重置flow组件所有vuex状态
  resetflow(context) {
    context.commit("RESET_NEWBUTTONDISABLED");
    context.commit("SET_FORMPARAMS", {});
    context.commit("SET_GENERALFILELIST", []);
  }
};

export default {
  namespaced: true,
  state,
  mutations,
  actions
};
