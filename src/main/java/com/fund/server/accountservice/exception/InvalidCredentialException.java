package com.fund.server.accountservice.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialException extends BusinessException{

    public InvalidCredentialException(){
        super(
                "INVALID_CREDENTIALS",
                "Phone number or PIN is incorrect",
                HttpStatus.UNAUTHORIZED
        );
    }
}
