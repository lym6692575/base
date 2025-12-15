#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
配置加载器
负责从文件系统或数据库加载生成器配置。
支持 .properties 和 .json 两种格式，以及数据库加载。
"""

import json
import sqlite3
from pathlib import Path

class ConfigLoader:
    """配置加载器类"""
    
    def __init__(self):
        self.db_path = Path(__file__).parent / "dataBase" / "codegen.db"
    
    def load(self, path=None, config_id=None):
        """
        加载配置
        
        优先级：
        1. 如果提供了config_id，从数据库加载
        2. 否则，从数据库加载默认配置（id=1）
        
        Args:
            path: 配置文件路径（已废弃，不再使用）
            config_id: 配置ID，从数据库加载时使用
            
        Returns:
            加载后的配置字典
            
        Raises:
            Exception: 如果读取数据库失败
        """
        # 1. 从数据库加载
        if config_id is not None:
            return self.load_from_db(config_id)
        
        # 2. 从数据库加载默认配置
        return self.load_from_db(1)
    
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
    
    def load_from_db(self, config_id):
        """
        从数据库加载配置
        
        Args:
            config_id: 配置ID
            
        Returns:
            配置字典
            
        Raises:
            Exception: 如果读取数据库失败
        """
        config_dict = {}
        
        if not self.db_path.exists():
            return config_dict
        
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        try:
            # 查询主配置
            cursor.execute('''
            SELECT 
                package_base, module, entity_name, id_type, mapping, table_name, subselect, 
                output_dir, templates_dir, entity_base_class, dto_base_class, mapper_base_class, 
                service_base_class, service_impl_base_class
            FROM config WHERE id = ?
            ''', (config_id,))
            
            config_row = cursor.fetchone()
            if not config_row:
                return config_dict
            
            # 填充主配置
            config_dict = {
                'packageBase': config_row[0],
                'module': config_row[1],
                'entityName': config_row[2],
                'idType': config_row[3],
                'mapping': config_row[4],
                'tableName': config_row[5],
                'subselect': config_row[6],
                'outputDir': config_row[7],
                'templatesDir': config_row[8],
                'entityBaseClass': config_row[9],
                'dtoBaseClass': config_row[10],
                'mapperBaseClass': config_row[11],
                'serviceBaseClass': config_row[12],
                'serviceImplBaseClass': config_row[13]
            }
            
            # 查询字段
            cursor.execute('''
            SELECT name, type, column_name, is_id, label
            FROM fields WHERE config_id = ?
            ''', (config_id,))
            
            fields = cursor.fetchall()
            parts = []
            for field in fields:
                name = field[0] or ''
                type_ = field[1] or 'String'
                column = field[2] or name
                id_ = str(field[3] or False)
                label = field[4] or name
                parts.append(f"{name}:{type_}:{column}:{id_}:{label}")
            
            config_dict['fields'] = ';'.join(parts)
            
        except Exception as e:
            print(f"Error loading from database: {str(e)}")
        finally:
            conn.close()
        
        return config_dict
    
    def save_to_db(self, config_dict):
        """
        保存配置到数据库
        
        Args:
            config_dict: 配置字典
            
        Returns:
            保存后的配置ID
        """
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        try:
            # 开始事务
            conn.execute('BEGIN TRANSACTION')
            
            # 插入主配置
            cursor.execute('''
            INSERT INTO config (
                package_base, module, entity_name, id_type, mapping, table_name, subselect, 
                output_dir, templates_dir, entity_base_class, dto_base_class, mapper_base_class, 
                service_base_class, service_impl_base_class
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ''', (
                config_dict.get('packageBase'),
                config_dict.get('module'),
                config_dict.get('entityName'),
                config_dict.get('idType'),
                config_dict.get('mapping'),
                config_dict.get('tableName'),
                config_dict.get('subselect'),
                config_dict.get('outputDir'),
                config_dict.get('templatesDir'),
                config_dict.get('entityBaseClass'),
                config_dict.get('dtoBaseClass'),
                config_dict.get('mapperBaseClass'),
                config_dict.get('serviceBaseClass'),
                config_dict.get('serviceImplBaseClass')
            ))
            
            config_id = cursor.lastrowid
            
            # 插入字段
            fields_str = config_dict.get('fields', '')
            for field_part in fields_str.split(';'):
                if not field_part:
                    continue
                
                parts = field_part.split(':', 4)
                if len(parts) < 5:
                    continue
                
                name, type_, column, id_, label = parts
                cursor.execute('''
                INSERT INTO fields (config_id, name, type, column_name, is_id, label)
                VALUES (?, ?, ?, ?, ?, ?)
                ''', (
                    config_id,
                    name.strip(),
                    type_.strip(),
                    column.strip(),
                    id_.strip().lower() == 'true',
                    label.strip()
                ))
            
            # 提交事务
            conn.commit()
            return config_id
        except Exception as e:
            conn.rollback()
            print(f"Error saving to database: {str(e)}")
            raise
        finally:
            conn.close()
    
    def get_all_configs(self):
        """
        获取所有配置
        
        Returns:
            配置列表
        """
        configs = []
        
        if not self.db_path.exists():
            return configs
        
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        try:
            cursor.execute('SELECT id, entity_name, module, created_at FROM config')
            config_rows = cursor.fetchall()
            
            for row in config_rows:
                configs.append({
                    'id': row[0],
                    'entityName': row[1],
                    'module': row[2],
                    'createdAt': row[3]
                })
        except Exception as e:
            print(f"Error getting all configs: {str(e)}")
        finally:
            conn.close()
        
        return configs
