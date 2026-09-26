package com.fund.server.accountservice.service;

import com.fund.server.accountservice.dto.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CustomerProfileService {
    Mono<CustomerProfileDetailResponse> findKeycloakId(String keycloakUserId);
    Mono<CustomerProfileDetailResponse> findCustomerByAccountId(UUID userAccountId);
    Mono<PageResponse<CustomerProfileDetailResponse>> findCustomerProfileFilterByPagination(CustomerProfileDetailFilter filter);
    Mono<String> update(UUID userId, UpdateCustomerProfileDetailRequest request);

    Mono<PageResponse<CustomerProfileDetailResponse>> getCustomerByFiltersPagination(CustomerFilter filter);

}
