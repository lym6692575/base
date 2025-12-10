package com.dqjq.codegen.Generator;

import com.dqjq.codegen.CodegenConfig;
import com.dqjq.codegen.CodegenFieldDef;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service 实现类生成器
 * <p>
 * 负责生成业务逻辑实现类。
 * </p>
 */
@Getter
public class ServiceImplGenerator extends BaseGenerator {

    private Path implPath;

    public ServiceImplGenerator(CodegenConfig cfg) {
        super(cfg);
    }

    @Override
    public void generate() throws IOException {
        Path baseOut = getBaseOutputDir();
        String basePackagePath = cfg.packageBase.replace('.', '/');
        String modulePath = baseOut + "/" + basePackagePath + "/" + cfg.module;
        Path implDir = projectRoot.resolve(modulePath + "/service/impl");
        
        ensureDirectory(implDir);
        
        this.implPath = implDir.resolve(cfg.entityName + "ServiceImpl.java");

        String servicePkg = cfg.packageBase + "." + cfg.module + ".service";
        String implPkg = servicePkg + ".impl";
        String dtoPkg = cfg.packageBase + "." + cfg.module + ".dto";
        String entityPkg = cfg.packageBase + "." + cfg.module + ".entity";
        String repoPkg = cfg.packageBase + "." + cfg.module + ".repository";
        String mapperPkg = cfg.packageBase + "." + cfg.module + ".mapper";

        Map<String, Object> data = new HashMap<>();
        data.put("package", implPkg);
        data.put("dtoPackage", dtoPkg);
        data.put("entityPackage", entityPkg);
        data.put("repoPackage", repoPkg);
        data.put("mapperPackage", mapperPkg);
        data.put("servicePackage", servicePkg);
        data.put("entityName", cfg.entityName);
        data.put("dtoName", cfg.entityName + "Dto");
        data.put("idType", cfg.idType);
        data.put("fields", cfg.fields);

        renderFreemarker("serviceImpl.ftl", data, implPath);
    }
}
