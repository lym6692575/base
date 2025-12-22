import route from '@/router'
import {RouterUtils} from '@/utils/ProjectUtils.js'

const state = {
    is: false, // 判断登录
    openMenus: null,
    userInfo: {},
    token: null,
    routerArr: [], // 静态+动态路由
    tagBarArr: [], // 面包屑数组
    selectedNode: null
}

const mutations = {
    SET_OPEN_MENUS(state, node) {
        state.openMenus = node
    },
    // 用户信息
    SET_USERINFO(state, node) {
        state.userInfo = node
    },
    // 保存token
    SET_TOKEN(state, node) {
        state.token = node
    },
    // 点击了登录退出
    CHANGESTORE(state, bool) {
        state.is = bool
    },
    // 存放路由
    SET_ROUTER(state, data) {
        state.routerArr = data
    },
    // 设置选择节点
    SET_SELECTED_NODE(state, node) {
        state.selectedNode = node
    },
    // 设置面包屑
    SET_TAGBAR(state, data) {
        state.tagBarArr = data
    },
    // 添加面包屑
    PUSH_TAGBAR(state, node) {
        state.tagBarArr.push(node)
    },
    // 删除面包屑
    DELETE_TAGBAR(state, nodeName) {
        state.tagBarArr = state.tagBarArr.filter(item => item.name !== nodeName);
    }
}

const actions = {
    setOpenMenus(context, state) {
        context.commit('SET_OPEN_MENUS', state)
    },

    // 保存用户信息
    setUserInfo(context, state) {
        context.commit('SET_USERINFO', state)
    },

    // 保存token
    setToken(context, state) {
        context.commit('SET_TOKEN', state)
    },

    changeStore(context, state) {
        context.commit('CHANGESTORE', state)
    },

    updateStoreChange(context, state) {
        context.commit('CHANGESTORE', state)
    },

    // 设置路由
    setRouter(context, state) {
        context.commit('SET_ROUTER', state)
    },

    /**
     * 设置选中的节点，并根据需要更新面包屑和进行路由跳转。
     *
     * @param {Object} context - Vuex 的 action 上下文对象，包含 commit 方法和 state 属性。
     * @param {Object} payload - 包含 node 和 query 的对象。
     *   - {Object} node - 选中的节点对象。
     *   - {Object} [query] - 路由跳转时携带的查询参数。
     *
     * 功能描述：
     * 1. 使用 commit 方法更新 Vuex store 中的选中节点（SET_SELECTED_NODE）。
     * 2. 如果节点尚未存在于 tagBarArr 数组中，则将其添加（PUSH_TAGBAR）。
     * 3. 如果当前路由不是目标节点，则进行路由跳转。如果传递了 query 参数，则使用 query 进行路由跳转，否则仅使用节点名称。
     */
    setSelectedNode(context, {node, query}) {
        // 更新选中节点
        context.commit('SET_SELECTED_NODE', node.name)

        // 检查节点是否已存在于 tagBarArr 中，如果不存在，则添加
        const exists = context.state.tagBarArr.some(element => element.name === node.name);
        if (!exists) {
            context.commit('PUSH_TAGBAR', node)
        }

        // 路由跳转逻辑：如果当前路由不是目标节点，则进行跳转
        const currentRouteName = route.currentRoute.name;
        if (currentRouteName !== node.name) {
            if (query) {
                route.push(query)
            } else {
                route.push({name: node.name})
            }
        }
    },

    // tagBar设置
    setTagBar(context, data) {
        context.commit('SET_TAGBAR', data)
    },

    // 添加tagBar
    pushTagBar(context, node) {
        context.commit('PUSH_TAGBAR', node)
    },

    // 删除tagBar节点
    deleteTagBar(context, nodeName) {
        let tags = context.state.tagBarArr;
        let activeName = context.state.selectedNode;
        if (activeName === nodeName) {
            tags.forEach((tag, index) => {
                if (tag.name === nodeName) {
                    let nextTab = tags[index + 1] || tags[index - 1];
                    if (nextTab) {
                        context.dispatch('setSelectedNode', {node: nextTab})
                    }
                }
            });
        }
        context.commit('DELETE_TAGBAR', nodeName)
        if (context.state.tagBarArr.length === 0) {
            const node = RouterUtils.findRouterByName('home')
            context.dispatch('setSelectedNode', {node})
        }
    },
}

export default {
    namespaced: true,
    state,
    mutations,
    actions
}
