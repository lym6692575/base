#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Service生成器
生成Java Service接口
"""

from pathlib import Path
from codegen.generators.base_generator import BaseGenerator

class ServiceGenerator(BaseGenerator):
    """Service生成器"""
    
    def generate(self):
        """
        生成Service接口
        """
        # 准备上下文数据
        context = {
            'package': self.get_full_package('service'),
            'entity_name': self.code_generator.entity_name,
            'id_type': self.code_generator.id_type,
            'entity_package': self.get_full_package('entity'),
            'dto_package': self.get_full_package('dto'),
            'service_base_class': self.config_dict.get('serviceBaseClass', ''),
            'has_base_class': bool(self.config_dict.get('serviceBaseClass', ''))
        }
        
        # 渲染模板
        content = self.render_template('java/service.j2', context)
        
        # 生成文件路径
        output_dir = self.code_generator.get_output_path('service')
        self.output_path = output_dir / f"{self.code_generator.entity_name}Service.java"
        
        # 写入文件
        self.write_file(self.output_path, content)
