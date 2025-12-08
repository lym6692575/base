package com.dqjq.codegen.Generator;

import com.dqjq.codegen.CodegenConfig;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Path;

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

        String template = readTemplate("repository.tpl");
        if (template.isEmpty()) {
            throw new IOException("无法加载 Repository 模板: repository.tpl");
        }

        String repoPkg = cfg.packageBase + "." + cfg.module + ".repository";
        String entityPkg = cfg.packageBase + "." + cfg.module + ".entity";

        String content = template
                .replace("{{package}}", repoPkg)
                .replace("{{entityPackage}}", entityPkg)
                .replace("{{entityName}}", cfg.entityName)
                .replace("{{idType}}", cfg.idType);

        writeFile(repoPath, content);
    }

}
