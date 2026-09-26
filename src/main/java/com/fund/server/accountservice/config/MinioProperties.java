package com.fund.server.accountservice.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage.minio")
public record MinioProperties(
        String endpoint,
        String accessKey,
        String secretKey,
        String bucket,
        String publicBaseUrl,
        Integer maxFilesPerUser,
        Long maxFileSizeBytes
) {

}
