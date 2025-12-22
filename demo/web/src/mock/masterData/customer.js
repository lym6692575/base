import Mock from "mockjs";

const url = {
  // getCustomersByExample: "/dev-api/md/customers/page/search/example",
  getCustomersByExampleAdd: "/dev-api/md/customers/page/search/example1"
};
Mock.setup({
  timeout: 4000 - 30000
});
function getQueryParms(url, name) {
  let parms = {};
  if (url.indexOf("?") != -1) {
    let parmStr = url.substring(url.indexOf("?") + 1);
    let parmArray = parmStr.split("&");
    for (let i = 0; i < parmArray.length; i++) {
      let parmKV = parmArray[i].split("=");
      parms[parmKV[0]] = parmKV[1];
    }
  }

  let value = parms[name];
  return value == undefined || value == null ? "" : value;
}

module.exports = [
  //模拟查询下拉框数据
  Mock.mock(RegExp(url.getCustomersByExample + ".*"), "get", options => {
    let url = options.url;
    let q = getQueryParms(url, "q");
    let querySize = getQueryParms(url, "querySize");

    let mockRule = "object|" + querySize;
    let values = [];
    let labels = [];
    for (let i = 0; i < querySize; i++) {
      values.push(Mock.Random.guid());
      labels.push(q + i);
    }

    return Mock.mock({
      [mockRule]: {
        pageNum: 1,
        pageSize: 5,
        totalSize: 10,
        totalPages: 2,
        elements: [
          {
            code: "6",
            name: "路人111甲",
            thisDate: 1611817382000,
            salesTo: null,
            text: null,
            country: null,
            sex: "2",
            age: "52",
            phone: "18888888888",
            nativePlace: "黑龙江省-鸡西市-XX区-XX小区",
            married: 1,
            status: 1,
            creator: "1",
            creatorName: "1",
            modifier: "1",
            modifierName: "1"
          },
          {
            code: "10",
            name: "路人戊",
            thisDate: 1611817382000,
            salesTo: null,
            text: null,
            country: null,
            sex: "2",
            age: "33",
            phone: "18888888888",
            nativePlace: "黑龙江省-鸡西市-XX区-XX小区",
            married: 1,
            status: 1,
            creator: "1",
            creatorName: "1",
            modifier: "1",
            modifierName: "1"
          },
          {
            code: "7",
            name: "路人乙",
            thisDate: 1611817382000,
            salesTo: null,
            text: null,
            country: null,
            sex: "2",
            age: "12",
            phone: "18888888888",
            nativePlace: "黑龙江省-鸡西市-XX区-XX小区",
            married: 1,
            status: 1,
            creator: "1",
            creatorName: "1",
            modifier: "1",
            modifierName: "1"
          },
          {
            code: "8",
            name: "路人丙",
            thisDate: 1611817382000,
            salesTo: null,
            text: null,
            country: null,
            sex: "2",
            age: "21",
            phone: "18888888888",
            nativePlace: "黑龙江省-鸡西市-XX区-XX小区",
            married: 1,
            status: 1,
            creator: "1",
            creatorName: "1",
            modifier: "1",
            modifierName: "1"
          },
          {
            code: "9",
            name: "路人丁",
            thisDate: 1611817382000,
            salesTo: null,
            text: null,
            country: null,
            sex: "2",
            age: "55",
            phone: "18888888888",
            nativePlace: "黑龙江省-鸡西市-XX区-XX小区",
            married: 1,
            status: 1,
            creator: "1",
            creatorName: "1",
            modifier: "1",
            modifierName: "1"
          }
        ]
      }
    });
  }),
  Mock.mock(RegExp(url.getCustomersByExampleAdd + ".*"), "get", options => {
    let url = options.url;
    let q = getQueryParms(url, "q");
    let querySize = getQueryParms(url, "querySize");

    let mockRule = "object|" + querySize;
    let values = [];
    let labels = [];
    for (let i = 0; i < querySize; i++) {
      values.push(Mock.Random.guid());
      labels.push(q + i);
    }

    return Mock.mock({
      [mockRule]: {
        pageNum: 1,
        pageSize: 5,
        totalSize: 10,
        totalPages: 2,
        elements: [
          { name: "小张三", age: 10, sex: 1, status: "1", education: "初中" }
        ]
      }
    });
  })
];
