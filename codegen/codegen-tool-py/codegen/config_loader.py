#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
配置加载器
负责从文件系统加载生成器配置。
支持 .properties 和 .json 两种格式。
"""

import json
from pathlib import Path

class ConfigLoader:
    """配置加载器类"""
    
    def load(self, path):
        """
        加载配置文件
        
        根据文件后缀自动判断加载方式。
        若以 .json 结尾则按 JSON 格式解析，否则按 Properties 格式解析。
        
        Args:
            path: 配置文件路径（绝对路径或相对路径）
            
        Returns:
            加载后的配置字典
            
        Raises:
            Exception: 如果读取文件失败
        """
        if path and path.lower().endswith('.json'):
            config_dict = self.load_json(path)
            if config_dict:
                return config_dict
        return self.load_properties(path)
    
    def load_properties(self, path):
        """
        加载 .properties 格式的配置文件
        
        Args:
            path: 文件路径
            
        Returns:
            配置字典
            
        Raises:
            Exception: 如果读取文件失败
        """
        config_dict = {}
        
        if not path:
            return config_dict
        
        file_path = Path(path)
        if file_path.exists():
            # 使用utf-8编码读取properties文件
            with open(file_path, 'r', encoding='utf-8') as f:
                # 这里使用简单的解析方式
                for line in f.readlines():
                    line = line.strip()
                    if line and not line.startswith('#'):
                        if '=' in line:
                            key, value = line.split('=', 1)
                            config_dict[key.strip()] = value.strip()
            return config_dict
        
        return config_dict
    
    def load_json(self, path):
        """
        加载 .json 格式的配置文件
        
        Args:
            path: JSON 文件路径
            
        Returns:
            配置字典
            
        Raises:
            Exception: 如果读取文件失败
        """
        config_dict = {}
        
        if not path:
            return config_dict
        
        file_path = Path(path)
        if file_path.exists():
            # 确保使用UTF-8编码读取文件
            with open(file_path, 'r', encoding='utf-8') as f:
                data = json.load(f)
                
            for key, value in data.items():
                # 特殊处理 fields 数组
                if key == 'fields' and isinstance(value, list):
                    parts = []
                    for field in value:
                        if isinstance(field, dict):
                            name = str(field.get('name', ''))
                            type_ = str(field.get('type', 'String'))
                            column = str(field.get('column', name))
                            id_ = str(field.get('id', False))
                            label = str(field.get('label', name))
                            parts.append(f"{name}:{type_}:{column}:{id_}:{label}")
                    config_dict['fields'] = ';'.join(parts)
                else:
                    config_dict[key] = str(value)
        
        return config_dict
