package com.dqjq.codegen;

import com.dqjq.codegen.Generator.CodeGenerator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Arrays;
import java.util.List;

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
    public static void main(String[] args) throws Exception {
        // 1. 尝试从命令行参数获取配置文件路径
        String configPathArg = args != null && args.length > 0 ? args[0] : null;
        
        // 2. 尝试从系统属性获取，默认为 "codegen.properties"
        String configPath = configPathArg != null ? configPathArg : System.getProperty("codegen.config", "codegen.properties");
        
        // 3. 如果使用的是默认配置名且文件不存在，进行智能回退
        if (configPathArg == null && "codegen.properties".equals(configPath) && !Files.exists(Paths.get(configPath))) {
            
            // 3.1 尝试在多个可能的位置寻找 codegen.json
            List<Path> candidates = Arrays.asList(
                Paths.get("codegen.json"),                    // 当前目录
                Paths.get("codegen-tool", "codegen.json"),    // 子模块目录（当在父工程根目录运行时）
                Paths.get("..", "codegen.json")               // 上级目录（当在子目录运行时）
            );
            
            boolean found = false;
            for (Path candidate : candidates) {
                if (Files.exists(candidate)) {
                    System.out.println("检测到配置文件: " + candidate.toAbsolutePath());
                    System.setProperty("codegen.config", candidate.toAbsolutePath().toString());
                    configPath = System.getProperty("codegen.config", configPath);
                    found = true;
                    break;
                }
            }

            // 3.2 若自动探测失败，则弹出 GUI 文件选择框
            if (!found) {
                System.out.println("未找到默认配置文件 (codegen.properties/json)，请在弹出的对话框中选择...");
                javax.swing.SwingUtilities.invokeAndWait(() -> {
                    javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
                    chooser.setDialogTitle("选择配置文件 (.properties 或 .json)");
                    chooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
                    // 设置默认目录为当前工作目录
                    chooser.setCurrentDirectory(Paths.get(".").toFile());
                    int result = chooser.showOpenDialog(null);
                    if (result == javax.swing.JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
                        System.setProperty("codegen.config", chooser.getSelectedFile().getAbsolutePath());
                    }
                });
                
                // 更新配置路径
                configPath = System.getProperty("codegen.config", configPath);
                
                // 再次检查文件是否存在（防止用户取消选择）
                if (!Files.exists(Paths.get(configPath))) {
                    System.out.println("未选择配置文件，已取消生成");
                    return;
                }
            }
        }
        
        // 4. 加载配置并执行生成
        System.out.println("正在加载配置: " + configPath);
        Properties p = ConfigLoader.load(configPath);
        CodegenConfig cfg = CodegenConfig.fromProperties(p);
        CodeGenerator g = new CodeGenerator(cfg);
        g.generate();
        System.out.println("代码生成完成! 实体名称: " + cfg.entityName);
        System.out.println("输出路径: " + g.getMainEntityPath().toAbsolutePath());
    }
}
