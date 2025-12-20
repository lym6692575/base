#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
代码生成器核心类
负责协调各个生成器的工作
"""

from pathlib import Path
import sqlite3
import json
from codegen.generators.base_generator import BaseGenerator

import re

class SchemeGenerator(BaseGenerator):
    """基于Scheme的通用生成器"""
    
    def __init__(self, code_generator, scheme_item, scheme_variables):
        super().__init__(code_generator)
        self.scheme_item = scheme_item
        self.scheme_variables = scheme_variables
        
        # 解析 Item 级变量 (并处理引用)
        self.item_variables = self._parse_item_variables(scheme_item.get('variables'), scheme_variables)
        
    def _parse_item_variables(self, variables_json, scheme_variables):
        """
        解析 Item 变量，支持 ${GlobalVar} 引用
        """
        if not variables_json:
            return {}
        
        try:
            vars_dict = json.loads(variables_json) if isinstance(variables_json, str) else variables_json
            result = {}
            
            for key, value in vars_dict.items():
                if isinstance(value, str) and value.startswith('${') and value.endswith('}'):
                    # 这是一个引用，例如 ${DefaultBase}
                    ref_key = value[2:-1]
                    # 从 Scheme 全局变量中查找，找不到则保留原值或为空
                    result[key] = scheme_variables.get(ref_key, value)
                else:
                    # 普通值
                    result[key] = value
            return result
        except Exception as e:
            print(f"Error parsing item variables: {e}")
            return {}

    def generate(self):
        # 1. 准备上下文
        # 核心上下文 (运行时)
        context = {
            'package': self.get_full_package(self.scheme_item['output_sub_package']),
            'entity_name': self.code_generator.entity_name,
            'id_type': self.code_generator.id_type,
            'table_name': self.code_generator.table_name,
            'subselect': self.code_generator.subselect,
            'mapping': self.code_generator.mapping,
            'fields': self.code_generator.fields,
            
            # 辅助变量
            'entity_package': self.get_full_package('entity'),
            'dto_package': self.get_full_package('dto'),
            'mapper_package': self.get_full_package('mapper'),
            'repository_package': self.get_full_package('repository'),
            'service_package': self.get_full_package('service')
        }
        
        # 2. 注入变量
        # 优先级：Config (覆盖) > Item Variables (映射后) > Scheme Variables (全局)
        
        # 先注入全局变量作为默认值 (可选，为了兼容旧模板可以直接用全局变量名)
        context.update(self.scheme_variables)
        
        # 再注入 Item 映射后的变量 (这是核心推荐用法)
        context.update(self.item_variables)
        
        # 最后注入 Config 里的覆盖值 (如果有)
        # 这一步稍微复杂点，因为 Config 目前没有通用的 variables 字段，
        # 但如果以后加了，应该在这里合并。
        # 目前 Config 里只有 entityBaseClass 等旧字段，为了兼容性可以手动映射一下
        # 但为了推行新模式，建议这里只保留 Item Variables
        
        # 3. 渲染模板
        template_id = str(self.scheme_item['template_id'])
        content = self.render_template(template_id, context)
        
        # 4. 计算输出路径
        filename = self.scheme_item['output_filename_pattern'].replace('{EntityName}', self.code_generator.entity_name)
        output_dir = self.code_generator.get_output_path(self.scheme_item['output_sub_package'])
        self.output_path = output_dir / filename
        
        # 5. 写入文件
        self.write_file(self.output_path, content)
        
        return self.output_path

class CodeGenerator:
    """代码生成器核心类"""
    
    def __init__(self, config_dict):
        """
        初始化代码生成器
        
        Args:
            config_dict: 配置字典
        """
        self.config_dict = config_dict
        self.main_entity_path = None
        
        # 解析配置
        self.package_base = config_dict.get('packageBase', '')
        self.module = config_dict.get('module', '')
        self.entity_name = config_dict.get('entityName', '')
        self.id_type = config_dict.get('idType', 'String')
        self.mapping = config_dict.get('mapping', 'TABLE')
        self.table_name = config_dict.get('tableName', '')
        self.subselect = config_dict.get('subselect', '')
        self.output_dir = config_dict.get('outputDir', '.')
        self.scheme_id = config_dict.get('scheme_id')
        
        # 解析字段定义
        self.fields = self._parse_fields(config_dict.get('fields', ''))
        
    def _parse_fields(self, fields_input):
        """
        解析字段定义
        
        Args:
            fields_input: 字段定义，可以是列表或字符串
            
        Returns:
            字段定义列表
        """
        fields = []
        
        # 如果是列表格式，直接处理
        if isinstance(fields_input, list):
            for field in fields_input:
                if isinstance(field, dict):
                    fields.append({
                        'name': field.get('name', '').strip(),
                        'type': field.get('type', 'String').strip(),
                        'column': field.get('column', '').strip() or field.get('name', '').strip(),
                        'id': bool(field.get('id', False)),
                        'label': field.get('label', '').strip() or field.get('name', '').strip()
                    })
            return fields
        
        # 如果是字符串格式，保持原有逻辑
        if not fields_input:
            return fields
        
        for field_part in fields_input.split(';'):
            if not field_part:
                continue
            
            parts = field_part.split(':', 4)
            if len(parts) < 5:
                continue
            
            name, type_, column, id_, label = parts
            fields.append({
                'name': name.strip(),
                'type': type_.strip(),
                'column': column.strip(),
                'id': id_.strip().lower() == 'true',
                'label': label.strip()
            })
        
        return fields
    
    def generate(self):
        """
        执行代码生成 (基于 Scheme)
        """
        if not self.scheme_id:
            print("Warning: No scheme_id provided, skipping generation.")
            return

        # 1. 从数据库加载 Scheme Items 和 Scheme Variables
        db_path = Path(__file__).parent / 'dataBase' / 'codegen.db'
        conn = sqlite3.connect(db_path)
        conn.row_factory = sqlite3.Row # 允许通过列名访问
        cursor = conn.cursor()
        
        try:
            # 获取 Scheme 信息（包括变量）
            cursor.execute("SELECT variables FROM schemes WHERE id = ?", (self.scheme_id,))
            scheme_row = cursor.fetchone()
            scheme_variables = {}
            if scheme_row and scheme_row['variables']:
                try:
                    scheme_variables = json.loads(scheme_row['variables'])
                except json.JSONDecodeError:
                    print("Warning: Failed to parse scheme variables JSON")

            # 获取 Items
            cursor.execute(
                "SELECT * FROM scheme_items WHERE scheme_id = ? AND is_enabled = 1",
                (self.scheme_id,)
            )
            items = cursor.fetchall()
            
            if not items:
                print(f"Warning: No enabled items found for scheme_id {self.scheme_id}")
                return

            print(f"Found {len(items)} generation tasks in scheme.")

            # 2. 遍历执行生成
            for item in items:
                try:
                    # 将 sqlite3.Row 转换为字典
                    item_dict = dict(item)
                    generator = SchemeGenerator(self, item_dict, scheme_variables)
                    output_path = generator.generate()
                    
                    # 简单判断是否是主实体 (仅用于日志)
                    if 'entity' in item_dict['output_sub_package'].lower() and not 'dto' in item_dict['output_sub_package'].lower():
                        self.main_entity_path = output_path
                        
                except Exception as e:
                    print(f"Error generating item {item_dict.get('output_filename_pattern')}: {str(e)}")
                    # 此时可以选择抛出异常终止，或者继续生成其他文件
                    # raise e 
        finally:
            conn.close()
    
    def get_package_path(self, subpackage=''):
        """
        获取包的目录路径
        
        Args:
            subpackage: 子包名
            
        Returns:
            包的目录路径
        """
        package_parts = [self.package_base]
        if self.module:
            package_parts.append(self.module)
        if subpackage:
            package_parts.append(subpackage)
        
        package_path = '.'.join(package_parts).replace('.', '/')
        return package_path
    
    def get_output_path(self, subpackage=''):
        """
        获取输出路径
        
        Args:
            subpackage: 子包名
            
        Returns:
            输出路径
        """
        package_path = self.get_package_path(subpackage)
        output_path = Path(self.output_dir) / package_path
        
        # 确保目录存在
        output_path.mkdir(parents=True, exist_ok=True)
        
        return output_path
