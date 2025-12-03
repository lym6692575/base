package com.dqjq.codegen;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class CodegenRunner {
    public static void main(String[] args) throws Exception {
        String configPathArg = args != null && args.length > 0 ? args[0] : null;
        String configPath = configPathArg != null ? configPathArg : System.getProperty("codegen.config", "codegen.properties");
        if (configPathArg == null && "codegen.properties".equals(configPath) && !Files.exists(Paths.get(configPath))) {
            Path jsonDefault = Paths.get("codegen.json");
            if (Files.exists(jsonDefault)) {
                System.setProperty("codegen.config", jsonDefault.toAbsolutePath().toString());
                configPath = System.getProperty("codegen.config", configPath);
            } else {
            javax.swing.SwingUtilities.invokeAndWait(() -> {
                javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
                chooser.setDialogTitle("选择配置文件 .properties");
                chooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
                int result = chooser.showOpenDialog(null);
                if (result == javax.swing.JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
                    System.setProperty("codegen.config", chooser.getSelectedFile().getAbsolutePath());
                }
            });
            configPath = System.getProperty("codegen.config", configPath);
            if (!Files.exists(Paths.get(configPath))) {
                System.out.println("未选择配置文件，已取消生成");
                return;
            }
            }
        }
        Properties p = ConfigLoader.load(configPath);
        CodegenConfig cfg = CodegenConfig.fromProperties(p);
        CodeGenerator g = new CodeGenerator(cfg);
        g.generate();
        System.out.println("Codegen done for " + cfg.entityName);
    }
}
