package com.fund.server.accountservice.validator;

import com.fund.server.accountservice.domain.UserAccount;
import com.fund.server.accountservice.domain.emuns.UserStatus;
import com.fund.server.accountservice.exception.AccountInactiveException;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class LoginValidator {

    public Mono<Void> validateCanLogin(UserAccount account) {
//        if (account.getRegistrationStatus() != RegistrationStatus.PIN_SET) {
//            return Mono.error(new RegistrationIncompleteException());
//        }

        if (account.getUserStatus() != UserStatus.ACTIVE) {
            return Mono.error(new AccountInactiveException());
        }

        return Mono.empty();
    }
}