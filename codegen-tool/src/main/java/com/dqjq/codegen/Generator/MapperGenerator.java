package com.dqjq.codegen.Generator;

import com.dqjq.codegen.CodegenConfig;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Path;

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

        String template = readTemplate("mapper.tpl");
        if (template.isEmpty()) {
            throw new IOException("无法加载 Mapper 模板: mapper.tpl");
        }

        String mapperPkg = cfg.packageBase + "." + cfg.module + ".mapper";
        String dtoPkg = cfg.packageBase + "." + cfg.module + ".dto";
        String entityPkg = cfg.packageBase + "." + cfg.module + ".entity";

        String content = template
                .replace("{{package}}", mapperPkg)
                .replace("{{dtoPackage}}", dtoPkg)
                .replace("{{entityPackage}}", entityPkg)
                .replace("{{entityName}}", cfg.entityName)
                .replace("{{dtoName}}", cfg.entityName + "Dto");

        writeFile(mapperPath, content);
    }

}
