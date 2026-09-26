package com.fund.server.accountservice.exception;

import org.springframework.http.HttpStatus;

public class AccountNotFullySetUpException extends  BusinessException{
    public AccountNotFullySetUpException(){
        super(
                "ACCOUNT_NOT_FULLY_SETUP",
                "Your account setup is incompleted. Please contact support.",
                HttpStatus.CONFLICT);
    }
}
