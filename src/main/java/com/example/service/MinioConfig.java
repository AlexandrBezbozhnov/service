package com.example.service;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("opZrb8svgemrO2DGR9qf", "Uc7i0fGTGClPk3qprkdGnFfY29wOV7dgn8zv8NY4")
                .build();
    }
}
