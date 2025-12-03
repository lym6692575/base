# 项目说明

该仓库已整理为聚合工程 + 子模块结构，便于多模块共享嵌入式数据库与构建阶段代码生成。

## 模块结构
- h2-server：轻量服务，启动 H2 TCP Server（端口 `9092`，数据目录 `./h2-server/data`）
- codegen-tool：代码生成器，在 `generate-sources` 阶段写入示例代码至 `generated-sources/src/main/java`
- src：业务测试主目录，提供独立运行示例入口 `com.example.demo.DemoMain`

## 快速开始
1. 启动 H2 TCP Server（必须先启动）
   - `mvn -pl h2-server spring-boot:run`
   - 日志会显示 `tcp://localhost:9092`，数据库文件位于仓库根目录 `./data`
2. 运行业务示例
   - 方式一：在 IDE 中运行 `src/main/java/com/example/demo/DemoMain.java`
   - 方式二：命令行运行 demo 模块
     - `mvn -pl demo exec:java`
3. 连接 H2 数据库（供各独立工具或测试使用）
   - JDBC URL：`jdbc:h2:tcp://localhost/base;MODE=MySQL;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE`
   - 用户名：`sa`；密码：留空

## 配置说明
- H2 TCP Server 参数：`h2-server/src/main/java/com/example/h2server/H2ServerApplication.java`
  - 端口：`9092`
  - 基目录：`./h2-server/data`

## 代码生成工具
- 运行生成器（在构建阶段写入示例代码）：
  - `mvn -pl codegen-tool generate-sources`
- 生成输出目录：`generated-sources/src/main/java`
- app 模块会在编译前将该目录加入源码路径（`build-helper-maven-plugin`）

## 环境切换
- 默认使用 `h2` Profile（嵌入式 + 控制台）
- 切换到 MySQL（便于 Navicat 管理）：
  - 启动参数添加：`--spring.profiles.active=dev`（或 `test`、`prod`）
  - 按环境文件中的数据源连接即可

## 常见问题
- Navicat 不支持 H2：如需图形化管理，建议用 DBeaver/DataGrip，或切到 MySQL Profile。
- 无法连接数据库：确保先启动 `h2-server`，并使用 `jdbc:h2:tcp://localhost/base` 连接。

## 目录约定
- 数据文件：`./data/base.mv.db`
- 生成代码：`generated-sources/src/main/java`
- 主应用源码：父级 `src/main/java`、`src/main/resources`、`src/test/java`
