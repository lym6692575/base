#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
创建纯UTF-8格式的__init__.py文件，没有BOM
"""

import os

# 创建空的__init__.py文件
def create_init_file(path):
    with open(path, 'w', encoding='utf-8') as f:
        f.write('')

# 创建各个目录的__init__.py文件
create_init_file('__init__.py')
create_init_file('codegen/__init__.py')
create_init_file('codegen/generators/__init__.py')

print("__init__.py文件创建完成")
