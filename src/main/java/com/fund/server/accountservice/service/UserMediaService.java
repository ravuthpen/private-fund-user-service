package com.fund.server.accountservice.service;

import com.fund.server.accountservice.dto.CustomerProfileDetailResponse;
import com.fund.server.accountservice.dto.CustomerProfileResponse;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserMediaService {
    Mono<CustomerProfileDetailResponse> uploadUserPhoto(UUID customerId, UUID userId, Flux<FilePart> files);
    Mono<CustomerProfileResponse> getUserPhotoUrls(UUID customerId, UUID userId);
    Mono<CustomerProfileResponse> deleteUserPhoto(UUID customerId, UUID userId, String objectKey);
}
