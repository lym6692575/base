package com.dqjq.codegen.Generator;

import com.dqjq.codegen.CodegenConfig;
import com.dqjq.codegen.CodegenFieldDef;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 实体类生成器 (FreeMarker 版)
 * <p>
 * 负责生成 JPA 实体类 (.java)。
 * 优先尝试使用 .ftl 模板，如果不存在则回退到 .tpl 字符串替换模式（为了兼容旧代码）。
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
        
        // 准备数据模型
        String entityPkg = cfg.packageBase + "." + cfg.module + ".entity";
        
        Set<String> imports = new HashSet<>();
        imports.add("lombok.Data");
        imports.add("javax.persistence.*");
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
        
        String entityExtends = null;
        if (cfg.entityBaseClass != null && !cfg.entityBaseClass.isEmpty()) {
            imports.add(cfg.entityBaseClass);
            entityExtends = simpleClassName(cfg.entityBaseClass);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("package", entityPkg);
        data.put("imports", imports);
        data.put("entityName", cfg.entityName);
        data.put("tableName", cfg.tableName);
        data.put("subselect", cfg.subselect);
        data.put("fields", cfg.fields);
        data.put("entityExtends", entityExtends);

        // 尝试使用 FreeMarker 模板
        String ftlName = cfg.mapping.equalsIgnoreCase("TABLE") ? "entity-table.ftl" : "entity-subselect.ftl";
        
        // 如果 FreeMarker 模板存在（我们在 BaseGenerator 中初始化了配置），尝试渲染
        try {
            renderFreemarker(ftlName, data, entityPath);
            return; 
        } catch (IOException e) {
            // 捕获异常后，抛出运行时异常或重新抛出 IOException 以便上层捕获
            // 这里我们选择抛出 IOException，并在消息中包含原始错误信息
            // 只有当错误是“模板未找到”时，才考虑降级。
            // 但目前的 renderFreemarker 实现中，如果模板找不到是抛 TemplateNotFoundException (它是 IOException 的子类)
            // 为了简化，我们假设只要是 IOException 且不是“文件已存在”，都可能是模板问题。
            
            // 如果是文件已存在，直接抛出，不再尝试降级
            if (e.getMessage() != null && e.getMessage().contains("目标文件已存在")) {
                throw e;
            }
            
            // 如果是其他错误（如模板语法错），也应该抛出，不应降级（因为降级后的 tpl 可能也不对）
            // 只有“模板文件不存在”才应该降级。
            // 但由于 BaseGenerator.renderFreemarker 包装了异常，我们简单打印并尝试降级
            // 或者更严格：
            System.err.println("FreeMarker 模板渲染失败 (" + ftlName + "): " + e.getMessage());
            // 如果你想严格终止，可以取消下面的注释：
            throw e; 
        }
    }
}
