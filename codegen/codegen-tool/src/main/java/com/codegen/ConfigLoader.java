package com.codegen;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 配置加载器
 * <p>
 * 负责从文件系统或类路径加载生成器配置。
 * 支持 .properties 和 .json 两种格式。
 * </p>
 */
public class ConfigLoader {

    /**
     * 加载配置文件
     * <p>
     * 根据文件后缀自动判断加载方式。
     * 若以 .json 结尾则按 JSON 格式解析，否则按 Properties 格式解析。
     * </p>
     *
     * @param path 配置文件路径（绝对路径或相对路径）
     * @return 加载后的 Properties 对象
     * @throws IOException 如果读取文件失败
     */
    public static Properties load(String path) throws IOException {
        if (path != null && path.toLowerCase().endsWith(".json")) {
            Properties p = loadJson(path);
            if (!p.isEmpty()) return p;
        }
        return loadProperties(path);
    }

    /**
     * 加载 .properties 格式的配置文件
     * <p>
     * 优先尝试从指定路径加载。
     * 如果文件不存在，则尝试从类路径下的 /codegen/sample.properties 加载作为回退。
     * </p>
     *
     * @param path 文件路径
     * @return Properties 对象
     * @throws IOException IO异常
     */
    public static Properties loadProperties(String path) throws IOException {
        Properties p = new Properties();
        if (path == null) return p;

        Path file = Paths.get(path);
        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                p.load(reader);
            }
            return p;
        }
        // 尝试从类路径加载默认配置
        try (InputStream in = ConfigLoader.class.getResourceAsStream("/codegen/sample.properties")) {
            if (in != null) {
                try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                    p.load(reader);
                }
            }
        }
        return p;
    }

    /**
     * 加载 .json 格式的配置文件
     * <p>
     * 将 JSON 对象扁平化转换为 Properties 对象。
     * 特别处理 "fields" 字段，将其转换为生成器所需的特定格式字符串。
     * </p>
     *
     * @param path JSON 文件路径
     * @return 转换后的 Properties 对象
     * @throws IOException IO异常
     */
    @SuppressWarnings("unchecked")
    public static Properties loadJson(String path) throws IOException {
        Properties p = new Properties();
        if (path == null) return p;

        Path file = Paths.get(path);
        if (Files.exists(file)) {
            ObjectMapper mapper = new ObjectMapper();
            // 读取 JSON 为 Map
            Map<String, Object> m = mapper.readValue(
                    Files.newBufferedReader(file, StandardCharsets.UTF_8), 
                    new TypeReference<Map<String, Object>>() {}
            );
            
            for (Map.Entry<String, Object> e : m.entrySet()) {
                if (e.getValue() == null) continue;
                
                // 特殊处理 fields 数组，将其转换为 Properties 兼容的字符串格式
                if ("fields".equals(e.getKey()) && e.getValue() instanceof List) {
                    List<?> arr = (List<?>) e.getValue();
                    List<String> parts = new ArrayList<>();
                    for (Object o : arr) {
                        if (o instanceof Map) {
                            Map<String, Object> fm = (Map<String, Object>) o;
                            String name = String.valueOf(fm.getOrDefault("name", ""));
                            String type = String.valueOf(fm.getOrDefault("type", "String"));
                            String column = String.valueOf(fm.getOrDefault("column", name));
                            String id = String.valueOf(fm.getOrDefault("id", false));
                            String label = String.valueOf(fm.getOrDefault("label", name));
                            parts.add(name + ":" + type + ":" + column + ":" + id + ":" + label);
                        }
                    }
                    p.setProperty("fields", String.join(";", parts));
                } else {
                    p.setProperty(e.getKey(), String.valueOf(e.getValue()));
                }
            }
        }
        return p;
    }
}
