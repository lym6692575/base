package com.example.h2server;

import org.h2.tools.Server;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;

@SpringBootApplication
public class H2ServerApplication implements CommandLineRunner {

    @Value("${h2.server.port:9092}")
    private int port;

    @Value("${h2.server.baseDir:./h2-server/data}")
    private String baseDir;

    @Value("${h2.server.allowOthers:true}")
    private boolean allowOthers;

    public static void main(String[] args) {
        SpringApplication.run(H2ServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Path basePath = Paths.get(baseDir).toAbsolutePath();
        Files.createDirectories(basePath);
        String fileUrl = "jdbc:h2:file:" + basePath.resolve("base").toString() + ";MODE=MySQL;DB_CLOSE_ON_EXIT=FALSE";
        try (Connection ignored = DriverManager.getConnection(fileUrl, "sa", "")) {}
        String allow = allowOthers ? "-tcpAllowOthers" : "";
        Server tcpServer = Server.createTcpServer("-tcp", allow, "-tcpPort", String.valueOf(port), "-baseDir", baseDir).start();
        System.out.println("H2 TCP server started: " + tcpServer.getURL());
    }
}
