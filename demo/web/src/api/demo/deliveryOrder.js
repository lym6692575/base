import request from "@/utils/request";

export function getById(id){
    return request({
        url: "flame-ura/deliveryOrders/" + id,
        method: 'get'
    })
}

export function getForPage(parms){
    let url = 'flame-ura/deliveryOrders/getDeliveryOrdersByExampleForPage';
    let urlParm = 'number=' + parms.number
    + "&billingOrg=" + parms.billingOrg
    + "&customer=" + parms.customer
    + "&productionName=" + parms.productionName
    + "&source=" + parms.source
    + "&state=" + parms.state
    + "&page=" + parms.page
    + "&size=" + parms.size;
    return request({
        url: url + "?" + urlParm,
        method: 'get'
    })
}

export function updateState(state, id){
    return request({
        url: 'flame-ura/deliveryOrders/updateState?state=' + state + "&id=" + id,
        method: 'patch'
    })
}

export function update(data){
    return request({
        url: 'flame-ura/deliveryOrders',
        method: 'put',
        data: data
    })
}