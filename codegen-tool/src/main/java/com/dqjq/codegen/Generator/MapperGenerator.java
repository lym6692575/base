package com.dqjq.codegen.Generator;

import com.dqjq.codegen.CodegenConfig;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapper 接口生成器
 * <p>
 * 负责生成实体转换 Mapper 接口。
 * </p>
 */
@Getter
public class MapperGenerator extends BaseGenerator {

    private Path mapperPath;

    public MapperGenerator(CodegenConfig cfg) {
        super(cfg);
    }

    @Override
    public void generate() throws IOException {
        Path baseOut = getBaseOutputDir();
        String basePackagePath = cfg.packageBase.replace('.', '/');
        String modulePath = baseOut + "/" + basePackagePath + "/" + cfg.module;
        Path mapperDir = projectRoot.resolve(modulePath + "/mapper");
        
        ensureDirectory(mapperDir);
        
        this.mapperPath = mapperDir.resolve(cfg.entityName + "Mapper.java");

        String mapperPkg = cfg.packageBase + "." + cfg.module + ".mapper";
        String dtoPkg = cfg.packageBase + "." + cfg.module + ".dto";
        String entityPkg = cfg.packageBase + "." + cfg.module + ".entity";

        Map<String, Object> data = new HashMap<>();
        data.put("package", mapperPkg);
        data.put("dtoPackage", dtoPkg);
        data.put("entityPackage", entityPkg);
        data.put("entityName", cfg.entityName);
        data.put("dtoName", cfg.entityName + "Dto");

        renderFreemarker("mapper.ftl", data, mapperPath);
    }

}
