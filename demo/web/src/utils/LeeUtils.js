/*
    小李工具类
*/

/*
    小李深拷贝
*/
export class DeepCopyUtils {
  /**
   * 执行深拷贝操作
   * @param {any} obj - 需要进行深拷贝的对象
   * @returns {any} - 深拷贝后的对象
   */
  static deepCopy(obj) {
    if (obj === null || typeof obj !== "object") {
      // 如果是基本类型或 null，直接返回
      return obj;
    }

    if (obj instanceof Date) {
      // 如果是 Date 对象，创建一个新的 Date 对象并设置相同的时间
      return new Date(obj.getTime());
    }

    let copy = Array.isArray(obj) ? [] : {};

    for (let key in obj) {
      if (obj.hasOwnProperty(key)) {
        copy[key] = DeepCopyUtils.deepCopy(obj[key]);
      }
    }

    return copy;
  }
}

/*
    小李api工具
*/
export class apiUtils {
  /**
   * 动态参数
   * 同时还处理下拉框的全选
   * @param {obj} params - 参数对象
   * 筛出非空
   */
  static filterParams(params) {
    if (!params || typeof params !== "object") {
      return {}; // 返回一个空对象
    }

    let filteredEntries = [];

    Object.entries(params).forEach(([key, value]) => {
      // console.log("value",value)
      // 如果属性值是数组
      if (Array.isArray(value)) {
        // let queryString = [];
        // value = value.filter(item => item !== undefined && item !== null && item !== "");
        // value.forEach(item => queryString.push(`${key}=${encodeURIComponent(item)}`));
        if (value[0] === "SELECTALL") {
        } else {
          filteredEntries.push([key, value]);
          value.forEach(item => {
            console.log(item)
          })
        }
      } else if (value !== undefined && value !== null && value !== "") {
        // 如果属性值不是数组，只需检查其是否为 undefined、null 或空字符串
        if (value === "SELECTALL") {
          // 如果是下拉框的全选忽略这个参数
        } else {
          filteredEntries.push([key, value]);
        }
      }
    });

    return Object.fromEntries(filteredEntries);
  }

  /**
   * 动态参数生成查询字符串
   * @param {obj} params - 参数对象
   * @returns {string} 查询字符串
   */
  static toQueryString(params) {
    let queryString = [];

    Object.entries(params).forEach(([key, value]) => {
      // 如果属性值是数组
      if (Array.isArray(value)) {
        // 过滤数组中的 undefined、null 和空字符串
        value = value.filter(item => item !== undefined && item !== null && item !== "");
        // 为数组的每一项添加一个键值对
        value.forEach(item => queryString.push(`${key}=${encodeURIComponent(item)}`));
      } else if (value !== undefined && value !== null && value !== "") {
        // 如果属性值不是数组，只需检查其是否为 undefined、null 或空字符串
        queryString.push(`${key}=${value}`);
      }
    });

    return queryString.join('&');
  }
}

/*
    小李date工具
*/
export class dateUtils {
  /**
   * 格式化日期对象为指定格式的字符串
   * @param {Date} date - 要格式化的日期对象
   * @param {string} formatString - 日期格式字符串，包含占位符和分隔符
   * @returns {string} 格式化后的日期字符串
   */
  static format(date, formatString) {
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const hours = date.getHours();
    const minutes = date.getMinutes();
    const seconds = date.getSeconds();

    const formatMap = {
      YYYY: year,
      YY: year.toString().slice(-2),
      MM: this.padZero(month),
      M: month,
      DD: this.padZero(day),
      D: day,
      HH: this.padZero(hours),
      H: hours,
      mm: this.padZero(minutes),
      m: minutes,
      ss: this.padZero(seconds),
      s: seconds
    };

    let formattedDate = formatString;
    for (const key in formatMap) {
      formattedDate = formattedDate.replace(key, formatMap[key]);
    }

    return formattedDate;
  }

  /**
   * 在数字小于 10 的情况下，在前面补零
   * @param {number} value - 要进行补零操作的数字
   * @returns {string} 补零后的字符串
   */
  static padZero(value) {
    return value.toString().padStart(2, "0");
  }
}

