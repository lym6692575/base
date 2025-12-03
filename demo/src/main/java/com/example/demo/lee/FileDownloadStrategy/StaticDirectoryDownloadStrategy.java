package com.example.demo.lee.FileDownloadStrategy;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * 从static目录下载文件的策略实现。
 */
public class StaticDirectoryDownloadStrategy implements FileDownloadStrategy {
    private final ResourceLoader resourceLoader;

    public StaticDirectoryDownloadStrategy(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * 实现从static目录下载文件的逻辑。
     * @param fileName 文件名。
     * @param staticPath static目录下的文件路径。
     * @return 返回文件下载的响应实体。
     */
    @Override
    public ResponseEntity<StreamingResponseBody> download(String fileName, String staticPath) {
        try {
            Resource resource = resourceLoader.getResource("classpath:static/" + staticPath);

            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(outputStream -> outputStream.write(("文件未找到").getBytes(StandardCharsets.UTF_8)));
            }

            Path file = resource.getFile().toPath();
            return createResponseEntity(fileName, file);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(outputStream -> outputStream.write(("文件读取失败：" + e.getMessage()).getBytes(StandardCharsets.UTF_8)));
        }
    }
}
