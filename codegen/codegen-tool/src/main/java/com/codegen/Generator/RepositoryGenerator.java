package com.codegen.Generator;

import src.main.java.com.codegen.CodegenConfig;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Repository 接口生成器
 * <p>
 * 负责生成 JPA Repository 接口。
 * </p>
 */
@Getter
public class RepositoryGenerator extends BaseGenerator {

    private Path repoPath;

    public RepositoryGenerator(CodegenConfig cfg) {
        super(cfg);
    }

    @Override
    public void generate() throws IOException {
        Path baseOut = getBaseOutputDir();
        String basePackagePath = cfg.packageBase.replace('.', '/');
        String modulePath = baseOut + "/" + basePackagePath + "/" + cfg.module;
        Path repoDir = projectRoot.resolve(modulePath + "/repository");
        
        ensureDirectory(repoDir);
        
        this.repoPath = repoDir.resolve(cfg.entityName + "Repo.java");

        String repoPkg = cfg.packageBase + "." + cfg.module + ".repository";
        String entityPkg = cfg.packageBase + "." + cfg.module + ".entity";

        Map<String, Object> data = new HashMap<>();
        data.put("package", repoPkg);
        data.put("entityPackage", entityPkg);
        data.put("entityName", cfg.entityName);
        data.put("idType", cfg.idType);

        renderFreemarker("repository.ftl", data, repoPath);
    }

}
