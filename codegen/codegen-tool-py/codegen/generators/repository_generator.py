#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Repository生成器
生成Java Repository接口
"""

from pathlib import Path
from codegen.generators.base_generator import BaseGenerator

class RepositoryGenerator(BaseGenerator):
    """Repository生成器"""
    
    def generate(self):
        """
        生成Repository接口
        """
        # 准备上下文数据
        context = {
            'package': self.get_full_package('repository'),
            'entity_name': self.code_generator.entity_name,
            'id_type': self.code_generator.id_type,
            'entity_package': self.get_full_package('entity'),
            'repository_base_class': self.config_dict.get('repositoryBaseClass', ''),
            'has_base_class': bool(self.config_dict.get('repositoryBaseClass', ''))
        }
        
        # 渲染模板
        content = self.render_template('java/repository.j2', context)
        
        # 生成文件路径
        output_dir = self.code_generator.get_output_path('repository')
        self.output_path = output_dir / f"{self.code_generator.entity_name}Repository.java"
        
        # 写入文件
        self.write_file(self.output_path, content)
