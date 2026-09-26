package com.fund.server.accountservice.exception;

import org.springframework.http.HttpStatus;

public abstract class BusinessException extends RuntimeException {

    private static final long serialVersionUID = -5583747697405962399L;
	private final String errorCode;
    private final HttpStatus status;

    protected BusinessException(String errorCode, String message, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

