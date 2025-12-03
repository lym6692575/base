package com.dqjq.codegen;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CodeGenerator {
    final CodegenConfig cfg;
    final Path projectRoot = Paths.get("");

    public CodeGenerator(CodegenConfig cfg) {
        this.cfg = cfg;
    }

    public void generate() throws IOException {
        String out = cfg.outputDir == null ? "" : cfg.outputDir.trim();
        String baseJava = out.isEmpty() ? "src/main/java" : out;
        Path baseJavaPath = Paths.get(baseJava).normalize();
        try {
            Files.createDirectories(baseJavaPath);
        } catch (IOException e) {
            baseJava = "src/main/java";
            baseJavaPath = Paths.get(baseJava);
            Files.createDirectories(baseJavaPath);
            System.out.println("输出目录不可写，已回退到: " + baseJava);
        }
        String basePackagePath = cfg.packageBase.replace('.', '/');
        String modulePath = baseJava + "/" + basePackagePath + "/" + cfg.module;
        Path entityDir = Paths.get(modulePath + "/entity");
        Path dtoDir = Paths.get(modulePath + "/dto");
        Path repoDir = Paths.get(modulePath + "/repository");
        Path mapperDir = Paths.get(modulePath + "/mapper");
        Path serviceDir = Paths.get(modulePath + "/service");
        Path implDir = Paths.get(modulePath + "/service/impl");
        Files.createDirectories(entityDir);
        Files.createDirectories(dtoDir);
        Files.createDirectories(repoDir);
        Files.createDirectories(mapperDir);
        Files.createDirectories(serviceDir);
        Files.createDirectories(implDir);

        Path entityPath = entityDir.resolve(cfg.entityName + "Entity.java");
        Path dtoPath = dtoDir.resolve(cfg.entityName + "Dto.java");
        Path repoPath = repoDir.resolve(cfg.entityName + "Repo.java");
        Path mapperPath = mapperDir.resolve(cfg.entityName + "Mapper.java");
        Path servicePath = serviceDir.resolve(cfg.entityName + "Service.java");
        Path implPath = implDir.resolve(cfg.entityName + "ServiceImpl.java");

        List<Path> targets = new ArrayList<>();
        targets.add(entityPath);
        targets.add(dtoPath);
        targets.add(repoPath);
        targets.add(mapperPath);
        targets.add(servicePath);
        targets.add(implPath);
        List<Path> conflicts = new ArrayList<>();
        for (Path t : targets) {
            if (Files.exists(t)) conflicts.add(t);
        }
        if (!conflicts.isEmpty()) {
            StringBuilder msg = new StringBuilder();
            msg.append("目标文件已存在，生成终止：\n");
            for (Path c : conflicts) {
                msg.append("- ").append(projectRoot.resolve(c).toString()).append("\n");
            }
            msg.append("请删除以上文件或修改配置（entityName/module/outputDir）。");
            throw new IOException(msg.toString());
        }

        String entityTemplate = cfg.mapping.equalsIgnoreCase("TABLE") ? readTemplate("entity-table.tpl") : readTemplate("entity-subselect.tpl");
        String dtoTemplate = readTemplate("dto.tpl");
        String repoTemplate = readTemplate("repository.tpl");
        String mapperTemplate = readTemplate("mapper.tpl");
        String serviceTemplate = readTemplate("service.tpl");
        String implTemplate = readTemplate("serviceImpl.tpl");

        String entityPkg = cfg.packageBase + "." + cfg.module + ".entity";
        String dtoPkg = cfg.packageBase + "." + cfg.module + ".dto";
        String repoPkg = cfg.packageBase + "." + cfg.module + ".repository";
        String mapperPkg = cfg.packageBase + "." + cfg.module + ".mapper";
        String servicePkg = cfg.packageBase + "." + cfg.module + ".service";
        String implPkg = servicePkg + ".impl";

        Set<String> entityImports = new HashSet<>();
        entityImports.add("lombok.Data");
        entityImports.add("javax.persistence.Entity");
        entityImports.add("javax.persistence.Id");
        entityImports.add("javax.persistence.Column");
        entityImports.add("io.swagger.annotations.ApiModelProperty");
        if (cfg.mapping.equalsIgnoreCase("SUBSELECT")) {
            entityImports.add("org.hibernate.annotations.Immutable");
            entityImports.add("org.hibernate.annotations.Subselect");
        } else {
            entityImports.add("javax.persistence.Table");
        }
        for (CodegenFieldDef f : cfg.fields) {
            if ("LocalDateTime".equals(f.type)) entityImports.add("java.time.LocalDateTime");
            if ("BigDecimal".equals(f.type)) entityImports.add("java.math.BigDecimal");
        }

        Set<String> dtoImports = new HashSet<>();
        dtoImports.add("lombok.Data");
        dtoImports.add("lombok.EqualsAndHashCode");
        dtoImports.add("io.swagger.annotations.ApiModelProperty");
        for (CodegenFieldDef f : cfg.fields) {
            if ("LocalDateTime".equals(f.type)) dtoImports.add("java.time.LocalDateTime");
            if ("BigDecimal".equals(f.type)) dtoImports.add("java.math.BigDecimal");
        }

        String entityFields = renderEntityFields(cfg.fields);
        String dtoFields = renderDtoFields(cfg.fields);

        String entityExtends = "";
        if (cfg.entityBaseClass != null && !cfg.entityBaseClass.isEmpty()) {
            entityImports.add(cfg.entityBaseClass);
            entityExtends = " extends " + simpleClassName(cfg.entityBaseClass);
        }

        String dtoExtends = "";
        if (cfg.dtoBaseClass != null && !cfg.dtoBaseClass.isEmpty()) {
            dtoImports.add(cfg.dtoBaseClass);
            dtoExtends = " extends " + simpleClassName(cfg.dtoBaseClass);
        }

        String entityContent = entityTemplate
                .replace("{{package}}", entityPkg)
                .replace("{{imports}}", renderImports(entityImports))
                .replace("{{entityName}}", cfg.entityName)
                .replace("{{tableName}}", cfg.tableName)
                .replace("{{subselect}}", cfg.subselect)
                .replace("{{fields}}", entityFields)
                .replace("{{entityExtends}}", entityExtends);

        String dtoContent = dtoTemplate
                .replace("{{package}}", dtoPkg)
                .replace("{{imports}}", renderImports(dtoImports))
                .replace("{{entityName}}", cfg.entityName)
                .replace("{{fields}}", dtoFields)
                .replace("{{dtoExtends}}", dtoExtends);

        String repoContent = repoTemplate
                .replace("{{package}}", repoPkg)
                .replace("{{entityPackage}}", entityPkg)
                .replace("{{entityName}}", cfg.entityName)
                .replace("{{idType}}", cfg.idType);

        String mapperContent = mapperTemplate
                .replace("{{package}}", mapperPkg)
                .replace("{{dtoPackage}}", dtoPkg)
                .replace("{{entityPackage}}", entityPkg)
                .replace("{{entityName}}", cfg.entityName)
                .replace("{{dtoName}}", cfg.entityName + "Dto");

        String serviceContent = serviceTemplate
                .replace("{{package}}", servicePkg)
                .replace("{{dtoPackage}}", dtoPkg)
                .replace("{{entityPackage}}", entityPkg)
                .replace("{{entityName}}", cfg.entityName)
                .replace("{{dtoName}}", cfg.entityName + "Dto")
                .replace("{{idType}}", cfg.idType);

        String predicates = renderPredicates(cfg.fields);
        String implContent = implTemplate
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

        writeFile(entityPath, entityContent);
        writeFile(dtoPath, dtoContent);
        writeFile(repoPath, repoContent);
        writeFile(mapperPath, mapperContent);
        writeFile(servicePath, serviceContent);
        writeFile(implPath, implContent);
    }

    String renderImports(Set<String> imports) {
        StringBuilder sb = new StringBuilder();
        for (String i : imports) {
            sb.append("import ").append(i).append(";\n");
        }
        return sb.toString();
    }

    String renderEntityFields(List<CodegenFieldDef> fields) {
        StringBuilder sb = new StringBuilder();
        for (CodegenFieldDef f : fields) {
            sb.append("    @io.swagger.annotations.ApiModelProperty(value = \"").append(f.label).append("\")\n");
            if (f.id) {
                sb.append("    @javax.persistence.Id\n");
            }
            sb.append("    @javax.persistence.Column(name = \"").append(f.column).append("\")\n");
            sb.append("    private ").append(f.type).append(" ").append(f.name).append(";\n\n");
        }
        return sb.toString();
    }

    String renderDtoFields(List<CodegenFieldDef> fields) {
        StringBuilder sb = new StringBuilder();
        for (CodegenFieldDef f : fields) {
            sb.append("    @io.swagger.annotations.ApiModelProperty(value = \"").append(f.label).append("\")\n");
            sb.append("    private ").append(f.type).append(" ").append(f.name).append(";\n\n");
        }
        return sb.toString();
    }

    String renderPredicates(List<CodegenFieldDef> fields) {
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

    String readTemplate(String name) throws IOException {
        String fsDir = cfg.templatesDir;
        if (fsDir != null && !fsDir.isEmpty()) {
            Path p = Paths.get(fsDir, name);
            if (Files.exists(p)) {
                byte[] bytes = Files.readAllBytes(p);
                return new String(bytes, StandardCharsets.UTF_8);
            }
        }
        String cp = "/codegen/templates/java/" + name;
        try (InputStream in = CodeGenerator.class.getResourceAsStream(cp)) {
            if (in == null) return "";
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    void writeFile(Path path, String content) throws IOException {
        if (Files.exists(path)) {
            throw new IOException("目标文件已存在: " + projectRoot.resolve(path));
        }
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }

    String simpleClassName(String fqcn) {
        int i = fqcn.lastIndexOf('.');
        return i >= 0 ? fqcn.substring(i + 1) : fqcn;
    }
}
