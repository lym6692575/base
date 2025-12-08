package com.dqjq.codegen.Generator;

import com.dqjq.codegen.CodegenConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * 生成器基类
 * <p>
 * 提供通用的文件读写、模板加载和辅助方法。
 * 所有具体的代码生成器都应继承此类。
 * </p>
 */
public abstract class BaseGenerator {
    protected final CodegenConfig cfg;
    protected final Path projectRoot = Paths.get("");

    public BaseGenerator(CodegenConfig cfg) {
        this.cfg = cfg;
    }

    /**
     * 执行生成逻辑
     * @throws IOException 如果生成过程中发生IO错误
     */
    public abstract void generate() throws IOException;

    /**
     * 读取模板文件内容
     * <p>
     * 优先从配置的 templatesDir 目录读取，
     * 如果不存在则从 classpath 下的 /codegen/templates/java/ 读取。
     * </p>
     *
     * @param name 模板文件名（例如 entity.tpl）
     * @return 模板内容字符串
     * @throws IOException 如果读取失败
     */
    protected String readTemplate(String name) throws IOException {
        String fsDir = cfg.templatesDir;
        if (fsDir != null && !fsDir.isEmpty()) {
            Path p = Paths.get(fsDir, name);
            if (Files.exists(p)) {
                byte[] bytes = Files.readAllBytes(p);
                return new String(bytes, StandardCharsets.UTF_8);
            }
        }
        // 注意：资源路径必须以 / 开头，且不能包含 \
        String cp = "/codegen/templates/java/" + name;
        try (InputStream in = getClass().getResourceAsStream(cp)) {
            if (in == null) {
                // 尝试不带斜杠的路径（某些ClassLoader实现差异）
                try (InputStream in2 = getClass().getClassLoader().getResourceAsStream("codegen/templates/java/" + name)) {
                    if (in2 == null) {
                        System.err.println("警告: 无法在类路径下找到模板: " + cp);
                        return "";
                    }
                    return readInputStream(in2);
                }
            }
            return readInputStream(in);
        }
    }

    private String readInputStream(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    /**
     * 将内容写入文件
     * <p>
     * 如果文件已存在，则抛出异常，防止覆盖。
     * </p>
     *
     * @param path 文件路径
     * @param content 文件内容
     * @throws IOException 如果文件已存在或写入失败
     */
    protected void writeFile(Path path, String content) throws IOException {
        if (Files.exists(path)) {
            throw new IOException("目标文件已存在: " + projectRoot.resolve(path));
        }
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 渲染 import 语句块
     *
     * @param imports 需要导入的类全限定名集合
     * @return 格式化后的 import 语句字符串
     */
    protected String renderImports(Set<String> imports) {
        StringBuilder sb = new StringBuilder();
        for (String i : imports) {
            sb.append("import ").append(i).append(";\n");
        }
        return sb.toString();
    }

    /**
     * 获取简单类名
     *
     * @param fqcn 全限定类名
     * @return 简单类名
     */
    protected String simpleClassName(String fqcn) {
        int i = fqcn.lastIndexOf('.');
        return i >= 0 ? fqcn.substring(i + 1) : fqcn;
    }
    
    /**
     * 获取基础输出目录
     *
     * @return 基础输出目录路径
     */
    protected Path getBaseOutputDir() {
        String out = cfg.outputDir == null ? "" : cfg.outputDir.trim();
        String baseJava = out.isEmpty() ? "src/main/java" : out;
        return Paths.get(baseJava).normalize();
    }
    
    /**
     * 确保目录存在
     * 
     * @param dir 目录路径
     * @throws IOException IO异常
     */
    protected void ensureDirectory(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
    }
}
