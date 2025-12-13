# Code Generator Tool - Python Version

Python版本的代码生成器，用于生成Java代码，包括Entity、DTO、Mapper、Repository和Service等。

## 功能特性

- ✅ 支持生成多种Java代码：Entity、DTO、Mapper、Repository、Service、ServiceImpl
- ✅ 支持JSON和Properties配置文件
- ✅ 基于Jinja2模板引擎，易于扩展
- ✅ 提供FastAPI接口，支持HTTP调用
- ✅ 支持命令行和批处理调用
- ✅ 轻量级，无需Java虚拟机

## 技术栈

- **Python** >= 3.8
- **FastAPI** - 用于提供HTTP接口
- **Jinja2** - 用于代码模板渲染
- **uvicorn** - ASGI服务器，用于运行FastAPI应用

## 安装说明

### 1. 克隆项目

```bash
git clone https://github.com/codegen-tool-py.git
cd codegen-tool-py
```

### 2. 安装依赖

#### 使用pip安装

```bash
pip install -r requirements.txt
```

#### 使用pip install -e（开发模式）

```bash
pip install -e .
```

## 使用方法

### 1. 命令行使用

#### 基本用法

```bash
# 使用默认配置文件（codegen.json或codegen.properties）
python codegen_runner.py

# 使用指定配置文件
python codegen_runner.py /path/to/config.json
```

#### 使用批处理脚本（Windows）

```bash
# 双击运行或命令行执行
run-codegen.bat

# 使用指定配置文件
run-codegen.bat /path/to/config.json
```

### 2. HTTP接口使用

#### 启动服务

```bash
# 启动FastAPI服务
python main.py

# 或使用uvicorn命令
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

服务将在 `http://localhost:8000` 上运行。

#### API文档

- **Swagger UI**: http://localhost:8000/docs
- **ReDoc**: http://localhost:8000/redoc

#### 接口列表

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/` | 返回helloWorld |
| POST | `/generate` | 调用代码生成器生成代码 |

#### 调用示例

```bash
# GET /
curl http://localhost:8000/

# POST /generate
curl -X POST -H "Content-Type: application/json" -d '{"config": {...}}' http://localhost:8000/generate
```

### 3. 配置文件示例

#### JSON格式

```json
{
  "packageBase": "com.example.demo.app",
  "module": "pwlpz",
  "entityName": "MonTrgtGen",
  "idType": "String",
  "mapping": "TABLE",
  "tableName": "test",
  "outputDir": "./output",
  "fields": [
    {
      "name": "trgtName",
      "type": "String",
      "column": "TRGT_NAME",
      "label": "trgtName"
    },
    {
      "name": "plantName",
      "type": "String",
      "column": "PLANT_NAME",
      "id": false,
      "label": "plantName"
    }
  ]
}
```

## 项目结构

```
codegen-tool-py/
├── codegen/              # 核心代码生成逻辑
│   ├── generators/       # 各种类型的代码生成器
│   ├── templates/        # 代码模板
│   ├── code_generator.py # 代码生成器核心
│   └── config_loader.py  # 配置加载器
├── codegen_runner.py     # 命令行入口
├── main.py               # FastAPI应用入口
├── run-codegen.bat       # Windows批处理脚本
├── test_post.py          # API测试脚本
├── README.md             # 项目说明文档
├── pyproject.toml        # 项目配置文件
└── requirements.txt      # 依赖列表
```

## 开发指南

### 1. 扩展模板

模板文件位于 `codegen/templates/java/` 目录下，使用Jinja2语法。

### 2. 添加新的生成器

1. 在 `codegen/generators/` 目录下创建新的生成器类，继承自 `BaseGenerator`
2. 实现 `generate()` 方法
3. 在 `codegen/code_generator.py` 中添加新生成器的初始化

### 3. 测试

```bash
# 运行API测试
python test_post.py

# 运行命令行测试
python codegen_runner.py test_config.json
```

## 配置项说明

| 配置项 | 类型 | 说明 | 示例值 |
|--------|------|------|--------|
| packageBase | String | 基础包名 | com.example.demo.app |
| module | String | 模块名 | pwlpz |
| entityName | String | 实体名称 | MonTrgtGen |
| idType | String | 主键类型 | String |
| mapping | String | 映射类型 | TABLE或SUBSELECT |
| tableName | String | 表名 | test |
| subselect | String | 子查询语句 | SELECT * FROM test |
| outputDir | String | 输出目录 | ./output |
| templatesDir | String | 模板目录 | ./custom_templates |
| entityBaseClass | String | 实体基类 | com.example.BaseEntity |
| dtoBaseClass | String | DTO基类 | com.example.BaseDto |
| mapperBaseClass | String | Mapper基类 | com.example.BaseMapper |
| serviceBaseClass | String | Service基类 | com.example.BaseService |
| serviceImplBaseClass | String | ServiceImpl基类 | com.example.BaseServiceImpl |
| fields | Array | 字段列表 | [{"name": "id", "type": "String", ...}] |

## 字段配置说明

| 字段 | 类型 | 说明 | 示例值 |
|------|------|------|--------|
| name | String | 字段名称 | trgtName |
| type | String | 字段类型 | String |
| column | String | 数据库列名 | TRGT_NAME |
| id | Boolean | 是否为主键 | true |
| label | String | 字段标签 | 目标名称 |

## License

MIT License

## 贡献

欢迎提交Issue和Pull Request！

## 联系我们

如有问题或建议，请通过GitHub Issues与我们联系。
