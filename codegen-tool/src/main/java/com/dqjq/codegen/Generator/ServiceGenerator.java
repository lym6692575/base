package com.dqjq.codegen.Generator;

import com.dqjq.codegen.CodegenConfig;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Service 接口生成器
 * <p>
 * 负责生成业务逻辑接口。
 * </p>
 */
@Getter
public class ServiceGenerator extends BaseGenerator {

    private Path servicePath;

    public ServiceGenerator(CodegenConfig cfg) {
        super(cfg);
    }

    @Override
    public void generate() throws IOException {
        Path baseOut = getBaseOutputDir();
        String basePackagePath = cfg.packageBase.replace('.', '/');
        String modulePath = baseOut + "/" + basePackagePath + "/" + cfg.module;
        Path serviceDir = projectRoot.resolve(modulePath + "/service");
        
        ensureDirectory(serviceDir);
        
        this.servicePath = serviceDir.resolve(cfg.entityName + "Service.java");

        String template = readTemplate("service.tpl");
        if (template.isEmpty()) {
            throw new IOException("无法加载 Service 模板: service.tpl");
        }

        String servicePkg = cfg.packageBase + "." + cfg.module + ".service";
        String dtoPkg = cfg.packageBase + "." + cfg.module + ".dto";
        String entityPkg = cfg.packageBase + "." + cfg.module + ".entity";

        String content = template
                .replace("{{package}}", servicePkg)
                .replace("{{dtoPackage}}", dtoPkg)
                .replace("{{entityPackage}}", entityPkg)
                .replace("{{entityName}}", cfg.entityName)
                .replace("{{dtoName}}", cfg.entityName + "Dto")
                .replace("{{idType}}", cfg.idType);

        writeFile(servicePath, content);
    }

}
