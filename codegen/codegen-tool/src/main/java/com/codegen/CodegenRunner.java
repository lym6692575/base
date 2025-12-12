package com.codegen;

import com.codegen.Generator.CodeGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 代码生成器主程序
 * <p>
 * 程序启动流程：
 * 1. 优先检查命令行参数是否指定配置文件路径。
 * 2. 若未指定，则检查系统属性 codegen.config。
 * 3. 若均未指定，默认寻找当前目录下的 codegen.properties。
 * 4. 若 codegen.properties 不存在，则尝试在多个常见位置寻找 codegen.json。
 * 5. 若上述文件均不存在，则弹出文件选择对话框让用户手动选择。
 * </p>
 */
public class CodegenRunner {
    public static void main(String[] args) {
        try {
            System.out.println("执行时间: " + LocalDateTime.now().toString());
            // 1. 尝试从命令行参数获取配置文件路径
            String configPathArg = args != null && args.length > 0 ? args[0] : null;
            
            // 2. 尝试从系统属性获取，默认为 "codegen.properties"
            String configPath = configPathArg != null ? configPathArg : System.getProperty("codegen.config", "codegen.properties");
            
            // 3. 如果使用的是默认配置名且文件不存在，进行智能回退
            if (configPathArg == null && "codegen.properties".equals(configPath) && !Files.exists(Paths.get(configPath))) {
                
                // 3.1 尝试在当前目录及其子目录中寻找 codegen.json
                Path currentDir = Paths.get(".");
                PathMatcher matcher = currentDir.getFileSystem().getPathMatcher("glob:codegen.json");
                
                boolean found = false;
                
                // 递归地搜索当前目录及其子目录，寻找 codegen.json 文件
                // 限制递归搜索的深度为 3，避免搜索过深导致性能问题
                try (Stream<Path> stream = Files.walk(currentDir, 3)) {
                    // 过滤出符合条件的文件
                    List<Path> matchingFiles = stream
                        .filter(Files::isRegularFile) // 只考虑文件，不考虑目录
                        .filter(matcher::matches)      // 只考虑文件名是 codegen.json 的文件
                        .collect(Collectors.toList());
                    
                    // 如果找到符合条件的文件
                    if (!matchingFiles.isEmpty()) {
                        // 选择第一个符合条件的文件
                        Path selectedFile = matchingFiles.get(0);
                        System.out.println("检测到配置文件: " + selectedFile.toAbsolutePath());
                        System.setProperty("codegen.config", selectedFile.toAbsolutePath().toString());
                        configPath = System.getProperty("codegen.config", configPath);
                        found = true;
                    }
                } catch (IOException e) {
                    System.err.println("搜索配置文件时发生错误: " + e.getMessage());
                    // 继续执行，让程序弹出 GUI 文件选择框
                }

                // 3.2 若自动探测失败，则弹出 GUI 文件选择框
                if (!found) {
                    System.out.println("未找到默认配置文件 (codegen.properties/json)，请在弹出的对话框中选择...");
                    // 使用 final 变量在 lambda 中引用
                    final String[] selectedPath = new String[1];
                    javax.swing.SwingUtilities.invokeAndWait(() -> {
                        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
                        chooser.setDialogTitle("选择配置文件 (.properties 或 .json)");
                        chooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
                        // 设置默认目录为当前工作目录
                        chooser.setCurrentDirectory(Paths.get(".").toFile());
                        int result = chooser.showOpenDialog(null);
                        if (result == javax.swing.JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
                            selectedPath[0] = chooser.getSelectedFile().getAbsolutePath();
                        }
                    });
                    
                    if (selectedPath[0] != null) {
                         System.setProperty("codegen.config", selectedPath[0]);
                         configPath = selectedPath[0];
                    }
                    
                    // 再次检查文件是否存在（防止用户取消选择）
                    if (configPath == null || !Files.exists(Paths.get(configPath))) {
                        System.out.println("未选择配置文件，已取消生成");
                        System.exit(1); // 明确退出
                        return;
                    }
                }
            }
            
            // 4. 加载配置并执行生成
            System.out.println("正在加载配置: " + configPath);
            Properties p = ConfigLoader.load(configPath);
            src.main.java.com.codegen.CodegenConfig cfg = src.main.java.com.codegen.CodegenConfig.fromProperties(p);
            CodeGenerator g = new CodeGenerator(cfg);
            g.generate();
            System.out.println("代码生成完成! 实体名称: " + cfg.entityName);
            if (g.getMainEntityPath() != null) {
                System.out.println("输出路径: " + g.getMainEntityPath().toAbsolutePath());
            }
            
        } catch (Exception e) {
            System.err.println("\n[ERROR] 代码生成失败: " + e.getMessage());
            e.printStackTrace();
            System.exit(1); // 发生异常时返回非零状态码
        }
    }
}
