import Mock from 'mockjs'

const url = {
    getSelectData: '/dev-api/api/getSelectData'
}

function getQueryParms(url, name){
    let parms = {}
    if (url.indexOf('?') != -1){
        let parmStr = url.substring(url.indexOf('?') + 1)
        let parmArray = parmStr.split('&')
        for (let i = 0; i < parmArray.length; i++){
            let parmKV = parmArray[i].split('=')
            parms[parmKV[0]] = parmKV[1]
        }
    }
    
    let value = parms[name]
    return (value == undefined || value == null) ? '' : value
}

module.exports = [
    //模拟查询下拉框数据
    Mock.mock(RegExp(url.getSelectData + ".*"), "get", (options) => {
        let url = options.url
        let q = getQueryParms(url, 'q')
        let querySize = getQueryParms(url, 'querySize')

        let mockRule = 'array|' + querySize
        let values = []
        let labels = []
        for (let i = 0; i < querySize; i++){
            values.push( Mock.Random.guid() )
            labels.push( q + i )
        }

        return Mock.mock({
            [mockRule]: [
                { 
                    "value|+1": values,
                    "label|+1": labels
                }
            ]
        })
    })
]