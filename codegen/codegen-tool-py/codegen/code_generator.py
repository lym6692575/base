#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
代码生成器核心类
负责协调各个生成器的工作
"""

from pathlib import Path
from codegen.generators.entity_generator import EntityGenerator
from codegen.generators.dto_generator import DtoGenerator
from codegen.generators.mapper_generator import MapperGenerator
from codegen.generators.repository_generator import RepositoryGenerator
from codegen.generators.service_generator import ServiceGenerator
from codegen.generators.service_impl_generator import ServiceImplGenerator

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
        self.templates_dir = config_dict.get('templatesDir', '')
        
        # 解析字段定义
        self.fields = self._parse_fields(config_dict.get('fields', ''))
        
        # 初始化生成器
        self.generators = []
        self._init_generators()
    
    def _parse_fields(self, fields_str):
        """
        解析字段定义字符串
        
        Args:
            fields_str: 字段定义字符串，格式为 "name:type:column:id:label;name2:type2:column2:id2:label2"
            
        Returns:
            字段定义列表
        """
        fields = []
        if not fields_str:
            return fields
        
        for field_part in fields_str.split(';'):
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
    
    def _init_generators(self):
        """初始化各种生成器"""
        # 实体生成器
        self.generators.append(EntityGenerator(self))
        
        # DTO生成器
        self.generators.append(DtoGenerator(self))
        
        # Mapper生成器
        self.generators.append(MapperGenerator(self))
        
        # Repository生成器
        self.generators.append(RepositoryGenerator(self))
        
        # Service生成器
        self.generators.append(ServiceGenerator(self))
        
        # ServiceImpl生成器
        self.generators.append(ServiceImplGenerator(self))
    
    def generate(self):
        """
        执行代码生成
        """
        for generator in self.generators:
            try:
                generator.generate()
                # 保存主实体路径
                if isinstance(generator, EntityGenerator):
                    self.main_entity_path = generator.output_path
            except Exception as e:
                print(f"生成 {generator.__class__.__name__} 时出错: {str(e)}")
                raise
    
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
