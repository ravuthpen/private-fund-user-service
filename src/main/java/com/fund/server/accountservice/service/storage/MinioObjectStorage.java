package com.fund.server.accountservice.service.storage;

import com.fund.server.accountservice.config.MinioProperties;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class MinioObjectStorage implements ObjectStorage{ // web server: Netty : even loop

    private final MinioClient minioClient;
    private final MinioProperties props;

    @Override
    public Mono<Void> putObject(String objectKey, String contentType, Path filePath) {
        return Mono.fromCallable(() ->{
            long size = Files.size(filePath);
            try(InputStream in = Files.newInputStream(filePath)){
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(props.bucket())
                                .object(objectKey)
                                .contentType(contentType)
                                .stream(in, size, -1) // -1 means "let Minio decide multipart chunk size"
                                .build()
                );
            }
            return true;
        })
                .subscribeOn(Schedulers.boundedElastic()) // Project Reactor
                .then();
    }

    @Override
    public Mono<Void> removeObject(String objectKey) {
        return Mono.fromRunnable(() -> {
            try {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(props.bucket())
                                .object(objectKey)
                                .build()
                );

            } catch (Exception e) {
                // idempotent delete: don't fail hard if missing
                //throw new RuntimeException(e);
            }
        })
                .subscribeOn(Schedulers.boundedElastic())
                .then() ;
    }

    @Override
    public Mono<String> presignedGetUrl(String objectKey, Duration expiry) {
        // If you have public gateway URL, you can skip presigned and build URL base
        if (props.publicBaseUrl() != null && !props.publicBaseUrl().isBlank()){
            String base = props.publicBaseUrl().endsWith("/") ? props.publicBaseUrl() : props.publicBaseUrl() + "/";
            return Mono.just(base + props.bucket() + "/" + objectKey);
        }
        return Mono.fromCallable(() -> minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(props.bucket())
                        .object(objectKey)
                        .expiry((int) expiry.toSeconds())
                        .build()
        ))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
