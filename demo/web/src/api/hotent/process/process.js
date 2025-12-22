import request from "@/utils/request";

//获取我的待办任务
export function getMyTask(keyword, page, size) {
    let querys = [];
    if (keyword != undefined && keyword != null){
        querys = [
            { property: "bt.subject_", value: keyword, group: "quick", relation: "OR", operation: "LIKE" },
            { property: "bt.proc_inst_id_", value: keyword, group: "quick", relation: "OR", operation: "LIKE" },
            { property: "bt.proc_def_name_", value: keyword, group: "quick", relation: "OR", operation: "LIKE" },
            { property: "bt.name_", value: keyword, group: "quick", relation: "OR", operation: "LIKE" }
        ];
    }

    let parm = {
        pageBean: { page: page, pageSize: size, total: 0 },
        querys: querys,
        sorter: []
    }
    return request({
        url: "hotent/runtime/task/v1/getTodoList",
        method: "post",
        data: parm
    });
}

//获取我的已办任务
export function getMyFinishedTask(keyword, page, size){
    let querys = [];
    if (keyword != undefined && keyword != null){
        querys = [
            { property: "subject_", value: keyword, group: "quick", relation: "OR", operation: "LIKE" },
            { property: "wfInst.id_", value: keyword, group: "quick", relation: "OR", operation: "LIKE" },
            { property: "proc_def_name_", value: keyword, group: "quick", relation: "OR", operation: "LIKE" }
        ];
    }
    let parm = {
        pageBean: { page: page, pageSize: size, total: 0 },
        querys: querys,
        sorter: []
    }
    return request({
        url: "hotent/runtime/instance/v1/getDoneInstList",
        method: "post",
        data: parm
    });
}

//查询流程定义节点配置
export function getDefSetting(defId, topDefId){
    let url = "hotent/flow/node/v1/getDefSetting?defId="+ defId +"&topDefKey=" + ((topDefId == undefined || topDefId == null) ? "" : topDefId);
    return request({
        url: url,
        method: 'get'
    })
}

//查询全局流程表单地址
export function getUrlForm(formKey){
    let url = "hotent/form/formUrlForm/v1/querList?status=deploy&formType=pc&formKey=" + formKey;
    let parm = {
        pageBean: {
            page: 1, pageSize: 10, total: 0
        },
        querys: [],
        sorter: []
    };
    return request({
        url: url,
        method: 'post',
        data: parm
    })
}

//获取流程节点配置
export function getNodeSetting(defId, nodeId){
    let url = "hotent/flow/node/v1/eventScriptEdit?defId="+ defId +"&nodeId=" + nodeId;
    return request({
        url: url,
        method: 'get'
    })
}

//查询流程实例
export function getProcInst(procInstId){
    let url = "hotent/runtime/instance/v1/getInstanceByInstId?instId=" + procInstId;
    return request({
        url: url,
        method: 'get'
    })
}

//查询流程审批历史
export function getHistoryOpinion(procInstId){
    let url = 'hotent/runtime/instance/v1/instanceFlowOpinions?instId=' + procInstId;
    return request({
        url: url,
        method: 'get'
    })
}

//流程启动
export function startProcess(account, orgId, flowKey, businessId, vars){
    let startParms = {
        account: account,
        startOrgId: orgId,
        flowKey: flowKey,
        businessKey: businessId
    };
    if (vars){
        startParms.vars = vars
    }

    return request({
        url: 'hotent/runtime/instance/v1/start',
        method: 'post',
        data: startParms
    })
}

export function complete(account, taskId, instId, formKey, actionName, opinion, vars){
    let parms = {
        account: account,
        taskId: taskId,
        instId: instId,
        formKey: formKey,
        actionName: actionName,
        opinion: opinion
    };
    if (vars){
        parms.vars = vars;
    }
    return request({
        url: 'hotent/runtime/task/v1/complete',
        method: 'post',
        data: parms
    })
}