/*
    小李树结构工具
*/
export class treeUtils {
  /**
   * 生成树状结构
   * @param {Array} originalArray - 原始数组，包含节点对象
   * @param {string} [parentKey='parentId'] - 父节点属性名称
   * @param {string} [idKey='id'] - 节点 ID 属性名称
   * @param {string} [childrenKey='children'] - 子节点数组属性名称
   * @returns {Array} 树状结构数组
   */
  static generateTree(
    originalArray,
    parentKey = "parentId",
    idKey = "id",
    childrenKey = "children"
  ) {
    const treeMap = {};
    const tree = [];

    // 构建映射表
    for (const item of originalArray) {
      const itemId = item[idKey];
      const parentId = item[parentKey];

      // 创建节点
      const newNode = {...item, [childrenKey]: []};

      // 将节点添加到映射表中
      if (treeMap[itemId]) {
        newNode[childrenKey] = treeMap[itemId][childrenKey]; // 保留已有的子节点
      }
      treeMap[itemId] = newNode;

      // 将根节点添加到树结构中
      if (parentId === null || parentId === undefined) {
        tree.push(newNode);
      } else {
        // 将非根节点添加到父节点的子节点列表中
        if (!treeMap[parentId]) {
          treeMap[parentId] = {[childrenKey]: []};
        }
        treeMap[parentId][childrenKey].push(newNode);
      }
    }

    return tree;
  }

  /**
   * 将树拉平成一维数组
   * @param {Array} tree - 树状结构数组
   * @param {string} [childrenKey='children'] - 子节点数组属性名称
   * @returns {Array} 一维数组
   */
  static flattenTree(tree, childrenKey = "children") {
    const flatArray = [];

    function flatten(node) {
      flatArray.push(node);

      if (node[childrenKey] && node[childrenKey].length > 0) {
        for (const child of node[childrenKey]) {
          flatten(child);
        }
      }
    }

    for (const node of tree) {
      flatten(node);
    }

    return flatArray;
  }
}

/*
    小李前端查询工具
*/

export class searchUtils {
  /**
   * 在给定数据中进行模糊查询
   * @param {Array} data - 要查询的数据数组
   * @param {string} query - 查询关键字
   * @param {string} property - 要模糊查询的属性名
   * @returns {Array} - 匹配查询条件的结果数组
   */
  static fuzzySearch(data, query, property) {
    const results = data.filter(item => {
      const value = item[property]; // 获取指定属性的值
      if (value && typeof value === "string") {
        const lowerCaseValue = value.toLowerCase();
        const lowerCaseQuery = query.toLowerCase();
        return lowerCaseValue.includes(lowerCaseQuery); // 忽略大小写，检查属性值是否包含查询字符串
      }
      return false;
    });

    // console.log(results, "res");
    return results || [];
  }
}

/*
    小李数组工具
*/

// 数组分组和还原
export class ReduceUtils {
  /**
   * 根据指定的键对数组进行分组
   * @param {Array} array 要分组的数组
   * @param {string} key 分组的键
   * @returns {Array} 分组后的数组，每个元素包含键值和对应的分组数组
   */
  static groupByKey(array, key) {
    return Object.entries(
      array.reduce((result, item) => {
        const keyValue = item[key];

        // 如果分组键值不存在，则创建一个空数组作为初始值
        if (!result[keyValue]) {
          result[keyValue] = [];
        }

        // 将当前元素添加到对应的分组数组中
        result[keyValue].push(item);

        return result;
      }, {})
    ).map(([key, value]) => ({key, value}));
  }

  /**
   * 还原分组后的对象为数组
   * @param {Array} groups 分组后的数组
   * @returns {Array} 还原后的数组
   */
  static flattenGroups(groups) {
    return groups.reduce((result, group) => {
      // 将每个分组数组连接到结果数组中
      return result.concat(group.value);
    }, []);
  }
}

/*
    小李递归工具
*/
export class RecursiveUtils {
  /**
   * 在一个可能包含嵌套数组的数组中递归地查找具有特定键值对的对象。
   *
   * @param {Array} array - 要搜索的数组，可以包含嵌套数组。
   * @param {string} key - 要匹配的对象键。
   * @param {*} value - 与键相对应的值，用于查找匹配的对象。
   * @returns {Object|undefined} - 如果找到具有指定键值对的对象，则返回该对象；否则返回undefined。
   *
   * @example
   * // 假设有一个包含嵌套数组的数组
   * const nestedArray = [
   *   { id: 1, name: 'Alice' },
   *   [
   *     { id: 2, name: 'Bob' },
   *     { id: 3, name: 'Charlie' }
   *   ],
   *   { id: 4, name: 'David' }
   * ];
   *
   * // 查找 id 为 3 的对象
   * const foundObject = RecursiveUtils.findObjectInArray(nestedArray, 'id', 3);
   * console.log(foundObject); // 输出: { id: 3, name: 'Charlie' }
   */
  static findObjectInArray(array, key, value) {
    if (!Array.isArray(array) || typeof key !== 'string') {
      return undefined;
    }

    for (const element of array) {
      // 如果元素是对象并且匹配，返回该对象
      if (element[key] === value) {
        return element;
      }

      // 如果元素是数组，递归搜索这个数组
      if (Array.isArray(element)) {
        const result = this.findObjectInArray(element, key, value);
        if (result) {
          return result;
        }
      }
    }

    // 如果没有找到，返回undefined
    return undefined;
  }
}
