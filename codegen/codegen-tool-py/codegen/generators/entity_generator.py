#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
实体生成器
生成Java实体类
"""

from pathlib import Path
from codegen.generators.base_generator import BaseGenerator

class EntityGenerator(BaseGenerator):
    """实体生成器"""
    
    def generate(self):
        """
        生成实体类
        """
        # 准备上下文数据
        context = {
            'package': self.get_full_package('entity'),
            'entity_name': self.code_generator.entity_name,
            'id_type': self.code_generator.id_type,
            'table_name': self.code_generator.table_name,
            'subselect': self.code_generator.subselect,
            'mapping': self.code_generator.mapping,
            'fields': self.code_generator.fields,
            'entity_base_class': self.config_dict.get('entityBaseClass', ''),
            'has_base_class': bool(self.config_dict.get('entityBaseClass', ''))
        }
        
        # 根据mapping类型选择不同的模板
        if self.code_generator.mapping == 'SUBSELECT':
            template_name = 'java/entity-subselect.j2'
        else:
            template_name = 'java/entity-table.j2'
        
        # 渲染模板
        content = self.render_template(template_name, context)
        
        # 生成文件路径
        output_dir = self.code_generator.get_output_path('entity')
        self.output_path = output_dir / f"{self.code_generator.entity_name}.java"
        
        # 写入文件
        self.write_file(self.output_path, content)
