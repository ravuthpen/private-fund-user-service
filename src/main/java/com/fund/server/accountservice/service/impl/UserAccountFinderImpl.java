package com.fund.server.accountservice.service.impl;

import com.fund.server.accountservice.domain.UserAccount;
import com.fund.server.accountservice.domain.emuns.UserType;
import com.fund.server.accountservice.dto.NormalizedPhone;
import com.fund.server.accountservice.exception.NotFoundException;
import com.fund.server.accountservice.repository.UserAccountRepository;
import com.fund.server.accountservice.service.UserAccountFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAccountFinderImpl implements UserAccountFinder {

    private final UserAccountRepository repository;

    @Override
    public Mono<UserAccount> findRequiredById(UUID id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(
                        new RuntimeException("user account id not found.")
                ));
    }

    @Override
    public Mono<UserAccount> findRequiredByPhoneAndUserType(NormalizedPhone phone, UserType userType) {
        return findByPhoneAndUserType(phone, userType)
                .switchIfEmpty(Mono.error(new NotFoundException("User account not found")));
    }

    @Override
    public Mono<UserAccount> findByPhoneAndUserType(NormalizedPhone phone, UserType userType) {
        return repository.findByCountryCodeAndPhoneNumberAndUserType(
                phone.countryCode(),
                phone.phoneNumber(),
                userType
        );
    }
}
