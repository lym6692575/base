# CodeGen Vue 前端项目

## 项目简介
CodeGen Vue 是一个基于 Vue 3 + Element Plus 开发的代码生成前端管理界面，用于与 codegen-tool 后端服务进行交互，实现代码生成任务的可视化管理和配置。

## 技术栈
- **前端框架**: Vue 3
- **UI 组件库**: Element Plus
- **路由管理**: Vue Router
- **状态管理**: Pinia
- **构建工具**: Vite
- **图标库**: @element-plus/icons-vue

## 项目结构
```
src/
├── assets/             # 静态资源文件
├── components/         # Vue 组件
├── router/             # 路由配置
├── services/           # API 服务层
├── store/              # 状态管理
├── views/              # 页面视图
│   ├── HomeView.vue    # 首页
│   ├── CodegenView.vue # 代码生成管理页面
│   └── AboutView.vue   # 关于页面
├── App.vue             # 根组件
└── main.js             # 入口文件
```

## 安装和运行

### 安装依赖
```bash
npm install
```

### 开发模式运行
```bash
npm run dev
```

### 构建生产版本
```bash
npm run build
```

### 预览生产版本
```bash
npm run preview
```

## 功能介绍

### 1. 首页
展示项目介绍和快速导航功能。

### 2. 代码生成管理
- **配置表单**: 提供代码生成的配置选项
  - 模板选择
  - 包路径配置
  - 模块名和实体名设置
  - 字段定义（名称、类型、是否必填等）

- **生成日志**: 显示代码生成过程中的实时日志信息

- **历史记录**: 展示代码生成历史记录，支持删除和清空操作

### 3. 关于页面
展示项目的功能特性和版本信息。

## API 服务层
在 `src/services/codegenApi.js` 中实现了与后端的交互接口：

- `generateCode(config)`: 调用后端生成代码
- `getGenerateHistory()`: 获取生成历史记录
- `deleteGenerateHistory(id)`: 删除指定的历史记录
- `clearGenerateHistory()`: 清空所有历史记录
- `getTemplates()`: 获取可用的代码模板
- `exportConfigToJson(config)`: 导出配置为 JSON
- `importConfigFromJson(json)`: 从 JSON 导入配置

## 状态管理
使用 Pinia 进行状态管理，在 `src/store/codegen.js` 中定义了：

- **State**: 生成历史、模板列表、当前任务、生成状态
- **Getters**: 成功/失败记录数量、启用模板列表
- **Actions**: 生成代码、加载模板、加载历史记录、删除/清空历史记录

## 路由配置
在 `src/router/index.js` 中配置了路由：

- `/`: 首页
- `/codegen`: 代码生成管理页面
- `/about`: 关于页面

## 注意事项

1. 确保 codegen-tool 后端服务已启动并正常运行
2. 如需修改 API 接口地址，请在 `src/services/codegenApi.js` 中进行配置
3. 开发过程中使用了本地存储来模拟数据，实际部署时请替换为真实的后端接口
4. 项目使用了 Element Plus 的图标库，如遇到图标显示问题，请检查 `@element-plus/icons-vue` 包是否正确安装

## 开发说明

### 组件开发规范
- 使用 Vue 3 的 Composition API
- 组件命名采用 PascalCase 格式
- 组件样式采用 scoped CSS

### 代码提交规范
- 提交信息使用英文或中文，清晰描述修改内容
- 提交前确保代码已通过构建检查

## License
MIT License
