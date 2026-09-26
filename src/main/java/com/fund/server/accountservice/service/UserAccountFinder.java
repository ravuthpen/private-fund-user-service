package com.fund.server.accountservice.service;

import com.fund.server.accountservice.domain.UserAccount;
import com.fund.server.accountservice.domain.emuns.UserType;
import com.fund.server.accountservice.dto.NormalizedPhone;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserAccountFinder {
    Mono<UserAccount> findRequiredById(UUID userAccountId);
    Mono<UserAccount> findRequiredByPhoneAndUserType(NormalizedPhone phone, UserType userType);
    Mono<UserAccount> findByPhoneAndUserType(NormalizedPhone phone, UserType userType);
}
