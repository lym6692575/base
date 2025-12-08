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
 * DTO 类生成器
 * <p>
 * 负责生成数据传输对象 (DTO)。
 * </p>
 */
@Getter
public class DtoGenerator extends BaseGenerator {

    private Path dtoPath;

    public DtoGenerator(CodegenConfig cfg) {
        super(cfg);
    }

    @Override
    public void generate() throws IOException {
        Path baseOut = getBaseOutputDir();
        String basePackagePath = cfg.packageBase.replace('.', '/');
        String modulePath = baseOut + "/" + basePackagePath + "/" + cfg.module;
        Path dtoDir = projectRoot.resolve(modulePath + "/dto");
        
        ensureDirectory(dtoDir);
        
        this.dtoPath = dtoDir.resolve(cfg.entityName + "Dto.java");

        String template = readTemplate("dto.tpl");
        if (template.isEmpty()) {
            throw new IOException("无法加载 DTO 模板: dto.tpl");
        }

        String dtoPkg = cfg.packageBase + "." + cfg.module + ".dto";
        
        Set<String> imports = new HashSet<>();
        imports.add("lombok.Data");
        imports.add("lombok.EqualsAndHashCode");
        imports.add("io.swagger.annotations.ApiModelProperty");
        
        for (CodegenFieldDef f : cfg.fields) {
            if ("LocalDateTime".equals(f.type)) imports.add("java.time.LocalDateTime");
            if ("BigDecimal".equals(f.type)) imports.add("java.math.BigDecimal");
        }
        
        String dtoExtends = "";
        if (cfg.dtoBaseClass != null && !cfg.dtoBaseClass.isEmpty()) {
            imports.add(cfg.dtoBaseClass);
            dtoExtends = " extends " + simpleClassName(cfg.dtoBaseClass);
        }

        String fieldsContent = renderFields(cfg.fields);

        String content = template
                .replace("{{package}}", dtoPkg)
                .replace("{{imports}}", renderImports(imports))
                .replace("{{entityName}}", cfg.entityName)
                .replace("{{fields}}", fieldsContent)
                .replace("{{dtoExtends}}", dtoExtends);

        writeFile(dtoPath, content);
    }

    private String renderFields(List<CodegenFieldDef> fields) {
        StringBuilder sb = new StringBuilder();
        for (CodegenFieldDef f : fields) {
            sb.append("    @io.swagger.annotations.ApiModelProperty(value = \"").append(f.label).append("\")\n");
            sb.append("    private ").append(f.type).append(" ").append(f.name).append(";\n\n");
        }
        return sb.toString();
    }

}
