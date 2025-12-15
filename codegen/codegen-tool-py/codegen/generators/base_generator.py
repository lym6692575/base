#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
生成器基类
定义生成器的通用接口和功能
"""

from pathlib import Path
from jinja2 import Environment, FileSystemLoader

class BaseGenerator:
    """生成器基类"""
    
    def __init__(self, code_generator):
        """
        初始化生成器
        
        Args:
            code_generator: 代码生成器实例
        """
        self.code_generator = code_generator
        self.config_dict = code_generator.config_dict
        self.output_path = None
        
        # 初始化Jinja2环境
        self.jinja_env = self._init_jinja_env()
    
    def _init_jinja_env(self):
        """
        初始化Jinja2环境
        
        Returns:
            Jinja2环境对象
        """
        # 默认模板目录
        default_templates_dir = Path(__file__).parent.parent / 'templates'
        
        # 如果指定了模板目录，则使用指定的目录
        templates_dir = self.config_dict.get('templatesDir', '')
        if templates_dir:
            template_path = Path(templates_dir)
        else:
            template_path = default_templates_dir
        
        return Environment(
            loader=FileSystemLoader(template_path),
            trim_blocks=True,
            lstrip_blocks=True,
            autoescape=False
        )
    
    def generate(self):
        """
        生成代码
        子类需要实现此方法
        """
        raise NotImplementedError("子类必须实现generate方法")
    
    def get_template(self, template_name):
        """
        获取模板
        
        Args:
            template_name: 模板名称
            
        Returns:
            模板对象
        """
        return self.jinja_env.get_template(template_name)
    
    def render_template(self, template_name, context):
        """
        渲染模板
        
        Args:
            template_name: 模板名称
            context: 上下文数据
            
        Returns:
            渲染后的代码
        """
        template = self.get_template(template_name)
        return template.render(context)
    
    def write_file(self, file_path, content):
        """
        写入文件
        
        Args:
            file_path: 文件路径
            content: 文件内容
            
        Raises:
            FileExistsError: 如果文件已存在
        """
        # 确保目录存在
        file_path.parent.mkdir(parents=True, exist_ok=True)
        
        # 检查文件是否已存在
        if file_path.exists():
            error_msg = f"文件已存在，无法生成: {file_path.absolute()}"
            print(f"错误: {error_msg}")
            raise FileExistsError(error_msg)
        
        # 写入文件
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        
        print(f"生成文件: {file_path.absolute()}")
    
    def get_full_package(self, subpackage):
        """
        获取完整包名
        
        Args:
            subpackage: 子包名
            
        Returns:
            完整包名
        """
        parts = [self.code_generator.package_base]
        if self.code_generator.module:
            parts.append(self.code_generator.module)
        parts.append(subpackage)
        return '.'.join(parts)
