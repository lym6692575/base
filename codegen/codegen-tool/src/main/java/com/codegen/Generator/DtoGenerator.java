package com.codegen.Generator;


import com.codegen.CodegenFieldDef;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

    public DtoGenerator(src.main.java.com.codegen.CodegenConfig cfg) {
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

        String dtoPkg = cfg.packageBase + "." + cfg.module + ".dto";
        
        Set<String> imports = new HashSet<>();
        imports.add("lombok.*");
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

        String entityExtends = null;
        if (cfg.entityBaseClass != null && !cfg.entityBaseClass.isEmpty()) {
            imports.add(cfg.entityBaseClass);
            entityExtends = simpleClassName(cfg.entityBaseClass);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("package", dtoPkg);
        data.put("imports", imports);
        data.put("entityName", cfg.entityName);
        data.put("dtoExtends", dtoExtends);
        data.put("fields", cfg.fields);
        data.put("entityExtends", entityExtends);

        renderFreemarker("dto.ftl", data, dtoPath);
    }

}
