#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Mapper生成器
生成Java Mapper接口
"""

from pathlib import Path
from codegen.generators.base_generator import BaseGenerator

class MapperGenerator(BaseGenerator):
    """Mapper生成器"""
    
    def generate(self):
        """
        生成Mapper接口
        """
        # 准备上下文数据
        context = {
            'package': self.get_full_package('mapper'),
            'entity_name': self.code_generator.entity_name,
            'id_type': self.code_generator.id_type,
            'entity_package': self.get_full_package('entity'),
            'mapper_base_class': self.config_dict.get('mapperBaseClass', ''),
            'has_base_class': bool(self.config_dict.get('mapperBaseClass', ''))
        }
        
        # 渲染模板
        content = self.render_template('java/mapper.j2', context)
        
        # 生成文件路径
        output_dir = self.code_generator.get_output_path('mapper')
        self.output_path = output_dir / f"{self.code_generator.entity_name}Mapper.java"
        
        # 写入文件
        self.write_file(self.output_path, content)
