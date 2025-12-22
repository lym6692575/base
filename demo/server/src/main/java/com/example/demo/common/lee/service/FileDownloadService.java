package com.example.demo.common.lee.service;

import com.example.demo.common.lee.FileDownloadStrategy.FileDownloadStrategy;
import com.example.demo.common.lee.FileDownloadStrategy.ServerFixedLocationDownloadStrategy;
import com.example.demo.common.lee.FileDownloadStrategy.StaticDirectoryDownloadStrategy;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件下载服务类，负责根据不同的下载类型选择合适的下载策略。
 */
@Service
public class FileDownloadService {
    private final Map<String, FileDownloadStrategy> downloadStrategies;

    /**
     * 构造函数，初始化并注册不同的下载策略。
     */
    public FileDownloadService(ResourceLoader resourceLoader) {
        this.downloadStrategies = new HashMap<>();
        // 注册下载策略，可以通过构造函数注入或者直接实例化
        this.downloadStrategies.put("serverFixed", new ServerFixedLocationDownloadStrategy());
        this.downloadStrategies.put("static", new StaticDirectoryDownloadStrategy(resourceLoader)); // 示例中需要ResourceLoader
    }

    /**
     * 执行文件下载操作。
     * @param type 下载类型，决定使用哪个策略。
     * @param fileName 要下载的文件名。
     * @param filePath 文件路径，可以是服务器路径或者静态资源路径。
     * @return 返回构建的响应实体，包含流或错误信息。
     */
    public ResponseEntity<StreamingResponseBody> executeDownload(String type, String fileName, String filePath) {
        if (!downloadStrategies.containsKey(type)) {
            return ResponseEntity.badRequest().body(
                    outputStream -> outputStream.write("无效的下载类型: ".getBytes(StandardCharsets.UTF_8))
            );
        }
        try {
            return downloadStrategies.get(type).download(fileName, filePath);
        } catch (IOException e) {
            // 日志记录和错误处理
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(outputStream -> outputStream.write(("下载过程中出现错误: " + e.getMessage()).getBytes(StandardCharsets.UTF_8)));
        }
    }
}