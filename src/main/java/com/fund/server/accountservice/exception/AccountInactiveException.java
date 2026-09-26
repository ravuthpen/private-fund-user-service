package com.fund.server.accountservice.exception;

import org.springframework.http.HttpStatus;

public class AccountInactiveException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public AccountInactiveException() {
        super(
                "ACCOUNT_INACTIVE",
                "Account is not active",
                HttpStatus.FORBIDDEN
        );
    }
}