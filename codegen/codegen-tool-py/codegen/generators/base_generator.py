#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
生成器基类
定义生成器的通用接口和功能
"""

from pathlib import Path
import sqlite3
from jinja2 import Environment, FileSystemLoader, BaseLoader, TemplateNotFound

class SQLiteLoader(BaseLoader):
    """SQLite数据库模板加载器"""
    
    def __init__(self, db_path):
        self.db_path = db_path

    def get_source(self, environment, template):
        try:
            # 如果 template 是一个 ID（数字字符串），则按 ID 查询
            # 如果是名称，则按名称查询
            conn = sqlite3.connect(self.db_path)
            cursor = conn.cursor()
            
            if template.isdigit():
                cursor.execute(
                    "SELECT content, updated_at FROM templates WHERE id = ?",
                    (int(template),)
                )
            else:
                cursor.execute(
                    "SELECT content, updated_at FROM templates WHERE name = ?",
                    (template,)
                )
            
            row = cursor.fetchone()
            conn.close()
            
            if row is None:
                raise TemplateNotFound(template)
                
            source = row[0]
            # 简单的缓存失效检查：始终重新加载（或者可以基于version）
            return source, template, lambda: True
        except Exception as e:
            raise TemplateNotFound(template) from e

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
        # 数据库路径
        db_path = Path(__file__).parent.parent / 'dataBase' / 'codegen.db'
        
        if not db_path.exists():
            raise FileNotFoundError(f"Database not found at {db_path}")

        return Environment(
            loader=SQLiteLoader(str(db_path)),
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
