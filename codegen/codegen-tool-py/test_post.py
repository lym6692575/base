#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试POST /generate接口
"""

import requests
import json

# 测试数据
config = {
    "packageBase": "com.example.demo.app",
    "module": "pwlpz",
    "entityName": "TestEntity",
    "idType": "String",
    "mapping": "TABLE",
    "tableName": "test_table",
    "outputDir": "./output",
    "fields": [
        {
            "name": "id",
            "type": "String",
            "column": "ID",
            "id": True,
            "label": "主键"
        },
        {
            "name": "name",
            "type": "String",
            "column": "NAME",
            "label": "名称"
        }
    ]
}

# 发送POST请求
try:
    response = requests.post(
        "http://localhost:8000/generate",
        headers={"Content-Type": "application/json"},
        json={"config": config}
    )
    
    print(f"状态码: {response.status_code}")
    print("响应内容:")
    print(json.dumps(response.json(), ensure_ascii=False, indent=2))
    
except Exception as e:
    print(f"请求失败: {str(e)}")
