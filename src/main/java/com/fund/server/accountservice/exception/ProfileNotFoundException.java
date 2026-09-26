package com.fund.server.accountservice.exception;

import java.util.UUID;

public class ProfileNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public ProfileNotFoundException(UUID id){
        super("Profile Not Found Exception ID: " + id);
    }
}
