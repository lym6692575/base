/**
 * 通用js方法封装处理
 * Copyright (c) 2019 app
 */

const baseURL = process.env.VUE_APP_BASE_API;

// 日期格式化
export function parseTime(time, pattern) {
  if (arguments.length === 0 || !time) {
    return null;
  }
  const format = pattern || "{y}-{m}-{d} {h}:{i}:{s}";
  let date;
  if (typeof time === "object") {
    date = time;
  } else {
    if (typeof time === "string" && /^[0-9]+$/.test(time)) {
      time = parseInt(time);
    }
    if (typeof time === "number" && time.toString().length === 10) {
      time = time * 1000;
    }
    date = new Date(time);
  }
  const formatObj = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay()
  };
  const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
    let value = formatObj[key];
    // Note: getDay() returns 0 on Sunday
    if (key === "a") {
      return ["日", "一", "二", "三", "四", "五", "六"][value];
    }
    if (result.length > 0 && value < 10) {
      value = "0" + value;
    }
    return value || 0;
  });
  return time_str;
}

// 表单重置
export function resetForm(refName) {
  if (this.$refs[refName]) {
    this.$refs[refName].resetFields();
  }
}

// 添加日期范围
export function addDateRange(params, dateRange) {
  var search = params;
  search.beginTime = "";
  search.endTime = "";
  if (null != dateRange && "" != dateRange) {
    search.beginTime = this.dateRange[0];
    search.endTime = this.dateRange[1];
  }
  return search;
}

// 回显数据字典
export function selectDictLabel(datas, value) {
  var actions = [];
  Object.keys(datas).map(key => {
    if (datas[key].dictValue == "" + value) {
      actions.push(datas[key].dictLabel);
      return false;
    }
  });
  return actions.join("");
}

// 通用下载方法
export function download(fileName) {
  window.location.href =
    baseURL +
    "/common/download?fileName=" +
    encodeURI(fileName) +
    "&delete=" +
    true;
}

// 字符串格式化(%s )
export function sprintf(str) {
  var args = arguments,
    flag = true,
    i = 1;
  str = str.replace(/%s/g, function() {
    var arg = args[i++];
    if (typeof arg === "undefined") {
      flag = false;
      return "";
    }
    return arg;
  });
  return flag ? str : "";
}

// 转换字符串，undefined,null等转化为""
export function praseStrEmpty(str) {
  if (!str || str == "undefined" || str == "null") {
    return "";
  }
  return str;
}
//把数组内所有children为null删除,主要用于treeselect
export function childrenNullToEmptyArray(arrayList) {
  arrayList.forEach(item => {
    if (!item.children || item.children.length == 0) {
      item.children = [];
      delete item["children"];
    }
    item.children && childrenNullToEmptyArray(item.children);
  });
  return arrayList;
}

/**
 * 构造树型结构数据
 * @param {*} data 数据源
 * @param {*} id id字段 默认 'id'
 * @param {*} parentId 父节点字段 默认 'parentId'
 * @param {*} children 孩子节点字段 默认 'children'
 * @param {*} rootId 根Id 默认 0
 */
export function handleTree(data, id, parentMenuId, children, rootId) {
  function sortNumber(a, b) {
    return a.orderNo - b.orderNo;
  }
  id = id || "id";
  parentMenuId = parentMenuId || "parentMenuId";
  children = children || "children";
  rootId = rootId || 0;
  //对源数据深度克隆
  const cloneData = JSON.parse(JSON.stringify(data));
  let ids = [];
  if (cloneData.length > 0) {
    cloneData.forEach(function(o, i) {
      ids.push(o[id]);
    });
  }

  const rootIds = cloneData.filter(items => {
    return !ids.includes(items[parentMenuId]);
  });
  var resultData = [];
  if (rootIds.length > 0) {
    rootIds.forEach(function(o, i) {
      resultData.push(treeDataChange(cloneData, o[id])[0]);
    });
  }
  function treeDataChange(cloneData, rootId_) {
    //循环所有项
    let treeData = cloneData.filter(father => {
      let branchArr = cloneData.filter(child => {
        //返回每一项的子级数组
        return father[id] === child[parentMenuId];
      });
      branchArr.length > 0 ? (father.children = branchArr) : "";
      if (father.children) {
        father.children = father.children.sort(sortNumber);
      }
      //返回第一层
      return father[id] === rootId_;
    });
    return treeData;
  }
  return resultData != "" ? resultData : data;
}
//获取菜单的叶子节点
export function getLeafMenuPath(menus) {
  let result = "";
  if (menus.subMenus) {
    result = getLeafMenuPath(menus.subMenus[0]);
  } else {
    let query = { from: "tab", tabId: menus.id };
    if (menus.args) {
      query = Object.assign(query, JSON.parse(menus.args));
    }

    result = {
      path: menus.path,
      query: query
    };
  }
  return result;
}
//获取菜单的叶子节点
export function getLeafMenu(menus) {
  let result = "";
  if (menus.subMenus) {
    result = getLeafMenu(menus.subMenus[0]);
  } else {
    // result = {
    //   path: menus.path,
    //   query: { from: "tab", menuId: menus.id, menuName: menus.name }
    // };
    result = menus;
  }
  return result;
}
//把数字类型转换为带有千分位字符
export function transformData(num, decimalNum) {
  let result = "";
  let value = num.toString();
  value = parseFloat(value.replace(/,/g, "")).toFixed(decimalNum);
  let intPart = Math.floor(Number(value)); //获取整数部分
  let intPartFormat = intPart.toString().replace(/(\d)(?=(?:\d{3})+$)/g, "$1,"); //将整数部分逢三一断
  let floatPart = ".000"; //预定义小数部分
  let value2Array = value.split(".");
  //=2表示数据有小数位
  if (value2Array.length == 2) {
    floatPart = value2Array[1].toString(); //拿到小数部分
    if (floatPart.length == 1) {
      //补0,实际上用不着
      result = intPartFormat + "." + floatPart + "0";
    } else {
      result = intPartFormat + "." + floatPart;
    }
  } else {
    result = intPartFormat + floatPart;
  }
  return result;
}
//快捷查询数据获取及处理
export function quickQueryDataAcquisition(
  page,
  size,
  objvalue,
  transferLabelName,
  transferValueName,
  serviceName,
  callBack
) {
  //给快捷查询添加初始值
  serviceName(page, size, objvalue).then(response => {
    if (response) {
      response.elements.forEach(function(m, n) {
        m.label = m[transferLabelName] + "(" + m[transferValueName] + ")";
        m.item = m[transferValueName];
      });
      callBack(response.elements);
    }
  });
}
//调用服务进行编码重复校验，如果服务有返回值（表明数据库中已经存在该值），则清空组件内input的值，进行重复提示
// primaryKey:当前输入框的值，duplicateRemoval是否校验，serviceName服务名称
export function deDuplicationCheck(primaryKey, serviceName, callBack) {
  //给快捷查询添加初始值
  serviceName(primaryKey).then(response => {
    if (response != "") {
      callBack(false);
    } else {
      callBack(true);
    }
  });
}

export function handleKeepAlive(to) {
  if (to.matched && to.matched.length > 2) {
    for (let i = 0; i < to.matched.length; i++) {
      //const element = to.matched[i];
      if (i > 0 && i < to.matched.length - 1) {
        to.matched.splice(i, 1);
        handleKeepAlive(to);
      }
    }
  }
}
//是否作为iframe页嵌入到其他系统
export function inIframe() {
  return window.self != window.top;
}
