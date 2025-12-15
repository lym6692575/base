#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
初始化SQLite数据库，将codegen.json数据导入到数据库中
"""

import sqlite3
import json
import os
from pathlib import Path

def init_database():
    """
    初始化SQLite数据库
    """
    # 获取数据库文件路径
    db_path = Path(__file__).parent / "codegen.db"
    
    # 获取codegen.json文件路径
    codegen_json_path = Path(__file__).parent.parent.parent / "codegen.json"
    
    # 连接数据库（如果不存在则创建）
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    
    try:
        # 创建主配置表
        cursor.execute('''
        CREATE TABLE IF NOT EXISTS config (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            package_base TEXT,
            module TEXT,
            entity_name TEXT,
            id_type TEXT,
            mapping TEXT,
            table_name TEXT,
            subselect TEXT,
            output_dir TEXT,
            templates_dir TEXT,
            entity_base_class TEXT,
            dto_base_class TEXT,
            mapper_base_class TEXT,
            service_base_class TEXT,
            service_impl_base_class TEXT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
        ''')
        
        # 创建字段表
        cursor.execute('''
        CREATE TABLE IF NOT EXISTS fields (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            config_id INTEGER,
            name TEXT,
            type TEXT,
            column_name TEXT,
            is_id BOOLEAN,
            label TEXT,
            FOREIGN KEY (config_id) REFERENCES config (id)
        )
        ''')
        
        # 读取codegen.json数据
        if not codegen_json_path.exists():
            print(f"Error: {codegen_json_path} not found")
            return
        
        with open(codegen_json_path, 'r', encoding='utf-8') as f:
            config_data = json.load(f)
        
        # 插入主配置
        cursor.execute('''
        INSERT INTO config (
            package_base, module, entity_name, id_type, mapping, table_name, subselect, 
            output_dir, templates_dir, entity_base_class, dto_base_class, mapper_base_class, 
            service_base_class, service_impl_base_class
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ''', (
            config_data.get('packageBase'),
            config_data.get('module'),
            config_data.get('entityName'),
            config_data.get('idType'),
            config_data.get('mapping'),
            config_data.get('tableName'),
            config_data.get('subselect'),
            config_data.get('outputDir'),
            config_data.get('templatesDir'),
            config_data.get('entityBaseClass'),
            config_data.get('dtoBaseClass'),
            config_data.get('mapperBaseClass'),
            config_data.get('serviceBaseClass'),
            config_data.get('serviceImplBaseClass')
        ))
        
        # 获取插入的config_id
        config_id = cursor.lastrowid
        
        # 插入字段数据
        fields = config_data.get('fields', [])
        for field in fields:
            cursor.execute('''
            INSERT INTO fields (config_id, name, type, column_name, is_id, label)
            VALUES (?, ?, ?, ?, ?, ?)
            ''', (
                config_id,
                field.get('name'),
                field.get('type'),
                field.get('column'),
                field.get('id', False),
                field.get('label')
            ))
        
        # 提交事务
        conn.commit()
        print(f"Database initialized successfully at {db_path}")
        print(f"Imported {len(fields)} fields for config '{config_data.get('entityName')}'")
        
    except Exception as e:
        print(f"Error initializing database: {str(e)}")
        conn.rollback()
    finally:
        # 关闭数据库连接
        conn.close()

if __name__ == "__main__":
    init_database()
