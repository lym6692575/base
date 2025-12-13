#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
DTO生成器
生成Java DTO类
"""

from pathlib import Path
from codegen.generators.base_generator import BaseGenerator

class DtoGenerator(BaseGenerator):
    """DTO生成器"""
    
    def generate(self):
        """
        生成DTO类
        """
        # 准备上下文数据
        context = {
            'package': self.get_full_package('dto'),
            'entity_name': self.code_generator.entity_name,
            'fields': self.code_generator.fields,
            'dto_base_class': self.config_dict.get('dtoBaseClass', ''),
            'has_base_class': bool(self.config_dict.get('dtoBaseClass', ''))
        }
        
        # 渲染模板
        content = self.render_template('java/dto.j2', context)
        
        # 生成文件路径
        output_dir = self.code_generator.get_output_path('dto')
        self.output_path = output_dir / f"{self.code_generator.entity_name}Dto.java"
        
        # 写入文件
        self.write_file(self.output_path, content)
