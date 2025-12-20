#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
初始化SQLite数据库，将codegen.json数据导入到数据库中
"""

import sqlite3
import json
import os
from pathlib import Path

# 默认模板数据
DEFAULT_TEMPLATES = [
    {
        'name': 'java_dto',
        'content': '''package {{ package }};

@Data
{% if has_base_class %}
@EqualsAndHashCode(callSuper = true)
{% else %}
@EqualsAndHashCode(callSuper = false)
{% endif %}
public class {{ entity_name }}Dto{% if has_base_class %} extends {{ dto_base_class }}{% endif %} {

{% for field in fields %}
    @ApiModelProperty(value="{{ field.label }}")
    private {{ field.type }} {{ field.name }};

{% endfor %}
}'''
    },
    {
        'name': 'java_entity_subselect',
        'content': '''package {{ package }};

@Entity
@Immutable
@Subselect("{{ subselect }}")
@Data
{% if has_base_class %}
@EqualsAndHashCode(callSuper = true)
{% endif %}
public class {{ entity_name }}{% if has_base_class %} extends {{ entity_base_class }}{% endif %} {

{% for field in fields %}
    @ApiModelProperty(value = "{{ field.label }}")
    {% if field.id %}
    @Id
    {% endif %}
    {% if field.column %}
    @Column(name = "{{ field.column }}")
    {% endif %}
    private {{ field.type }} {{ field.name }};

{% endfor %}
}'''
    },
    {
        'name': 'java_entity_table',
        'content': '''package {{ package }};

@Entity
@Table(name = "{{ table_name }}")
@Data
{% if has_base_class %}
@EqualsAndHashCode(callSuper = true)
{% endif %}
public class {{ entity_name }}{% if has_base_class %} extends {{ entity_base_class }}{% endif %} {

{% for field in fields %}
    @ApiModelProperty(value = "{{ field.label }}")
    {% if field.id %}
    @Id
    {% endif %}
    {% if field.column %}
    @Column(name = "{{ field.column }}")
    {% endif %}
    private {{ field.type }} {{ field.name }};

{% endfor %}
}'''
    },
    {
        'name': 'java_mapper',
        'content': '''package {{ package }};

import {{ entity_package }}.{{ entity_name }};
import org.springframework.stereotype.Component;

{% if has_base_class %}
/**
 * {{ entity_name }} Mapper
 * <p>
 * 继承 BaseMapper，自动获得基础的 Entity <-> DTO 转换能力。
 * 如有特殊转换逻辑，请在此类中重写 dtoToEntity 或 entityToDto 方法。
 * </p>
 */
@Component
public class {{ entity_name }}Mapper extends {{ mapper_base_class }} {
    
    public {{ entity_name }}Mapper() {
        super({{ entity_name }}.class);
    }

    /*
    @Override
    public {{ entity_name }} dtoToEntity({{ entity_name }}Dto dto) {
        // 1. 调用父类默认转换
        {{ entity_name }} entity = super.dtoToEntity(dto);
        // 2. 处理特殊逻辑
        if (entity != null) {
            // entity.setSomeField(convert(dto.getSomeField()));
        }
        return entity;
    }

    @Override
    public {{ entity_name }}Dto entityToDto({{ entity_name }} entity) {
        // 1. 调用父类默认转换
        {{ entity_name }}Dto dto = super.entityToDto(entity);
        // 2. 处理特殊逻辑
        if (dto != null) {
            // dto.setSomeField(convert(entity.getSomeField()));
        }
        return dto;
    }
    */
}
{% else %}
/**
 * {{ entity_name }} Mapper
 */
@Component
public interface {{ entity_name }}Mapper {
    
    // 可以在此定义自定义的映射方法
}
{% endif %}'''
    },
    {
        'name': 'java_repository',
        'content': '''package {{ package }};

import {{ entity_package }}.{{ entity_name }};
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

{% if has_base_class %}
@Repository
public interface {{ entity_name }}Repository extends {{ repository_base_class }}<{{ entity_name }}, {{ id_type }}> {
}
{% else %}
@Repository
public interface {{ entity_name }}Repository extends JpaRepository<{{ entity_name }}, {{ id_type }}> {
}
{% endif %}'''
    },
    {
        'name': 'java_service',
        'content': '''package {{ package }};

import {{ entity_package }}.{{ entity_name }};
import {{ dto_package }}.{{ entity_name }}Dto;

{% if has_base_class %}
public interface {{ entity_name }}Service extends {{ service_base_class }}<{{ entity_name }}, {{ entity_name }}Dto, {{ id_type }}> {
}
{% else %}
public interface {{ entity_name }} Service {
    // 可以在此定义服务方法
}
{% endif %}'''
    },
    {
        'name': 'java_service_impl',
        'content': '''package {{ package }};

import {{ entity_package }}.{{ entity_name }};
import {{ dto_package }}.{{ entity_name }}Dto;
import {{ service_package }}.{{ entity_name }}Service;
import {{ repository_package }}.{{ entity_name }}Repository;
import {{ mapper_package }}.{{ entity_name }}Mapper;
import org.springframework.stereotype.Service;

{% if has_base_class %}
@Service
public class {{ entity_name }}ServiceImpl extends {{ service_impl_base_class }}<{{ entity_name }}, {{ entity_name }}Dto, {{ id_type }}> implements {{ entity_name }}Service {
    private final {{ entity_name }}Repository repository;

    public {{ entity_name }}ServiceImpl({{ entity_name }}Repository repository, {{ entity_name }}Mapper mapper) {
        super(repository, mapper, {{ entity_name }}.class);
        this.repository = repository;
    }
}
{% else %}
@Service
public class {{ entity_name }}ServiceImpl implements {{ entity_name }}Service {
    private final {{ entity_name }}Repository repository;

    public {{ entity_name }}ServiceImpl({{ entity_name }}Repository repository) {
        this.repository = repository;
    }
    
    // 可以在此实现服务方法
}
{% endif %}'''
    }
]

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
            template_group TEXT DEFAULT 'default',
            scheme_id INTEGER,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
        ''')
        
        # 尝试添加 scheme_id 列（如果不存在）
        try:
            cursor.execute("ALTER TABLE config ADD COLUMN scheme_id INTEGER")
        except sqlite3.OperationalError:
            pass  # 列已存在

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

        # 创建模板表 (新结构)
        # 如果表存在，先删除旧表（注意：生产环境要小心，这里为了重构直接删除）
        cursor.execute("DROP TABLE IF EXISTS templates")
        
        cursor.execute('''
        CREATE TABLE templates (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT UNIQUE NOT NULL,
            content TEXT,
            description TEXT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
        ''')

        # 创建方案表
        cursor.execute('''
        CREATE TABLE IF NOT EXISTS schemes (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT UNIQUE NOT NULL,
            description TEXT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
        ''')

        # 创建方案明细表
        cursor.execute('''
        CREATE TABLE IF NOT EXISTS scheme_items (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            scheme_id INTEGER NOT NULL,
            template_id INTEGER NOT NULL,
            output_filename_pattern TEXT NOT NULL,
            output_sub_package TEXT NOT NULL,
            is_enabled BOOLEAN DEFAULT 1,
            FOREIGN KEY (scheme_id) REFERENCES schemes (id),
            FOREIGN KEY (template_id) REFERENCES templates (id)
        )
        ''')
        
        # 插入默认模板
        print("Inserting default templates...")
        for tpl in DEFAULT_TEMPLATES:
            cursor.execute('''
            INSERT OR IGNORE INTO templates (name, content) VALUES (?, ?)
            ''', (tpl['name'], tpl['content']))
            
            # 更新内容（如果是已存在的记录）
            cursor.execute('''
            UPDATE templates SET content = ? WHERE name = ?
            ''', (tpl['content'], tpl['name']))
            
        conn.commit()

        # 插入默认方案 (Default Java Scheme)
        print("Inserting default scheme...")
        cursor.execute("INSERT OR IGNORE INTO schemes (name, description) VALUES (?, ?)", 
                      ('default_java', 'Default Java Spring Boot Scheme'))
        scheme_id = cursor.execute("SELECT id FROM schemes WHERE name = ?", ('default_java',)).fetchone()[0]

        # 插入默认方案明细
        # 获取模板ID
        tpl_map = {}
        for tpl in DEFAULT_TEMPLATES:
            tid = cursor.execute("SELECT id FROM templates WHERE name = ?", (tpl['name'],)).fetchone()[0]
            tpl_map[tpl['name']] = tid

        scheme_items = [
            (scheme_id, tpl_map['java_dto'], '{EntityName}Dto.java', 'dto'),
            (scheme_id, tpl_map['java_entity_table'], '{EntityName}.java', 'entity'), # 注意：这里默认用了table，后面逻辑需要处理 subselect 的情况，或者创建两个 scheme
            (scheme_id, tpl_map['java_mapper'], '{EntityName}Mapper.java', 'mapper'),
            (scheme_id, tpl_map['java_repository'], '{EntityName}Repository.java', 'repository'),
            (scheme_id, tpl_map['java_service'], '{EntityName}Service.java', 'service'),
            (scheme_id, tpl_map['java_service_impl'], '{EntityName}ServiceImpl.java', 'service.impl')
        ]
        
        # 先清空该 scheme 的 items (防止重复)
        cursor.execute("DELETE FROM scheme_items WHERE scheme_id = ?", (scheme_id,))
        
        for item in scheme_items:
            cursor.execute('''
            INSERT INTO scheme_items (scheme_id, template_id, output_filename_pattern, output_sub_package)
            VALUES (?, ?, ?, ?)
            ''', item)

        # 读取codegen.json数据
        if codegen_json_path.exists():
            with open(codegen_json_path, 'r', encoding='utf-8') as f:
                config_data = json.load(f)
            
            # 插入主配置
            cursor.execute('''
            INSERT INTO config (
                package_base, module, entity_name, id_type, mapping, table_name, subselect, 
                output_dir, templates_dir, entity_base_class, dto_base_class, mapper_base_class, 
                service_base_class, service_impl_base_class, scheme_id
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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
                config_data.get('serviceImplBaseClass'),
                scheme_id # 关联默认 scheme
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
            
            print(f"Imported config '{config_data.get('entityName')}' with scheme_id={scheme_id}")

        # 提交事务
        conn.commit()
        print(f"Database initialized successfully at {db_path}")
        
    except Exception as e:
        print(f"Error initializing database: {str(e)}")
        conn.rollback()
        import traceback
        traceback.print_exc()
    finally:
        # 关闭数据库连接
        conn.close()

if __name__ == "__main__":
    init_database()
