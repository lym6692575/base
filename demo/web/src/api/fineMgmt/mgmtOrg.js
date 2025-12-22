import request from "@/utils/request";

// 获取用户有权的组织树
export function getMgmtOrgTrees(userId, depth) {
  return request({
    url: "flame-ura/mgmtOrgs/tree?userId=" + userId + "&depth=" + depth,
    method: "get"
  });
}
//获取用户有权管理的组织
export function getMgmtOrgs(userId, depth) {
  return request({
    url: "flame-ura/mgmtOrgs?userId=" + userId + "&depth=" + depth,
    method: "get"
  });
}
// 根据样例获取用户有权管理的组织树
export function getMgmtOrgTreesByExample(
  userId,
  {
    name,
    code,
    orgTypeId = -1,
    isInternal,
    parentOrgId = -1,
    hierarchy,
    creator,
    creatorId,
    createTime,
    modifier,
    modifierId,
    modifyTime,
    status
  }
) {
  let url = "flame-ura/mgmtOrgs/tree/search/example";
  let filters =
    "userId=" +
    userId +
    (name ? "&name=" + name : "") +
    (code ? "&code=" + code : "") +
    (orgTypeId > -1 ? "&orgTypeId=" + orgTypeId : "") +
    (isInternal ? "&isInternal=" + isInternal : "") +
    (parentOrgId > -1 ? "&parentOrgId=" + parentOrgId : "") +
    (hierarchy > -1 ? "&hierarchy=" + hierarchy : "") +
    (creator ? "&creator=" + creator : "") +
    (creatorId ? "&creatorId=" + creatorId : "") +
    (createTime ? "&createTime=" + createTime : "") +
    (modifier ? "&modifier=" + modifier : "") +
    (modifierId ? "&modifierId=" + modifierId : "") +
    (modifyTime ? "&modifyTime=" + modifyTime : "") +
    (status && status > -1 ? "&status=" + status : "") +
    "&includeType=true&isDelete=false";
  url = url + "?" + filters;
  return request({
    url: url,
    method: "get"
  });
}

// 根据参数获取用户有权管理的组织
export function getMgmtOrgsByParams(
  userId,
  {
    name,
    code,
    orgTypeId = -1,
    isInternal,
    parentOrgId = -1,
    hierarchy,
    creator,
    creatorId,
    createTime,
    modifier,
    modifierId,
    modifyTime,
    status
  }
) {
  return request({
    url:
      "flame-ura/mgmtOrgs/search/params?" +
      "&userId=" +
      userId +
      (name ? "&name=" + name : "") +
      (code ? "&code=" + code : "") +
      (orgTypeId > -1 ? "&orgTypeId=" + orgTypeId : "") +
      (isInternal ? "&isInternal=" + isInternal : "") +
      (parentOrgId > -1 ? "&parentOrgId=" + parentOrgId : "") +
      (hierarchy > -1 ? "&hierarchy=" + hierarchy : "") +
      (creator ? "&creator=" + creator : "") +
      (creatorId ? "&creatorId=" + creatorId : "") +
      (createTime ? "&createTime=" + createTime : "") +
      (modifier ? "&modifier=" + modifier : "") +
      (modifierId ? "&modifierId=" + modifierId : "") +
      (modifyTime ? "&modifyTime=" + modifyTime : "") +
      (status && status > -1 ? "&status=" + status : "") +
      "&includeUnselectedParent=true&includeType=true&isDelete=false",
    method: "get"
  });
}

//获取某有权管理组织的自身及下级有权管理组织
export function getSelfAndSubMgmtOrgTreesOfMgmtOrgForUser(
  userId,
  mgmtOrgId,
  depth = -1
) {
  let url = "flame-ura/mgmtOrgs/selfAndSubMgmtOrgs/tree/search/mgmtOrgId";
  let filters =
    "userId=" + userId + "&mgmtOrgId=" + mgmtOrgId + "&depth=" + depth;
  url = url + "?" + filters;
  return request({
    url: url,
    method: "get"
  });
}
