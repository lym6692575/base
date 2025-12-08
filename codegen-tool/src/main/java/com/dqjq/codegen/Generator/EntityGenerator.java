package com.dqjq.codegen.Generator;

import com.dqjq.codegen.CodegenConfig;
import com.dqjq.codegen.CodegenFieldDef;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 实体类生成器
 * <p>
 * 负责生成 JPA 实体类 (.java)。
 * 支持 TABLE 和 SUBSELECT 两种映射策略。
 * </p>
 */
@Getter
public class EntityGenerator extends BaseGenerator {

    private Path entityPath;

    public EntityGenerator(CodegenConfig cfg) {
        super(cfg);
    }

    @Override
    public void generate() throws IOException {
        Path baseOut = getBaseOutputDir();
        String basePackagePath = cfg.packageBase.replace('.', '/');
        String modulePath = baseOut + "/" + basePackagePath + "/" + cfg.module;
        Path entityDir = projectRoot.resolve(modulePath + "/entity");
        
        ensureDirectory(entityDir);
        
        this.entityPath = entityDir.resolve(cfg.entityName + "Entity.java");
        
        // 检查文件是否已存在，避免覆盖（父类 CodeGenerator 之前是在 generate 开始时统一检查，
        // 这里我们可以在 generate 时检查，或者让调用者先检查。
        // 为了保持原有逻辑的一致性，这里我们假设调用者会处理冲突，或者我们在 writeFile 中处理）
        // BaseGenerator.writeFile 会抛出异常如果文件存在。

        String templateName = cfg.mapping.equalsIgnoreCase("TABLE") ? "entity-table.tpl" : "entity-subselect.tpl";
        String template = readTemplate(templateName);
        
        if (template.isEmpty()) {
            throw new IOException("无法加载实体模板: " + templateName);
        }

        String entityPkg = cfg.packageBase + "." + cfg.module + ".entity";
        
        Set<String> imports = new HashSet<>();
        imports.add("lombok.Data");
        imports.add("javax.persistence.Entity");
        imports.add("javax.persistence.Id");
        imports.add("javax.persistence.Column");
        imports.add("io.swagger.annotations.ApiModelProperty");
        
        if (cfg.mapping.equalsIgnoreCase("SUBSELECT")) {
            imports.add("org.hibernate.annotations.Immutable");
            imports.add("org.hibernate.annotations.Subselect");
        } else {
            imports.add("javax.persistence.Table");
        }
        
        for (CodegenFieldDef f : cfg.fields) {
            if ("LocalDateTime".equals(f.type)) imports.add("java.time.LocalDateTime");
            if ("BigDecimal".equals(f.type)) imports.add("java.math.BigDecimal");
        }
        
        String entityExtends = "";
        if (cfg.entityBaseClass != null && !cfg.entityBaseClass.isEmpty()) {
            imports.add(cfg.entityBaseClass);
            entityExtends = " extends " + simpleClassName(cfg.entityBaseClass);
        }

        String fieldsContent = renderFields(cfg.fields);

        String content = template
                .replace("{{package}}", entityPkg)
                .replace("{{imports}}", renderImports(imports))
                .replace("{{entityName}}", cfg.entityName)
                .replace("{{tableName}}", cfg.tableName)
                .replace("{{subselect}}", cfg.subselect)
                .replace("{{fields}}", fieldsContent)
                .replace("{{entityExtends}}", entityExtends);

        writeFile(entityPath, content);
    }

    private String renderFields(List<CodegenFieldDef> fields) {
        StringBuilder sb = new StringBuilder();
        for (CodegenFieldDef f : fields) {
            sb.append("    @ApiModelProperty(value = \"").append(f.label).append("\")\n");
            if (f.id) {
                sb.append("    @Id\n");
            }
            sb.append("    @Column(name = \"").append(f.column).append("\")\n");
            sb.append("    private ").append(f.type).append(" ").append(f.name).append(";\n\n");
        }
        return sb.toString();
    }

}
