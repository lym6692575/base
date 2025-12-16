# 代码生成器项目

这是一个基于 Python FastAPI + Vue 3 + Tauri 构建的代码生成器项目，支持通过 Web 界面配置和生成代码。

## 项目结构

### 后端模块
- `codegen-tool-py`：Python FastAPI 后端服务
  - 提供配置管理 API（增删改查）
  - 代码生成核心逻辑
  - SQLite 数据库存储配置
  - 端口：8000

### 前端模块
- `codegen-tauri`：Vue 3 + Tauri 前端应用
  - 配置管理界面
  - 支持 JSON 编辑器和表格编辑两种模式
  - 代码生成触发
  - 生成日志展示

## 快速开始

### 1. 启动后端服务

```bash
cd codegen-tool-py
python -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

### 2. 启动前端开发环境

```bash
cd codegen-tauri
npm install
npm run dev
```

### 3. 访问应用

- 前端应用：默认在浏览器中自动打开
- 后端 API 文档：http://localhost:8000/docs

## 功能说明

### 配置管理
- 支持创建、编辑、删除配置
- 提供两种编辑模式：
  - JSON 编辑器：适合熟悉 JSON 格式的用户
  - 表格编辑：直观易用，适合快速配置
- 配置包括：
  - 包基础路径
  - 模块名称
  - 实体名称
  - ID 类型
  - 映射类型（TABLE/SUBSELECT）
  - 表名/子查询
  - 输出目录
  - 基础类配置
  - 字段配置

### 代码生成
- 基于配置生成 Java 代码
- 支持生成多种类型的文件（实体类、DTO、Mapper、Service 等）
- 实时展示生成日志
- 支持从数据库加载配置

## 数据库

- 使用 SQLite 数据库存储配置
- 数据库文件：`codegen-tool-py/codegen/dataBase/codegen.db`
- 表结构：
  - `config`：存储主配置
  - `fields`：存储字段配置

## 技术栈

### 后端
- Python 3.x
- FastAPI
- SQLite
- uvicorn

### 前端
- Vue 3
- Element Plus
- Tauri
- Vite

## 开发说明

### 后端开发
- 配置加载逻辑：`codegen-tool-py/codegen/config_loader.py`
- 代码生成逻辑：`codegen-tool-py/codegen/code_generator.py`
- API 接口：`codegen-tool-py/main.py`

### 前端开发
- 配置管理页面：`codegen-tauri/src/views/ConfigManagerView.vue`
- 配置表单组件：`codegen-tauri/src/components/ConfigForm.vue`
- API 服务：`codegen-tauri/src/services/configApi.js`
- 状态管理：`codegen-tauri/src/store/config.js`

## 构建生产版本

### 前端构建

```bash
cd codegen-tauri
npm run build
```

### Tauri 应用构建

```bash
cd codegen-tauri
npm run tauri build
```

## 接口说明

### 配置管理 API

| 接口 | 方法 | 描述 |
|------|------|------|
| `/configs` | GET | 获取所有配置 |
| `/configs/{id}` | GET | 获取特定配置 |
| `/configs` | POST | 创建新配置 |
| `/configs/{id}` | PUT | 更新配置 |
| `/configs/{id}` | DELETE | 删除配置 |

### 代码生成 API

| 接口 | 方法 | 描述 |
|------|------|------|
| `/generate/{id}` | POST | 使用指定配置生成代码 |

## 常见问题

### 跨域问题
- 后端已配置 CORS 中间件，允许所有源访问
- 配置文件：`codegen-tool-py/main.py` 中的 `CORSMiddleware` 配置

### 生成代码失败
- 检查配置是否完整
- 查看生成日志获取详细错误信息
- 确保输出目录存在且有写入权限

## 版本历史

- 初始版本：基础配置管理和代码生成功能
- v1.0.0：添加 JSON 编辑器和表格编辑模式切换
- v1.1.0：优化 UI 布局和交互体验

## 贡献指南

欢迎提交 Issue 和 Pull Request！

## 许可证

MIT License
