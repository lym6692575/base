# CodegenRunner 使用说明

## 运行
- 使用外部配置文件：`-Dcodegen.config=path/to/your.properties`
- Maven 运行示例：`mvn -DskipTests exec:java -Dcodegen.config=src/main/resources/codegen/sample.properties`
- 无参数运行时，如默认 `codegen.properties` 不存在，会弹出文件选择窗体以选择 `.properties` 配置文件。

## 配置项
- `packageBase`：基础包前缀，例如 `com.dqjq.myapp.oracle`
- `module`：业务模块包名，例如 `demo`
- `entityName`：实体/DTO/仓库/服务等统一名前缀，例如 `DemoGen`
- `idType`：主键类型，例如 `Long`
- `mapping`：`TABLE` 或 `SUBSELECT`
- `tableName`：当 `mapping=TABLE` 时的表名
- `subselect`：当 `mapping=SUBSELECT` 时的查询语句
- `fields`：字段定义，格式 `name:type:column:isId:label`，使用分号分隔多个
- `outputDir`：代码输出目录，默认 `src/main/java`
- `templatesDir`：自定义模板目录（可选），为空时使用内置模板
- `entityBaseClass`：实体基类全限定名，默认 `com.dqjq.base.lee.entity.BaseEntity`
- `dtoBaseClass`：DTO 基类全限定名，默认 `com.dqjq.base.lee.dto.BaseDto`

说明：
- 若不需要继承基础类，可将 `entityBaseClass` 或 `dtoBaseClass` 置空。
- 当配置了基础类时，生成的 `Entity` 与 `Dto` 会自动 `extends` 对应基类，并自动添加 `import`。

## 示例配置
```
packageBase=com.dqjq.myapp.oracle
module=demo
entityName=DemoGen
idType=Long
mapping=SUBSELECT
subselect=SELECT 1 AS ID, 'DEMO' AS NAME FROM DUAL
fields=id:Long:ID:true:ID;name:String:NAME:false:名称
outputDir=src/main/java
entityBaseClass=com.dqjq.base.lee.entity.BaseEntity
dtoBaseClass=com.dqjq.base.lee.dto.BaseDto
```

## 生成内容
- `entity`：`{{entityName}}Entity.java`
- `dto`：`{{entityName}}Dto.java`
- `repository`：`{{entityName}}Repo.java`
- `mapper`：`{{entityName}}Mapper.java`
- `service`：`{{entityName}}Service.java`
- `service/impl`：`{{entityName}}ServiceImpl.java`

## 模板与继承占位符
- 实体模板类声明：`public class {{entityName}}Entity{{entityExtends}}`
- DTO 模板类声明：`public class {{entityName}}Dto{{dtoExtends}}`
- 当配置基础类时，`{{entityExtends}}`/`{{dtoExtends}}` 会被替换为 ` extends BaseEntity` / ` extends BaseDto`（或你的自定义基类名）。
- 冲突策略：当目标文件已存在时会中止生成并抛出错误，提示全部冲突文件路径；请删除冲突文件或调整 `entityName/module/outputDir`。