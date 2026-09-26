package com.fund.server.accountservice.exception;

import org.springframework.http.HttpStatus;

public class InvalidPhoneNumberException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public InvalidPhoneNumberException(String message) {
        super("INVALID_PHONE_NUMBER", message, HttpStatus.BAD_REQUEST);
    }
}