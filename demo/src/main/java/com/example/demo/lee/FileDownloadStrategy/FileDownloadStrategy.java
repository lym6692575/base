package com.example.demo.lee.FileDownloadStrategy;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 文件下载策略接口，定义了下载文件的方法和创建响应实体的默认方法。
 */

public interface FileDownloadStrategy {
    /**
     * 下载文件的方法，需在实现类中具体实现。
     * @param fileName 文件名，用于响应头中显示。
     * @param filePath 文件路径，指定要下载的文件位置。
     * @return 返回构建好的响应实体。
     * @throws IOException 抛出文件操作相关的输入输出异常。
     */
    ResponseEntity<StreamingResponseBody> download(String fileName, String filePath) throws IOException;

    /**
     * 创建文件下载的响应实体，默认实现。
     * @param fileName 文件名，用于响应头中显示。
     * @param file 文件路径对象。
     * @return 返回构建好的响应实体。
     * @throws IOException 抛出文件操作相关的输入输出异常。
     */
    default ResponseEntity<StreamingResponseBody> createResponseEntity(String fileName, Path file) throws IOException {
        long fileSize = Files.size(file);

        StreamingResponseBody responseBody = outputStream -> {
            try (BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(file))) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        };

        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize));

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);
    }
}