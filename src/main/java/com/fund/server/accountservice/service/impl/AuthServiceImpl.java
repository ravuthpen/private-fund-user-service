package com.fund.server.accountservice.service.impl;

import com.fund.server.accountservice.dto.LoginRequest;
import com.fund.server.accountservice.dto.LoginResponse;
import com.fund.server.accountservice.dto.NormalizedPhone;
import com.fund.server.accountservice.service.AuthService;
import com.fund.server.accountservice.service.PhoneNumberService;
import com.fund.server.accountservice.service.UserAccountFinder;
import com.fund.server.accountservice.service.keycloak.KeycloakAuthClient;
import com.fund.server.accountservice.util.LogMasker;
import com.fund.server.accountservice.util.PhoneNormalizer;
import com.fund.server.accountservice.validator.LoginValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final PhoneNormalizer phoneNormalizer;
    private final PhoneNumberService phoneNumberService;
    private final UserAccountFinder userAccountFinder;
    private final LoginValidator loginValidator;
    private final KeycloakAuthClient keycloakAuthClient;

    @Override
    public Mono<LoginResponse> login(LoginRequest request) {
        /**
         * Reason:
         * Always normalized phone before searching
         * User input as 012....,088....,or with spaces.
         */
        NormalizedPhone phone = phoneNumberService.normalize(
                request.countryCode(),
                request.phoneNumber()
        );
        log.info(
                "Login requested. userType={}, phone={}",
                request.userType(),
                LogMasker.maskPhone(phone.phoneNumber())
        );
        return userAccountFinder.findRequiredByPhoneAndUserType(phone, request.userType())
                /**
                 * Reason:
                 * Validate that is account is allowed to log in.
                 */
                .flatMap(account-> loginValidator.validateCanLogin(account)
                        .thenReturn(account)
                )
                /**
                 * Reason:
                 * If validate succeeds, authenticate the user with keycloak.
                 */
                .flatMap(account -> keycloakAuthClient.login(
                        phoneNormalizer.toUsername(
                                phone.countryCode(),
                                phone.phoneNumber()),
                        request.pin()
                ))
                .doOnSuccess(response -> log.info(
                        "Login successfully. userType={}, phone={}",
                        request.userType(),
                        LogMasker.maskPhone(phone.phoneNumber())
                ))
                .doOnError(error -> log.warn(
                        "Login failed. userType={}, phone={}, reason={}",
                        request.userType(),
                        LogMasker.maskPhone(phone.phoneNumber()),
                        error.getMessage()
                ));
    }
}
