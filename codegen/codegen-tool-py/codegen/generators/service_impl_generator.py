#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
ServiceImpl生成器
生成Java ServiceImpl实现类
"""

from pathlib import Path
from codegen.generators.base_generator import BaseGenerator

class ServiceImplGenerator(BaseGenerator):
    """ServiceImpl生成器"""
    
    def generate(self):
        """
        生成ServiceImpl实现类
        """
        # 准备上下文数据
        context = {
            'package': self.get_full_package('service.impl'),
            'entity_name': self.code_generator.entity_name,
            'id_type': self.code_generator.id_type,
            'entity_package': self.get_full_package('entity'),
            'dto_package': self.get_full_package('dto'),
            'service_package': self.get_full_package('service'),
            'repository_package': self.get_full_package('repository'),
            'mapper_package': self.get_full_package('mapper'),
            'service_impl_base_class': self.config_dict.get('serviceImplBaseClass', ''),
            'has_base_class': bool(self.config_dict.get('serviceImplBaseClass', ''))
        }
        
        # 渲染模板
        content = self.render_template('java/serviceImpl.j2', context)
        
        # 生成文件路径
        output_dir = self.code_generator.get_output_path('service/impl')
        self.output_path = output_dir / f"{self.code_generator.entity_name}ServiceImpl.java"
        
        # 写入文件
        self.write_file(self.output_path, content)
