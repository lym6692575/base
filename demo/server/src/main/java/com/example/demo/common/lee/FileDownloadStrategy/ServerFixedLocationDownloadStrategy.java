package com.example.demo.common.lee.FileDownloadStrategy;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 从服务器固定位置下载文件的策略实现。
 */
public class ServerFixedLocationDownloadStrategy implements FileDownloadStrategy {

    /**
     * 实现文件下载逻辑。
     * @param fileName 文件名。
     * @param filePath 文件路径。
     * @return 返回文件下载的响应实体。
     */
    @Override
    public ResponseEntity<StreamingResponseBody> download(String fileName, String filePath) {
        try {
            Path file = Paths.get(filePath);
            if (!Files.exists(file)) {
                throw new FileNotFoundException("文件 " + filePath + " 不存在！");
            }
            return createResponseEntity(fileName, file);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(outputStream -> outputStream.write(("文件读取失败：" + e.getMessage()).getBytes(StandardCharsets.UTF_8)));
        }
    }
}