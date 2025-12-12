package com.codegen.Generator;

import src.main.java.com.codegen.CodegenConfig;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

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

        String servicePkg = cfg.packageBase + "." + cfg.module + ".service";
        String dtoPkg = cfg.packageBase + "." + cfg.module + ".dto";
        String entityPkg = cfg.packageBase + "." + cfg.module + ".entity";

        Map<String, Object> data = new HashMap<>();
        data.put("package", servicePkg);
        data.put("dtoPackage", dtoPkg);
        data.put("entityPackage", entityPkg);
        data.put("entityName", cfg.entityName);
        data.put("dtoName", cfg.entityName + "Dto");
        data.put("idType", cfg.idType);
        data.put("serviceBaseClass", cfg.serviceBaseClass);
        data.put("serviceImplBaseClass", cfg.serviceImplBaseClass);

        renderFreemarker("service.ftl", data, servicePath);
    }

}
