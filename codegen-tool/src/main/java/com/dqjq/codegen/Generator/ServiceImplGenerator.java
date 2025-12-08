package com.dqjq.codegen.Generator;

import com.dqjq.codegen.CodegenConfig;
import com.dqjq.codegen.CodegenFieldDef;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

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

        String template = readTemplate("serviceImpl.tpl");
        if (template.isEmpty()) {
            throw new IOException("无法加载 ServiceImpl 模板: serviceImpl.tpl");
        }

        String servicePkg = cfg.packageBase + "." + cfg.module + ".service";
        String implPkg = servicePkg + ".impl";
        String dtoPkg = cfg.packageBase + "." + cfg.module + ".dto";
        String entityPkg = cfg.packageBase + "." + cfg.module + ".entity";
        String repoPkg = cfg.packageBase + "." + cfg.module + ".repository";
        String mapperPkg = cfg.packageBase + "." + cfg.module + ".mapper";

        String predicates = renderPredicates(cfg.fields);

        String content = template
                .replace("{{package}}", implPkg)
                .replace("{{dtoPackage}}", dtoPkg)
                .replace("{{entityPackage}}", entityPkg)
                .replace("{{repoPackage}}", repoPkg)
                .replace("{{mapperPackage}}", mapperPkg)
                .replace("{{servicePackage}}", servicePkg)
                .replace("{{entityName}}", cfg.entityName)
                .replace("{{dtoName}}", cfg.entityName + "Dto")
                .replace("{{idType}}", cfg.idType)
                .replace("{{predicates}}", predicates);

        writeFile(implPath, content);
    }

    private String renderPredicates(List<CodegenFieldDef> fields) {
        StringBuilder sb = new StringBuilder();
        sb.append("        super.addPredicate(predicates, criteriaQuery, root, criteriaBuilder, key, value, params);\n");
        for (CodegenFieldDef f : fields) {
            if ("String".equals(f.type)) {
                sb.append("        if (\"").append(f.name).append("\".equals(key) && value instanceof String) {\n");
                sb.append("            predicates.add(criteriaBuilder.like(root.get(\"").append(f.name).append("\"), \"%\" + value + \"%\"));\n");
                sb.append("        }\n");
            } else {
                sb.append("        if (\"").append(f.name).append("\".equals(key) && value != null) {\n");
                sb.append("            predicates.add(criteriaBuilder.equal(root.get(\"").append(f.name).append("\"), value));\n");
                sb.append("        }\n");
            }
        }
        return sb.toString();
    }

}
