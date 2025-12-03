package com.dqjq.codegen;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ConfigLoader {
    public static Properties load(String path) throws java.io.IOException {
        if (path != null && path.toLowerCase().endsWith(".json")) {
            Properties p = loadJson(path);
            if (!p.isEmpty()) return p;
        }
        return loadProperties(path);
    }

    public static Properties loadProperties(String path) throws java.io.IOException {
        Properties p = new Properties();
        Path file = Paths.get(path);
        if (Files.exists(file)) {
            try (java.io.Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                p.load(reader);
            }
            return p;
        }
        try (InputStream in = ConfigLoader.class.getResourceAsStream("/codegen/sample.properties")) {
            if (in != null) {
                try (java.io.Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                    p.load(reader);
                }
            }
        }
        return p;
    }

    public static Properties loadJson(String path) throws java.io.IOException {
        Properties p = new Properties();
        Path file = Paths.get(path);
        if (Files.exists(file)) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> m = mapper.readValue(Files.newBufferedReader(file, StandardCharsets.UTF_8), new TypeReference<Map<String, Object>>() {});
            for (Map.Entry<String, Object> e : m.entrySet()) {
                if (e.getValue() == null) continue;
                if ("fields".equals(e.getKey()) && e.getValue() instanceof List) {
                    List<?> arr = (List<?>) e.getValue();
                    List<String> parts = new ArrayList<>();
                    for (Object o : arr) {
                        if (o instanceof Map) {
                            Map<?, ?> fm = (Map<?, ?>) o;
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
