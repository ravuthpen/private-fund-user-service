package com.fund.server.accountservice.service.storage;

import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.time.Duration;

public interface ObjectStorage { // SOLID principle : "O" Open Extension, Closed modification
    // Command not Query

    Mono<Void> putObject(String objectKey, String contentType, Path filePath);
    Mono<Void> removeObject(String objectKey);
    Mono<String> presignedGetUrl(String objectKey, Duration expiry);

}
