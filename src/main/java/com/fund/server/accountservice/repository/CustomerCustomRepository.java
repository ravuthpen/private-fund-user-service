package com.fund.server.accountservice.repository;

import com.fund.server.accountservice.dto.CustomerFilter;
import com.fund.server.accountservice.dto.CustomerProfileDetail;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerCustomRepository {
    Flux<CustomerProfileDetail> findCustomerProfileByFilter(
            CustomerFilter filter,
            int limit,
            long offset
    );

    Mono<Long> countCustomerProfiles(CustomerFilter filter);
}
