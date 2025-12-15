# 代码生成器数据库说明

## 数据库概述

本目录包含代码生成器的SQLite数据库，用于存储代码生成配置和字段信息。

## 数据库文件

- `codegen.db` - SQLite数据库文件
- `init_db.py` - 数据库初始化脚本，用于创建表结构并导入codegen.json数据

## 表结构

### 1. config表

存储代码生成的主配置信息。

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `id` | INTEGER | 主键，自增 |
| `package_base` | TEXT | 基础包名，如：com.example.demo.app |
| `module` | TEXT | 模块名，如：pwlpz |
| `entity_name` | TEXT | 实体名称，如：MonTrgtGen |
| `id_type` | TEXT | 主键类型，如：String |
| `mapping` | TEXT | 映射类型，TABLE或SUBSELECT |
| `table_name` | TEXT | 表名，如：test |
| `subselect` | TEXT | 子查询语句，如：SELECT * FROM test |
| `output_dir` | TEXT | 输出目录，如：c:/个人代码/base/demo/src/main/java |
| `templates_dir` | TEXT | 模板目录，如：./custom_templates |
| `entity_base_class` | TEXT | 实体基类，如：com.example.BaseEntity |
| `dto_base_class` | TEXT | DTO基类，如：com.example.BaseDto |
| `mapper_base_class` | TEXT | Mapper基类，如：com.example.BaseMapper |
| `service_base_class` | TEXT | Service基类，如：com.example.BaseService |
| `service_impl_base_class` | TEXT | ServiceImpl基类，如：com.example.BaseServiceImpl |
| `created_at` | TIMESTAMP | 创建时间，默认当前时间 |

### 2. fields表

存储字段配置信息，与config表关联。

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `id` | INTEGER | 主键，自增 |
| `config_id` | INTEGER | 关联的config表id |
| `name` | TEXT | 字段名称，如：trgtName |
| `type` | TEXT | 字段类型，如：String |
| `column_name` | TEXT | 数据库列名，如：TRGT_NAME |
| `is_id` | BOOLEAN | 是否为主键，true/false |
| `label` | TEXT | 字段标签，如：trgtName |

## 初始化数据库

运行以下命令初始化数据库：

```bash
python init_db.py
```

该脚本会：
1. 创建必要的表结构
2. 从项目根目录的codegen.json文件导入配置
3. 将配置和字段信息存储到数据库中

## 使用示例

### 查询所有配置

```python
import sqlite3

conn = sqlite3.connect('codegen.db')
cursor = conn.cursor()

cursor.execute('SELECT * FROM config')
configs = cursor.fetchall()

for config in configs:
    print(f"ID: {config[0]}, Entity: {config[3]}")

conn.close()
```

### 查询特定配置的字段

```python
import sqlite3

conn = sqlite3.connect('codegen.db')
cursor = conn.cursor()

# 查询ID为1的配置的所有字段
cursor.execute('SELECT * FROM fields WHERE config_id = ?', (1,))
fields = cursor.fetchall()

for field in fields:
    print(f"字段: {field[2]}, 类型: {field[3]}")

conn.close()
```

## 扩展建议

1. 添加配置的更新和删除功能
2. 支持从数据库读取配置生成代码
3. 添加配置版本管理
4. 支持配置的导出和导入
5. 添加Web界面管理配置
